package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.receipt.BoxTypeInfoReq;
import com.xlwms.rebuild.dto.receipt.PalletReceiptScanReq;
import com.xlwms.rebuild.dto.receipt.SkuForecastReceiptReq;
import com.xlwms.rebuild.dto.receipt.WholeOrderReceiptReq;
import com.xlwms.rebuild.service.NewProductStore;
import com.xlwms.rebuild.service.PutawayStore;
import com.xlwms.rebuild.service.ReceiptStore;
import com.xlwms.rebuild.service.ReceiptStore.Asn;
import com.xlwms.rebuild.service.ReceiptStore.Box;
import com.xlwms.rebuild.service.ReceiptStore.BoxTypeLine;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.BoxType;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 收货：/gateway/wms/pda/receipt/ 下全部 21 个路径（listBoxType 在 APK 里有两个方法共用一个路径）与
 * /gateway/wms/blReceipt/measureBoxType。路径、参数名、请求体与响应字段取自 APK；规则见 {@link ReceiptStore}。
 * scanPallet / getBoxTypeInfo / skuReceipt / wholeOrderReceipt 的请求体沿用 dto/receipt 下批次 1 按反编译建的类。
 * 数据虚构，不连生产。
 */
@RestController
public class PdaReceiptFlowController {

    // ---------- 请求体（字段名取自 APK 同名类） ----------

    public record SkuQty(String productName, String productSku, Integer receiptQty) {
    }

    public record WholeReceiptBox(String boxBarcode, List<SkuQty> skuList) {
    }

    /** SubmitWholeReceipt */
    public record SubmitWholeReceipt(String asnNo, List<WholeReceiptBox> boxList, String customerCode, String rowVersion, String sourceNo, String whCode) {
    }

    public record BoxTypeQty(String boxType, int receiptQty) {
    }

    /** BoxTypeReceipt */
    public record BoxTypeReceipt(String asnNo, List<BoxTypeQty> boxTypeDTOS, String customerCode, String rowVersion, String sourceNo, String whCode) {
    }

    public record BatchAsn(String asnNo, List<WholeReceiptBox> boxList, String customerCode, String rowVersion, String sourceNo) {
    }

    public record BatchTypeAsn(String asnNo, List<BoxTypeQty> boxTypeDTOS, String customerCode, String rowVersion, String sourceNo) {
    }

    /** BatchReceiptSubmitReq */
    public record BatchReceiptSubmitReq(List<BatchAsn> boxList, List<BatchTypeAsn> boxTypeList, String whCode) {
    }

    public record PalletDims(String palletCode, List<WholeReceiptBox> palletBoxList, List<BoxTypeQty> palletBoxTypeList, Double palletHeight,
                             Double palletLength, Double palletWeight, Double palletWidth, String remark) {
    }

    /** BoxPalletReceiptReq / BoxTypePalletReceiptReq（两者只差 palletBoxList / palletBoxTypeList 和 receiptPallets） */
    public record PalletReceiptReq(String asnNo, String customerCode, List<PalletDims> palletList, Integer receiptPallets, String rowVersion,
                                   String sourceNo, String whCode) {
    }

    public record PalletSku(Integer asnQty, String boxBarcode, Integer receiptQty, String sku) {
    }

    /** ScanPalletSkuReq */
    public record ScanPalletSkuReq(String asnNo, String barcode, String customerCode, List<PalletSku> skuList, String sourceNo, String whCode) {
    }

    /** SkuDetailPageReq */
    public record SkuDetailPageReq(String asnNo, int current, String customerCode, int size, String whCode) {
    }

    public record WmsBoxTypeInfo(String boxType, String customBoxType, String wmsBoxHeight, String wmsBoxLength, String wmsBoxWeight,
                                 String wmsBoxWidth, Integer wmsUnitMark) {
    }

    /** MeasureBoxType */
    public record MeasureBoxType(String asnNo, String customerCode, String sourceNo, String whCode, List<WmsBoxTypeInfo> wmsBoxTypeInfoDTOList) {
    }

    /** BatchMeasureBoxTypeReq */
    public record BatchMeasureBoxTypeReq(List<MeasureBoxType> boxTypeInfoList, String whCode) {
    }

    private final ReceiptStore receipts;
    private final StockStore stock;
    private final NewProductStore newProducts;
    private final PutawayStore putaway;

