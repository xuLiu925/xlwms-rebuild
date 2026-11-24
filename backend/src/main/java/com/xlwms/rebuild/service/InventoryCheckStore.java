package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.service.StockStore.Product;
import com.xlwms.rebuild.service.StockStore.Row;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * PDA 盘点（/pda/inventoryCheck/**）的内存示例数据，重启或 reset 后恢复。数据虚构，规则是本项目假设：
 * <ul>
 *   <li>盘点单 status：0 待盘点 / 1 盘点中 / 2 已完成 / 3 已取消；checkType：1 明盘 / 2 盲盘</li>
 *   <li>网页端（批次 9）可新建盘点单：库位必须存在且不能和其他未完成的盘点单重叠；只有待盘点的单能取消，取消后 PDA 列表不再显示</li>
 *   <li>start 时按 {@link StockStore} 当前库存给每个库位拍账面快照（bookStock）</li>
 *   <li>每个库位提交实盘数（solidStock）后标记完成；全部库位完成才能 complete</li>
 *   <li>complete 时把实盘数写回库存（盘盈盘亏直接调整）；只调散件库存</li>
 * </ul>
 */
@Service
public class InventoryCheckStore {

    public static class Detail {
        public final String itemId;
        public final String cellNo;
        public final String sku;
        public final int productQuality;
        public final int bookStock;
        public Integer solidStock;
        public String remark = "";

        Detail(String itemId, String cellNo, String sku, int productQuality, int bookStock) {
            this.itemId = itemId;
            this.cellNo = cellNo;
            this.sku = sku;
            this.productQuality = productQuality;
            this.bookStock = bookStock;
        }
    }

    public static class Cell {
        public final String cellNo;
        public final List<Detail> details = new ArrayList<>();
        public boolean completed;

        Cell(String cellNo) {
            this.cellNo = cellNo;
        }
    }

    public static class Order {
        public final String orderSn;
        public final int checkType;
        public final String remark;
        public final String createTime;
        public final List<String> cellNos;
        public final Map<String, Cell> cells = new LinkedHashMap<>();
        public int status;

        Order(String orderSn, int checkType, String remark, String createTime, List<String> cellNos) {
            this.orderSn = orderSn;
            this.checkType = checkType;
            this.remark = remark;
            this.createTime = createTime;
            this.cellNos = cellNos;
        }

        public int completedCellCount() {
            return (int) cells.values().stream().filter(c -> c.completed).count();
        }
    }

    /** 盘点校验失败的一条原因（对应 APK 的 InventoryCheckError）。 */
    public record CheckError(String cause, String cellNo, String customerCode, String customerName, String sku) {
    }

    private final StockStore stock;
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private int itemSeq;

    public InventoryCheckStore(StockStore stock) {
        this.stock = stock;
        reset();
    }

    public synchronized void reset() {
        orders.clear();
        itemSeq = 0;
        orders.put("IC2611000001", new Order("IC2611000001", 1, "示例明盘：A 区", "2026-11-24 08:00:00", List.of("A-01-01", "A-01-02", "A-02-01")));
        orders.put("IC2611000002", new Order("IC2611000002", 2, "示例盲盘：B 区", "2026-11-24 08:30:00", List.of("B-01-01", "B-01-03")));
    }

    public synchronized List<Order> list() {
        return new ArrayList<>(orders.values());
    }

    public synchronized Order get(String orderSn) {
        return Optional.ofNullable(orders.get(orderSn == null ? "" : orderSn.trim()))
                .orElseThrow(() -> new BizException("盘点单不存在：" + orderSn));
    }

    /** 网页端新建盘点单。单号按当前最大序号 + 1。 */
    public synchronized Order create(int checkType, List<String> cellNos, String remark, String createTime) {
        if (checkType != 1 && checkType != 2) {
            throw new BizException("盘点方式只能是 1 明盘 / 2 盲盘");
        }
        if (cellNos == null || cellNos.isEmpty()) {
            throw new BizException("请选择要盘点的库位");
        }
        List<String> cells = cellNos.stream().map(c -> c == null ? "" : c.trim()).distinct().toList();
        java.util.Set<String> known = stock.cells();
        for (String c : cells) {
            if (!known.contains(c)) {
                throw new BizException("库位不存在：" + c);
            }
            for (Order o : orders.values()) {
                if ((o.status == 0 || o.status == 1) && o.cellNos.contains(c)) {
                    throw new BizException("库位 " + c + " 已在未完成的盘点单 " + o.orderSn + " 中");
                }
            }
        }
        if (remark != null && remark.length() > 128) {
            throw new BizException("备注不能超过 128 个字符");
        }
        int max = orders.keySet().stream().mapToInt(k -> Integer.parseInt(k.substring(6))).max().orElse(0);
        String sn = String.format("IC2611%06d", max + 1);
        Order o = new Order(sn, checkType, remark == null ? "" : remark.trim(), createTime, cells);
        orders.put(sn, o);
        return o;
    }

