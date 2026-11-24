package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.FreightStore;
import com.xlwms.rebuild.service.FreightStore.FreightOrder;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 出库确认与大货复核：/gateway/wms/pda/delivery/**（8 个接口）。路径、参数名、请求体与响应字段取自 APK
 * （DeliveryConfirmReq、BulkDeliveryConfirmReq、ReviewBulkConfirmDeliveryReq、InvoiceInfoQueryBean、ScanRecheckOutBean）。
 * outboundType 编码是本项目假设：1 一件代发（{@link OutboundSampleStore}）/ 2 大货按箱（{@link FreightStore}）。
 * 数据虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/delivery")
public class PdaDeliveryController {

    public record DeliveryConfirmReq(List<String> deliveryNoList, String whCode) {
    }

    public record BulkDeliveryConfirmItem(String customerCode, String deliveryNo) {
    }

    public record BulkDeliveryConfirmReq(List<BulkDeliveryConfirmItem> confirmDeliveryDTOList, Integer pallets, String whCode) {
    }

    public record ReviewBulkBoxDtl(String boxType, int realOutBoundCount, int waitOutBoundCount) {
    }

    public record ReviewBulkSkuDtl(String productSku, int realOutBoundCount, int waitOutBoundCount) {
    }

    public record ReviewBulkConfirmDeliveryReq(List<ReviewBulkBoxDtl> boxDtlList, String customerCode, Map<String, Object> deliveryMsgCreateDTO,
                                               String deliveryNo, List<ReviewBulkSkuDtl> skuDtlList, String sourceNo, String whCode) {
    }

    private final OutboundSampleStore parcels;
    private final FreightStore freight;
    private final StockStore stock;

    public PdaDeliveryController(OutboundSampleStore parcels, FreightStore freight, StockStore stock) {
        this.parcels = parcels;
        this.freight = freight;
        this.stock = stock;
    }

    // ---------- 出库确认 ----------

    private static Map<String, Object> invoice(String deliveryNo, String sourceNo, String customerCode, String expressNo, String channel,
                                               int outboundType, int boxQty, int boxTypeQty, Double weight) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxQty", boxQty);
        m.put("boxTypeQty", boxTypeQty);
        m.put("customerCode", customerCode);
        m.put("deliveryNo", deliveryNo);
        m.put("expressNo", expressNo);
        m.put("logisticsChannel", channel);
        m.put("logisticsChannelName", channel);
        m.put("outboundType", outboundType);
        m.put("realWeight", weight == null ? 0 : weight);
        m.put("sourceNo", sourceNo);
        return m;
    }

    /** 扫单号取待确认出库的单据（List&lt;InvoiceInfoQueryBean&gt;）。 */
    @GetMapping("/getConfirmInfo")
    public R<List<Map<String, Object>>> getConfirmInfo(@RequestParam String paramNo, @RequestParam(required = false) String whCode,
                                                       @RequestParam(defaultValue = "1") int outboundType) {
        if (outboundType == 2) {
            FreightOrder o = freight.lookup(paramNo);
            if (o.status != 20) {
                throw new BizException("大货单当前状态为" + FreightStore.ORDER_STATUS.get(o.status) + "，不能确认出库");
            }
            return R.ok(List.of(invoice(o.deliveryNo, o.sourceNo, o.customerCode, "", o.logisticsChannelName, 2, o.boxQty(), o.boxes.size(), null)));
        }
        if (outboundType != 1) {
            throw new BizException("outboundType 只能是 1（一件代发）或 2（大货）");
        }
        Delivery d = parcels.lookup(paramNo);
        if (d.status != 20) {
            throw new BizException("出库单当前状态为" + OutboundSampleStore.DELIVERY_STATUS.get(d.status) + "，不能确认出库");
        }
        return R.ok(List.of(invoice(d.deliveryNo, d.sourceNo, d.customerCode, d.expressNo, d.logisticsChannel, 1, d.packs.size(), 0, d.weightKg)));
    }

    /** 先全部校验再统一出库，任何一单不满足条件则整批不生效。 */
    private void confirmAll(List<String> nos, Integer pallets) {
        if (nos == null || nos.isEmpty()) {
            throw new BizException("请选择要确认出库的单据");
        }
        for (String no : nos) {
            if (freight.has(no)) {
                FreightOrder o = freight.order(no);
                if (o.status != 20) {
                    throw new BizException("大货单 " + no + " 当前状态为" + FreightStore.ORDER_STATUS.get(o.status) + "，不能确认出库");
                }
            } else {
                Delivery d = parcels.delivery(no);
                if (d.status != 20) {
                    throw new BizException("出库单 " + no + " 当前状态为" + OutboundSampleStore.DELIVERY_STATUS.get(d.status) + "，不能确认出库");
                }
            }
        }
        for (String no : nos) {
            if (freight.has(no)) {
                freight.confirm(no, pallets);
            } else {
                parcels.confirmDelivery(no);
            }
        }
    }

    @PostMapping("/confirmDelivery")
    public R<Object> confirmDelivery(@RequestBody DeliveryConfirmReq req) {
        confirmAll(req.deliveryNoList(), null);
        return R.ok(null);
    }

    @PostMapping("/bulkConfirmDelivery")
    public R<Object> bulkConfirmDelivery(@RequestBody BulkDeliveryConfirmReq req) {
        List<String> nos = req.confirmDeliveryDTOList() == null ? List.of()
                : req.confirmDeliveryDTOList().stream().map(BulkDeliveryConfirmItem::deliveryNo).toList();
        if (req.pallets() != null && req.pallets() < 0) {
            throw new BizException("托数不能为负数");
        }
        confirmAll(nos, req.pallets());
        return R.ok(null);
    }

    // ---------- 大货复核 ----------

    /** ScanRecheckOutBean：一个箱型一行。 */
    private List<Map<String, Object>> recheckLines(FreightOrder o) {
        if (o.status == 0) {
            throw new BizException("大货单尚未拣货完成，不能复核");
        }
        List<Map<String, Object>> out = new ArrayList<>();
        o.boxes.forEach((bt, qty) -> {
            BoxType b = stock.boxType(bt);
            Product p = stock.product(b.sku());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("barcode", p.barcode());
            m.put("boxType", bt);
            m.put("boxTypeFlag", true);
            m.put("currentNum", o.status >= 20 ? qty : 0);
            m.put("customerCode", o.customerCode);
            m.put("customerName", o.customerName);
            m.put("customizeBarcode", "");
            m.put("deliveryNo", o.deliveryNo);
            m.put("expectedTime", o.expectedTime);
            m.put("fnSku", p.fnsku());
            m.put("logisticsChannelName", o.logisticsChannelName);
            m.put("outboundWay", 2);
            m.put("productName", p.name());
            m.put("productSku", p.sku());
            m.put("skuId", p.sku());
            m.put("skuList", List.of(Map.of("productSku", p.sku(), "qty", qty * b.pcsPerBox())));
            m.put("sourceNo", o.sourceNo);
            m.put("waitOutBoundCount", qty);
            out.add(m);
        });
        return out;
    }

    @GetMapping("/review/scanOutbound")
    public R<List<Map<String, Object>>> scanOutbound(@RequestParam String paramNo, @RequestParam(required = false) String whCode) {
        return R.ok(recheckLines(freight.lookup(paramNo)));
    }

    @GetMapping("/review/getOutboundDetail")
    public R<List<Map<String, Object>>> getOutboundDetail(@RequestParam(required = false) String paramNo,
                                                          @RequestParam(required = false) String customerCode,
                                                          @RequestParam(required = false) String deliveryNo,
                                                          @RequestParam(required = false) String whCode) {
        String no = deliveryNo != null && !deliveryNo.isBlank() ? deliveryNo : paramNo;
        return R.ok(recheckLines(freight.lookup(no)));
    }

    /** 是否要求复核时扫序列号。本地演示不启用序列号管理，固定 false。 */
    @GetMapping("/review/getSysBaseParam")
    public R<Boolean> getSysBaseParam(@RequestParam(required = false) String whCode) {
        return R.ok(false);
    }

    /** 序列号扫描：本地演示未启用序列号管理，只校验 SKU 属于该单并回显（响应字段为本项目占位，APK 的 PdaReviewScanProductSnResp 未展开）。 */
    @GetMapping("/review/scanProductSn")
    public R<Map<String, Object>> scanProductSn(@RequestParam(required = false) String whCode, @RequestParam(required = false) String customerCode,
                                                @RequestParam String deliveryNo, @RequestParam String productSnNo,
                                                @RequestParam(required = false) String productSku, @RequestParam(required = false) String packNo,
                                                @RequestParam(required = false) List<String> usedProductSkuList) {
        FreightOrder o = freight.order(deliveryNo);
        if (productSnNo.isBlank()) {
            throw new BizException("序列号不能为空");
        }
        if (usedProductSkuList != null && usedProductSkuList.contains(productSnNo)) {
            throw new BizException("序列号已扫描：" + productSnNo);
        }
        boolean belongs = productSku == null || o.boxes.keySet().stream().anyMatch(bt -> stock.boxType(bt).sku().equals(productSku));
        if (!belongs) {
            throw new BizException("SKU 不属于该大货单：" + productSku);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("deliveryNo", o.deliveryNo);
        m.put("productSku", productSku);
        m.put("productSnNo", productSnNo);
        m.put("packNo", packNo);
        return R.ok(m);
    }

    @PostMapping("/review/bulkConfirmDelivery")
    public R<Object> reviewBulkConfirm(@RequestBody ReviewBulkConfirmDeliveryReq req) {
        Map<String, Integer> real = new LinkedHashMap<>();
        if (req.boxDtlList() != null) {
            req.boxDtlList().forEach(b -> real.merge(b.boxType(), b.realOutBoundCount(), Integer::sum));
        }
        freight.review(req.deliveryNo(), real);
        return R.ok(null);
    }
}
