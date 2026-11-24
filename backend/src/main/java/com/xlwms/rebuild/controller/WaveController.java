package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.wave.QueryInterceptingReq;
import com.xlwms.rebuild.dto.wave.WavePickReq;
import com.xlwms.rebuild.dto.wave.WaveReqBody;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.OutboundSampleStore.PickLine;
import com.xlwms.rebuild.service.OutboundSampleStore.Wave;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 波次拣货接口（/gateway/wms/pda/wave/*）。路径、查询参数、请求体按 APK ApiService 还原，
 * 响应字段按 APK 的 WavePageBean / WavePageCellRecord / PickWayBean / WaveSkuBean / WarehouseScan /
 * WaveSkuResp / WavePickSubmitResp 还原。数据来自 {@link OutboundSampleStore}，全部虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/wave")
public class WaveController {

    static final String TENANT = "T0001";

    private final OutboundSampleStore store;

    public WaveController(OutboundSampleStore store) {
        this.store = store;
    }

    /** 待拣波次列表。pickType 对应 pickWay（编码为本项目占位），传 0 返回全部未完成波次。 */
    @GetMapping("/pickList")
    public R<List<Map<String, Object>>> pickList(@RequestParam(defaultValue = "0") int pickType,
                                                 @RequestParam(required = false) String whCode) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Wave w : store.waves()) {
            if (w.pickStatus == 2 || (pickType != 0 && w.pickWay != pickType)) {
                continue;
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("customerCode", w.customerCode);
            m.put("customerName", w.customerName);
            m.put("deliveryNo", w.deliveryNos.size() == 1 ? w.deliveryNos.get(0) : null);
            m.put("deviceStatus", null);
            m.put("orderCount", w.deliveryNos.size());
            m.put("pickCartNo", w.pickCartNo);
            m.put("pickWay", w.pickWay);
            m.put("sourceNo", null);
            m.put("tenantCode", TENANT);
            m.put("totalCount", w.planQty());
            m.put("waveNo", w.waveNo);
            m.put("whCode", OutboundSampleStore.WH);
            out.add(m);
        }
        return R.ok(out);
    }

    /** 波次按 SKU 汇总的分页（每个 SKU 带它所在的库位列表）。 */
    @GetMapping("/page")
    public R<Map<String, Object>> page(@RequestParam(defaultValue = "1") int current,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam String waveNo,
                                       @RequestParam(required = false) String whCode) {
        Wave w = store.wave(waveNo);
        Map<String, List<PickLine>> bySku = w.lines.stream()
                .collect(Collectors.groupingBy(l -> l.productSku, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> records = new ArrayList<>();
        bySku.forEach((sku, lines) -> {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("cellList", lines.stream().map(l -> Map.of("cellNo", l.cellNo, "pickQty", l.pickQty, "planQty", l.planQty)).toList());
            r.put("customerCode", w.customerCode);
            r.put("isExpand", false);
            r.put("pickQty", lines.stream().mapToInt(l -> l.pickQty).sum());
            r.put("planQty", lines.stream().mapToInt(l -> l.planQty).sum());
            r.put("productSku", sku);
            records.add(r);
        });
        Map<String, Object> data = head(w);
        data.put("skuPage", PageUtil.page(records, current, size));
        return R.ok(data);
    }

    /** 波次按库位的拣货明细分页。 */
    @GetMapping("/pageCell")
    public R<Map<String, Object>> pageCell(@RequestParam(defaultValue = "1") int current,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam String waveNo,
                                           @RequestParam(required = false) String whCode) {
        Wave w = store.wave(waveNo);
        List<Map<String, Object>> records = w.lines.stream().map(l -> cellDetail(w, l)).toList();
        Map<String, Object> data = head(w);
        data.put("cellPageList", PageUtil.page(records, current, size));
        return R.ok(data);
    }

    @GetMapping("/skuDetail")
    public R<Map<String, Object>> skuDetail(@RequestParam String productSku,
                                            @RequestParam(required = false) String customerCode,
                                            @RequestParam(required = false) String whCode) {
        PickLine l = store.waves().stream().flatMap(w -> w.lines.stream())
                .filter(x -> x.productSku.equals(productSku)).findFirst()
                .orElseThrow(() -> new BizException("SKU 不存在：" + productSku));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fnsku", null);
        m.put("otherBarcode", l.barcode);
        m.put("productName", l.productName);
        m.put("productPicVO", null);
        m.put("productSku", l.productSku);
        return R.ok(m);
    }

    /** 扫库位：返回该库位上本波次还需拣的 SKU 与数量。 */
    @PostMapping("/scanCell")
    public R<Map<String, Object>> scanCell(@RequestBody WaveReqBody req) {
        Wave w = store.wave(req.waveNo());
        PickLine l = w.lines.stream().filter(x -> x.cellNo.equals(req.cellNo()) && x.pickQty < x.planQty).findFirst()
                .orElseThrow(() -> new BizException("库位 " + req.cellNo() + " 在本波次没有待拣任务"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("cellNo", l.cellNo);
        m.put("productQty", l.planQty - l.pickQty);
        m.put("productQuality", 1);
        m.put("productSku", l.productSku);
        return R.ok(m);
    }

    /** 扫 SKU：条码或 SKU 都可以，返回该库位上的计划/已拣数量。 */
    @PostMapping("/scanSku")
    public R<Map<String, Object>> scanSku(@RequestBody WaveReqBody req) {
        Wave w = store.wave(req.waveNo());
        String code = req.barcode() != null && !req.barcode().isBlank() ? req.barcode() : req.sku();
        PickLine l = w.lines.stream()
                .filter(x -> (x.barcode.equals(code) || x.productSku.equals(code))
                        && (req.cellNo() == null || req.cellNo().isBlank() || x.cellNo.equals(req.cellNo())))
                .findFirst().orElseThrow(() -> new BizException("条码 " + code + " 不在本波次该库位的拣货任务中"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxType", null);
        m.put("customerCode", w.customerCode);
        m.put("pickQty", l.pickQty);
        m.put("planQty", l.planQty);
        m.put("productSku", l.productSku);
        m.put("totalPlanQty", w.lines.stream().filter(x -> x.productSku.equals(l.productSku)).mapToInt(x -> x.planQty).sum());
        m.put("whCode", OutboundSampleStore.WH);
        return R.ok(m);
    }

    /** 提交拣货（真实系统是写操作；这里只改内存示例数据）。 */
    @PostMapping("/pick")
    public R<Map<String, Object>> pick(@RequestBody WavePickReq req) {
        Wave w = store.pick(req.waveNo(), req.productSku(), req.cellNo(), req.pickQty());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("deviceSpecialCell", false);
        m.put("pickStatus", w.pickStatus);
        m.put("waveNo", w.waveNo);
        return R.ok(m);
    }

    /** 订单是否被拦截，示例数据中恒为 0（未拦截）。 */
    @PostMapping("/queryIntercepting")
    public R<Integer> queryIntercepting(@RequestBody(required = false) QueryInterceptingReq req) {
        return R.ok(0);
    }

    private static Map<String, Object> head(Wave w) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", w.customerCode);
        m.put("orderCount", w.deliveryNos.size());
        m.put("pickCartNo", w.pickCartNo);
        m.put("pickStatus", w.pickStatus);
        m.put("tenantCode", TENANT);
        m.put("waveNo", w.waveNo);
        m.put("whCode", OutboundSampleStore.WH);
        return m;
    }

    private static Map<String, Object> cellDetail(Wave w, PickLine l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxType", null);
        m.put("cellNo", l.cellNo);
        m.put("customerCode", w.customerCode);
        m.put("customizeBarcode", null);
        m.put("pickQty", l.pickQty);
        m.put("planQty", l.planQty);
        m.put("productQuality", 1);
        m.put("productSku", l.productSku);
        m.put("realQty", l.pickQty);
        m.put("totalPlanQty", w.planQty());
        return m;
    }
}
