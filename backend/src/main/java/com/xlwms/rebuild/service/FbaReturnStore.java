package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * FBA 退货入库与退货拣货（/pda/blReturnIn/**）的内存数据，重启或 reset 后恢复。上架写入、拣货扣减 {@link StockStore}。
 * 数据虚构，规则与编码是本项目假设：
 * <ul>
 *   <li>FBA 退货任务 status：0 待签收 / 10 已签收 / 20 待清点 / 30 待上架 / 40 已完成</li>
 *   <li>confirmSign 扫快递签收（0→10）；updateStatusSign 确认签收明细（10→20）；updateStatusInventory 清点（20→30）；
 *       全部上架后 40。没有预报的包裹走 signAndInventory，一步建成“待上架”任务</li>
 *   <li>清点时可以加预报外的 FNSKU（isNewAdd=true）；次品只能上 E 区</li>
 *   <li>退货拣货任务 pickStatus：0 待拣货 / 1 拣货中 / 2 已完成；finishTaskOrder 允许少拣后结束</li>
 * </ul>
 */
@Service
public class FbaReturnStore {

    public static final Map<Integer, String> STATUS = Map.of(0, "待签收", 10, "已签收", 20, "待清点", 30, "待上架", 40, "已完成");

    public static class Line {
        public final String fnsku;
        public final String productSku;
        public final int returnQty;
        public final boolean newAdd;
        public int good;
        public int bad;
        public int putGood;
        public int putBad;

        Line(String fnsku, String productSku, int returnQty, boolean newAdd) {
            this.fnsku = fnsku;
            this.productSku = productSku;
            this.returnQty = returnQty;
            this.newAdd = newAdd;
        }

        public int wait(int quality) {
            return quality == 2 ? bad - putBad : good - putGood;
        }
    }

    public static class Task {
        public final String fritTaskNo;
        public final String sourceNo;
        public final String customerCode;
        public final String customerName;
        public final String expressNo;
        public final String logisticsCarrier;
        public final Map<String, Line> lines = new LinkedHashMap<>();
        public String temporaryCellNo = "";
        public String remark = "";
        public int status;

        Task(String fritTaskNo, String sourceNo, String customerCode, String customerName, String expressNo, String logisticsCarrier) {
            this.fritTaskNo = fritTaskNo;
            this.sourceNo = sourceNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.expressNo = expressNo;
            this.logisticsCarrier = logisticsCarrier;
        }

        public int waitTotal() {
            return lines.values().stream().mapToInt(l -> l.wait(1) + l.wait(2)).sum();
        }
    }

    public static class PickLine {
        public final String fnsku;
        public final String productSku;
        public final String cellNo;
        public final int planQty;
        public int pickQty;

        PickLine(String fnsku, String productSku, String cellNo, int planQty) {
            this.fnsku = fnsku;
            this.productSku = productSku;
            this.cellNo = cellNo;
            this.planQty = planQty;
        }
    }

    public static class PickTask {
        public final String frstTaskNo;
        public final String customerCode;
        public final String customerName;
        public final List<PickLine> lines = new ArrayList<>();
        public int pickStatus;

        PickTask(String frstTaskNo, String customerCode, String customerName) {
            this.frstTaskNo = frstTaskNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
        }
    }

    private final StockStore stock;
    private final Map<String, Task> tasks = new LinkedHashMap<>();
    private final Map<String, PickTask> picks = new LinkedHashMap<>();
    private int seq;

    public FbaReturnStore(StockStore stock) {
        this.stock = stock;
        reset();
    }

    public synchronized void reset() {
        tasks.clear();
        picks.clear();
        seq = 2;
        Task t1 = task("FR2611000001", "FBA-RM-0001", "C001", "FBARTN0001", "示例承运商A");
        t1.lines.put("FN-DEMO-0001", new Line("FN-DEMO-0001", "SKU-0001", 3, false));
        t1.lines.put("FN-DEMO-0002", new Line("FN-DEMO-0002", "SKU-0002", 2, false));
        Task t2 = task("FR2611000002", "FBA-RM-0002", "C002", "FBARTN0002", "示例承运商B");
        Line l2 = new Line("FN-DEMO-0004", "SKU-0004", 5, false);
        l2.good = 4;
        l2.bad = 1;
        t2.lines.put("FN-DEMO-0004", l2);
        t2.status = 30;
        t2.temporaryCellNo = "E-01-01";
        PickTask p = new PickTask("PK2611000001", "C004", "示例客户4");
        p.lines.add(new PickLine("FN-DEMO-0008", "SKU-0008", "D-01-01", 3));
        p.lines.add(new PickLine("FN-DEMO-0009", "SKU-0009", "D-01-02", 2));
        picks.put(p.frstTaskNo, p);
    }

    private Task task(String no, String sourceNo, String cc, String expressNo, String carrier) {
        Task t = new Task(no, sourceNo, cc, stock.customers().getOrDefault(cc, cc), expressNo, carrier);
        tasks.put(no, t);
        return t;
    }

