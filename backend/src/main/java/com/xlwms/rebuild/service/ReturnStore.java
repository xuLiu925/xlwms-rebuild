package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * 退件（/pda/return/receive、/pda/return/create、/pda/return/putaway）的内存数据，重启或 reset 后恢复。
 * 上架写入 {@link StockStore}，销毁只扣待处理数不进库存。数据虚构，规则与编码是本项目假设：
 * <ul>
 *   <li>退件单 status：10 待收货 / 20 待上架 / 30 已完成 / 90 已取消</li>
 *   <li>orderType：1 买家退货 / 2 拒收 / 3 PDA 新建（无预报）/ 4 认领</li>
 *   <li>品质 productQuality：1 良品 / 2 次品；次品只能上架到 E 区（{@link StockStore#requireQualityCell}）</li>
 *   <li>收货要求至少一个包裹到货；允许收到预报外的产品（forecast=false）；序列号在单内不能重复</li>
 *   <li>销毁必须填处理说明</li>
 * </ul>
 */
@Service
public class ReturnStore {

    public static final Map<Integer, String> STATUS = Map.of(10, "待收货", 20, "待上架", 30, "已完成", 90, "已取消");

    public static class Line {
        public final String productSku;
        public final int forecastQty;
        public int goodQty;
        public int defectiveQty;
        public int putGood;
        public int putBad;
        public int destroyed;

        Line(String productSku, int forecastQty) {
            this.productSku = productSku;
            this.forecastQty = forecastQty;
        }

        public int wait(int quality) {
            return quality == 2 ? defectiveQty - putBad : goodQty - putGood;
        }
    }

    public static class Pkg {
        public final String packNo;
        public final String trackNo;
        public boolean reached;
        public String tempCellNo = "";

        Pkg(String packNo, String trackNo) {
            this.packNo = packNo;
            this.trackNo = trackNo;
        }
    }

    public static class Order {
        public final String orderNo;
        public final String customerCode;
        public final String customerName;
        public final int orderType;
        public final String deliveryNo;
        public final String expressNo;
        public final Map<String, Line> lines = new LinkedHashMap<>();
        public final List<Pkg> packages = new ArrayList<>();
        public final Set<String> serials = new LinkedHashSet<>();
        public String remark = "";
        public int status = 10;

        Order(String orderNo, String customerCode, String customerName, int orderType, String deliveryNo, String expressNo) {
            this.orderNo = orderNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.orderType = orderType;
            this.deliveryNo = deliveryNo;
            this.expressNo = expressNo;
        }

        public int waitTotal() {
            return lines.values().stream().mapToInt(l -> l.wait(1) + l.wait(2)).sum();
        }
    }

    private final StockStore stock;
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private int seq;
    private int lastOrderType;

    public ReturnStore(StockStore stock) {
        this.stock = stock;
        reset();
    }

    public synchronized void reset() {
        orders.clear();
        seq = 3;
        lastOrderType = 1;
        Order o1 = order("RN2611000001", "C001", 1, "OB2611000101", "RTRK000001");
        o1.lines.put("SKU-0001", new Line("SKU-0001", 2));
        o1.lines.put("SKU-0002", new Line("SKU-0002", 1));
        o1.packages.add(new Pkg("RN2611000001-1", "RTRK000001"));
        Order o2 = order("RN2611000002", "C002", 1, "OB2611000201", "RTRK000002");
        o2.lines.put("SKU-0004", new Line("SKU-0004", 3));
        o2.packages.add(new Pkg("RN2611000002-1", "RTRK000002"));
        o2.packages.add(new Pkg("RN2611000002-2", "RTRK000002-2"));
        // 已收货、待上架：良品 1 / 次品 1
        Order o3 = order("RN2611000003", "C003", 2, "OB2611000301", "RTRK000003");
        Line l3 = new Line("SKU-0006", 2);
        l3.goodQty = 1;
        l3.defectiveQty = 1;
        o3.lines.put("SKU-0006", l3);
        Pkg p3 = new Pkg("RN2611000003-1", "RTRK000003");
        p3.reached = true;
        o3.packages.add(p3);
        o3.status = 20;
    }

    private Order order(String no, String cc, int type, String deliveryNo, String expressNo) {
        Order o = new Order(no, cc, stock.customers().getOrDefault(cc, cc), type, deliveryNo, expressNo);
        orders.put(no, o);
        return o;
    }

    // ---------- 查询 ----------

    public synchronized List<Order> list(int status) {
        return orders.values().stream().filter(o -> o.status == status).toList();
    }

    public synchronized Order get(String orderNo) {
        return Optional.ofNullable(orders.get(orderNo == null ? "" : orderNo.trim()))
                .orElseThrow(() -> new BizException("退件单不存在：" + orderNo));
    }

    /** 按退件单号 / 原出库单号 / 快递单号 / 包裹跟踪号查。 */
    public synchronized List<Order> searchNo(String content, int status) {
        String c = content == null ? "" : content.trim();
        return orders.values().stream().filter(o -> o.status == status)
                .filter(o -> o.orderNo.equals(c) || o.deliveryNo.equals(c) || o.expressNo.equals(c)
                        || o.packages.stream().anyMatch(p -> p.trackNo.equals(c))).toList();
    }

    /** 按产品条码 / SKU / FNSKU 查含该产品的单。 */
    public synchronized List<Order> searchProduct(String content, int status) {
        Product p = stock.findProduct(content);
        if (p == null) {
            return List.of();
        }
        return orders.values().stream().filter(o -> o.status == status && o.lines.containsKey(p.sku())).toList();
    }

    public synchronized boolean trackNoUsed(String trackNo) {
        return orders.values().stream().anyMatch(o -> o.expressNo.equals(trackNo) || o.packages.stream().anyMatch(p -> p.trackNo.equals(trackNo)));
    }

    public synchronized Order byTrackNo(String trackNo) {
        return orders.values().stream().filter(o -> o.expressNo.equals(trackNo) || o.packages.stream().anyMatch(p -> p.trackNo.equals(trackNo)))
                .findFirst().orElse(null);
    }

    public synchronized int lastOrderType() {
        return lastOrderType;
    }

    public synchronized void setLastOrderType(int tab) {
        if (tab < 1 || tab > 4) {
            throw new BizException("tab 只能是 1~4");
        }
        lastOrderType = tab;
    }

    // ---------- 收货 ----------

    public synchronized void addSerial(String orderNo, String sku, String serial) {
        Order o = get(orderNo);
        if (serial == null || serial.isBlank()) {
            throw new BizException("序列号不能为空");
        }
        if (!o.lines.containsKey(sku)) {
            throw new BizException("SKU 不在退件单 " + o.orderNo + " 中：" + sku);
        }
        if (!o.serials.add(sku + "#" + serial.trim())) {
            throw new BizException("序列号已扫描：" + serial);
        }
    }

    /**
     * 收货提交。reached：包裹号 → [是否到货, 暂存库位]；qty：SKU → [良品, 次品]。先全部校验再生效。
     */
    public synchronized Order receive(String orderNo, Map<String, String[]> reached, Map<String, int[]> qty, String remark) {
        Order o = get(orderNo);
        if (o.status != 10) {
            throw new BizException("退件单当前状态为" + STATUS.get(o.status) + "，不能收货");
        }
        for (Map.Entry<String, String[]> e : reached.entrySet()) {
            if (o.packages.stream().noneMatch(p -> p.packNo.equals(e.getKey()) || p.trackNo.equals(e.getKey()))) {
                throw new BizException("包裹不属于退件单 " + o.orderNo + "：" + e.getKey());
            }
            String cell = e.getValue()[1];
            if (cell != null && !cell.isBlank()) {
                stock.requireCell(cell, true);
            }
        }
        if (reached.values().stream().noneMatch(v -> "1".equals(v[0]))) {
            throw new BizException("至少要有一个包裹到货");
        }
        int total = 0;
        for (Map.Entry<String, int[]> e : qty.entrySet()) {
            stock.product(e.getKey());
            if (!stock.product(e.getKey()).customerCode().equals(o.customerCode)) {
                throw new BizException("SKU " + e.getKey() + " 不属于客户 " + o.customerCode);
            }
            if (e.getValue()[0] < 0 || e.getValue()[1] < 0) {
                throw new BizException("收货数量不能为负数");
            }
            total += e.getValue()[0] + e.getValue()[1];
        }
        if (total == 0) {
            throw new BizException("收货数量不能为 0");
        }
        reached.forEach((k, v) -> o.packages.stream().filter(p -> p.packNo.equals(k) || p.trackNo.equals(k)).forEach(p -> {
            p.reached = "1".equals(v[0]);
            p.tempCellNo = v[1] == null ? "" : v[1].trim();
        }));
        qty.forEach((sku, v) -> {
            Line l = o.lines.computeIfAbsent(sku, k -> new Line(k, 0));
            l.goodQty = v[0];
            l.defectiveQty = v[1];
        });
        o.remark = remark == null ? "" : remark;
        o.status = 20;
        return o;
    }

    /** PDA 新建退件单（无预报）：直接是待上架状态。返回新单号。 */
    public synchronized Order create(String customerCode, int orderType, List<String[]> packages, Map<String, int[]> qty, String remark) {
        if (!stock.customers().containsKey(customerCode)) {
            throw new BizException("客户不存在：" + customerCode);
        }
        if (packages.isEmpty()) {
            throw new BizException("至少要有一个包裹");
        }
        Set<String> seen = new LinkedHashSet<>();
        for (String[] p : packages) {
            if (p[0] == null || p[0].isBlank() || !seen.add(p[0].trim()) || trackNoUsed(p[0].trim())) {
                throw new BizException("跟踪号为空、重复或已有退件单：" + p[0]);
            }
            if (p[1] != null && !p[1].isBlank()) {
                stock.requireCell(p[1], true);
            }
        }
        int total = 0;
        for (Map.Entry<String, int[]> e : qty.entrySet()) {
            if (!stock.product(e.getKey()).customerCode().equals(customerCode)) {
                throw new BizException("SKU " + e.getKey() + " 不属于客户 " + customerCode);
            }
            if (e.getValue()[0] < 0 || e.getValue()[1] < 0) {
                throw new BizException("数量不能为负数");
            }
            total += e.getValue()[0] + e.getValue()[1];
        }
        if (total == 0) {
            throw new BizException("产品数量不能为 0");
        }
        String no = String.format("RN2611%06d", ++seq);
        Order o = order(no, customerCode, orderType, "", packages.get(0)[0].trim());
        for (int i = 0; i < packages.size(); i++) {
            Pkg p = new Pkg(no + "-" + (i + 1), packages.get(i)[0].trim());
            p.reached = true;
            p.tempCellNo = packages.get(i)[1] == null ? "" : packages.get(i)[1].trim();
            o.packages.add(p);
        }
        qty.forEach((sku, v) -> {
            Line l = new Line(sku, 0);
            l.goodQty = v[0];
            l.defectiveQty = v[1];
            o.lines.put(sku, l);
        });
        o.remark = remark == null ? "" : remark;
        o.status = 20;
        return o;
    }

    // ---------- 上架 / 销毁 ----------

    private Line line(Order o, String code) {
        if (o.status != 20) {
            throw new BizException("退件单当前状态为" + STATUS.get(o.status) + "，不能上架或销毁");
        }
        Product p = stock.findProduct(code);
        Line l = p == null ? null : o.lines.get(p.sku());
        if (l == null) {
            throw new BizException("产品不在退件单 " + o.orderNo + " 中：" + code);
        }
        return l;
    }

    /** cells：库位 → 数量。先校验全部再上架。 */
    public synchronized Order putaway(String orderNo, String code, int quality, Map<String, Integer> cells) {
        Order o = get(orderNo);
        Line l = line(o, code);
        int sum = cells.values().stream().mapToInt(Integer::intValue).sum();
        if (cells.isEmpty() || sum <= 0 || sum > l.wait(quality) || cells.values().stream().anyMatch(q -> q <= 0)) {
            throw new BizException("上架数量需大于 0 且不超过待上架数 " + l.wait(quality));
        }
        cells.keySet().forEach(c -> stock.requireQualityCell(c, quality));
        cells.forEach((c, q) -> stock.putIn(c, l.productSku, "", quality, q));
        if (quality == 2) {
            l.putBad += sum;
        } else {
            l.putGood += sum;
        }
        if (o.waitTotal() == 0) {
            o.status = 30;
        }
        return o;
    }

    /** 销毁：只处理次品（本项目假设），扣待上架数，不进库存。 */
    public synchronized Order destroy(String orderNo, String code, int qty, String handleRemark) {
        Order o = get(orderNo);
        Line l = line(o, code);
        if (handleRemark == null || handleRemark.isBlank()) {
            throw new BizException("销毁必须填写处理说明");
        }
        if (qty <= 0 || qty > l.wait(2)) {
            throw new BizException("销毁数量需大于 0 且不超过待处理次品数 " + l.wait(2));
        }
        l.putBad += qty;
        l.destroyed += qty;
        if (o.waitTotal() == 0) {
            o.status = 30;
        }
        return o;
    }
}