    public PdaReceiptFlowController(ReceiptStore receipts, StockStore stock, NewProductStore newProducts, PutawayStore putaway) {
        this.receipts = receipts;
        this.stock = stock;
        this.newProducts = newProducts;
        this.putaway = putaway;
    }

    // ---------- 视图 ----------

    private int pcs(Asn a, boolean received) {
        int boxPcs = a.boxes.values().stream().mapToInt(b -> received ? b.receiptQty() : b.asnQty()).sum();
        int typePcs = a.boxTypes.values().stream().mapToInt(t -> (received ? t.receiptQty : t.asnBoxQty) * stock.boxType(t.boxType).pcsPerBox()).sum();
        int skuPcs = a.skuLines.values().stream().mapToInt(l -> received ? l.receiptQty : l.asnQty).sum();
        return boxPcs + typePcs + skuPcs;
    }

    private Set<String> skus(Asn a) {
        Set<String> s = new HashSet<>();
        a.boxes.values().forEach(b -> s.addAll(b.skus.keySet()));
        a.boxTypes.keySet().forEach(bt -> s.add(stock.boxType(bt).sku()));
        s.addAll(a.skuLines.keySet());
        return s;
    }

    /** ReceiptAsnInfo */
    private Map<String, Object> asnInfo(Asn a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asnNo", a.asnNo);
        m.put("boxCount", a.boxCount());
        m.put("customerCode", a.customerCode);
        m.put("customerName", a.customerName);
        m.put("forecastType", a.forecastType);
        m.put("forecastTypeName", ReceiptStore.FORECAST_TYPE.get(a.forecastType));
        m.put("inboundType", a.inboundType);
        m.put("palletReceiptFlag", a.palletReceiptFlag);
        m.put("receiptQty", pcs(a, true));
        m.put("rowVersion", String.valueOf(a.rowVersion));
        m.put("skuNum", skus(a).size());
        m.put("sourceNo", a.sourceNo);
        m.put("status", a.status());
        m.put("tenantCode", "DEMO");
        m.put("trackingNo", a.trackingNo);
        return m;
    }

