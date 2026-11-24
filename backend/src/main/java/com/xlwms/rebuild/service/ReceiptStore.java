package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * PDA 收货（/pda/receipt/** 新接口）的内存数据，重启或 reset 后恢复。收货会在 {@link PutawayStore} 生成/追加上架单。
 * 数据虚构，规则与编码是本项目假设：
 * <ul>
 *   <li>forecastType：1 按箱预报（箱唛 + 箱内 SKU 明细）/ 2 按箱型预报 / 3 按 SKU 预报；inboundType：1 普通入库 / 2 中转入库</li>
 *   <li>入库单 status 沿用网页端观察到的 10 在途 / 11 收货中 / 15 已收货；“完成收货”后即使少收也置为 15</li>
 *   <li>不允许超收；一个箱唛只能收一次</li>
 *   <li>rowVersion 每次变更加 1，请求里带了 rowVersion 就必须与当前一致（乐观锁）</li>
 *   <li>箱型状态：0 未收 / 1 部分收货 / 2 已收完</li>
 * </ul>
 */
@Service
public class ReceiptStore {

    public static final Map<Integer, String> FORECAST_TYPE = Map.of(1, "按箱", 2, "按箱型", 3, "按SKU");
    public static final Map<Integer, String> STATUS = Map.of(10, "在途", 11, "收货中", 15, "已收货");

    public static class SkuLine {
        public final int asnQty;
        public int receiptQty;

        SkuLine(int asnQty) {
            this.asnQty = asnQty;
        }
    }

    public static class Box {
        public final String boxBarcode;
        public final Map<String, SkuLine> skus = new LinkedHashMap<>();
        public boolean received;

        Box(String boxBarcode) {
            this.boxBarcode = boxBarcode;
        }

        public int asnQty() {
            return skus.values().stream().mapToInt(s -> s.asnQty).sum();
        }

        public int receiptQty() {
            return skus.values().stream().mapToInt(s -> s.receiptQty).sum();
        }
    }

    public static class BoxTypeLine {
        public final String boxType;
        public final int asnBoxQty;
        public int receiptQty;
        /** 实测箱规（cm / kg），未测量为 null */
        public double[] measured;

        BoxTypeLine(String boxType, int asnBoxQty) {
            this.boxType = boxType;
            this.asnBoxQty = asnBoxQty;
        }

        public int status() {
            return receiptQty == 0 ? 0 : receiptQty < asnBoxQty ? 1 : 2;
        }
    }

    public static class Asn {
        public final String asnNo;
        public final String sourceNo;
        public final String trackingNo;
        public final String customerCode;
        public final String customerName;
        public final int forecastType;
        public final int inboundType;
        public final int palletReceiptFlag;
        public final String createTime;
        public final Map<String, Box> boxes = new LinkedHashMap<>();
        public final Map<String, BoxTypeLine> boxTypes = new LinkedHashMap<>();
        /** 按 SKU 预报（forecastType=3）的明细，可多次收货累计 */
        public final Map<String, SkuLine> skuLines = new LinkedHashMap<>();
        public boolean finished;
        public int rowVersion = 1;

        Asn(String asnNo, String customerCode, String customerName, int forecastType, int inboundType, int palletReceiptFlag, String createTime) {
            this.asnNo = asnNo;
            this.sourceNo = "CS" + asnNo.substring(2);
            this.trackingNo = "TRKIN" + asnNo.substring(asnNo.length() - 4);
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.forecastType = forecastType;
            this.inboundType = inboundType;
            this.palletReceiptFlag = palletReceiptFlag;
            this.createTime = createTime;
        }

        public int boxCount() {
            return boxes.size() + boxTypes.values().stream().mapToInt(b -> b.asnBoxQty).sum() + (skuLines.isEmpty() ? 0 : 1);
        }

