package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 大货（按箱）出库的内存示例数据：按箱拣货波次（/pda/wave/box*、directPage、bulk/pickWay…）→
 * 大货复核（/pda/delivery/review/**）→ 出库确认（/pda/delivery/*confirmDelivery）。重启或 reset 后恢复。
 * 数据虚构，规则与编码是本项目假设：
 * <ul>
 *   <li>波次 pickStatus：0 待拣货 / 1 拣货中 / 2 已完成；pickWay 固定为 3（按箱拣货，占位）</li>
 *   <li>大货单 status：0 待拣货 / 10 待复核 / 20 已复核 / 30 已出库</li>
 *   <li>按箱拣货会扣减 {@link StockStore} 中对应库位的按箱库存</li>
 *   <li>复核要求每个箱型的实出箱数等于应出箱数</li>
 * </ul>
 */
@Service
public class FreightStore {

    public static final Map<Integer, String> ORDER_STATUS = Map.of(0, "待拣货", 10, "待复核", 20, "已复核", 30, "已出库");

    /** 波次中一个库位上一个箱型的拣货任务。 */
    public static class BoxLine {
        public final String cellNo;
        public final String boxType;
        public final int planQty;
        public int pickQty;

        BoxLine(String cellNo, String boxType, int planQty) {
            this.cellNo = cellNo;
            this.boxType = boxType;
            this.planQty = planQty;
        }
    }

    public static class FreightOrder {
        public final String deliveryNo;
        public final String sourceNo;
        public final String customerCode;
        public final String customerName;
        public final String logisticsChannelName;
        public final String expectedTime;
        /** 箱型 → 应出箱数 */
        public final Map<String, Integer> boxes = new LinkedHashMap<>();
        public int status;
        public Integer pallets;

        FreightOrder(String deliveryNo, String customerCode, String customerName, String channel, String expectedTime) {
            this.deliveryNo = deliveryNo;
            this.sourceNo = "SO" + deliveryNo.substring(2);
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.logisticsChannelName = channel;
            this.expectedTime = expectedTime;
        }

        public int boxQty() {
            return boxes.values().stream().mapToInt(Integer::intValue).sum();
        }
    }

    public static class BoxWave {
        public final String waveNo;
        public final String customerCode;
        public final String customerName;
        public final List<BoxLine> lines = new ArrayList<>();
        public final List<String> deliveryNos = new ArrayList<>();
        public int pickStatus;

        BoxWave(String waveNo, String customerCode, String customerName) {
            this.waveNo = waveNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
        }

        public int planQty() {
            return lines.stream().mapToInt(l -> l.planQty).sum();
        }

        public int pickQty() {
            return lines.stream().mapToInt(l -> l.pickQty).sum();
        }
    }

    private final StockStore stock;
    private final Map<String, BoxWave> waves = new LinkedHashMap<>();
    private final Map<String, FreightOrder> orders = new LinkedHashMap<>();

    public FreightStore(StockStore stock) {
        this.stock = stock;
        reset();
    }

    public synchronized void reset() {
        waves.clear();
        orders.clear();
        BoxWave w = new BoxWave("BW26110001", "C005", "示例客户5");
        w.lines.add(new BoxLine("F-01-01", "BOX-0010-A", 5));
        w.lines.add(new BoxLine("F-01-02", "BOX-0010-A", 3));
        w.lines.add(new BoxLine("F-02-01", "BOX-0011-A", 3));
        FreightOrder o1 = order("FO2611000001", "示例卡派A", "2026-11-25 10:00:00");
        o1.boxes.put("BOX-0010-A", 5);
        o1.boxes.put("BOX-0011-A", 3);
        FreightOrder o2 = order("FO2611000002", "示例卡派A", "2026-11-25 14:00:00");
        o2.boxes.put("BOX-0010-A", 3);
        w.deliveryNos.addAll(List.of(o1.deliveryNo, o2.deliveryNo));
        waves.put(w.waveNo, w);
    }

    private FreightOrder order(String no, String channel, String expected) {
        FreightOrder o = new FreightOrder(no, "C005", "示例客户5", channel, expected);
        orders.put(no, o);
        return o;
    }

    // ---------- 查询 ----------

    public synchronized BoxWave wave(String waveNo) {
        return Optional.ofNullable(waves.get(waveNo == null ? "" : waveNo.trim()))
                .orElseThrow(() -> new BizException("按箱波次不存在：" + waveNo));
    }

