package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.review.Review4packageReq;
import com.xlwms.rebuild.dto.review.ReviewConfirmDeliveryReq;
import com.xlwms.rebuild.dto.review.ReviewMorePieceReq;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
import com.xlwms.rebuild.service.OutboundSampleStore.Pack;
import com.xlwms.rebuild.service.OutboundSampleStore.PackItem;
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
 * PDA 出库复核接口（/gateway/wms/pda/review/*）。路径、参数、请求体按 APK ApiService 还原，
 * 响应字段按 APK 的 PiecePackageBean / MorePieceBean / PutAwayStatus 还原。
 * 数据来自 {@link OutboundSampleStore}，全部虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/review")
public class ReviewController {

    private final OutboundSampleStore store;

    public ReviewController(OutboundSampleStore store) {
        this.store = store;
    }

    /** 按包裹复核：扫出库单号或快递单号，返回包裹列表与当前待复核包裹。 */
    @GetMapping("/get4piecePackage")
    public R<Map<String, Object>> get4piecePackage(@RequestParam(required = false) String deliveryNo,
                                                   @RequestParam(required = false) String expressNo,
                                                   @RequestParam(required = false) Integer finishReview,
                                                   @RequestParam(required = false) String customerCode,
                                                   @RequestParam(required = false) String whCode) {
        Delivery d = store.findDelivery(deliveryNo, expressNo);
        List<Map<String, Object>> packs = d.packs.stream().map(p -> pack(d, p)).toList();
        Map<String, Object> m = head(d);
        m.put("logisticsChannel", d.logisticsChannel);
        m.put("packageList", packs);
        m.put("packageNum", d.packs.size());
        m.put("piecePackage", d.packs.stream().filter(p -> p.reviewStatus == 0).findFirst()
                .map(p -> pack(d, p)).orElse(packs.get(0)));
        m.put("reviewPackageNum", (int) d.packs.stream().filter(p -> p.reviewStatus == 1).count());
        m.put("reviewSkuNum", d.scanNum());
        m.put("skuNum", d.skuNum());
        return R.ok(m);
    }

    @PostMapping("/review4packageV2")
    public R<Map<String, Object>> review4package(@RequestBody Review4packageReq req) {
        boolean all = store.review(req.deliveryNo(), req.packNo(), req.oneClickPass(), req.reviewList());
        return R.ok(putAwayStatus(all));
    }

    /**
     * 一票多件复核前取客户与快递信息（CustomerCode4More：delivery / express / orderType / outBoundList / tenantCode / whCode）。
     * orderType 编码是本项目假设（1 一票一件 / 2 一票多件，按出库单的件数判断）；tenantCode 为占位值。
     */
    @GetMapping("/getCustomerCode4More")
    public R<Map<String, Object>> getCustomerCode4More(@RequestParam String deliveryNo, @RequestParam(required = false) String whCode) {
        Delivery d = store.lookup(deliveryNo);
        Map<String, Object> express = new LinkedHashMap<>();
        express.put("customerCode", d.customerCode);
        express.put("customerName", d.customerName);
        express.put("deliveryNo", d.deliveryNo);
        express.put("expressNo", d.expressNo);
        express.put("logisticsChannel", d.logisticsChannel);
        express.put("sourceNo", d.sourceNo);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("delivery", Map.of("customerCode", d.customerCode, "deliveryNo", d.deliveryNo));
        m.put("express", List.of(express));
        m.put("orderType", d.skuNum() > 1 ? 2 : 1);
        m.put("outBoundList", List.of());
        m.put("tenantCode", "DEMO");
        m.put("whCode", OutboundSampleStore.WH);
        return R.ok(m);
    }

    /** 一票多件复核：返回整单 SKU 汇总与已扫数量。 */
    @GetMapping("/get4MorePiece")
    public R<Map<String, Object>> get4MorePiece(@RequestParam String deliveryNo,
                                                @RequestParam(required = false) Integer finishReview,
                                                @RequestParam(required = false) String customerCode,
                                                @RequestParam(required = false) String whCode) {
        Delivery d = store.findDelivery(deliveryNo, null);
        Map<String, Map<String, Object>> skus = new LinkedHashMap<>();
        for (Pack p : d.packs) {
            for (PackItem i : p.items) {
                Map<String, Object> s = skus.computeIfAbsent(i.sku, k -> {
                    Map<String, Object> n = new LinkedHashMap<>();
                    n.put("allQty", 0);
                    n.put("barcode", i.barcode);
                    n.put("needPack", 0);
                    n.put("productCodeList", List.of());
                    n.put("productName", i.productName);
                    n.put("productPicVO", null);
                    n.put("productSku", i.sku);
                    n.put("productSnFlag", false);
                    n.put("productSnList", List.of());
                    n.put("productSnNoList", List.of());
                    n.put("recommendedPackMaterial", null);
                    n.put("recordScanQty", 0);
                    n.put("scanQty", 0);
                    return n;
                });
                s.put("allQty", (int) s.get("allQty") + i.qty);
                s.put("scanQty", (int) s.get("scanQty") + i.scanQty);
                s.put("recordScanQty", s.get("scanQty"));
            }
        }
        Map<String, Object> m = head(d);
        m.put("expressPrintStatus", "1");
        m.put("expressPrintStatusName", "已打印");
        m.put("fileKey", null);
        m.put("fileName", null);
        m.put("orderSkuAllQty", d.skuNum());
        m.put("orderStatus", d.status);
        m.put("scanOrderSkuQty", d.scanNum());
        m.put("skuList", List.copyOf(skus.values()));
        m.put("skuTypeQty", skus.size());
        m.put("status", d.status);
        return R.ok(m);
    }

    @PostMapping("/morePieceV2")
    public R<Map<String, Object>> morePiece(@RequestBody ReviewMorePieceReq req) {
        boolean all = store.review(req.deliveryNo(), null, req.oneClickPass(), req.reviewList());
        return R.ok(putAwayStatus(all));
    }

    /** 复核后确认出库（真实系统是写操作；这里只改内存示例数据）。 */
    @PostMapping("/confirmDelivery")
    public R<Object> confirmDelivery(@RequestBody ReviewConfirmDeliveryReq req) {
        if (req.deliveryNo() == null || req.deliveryNo().isBlank()) {
            throw new BizException("出库单号不能为空");
        }
        Delivery d = store.confirmDelivery(req.deliveryNo());
        return R.ok(Map.of("deliveryNo", d.deliveryNo, "status", d.status));
    }

    private static Map<String, Object> head(Delivery d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", d.customerCode);
        m.put("customerName", d.customerName);
        m.put("deliveryNo", d.deliveryNo);
        m.put("expressNo", d.expressNo);
        m.put("oneClickPassEnabled", true);
        m.put("sourceNo", d.sourceNo);
        m.put("tenantCode", WaveController.TENANT);
        m.put("whCode", OutboundSampleStore.WH);
        return m;
    }

    private static Map<String, Object> pack(Delivery d, Pack p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("allQty", p.items.stream().mapToInt(i -> i.qty).sum());
        m.put("customerCode", d.customerCode);
        m.put("deliveryNo", d.deliveryNo);
        m.put("expressNo", d.expressNo);
        m.put("expressPrintStatus", "1");
        m.put("fileKey", null);
        m.put("fileName", null);
        m.put("mergeLabel", false);
        m.put("packNo", p.packNo);
        m.put("packSerial", p.packSerial);
        m.put("reviewStatus", p.reviewStatus);
        m.put("scanQty", p.items.stream().mapToInt(i -> i.scanQty).sum());
        m.put("skuList", p.items.stream().map(i -> {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("barcode", i.barcode);
            s.put("otherBarcodeList", List.of());
            s.put("productCodeList", List.of());
            s.put("productName", i.productName);
            s.put("productPicVO", null);
            s.put("productSnFlag", false);
            s.put("productSnList", List.of());
            s.put("productSnNoList", List.of());
            s.put("qty", i.qty);
            s.put("recordScanQty", i.scanQty);
            s.put("scanQty", i.scanQty);
            s.put("sku", i.sku);
            return s;
        }).toList());
        m.put("sourceNo", d.sourceNo);
        return m;
    }

    private static Map<String, Object> putAwayStatus(boolean allReviewed) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("deviceSpecialCell", false);
        m.put("putAwayStatus", allReviewed ? 1 : 0);
        m.put("putawayNo", null);
        m.put("quickOutbound", false);
        return m;
    }
}
