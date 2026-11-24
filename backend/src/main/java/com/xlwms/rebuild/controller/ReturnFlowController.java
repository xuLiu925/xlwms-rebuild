package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.returnputaway.ReturnPutawayScanReq;
import com.xlwms.rebuild.dto.returnputaway.ShelvesInfoReq;
import com.xlwms.rebuild.service.PutawayStore;
import com.xlwms.rebuild.service.ReturnStore;
import com.xlwms.rebuild.service.ReturnStore.Line;
import com.xlwms.rebuild.service.ReturnStore.Order;
import com.xlwms.rebuild.service.ReturnStore.Pkg;
import com.xlwms.rebuild.service.StockStore;
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
 * PDA 退件：/gateway/wms/pda/return/receive/**（11）、/return/create/**（6 个方法，commitOrder 两个方法共用路径）、
 * /return/putaway/**（7，含 destruct）。路径、参数名、请求体与响应字段取自 APK；规则见 {@link ReturnStore}。
 * putaway 的请求体沿用 dto/returnputaway 下批次 1 的类。数据虚构，不连生产；destruct 只在本地内存里扣减待处理数。
 */
@RestController
public class ReturnFlowController {

    // ---------- 请求体 ----------

    public record PkgReq(String packNo, String trackNo, String reach, String tempCellNo) {
    }

    public record SkuReq(String productSku, Integer goodQty, Integer defectiveQty) {
    }

    /** ReceiptMailSubmit（只取用到的字段） */
    public record ReceiptMailSubmit(String customerCode, String deliveryNo, String orderNo, Integer orderType, List<PkgReq> packageList,
                                    String remark, List<SkuReq> skuList, String whCode) {
    }

    public record ScanProductSnNoReq(String customerCode, String productSku, String productSnNo, String remark, String returnNo, String whCode) {
    }

    public record ReceiveScanBarcodeReq(String barcode, String customerCode, String tenantCode, String whCode) {
    }

    public record ReturnCreateTypeReq(Boolean includePlatformReturn, String trackNo, String whCode) {
    }

    public record UpdateLastOrderTypeReq(int tab, String whCode) {
    }

    public record CommitPackage(String trackNo, String tempCellNo, Double weight, Double length, Double width, Double height, Integer reach) {
    }

    /** CommitOrderReq 与 PlatformReturnCommitReq 共用路径，这里合并字段；platformReturn 本地不支持。 */
    public record CommitOrderReq(String customerCode, Integer orderType, List<CommitPackage> packageList, List<SkuReq> productList,
                                 Map<String, Object> platformReturn, String remark, String whCode) {
    }

    public record DestructSubmit(Integer qty) {
    }

    /** DestructReq（只取用到的字段） */
    public record DestructReq(String barcode, String customerCode, String handleRemark, String productName, List<DestructSubmit> putawayExpirationSubmitDTOList,
                              Integer qty, String returnNo, String skuId, String whCode) {
    }

    private final ReturnStore returns;
    private final StockStore stock;
    private final PutawayStore putaway;

    public ReturnFlowController(ReturnStore returns, StockStore stock, PutawayStore putaway) {
        this.returns = returns;
        this.stock = stock;
        this.putaway = putaway;
    }

    // ---------- 视图 ----------