    /** ReceiptBoxScan.Sku / GetAsnDtlResp.Sku（两者字段基本相同，这里合并输出）。 */
    private Map<String, Object> skuView(String sku, int asnQty, int receiptQty) {
        Product p = stock.product(sku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asnQty", asnQty);
        m.put("barcode", p.barcode());
        m.put("barcodeList", List.of(p.barcode()));
        m.put("expirationStatus", 0);
        m.put("expiryTime", "");
        m.put("isDoneExpiry", false);
        m.put("isNew", newProducts.isNew(sku) ? 1 : 0);
        m.put("productImg", "");
        m.put("productName", p.name());
        m.put("productSku", sku);
        m.put("productionTime", "");
        m.put("realReceiptQty", receiptQty);
        m.put("receiptQty", receiptQty);
        return m;
    }

    /** ReceiptBoxScan / GetAsnDtlResp.Box */
    private Map<String, Object> boxView(Box b) {
        String first = b.skus.keySet().iterator().next();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("anomalyReason", "");
        m.put("asnQty", b.asnQty());
        m.put("barcode", stock.product(first).barcode());
        m.put("boxBarcode", b.boxBarcode);
        m.put("bySkuDropShipping", false);
        m.put("customizeBarcode", "");
        m.put("matchSku", b.skus.size() == 1 ? first : "");
        m.put("receiptFlag", b.received ? 1 : 0);
        m.put("receiptQty", b.receiptQty());
        m.put("skuList", b.skus.entrySet().stream().map(e -> skuView(e.getKey(), e.getValue().asnQty, e.getValue().receiptQty)).toList());
        m.put("skuNum", b.skus.size());
        return m;
    }

    /** ListBoxTypeResp.Record */
    private Map<String, Object> boxTypeRecord(BoxTypeLine t) {
        BoxType b = stock.boxType(t.boxType);
        Product p = stock.product(b.sku());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxType", t.boxType);
        m.put("customizeBarcode", "");
        m.put("needReceiptQty", t.asnBoxQty);
        m.put("realReceiptQty", t.receiptQty);
        m.put("skuList", List.of(Map.of("asnQty", String.valueOf(b.pcsPerBox()), "productName", p.name(), "productSku", p.sku())));
        m.put("status", t.status());
        return m;
    }

    /** GetAsnDtlResp.BoxType：在 Record 基础上加箱规（优先用实测值）。 */
    private Map<String, Object> boxTypeDetail(BoxTypeLine t) {
        BoxType b = stock.boxType(t.boxType);
        double[] d = t.measured != null ? t.measured : new double[] {b.length(), b.width(), b.height(), b.weight()};
        Map<String, Object> m = new LinkedHashMap<>(boxTypeRecord(t));
        m.put("anomalyReason", "");
        m.put("asnBoxQty", t.asnBoxQty);
        m.put("boxWeightUnit", "kg");
        m.put("boxWeightUnitBs", "lb");
        m.put("length", fmt(d[0]));
        m.put("lengthBs", fmt(d[0] / 2.54));
        m.put("width", fmt(d[1]));
        m.put("widthBs", fmt(d[1] / 2.54));
        m.put("height", fmt(d[2]));
        m.put("heightBs", fmt(d[2] / 2.54));
        m.put("weight", fmt(d[3]));
        m.put("weightBs", fmt(d[3] * 2.20462));
        m.put("sizeUnit", "cm");
        m.put("sizeUnitBs", "in");
        m.put("skuList", List.of(skuView(b.sku(), t.asnBoxQty * b.pcsPerBox(), t.receiptQty * b.pcsPerBox())));
        return m;
    }

    private static String fmt(double v) {
        return String.format("%.2f", v);
    }

    private static Map<String, Integer> skuMap(List<SkuQty> list) {
        Map<String, Integer> m = new LinkedHashMap<>();
        if (list != null) {
            for (SkuQty s : list) {
                m.merge(s.productSku(), s.receiptQty() == null ? 0 : s.receiptQty(), Integer::sum);
            }
        }
        return m;
    }

    // ---------- 查询 ----------

    @GetMapping("/gateway/wms/pda/receipt/getAllReceiptList")
    public R<List<Map<String, Object>>> getAllReceiptList(@RequestParam(required = false) String whCode) {
        return R.ok(receipts.list().stream().filter(a -> a.status() != 15).map(this::asnInfo).toList());
    }

    @GetMapping("/gateway/wms/pda/receipt/getAsn")
    public R<List<Map<String, Object>>> getAsn(@RequestParam String param, @RequestParam(required = false) String whCode) {
        return R.ok(receipts.search(param).stream().map(this::asnInfo).toList());
    }

    @GetMapping("/gateway/wms/pda/receipt/getOrderDtl")
    public R<Map<String, Object>> getOrderDtl(@RequestParam String asnNo, @RequestParam(required = false) String customerCode,
                                              @RequestParam(required = false) String whCode) {
        Asn a = receipts.get(asnNo);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asnNo", a.asnNo);
        m.put("asnQty", pcs(a, false));
        m.put("boxCount", a.boxCount());
        m.put("customerCode", a.customerCode);
        m.put("customerName", a.customerName);
        m.put("forecastType", a.forecastType);
        m.put("forecastTypeName", ReceiptStore.FORECAST_TYPE.get(a.forecastType));
        m.put("receiptQty", pcs(a, true));
        m.put("rowVersion", String.valueOf(a.rowVersion));
        m.put("sourceNo", a.sourceNo);
        m.put("status", a.status());
        m.put("unReceiptBoxCount", a.boxCount() - a.receivedBoxCount());
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    /** 入库单按 SKU 汇总的分页（Records&lt;ReceiptBoxScan.Sku&gt;）。 */
    @PostMapping("/gateway/wms/pda/receipt/getOrderDtl/skuDetailPage")
    public R<Map<String, Object>> skuDetailPage(@RequestBody SkuDetailPageReq req) {
        Asn a = receipts.get(req.asnNo());
        Map<String, int[]> agg = new LinkedHashMap<>();
        a.boxes.values().forEach(b -> b.skus.forEach((sku, l) -> {
            int[] v = agg.computeIfAbsent(sku, k -> new int[2]);
            v[0] += l.asnQty;
            v[1] += l.receiptQty;
        }));
        a.boxTypes.values().forEach(t -> {
            BoxType b = stock.boxType(t.boxType);
            int[] v = agg.computeIfAbsent(b.sku(), k -> new int[2]);
            v[0] += t.asnBoxQty * b.pcsPerBox();
            v[1] += t.receiptQty * b.pcsPerBox();
        });
        a.skuLines.forEach((sku, l) -> {
            int[] v = agg.computeIfAbsent(sku, k -> new int[2]);
            v[0] += l.asnQty;
            v[1] += l.receiptQty;
        });
        List<Map<String, Object>> rows = new ArrayList<>();
        agg.forEach((sku, v) -> rows.add(skuView(sku, v[0], v[1])));
        return R.ok(PageUtil.page(rows, Math.max(1, req.current()), req.size() <= 0 ? 20 : req.size()));
    }

    /** 扫箱唛或产品条码：箱唛返回该箱；产品条码返回入库单里含该产品的箱（List&lt;ReceiptBoxScan&gt;）。 */
    @GetMapping("/gateway/wms/pda/receipt/v2/getBoxDtl")
    public R<List<Map<String, Object>>> getBoxDtl(@RequestParam String asnNo, @RequestParam(required = false) String boxBarcode,
                                                  @RequestParam(required = false) String barcode, @RequestParam(required = false) String customerCode,
                                                  @RequestParam(required = false) String whCode) {
        Asn a = receipts.get(asnNo);
        if (boxBarcode != null && !boxBarcode.isBlank()) {
            Box b = a.boxes.get(boxBarcode.trim());
            if (b == null) {
                throw new BizException("箱唛不属于入库单 " + a.asnNo + "：" + boxBarcode);
            }
            return R.ok(List.of(boxView(b)));
        }
        Product p = stock.findProduct(barcode);
        if (p == null) {
            throw new BizException("未识别的条码：" + barcode);
        }
        List<Map<String, Object>> out = a.boxes.values().stream().filter(b -> b.skus.containsKey(p.sku())).map(this::boxView).toList();
        if (out.isEmpty()) {
            throw new BizException("入库单 " + a.asnNo + " 中没有该产品：" + p.sku());
        }
        return R.ok(out);
    }

    @GetMapping("/gateway/wms/pda/receipt/listBoxType")
    public R<Map<String, Object>> listBoxType(@RequestParam String asnNo, @RequestParam(required = false) String customerCode,
                                              @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String whCode, @RequestParam(required = false) String boxType) {
        Asn a = receipts.get(asnNo);
        List<Map<String, Object>> rows = a.boxTypes.values().stream()
                .filter(t -> boxType == null || boxType.isBlank() || t.boxType.contains(boxType.trim()))
                .map(this::boxTypeRecord).toList();
        return R.ok(PageUtil.page(rows, current, size));
    }

    @GetMapping("/gateway/wms/pda/receipt/getBoxTypeDtl")
    public R<List<Map<String, Object>>> getBoxTypeDtl(@RequestParam String asnNo, @RequestParam String boxType,
                                                      @RequestParam(required = false) String customerCode, @RequestParam(required = false) String whCode) {
        BoxTypeLine t = receipts.get(asnNo).boxTypes.get(boxType.trim());
        if (t == null) {
            throw new BizException("箱型不属于入库单 " + asnNo + "：" + boxType);
        }
        return R.ok(List.of(boxTypeDetail(t)));
    }

    /** 批量收货前取入库单明细（List&lt;GetAsnDtlResp&gt;）。 */
    @GetMapping("/gateway/wms/pda/receipt/batchReceipt/getAsnDtl")
    public R<List<Map<String, Object>>> getAsnDtl(@RequestParam String param, @RequestParam(required = false) String whCode) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Asn a : receipts.search(param)) {
            Map<String, Object> m = asnInfo(a);
            m.put("boxList", a.boxes.values().stream().map(this::boxView).toList());
            m.put("boxTypeList", a.boxTypes.values().stream().map(this::boxTypeDetail).toList());
            m.put("createTime", a.createTime);
            m.put("currentBox", a.boxes.containsKey(param.trim()) ? boxView(a.boxes.get(param.trim())) : null);
            m.put("orderAnomalyReason", "");
            m.put("originalReceiptQty", pcs(a, true));
            m.put("overReceipt", false);
            m.put("updateTime", 1795536000000L + a.rowVersion);
            m.put("whCode", StockStore.WH);
            out.add(m);
        }
        return R.ok(out);
    }

