package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
import com.xlwms.rebuild.service.OutboundSampleStore.Pack;
import com.xlwms.rebuild.service.OutboundSampleStore.PackItem;
import com.xlwms.rebuild.service.OutboundSampleStore.Wave;
import com.xlwms.rebuild.service.StockStore.Row;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 设备任务（自动化存储设备）的本地模拟。接口与字段取自 APK 的 Device*Req/Resp，下面的规则全部是本项目假设：
 * <ul>
 *   <li>设备库位固定为 {@link #DEVICE_CELLS}；只有涉及这些库位的作业才需要设备出库位，其他直接放行（canProceed=true，不建任务）。</li>
 *   <li>设备“瞬间”处理完：库位 {@link #FAULT_CELL} 上的行模拟异常（料箱未到位），其余行成功。</li>
 *   <li>同一 bizType + bizNo 重复准备返回同一个任务（幂等）；异常行人工处理（resolve）后视为完成。</li>
 *   <li>状态串 deviceStatus：NONE 不需要设备 / EXCEPTION 有未处理异常 / COMPLETED 完成。bizStatus：WAITING / READY。</li>
 * </ul>
 */
@Component
public class DeviceStore {

    public static final String VENDOR = "DEMO-DEVICE";
    public static final List<String> DEVICE_CELLS = List.of("B-01-01", "B-01-03");
    public static final String FAULT_CELL = "B-01-03";

    public static class Line {
        public final long taskId;
        public final int seqNo;
        public final int subSeqNo;
        public final String outOrderNo;
        public final String sku;
        public final String cellNo;
        public final int requestedQty;
        public int processedQty;
        public boolean success;
        public boolean resolved;
        public String exceptionType = "";
        public String exceptionMsg = "";
        public final long sentTime;
        public long callbackTime;

        Line(long taskId, int seqNo, int subSeqNo, String outOrderNo, String sku, String cellNo, int requestedQty, long now) {
            this.taskId = taskId;
            this.seqNo = seqNo;
            this.subSeqNo = subSeqNo;
            this.outOrderNo = outOrderNo;
            this.sku = sku;
            this.cellNo = cellNo;
            this.requestedQty = requestedQty;
            this.sentTime = now;
        }

        public boolean done() {
            return success || resolved;
        }

        public String taskStatus() {
            return resolved ? "RESOLVED" : success ? "SUCCESS" : "FAILED";
        }
    }

    public static class Task {
        public final long id;
        public final String bizType;
        public final String bizNo;
        public final String whCode;
        public final List<Line> lines = new ArrayList<>();

        Task(long id, String bizType, String bizNo, String whCode) {
            this.id = id;
            this.bizType = bizType;
            this.bizNo = bizNo;
            this.whCode = whCode;
        }

        public String deviceStatus() {
            if (lines.isEmpty()) {
                return "NONE";
            }
            return lines.stream().allMatch(Line::done) ? "COMPLETED" : "EXCEPTION";
        }

        public boolean canProceed() {
            return !"EXCEPTION".equals(deviceStatus());
        }

        public String exceptionMsg() {
            return lines.stream().filter(l -> !l.done()).map(l -> l.exceptionMsg).findFirst().orElse("");
        }
    }

    private final StockStore stock;
    private final OutboundSampleStore outbound;
    private final Map<String, Task> tasks = new LinkedHashMap<>();
    private long seq;

    public DeviceStore(StockStore stock, OutboundSampleStore outbound) {
        this.stock = stock;
        this.outbound = outbound;
        reset();
    }

    public synchronized void reset() {
        tasks.clear();
        seq = 7_100_000_000_000_000L;
    }

    private static String key(String bizType, String bizNo) {
        return bizType + "|" + bizNo;
    }

    /** 新建任务并模拟设备处理；cells 中不涉及设备库位的行不会进入任务。 */
    private Task create(String bizType, String bizNo, String whCode, List<Object[]> rows) {
        long now = System.currentTimeMillis();
        Task t = new Task(++seq, bizType, bizNo, whCode == null ? "" : whCode);
        for (Object[] r : rows) {
            String cell = (String) r[4];
            if (!DEVICE_CELLS.contains(cell)) {
                continue;
            }
            Line l = new Line(t.id, (int) r[0], (int) r[1], (String) r[2], (String) r[3], cell, (int) r[5], now);
            if (FAULT_CELL.equals(cell)) {
                l.exceptionType = "BIN_NOT_ARRIVED";
                l.exceptionMsg = "设备料箱未到位：" + cell;
            } else {
                l.success = true;
                l.processedQty = l.requestedQty;
                l.callbackTime = now;
            }
            t.lines.add(l);
        }
        tasks.put(key(bizType, bizNo), t);
        return t;
    }

    private Task existing(String bizType, String bizNo) {
        return tasks.get(key(bizType, bizNo));
    }

    // ---------- 准备 ----------

    /** 指定库位准备（整库位作业前），bizNo 由库位拼出。 */
    public synchronized Task prepareCells(List<String> cellNos, String whCode) {
        if (cellNos == null || cellNos.isEmpty()) {
            throw new BizException("请选择库位");
        }
        List<String> sorted = cellNos.stream().map(String::trim).distinct().sorted().toList();
        for (String c : sorted) {
            if (!stock.cells().contains(c)) {
                throw new BizException("库位不存在：" + c);
            }
        }
        String bizNo = "CELL:" + String.join(",", sorted);
        Task t = existing("CELL", bizNo);
        if (t != null) {
            return t;
        }
        List<Object[]> rows = new ArrayList<>();
        int seqNo = 0;
        for (String c : sorted) {
            for (Row r : stock.find(c, null, null, null, null)) {
                rows.add(new Object[] {++seqNo, 1, "", r.productSku, c, r.qty});
            }
        }
        return create("CELL", bizNo, whCode, rows);
    }

    /** 按 SKU + 库位定位库存（库内查询/移库前）。 */
    public synchronized Task locateStock(String bizType, List<String[]> skuCells, String whCode) {
        if (skuCells == null || skuCells.isEmpty()) {
            throw new BizException("请选择 SKU 与库位");
        }
        List<Object[]> rows = new ArrayList<>();
        StringBuilder no = new StringBuilder("STOCK:");
        int seqNo = 0;
        for (String[] p : skuCells) {
            String sku = p[0] == null ? "" : p[0].trim();
            String cell = p[1] == null ? "" : p[1].trim();
            int qty = stock.find(cell, null, sku, null, null).stream().mapToInt(r -> r.qty).sum();
            if (qty <= 0) {
                throw new BizException("库位 " + cell + " 没有 " + sku + " 的库存");
            }
            rows.add(new Object[] {++seqNo, 1, "", sku, cell, qty});
            no.append(sku).append('@').append(cell).append(';');
        }
        String type = bizType == null || bizType.isBlank() ? "STOCK" : bizType.trim();
        Task t = existing(type, no.toString());
        return t != null ? t : create(type, no.toString(), whCode, rows);
    }

    /** 出库单准备：出库单各包裹明细所在库位取自其波次的拣货行。 */
    public synchronized Task prepareBill(String bizType, String bizNo, String whCode) {
        if (!"DELIVERY".equals(bizType)) {
            throw new BizException("本地只支持 bizType=DELIVERY 的单据准备");
        }
        Task t = existing(bizType, bizNo);
        if (t != null) {
            return t;
        }
        Delivery d = outbound.delivery(bizNo);
        return create(bizType, bizNo, whCode, deliveryRows(outbound.wave(d.waveNo), d, 1));
    }

    /** 波次准备：逐个出库单、逐个明细生成任务行（seqNo = 出库单序号，subSeqNo = 明细序号）。 */
    public synchronized Task prepareWave(String bizType, String waveNo, String whCode) {
        if (bizType != null && !bizType.isBlank() && !"WAVE".equals(bizType)) {
            throw new BizException("波次准备的 bizType 只能是 WAVE");
        }
        Task t = existing("WAVE", waveNo);
        if (t != null) {
            return t;
        }
        Wave w = outbound.wave(waveNo);
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < w.deliveryNos.size(); i++) {
            rows.addAll(deliveryRows(w, outbound.delivery(w.deliveryNos.get(i)), i + 1));
        }
        return create("WAVE", waveNo, whCode, rows);
    }

    private List<Object[]> deliveryRows(Wave w, Delivery d, int seqNo) {
        List<Object[]> rows = new ArrayList<>();
        int sub = 0;
        for (Pack p : d.packs) {
            for (PackItem it : p.items) {
                String cell = w.lines.stream().filter(l -> l.productSku.equals(it.sku)).map(l -> l.cellNo).findFirst().orElse("");
                rows.add(new Object[] {seqNo, ++sub, d.deliveryNo, it.sku, cell, it.qty});
            }
        }
        return rows;
    }

    // ---------- 查询 / 处理 ----------

    public synchronized Task detail(String bizType, String bizNo) {
        Task t = existing(bizType, bizNo);
        if (t == null) {
            throw new BizException("没有设备任务：" + bizType + " " + bizNo);
        }
        return t;
    }

    /** 人工处理全部异常行。 */
    public synchronized Task resolve(String bizType, String bizNo) {
        Task t = detail(bizType, bizNo);
        List<Line> open = t.lines.stream().filter(l -> !l.done()).toList();
        if (open.isEmpty()) {
            throw new BizException("该任务没有待处理的异常");
        }
        open.forEach(DeviceStore::markResolved);
        return t;
    }

    /** 人工处理波次中某个出库单的某一行。 */
    public synchronized Task resolveWaveLine(String waveNo, String outOrderNo, int subSeqNo) {
        Task t = detail("WAVE", waveNo);
        Line l = t.lines.stream().filter(x -> x.outOrderNo.equals(outOrderNo) && x.subSeqNo == subSeqNo).findFirst()
                .orElseThrow(() -> new BizException("波次任务中没有该出库单明细：" + outOrderNo + " #" + subSeqNo));
        if (l.done()) {
            throw new BizException("该明细没有待处理的异常");
        }
        markResolved(l);
        return t;
    }

    private static void markResolved(Line l) {
        l.resolved = true;
        l.processedQty = l.requestedQty;
        l.callbackTime = System.currentTimeMillis();
    }

}