    // ---------- 查询 ----------

    public synchronized List<Task> list() {
        return new ArrayList<>(tasks.values());
    }

    public synchronized Task get(String fritTaskNo) {
        return Optional.ofNullable(tasks.get(fritTaskNo == null ? "" : fritTaskNo.trim()))
                .orElseThrow(() -> new BizException("FBA 退货任务不存在：" + fritTaskNo));
    }

    /** 按任务号 / 快递单号 / 来源单号找。 */
    public synchronized Task find(String fritTaskNo, String expressNo, String sourceNo) {
        for (Task t : tasks.values()) {
            if ((notBlank(fritTaskNo) && t.fritTaskNo.equals(fritTaskNo.trim())) || (notBlank(expressNo) && t.expressNo.equals(expressNo.trim()))
                    || (notBlank(sourceNo) && t.sourceNo.equals(sourceNo.trim()))) {
                return t;
            }
        }
        throw new BizException("未找到 FBA 退货任务：" + firstNonBlank(fritTaskNo, expressNo, sourceNo));
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String firstNonBlank(String... s) {
        for (String x : s) {
            if (notBlank(x)) {
                return x;
            }
        }
        return "";
    }

    private Product fnskuProduct(String fnsku, String customerCode) {
        Product p = stock.findProduct(fnsku);
        if (p == null) {
            throw new BizException("未识别的 FNSKU：" + fnsku);
        }
        if (notBlank(customerCode) && !p.customerCode().equals(customerCode.trim())) {
            throw new BizException("FNSKU " + fnsku + " 不属于客户 " + customerCode);
        }
        return p;
    }

    public synchronized Product scanFnsku(String fnsku, String customerCode) {
        return fnskuProduct(fnsku, customerCode);
    }

    private void need(Task t, int status, String action) {
        if (t.status != status) {
            throw new BizException("任务 " + t.fritTaskNo + " 当前状态为" + STATUS.get(t.status) + "，不能" + action);
        }
    }

    // ---------- 签收 / 清点 ----------

    public synchronized Task confirmSign(String trackNo, String customerCode, String temporaryCellNo) {
        Task t = tasks.values().stream().filter(x -> x.expressNo.equals(trackNo == null ? "" : trackNo.trim())).findFirst()
                .orElseThrow(() -> new BizException("没有该快递的 FBA 退货预报，请用“签收并清点”：" + trackNo));
        if (notBlank(customerCode) && !t.customerCode.equals(customerCode.trim())) {
            throw new BizException("快递 " + trackNo + " 的客户不是 " + customerCode);
        }
        need(t, 0, "签收");
        if (notBlank(temporaryCellNo)) {
            stock.requireCell(temporaryCellNo, true);
            t.temporaryCellNo = temporaryCellNo.trim();
        }
        t.status = 10;
        return t;
    }

    public synchronized Task updateSign(String fritTaskNo, List<String> fnskus, String temporaryCellNo) {
        Task t = get(fritTaskNo);
        need(t, 10, "确认签收");
        for (String f : fnskus) {
            if (!t.lines.containsKey(f)) {
                throw new BizException("FNSKU 不在任务 " + t.fritTaskNo + " 的预报中：" + f);
            }
        }
        if (notBlank(temporaryCellNo)) {
            stock.requireCell(temporaryCellNo, true);
            t.temporaryCellNo = temporaryCellNo.trim();
        }
        t.status = 20;
        return t;
    }

    /** 清点时追加预报外的 FNSKU。 */
    public synchronized Line addFnsku(String fritTaskNo, String fnsku) {
        Task t = get(fritTaskNo);
        need(t, 20, "清点");
        Product p = fnskuProduct(fnsku, t.customerCode);
        if (t.lines.containsKey(p.fnsku())) {
            throw new BizException("FNSKU 已在任务中：" + p.fnsku());
        }
        Line l = new Line(p.fnsku(), p.sku(), 0, true);
        t.lines.put(p.fnsku(), l);
        return l;
    }

    /** 清点：qty 为 FNSKU → [良品, 次品]，先全部校验再生效。 */
    public synchronized Task inventory(String fritTaskNo, Map<String, int[]> qty, String remark, String temporaryCellNo) {
        Task t = get(fritTaskNo);
        need(t, 20, "清点");
        checkQty(qty, t.customerCode);
        for (String f : qty.keySet()) {
            if (!t.lines.containsKey(f)) {
                throw new BizException("FNSKU 不在任务中，请先添加：" + f);
            }
        }
        if (notBlank(temporaryCellNo)) {
            stock.requireCell(temporaryCellNo, true);
            t.temporaryCellNo = temporaryCellNo.trim();
        }
        qty.forEach((f, v) -> {
            t.lines.get(f).good = v[0];
            t.lines.get(f).bad = v[1];
        });
        t.remark = remark == null ? "" : remark;
        t.status = 30;
        return t;
    }

    private void checkQty(Map<String, int[]> qty, String customerCode) {
        int total = 0;
        for (Map.Entry<String, int[]> e : qty.entrySet()) {
            fnskuProduct(e.getKey(), customerCode);
            if (e.getValue()[0] < 0 || e.getValue()[1] < 0) {
                throw new BizException("清点数量不能为负数");
            }
            total += e.getValue()[0] + e.getValue()[1];
        }
        if (total == 0) {
            throw new BizException("清点数量不能为 0");
        }
    }

    /** 无预报包裹：签收并清点一步完成，直接建成“待上架”任务。 */
    public synchronized Task signAndInventory(String customerCode, String expressNo, String carrier, Map<String, int[]> qty,
                                              String temporaryCellNo, String remark) {
        if (!stock.customers().containsKey(customerCode == null ? "" : customerCode)) {
            throw new BizException("客户不存在：" + customerCode);
        }
        if (!notBlank(expressNo) || tasks.values().stream().anyMatch(t -> t.expressNo.equals(expressNo.trim()))) {
            throw new BizException("快递单号为空或已有 FBA 退货任务：" + expressNo);
        }
        checkQty(qty, customerCode);
        if (notBlank(temporaryCellNo)) {
            stock.requireCell(temporaryCellNo, true);
        }
        String no = String.format("FR2611%06d", ++seq);
        Task t = task(no, "", customerCode, expressNo.trim(), carrier == null ? "" : carrier);
        qty.forEach((f, v) -> {
            Product p = stock.findProduct(f);
            Line l = new Line(p.fnsku(), p.sku(), 0, true);
            l.good = v[0];
            l.bad = v[1];
            t.lines.put(p.fnsku(), l);
        });
        t.temporaryCellNo = temporaryCellNo == null ? "" : temporaryCellNo.trim();
        t.remark = remark == null ? "" : remark;
        t.status = 30;
        return t;
    }

    // ---------- 上架 ----------

    public synchronized Task putaway(String fritTaskNo, String fnsku, int quality, int qty, String cellNo) {
        Task t = get(fritTaskNo);
        need(t, 30, "上架");
        Line l = t.lines.get(fnsku);
        if (l == null) {
            throw new BizException("FNSKU 不在任务中：" + fnsku);
        }
        if (qty <= 0 || qty > l.wait(quality)) {
            throw new BizException("上架数量需大于 0 且不超过待上架数 " + l.wait(quality));
        }
        stock.requireQualityCell(cellNo, quality);
        stock.putIn(cellNo, l.productSku, "", quality, qty);
        if (quality == 2) {
            l.putBad += qty;
        } else {
            l.putGood += qty;
        }
        if (t.waitTotal() == 0) {
            t.status = 40;
        }
        return t;
    }

    // ---------- 退货拣货 ----------

    public synchronized List<PickTask> pickTasks() {
        return picks.values().stream().filter(p -> p.pickStatus != 2).toList();
    }

    public synchronized PickTask pickTask(String frstTaskNo) {
        return Optional.ofNullable(picks.get(frstTaskNo == null ? "" : frstTaskNo.trim()))
                .orElseThrow(() -> new BizException("拣货任务不存在：" + frstTaskNo));
    }

    public synchronized PickLine pickLine(PickTask p, String fnsku, String cellNo) {
        return p.lines.stream().filter(l -> l.fnsku.equals(fnsku) && (cellNo == null || cellNo.isBlank() || l.cellNo.equals(cellNo.trim())))
                .findFirst().orElseThrow(() -> new BizException("任务 " + p.frstTaskNo + " 中没有该 FNSKU/库位的拣货：" + fnsku));
    }

    public synchronized PickTask pick(String frstTaskNo, String fnsku, String cellNo, int qty) {
        PickTask p = pickTask(frstTaskNo);
        if (p.pickStatus == 2) {
            throw new BizException("拣货任务已完成");
        }
        PickLine l = pickLine(p, fnsku, cellNo);
        if (qty <= 0 || l.pickQty + qty > l.planQty) {
            throw new BizException("拣货数量超出待拣数量，剩余 " + (l.planQty - l.pickQty));
        }
        stock.deduct(l.cellNo, l.productSku, "", 1, qty);
        l.pickQty += qty;
        p.pickStatus = p.lines.stream().allMatch(x -> x.pickQty == x.planQty) ? 2 : 1;
        return p;
    }

    public synchronized PickTask finishPick(String frstTaskNo) {
        PickTask p = pickTask(frstTaskNo);
        if (p.pickStatus == 2) {
            throw new BizException("拣货任务已完成");
        }
        if (p.lines.stream().allMatch(l -> l.pickQty == 0)) {
            throw new BizException("还没有拣货，不能结束任务");
        }
        p.pickStatus = 2;
        return p;
    }
}