    // ---------- 收货 ----------

    /** 先校验全部箱子，全部通过才收货；返回上架单号。 */
    private String receiveBoxes(String asnNo, String rowVersion, List<WholeReceiptBox> boxes, String palletCode) {
        Asn a = receipts.get(asnNo);
        if (boxes == null || boxes.isEmpty()) {
            throw new BizException("请至少收一箱");
        }
        Set<String> seen = new HashSet<>();
        for (WholeReceiptBox b : boxes) {
            if (!seen.add(b.boxBarcode())) {
                throw new BizException("箱唛重复：" + b.boxBarcode());
            }
            receipts.checkBox(a, rowVersion, b.boxBarcode(), skuMap(b.skuList()));
        }
        for (WholeReceiptBox b : boxes) {
            receipts.receiveBox(asnNo, null, b.boxBarcode(), skuMap(b.skuList()), palletCode);
        }
        return receipts.putawayNoOf(asnNo);
    }

    private String receiveTypes(String asnNo, String rowVersion, List<BoxTypeQty> types, String palletCode) {
        Asn a = receipts.get(asnNo);
        if (types == null || types.isEmpty()) {
            throw new BizException("请至少收一个箱型");
        }
        Map<String, Integer> already = new LinkedHashMap<>();
        for (BoxTypeQty t : types) {
            receipts.checkBoxType(a, rowVersion, t.boxType(), t.receiptQty(), already.getOrDefault(t.boxType(), 0));
            already.merge(t.boxType(), t.receiptQty(), Integer::sum);
        }
        for (BoxTypeQty t : types) {
            receipts.receiveBoxType(asnNo, null, t.boxType(), t.receiptQty(), palletCode);
        }
        return receipts.putawayNoOf(asnNo);
    }

