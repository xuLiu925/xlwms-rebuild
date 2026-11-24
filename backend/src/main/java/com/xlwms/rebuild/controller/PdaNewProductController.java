package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.NewProductStore;
import com.xlwms.rebuild.service.NewProductStore.Measure;
import com.xlwms.rebuild.service.ReceiptStore;
import com.xlwms.rebuild.service.ReceiptStore.Asn;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 新品维护：/gateway/wms/pda/newProduct/{getNeedMaintain,getNeedNewProductMaintain,listNeedNewProductMaintain,receiptMeasure,returnInNeedMaintain}。
 * 路径、请求体与响应字段（NewProductManBean）取自 APK；“哪些算新品”的规则见 {@link NewProductStore}。数据虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/newProduct")
public class PdaNewProductController {

    /** AllNeedNewProductMaintainReq */
    public record AllNeedReq(String asnNo, String customerCode, String whCode) {
    }

    /** NeedNewProductMaintainReq：skuList 在 APK 里是 List&lt;Object&gt;，这里接受 SKU 字符串或含 productSku 的对象。 */
    public record NeedReq(String asnNo, String customerCode, List<Object> skuList, String whCode) {
    }

    /** ReceiptMeasureBean（公制字段为准；wmsUnitMark 本项目忽略） */
    public record ReceiptMeasureBean(String customerCode, String productSku, String whCode, String wmsHeight, String wmsLength, Integer wmsUnitMark,
                                     String wmsWeight, String wmsWidth) {
    }

    public record SkuFnSku(String fnsku, Boolean isNew, String productSku) {
    }

    /** SkuFnSkuListBean */
    public record SkuFnSkuListBean(String customerCode, List<SkuFnSku> skuFnSkuList, String whCode) {
    }

    private final NewProductStore store;
    private final ReceiptStore receipts;
    private final StockStore stock;

    public PdaNewProductController(NewProductStore store, ReceiptStore receipts, StockStore stock) {
        this.store = store;
        this.receipts = receipts;
        this.stock = stock;
    }

    private static String fmt(double v) {
        return String.format("%.2f", v);
    }

    /** NewProductManBean：已测量的带实测值，未测量的尺寸重量为空串。 */
    private Map<String, Object> bean(String sku) {
        Product p = stock.product(sku);
        Measure m = store.measure(sku);
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("barcode", p.barcode());
        b.put("fnsku", List.of(p.fnsku()));
        b.put("height", m == null ? "" : fmt(m.height()));
        b.put("heightBs", m == null ? "" : fmt(m.height() / 2.54));
        b.put("isCnMark", false);
        b.put("length", m == null ? "" : fmt(m.length()));
        b.put("lengthBs", m == null ? "" : fmt(m.length() / 2.54));
        b.put("productName", p.name());
        b.put("productSku", sku);
        b.put("weight", m == null ? "" : fmt(m.weight()));
        b.put("weightBs", m == null ? "" : fmt(m.weight() * 2.20462));
        b.put("width", m == null ? "" : fmt(m.width()));
        b.put("widthBs", m == null ? "" : fmt(m.width() / 2.54));
        return b;
    }

    private Set<String> asnSkus(String asnNo) {
        Asn a = receipts.get(asnNo);
        Set<String> s = new LinkedHashSet<>();
        a.boxes.values().forEach(b -> s.addAll(b.skus.keySet()));
        a.boxTypes.keySet().forEach(bt -> s.add(stock.boxType(bt).sku()));
        s.addAll(a.skuLines.keySet());
        return s;
    }

    private static String skuOf(Object o) {
        if (o instanceof String s) {
            return s;
        }
        if (o instanceof Map<?, ?> m && m.get("productSku") != null) {
            return m.get("productSku").toString();
        }
        throw new BizException("skuList 元素需为 SKU 字符串或含 productSku 的对象");
    }

    private List<Map<String, Object>> needOf(NeedReq req) {
        Set<String> skus = asnSkus(req.asnNo());
        if (req.skuList() != null && !req.skuList().isEmpty()) {
            Set<String> wanted = new LinkedHashSet<>();
            req.skuList().forEach(o -> wanted.add(skuOf(o)));
            skus.retainAll(wanted);
        }
        return skus.stream().filter(store::isNew).map(this::bean).toList();
    }

    /** 入库单里全部待维护新品。 */
    @PostMapping("/getNeedMaintain")
    public R<List<Map<String, Object>>> getNeedMaintain(@RequestBody AllNeedReq req) {
        return R.ok(asnSkus(req.asnNo()).stream().filter(store::isNew).map(this::bean).toList());
    }

    /** 入库单里指定 SKU 中的待维护新品。 */
    @PostMapping("/getNeedNewProductMaintain")
    public R<List<Map<String, Object>>> getNeedNewProductMaintain(@RequestBody NeedReq req) {
        return R.ok(needOf(req));
    }

    /** 多张入库单一起查，按 SKU 去重。 */
    @PostMapping("/listNeedNewProductMaintain")
    public R<List<Map<String, Object>>> listNeed(@RequestBody List<NeedReq> reqs) {
        Map<String, Map<String, Object>> out = new LinkedHashMap<>();
        for (NeedReq r : reqs) {
            needOf(r).forEach(b -> out.putIfAbsent((String) b.get("productSku"), b));
        }
        return R.ok(new ArrayList<>(out.values()));
    }

    /** 收货时测量新品：先校验全部，再统一记录。 */
    @PostMapping("/receiptMeasure")
    public R<Object> receiptMeasure(@RequestBody List<ReceiptMeasureBean> list) {
        if (list == null || list.isEmpty()) {
            throw new BizException("没有要测量的 SKU");
        }
        List<double[]> parsed = new ArrayList<>();
        for (ReceiptMeasureBean m : list) {
            stock.product(m.productSku());
            try {
                double[] d = {Double.parseDouble(m.wmsLength()), Double.parseDouble(m.wmsWidth()), Double.parseDouble(m.wmsHeight()),
                        Double.parseDouble(m.wmsWeight())};
                if (d[0] <= 0 || d[1] <= 0 || d[2] <= 0 || d[3] <= 0 || d[0] > 300 || d[1] > 300 || d[2] > 300 || d[3] > 500) {
                    throw new BizException("SKU " + m.productSku() + " 的尺寸/重量超出范围（本地演示限制：0~300 cm，0~500 kg）");
                }
                parsed.add(d);
            } catch (NumberFormatException | NullPointerException e) {
                throw new BizException("SKU " + m.productSku() + " 的尺寸重量格式错误");
            }
        }
        for (int i = 0; i < list.size(); i++) {
            double[] d = parsed.get(i);
            store.record(list.get(i).productSku(), d[0], d[1], d[2], d[3]);
        }
        return R.ok(null);
    }

    /** 退货入库时判断哪些 SKU 需要维护（按 SKU 或 FNSKU 识别）。 */
    @PostMapping("/returnInNeedMaintain")
    public R<List<Map<String, Object>>> returnInNeedMaintain(@RequestBody SkuFnSkuListBean req) {
        Set<String> skus = new LinkedHashSet<>();
        for (SkuFnSku s : req.skuFnSkuList() == null ? List.<SkuFnSku>of() : req.skuFnSkuList()) {
            Product p = s.productSku() != null && !s.productSku().isBlank() ? stock.product(s.productSku()) : stock.findProduct(s.fnsku());
            if (p == null) {
                throw new BizException("未识别的 SKU/FNSKU：" + s.fnsku());
            }
            skus.add(p.sku());
        }
        return R.ok(skus.stream().filter(store::isNew).map(this::bean).toList());
    }
}
