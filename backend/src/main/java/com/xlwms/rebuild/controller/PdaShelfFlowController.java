package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.shelves.BoxShelvesReq;
import com.xlwms.rebuild.dto.shelves.SkuShelvesReq;
import com.xlwms.rebuild.service.PutawayStore;
import com.xlwms.rebuild.service.PutawayStore.Box;
import com.xlwms.rebuild.service.PutawayStore.Order;
import com.xlwms.rebuild.service.PutawayStore.Pallet;
import com.xlwms.rebuild.service.PutawayStore.SkuLine;
import com.xlwms.rebuild.service.ReceiptStore;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.BoxType;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 上架：/gateway/wms/pda/shelf/ 下全部 23 个接口与 /gateway/wms/shelf/selectCell。路径、参数名、请求体与响应字段取自 APK；
 * 规则见 {@link PutawayStore}，上架写入 {@link StockStore}。v2/confirm 与 v2/box/submitShelf 的请求体沿用 dto/shelves 下批次 1 的类。
 * shelfType、putAwayType 等编码为本项目假设。数据虚构，不连生产。
 */
@RestController
public class PdaShelfFlowController {

    public record CommitCompleteOrderReq(String cellNo, String customerCode, Integer inboundType, String putawayNo, String whCode) {
    }

    public record TrfShelvesItem(int boxQty, String boxType, String cellNo) {
    }

    /** TrfShelvesReq */
    public record TrfShelvesReq(List<TrfShelvesItem> boxTypeList, String customerCode, String putawayNo, String whCode) {
    }

    public record PalletShelfScanReq(String palletCode, String whCode) {
    }

    public record PalletCell(String palletCode, String shelfCellNo) {
    }

    public record PreCheckPalletListReq(String customerCode, Integer inboundType, List<PalletCell> palletList, String putawayNo, String sourceNo,
                                        String whCode) {
    }

    public record PalletShelfSubmitReq(String customerCode, String palletCode, String putawayNo, String shelfCellNo, String sourceNo, String whCode) {
    }

    public record BatchPutawayScanBoxReq(List<String> barcodes, String whCode) {
    }

    public record BatchPutawayScanCellReq(String cellNo, String whCode) {
    }

    public record BatchPutawaySkuQueryReq(List<String> barcodes, String customerCode, Integer inboundType, String putawayNo, String whCode) {
    }

    public record SubmitBox(String boxBarcode) {
    }

    public record SubmitBoxType(String boxType, int expectedPendingPutawayQty, int putawayQty) {
    }

    public record SubmitOrder(List<SubmitBox> boxList, List<SubmitBoxType> boxTypeList, String customerCode, Integer inboundType, String putawayNo,
                              String sourceNo) {
    }

    public record BatchPutawaySubmitReq(String cellNo, List<SubmitOrder> orderList, String whCode) {
    }

    private final PutawayStore putaway;
    private final StockStore stock;

    public PdaShelfFlowController(PutawayStore putaway, StockStore stock) {
        this.putaway = putaway;
        this.stock = stock;
    }

    // ---------- 视图 ----------

    /** ShelfvesWay：shelfWay 本项目假设与 putawayType 相同。 */
    private static Map<String, Object> way(Order o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asnNo", o.asnNo);
        m.put("customerCode", o.customerCode);
        m.put("customerName", o.customerName);
        m.put("deviceStatus", "");
        m.put("forecastType", o.forecastType);
        m.put("forecastTypeName", ReceiptStore.FORECAST_TYPE.get(o.forecastType));
        m.put("inboundType", o.inboundType);
        m.put("putawayNo", o.putawayNo);
        m.put("putawayType", o.putawayType());
        m.put("shelfWay", o.putawayType());
        m.put("sourceNo", o.sourceNo);
        m.put("status", o.status());
        m.put("whCode", StockStore.WH);
        return m;
    }