    @PostMapping("/gateway/wms/pda/receipt/wholeBoxReceipt")
    public R<String> wholeBoxReceipt(@RequestBody SubmitWholeReceipt req) {
        return R.ok(receiveBoxes(req.asnNo(), req.rowVersion(), req.boxList(), null));
    }

    /** 收本次的箱子（可以为空）并完成收货，未收的箱视为少收。 */
    @PostMapping("/gateway/wms/pda/receipt/finishWholeBoxReceipt")
    public R<String> finishWholeBoxReceipt(@RequestBody SubmitWholeReceipt req) {
        if (req.boxList() != null && !req.boxList().isEmpty()) {
            receiveBoxes(req.asnNo(), req.rowVersion(), req.boxList(), null);
        }
        receipts.finish(req.asnNo());
        return R.ok(receipts.putawayNoOf(req.asnNo()));
    }

    @PostMapping("/gateway/wms/pda/receipt/BoxTypeReceipt")
    public R<String> boxTypeReceipt(@RequestBody BoxTypeReceipt req) {
        return R.ok(receiveTypes(req.asnNo(), req.rowVersion(), req.boxTypeDTOS(), null));
    }

    @PostMapping("/gateway/wms/pda/receipt/finishBoxTypeReceipt")
    public R<Object> finishBoxTypeReceipt(@RequestBody BoxTypeReceipt req) {
        if (req.boxTypeDTOS() != null && !req.boxTypeDTOS().isEmpty()) {
            receiveTypes(req.asnNo(), req.rowVersion(), req.boxTypeDTOS(), null);
        }
        receipts.finish(req.asnNo());
        return R.ok(null);
    }

    /** 批量收货：逐单处理，失败的单记入 failedList，成功的照常收货（BatchReceiptSubmitResp）。 */
    @PostMapping("/gateway/wms/pda/receipt/batchReceipt/submit")
    public R<Map<String, Object>> batchSubmit(@RequestBody BatchReceiptSubmitReq req) {
        List<Map<String, Object>> failed = new ArrayList<>();
        if (req.boxList() != null) {
            for (BatchAsn a : req.boxList()) {
                try {
                    receiveBoxes(a.asnNo(), a.rowVersion(), a.boxList(), null);
                } catch (BizException e) {
                    failed.add(failedItem(a.asnNo(), a.sourceNo(), a.customerCode(),
                            a.boxList() == null || a.boxList().isEmpty() ? "" : a.boxList().get(0).boxBarcode(), "", e.getMessage()));
                }
            }
        }
        if (req.boxTypeList() != null) {
            for (BatchTypeAsn a : req.boxTypeList()) {
                try {
                    receiveTypes(a.asnNo(), a.rowVersion(), a.boxTypeDTOS(), null);
                } catch (BizException e) {
                    failed.add(failedItem(a.asnNo(), a.sourceNo(), a.customerCode(), "",
                            a.boxTypeDTOS() == null || a.boxTypeDTOS().isEmpty() ? "" : a.boxTypeDTOS().get(0).boxType(), e.getMessage()));
                }
            }
        }
        return R.ok(Map.of("failedList", failed));
    }