    /** 按波次号或大货单号找波次。 */
    public synchronized BoxWave waveByBill(String billNo) {
        String b = billNo == null ? "" : billNo.trim();
        if (waves.containsKey(b)) {
            return waves.get(b);
        }
        return waves.values().stream().filter(w -> w.deliveryNos.contains(b)).findFirst()
                .orElseThrow(() -> new BizException("未找到单号对应的按箱波次：" + billNo));
    }

    public synchronized FreightOrder order(String deliveryNo) {
        return Optional.ofNullable(orders.get(deliveryNo == null ? "" : deliveryNo.trim()))
                .orElseThrow(() -> new BizException("大货单不存在：" + deliveryNo));
    }

    /** 按大货单号或来源单号找。 */
    public synchronized FreightOrder lookup(String paramNo) {
        String p = paramNo == null ? "" : paramNo.trim();
        if (orders.containsKey(p)) {
            return orders.get(p);
        }
        return orders.values().stream().filter(o -> o.sourceNo.equals(p)).findFirst()
                .orElseThrow(() -> new BizException("未找到大货单：" + paramNo));
    }

    public synchronized List<FreightOrder> orders() {
        return new ArrayList<>(orders.values());
    }

    public synchronized List<BoxLine> linesAt(BoxWave w, String cellNo) {
        List<BoxLine> ls = w.lines.stream().filter(l -> l.cellNo.equalsIgnoreCase(cellNo == null ? "" : cellNo.trim())).toList();
        if (ls.isEmpty()) {
            throw new BizException("库位不在波次 " + w.waveNo + " 的拣货任务中：" + cellNo);
        }
        return ls;
    }

    // ---------- 按箱拣货 ----------

    public synchronized BoxWave pick(String waveNo, String cellNo, String boxType, int qty) {
        BoxWave w = wave(waveNo);
        if (w.pickStatus == 2) {
            throw new BizException("波次已拣货完成");
        }
        BoxLine l = linesAt(w, cellNo).stream().filter(x -> x.boxType.equals(boxType)).findFirst()
                .orElseThrow(() -> new BizException("库位 " + cellNo + " 没有箱型 " + boxType + " 的拣货任务"));
        if (qty <= 0 || l.pickQty + qty > l.planQty) {
            throw new BizException("拣货箱数超出待拣数量，剩余 " + (l.planQty - l.pickQty));
        }
        stock.deduct(l.cellNo, stock.boxType(boxType).sku(), boxType, 1, qty);
        l.pickQty += qty;
        boolean done = w.lines.stream().allMatch(x -> x.pickQty == x.planQty);
        w.pickStatus = done ? 2 : 1;
        if (done) {
            w.deliveryNos.forEach(no -> orders.get(no).status = 10);
        }
        return w;
    }

    // ---------- 复核 / 出库 ----------

    /** 大货复核：realCounts 为 箱型 → 实出箱数，必须与应出一致。 */
    public synchronized FreightOrder review(String deliveryNo, Map<String, Integer> realCounts) {
        FreightOrder o = order(deliveryNo);
        if (o.status == 0) {
            throw new BizException("大货单尚未拣货完成，不能复核");
        }
        if (o.status >= 20) {
            throw new BizException("大货单已复核");
        }
        for (Map.Entry<String, Integer> e : o.boxes.entrySet()) {
            int real = realCounts.getOrDefault(e.getKey(), 0);
            if (real != e.getValue()) {
                throw new BizException("箱型 " + e.getKey() + " 实出 " + real + " 箱，应出 " + e.getValue() + " 箱");
            }
        }
        for (String bt : realCounts.keySet()) {
            if (!o.boxes.containsKey(bt)) {
                throw new BizException("箱型不属于该大货单：" + bt);
            }
        }
        o.status = 20;
        return o;
    }

    public synchronized FreightOrder confirm(String deliveryNo, Integer pallets) {
        FreightOrder o = order(deliveryNo);
        if (o.status != 20) {
            throw new BizException("大货单 " + deliveryNo + " 当前状态为" + ORDER_STATUS.get(o.status) + "，不能确认出库");
        }
        o.status = 30;
        o.pallets = pallets;
        return o;
    }

    public synchronized boolean has(String deliveryNo) {
        return orders.containsKey(deliveryNo == null ? "" : deliveryNo.trim());
    }
}
