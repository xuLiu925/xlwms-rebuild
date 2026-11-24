package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.dto.review.ReviewItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 出库作业（波次拣货 → 复核 → 确认出库）的内存示例数据，进程重启即恢复初始状态。
 * 数据全部虚构。状态编码与流转规则是本项目的假设，真实系统的编码和规则未知：
 * <ul>
 *   <li>波次 pickStatus：0 待拣货 / 1 拣货中 / 2 已完成</li>
 *   <li>波次 pickWay：1 单品拣货（一票一件）/ 2 多品拣货（一票多件）</li>
 *   <li>出库单 status：0 待拣货 / 10 待复核 / 20 已复核 / 30 已出库</li>
 *   <li>波次拣完后，其出库单才可复核；全部包裹复核后才可确认出库</li>
 *   <li>网页端（批次 3）：多品波次拣完后可二次分拣（只计数，不卡复核）；称重页要求已复核，称重后即确认出库</li>
 * </ul>
 */
@Service
public class OutboundSampleStore {

    public static final String WH = "WH01";

    public static final Map<Integer, String> PICK_STATUS = Map.of(0, "待拣货", 1, "拣货中", 2, "已完成");
    public static final Map<Integer, String> PICK_WAY = Map.of(1, "单品拣货", 2, "多品拣货");
    public static final Map<Integer, String> DELIVERY_STATUS = Map.of(0, "待拣货", 10, "待复核", 20, "已复核", 30, "已出库");

    /** 波次中一个 SKU 在一个库位上的拣货任务。 */
    public static class PickLine {
        public final String productSku;
        public final String productName;
        public final String barcode;
        public final String cellNo;
        public final int planQty;
        public int pickQty;

        PickLine(String productSku, String productName, String barcode, String cellNo, int planQty) {
            this.productSku = productSku;
            this.productName = productName;
            this.barcode = barcode;
            this.cellNo = cellNo;
            this.planQty = planQty;
        }
    }

    public static class Wave {
        public final String waveNo;
        public final String customerCode;
        public final String customerName;
        public final String pickCartNo;
        public final int pickWay;
        public final String createTime;
        public final List<PickLine> lines = new ArrayList<>();
        public final List<String> deliveryNos = new ArrayList<>();
        public int pickStatus;

        Wave(String waveNo, String customerCode, String customerName, String pickCartNo, int pickWay, String createTime) {
            this.waveNo = waveNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.pickCartNo = pickCartNo;
            this.pickWay = pickWay;
            this.createTime = createTime;
        }

        public int planQty() {
            return lines.stream().mapToInt(l -> l.planQty).sum();
        }

        public int pickQty() {
            return lines.stream().mapToInt(l -> l.pickQty).sum();
        }
    }

    public static class PackItem {
        public final String sku;
        public final String barcode;
        public final String productName;
        public final int qty;
        public int scanQty;
        /** 二次分拣已分到该出库单格口的数量（网页端二次分拣页用） */
        public int sortQty;

        PackItem(String sku, String barcode, String productName, int qty) {
            this.sku = sku;
            this.barcode = barcode;
            this.productName = productName;
            this.qty = qty;
        }
    }

    public static class Pack {
        public final String packNo;
        public final int packSerial;
        public final List<PackItem> items = new ArrayList<>();
        /** 0 未复核 / 1 已复核（占位） */
        public int reviewStatus;

        Pack(String packNo, int packSerial) {
            this.packNo = packNo;
            this.packSerial = packSerial;
        }
    }

    public static class Delivery {
        public final String deliveryNo;
        public final String sourceNo;
        public final String expressNo;
        public final String customerCode;
        public final String customerName;
        public final String logisticsChannel;
        public final String waveNo;
        public final List<Pack> packs = new ArrayList<>();
        public int status;
        /** 称重结果（kg），未称重为 null */
        public Double weightKg;

        Delivery(String deliveryNo, String sourceNo, String expressNo, String customerCode, String customerName,
                 String logisticsChannel, String waveNo) {
            this.deliveryNo = deliveryNo;
            this.sourceNo = sourceNo;
            this.expressNo = expressNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.logisticsChannel = logisticsChannel;
            this.waveNo = waveNo;
        }