    private static Map<String, Object> failedItem(String asnNo, String sourceNo, String customerCode, String boxBarcode, String boxType, String msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asnNo", asnNo);
        m.put("boxBarcode", boxBarcode);
        m.put("boxType", boxType);
        m.put("customerCode", customerCode);
        m.put("failedMsg", msg);
        m.put("sourceNo", sourceNo);
        return m;
    }

    private void checkPallets(PalletReceiptReq req) {
        Asn a = receipts.get(req.asnNo());
        if (a.palletReceiptFlag != 1) {
            throw new BizException("入库单 " + a.asnNo + " 不是托盘收货单");
        }
        if (req.palletList() == null || req.palletList().isEmpty()) {
            throw new BizException("请至少扫一个托盘");
        }
        Set<String> seen = new HashSet<>();
        for (PalletDims p : req.palletList()) {
            if (p.palletCode() == null || p.palletCode().isBlank()) {
                throw new BizException("托盘号不能为空");
            }
            if (!seen.add(p.palletCode()) || putaway.palletInUse(p.palletCode())) {
                throw new BizException("托盘已被占用：" + p.palletCode());
            }
        }
    }

    /** 按箱唛托盘收货：先校验全部托盘和箱子，再逐托收货并绑定托盘。 */
    @PostMapping("/gateway/wms/pda/receipt/boxPalletReceipt")
    public R<Object> boxPalletReceipt(@RequestBody PalletReceiptReq req) {
        checkPallets(req);
        Asn a = receipts.get(req.asnNo());
        Set<String> seen = new HashSet<>();
        for (PalletDims p : req.palletList()) {
            for (WholeReceiptBox b : p.palletBoxList() == null ? List.<WholeReceiptBox>of() : p.palletBoxList()) {
                if (!seen.add(b.boxBarcode())) {
                    throw new BizException("箱唛重复：" + b.boxBarcode());
                }
                receipts.checkBox(a, req.rowVersion(), b.boxBarcode(), skuMap(b.skuList()));
            }
        }
        for (PalletDims p : req.palletList()) {
            for (WholeReceiptBox b : p.palletBoxList() == null ? List.<WholeReceiptBox>of() : p.palletBoxList()) {
                receipts.receiveBox(req.asnNo(), null, b.boxBarcode(), skuMap(b.skuList()), p.palletCode());
            }
        }
        return R.ok(null);
    }

    /** 按箱型托盘收货。 */
    @PostMapping("/gateway/wms/pda/receipt/boxTypePalletReceipt")
    public R<Object> boxTypePalletReceipt(@RequestBody PalletReceiptReq req) {
        checkPallets(req);
        Asn a = receipts.get(req.asnNo());
        Map<String, Integer> already = new LinkedHashMap<>();
        for (PalletDims p : req.palletList()) {
            for (BoxTypeQty t : p.palletBoxTypeList() == null ? List.<BoxTypeQty>of() : p.palletBoxTypeList()) {
                receipts.checkBoxType(a, req.rowVersion(), t.boxType(), t.receiptQty(), already.getOrDefault(t.boxType(), 0));
                already.merge(t.boxType(), t.receiptQty(), Integer::sum);
            }
        }
        for (PalletDims p : req.palletList()) {
            for (BoxTypeQty t : p.palletBoxTypeList() == null ? List.<BoxTypeQty>of() : p.palletBoxTypeList()) {
                receipts.receiveBoxType(req.asnNo(), null, t.boxType(), t.receiptQty(), p.palletCode());
            }
        }
        return R.ok(null);
    }

