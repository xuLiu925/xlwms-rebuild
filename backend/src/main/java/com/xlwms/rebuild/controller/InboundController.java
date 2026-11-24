package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 入库管理（ASN）列表相关接口，只读，示例数据。 */
@RestController
@RequestMapping("/gateway/wms/blAsn")
public class InboundController {

    @PostMapping("/status/statistics")
    public R<List<Map<String, Object>>> statistics() {
        return R.ok(List.of(
                Map.of("status", 10, "statusName", "在途", "statusSum", 3),
                Map.of("status", 11, "statusName", "收货中", "statusSum", 0),
                Map.of("status", 15, "statusName", "已收货", "statusSum", 1),
                Map.of("status", 30, "statusName", "已上架", "statusSum", 20),
                Map.of("status", 90, "statusName", "已取消", "statusSum", 0)));
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page() {
        // Map.of 最多 10 对键值，这里用 LinkedHashMap 并保持字段顺序
        Map<String, Object> rec = new LinkedHashMap<>();
        rec.put("id", 1L);
        rec.put("asnNo", "ASN0000000000XX");
        rec.put("sourceNo", "IB000000000XX");
        rec.put("inboundType", 1);
        rec.put("customerCode", "C001");
        rec.put("customerName", "示例客户");
        rec.put("whCode", "WH01");
        rec.put("status", 10);
        rec.put("boxCount", 10);
        rec.put("receiptCount", 0);
        rec.put("skuQtySum", 100);
        rec.put("etaTime", "2026-12-01");
        rec.put("createTime", "2026-11-01 10:00:00");
        return R.ok(Map.of("records", List.of(rec), "total", 1, "size", 20, "current", 1));
    }
}