        public int skuNum() {
            return packs.stream().mapToInt(p -> p.items.stream().mapToInt(i -> i.qty).sum()).sum();
        }

        public int scanNum() {
            return packs.stream().mapToInt(p -> p.items.stream().mapToInt(i -> i.scanQty).sum()).sum();
        }
    }

    private final Map<String, Wave> waves = new LinkedHashMap<>();
    private final Map<String, Delivery> deliveries = new LinkedHashMap<>();

    public OutboundSampleStore() {
        reset();
    }

    /** 恢复初始示例数据（测试与“重置演示”用）。 */
    public synchronized void reset() {
        waves.clear();
        deliveries.clear();
        // 波次 1：单品拣货，3 票一件一单
        Wave w1 = wave("WV26110001", "C001", "示例客户1", "CART-01", 1, "2026-11-24 08:30:00");
        String[][] p1 = {{"SKU-0001", "示例收纳盒 1", "BC00010001", "A-01-01"}, {"SKU-0002", "示例数据线 2", "BC00010002", "A-01-02"},
                {"SKU-0003", "示例T恤 3", "BC00010003", "A-02-01"}};
        for (int i = 0; i < p1.length; i++) {
            w1.lines.add(new PickLine(p1[i][0], p1[i][1], p1[i][2], p1[i][3], 1));
            Delivery d = delivery(String.format("OB2611%06d", 101 + i), w1, "示例快递A", String.format("TRK%010d", 101 + i));
            Pack pack = new Pack(d.deliveryNo + "-1", 1);
            pack.items.add(new PackItem(p1[i][0], p1[i][2], p1[i][1], 1));
            d.packs.add(pack);
        }
        // 波次 2：多品拣货，2 票一票多件
        Wave w2 = wave("WV26110002", "C002", "示例客户2", "CART-02", 2, "2026-11-24 09:10:00");
        w2.lines.add(new PickLine("SKU-0004", "示例水壶 4", "BC00010004", "B-01-01", 3));
        w2.lines.add(new PickLine("SKU-0005", "示例台灯 5", "BC00010005", "B-01-03", 2));
        Delivery d21 = delivery("OB2611000201", w2, "示例快递B", "TRK0000000201");
        Pack p21 = new Pack("OB2611000201-1", 1);
        p21.items.add(new PackItem("SKU-0004", "BC00010004", "示例水壶 4", 2));
        p21.items.add(new PackItem("SKU-0005", "BC00010005", "示例台灯 5", 1));
        d21.packs.add(p21);
        Delivery d22 = delivery("OB2611000202", w2, "示例快递B", "TRK0000000202");
        Pack p22 = new Pack("OB2611000202-1", 1);
        p22.items.add(new PackItem("SKU-0004", "BC00010004", "示例水壶 4", 1));
        p22.items.add(new PackItem("SKU-0005", "BC00010005", "示例台灯 5", 1));
        d22.packs.add(p22);
        // 波次 3：已拣完，出库单待复核
        Wave w3 = wave("WV26110003", "C003", "示例客户3", "CART-03", 1, "2026-11-23 16:00:00");
        PickLine l3 = new PickLine("SKU-0006", "示例手机壳 6", "BC00010006", "C-03-02", 2);
        l3.pickQty = 2;
        w3.lines.add(l3);
        w3.pickStatus = 2;
        for (int i = 0; i < 2; i++) {
            Delivery d = delivery(String.format("OB2611%06d", 301 + i), w3, "示例邮政", String.format("TRK%010d", 301 + i));
            d.status = 10;
            Pack pack = new Pack(d.deliveryNo + "-1", 1);
            pack.items.add(new PackItem("SKU-0006", "BC00010006", "示例手机壳 6", 1));
            d.packs.add(pack);
        }
        // 波次 4：多品拣货、已拣完，供网页端二次分拣 / 复核 / 按箱复核 / 称重演示；401 有两个包裹
        Wave w4 = wave("WV26110004", "C004", "示例客户4", "CART-04", 2, "2026-11-24 07:40:00");
        PickLine l41 = new PickLine("SKU-0008", "示例笔记本 8", "BC00010008", "D-01-01", 3);
        PickLine l42 = new PickLine("SKU-0009", "示例马克杯 9", "BC00010009", "D-01-02", 2);
        l41.pickQty = 3;
        l42.pickQty = 2;
        w4.lines.add(l41);
        w4.lines.add(l42);
        w4.pickStatus = 2;
        Delivery d41 = delivery("OB2611000401", w4, "示例快递A", "TRK0000000401");
        d41.status = 10;
        Pack p411 = new Pack("OB2611000401-1", 1);
        p411.items.add(new PackItem("SKU-0008", "BC00010008", "示例笔记本 8", 2));
        Pack p412 = new Pack("OB2611000401-2", 2);
        p412.items.add(new PackItem("SKU-0009", "BC00010009", "示例马克杯 9", 1));
        d41.packs.add(p411);
        d41.packs.add(p412);
        Delivery d42 = delivery("OB2611000402", w4, "示例快递A", "TRK0000000402");
        d42.status = 10;
        Pack p42 = new Pack("OB2611000402-1", 1);
        p42.items.add(new PackItem("SKU-0008", "BC00010008", "示例笔记本 8", 1));
        p42.items.add(new PackItem("SKU-0009", "BC00010009", "示例马克杯 9", 1));
        d42.packs.add(p42);
    }

