package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 上架单（/pda/shelf/** 新接口）的内存数据。上架单在 PDA 收货时由 {@link ReceiptStore} 生成，上架会写入 {@link StockStore}。
 * 数据虚构，规则与编码是本项目假设：
 * <ul>
 *   <li>putawayNo = "PA" + 入库单号去掉前两位</li>
 *   <li>putawayType：1 按 SKU 上架（按箱预报）/ 2 按箱型上架 / 3 整托上架</li>
 *   <li>上架单 status：0 待上架 / 1 上架中 / 2 已完成；箱 status：0 待上架 / 1 已上架</li>
 *   <li>按 SKU 上架只累计数量；整箱上架要求箱内每个 SKU 的待上架数都够，避免同一批货被上架两次</li>
 * </ul>
 */
@Service
public class PutawayStore {

    public static class SkuLine {
        public int receiptQty;
        public int putawayQty;

        public int pending() {
            return receiptQty - putawayQty;
        }
    }

    public static class Box {
        public final String boxBarcode;
        public final Map<String, Integer> skus = new LinkedHashMap<>();
        public String palletCode;
        public boolean shelved;

        Box(String boxBarcode) {
            this.boxBarcode = boxBarcode;
        }
    }

    public static class BoxTypeLine {
        public int receiptQty;
        public int putawayQty;

        public int pending() {
            return receiptQty - putawayQty;
        }
    }

    public static class Pallet {
        public final String palletCode;
        public final List<String> boxBarcodes = new ArrayList<>();
        public final Map<String, Integer> boxTypes = new LinkedHashMap<>();
        /** 按 SKU 收货时放在托盘上的散件 */
        public final Map<String, Integer> skus = new LinkedHashMap<>();
        public String shelfCellNo;

        Pallet(String palletCode) {
            this.palletCode = palletCode;
        }
    }

    public static class Order {
        public final String putawayNo;
        public final String asnNo;
        public final String sourceNo;
        public final String customerCode;
        public final String customerName;
        public final int forecastType;
        public final int inboundType;
        public final Map<String, SkuLine> skus = new LinkedHashMap<>();
        public final Map<String, Box> boxes = new LinkedHashMap<>();
        public final Map<String, BoxTypeLine> boxTypes = new LinkedHashMap<>();
        public final Map<String, Pallet> pallets = new LinkedHashMap<>();

        Order(String putawayNo, String asnNo, String sourceNo, String customerCode, String customerName, int forecastType, int inboundType) {
            this.putawayNo = putawayNo;
            this.asnNo = asnNo;
            this.sourceNo = sourceNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.forecastType = forecastType;
            this.inboundType = inboundType;
        }

        public int putawayType() {
            if (!pallets.isEmpty()) {
                return 3;
            }
            return forecastType == 2 ? 2 : 1;
        }

        public int pendingSkuQty() {
            return skus.values().stream().mapToInt(SkuLine::pending).sum();
        }

        public int pendingBoxQty() {
            return boxTypes.values().stream().mapToInt(BoxTypeLine::pending).sum() + (int) boxes.values().stream().filter(b -> !b.shelved).count();
        }

        public int status() {
            int pendingAll = pendingSkuQty() + boxTypes.values().stream().mapToInt(BoxTypeLine::pending).sum();
            int doneAll = skus.values().stream().mapToInt(l -> l.putawayQty).sum() + boxTypes.values().stream().mapToInt(l -> l.putawayQty).sum();
            if (pendingAll == 0) {
                return 2;
            }
            return doneAll > 0 ? 1 : 0;
        }
    }

    private final StockStore stock;
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public PutawayStore(StockStore stock) {
        this.stock = stock;
    }

    public synchronized void reset() {
        orders.clear();
    }

    // ---------- 由收货调用 ----------

    synchronized Order orderFor(ReceiptStore.Asn a) {
        String no = "PA" + a.asnNo.substring(2);
        return orders.computeIfAbsent(no, k -> new Order(k, a.asnNo, a.sourceNo, a.customerCode, a.customerName, a.forecastType, a.inboundType));
    }

    synchronized void addBox(ReceiptStore.Asn a, String boxBarcode, Map<String, Integer> skus, String palletCode) {
        Order o = orderFor(a);
        Box b = new Box(boxBarcode);
        skus.forEach((sku, qty) -> {
            if (qty > 0) {
                b.skus.put(sku, qty);
                o.skus.computeIfAbsent(sku, k -> new SkuLine()).receiptQty += qty;
            }
        });
        b.palletCode = palletCode;
        o.boxes.put(boxBarcode, b);
        if (palletCode != null) {
            o.pallets.computeIfAbsent(palletCode, Pallet::new).boxBarcodes.add(boxBarcode);
        }
    }

    synchronized void addBoxType(ReceiptStore.Asn a, String boxType, int qty, String palletCode) {
        Order o = orderFor(a);
        o.boxTypes.computeIfAbsent(boxType, k -> new BoxTypeLine()).receiptQty += qty;
        if (palletCode != null) {
            o.pallets.computeIfAbsent(palletCode, Pallet::new).boxTypes.merge(boxType, qty, Integer::sum);
        }
    }

    synchronized void addSkus(ReceiptStore.Asn a, Map<String, Integer> qty, String palletCode) {
        Order o = orderFor(a);
        qty.forEach((sku, q) -> o.skus.computeIfAbsent(sku, k -> new SkuLine()).receiptQty += q);
        if (palletCode != null) {
            Pallet p = o.pallets.computeIfAbsent(palletCode, Pallet::new);
            qty.forEach((sku, q) -> p.skus.merge(sku, q, Integer::sum));
        }
    }