    /** 取消：只有待盘点的单能取消。 */
    public synchronized Order cancel(String orderSn) {
        Order o = get(orderSn);
        if (o.status != 0) {
            throw new BizException("只有待盘点的单能取消");
        }
        o.status = 3;
        return o;
    }

    private Detail detail(String cellNo, String sku, int quality, int book) {
        return new Detail(String.valueOf(++itemSeq), cellNo, sku, quality, book);
    }

    /** 开始盘点：给每个库位拍账面快照。重复开始返回错误列表而不是抛异常（APK 的返回类型是 List&lt;InventoryCheckError&gt;）。 */
    public synchronized List<CheckError> start(String orderSn) {
        Order o = get(orderSn);
        if (o.status == 2) {
            return List.of(new CheckError("盘点单已完成", "", "", "", ""));
        }
        if (o.status == 3) {
            return List.of(new CheckError("盘点单已取消", "", "", "", ""));
        }
        if (o.status == 1) {
            return List.of();
        }
        for (String cellNo : o.cellNos) {
            Cell c = new Cell(cellNo);
            for (Row r : stock.find(cellNo, null, null, "", null)) {
                c.details.add(detail(cellNo, r.productSku, r.productQuality, r.qty));
            }
            o.cells.put(cellNo, c);
        }
        o.status = 1;
        return List.of();
    }

    private Cell cell(Order o, String cellNo) {
        if (o.status != 1) {
            throw new BizException("盘点单不在盘点中");
        }
        Cell c = o.cells.get(cellNo == null ? "" : cellNo.trim());
        if (c == null) {
            throw new BizException("库位不在本盘点单中：" + cellNo);
        }
        return c;
    }

    /** 扫描产品：返回该库位里这个产品的明细；账面没有但产品存在时新增一行（bookStock=0，即盘盈）。 */
    public synchronized List<Detail> scan(String orderSn, String cellNo, String scanCode) {
        Cell c = cell(get(orderSn), cellNo);
        Product p = stock.findProduct(scanCode);
        if (p == null) {
            throw new BizException("未识别的条码：" + scanCode);
        }
        List<Detail> hit = c.details.stream().filter(d -> d.sku.equals(p.sku())).toList();
        if (!hit.isEmpty()) {
            return hit;
        }
        Detail d = detail(c.cellNo, p.sku(), 1, 0);
        c.details.add(d);
        return List.of(d);
    }

    /** 提交一个库位的实盘数。entries: itemId 或 sku → [solidStock, remark] */
    public synchronized Order submitCell(String orderSn, String cellNo, List<Object[]> entries) {
        Order o = get(orderSn);
        Cell c = cell(o, cellNo);
        for (Object[] e : entries) {
            String itemId = (String) e[0];
            String sku = (String) e[1];
            Integer solid = (Integer) e[2];
            if (solid == null || solid < 0) {
                throw new BizException("实盘数量不能为空或负数");
            }
            Detail d = c.details.stream().filter(x -> (itemId != null && itemId.equals(x.itemId)) || (itemId == null && x.sku.equals(sku)))
                    .findFirst().orElseGet(() -> {
                        Product p = stock.product(sku);
                        Detail nd = detail(c.cellNo, p.sku(), 1, 0);
                        c.details.add(nd);
                        return nd;
                    });
            d.solidStock = solid;
            d.remark = e[3] == null ? "" : (String) e[3];
        }
        // 未提交实盘数的明细视为 0（整库位盘点）
        c.details.forEach(d -> {
            if (d.solidStock == null) {
                d.solidStock = 0;
            }
        });
        c.completed = true;
        return o;
    }

    public synchronized List<CheckError> complete(String orderSn) {
        Order o = get(orderSn);
        if (o.status != 1) {
            return List.of(new CheckError(o.status == 0 ? "盘点单尚未开始" : o.status == 3 ? "盘点单已取消" : "盘点单已完成", "", "", "", ""));
        }
        List<CheckError> errors = new ArrayList<>();
        o.cells.values().stream().filter(c -> !c.completed)
                .forEach(c -> errors.add(new CheckError("库位未盘点", c.cellNo, "", "", "")));
        if (!errors.isEmpty()) {
            return errors;
        }
        for (Cell c : o.cells.values()) {
            for (Detail d : c.details) {
                if (d.solidStock != d.bookStock) {
                    stock.setQty(c.cellNo, d.sku, "", d.productQuality, d.solidStock);
                }
            }
        }
        o.status = 2;
        return List.of();
    }
}