    private Wave wave(String no, String cc, String cn, String cart, int pickWay, String time) {
        Wave w = new Wave(no, cc, cn, cart, pickWay, time);
        waves.put(no, w);
        return w;
    }

    private Delivery delivery(String no, Wave w, String channel, String expressNo) {
        Delivery d = new Delivery(no, "SO" + no.substring(2), expressNo, w.customerCode, w.customerName, channel, w.waveNo);
        deliveries.put(no, d);
        w.deliveryNos.add(no);
        return d;
    }

    // ---------- 查询 ----------

    public synchronized List<Wave> waves() {
        return new ArrayList<>(waves.values());
    }

    public synchronized Wave wave(String waveNo) {
        return Optional.ofNullable(waves.get(waveNo)).orElseThrow(() -> new BizException("波次不存在：" + waveNo));
    }

    public synchronized Delivery delivery(String deliveryNo) {
        return Optional.ofNullable(deliveries.get(deliveryNo)).orElseThrow(() -> new BizException("出库单不存在：" + deliveryNo));
    }

    /** 按出库单号或快递单号找出库单。 */
    public synchronized Delivery findDelivery(String deliveryNo, String expressNo) {
        if (deliveryNo != null && !deliveryNo.isBlank()) {
            return delivery(deliveryNo.trim());
        }
        return deliveries.values().stream().filter(d -> d.expressNo.equals(expressNo)).findFirst()
                .orElseThrow(() -> new BizException("未找到快递单号对应的出库单：" + expressNo));
    }

    // ---------- 拣货 ----------

    public synchronized PickLine line(Wave w, String productSku, String cellNo) {
        return w.lines.stream()
                .filter(l -> l.productSku.equals(productSku) && (cellNo == null || cellNo.isBlank() || l.cellNo.equals(cellNo)))
                .findFirst().orElseThrow(() -> new BizException("波次 " + w.waveNo + " 中没有该 SKU/库位的拣货任务"));
    }

    public synchronized Wave pick(String waveNo, String productSku, String cellNo, int qty) {
        Wave w = wave(waveNo);
        if (w.pickStatus == 2) {
            throw new BizException("波次已拣货完成");
        }
        PickLine l = line(w, productSku, cellNo);
        if (qty <= 0 || l.pickQty + qty > l.planQty) {
            throw new BizException("拣货数量超出待拣数量，剩余 " + (l.planQty - l.pickQty));
        }
        l.pickQty += qty;
        boolean done = w.lines.stream().allMatch(x -> x.pickQty == x.planQty);
        w.pickStatus = done ? 2 : 1;
        if (done) {
            w.deliveryNos.forEach(no -> deliveries.get(no).status = 10);
        }
        return w;
    }

    // ---------- 复核 ----------