    /** ReturnReceiptOrder / ReturnPutawayOrder（字段基本相同；后者 status 为字符串、多 deviceStatus）。 */
    private static Map<String, Object> orderView(Order o, boolean putawayView) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", o.customerCode);
        m.put("customerName", o.customerName);
        m.put("deliveryNo", o.deliveryNo);
        if (putawayView) {
            m.put("deviceStatus", "");
        }
        m.put("expressNo", o.expressNo);
        m.put("fritTaskNo", "");
        m.put("logisticsCarrier", "");
        m.put("orderNo", o.orderNo);
        m.put("orderType", o.orderType);
        m.put("removeNo", "");
        m.put("status", putawayView ? String.valueOf(o.status) : o.status);
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return m;
    }

    /** ReturnSkuRep.Record */
    private Map<String, Object> skuRecord(String sku, Line l) {
        Product p = stock.product(sku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("barcode", p.barcode());
        m.put("customerRemark", "");
        m.put("defectiveQty", l == null ? 0 : l.defectiveQty);
        m.put("expirationStatus", 0);
        m.put("expiryTime", "");
        m.put("fnsku", p.fnsku());
        m.put("forecast", l != null && l.forecastQty > 0);
        m.put("forecastQty", l == null ? 0 : l.forecastQty);
        m.put("goodQty", l == null ? 0 : l.goodQty);
        m.put("isDoneExpiry", false);
        m.put("isExpanded", false);
        m.put("picUrl", "");
        m.put("productName", p.name());
        m.put("productPicVO", null);
        m.put("productSku", sku);
        m.put("productSnFlag", false);
        m.put("productSnList", List.of());
        m.put("productSnNoList", List.of());
        m.put("productionTime", "");
        m.put("receiveQty", l == null ? 0 : l.goodQty + l.defectiveQty);
        m.put("skuId", sku);
        return m;
    }

    /** ReceiptListPackage.Pkg（尺寸重量本地不记录，返回空串）。 */
    private static Map<String, Object> pkgView(Pkg p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("appendixList", List.of());
        m.put("attachment", null);
        m.put("currentSizeUnitCn", true);
        for (String k : List.of("height", "heightBs", "length", "lengthBs", "weight", "weightBs", "width", "widthBs")) {
            m.put(k, "");
        }
        m.put("isChecked", p.reached);
        m.put("logisticsCarrierName", "");
        m.put("packMark", "");
        m.put("packNo", p.packNo);
        m.put("reach", p.reached ? "1" : "0");
        m.put("remark", "");
        m.put("sizeUnit", "cm");
        m.put("sizeUnitBs", "in");
        m.put("tempCellNo", p.tempCellNo);
        m.put("tempCellNoPass", p.tempCellNo.isEmpty() ? 0 : 1);
        m.put("tempCellNoTemp", "");
        m.put("trackNo", p.trackNo);
        m.put("weightUnit", "kg");
        m.put("weightUnitBs", "lb");
        return m;
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    // ---------- 收货 ----------

    @GetMapping("/gateway/wms/pda/return/receive/listOrder")
    public R<Map<String, Object>> receiveListOrder(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(required = false) String whCode) {
        return R.ok(PageUtil.page(returns.list(10).stream().map(o -> orderView(o, false)).toList(), current, size));
    }

    @GetMapping("/gateway/wms/pda/return/receive/scanOrderNo")
    public R<List<Map<String, Object>>> receiveScanOrderNo(@RequestParam String scanContent, @RequestParam(required = false) String whCode) {
        List<Map<String, Object>> out = returns.searchNo(scanContent, 10).stream().map(o -> orderView(o, false)).toList();
        if (out.isEmpty()) {
            throw new BizException("没有待收货的退件单：" + scanContent);
        }
        return R.ok(out);
    }

    /** 扫快递单号或产品条码找待收货退件单。 */
    @GetMapping("/gateway/wms/pda/return/receive/scanBarcode")
    public R<List<Map<String, Object>>> receiveScanBarcode(@RequestParam String scanContent, @RequestParam(required = false) String whCode) {
        List<Order> hit = returns.searchNo(scanContent, 10);
        if (hit.isEmpty()) {
            hit = returns.searchProduct(scanContent, 10);
        }
        if (hit.isEmpty()) {
            throw new BizException("没有待收货的退件单：" + scanContent);
        }
        return R.ok(hit.stream().map(o -> orderView(o, false)).toList());
    }

    @GetMapping("/gateway/wms/pda/return/receive/detail")
    public R<Map<String, Object>> receiveDetail(@RequestParam(required = false) String customerCode, @RequestParam(required = false) String expressNo,
                                                @RequestParam String orderNo, @RequestParam(required = false) String customerName,
                                                @RequestParam(required = false) String whCode) {
        Order o = returns.get(orderNo);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("appendixList", List.of());
        m.put("customerCode", o.customerCode);
        m.put("customerName", o.customerName);
        m.put("deliveryNo", o.deliveryNo);
        m.put("expressNo", o.expressNo);
        m.put("orderNo", o.orderNo);
        m.put("orderType", o.orderType);
        m.put("platformReturnSource", null);
        m.put("receiveRemark", o.remark);
        m.put("referOrderNo", o.deliveryNo);
        m.put("remark", "");
        m.put("removeNo", "");
        m.put("tenantCode", "DEMO");
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    @GetMapping("/gateway/wms/pda/return/receive/listProduct")
    public R<Map<String, Object>> listProduct(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String customerCode, @RequestParam String orderNo,
                                              @RequestParam(required = false) String whCode) {
        Order o = returns.get(orderNo);
        List<Map<String, Object>> rows = new ArrayList<>();
        o.lines.forEach((sku, l) -> rows.add(skuRecord(sku, l)));
        return R.ok(PageUtil.page(rows, current, size));
    }

    @GetMapping("/gateway/wms/pda/return/receive/listPackage")
    public R<Map<String, Object>> listPackage(@RequestParam(required = false) String customerCode, @RequestParam String orderNo,
                                              @RequestParam(required = false) String whCode) {
        Order o = returns.get(orderNo);
        long reached = o.packages.stream().filter(p -> p.reached).count();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("allQty", String.valueOf(o.packages.size()));
        m.put("pkgList", o.packages.stream().map(ReturnFlowController::pkgView).toList());
        m.put("waitReachQty", String.valueOf(o.packages.size() - reached));
        return R.ok(m);
    }

    /** 收货时扫产品：返回该产品在单内的行；预报外的产品返回 forecast=false 的空行（客户必须一致）。 */
    @GetMapping("/gateway/wms/pda/return/receive/detail/scanBarcode")
    public R<Map<String, Object>> detailScanBarcode(@RequestParam(required = false) String customerCode, @RequestParam String scanContent,
                                                    @RequestParam String orderNo, @RequestParam(required = false) String deliveryNo,
                                                    @RequestParam(required = false) String whCode) {
        Order o = returns.get(orderNo);
        Product p = stock.findProduct(scanContent);
        if (p == null) {
            throw new BizException("未识别的条码：" + scanContent);
        }
        if (!p.customerCode().equals(o.customerCode)) {
            throw new BizException("产品 " + p.sku() + " 不属于退件单的客户 " + o.customerCode);
        }
        return R.ok(skuRecord(p.sku(), o.lines.get(p.sku())));
    }

    @GetMapping("/gateway/wms/pda/return/receive/detail/scanExpressNo")
    public R<Map<String, Object>> detailScanExpressNo(@RequestParam(required = false) String customerCode, @RequestParam String scanContent,
                                                      @RequestParam String orderNo, @RequestParam(required = false) String deliveryNo,
                                                      @RequestParam(required = false) String whCode) {
        Order o = returns.get(orderNo);
        return R.ok(o.packages.stream().filter(p -> p.trackNo.equals(scanContent.trim()) || p.packNo.equals(scanContent.trim()))
                .findFirst().map(ReturnFlowController::pkgView)
                .orElseThrow(() -> new BizException("包裹不属于退件单 " + o.orderNo + "：" + scanContent)));
    }

    /** 校验暂存库位。 */
    @GetMapping("/gateway/wms/pda/return/receive/scanCell")
    public R<Object> receiveScanCell(@RequestParam String cellNo, @RequestParam(required = false) String whCode) {
        stock.requireCell(cellNo, true);
        return R.ok(null);
    }

    @PostMapping("/gateway/wms/pda/return/receive/scanProductSnNo")
    public R<Object> scanProductSnNo(@RequestBody ScanProductSnNoReq req) {
        returns.addSerial(req.returnNo(), req.productSku(), req.productSnNo());
        return R.ok(null);
    }

    @PostMapping("/gateway/wms/pda/return/receive/submit")
    public R<Object> receiveSubmit(@RequestBody ReceiptMailSubmit req) {
        Map<String, String[]> reached = new LinkedHashMap<>();
        if (req.packageList() != null) {
            for (PkgReq p : req.packageList()) {
                String key = p.packNo() != null && !p.packNo().isBlank() ? p.packNo() : p.trackNo();
                reached.put(key, new String[] {p.reach() == null ? "1" : p.reach(), p.tempCellNo()});
            }
        }
        Map<String, int[]> qty = new LinkedHashMap<>();
        if (req.skuList() != null) {
            for (SkuReq s : req.skuList()) {
                int[] v = qty.computeIfAbsent(s.productSku(), k -> new int[2]);
                v[0] += nz(s.goodQty());
                v[1] += nz(s.defectiveQty());
            }
        }
        returns.receive(req.orderNo(), reached, qty, req.remark());
        return R.ok(null);
    }

    // ---------- PDA 新建退件 ----------

    @GetMapping("/gateway/wms/pda/return/create/getLastOrderType")
    public R<Integer> getLastOrderType(@RequestParam(required = false) String whCode) {
        return R.ok(returns.lastOrderType());
    }

    @PostMapping("/gateway/wms/pda/return/create/updateLastOrderType")
    public R<Object> updateLastOrderType(@RequestBody UpdateLastOrderTypeReq req) {
        returns.setLastOrderType(req.tab());
        return R.ok(null);
    }

    /**
     * 扫跟踪号判断怎么建单（ReturnCreateTypeResp）。createType 本项目假设：1 新建 / 2 已有退件单（应走退件收货）。
     * 第二个参数在 APK 里是一个字符串请求头，名字未还原，这里不读取。
     */
    @PostMapping("/gateway/wms/pda/return/create/type")
    public R<Map<String, Object>> createType(@RequestBody ReturnCreateTypeReq req) {
        String trackNo = req.trackNo() == null ? "" : req.trackNo().trim();
        if (trackNo.isEmpty()) {
            throw new BizException("跟踪号不能为空");
        }
        Order existing = returns.byTrackNo(trackNo);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("createType", existing == null ? 1 : 2);
        m.put("customerCode", existing == null ? "" : existing.customerCode);
        m.put("customerName", existing == null ? "" : existing.customerName);
        m.put("platformReturn", List.of());
        m.put("platformReturnRows", List.of());
        m.put("trackNo", trackNo);
        return R.ok(m);
    }

    /** 新建退件时扫产品（ReceiveScanBarcodeResp）；带了客户就校验产品属于该客户。 */
    @PostMapping("/gateway/wms/pda/return/create/scanBarcode")
    public R<Map<String, Object>> createScanBarcode(@RequestBody ReceiveScanBarcodeReq req) {
        Product p = stock.findProduct(req.barcode());
        if (p == null) {
            throw new BizException("未识别的条码：" + req.barcode());
        }
        if (req.customerCode() != null && !req.customerCode().isBlank() && !p.customerCode().equals(req.customerCode().trim())) {
            throw new BizException("产品 " + p.sku() + " 不属于客户 " + req.customerCode());
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("defectiveQty", 0);
        m.put("fnsku", p.fnsku());
        m.put("goodQty", 0);
        m.put("productName", p.name());
        m.put("productSku", p.sku());
        m.put("skuId", p.sku());
        return R.ok(m);
    }

    /** 提交新建退件单，返回退件单号；新单直接为待上架。平台退货（platformReturn）本地不支持。 */
    @PostMapping("/gateway/wms/pda/return/create/commitOrder")
    // APK 里平台退货那个方法还带一个字符串请求头，名字未还原，这里不读取
    public R<String> commitOrder(@RequestBody CommitOrderReq req) {
        if (req.platformReturn() != null && (req.productList() == null || req.productList().isEmpty())) {
            throw new BizException("本地演示不支持平台退货建单");
        }
        List<String[]> pkgs = new ArrayList<>();
        if (req.packageList() != null) {
            req.packageList().forEach(p -> pkgs.add(new String[] {p.trackNo(), p.tempCellNo()}));
        }
        Map<String, int[]> qty = new LinkedHashMap<>();
        if (req.productList() != null) {
            for (SkuReq s : req.productList()) {
                int[] v = qty.computeIfAbsent(s.productSku(), k -> new int[2]);
                v[0] += nz(s.goodQty());
                v[1] += nz(s.defectiveQty());
            }
        }
        int type = req.orderType() == null ? 3 : req.orderType();
        return R.ok(returns.create(req.customerCode(), type, pkgs, qty, req.remark()).orderNo);
    }

    // ---------- 上架 / 销毁 ----------

    @GetMapping("/gateway/wms/pda/return/putaway/listOrder")
    public R<Map<String, Object>> putawayListOrder(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(required = false) String whCode) {
        return R.ok(PageUtil.page(returns.list(20).stream().map(o -> orderView(o, true)).toList(), current, size));
    }

    @PostMapping("/gateway/wms/pda/return/putaway/scanOrderNo")
    public R<List<Map<String, Object>>> putawayScanOrderNo(@RequestBody ReturnPutawayScanReq req) {
        List<Map<String, Object>> out = returns.searchNo(req.getScanContent(), 20).stream().map(o -> orderView(o, true)).toList();
        if (out.isEmpty()) {
            throw new BizException("没有待上架的退件单：" + req.getScanContent());
        }
        return R.ok(out);
    }

    @PostMapping("/gateway/wms/pda/return/putaway/scanBarcode")
    public R<List<Map<String, Object>>> putawayScanBarcode(@RequestBody ReturnPutawayScanReq req) {
        List<Order> hit = returns.searchProduct(req.getScanContent(), 20);
        if (hit.isEmpty()) {
            hit = returns.searchNo(req.getScanContent(), 20);
        }
        if (hit.isEmpty()) {
            throw new BizException("没有待上架的退件单：" + req.getScanContent());
        }
        return R.ok(hit.stream().map(o -> orderView(o, true)).toList());
    }

    /** 某退件单里某产品按品质的待上架明细（List&lt;ReturnShelvesDetail&gt;），cellNo 为推荐库位。 */
    @GetMapping("/gateway/wms/pda/return/putaway/detail/scanBarcode")
    public R<List<Map<String, Object>>> putawayDetail(@RequestParam(required = false) String whCode, @RequestParam String barcode,
                                                      @RequestParam(required = false) String customerCode, @RequestParam String returnNo) {
        Order o = returns.get(returnNo);
        Product p = stock.findProduct(barcode);
        Line l = p == null ? null : o.lines.get(p.sku());
        if (l == null) {
            throw new BizException("产品不在退件单 " + o.orderNo + " 中：" + barcode);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (int q : new int[] {1, 2}) {
            int total = q == 2 ? l.defectiveQty : l.goodQty;
            if (total == 0) {
                continue;
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("barcode", p.barcode());
            m.put("cellNo", q == 2 ? "E-01-01" : putaway.recommendCell(p.sku()));
            m.put("customerCode", o.customerCode);
            m.put("forecastQty", l.forecastQty);
            m.put("handleRemark", "");
            m.put("handlerAppendixList", List.of());
            m.put("productName", p.name());
            m.put("productQuality", q);
            m.put("putawayExpirationSubmitDTOList", List.of());
            m.put("putawayQty", total - l.wait(q));
            m.put("returnNo", o.orderNo);
            m.put("skuId", p.sku());
            m.put("tenantCode", "DEMO");
            m.put("waitPutawayQty", l.wait(q));
            m.put("weight", 0);
            m.put("weightBs", 0);
            m.put("weightBsUnit", "lb");
            m.put("weightUnit", "kg");
            m.put("whCode", StockStore.WH);
            out.add(m);
        }
        return R.ok(out);
    }

    /** 校验上架库位；次品只能上 E 区。 */
    @GetMapping("/gateway/wms/pda/return/putaway/scanCell")
    public R<Object> putawayScanCell(@RequestParam String cellNo, @RequestParam(defaultValue = "1") int productQuality,
                                     @RequestParam(required = false) String whCode) {
        stock.requireQualityCell(cellNo, productQuality);
        return R.ok(null);
    }

    /** 退件上架（ReturnPutawaySubmitResp）。有 putawayExpirationSubmitDTOList 时按其中的库位和数量分多个库位上架，否则用 cellNo + qty。 */
    @PostMapping("/gateway/wms/pda/return/putaway/submit")
    public R<Map<String, Object>> putawaySubmit(@RequestBody ShelvesInfoReq req) {
        Map<String, Integer> cells = new LinkedHashMap<>();
        if (req.getPutawayExpirationSubmitDTOList() != null && !req.getPutawayExpirationSubmitDTOList().isEmpty()) {
            req.getPutawayExpirationSubmitDTOList().forEach(s -> cells.merge(s.getCellNo(), s.getQty(), Integer::sum));
        } else {
            cells.put(req.getCellNo(), req.getQty());
        }
        String code = req.getBarcode() != null && !req.getBarcode().isBlank() ? req.getBarcode() : req.getSkuId();
        Order o = returns.putaway(req.getReturnNo(), code, req.getProductQuality() == null ? 1 : req.getProductQuality(), cells);
        return R.ok(Map.of("customerCode", o.customerCode, "returnNo", o.orderNo, "status", o.status));
    }

    /** 销毁次品（DestructResp）：只在本地内存中扣减待处理数，必须填处理说明。 */
    @PostMapping("/gateway/wms/pda/return/putaway/destruct")
    public R<Map<String, Object>> destruct(@RequestBody DestructReq req) {
        int qty = req.putawayExpirationSubmitDTOList() != null && !req.putawayExpirationSubmitDTOList().isEmpty()
                ? req.putawayExpirationSubmitDTOList().stream().mapToInt(d -> nz(d.qty())).sum() : nz(req.qty());
        String code = req.barcode() != null && !req.barcode().isBlank() ? req.barcode() : req.skuId();
        Order o = returns.destroy(req.returnNo(), code, qty, req.handleRemark());
        return R.ok(Map.of("customerCode", o.customerCode, "returnNo", o.orderNo, "status", o.status));
    }
}
