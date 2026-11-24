package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
import com.xlwms.rebuild.service.OutboundSampleStore.Wave;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网页端波次管理（/outbound/wave）列表接口。
 * 注意：网页端波次页的真实接口路径未抓取，/gateway/wms/wave/* 是本项目的占位路径；
 * /gateway/wms/demo/* 是本地演示专用，真实系统没有。数据与 PDA 波次接口共用 {@link OutboundSampleStore}。
 */
@RestController
public class WaveWebController {

    private final OutboundSampleStore store;

    public WaveWebController(OutboundSampleStore store) {
        this.store = store;
    }

    @PostMapping("/gateway/wms/wave/statistics")
    public R<List<Map<String, Object>>> statistics() {
        List<Wave> all = store.waves();
        List<Map<String, Object>> out = new ArrayList<>();
        for (int s : new int[] {0, 1, 2}) {
            out.add(Map.of("status", s, "statusName", OutboundSampleStore.PICK_STATUS.get(s),
                    "statusSum", (int) all.stream().filter(w -> w.pickStatus == s).count()));
        }
        return R.ok(out);
    }

    @PostMapping("/gateway/wms/wave/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        String status = PageUtil.strParam(body, "status");
        String keyword = PageUtil.strParam(body, "keyword").toLowerCase();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Wave w : store.waves()) {
            if (!status.isEmpty() && !status.equals(String.valueOf(w.pickStatus))) {
                continue;
            }
            if (!keyword.isEmpty() && !w.waveNo.toLowerCase().contains(keyword)
                    && w.deliveryNos.stream().noneMatch(n -> n.toLowerCase().contains(keyword))) {
                continue;
            }
            rows.add(row(w));
        }
        return R.ok(PageUtil.page(rows, body));
    }

    /** 本地演示：把出库作业示例数据恢复到初始状态。 */
    @PostMapping("/gateway/wms/demo/outbound/reset")
    public R<Void> reset() {
        store.reset();
        return R.ok(null);
    }

    private Map<String, Object> row(Wave w) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("waveNo", w.waveNo);
        m.put("customerCode", w.customerCode);
        m.put("customerName", w.customerName);
        m.put("pickWay", w.pickWay);
        m.put("pickWayName", OutboundSampleStore.PICK_WAY.get(w.pickWay));
        m.put("pickStatus", w.pickStatus);
        m.put("pickStatusName", OutboundSampleStore.PICK_STATUS.get(w.pickStatus));
        m.put("pickCartNo", w.pickCartNo);
        m.put("orderCount", w.deliveryNos.size());
        m.put("skuKinds", (int) w.lines.stream().map(l -> l.productSku).distinct().count());
        m.put("planQty", w.planQty());
        m.put("pickQty", w.pickQty());
        m.put("createTime", w.createTime);
        m.put("lines", w.lines.stream().map(l -> Map.of("productSku", l.productSku, "productName", l.productName,
                "barcode", l.barcode, "cellNo", l.cellNo, "planQty", l.planQty, "pickQty", l.pickQty)).toList());
        m.put("deliveries", w.deliveryNos.stream().map(no -> {
            Delivery d = store.delivery(no);
            return Map.of("deliveryNo", d.deliveryNo, "expressNo", d.expressNo, "skuNum", d.skuNum(),
                    "scanNum", d.scanNum(), "status", d.status,
                    "statusName", OutboundSampleStore.DELIVERY_STATUS.get(d.status));
        }).toList());
        return m;
    }
}