    private Map<String, Object> recommendCellBean(String sku) {
        List<String> holding = stock.cellsHolding(sku);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < holding.size(); i++) {
            String c = holding.get(i);
            list.add(Map.of("cellNo", c, "cellType", "货架位", "lastPutaway", i == 0, "productSku", sku,
                    "stkQty", String.valueOf(stock.find(c, null, sku, "", null).stream().mapToInt(r -> r.qty).sum())));
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("recommendCell", putaway.recommendCell(sku));
        m.put("recommendCellList", list);
        return m;
    }

    /** ShelvesScanBarcode.Sku / BarcodeCheck 共用的 SKU 行。 */
    private Map<String, Object> skuRow(String sku, SkuLine l) {
        Product p = stock.product(sku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("barcode", p.barcode());
        m.put("productName", p.name());
        m.put("productSku", sku);
        m.put("putawayQty", l.putawayQty);
        m.put("receiptQty", l.receiptQty);
        m.put("recommendCell", putaway.recommendCell(sku));
        m.put("stockBatchList", List.of());
        return m;
    }

    private static Map<String, Object> completeResp(Order o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("allBoxQty", o.boxes.size() + o.boxTypes.values().stream().mapToInt(l -> l.receiptQty).sum());
        m.put("allSkuQty", o.skus.values().stream().mapToInt(l -> l.receiptQty).sum());
        m.put("boxTypeQty", o.boxTypes.size());
        m.put("customerCode", o.customerCode);
        m.put("pendingBoxQty", o.pendingBoxQty());
        m.put("pendingSkuQty", o.pendingSkuQty());
        m.put("putawayNo", o.putawayNo);
        m.put("sourceNo", o.sourceNo);
        return m;
    }

    private static Map<String, Object> putAwayStatus(Order o) {
        return Map.of("deviceSpecialCell", false, "putAwayStatus", o.status() == 2 ? 1 : 0, "putawayNo", o.putawayNo, "quickOutbound", false);
    }

    private static List<Map<String, Object>> stockBatch(int qty) {
        return List.of(Map.of("expiryTime", "", "productionTime", "", "putawayQty", 0, "receiptQty", qty));
    }

    // ---------- 合并上架入口 ----------

    /** 待上架单分页。putAwayType 为空表示全部。 */
    @GetMapping("/gateway/wms/pda/shelf/merge/page")
    public R<Map<String, Object>> mergePage(@RequestParam(defaultValue = "1") int current, @RequestParam(required = false) Integer putAwayType,
                                            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String whCode) {
        List<Map<String, Object>> rows = putaway.list().stream().filter(o -> o.status() != 2)
                .filter(o -> putAwayType == null || o.putawayType() == putAwayType).map(PdaShelfFlowController::way).toList();
        return R.ok(PageUtil.page(rows, current, size));
    }

    /** 扫上架单号 / 入库单号 / 来源单号 / 箱唛 / 托盘号，返回匹配的上架单。 */
    @GetMapping("/gateway/wms/pda/shelf/merge/scanContent")
    public R<List<Map<String, Object>>> scanContent(@RequestParam String content, @RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(required = false) Integer putAwayType, @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(required = false) String whCode) {
        List<Map<String, Object>> out = putaway.search(content).stream().map(PdaShelfFlowController::way).toList();
        if (out.isEmpty()) {
            throw new BizException("没有找到对应的上架单：" + content);
        }
        return R.ok(out);
    }

    // ---------- 按 SKU 上架 ----------

    /** 扫产品条码（BarcodeCheck）。v1 与 v2 两个路径行为相同。 */
    @GetMapping({"/gateway/wms/pda/shelf/scanBarcode", "/gateway/wms/pda/shelf/v2/scanBarcode"})
    public R<Map<String, Object>> scanBarcode(@RequestParam String barcode, @RequestParam(required = false) String customerCode,
                                              @RequestParam String putawayNo, @RequestParam(required = false) String whCode) {
        Order o = putaway.get(putawayNo);
        Product p = stock.findProduct(barcode);
        if (p == null || !o.skus.containsKey(p.sku())) {
            throw new BizException("条码不属于上架单 " + o.putawayNo + "：" + barcode);
        }
        SkuLine l = o.skus.get(p.sku());
        Map<String, Object> m = skuRow(p.sku(), l);
        m.putAll(recommendCellBean(p.sku()));
        m.put("stockBatchList", stockBatch(l.receiptQty));
        return R.ok(m);
    }

    /** 校验上架库位。shelfType 本项目不区分。 */
    @GetMapping("/gateway/wms/pda/shelf/scanCell")
    public R<Object> scanCell(@RequestParam String cellNo, @RequestParam(required = false) String customerCode,
                              @RequestParam(required = false) String putawayNo, @RequestParam(required = false) String whCode,
                              @RequestParam(required = false) Integer shelfType) {
        stock.requireCell(cellNo, true);
        if (putawayNo != null && !putawayNo.isBlank()) {
            putaway.get(putawayNo);
        }
        return R.ok(null);
    }

    /** 先扫库位再看这张上架单还剩哪些 SKU（ShelvesScanBarcode）。 */
    @GetMapping("/gateway/wms/pda/shelf/v2/scanShelfByCellNo")
    public R<Map<String, Object>> scanShelfByCellNo(@RequestParam String putawayNo, @RequestParam String cellNo,
                                                    @RequestParam(required = false) String customerCode, @RequestParam(required = false) String whCode) {
        Order o = putaway.get(putawayNo);
        stock.requireCell(cellNo, true);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", o.customerCode);
        m.put("putAwayStatus", o.status() == 2 ? 1 : 0);
        m.put("putawayNo", o.putawayNo);
        m.put("putawayType", o.putawayType());
        List<Map<String, Object>> skus = new ArrayList<>();
        o.skus.forEach((sku, l) -> {
            if (l.pending() > 0) {
                skus.add(skuRow(sku, l));
            }
        });
        m.put("skuList", skus);
        m.put("sourceNo", o.sourceNo);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    /** 一键完成上架前的汇总（CompleteOrderResp）。 */
    @GetMapping("/gateway/wms/pda/shelf/onoPieceCompleteOrder")
    public R<Map<String, Object>> onePieceComplete(@RequestParam(required = false) String whCode, @RequestParam(required = false) String customerCode,
                                                   @RequestParam String putawayNo) {
        return R.ok(completeResp(putaway.get(putawayNo)));
    }

    @GetMapping("/gateway/wms/pda/shelf/crossDockCompleteOrder")
    public R<Map<String, Object>> crossDockComplete(@RequestParam(required = false) String whCode, @RequestParam(required = false) String customerCode,
                                                    @RequestParam String putawayNo) {
        Order o = putaway.get(putawayNo);
        if (o.inboundType != 2) {
            throw new BizException("上架单 " + o.putawayNo + " 不是中转入库");
        }
        return R.ok(completeResp(o));
    }

    /** 把上架单剩余的全部货上架到一个库位。 */
    @PostMapping("/gateway/wms/pda/shelf/commitCompleteOrder")
    public R<Object> commitCompleteOrder(@RequestBody CommitCompleteOrderReq req) {
        putaway.completeAll(putaway.get(req.putawayNo()), req.cellNo());
        return R.ok(null);
    }

    /**
     * 上架单的推荐库位与待上架明细（RecommendCellListBean：skuList / boxList 两组分页）。
     * areaTypes 本项目不区分。按箱型的行放在 boxList.records[0].boxTypeList 下。
     */
    @GetMapping("/gateway/wms/pda/shelf/v2/getRecommendCell/v2")
    public R<Map<String, Object>> getRecommendCell(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam String putawayNo, @RequestParam(required = false) String whCode,
                                                   @RequestParam(required = false) String customerCode, @RequestParam(required = false) String areaTypes) {
        Order o = putaway.get(putawayNo);
        List<Map<String, Object>> skus = new ArrayList<>();
        o.skus.forEach((sku, l) -> {
            if (l.pending() > 0) {
                Map<String, Object> m = skuRow(sku, l);
                m.put("recommendCellList", recommendCellBean(sku).get("recommendCellList"));
                m.remove("recommendCell");
                skus.add(m);
            }
        });
        List<Map<String, Object>> types = new ArrayList<>();
        o.boxTypes.forEach((bt, l) -> {
            if (l.pending() > 0) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("allBoxQty", l.receiptQty);
                m.put("boxType", bt);
                m.put("cellAreaType", "存储区");
                m.put("customizeBarcode", "");
                m.put("lastPutaway", !stock.cellsHolding(stock.boxType(bt).sku()).isEmpty());
                m.put("recommendCell", putaway.recommendCell(stock.boxType(bt).sku()));
                m.put("waitShelfBoxQty", l.pending());
                types.add(m);
            }
        });
        List<Map<String, Object>> boxGroups = types.isEmpty() ? List.of()
                : List.of(Map.of("boxTypeList", types, "inboundType", String.valueOf(o.inboundType)));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxList", PageUtil.page(boxGroups, current, size));
        m.put("customerCode", o.customerCode);
        m.put("putAwayStatus", o.status() == 2 ? 1 : 0);
        m.put("putawayNo", o.putawayNo);
        m.put("putawayType", o.putawayType());
        m.put("skuList", PageUtil.page(skus, current, size));
        m.put("sourceNo", o.sourceNo);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    /** 按 SKU 上架确认（PutAwayStatus）：同一 SKU 多行合计不得超过待上架数，库位全部有效，校验通过后统一上架。 */
    @PostMapping("/gateway/wms/pda/shelf/v2/confirm")
    public R<Map<String, Object>> confirm(@RequestBody SkuShelvesReq req) {
        Order o = putaway.get(req.getPutawayNo());
        if (req.getSkuList() == null || req.getSkuList().isEmpty()) {
            throw new BizException("skuList 不能为空");
        }
        Map<String, Integer> sum = new LinkedHashMap<>();
        for (SkuShelvesReq.SkuShelvesItem it : req.getSkuList()) {
            SkuLine l = o.skus.get(it.getProductSku());
            if (l == null) {
                throw new BizException("SKU 不在上架单 " + o.putawayNo + " 中：" + it.getProductSku());
            }
            stock.requireCell(it.getCellNo(), true);
            int total = sum.merge(it.getProductSku(), it.getPutawayQty(), Integer::sum);
            if (it.getPutawayQty() <= 0 || total > l.pending()) {
                throw new BizException("SKU " + it.getProductSku() + " 上架数量超出待上架数量，剩余 " + l.pending());
            }
        }
        for (SkuShelvesReq.SkuShelvesItem it : req.getSkuList()) {
            putaway.putSku(o, it.getProductSku(), it.getPutawayQty(), it.getCellNo());
        }
        return R.ok(putAwayStatus(o));
    }

    /** 整箱上架（PutAwayStatus）：每箱可以去不同库位；先校验全部箱子再统一上架。 */
    @PostMapping("/gateway/wms/pda/shelf/v2/box/submitShelf")
    public R<Map<String, Object>> submitShelf(@RequestBody BoxShelvesReq req) {
        Order o = putaway.get(req.getPutawayNo());
        if (req.getBoxBarcodeList() == null || req.getBoxBarcodeList().isEmpty()) {
            throw new BizException("boxBarcodeList 不能为空");
        }
        Map<String, Integer> need = new LinkedHashMap<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (BoxShelvesReq.BoxShelvesItem it : req.getBoxBarcodeList()) {
            Box b = o.boxes.get(it.getBoxBarcode());
            if (b == null || b.shelved || !seen.add(it.getBoxBarcode())) {
                throw new BizException("箱唛不存在、已上架或重复：" + it.getBoxBarcode());
            }
            stock.requireCell(it.getCellNo(), true);
            b.skus.forEach((sku, q) -> need.merge(sku, q, Integer::sum));
        }
        for (Map.Entry<String, Integer> e : need.entrySet()) {
            if (o.skus.get(e.getKey()).pending() < e.getValue()) {
                throw new BizException(e.getKey() + " 已部分按 SKU 上架，不能整箱上架");
            }
        }
        for (BoxShelvesReq.BoxShelvesItem it : req.getBoxBarcodeList()) {
            putaway.putBox(o, it.getBoxBarcode(), it.getCellNo());
        }
        return R.ok(putAwayStatus(o));
    }

    // ---------- 整箱上架 ----------

    @GetMapping("/gateway/wms/pda/shelf/box/listBoxBarCode")
    public R<Map<String, Object>> listBoxBarCode(@RequestParam String putawayNo, @RequestParam(required = false) String customerCode,
                                                 @RequestParam(required = false) String whCode) {
        Order o = putaway.get(putawayNo);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxItems", o.boxes.values().stream().map(b -> Map.of("boxBarcode", b.boxBarcode, "status", b.shelved ? 1 : 0)).toList());
        m.put("customerCode", o.customerCode);
        m.put("putawayNo", o.putawayNo);
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    @GetMapping("/gateway/wms/pda/shelf/v3/box/scanBoxBarcode")
    public R<List<Map<String, Object>>> scanBoxBarcodeV3(@RequestParam String boxBarcode, @RequestParam String putawayNo,
                                                         @RequestParam(required = false) String customerCode, @RequestParam(required = false) String whCode) {
        Order o = putaway.get(putawayNo);
        Box b = o.boxes.get(boxBarcode.trim());
        if (b == null) {
            throw new BizException("箱唛不在上架单 " + o.putawayNo + " 中：" + boxBarcode);
        }
        if (b.shelved) {
            throw new BizException("该箱已上架：" + boxBarcode);
        }
        List<Map<String, Object>> items = new ArrayList<>();
        b.skus.forEach((sku, qty) -> {
            Map<String, Object> i = new LinkedHashMap<>();
            i.put("expiryTime", "");
            i.put("productName", stock.product(sku).name());
            i.put("productSku", sku);
            i.put("productionTime", "");
            i.put("receiptQty", qty);
            i.put("stockBatchList", stockBatch(qty));
            items.add(i);
        });
        return R.ok(List.of(Map.of("boxBarcode", b.boxBarcode, "customizeBarcode", "", "skuItems", items)));
    }

    // ---------- 中转按箱型上架 ----------

    /** ScanBoxTypeBean */
    @GetMapping("/gateway/wms/pda/shelf/v2/crossDock/scanBoxType")
    public R<List<Map<String, Object>>> scanBoxType(@RequestParam String boxType, @RequestParam(required = false) String customerCode,
                                                    @RequestParam String putawayNo, @RequestParam(required = false) String whCode) {
        Order o = putaway.get(putawayNo);
        PutawayStore.BoxTypeLine l = o.boxTypes.get(boxType.trim());
        if (l == null) {
            throw new BizException("箱型不在上架单 " + o.putawayNo + " 中：" + boxType);
        }
        BoxType b = stock.boxType(boxType.trim());
        Product p = stock.product(b.sku());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxHeight", b.height());
        m.put("boxHeightBs", Math.round(b.height() / 2.54 * 100) / 100.0);
        m.put("boxLength", b.length());
        m.put("boxLengthBs", Math.round(b.length() / 2.54 * 100) / 100.0);
        m.put("boxQty", l.receiptQty);
        m.put("boxType", b.boxType());
        m.put("boxWeight", b.weight());
        m.put("boxWeightBs", Math.round(b.weight() * 2.20462 * 100) / 100.0);
        m.put("boxWeightUnit", "kg");
        m.put("boxWeightUnitBs", "lb");
        m.put("boxWidth", b.width());
        m.put("boxWidthBs", Math.round(b.width() / 2.54 * 100) / 100.0);
        m.put("currentNum", l.putawayQty);
        m.put("customizeBarcode", "");
        m.put("putawayNo", o.putawayNo);
        m.put("recommendCell", putaway.recommendCell(b.sku()));
        m.put("sizeUnit", "cm");
        m.put("sizeUnitBs", "in");
        m.put("skuList", List.of(Map.of("productName", p.name(), "productSku", p.sku(), "qty", b.pcsPerBox())));
        m.put("waitShelfBoxQty", l.pending());
        return R.ok(List.of(m));
    }

    /** 中转按箱型上架提交（PutAwayStatus）。先校验全部行，再统一上架。 */
    @PostMapping("/gateway/wms/pda/shelf/v2/crossDock/submitBoxType")
    public R<Map<String, Object>> submitBoxType(@RequestBody TrfShelvesReq req) {
        Order o = putaway.get(req.putawayNo());
        if (req.boxTypeList() == null || req.boxTypeList().isEmpty()) {
            throw new BizException("boxTypeList 不能为空");
        }
        Map<String, Integer> sum = new LinkedHashMap<>();
        for (TrfShelvesItem it : req.boxTypeList()) {
            PutawayStore.BoxTypeLine l = o.boxTypes.get(it.boxType());
            if (l == null) {
                throw new BizException("箱型不在上架单 " + o.putawayNo + " 中：" + it.boxType());
            }
            stock.requireCell(it.cellNo(), true);
            int total = sum.merge(it.boxType(), it.boxQty(), Integer::sum);
            if (it.boxQty() <= 0 || total > l.pending()) {
                throw new BizException("箱型 " + it.boxType() + " 上架箱数超出待上架数量，剩余 " + l.pending());
            }
        }
        for (TrfShelvesItem it : req.boxTypeList()) {
            putaway.putBoxType(o, it.boxType(), it.boxQty(), it.cellNo());
        }
        return R.ok(putAwayStatus(o));
    }

    // ---------- 整托上架 ----------

    @PostMapping("/gateway/wms/pda/shelf/scanPalletCode")
    public R<Map<String, Object>> scanPalletCode(@RequestBody PalletShelfScanReq req) {
        Order o = putaway.byPallet(req.palletCode());
        Pallet p = o.pallets.get(req.palletCode().trim());
        if (p.shelfCellNo != null) {
            throw new BizException("托盘已上架到 " + p.shelfCellNo);
        }
        String firstSku = p.boxBarcodes.isEmpty() ? stock.boxType(p.boxTypes.keySet().iterator().next()).sku()
                : o.boxes.get(p.boxBarcodes.get(0)).skus.keySet().iterator().next();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", o.customerCode);
        m.put("deviceStatus", "");
        m.put("forecastType", o.forecastType);
        m.put("forecastTypeName", ReceiptStore.FORECAST_TYPE.get(o.forecastType));
        m.put("inboundType", o.inboundType);
        m.put("putawayNo", o.putawayNo);
        m.put("recommendCell", putaway.recommendCell(firstSku));
        m.put("sourceNo", o.sourceNo);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    /** 批量整托上架前校验：返回有问题的托盘（bindPalletCode 为该库位上已上架的其他托盘）；空列表表示可以提交。 */
    @PostMapping("/gateway/wms/pda/shelf/preCheckPalletList")
    public R<List<Map<String, Object>>> preCheckPalletList(@RequestBody PreCheckPalletListReq req) {
        Order o = putaway.get(req.putawayNo());
        List<Map<String, Object>> problems = new ArrayList<>();
        for (PalletCell pc : req.palletList() == null ? List.<PalletCell>of() : req.palletList()) {
            if (!o.pallets.containsKey(pc.palletCode())) {
                throw new BizException("托盘不在上架单 " + o.putawayNo + " 中：" + pc.palletCode());
            }
            stock.requireCell(pc.shelfCellNo(), true);
            putaway.list().stream().flatMap(x -> x.pallets.values().stream())
                    .filter(x -> pc.shelfCellNo().equals(x.shelfCellNo) && !x.palletCode.equals(pc.palletCode()))
                    .findFirst()
                    .ifPresent(x -> problems.add(Map.of("bindPalletCode", x.palletCode, "palletCode", pc.palletCode(), "shelfCellNo", pc.shelfCellNo())));
        }
        return R.ok(problems);
    }

    @PostMapping("/gateway/wms/pda/shelf/submitPalletShelf")
    public R<Map<String, Object>> submitPalletShelf(@RequestBody PalletShelfSubmitReq req) {
        Order o = req.putawayNo() == null || req.putawayNo().isBlank() ? putaway.byPallet(req.palletCode()) : putaway.get(req.putawayNo());
        putaway.putPallet(o, req.palletCode(), req.shelfCellNo());
        return R.ok(Map.of("putawayNo", o.putawayNo));
    }

    // ---------- 批量上架（跨上架单） ----------

    /** 扫一批箱唛或箱型，返回每个条码对应的待上架候选（List&lt;BatchPutawayScanBoxResult&gt;）。 */
    @PostMapping("/gateway/wms/pda/shelf/batch/scanBox")
    public R<List<Map<String, Object>>> batchScanBox(@RequestBody BatchPutawayScanBoxReq req) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (String code : req.barcodes() == null ? List.<String>of() : req.barcodes()) {
            List<Map<String, Object>> candidates = new ArrayList<>();
            for (Order o : putaway.list()) {
                Box b = o.boxes.get(code);
                if (b != null && !b.shelved) {
                    candidates.add(candidate(o, b.boxBarcode, "", b.skus.values().stream().mapToInt(Integer::intValue).sum(), b.skus.size()));
                }
                PutawayStore.BoxTypeLine l = o.boxTypes.get(code);
                if (l != null && l.pending() > 0) {
                    candidates.add(candidate(o, "", code, l.pending(), 1));
                }
            }
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("candidateList", candidates);
            r.put("message", candidates.isEmpty() ? "没有待上架的箱：" + code : "");
            r.put("queryBarcode", code);
            out.add(r);
        }
        return R.ok(out);
    }

    private static Map<String, Object> candidate(Order o, String boxBarcode, String boxType, int pending, int skuCount) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxBarcode", boxBarcode);
        m.put("boxType", boxType);
        m.put("customerCode", o.customerCode);
        m.put("customerName", o.customerName);
        m.put("customizeBarcode", "");
        m.put("inboundType", o.inboundType);
        m.put("orderPendingPutawayQty", o.pendingBoxQty());
        m.put("pendingPutawayQty", pending);
        m.put("putawayNo", o.putawayNo);
        m.put("putawayQty", 0);
        m.put("skuCount", skuCount);
        m.put("sourceNo", o.sourceNo);
        return m;
    }

    @PostMapping("/gateway/wms/pda/shelf/batch/scanCell")
    public R<Object> batchScanCell(@RequestBody BatchPutawayScanCellReq req) {
        stock.requireCell(req.cellNo(), true);
        return R.ok(null);
    }

    /** 箱内 SKU 明细（List&lt;BatchPutawaySkuResult&gt;）。 */
    @PostMapping("/gateway/wms/pda/shelf/batch/skuList")
    public R<List<Map<String, Object>>> batchSkuList(@RequestBody BatchPutawaySkuQueryReq req) {
        Order o = putaway.get(req.putawayNo());
        List<Map<String, Object>> out = new ArrayList<>();
        for (String code : req.barcodes() == null ? List.<String>of() : req.barcodes()) {
            Box b = o.boxes.get(code);
            List<Map<String, Object>> skus = new ArrayList<>();
            if (b != null) {
                b.skus.forEach((sku, qty) -> {
                    Product p = stock.product(sku);
                    skus.add(Map.of("barcode", p.barcode(), "productImg", "", "productName", p.name(), "productSku", sku, "receiptQty", qty));
                });
            }
            out.add(Map.of("barcode", code, "message", b == null ? "箱唛不在上架单中：" + code : "", "skuList", skus));
        }
        return R.ok(out);
    }

    /** 批量上架到一个库位：逐单处理，单内先校验后上架；失败的单记入 failedList（BatchPutawaySubmitResp）。 */
    @PostMapping("/gateway/wms/pda/shelf/batch/submit")
    public R<Map<String, Object>> batchSubmit(@RequestBody BatchPutawaySubmitReq req) {
        stock.requireCell(req.cellNo(), true);
        List<Map<String, Object>> failed = new ArrayList<>();
        int success = 0;
        for (SubmitOrder so : req.orderList() == null ? List.<SubmitOrder>of() : req.orderList()) {
            List<SubmitBox> boxes = so.boxList() == null ? List.of() : so.boxList();
            List<SubmitBoxType> types = so.boxTypeList() == null ? List.of() : so.boxTypeList();
            try {
                Order o = putaway.get(so.putawayNo());
                Map<String, Integer> need = new LinkedHashMap<>();
                for (SubmitBox b : boxes) {
                    Box pb = o.boxes.get(b.boxBarcode());
                    if (pb == null || pb.shelved) {
                        throw new BizException("箱唛不存在或已上架：" + b.boxBarcode());
                    }
                    pb.skus.forEach((sku, qty) -> need.merge(sku, qty, Integer::sum));
                }
                for (Map.Entry<String, Integer> e : need.entrySet()) {
                    if (o.skus.get(e.getKey()).pending() < e.getValue()) {
                        throw new BizException(e.getKey() + " 已部分按 SKU 上架，不能整箱上架");
                    }
                }
                for (SubmitBoxType t : types) {
                    PutawayStore.BoxTypeLine l = o.boxTypes.get(t.boxType());
                    if (l == null || t.putawayQty() <= 0 || t.putawayQty() > l.pending()) {
                        throw new BizException("箱型 " + t.boxType() + " 上架数量不合法，待上架 " + (l == null ? 0 : l.pending()));
                    }
                    // 客户端看到的待上架数与服务端不一致，说明别人已经上过架
                    if (t.expectedPendingPutawayQty() > 0 && t.expectedPendingPutawayQty() != l.pending()) {
                        throw new BizException("箱型 " + t.boxType() + " 待上架数量已变化，请刷新");
                    }
                }
                for (SubmitBox b : boxes) {
                    putaway.putBox(o, b.boxBarcode(), req.cellNo());
                    success++;
                }
                for (SubmitBoxType t : types) {
                    putaway.putBoxType(o, t.boxType(), t.putawayQty(), req.cellNo());
                    success += t.putawayQty();
                }
            } catch (BizException e) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("boxBarcode", boxes.isEmpty() ? "" : boxes.get(0).boxBarcode());
                f.put("boxType", types.isEmpty() ? "" : types.get(0).boxType());
                f.put("customerCode", so.customerCode());
                f.put("failReason", e.getMessage());
                f.put("inboundType", so.inboundType());
                f.put("putawayNo", so.putawayNo());
                f.put("putawayQty", boxes.size() + types.stream().mapToInt(SubmitBoxType::putawayQty).sum());
                f.put("sourceNo", so.sourceNo());
                failed.add(f);
            }
        }
        int failedQty = failed.stream().mapToInt(f -> (int) f.get("putawayQty")).sum();
        return R.ok(Map.of("failedList", failed, "failedQty", failedQty, "successQty", success));
    }

    // ---------- 选库位 ----------

    /** 可选库位（Records&lt;SelectShelfCell&gt;）。isNullCell：1 空库位 / 0 有货。锁定库位不返回。 */
    @GetMapping("/gateway/wms/shelf/selectCell")
    public R<Map<String, Object>> selectCell(@RequestParam(defaultValue = "1") int current, @RequestParam(required = false) Integer productQuality,
                                             @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String whCode,
                                             @RequestParam(required = false) String cellNo) {
        List<String> empty = stock.emptyCells();
        List<Map<String, Object>> rows = stock.cells().stream()
                .filter(c -> !StockStore.LOCKED_CELLS.contains(c))
                .filter(c -> cellNo == null || cellNo.isBlank() || c.contains(cellNo.trim()))
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("areaNo", c.substring(0, 1) + "区");
                    m.put("areaType", c.startsWith("F") ? "存储区" : "拣货区");
                    m.put("cellNo", c);
                    m.put("cellType", c.startsWith("F") ? "托盘位" : "货架位");
                    m.put("isNullCell", empty.contains(c) ? 1 : 0);
                    m.put("productQuality", productQuality == null ? 1 : productQuality);
                    return m;
                }).toList();
        return R.ok(PageUtil.page(rows, current, size));
    }
}