        public int receivedBoxCount() {
            // 按 SKU 预报的单没有箱，整单记为 1 “箱”，全部收完才算收完
            int skuDone = skuLines.isEmpty() ? 0 : skuLines.values().stream().allMatch(l -> l.receiptQty == l.asnQty) ? 1 : 0;
            return (int) boxes.values().stream().filter(b -> b.received).count() + boxTypes.values().stream().mapToInt(b -> b.receiptQty).sum() + skuDone;
        }

        public boolean anyReceived() {
            return boxes.values().stream().anyMatch(b -> b.received) || boxTypes.values().stream().anyMatch(t -> t.receiptQty > 0)
                    || skuLines.values().stream().anyMatch(l -> l.receiptQty > 0);
        }

        public int status() {
            if (finished || receivedBoxCount() == boxCount()) {
                return 15;
            }
            return anyReceived() ? 11 : 10;
        }
    }

    private final StockStore stock;
    private final PutawayStore putaway;
    private final Map<String, Asn> asns = new LinkedHashMap<>();

    public ReceiptStore(StockStore stock, PutawayStore putaway) {
        this.stock = stock;
        this.putaway = putaway;
        reset();
    }

    public synchronized void reset() {
        asns.clear();
        putaway.reset();
        Asn a1 = asn("RA2611000001", "C001", "示例客户1", 1, 1, 0, "2026-11-23 15:00:00");
        box(a1, "BX-0001-01", "SKU-0001", 10, "SKU-0002", 5);
        box(a1, "BX-0001-02", "SKU-0001", 10);
        box(a1, "BX-0001-03", "SKU-0012", 4);
        Asn a2 = asn("RA2611000002", "C005", "示例客户5", 2, 1, 0, "2026-11-23 16:00:00");
        a2.boxTypes.put("BOX-0010-A", new BoxTypeLine("BOX-0010-A", 4));
        a2.boxTypes.put("BOX-0011-A", new BoxTypeLine("BOX-0011-A", 2));
        Asn a3 = asn("RA2611000003", "C005", "示例客户5", 2, 2, 0, "2026-11-24 07:00:00");
        a3.boxTypes.put("BOX-0010-A", new BoxTypeLine("BOX-0010-A", 3));
        Asn a4 = asn("RA2611000004", "C002", "示例客户2", 1, 1, 1, "2026-11-24 07:30:00");
        box(a4, "BX-0004-01", "SKU-0004", 12);
        box(a4, "BX-0004-02", "SKU-0005", 6, "SKU-0013", 3);
        Asn a5 = asn("RA2611000005", "C003", "示例客户3", 3, 1, 0, "2026-11-24 08:00:00");
        a5.skuLines.put("SKU-0006", new SkuLine(20));
        a5.skuLines.put("SKU-0003", new SkuLine(6));
    }

    private Asn asn(String no, String cc, String cn, int forecastType, int inboundType, int palletFlag, String time) {
        Asn a = new Asn(no, cc, cn, forecastType, inboundType, palletFlag, time);
        asns.put(no, a);
        return a;
    }

    private static void box(Asn a, String barcode, Object... skuQty) {
        Box b = new Box(barcode);
        for (int i = 0; i < skuQty.length; i += 2) {
            b.skus.put((String) skuQty[i], new SkuLine((Integer) skuQty[i + 1]));
        }
        a.boxes.put(barcode, b);
    }

    // ---------- 查询 ----------

    public synchronized List<Asn> list() {
        return new ArrayList<>(asns.values());
    }

    public synchronized Asn get(String asnNo) {
        return Optional.ofNullable(asns.get(asnNo == null ? "" : asnNo.trim()))
                .orElseThrow(() -> new BizException("入库单不存在：" + asnNo));
    }

    /** 按入库单号 / 来源单号 / 跟踪号 / 箱唛查。 */
    public synchronized List<Asn> search(String param) {
        String p = param == null ? "" : param.trim();
        List<Asn> hit = asns.values().stream().filter(a -> a.asnNo.equals(p) || a.sourceNo.equals(p) || a.trackingNo.equals(p)
                || a.boxes.containsKey(p)).toList();
        if (hit.isEmpty()) {
            throw new BizException("未找到入库单：" + param);
        }
        return hit;
    }