    /** 托盘收货时扫产品条码：返回还没收的、含该产品的箱（List&lt;ReceiptBoxScan&gt;）。 */
    @PostMapping("/gateway/wms/pda/receipt/scanPalletSku")
    public R<List<Map<String, Object>>> scanPalletSku(@RequestBody ScanPalletSkuReq req) {
        Asn a = receipts.get(req.asnNo());
        Product p = stock.findProduct(req.barcode());
        if (p == null) {
            throw new BizException("未识别的条码：" + req.barcode());
        }
        Set<String> scanned = new HashSet<>();
        if (req.skuList() != null) {
            req.skuList().forEach(s -> scanned.add(s.boxBarcode()));
        }
        List<Map<String, Object>> out = a.boxes.values().stream()
                .filter(b -> !b.received && !scanned.contains(b.boxBarcode) && b.skus.containsKey(p.sku())).map(this::boxView).toList();
        if (out.isEmpty()) {
            throw new BizException("入库单 " + a.asnNo + " 中没有待收的该产品：" + p.sku());
        }
        return R.ok(out);
    }

    // ---------- 原批次 1 的 4 个接口（已改为有状态） ----------

    /** 收货前扫托盘，看托盘能不能用（ScanPalletStatus）。status 本项目假设：0 空闲可用 / 1 已绑定未上架。 */
    @PostMapping("/gateway/wms/pda/receipt/scanPallet")
    public R<Map<String, Object>> scanPallet(@RequestBody PalletReceiptScanReq req) {
        String code = req.getPalletCode() == null ? "" : req.getPalletCode().trim();
        if (code.isEmpty()) {
            throw new BizException("托盘号不能为空");
        }
        return R.ok(Map.of("palletCode", code, "status", putaway.palletInUse(code) ? 1 : 0, "tenantCode", "DEMO", "whCode", StockStore.WH));
    }

    /** 入库单箱型的箱规（List&lt;BoxTypeInfoRsp&gt;），boxTypeList 为空返回全部；有实测值时用实测值。 */
    @PostMapping("/gateway/wms/pda/receipt/getBoxTypeInfo")
    public R<List<Map<String, Object>>> getBoxTypeInfo(@RequestBody BoxTypeInfoReq req) {
        Asn a = receipts.get(req.getAsnNo());
        List<String> wanted = req.getBoxTypeList() == null || req.getBoxTypeList().isEmpty()
                ? new ArrayList<>(a.boxTypes.keySet()) : req.getBoxTypeList();
        List<Map<String, Object>> out = new ArrayList<>();
        for (String bt : wanted) {
            BoxTypeLine t = a.boxTypes.get(bt);
            if (t == null) {
                throw new BizException("箱型不属于入库单 " + a.asnNo + "：" + bt);
            }
            BoxType b = stock.boxType(bt);
            double[] d = t.measured != null ? t.measured : new double[] {b.length(), b.width(), b.height(), b.weight()};
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("boxType", bt);
            m.put("customizeBarcode", "");
            m.put("height", fmt(d[2]));
            m.put("heightBs", fmt(d[2] / 2.54));
            m.put("isCnMark", false);
            m.put("length", fmt(d[0]));
            m.put("lengthBs", fmt(d[0] / 2.54));
            m.put("sizeUnit", "cm");
            m.put("unitMark", "1");
            m.put("weight", fmt(d[3]));
            m.put("weightBs", fmt(d[3] * 2.20462));
            m.put("weightUnit", "kg");
            m.put("width", fmt(d[1]));
            m.put("widthBs", fmt(d[1] / 2.54));
            out.add(m);
        }
        return R.ok(out);
    }

