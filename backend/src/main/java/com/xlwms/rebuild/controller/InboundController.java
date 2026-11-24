package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
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
        Map<String, Object> rec = Map.of(
                "id", 1L, "asnNo", "ASN0000000000XX", "sourceNo", "IB000000000XX", "inboundType", 1,
                "customerCode", "C001", "customerName", "示例客户", "whCode", "WH01", "status", 10,
                "boxCount", 10, "receiptCount", 0, "skuQtySum", 100, "etaTime", "2026-12-01",
                "createTime", "2026-11-01 10:00:00");
        return R.ok(Map.of("records", List.of(rec), "total", 1, "size", 20, "current", 1));
    }
}