    // ---------- 收货 ----------

    private void checkOpen(Asn a, String rowVersion) {
        if (a.finished) {
            throw new BizException("入库单已完成收货：" + a.asnNo);
        }
        if (rowVersion != null && !rowVersion.isBlank() && !rowVersion.trim().equals(String.valueOf(a.rowVersion))) {
            throw new BizException("入库单已被修改（当前版本 " + a.rowVersion + "），请刷新后重试");
        }
    }

    /**
     * 收一个箱唛。skuQty 为空表示按预报全收；否则按给定数量收（可少收，不可超收，不能出现箱里没有的 SKU）。
     * palletCode 不为空表示托盘收货，上架单会记录托盘与箱的绑定。
     */
    public synchronized Box receiveBox(String asnNo, String rowVersion, String boxBarcode, Map<String, Integer> skuQty, String palletCode) {
        Asn a = get(asnNo);
        Map<String, Integer> qty = checkBox(a, rowVersion, boxBarcode, skuQty);
        Box b = a.boxes.get(boxBarcode.trim());
        qty.forEach((sku, q) -> b.skus.get(sku).receiptQty = q);
        b.received = true;
        a.rowVersion++;
        putaway.addBox(a, b.boxBarcode, qty, palletCode);
        return b;
    }

    /** 只校验不收货，返回实际要收的 SKU 数量。批量 / 托盘收货先对全部箱子调用它，全部通过再真正收货。 */
    public synchronized Map<String, Integer> checkBox(Asn a, String rowVersion, String boxBarcode, Map<String, Integer> skuQty) {
        checkOpen(a, rowVersion);
        if (a.forecastType != 1) {
            throw new BizException("入库单 " + a.asnNo + " 是按箱型预报，请按箱型收货");
        }
        Box b = Optional.ofNullable(a.boxes.get(boxBarcode == null ? "" : boxBarcode.trim()))
                .orElseThrow(() -> new BizException("箱唛不属于入库单 " + a.asnNo + "：" + boxBarcode));
        if (b.received) {
            throw new BizException("该箱已收货：" + b.boxBarcode);
        }
        Map<String, Integer> qty = new LinkedHashMap<>();
        if (skuQty == null || skuQty.isEmpty()) {
            b.skus.forEach((sku, l) -> qty.put(sku, l.asnQty));
        } else {
            skuQty.forEach((sku, q) -> {
                SkuLine l = b.skus.get(sku);
                if (l == null) {
                    throw new BizException("箱 " + b.boxBarcode + " 中没有 SKU " + sku);
                }
                if (q == null || q < 0 || q > l.asnQty) {
                    throw new BizException("SKU " + sku + " 收货数量需在 0~" + l.asnQty + " 之间（不允许超收）");
                }
                qty.merge(sku, q, Integer::sum);
            });
        }
        return qty;
    }

    /** 只校验不收货。already 为同一批次里这个箱型已经要收的箱数（批量提交时累计校验）。 */
    public synchronized BoxTypeLine checkBoxType(Asn a, String rowVersion, String boxType, int qty, int already) {
        checkOpen(a, rowVersion);
        BoxTypeLine l = Optional.ofNullable(a.boxTypes.get(boxType == null ? "" : boxType.trim()))
                .orElseThrow(() -> new BizException("箱型不属于入库单 " + a.asnNo + "：" + boxType));
        if (qty <= 0 || l.receiptQty + already + qty > l.asnBoxQty) {
            throw new BizException("箱型 " + l.boxType + " 收货箱数超出预报，剩余可收 " + (l.asnBoxQty - l.receiptQty - already));
        }
        stock.boxType(l.boxType);
        return l;
    }