    /**
     * 按 SKU 收货（SkuForecastReceiptReq），返回上架单号。skuList 是散收，skuPalletList 是按托盘收；
     * 全部 SKU 合计校验通过（不超收、托盘空闲）后才收货。
     */
    @PostMapping("/gateway/wms/pda/receipt/skuReceipt")
    public R<String> skuReceipt(@RequestBody SkuForecastReceiptReq req) {
        Asn a = receipts.get(req.getAsnNo());
        Map<String, Integer> loose = new LinkedHashMap<>();
        if (req.getSkuList() != null) {
            req.getSkuList().forEach(s -> loose.merge(s.getProductSku(), s.getReceiptQty(), Integer::sum));
        }
        Map<String, Map<String, Integer>> byPallet = new LinkedHashMap<>();
        if (req.getSkuPalletList() != null) {
            for (SkuForecastReceiptReq.SkuPallet p : req.getSkuPalletList()) {
                String code = p.getPalletCode() == null ? "" : p.getPalletCode().trim();
                if (code.isEmpty() || byPallet.containsKey(code) || putaway.palletInUse(code)) {
                    throw new BizException("托盘号为空或已被占用：" + code);
                }
                Map<String, Integer> m = new LinkedHashMap<>();
                if (p.getSkuList() != null) {
                    p.getSkuList().forEach(s -> m.merge(s.getProductSku(), s.getReceiptQty(), Integer::sum));
                }
                byPallet.put(code, m);
            }
        }
        Map<String, Integer> total = new LinkedHashMap<>(loose);
        byPallet.values().forEach(m -> m.forEach((sku, q) -> total.merge(sku, q, Integer::sum)));
        receipts.checkSkus(a, req.getRowVersion(), total);
        if (!loose.isEmpty()) {
            receipts.receiveSkus(a.asnNo, loose, null);
        }
        byPallet.forEach((code, m) -> {
            if (!m.isEmpty()) {
                receipts.receiveSkus(a.asnNo, m, code);
            }
        });
        return R.ok(receipts.putawayNoOf(a.asnNo));
    }

    /** 整单收货：剩余全部按预报收完并完成收货。 */
    @PostMapping("/gateway/wms/pda/receipt/wholeOrderReceipt")
    public R<Object> wholeOrderReceipt(@RequestBody WholeOrderReceiptReq req) {
        receipts.receiveAllAndFinish(req.getAsnNo(), req.getRowVersion());
        return R.ok(null);
    }

    // ---------- 箱规测量 ----------

    private void measure(MeasureBoxType m) {
        if (m.wmsBoxTypeInfoDTOList() == null || m.wmsBoxTypeInfoDTOList().isEmpty()) {
            throw new BizException("没有要测量的箱型");
        }
        // 先解析并校验全部箱型，再统一写入，避免一单里只记了一部分
        Asn a = receipts.get(m.asnNo());
        List<double[]> parsed = new ArrayList<>();
        for (WmsBoxTypeInfo i : m.wmsBoxTypeInfoDTOList()) {
            if (!a.boxTypes.containsKey(i.boxType())) {
                throw new BizException("箱型不属于入库单 " + a.asnNo + "：" + i.boxType());
            }
            try {
                double[] d = {Double.parseDouble(i.wmsBoxLength()), Double.parseDouble(i.wmsBoxWidth()),
                        Double.parseDouble(i.wmsBoxHeight()), Double.parseDouble(i.wmsBoxWeight())};
                for (double v : d) {
                    if (v <= 0) {
                        throw new BizException("箱型 " + i.boxType() + " 的尺寸重量必须大于 0");
                    }
                }
                parsed.add(d);
            } catch (NumberFormatException | NullPointerException e) {
                throw new BizException("箱型 " + i.boxType() + " 的尺寸重量格式错误");
            }
        }
        for (int k = 0; k < parsed.size(); k++) {
            double[] d = parsed.get(k);
            receipts.measureBoxType(a.asnNo, m.wmsBoxTypeInfoDTOList().get(k).boxType(), d[0], d[1], d[2], d[3]);
        }
    }

    @PostMapping("/gateway/wms/blReceipt/measureBoxType")
    public R<Object> measureBoxType(@RequestBody MeasureBoxType req) {
        measure(req);
        return R.ok(null);
    }

    /** 批量测量：逐单处理，返回每单结果（errorMsg 为空表示成功）。 */
    @PostMapping("/gateway/wms/pda/receipt/measureBoxType/batch")
    public R<List<Map<String, Object>>> measureBatch(@RequestBody BatchMeasureBoxTypeReq req) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (MeasureBoxType m : req.boxTypeInfoList() == null ? List.<MeasureBoxType>of() : req.boxTypeInfoList()) {
            String err = "";
            try {
                measure(m);
            } catch (BizException e) {
                err = e.getMessage();
            }
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("asnNo", m.asnNo());
            r.put("customerCode", m.customerCode());
            r.put("errorMsg", err);
            r.put("sourceNo", m.sourceNo());
            out.add(r);
        }
        return R.ok(out);
    }
}