    /**
     * 复核一个包裹（packNo 为空时复核整单）。oneClickPass 直接视为全部扫描完成。
     * @return 该出库单是否已全部复核
     */
    public synchronized boolean review(String deliveryNo, String packNo, boolean oneClickPass, List<ReviewItem> items) {
        Delivery d = delivery(deliveryNo);
        if (d.status == 0) {
            throw new BizException("所在波次尚未拣货完成，不能复核");
        }
        if (d.status >= 20) {
            throw new BizException("出库单已复核");
        }
        List<Pack> targets = d.packs.stream()
                .filter(p -> packNo == null || packNo.isBlank() || p.packNo.equals(packNo)).toList();
        if (targets.isEmpty()) {
            throw new BizException("包裹不存在：" + packNo);
        }
        for (Pack p : targets) {
            if (oneClickPass) {
                p.items.forEach(i -> i.scanQty = i.qty);
            } else if (items != null) {
                for (ReviewItem r : items) {
                    PackItem it = p.items.stream().filter(i -> i.barcode.equals(r.barcode())).findFirst()
                            .orElseThrow(() -> new BizException("条码不属于该包裹：" + r.barcode()));
                    if (it.scanQty + r.qty() > it.qty) {
                        throw new BizException("条码 " + r.barcode() + " 扫描数量超出应发数量");
                    }
                    it.scanQty += r.qty();
                }
            }
            if (p.items.stream().allMatch(i -> i.scanQty == i.qty)) {
                p.reviewStatus = 1;
            }
        }
        boolean all = d.packs.stream().allMatch(p -> p.reviewStatus == 1);
        if (all) {
            d.status = 20;
        }
        return all;
    }

    public synchronized Delivery confirmDelivery(String deliveryNo) {
        Delivery d = delivery(deliveryNo);
        if (d.status != 20) {
            throw new BizException("出库单当前状态为" + DELIVERY_STATUS.get(d.status) + "，不能确认出库");
        }
        d.status = 30;
        return d;
    }

    // ---------- 网页端作业（批次 3） ----------

    /** 按出库单号或快递单号查找。 */
    public synchronized Delivery lookup(String no) {
        String key = no == null ? "" : no.trim();
        Delivery d = deliveries.get(key);
        if (d != null) {
            return d;
        }
        return deliveries.values().stream().filter(x -> x.expressNo.equals(key)).findFirst()
                .orElseThrow(() -> new BizException("未找到出库单：" + no));
    }

    /** 二次分拣：在多品波次中扫一个条码，分到第一个还缺这个条码的出库单格口。返回 [格口号(从1起), 出库单]。 */
    public synchronized Object[] sortScan(String waveNo, String barcode) {
        Wave w = wave(waveNo);
        if (w.pickWay != 2) {
            throw new BizException("只有多品波次需要二次分拣");
        }
        if (w.pickStatus != 2) {
            throw new BizException("波次尚未拣货完成，不能分拣");
        }
        for (int i = 0; i < w.deliveryNos.size(); i++) {
            Delivery d = deliveries.get(w.deliveryNos.get(i));
            for (Pack p : d.packs) {
                for (PackItem it : p.items) {
                    if (it.barcode.equals(barcode) && it.sortQty < it.qty) {
                        it.sortQty++;
                        return new Object[] {i + 1, d};
                    }
                }
            }
        }
        throw new BizException("条码不属于该波次，或该条码已全部分拣：" + barcode);
    }

    /** 按箱复核：扫包裹号，整箱视为复核通过。返回所属出库单。 */
    public synchronized Delivery reviewPack(String packNo) {
        String key = packNo == null ? "" : packNo.trim();
        Delivery d = deliveries.values().stream().filter(x -> x.packs.stream().anyMatch(p -> p.packNo.equals(key))).findFirst()
                .orElseThrow(() -> new BizException("包裹不存在：" + packNo));
        Pack p = d.packs.stream().filter(x -> x.packNo.equals(key)).findFirst().orElseThrow();
        if (p.reviewStatus == 1) {
            throw new BizException("该包裹已复核");
        }
        review(d.deliveryNo, key, true, null);
        return d;
    }

    /** 称重出库：要求已复核；记录重量后确认出库。 */
    public synchronized Delivery weighAndShip(String no, double weightKg) {
        Delivery d = lookup(no);
        if (weightKg <= 0 || weightKg > 70) {
            throw new BizException("重量需在 0~70 kg 之间（本地演示限制）");
        }
        if (d.status != 20) {
            throw new BizException("出库单当前状态为" + DELIVERY_STATUS.get(d.status) + "，需复核完成后才能称重出库");
        }
        d.weightKg = weightKg;
        return confirmDelivery(d.deliveryNo);
    }
}