    public synchronized BoxTypeLine receiveBoxType(String asnNo, String rowVersion, String boxType, int qty, String palletCode) {
        Asn a = get(asnNo);
        BoxTypeLine l = checkBoxType(a, rowVersion, boxType, qty, 0);
        l.receiptQty += qty;
        a.rowVersion++;
        putaway.addBoxType(a, l.boxType, qty, palletCode);
        return l;
    }

    /** 只校验不收货：按 SKU 收货。qty 为本批每个 SKU 的合计数量。 */
    public synchronized void checkSkus(Asn a, String rowVersion, Map<String, Integer> qty) {
        checkOpen(a, rowVersion);
        if (a.forecastType != 3) {
            throw new BizException("入库单 " + a.asnNo + " 不是按 SKU 预报");
        }
        if (qty.isEmpty()) {
            throw new BizException("请至少收一个 SKU");
        }
        qty.forEach((sku, q) -> {
            SkuLine l = a.skuLines.get(sku);
            if (l == null) {
                throw new BizException("SKU 不属于入库单 " + a.asnNo + "：" + sku);
            }
            if (q == null || q <= 0 || l.receiptQty + q > l.asnQty) {
                throw new BizException("SKU " + sku + " 收货数量超出预报，剩余可收 " + (l.asnQty - l.receiptQty));
            }
        });
    }

    /** 按 SKU 收货（调用前先 checkSkus）。palletCode 不为空表示这些货在该托盘上。 */
    public synchronized void receiveSkus(String asnNo, Map<String, Integer> qty, String palletCode) {
        Asn a = get(asnNo);
        qty.forEach((sku, q) -> a.skuLines.get(sku).receiptQty += q);
        a.rowVersion++;
        putaway.addSkus(a, qty, palletCode);
    }

    /** 整单收货：剩余的箱、箱型、SKU 全部按预报收完并完成收货。 */
    public synchronized Asn receiveAllAndFinish(String asnNo, String rowVersion) {
        Asn a = get(asnNo);
        checkOpen(a, rowVersion);
        for (Box b : a.boxes.values()) {
            if (!b.received) {
                receiveBox(asnNo, null, b.boxBarcode, null, null);
            }
        }
        for (BoxTypeLine t : a.boxTypes.values()) {
            if (t.receiptQty < t.asnBoxQty) {
                receiveBoxType(asnNo, null, t.boxType, t.asnBoxQty - t.receiptQty, null);
            }
        }
        Map<String, Integer> rest = new LinkedHashMap<>();
        a.skuLines.forEach((sku, l) -> {
            if (l.receiptQty < l.asnQty) {
                rest.put(sku, l.asnQty - l.receiptQty);
            }
        });
        if (!rest.isEmpty()) {
            receiveSkus(asnNo, rest, null);
        }
        return finish(asnNo);
    }

    public synchronized Asn finish(String asnNo) {
        Asn a = get(asnNo);
        if (a.finished) {
            throw new BizException("入库单已完成收货：" + a.asnNo);
        }
        if (!a.anyReceived()) {
            throw new BizException("入库单还没有收货，不能完成");
        }
        a.finished = true;
        a.rowVersion++;
        return a;
    }

    /** 按箱型测量箱规（记录在入库单的箱型上）。 */
    public synchronized void measureBoxType(String asnNo, String boxType, double l, double w, double h, double kg) {
        Asn a = get(asnNo);
        BoxTypeLine line = Optional.ofNullable(a.boxTypes.get(boxType)).orElseThrow(() -> new BizException("箱型不属于入库单 " + a.asnNo + "：" + boxType));
        if (l <= 0 || w <= 0 || h <= 0 || kg <= 0) {
            throw new BizException("箱型 " + boxType + " 的尺寸重量必须大于 0");
        }
        line.measured = new double[] {l, w, h, kg};
        a.rowVersion++;
    }

    public synchronized String putawayNoOf(String asnNo) {
        return "PA" + get(asnNo).asnNo.substring(2);
    }
}
