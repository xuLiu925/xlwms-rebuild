package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一件代发（/outbound/parcel）列表接口，只读，全部为虚构示例数据。
 * 接口路径来自页面观察；请求体与 records 字段名未抓取，这里的 status 编码和字段名是本项目自定的占位。
 */
@RestController
@RequestMapping("/gateway/wms")
public class DeliveryController {

    /** 页签状态：编码为占位，真实编码未知。 */
    static final Map<Integer, String> STATUS = new LinkedHashMap<>();

    static {
        STATUS.put(10, "待处理");
        STATUS.put(20, "待拣货");
        STATUS.put(30, "待复核");
        STATUS.put(40, "待出库");
        STATUS.put(50, "已出库");
        STATUS.put(80, "异常");
        STATUS.put(90, "已取消");
    }

    private static final List<Map<String, Object>> ALL = buildSample();

    private static List<Map<String, Object>> buildSample() {
        String[] channels = {"示例快递A", "示例快递B", "示例邮政"};
        String[] carriers = {"CarrierA", "CarrierB", "Post"};
        String[] countries = {"DE", "FR", "IT", "ES", "GB"};
        String[] platforms = {"示例平台1", "示例平台2", "独立站"};
        Integer[] statuses = STATUS.keySet().toArray(new Integer[0]);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= 36; i++) {
            int status = statuses[i % statuses.length];
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("id", 1_000_000_000_000_000L + i);
            r.put("deliveryNo", String.format("OB2611%06d", i));
            r.put("customerCode", "C00" + (i % 3 + 1));
            r.put("customerName", "示例客户" + (i % 3 + 1));
            r.put("skuKind", i % 4 == 0 ? "一单多件" : "一单一件");
            r.put("channelName", channels[i % channels.length]);
            r.put("carrier", carriers[i % carriers.length]);
            r.put("countryCode", countries[i % countries.length]);
            r.put("trackingNo", status >= 40 && status != 90 ? String.format("TRK%010d", i) : "");
            r.put("parcelQty", i % 4 == 0 ? 2 : 1);
            r.put("platform", platforms[i % platforms.length]);
            r.put("status", status);
            r.put("statusName", STATUS.get(status));
            r.put("waveNo", status >= 20 && status != 90 ? String.format("WV2611%04d", i / 5 + 1) : "");
            r.put("labelPrinted", status >= 30 && status != 90);
            r.put("shipDeadline", String.format("2026-11-%02d 23:59:59", 25 + i % 5));
            r.put("createTime", String.format("2026-11-%02d %02d:%02d:00", 10 + i % 14, 8 + i % 10, i % 60));
            list.add(r);
        }
        return list;
    }

    private static List<Map<String, Object>> filter(Map<String, Object> body, boolean byStatus) {
        String status = PageUtil.strParam(body, "status");
        String keyword = PageUtil.strParam(body, "keyword").toLowerCase();
        String customer = PageUtil.strParam(body, "customerCode");
        return ALL.stream()
                .filter(r -> !byStatus || status.isEmpty() || status.equals(String.valueOf(r.get("status"))))
                .filter(r -> customer.isEmpty() || customer.equals(r.get("customerCode")))
                .filter(r -> keyword.isEmpty()
                        || r.get("deliveryNo").toString().toLowerCase().contains(keyword)
                        || r.get("trackingNo").toString().toLowerCase().contains(keyword)
                        || r.get("waveNo").toString().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    /** 各页签计数（按当前筛选条件，不含状态本身）。 */
    @PostMapping("/blDelivery/queryInTotal")
    public R<List<Map<String, Object>>> queryInTotal(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> rows = filter(body, false);
        List<Map<String, Object>> out = new ArrayList<>();
        STATUS.forEach((code, name) -> out.add(Map.of("status", code, "statusName", name,
                "statusSum", rows.stream().filter(r -> code.equals(r.get("status"))).count())));
        return R.ok(out);
    }

    @PostMapping("/blDelivery/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(PageUtil.page(filter(body, true), body));
    }
}
