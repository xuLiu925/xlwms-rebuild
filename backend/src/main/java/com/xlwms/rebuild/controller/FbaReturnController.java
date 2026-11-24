package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.FbaReturnStore;
import com.xlwms.rebuild.service.FbaReturnStore.Line;
import com.xlwms.rebuild.service.FbaReturnStore.PickLine;
import com.xlwms.rebuild.service.FbaReturnStore.PickTask;
import com.xlwms.rebuild.service.FbaReturnStore.Task;
import com.xlwms.rebuild.service.PutawayStore;
import com.xlwms.rebuild.service.ReturnStore;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA FBA 退货：/gateway/wms/pda/blReturnIn/** 共 24 个接口——签收、清点、上架、认领、退货拣货。
 * 路径、参数名、请求体与响应字段取自 APK；规则见 {@link FbaReturnStore}。认领（createClaim）会在 {@link ReturnStore} 建一张
 * orderType=4 的待上架退件单。客户权限字段为占位（全部 true）。数据虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/blReturnIn")
public class FbaReturnController {

    // ---------- 请求体 ----------

    public record FbaConfirmSignReq(String customerCode, String customerName, String logisticsCarrier, String temporaryCellNo, String trackNo,
                                    String whCode) {
    }

    public record SignSku(String expressNo, String fnsku, String logisticsCarrier, String sku) {
    }

    public record UpdateStatusSign(String customerCode, String fritTaskNo, List<SignSku> signSkuList, String temporaryCellNo, String whCode) {
    }

    public record InventoryDetail(String fnsku, String receiptQtyBad, String receiptQtyGood, String sku, String skuId) {
    }

    public record UpdateStatusInventoryReq(String customerCode, List<InventoryDetail> detailList, String expressNo, String fritTaskNo,
                                           String logisticsCarrier, String receiptRemark, String temporaryCellNo, String whCode) {
    }

    public record PreCheckProduct(Integer defectiveQty, String fnsku, Integer positiveQty, String productName, String productSku, String skuId) {
    }

    public record InventoryPreCheckReq(String customerCode, String customerName, String expressNo, String logisticsCarrier,
                                       List<PreCheckProduct> productList, String receiptRemark, String temporaryCellNo, String whCode) {
    }

    public record JudgeCallReq(String cellNo, String customerCode, String expressNo, String fritTaskNo, String logisticsCarrier, int productQuality,
                               String sourceNo, String whCode) {
    }

    public record FbaReturnShelvesReq(String cellNo, String customerCode, String expressNo, String fnsku, String fritTaskNo, String logisticsCarrier,
                                      int productQuality, String productSku, int putawayQty, String sourceNo, String whCode) {
    }

    /** CreateClaimReq：productList 在 APK 里是 List&lt;Object&gt;，这里接受 {productSku/fnsku/barcode, goodQty|positiveQty, defectiveQty}。 */
    public record CreateClaimReq(List<Object> appendixList, String cellNo, String claimCustomerCode, String expressNo, Integer operateType,
                                 List<Map<String, Object>> productList, String remark, String whCode) {
    }

    public record PostTaskOrderReq(String cellNo, String customerCode, String fnsku, String frstTaskNo, int pickQty, String whCode) {
    }

    public record FinishPickTaskReq(String acceptLanguage, String customerCode, String frstTaskNo, String whCode) {
    }

    private final FbaReturnStore store;
    private final ReturnStore returns;
    private final StockStore stock;
    private final PutawayStore putaway;

    public FbaReturnController(FbaReturnStore store, ReturnStore returns, StockStore stock, PutawayStore putaway) {
        this.store = store;
        this.returns = returns;
        this.stock = stock;
        this.putaway = putaway;
    }

    // ---------- 视图 ----------

    private static int parse(String v, String label) {
        if (v == null || v.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new BizException(label + "格式错误：" + v);
        }
    }

    private static int nz(Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number n) {
            return n.intValue();
        }
        return parse(v.toString(), "数量");
    }

    /** FbaFnSkuBean */
    private Map<String, Object> fnskuBean(Line l) {
        Product p = stock.product(l.productSku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fnsku", l.fnsku);
        m.put("inventoryQty", l.good + l.bad);
        m.put("isNewAdd", l.newAdd);
        m.put("productName", p.name());
        m.put("productQuality", 1);
        m.put("productSku", l.productSku);
        m.put("putAwayQty", l.putGood + l.putBad);
        m.put("realReceiveQtyNum", l.good + l.bad);
        m.put("receiptQtyBad", l.bad);
        m.put("receiptQtyGood", l.good);
        m.put("receiptQtyNum", l.good + l.bad);
        m.put("recommendCell", putaway.recommendCell(l.productSku));
        m.put("returnQty", l.returnQty);
        m.put("skuId", l.productSku);
        return m;
    }

    /** ListInventoryFnSkuBean */
    private Map<String, Object> taskBean(Task t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("appendixList", List.of());
        m.put("customerCode", t.customerCode);
        m.put("customerName", t.customerName);
        m.put("expressNo", t.expressNo);
        m.put("fnSkuList", t.lines.values().stream().map(this::fnskuBean).toList());
        m.put("fritTaskNo", t.fritTaskNo);
        m.put("logisticsCarrier", t.logisticsCarrier);
        m.put("orderId", t.fritTaskNo);
        m.put("pushed", 0);
        m.put("sourceNo", t.sourceNo);
        m.put("status", t.status);
        m.put("temporaryCellNo", t.temporaryCellNo);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        m.put("whName", "示例仓库1");
        return m;
    }

    /** FbaReturnSignBean */
    private static Map<String, Object> signBean(Task t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", t.customerCode);
        m.put("customerName", t.customerName);
        m.put("expressNo", t.expressNo);
        m.put("fritTaskNo", t.fritTaskNo);
        m.put("logisticsCarrier", t.logisticsCarrier);
        m.put("sourceNo", t.sourceNo);
        m.put("temporaryCellNo", t.temporaryCellNo);
        m.put("whCode", StockStore.WH);
        return m;
    }

    /** ListTaskOrderBean */
    private Map<String, Object> pickBean(PickTask p) {
        Map<String, Map<String, Object>> byFnsku = new LinkedHashMap<>();
        for (PickLine l : p.lines) {
            Map<String, Object> f = byFnsku.computeIfAbsent(l.fnsku, k -> {
                Map<String, Object> n = new LinkedHashMap<>();
                n.put("fnsku", k);
                n.put("handleQty", 0);
                n.put("isExpand", false);
                n.put("pickQty", 0);
                n.put("productName", stock.product(l.productSku).name());
                n.put("productSku", l.productSku);
                n.put("trendingCellNoList", new ArrayList<Map<String, Object>>());
                return n;
            });
            f.put("handleQty", (int) f.get("handleQty") + l.pickQty);
            f.put("pickQty", (int) f.get("pickQty") + l.planQty);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cells = (List<Map<String, Object>>) f.get("trendingCellNoList");
            cells.add(Map.of("cellNo", l.cellNo, "pickQty", l.planQty - l.pickQty));
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", p.customerCode);
        m.put("customerName", p.customerName);
        m.put("fnskuList", new ArrayList<>(byFnsku.values()));
        m.put("frstTaskNo", p.frstTaskNo);
        m.put("pickStatus", p.pickStatus);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return m;
    }

    // ---------- 基础 ----------

    /** 客户列表（CustomerBean），两个权限字段为占位值。 */
    @GetMapping("/customerList")
    public R<List<Map<String, Object>>> customerList(@RequestParam(required = false) String whCode) {
        List<Map<String, Object>> out = new ArrayList<>();
        stock.customers().forEach((cc, cn) -> out.add(Map.of("createFBAReturnInOrderPermission", true, "createReturnOrderPermission", true,
                "customerCode", cc, "customerName", cn)));
        return R.ok(out);
    }

    @GetMapping("/scanFnsku")
    public R<Map<String, Object>> scanFnsku(@RequestParam(required = false) String customerCode, @RequestParam String fnsku,
                                            @RequestParam(required = false) String whCode) {
        Product p = store.scanFnsku(fnsku, customerCode);
        boolean forecast = store.list().stream().anyMatch(t -> t.status < 30 && t.lines.containsKey(p.fnsku()) && t.lines.get(p.fnsku()).returnQty > 0);
        return R.ok(Map.of("fnsku", p.fnsku(), "forecast", forecast, "productName", p.name(), "productSku", p.sku(), "skuId", p.sku()));
    }

    /** 校验库位，返回库位号。 */
    @GetMapping("/scanCell")
    public R<String> scanCell(@RequestParam String cellNo, @RequestParam(required = false) String whCode) {
        stock.requireCell(cellNo, true);
        return R.ok(cellNo.trim());
    }

    // ---------- 签收 ----------

    @PostMapping("/confirmSign")
    public R<Object> confirmSign(@RequestBody FbaConfirmSignReq req) {
        store.confirmSign(req.trackNo(), req.customerCode(), req.temporaryCellNo());
        return R.ok(null);
    }

    @GetMapping("/signDetail")
    public R<Map<String, Object>> signDetail(@RequestParam(required = false) String customerCode, @RequestParam String fritTaskNo,
                                             @RequestParam(required = false) String whCode) {
        Task t = store.get(fritTaskNo);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", t.customerCode);
        m.put("customerName", t.customerName);
        m.put("expressNo", t.expressNo);
        m.put("fritTaskNo", t.fritTaskNo);
        m.put("logisticsCarrier", t.logisticsCarrier);
        m.put("remark", t.remark);
        m.put("skuList", t.lines.values().stream().map(l -> Map.of("fnsku", l.fnsku, "productName", stock.product(l.productSku).name(),
                "returnQty", l.returnQty, "sku", l.productSku)).toList());
        m.put("temporaryCellNo", t.temporaryCellNo);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    @PostMapping("/updateStatusSign")
    public R<Object> updateStatusSign(@RequestBody UpdateStatusSign req) {
        List<String> fnskus = req.signSkuList() == null ? List.of() : req.signSkuList().stream().map(SignSku::fnsku).toList();
        store.updateSign(req.fritTaskNo(), fnskus, req.temporaryCellNo());
        return R.ok(null);
    }

    // ---------- 清点 ----------

    @GetMapping("/listInventoryFnSku")
    public R<Map<String, Object>> listInventoryFnSku(@RequestParam(required = false) String customerCode, @RequestParam(required = false) String expressNo,
                                                     @RequestParam(required = false) String fritTaskNo, @RequestParam(required = false) String logisticsCarrier,
                                                     @RequestParam(required = false) String whCode) {
        return R.ok(taskBean(store.find(fritTaskNo, expressNo, null)));
    }

    @GetMapping("/addFnSku")
    public R<Map<String, Object>> addFnSku(@RequestParam(required = false) String customerCode, @RequestParam(required = false) String expressNo,
                                           @RequestParam(required = false) String fritTaskNo, @RequestParam String fnsku,
                                           @RequestParam(required = false) String whCode) {
        Task t = store.find(fritTaskNo, expressNo, null);
        return R.ok(fnskuBean(store.addFnsku(t.fritTaskNo, fnsku)));
    }

    @PostMapping("/updateStatusInventory")
    public R<Map<String, Object>> updateStatusInventory(@RequestBody UpdateStatusInventoryReq req) {
        Map<String, int[]> qty = new LinkedHashMap<>();
        if (req.detailList() != null) {
            for (InventoryDetail d : req.detailList()) {
                int[] v = qty.computeIfAbsent(d.fnsku(), k -> new int[2]);
                v[0] += parse(d.receiptQtyGood(), "良品数量");
                v[1] += parse(d.receiptQtyBad(), "次品数量");
            }
        }
        Task t = store.find(req.fritTaskNo(), req.expressNo(), null);
        store.inventory(t.fritTaskNo, qty, req.receiptRemark(), req.temporaryCellNo());
        return R.ok(Map.of("fritTaskNo", t.fritTaskNo, "status", t.status));
    }

    /** 无预报包裹：签收并清点。 */
    @PostMapping("/signAndInventory")
    public R<Object> signAndInventory(@RequestBody InventoryPreCheckReq req) {
        Map<String, int[]> qty = new LinkedHashMap<>();
        if (req.productList() != null) {
            for (PreCheckProduct p : req.productList()) {
                String code = p.fnsku() != null && !p.fnsku().isBlank() ? p.fnsku() : p.productSku();
                Product prod = stock.findProduct(code);
                if (prod == null) {
                    throw new BizException("未识别的 FNSKU/SKU：" + code);
                }
                int[] v = qty.computeIfAbsent(prod.fnsku(), k -> new int[2]);
                v[0] += nz(p.positiveQty());
                v[1] += nz(p.defectiveQty());
            }
        }
        store.signAndInventory(req.customerCode(), req.expressNo(), req.logisticsCarrier(), qty, req.temporaryCellNo(), req.receiptRemark());
        return R.ok(null);
    }

    // ---------- 上架 ----------

    @GetMapping("/selectPutAwayList")
    public R<List<Map<String, Object>>> selectPutAwayList(@RequestParam(required = false) String whCode) {
        return R.ok(store.list().stream().filter(t -> t.status == 30).map(FbaReturnController::signBean).toList());
    }

    /** 按任务号 / 快递单号 / 来源单号找待上架任务。 */
    @GetMapping("/selectPutAway")
    public R<List<Map<String, Object>>> selectPutAway(@RequestParam String no, @RequestParam(required = false) String whCode) {
        Task t = store.find(no, no, no);
        if (t.status != 30) {
            throw new BizException("任务 " + t.fritTaskNo + " 当前状态为" + FbaReturnStore.STATUS.get(t.status) + "，不是待上架");
        }
        return R.ok(List.of(signBean(t)));
    }

    @GetMapping("/listPutAwayFnSku")
    public R<Map<String, Object>> listPutAwayFnSku(@RequestParam(required = false) String customerCode, @RequestParam(required = false) String expressNo,
                                                   @RequestParam(required = false) String fritTaskNo, @RequestParam(required = false) String logisticsCarrier,
                                                   @RequestParam(required = false) String sourceNo, @RequestParam(required = false) String whCode) {
        return R.ok(taskBean(store.find(fritTaskNo, expressNo, sourceNo)));
    }

    /** 某 FNSKU 按品质的待上架数与推荐库位（List&lt;ProductQualityBean&gt;），次品推荐 E 区。 */
    @GetMapping("/selectProductQuality")
    public R<List<Map<String, Object>>> selectProductQuality(@RequestParam(required = false) String expressNo, @RequestParam(required = false) String customerCode,
                                                             @RequestParam(required = false) String fritTaskNo, @RequestParam(required = false) String logisticsCarrier,
                                                             @RequestParam(required = false) String sourceNo, @RequestParam String fnsku,
                                                             @RequestParam(required = false) String whCode) {
        Task t = store.find(fritTaskNo, expressNo, sourceNo);
        Line l = t.lines.get(fnsku);
        if (l == null) {
            throw new BizException("FNSKU 不在任务中：" + fnsku);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (int q : new int[] {1, 2}) {
            if (l.wait(q) > 0) {
                out.add(Map.of("fnsku", l.fnsku, "productQuality", q, "productQualityName", StockStore.QUALITY.get(q), "productSku", l.productSku,
                        "receiptQty", l.wait(q), "recommendCell", q == 2 ? "E-01-01" : putaway.recommendCell(l.productSku)));
            }
        }
        return R.ok(out);
    }

    /** 判断库位能否放该品质的货。 */
    @PostMapping("/judgeCell")
    public R<Object> judgeCell(@RequestBody JudgeCallReq req) {
        stock.requireQualityCell(req.cellNo(), req.productQuality());
        return R.ok(null);
    }

    /** FBA 退货上架（FbaReturnShelvesStatus：putAwayStatus 1 表示整单上架完成）。 */
    @PostMapping("/updateStatusShelf")
    public R<Map<String, Object>> updateStatusShelf(@RequestBody FbaReturnShelvesReq req) {
        Task t = store.find(req.fritTaskNo(), req.expressNo(), req.sourceNo());
        store.putaway(t.fritTaskNo, req.fnsku(), req.productQuality(), req.putawayQty(), req.cellNo());
        return R.ok(Map.of("fritTaskNo", t.fritTaskNo, "putAwayStatus", t.status == 40 ? 1 : 0));
    }

    // ---------- 认领 ----------

    /** 认领扫码：返回条码对应的产品（List&lt;ClaimBarcodeBean&gt;）。带了客户就只认该客户的产品。 */
    @GetMapping("/claim/scanBarcode")
    public R<List<Map<String, Object>>> scanClaimBarcode(@RequestParam String scanContent, @RequestParam(required = false) String customerCode,
                                                         @RequestParam(required = false) String whCode) {
        Product p = stock.findProduct(scanContent);
        if (p == null || (customerCode != null && !customerCode.isBlank() && !p.customerCode().equals(customerCode.trim()))) {
            throw new BizException("未找到可认领的产品：" + scanContent);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("barcode", p.barcode());
        m.put("createFBAReturnInOrderPermission", true);
        m.put("createReturnOrderPermission", true);
        m.put("customerCode", p.customerCode());
        m.put("customerName", p.customerName());
        m.put("defectiveQty", 0);
        m.put("fnsku", p.fnsku());
        m.put("forecastQty", 0);
        m.put("goodQty", 0);
        m.put("picUrl", "");
        m.put("productName", p.name());
        m.put("productSku", p.sku());
        m.put("receiveQty", 0);
        m.put("skuId", p.sku());
        return R.ok(List.of(m));
    }

    /** 认领入库：为认领客户建一张待上架退件单（orderType=4），之后走 /return/putaway 上架。operateType 本地不区分。 */
    @PostMapping("/createClaim")
    public R<Object> createClaim(@RequestBody CreateClaimReq req) {
        Map<String, int[]> qty = new LinkedHashMap<>();
        if (req.productList() != null) {
            for (Map<String, Object> p : req.productList()) {
                Object code = p.getOrDefault("productSku", p.getOrDefault("fnsku", p.get("barcode")));
                Product prod = code == null ? null : stock.findProduct(code.toString());
                if (prod == null) {
                    throw new BizException("未识别的产品：" + code);
                }
                int[] v = qty.computeIfAbsent(prod.sku(), k -> new int[2]);
                v[0] += nz(p.getOrDefault("goodQty", p.get("positiveQty")));
                v[1] += nz(p.get("defectiveQty"));
            }
        }
        List<String[]> pkgs = new ArrayList<>();
        pkgs.add(new String[] {req.expressNo(), req.cellNo()});
        returns.create(req.claimCustomerCode(), 4, pkgs, qty, req.remark());
        return R.ok(null);
    }

    // ---------- 退货拣货 ----------

    @GetMapping("/pick/listTaskOrder")
    public R<List<Map<String, Object>>> listTaskOrder(@RequestParam(required = false) String whCode) {
        return R.ok(store.pickTasks().stream().map(this::pickBean).toList());
    }

    @GetMapping("/pick/selectTaskOrder")
    public R<Map<String, Object>> selectTaskOrder(@RequestParam String frstTaskNo, @RequestParam(required = false) String whCode) {
        return R.ok(pickBean(store.pickTask(frstTaskNo)));
    }

    /** 校验拣货库位：库位上要有该客户的货。 */
    @GetMapping("/pick/verifyCellNo")
    public R<Object> verifyCellNo(@RequestParam String cellNo, @RequestParam(required = false) String customerCode,
                                  @RequestParam(required = false) String whCode) {
        stock.requireCell(cellNo, false);
        if (stock.find(cellNo, customerCode, null, null, null).isEmpty()) {
            throw new BizException("库位 " + cellNo + " 没有该客户的库存");
        }
        return R.ok(null);
    }

    /** 在库位上扫 FNSKU，返回该库位还要拣的数量（FbaReturnVerifyBarcode）。 */
    @GetMapping("/pick/verifyBarcode")
    public R<Map<String, Object>> verifyBarcode(@RequestParam String cellNo, @RequestParam(required = false) String customerCode,
                                                @RequestParam String fnsku, @RequestParam String frstTaskNo, @RequestParam(required = false) String whCode) {
        PickTask p = store.pickTask(frstTaskNo);
        Product prod = stock.findProduct(fnsku);
        PickLine l = store.pickLine(p, prod == null ? fnsku : prod.fnsku(), cellNo);
        return R.ok(Map.of("fnsku", l.fnsku, "pickQty", l.planQty - l.pickQty));
    }

    @PostMapping("/pick/postTaskOrder")
    public R<Map<String, Object>> postTaskOrder(@RequestBody PostTaskOrderReq req) {
        Product prod = stock.findProduct(req.fnsku());
        PickTask p = store.pick(req.frstTaskNo(), prod == null ? req.fnsku() : prod.fnsku(), req.cellNo(), req.pickQty());
        return R.ok(Map.of("frstTaskNo", p.frstTaskNo, "pickStatus", p.pickStatus));
    }

    @PostMapping("/pick/finishTaskOrder")
    public R<Object> finishTaskOrder(@RequestBody FinishPickTaskReq req) {
        store.finishPick(req.frstTaskNo());
        return R.ok(null);
    }
}