    /** 托盘是否已绑定在未上架完的上架单上。 */
    public synchronized boolean palletInUse(String palletCode) {
        return orders.values().stream().anyMatch(o -> {
            Pallet p = o.pallets.get(palletCode);
            return p != null && p.shelfCellNo == null;
        });
    }

    // ---------- 查询 ----------

    public synchronized List<Order> list() {
        return new ArrayList<>(orders.values());
    }

    public synchronized Order get(String putawayNo) {
        return Optional.ofNullable(orders.get(putawayNo == null ? "" : putawayNo.trim()))
                .orElseThrow(() -> new BizException("上架单不存在：" + putawayNo));
    }

    /** 按上架单号 / 入库单号 / 来源单号 / 箱唛 / 托盘号找上架单。 */
    public synchronized List<Order> search(String content) {
        String c = content == null ? "" : content.trim();
        return orders.values().stream().filter(o -> o.putawayNo.equals(c) || o.asnNo.equals(c) || o.sourceNo.equals(c)
                || o.boxes.containsKey(c) || o.pallets.containsKey(c) || o.boxTypes.containsKey(c)).toList();
    }

    public synchronized Order byPallet(String palletCode) {
        return orders.values().stream().filter(o -> o.pallets.containsKey(palletCode == null ? "" : palletCode.trim())).findFirst()
                .orElseThrow(() -> new BizException("托盘未绑定上架单：" + palletCode));
    }

    /** 推荐库位：已有该 SKU 库存的库位优先，否则第一个空库位。 */
    public synchronized String recommendCell(String sku) {
        List<String> holding = stock.cellsHolding(sku);
        if (!holding.isEmpty()) {
            return holding.get(0);
        }
        List<String> empty = stock.emptyCells();
        return empty.isEmpty() ? "" : empty.get(0);
    }

    // ---------- 上架 ----------

    public synchronized void putSku(Order o, String sku, int qty, String cellNo) {
        SkuLine l = o.skus.get(sku);
        if (l == null) {
            throw new BizException("SKU 不在上架单 " + o.putawayNo + " 中：" + sku);
        }
        if (qty <= 0 || qty > l.pending()) {
            throw new BizException("上架数量超出待上架数量，剩余 " + l.pending());
        }
        stock.putIn(cellNo, sku, "", 1, qty);
        l.putawayQty += qty;
    }

    public synchronized void putBox(Order o, String boxBarcode, String cellNo) {
        Box b = o.boxes.get(boxBarcode);
        if (b == null) {
            throw new BizException("箱唛不在上架单 " + o.putawayNo + " 中：" + boxBarcode);
        }
        if (b.shelved) {
            throw new BizException("该箱已上架：" + boxBarcode);
        }
        for (Map.Entry<String, Integer> e : b.skus.entrySet()) {
            if (o.skus.get(e.getKey()).pending() < e.getValue()) {
                throw new BizException("箱 " + boxBarcode + " 中 " + e.getKey() + " 已部分按 SKU 上架，不能整箱上架");
            }
        }
        stock.requireCell(cellNo, true);
        b.skus.forEach((sku, qty) -> {
            stock.putIn(cellNo, sku, "", 1, qty);
            o.skus.get(sku).putawayQty += qty;
        });
        b.shelved = true;
    }

    public synchronized void putBoxType(Order o, String boxType, int qty, String cellNo) {
        BoxTypeLine l = o.boxTypes.get(boxType);
        if (l == null) {
            throw new BizException("箱型不在上架单 " + o.putawayNo + " 中：" + boxType);
        }
        if (qty <= 0 || qty > l.pending()) {
            throw new BizException("上架箱数超出待上架数量，剩余 " + l.pending());
        }
        stock.putIn(cellNo, stock.boxType(boxType).sku(), boxType, 1, qty);
        l.putawayQty += qty;
    }

    public synchronized void putPallet(Order o, String palletCode, String cellNo) {
        Pallet p = o.pallets.get(palletCode);
        if (p == null) {
            throw new BizException("托盘不在上架单 " + o.putawayNo + " 中：" + palletCode);
        }
        if (p.shelfCellNo != null) {
            throw new BizException("托盘已上架到 " + p.shelfCellNo);
        }
        stock.requireCell(cellNo, true);
        for (String bb : p.boxBarcodes) {
            putBox(o, bb, cellNo);
        }
        p.boxTypes.forEach((bt, qty) -> putBoxType(o, bt, qty, cellNo));
        p.skus.forEach((sku, qty) -> putSku(o, sku, qty, cellNo));
        p.shelfCellNo = cellNo.trim();
    }

    /** 一键完成：剩余全部上架到同一个库位。 */
    public synchronized void completeAll(Order o, String cellNo) {
        stock.requireCell(cellNo, true);
        if (o.status() == 2) {
            throw new BizException("上架单已全部上架");
        }
        for (Box b : o.boxes.values()) {
            if (!b.shelved && b.skus.entrySet().stream().allMatch(e -> o.skus.get(e.getKey()).pending() >= e.getValue())) {
                putBox(o, b.boxBarcode, cellNo);
            }
        }
        o.skus.forEach((sku, l) -> {
            if (l.pending() > 0) {
                putSku(o, sku, l.pending(), cellNo);
            }
        });
        o.boxTypes.forEach((bt, l) -> {
            if (l.pending() > 0) {
                putBoxType(o, bt, l.pending(), cellNo);
            }
        });
        o.boxes.values().forEach(b -> b.shelved = true);
        o.pallets.values().forEach(p -> {
            if (p.shelfCellNo == null) {
                p.shelfCellNo = cellNo.trim();
            }
        });
    }
}
