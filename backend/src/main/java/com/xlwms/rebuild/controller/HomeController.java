package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 首页接口。全部返回虚构示例数据，字段名取自公开观察到的响应结构。 */
@RestController
@RequestMapping("/gateway/wms/home")
public class HomeController {

    @PostMapping("/stockSummary")
    public R<Map<String, Object>> stockSummary() {
        return R.ok(Map.of("inStockNum", "1000", "availableSkuNum", "100", "skuAmount", "990",
                "boxAmount", 8, "returnAmount", 2));
    }

    @PostMapping("/todoData")
    public R<Map<String, Object>> todoData() {
        return R.ok(Map.of("inBoundOrderNum", 10, "smallOutBoundOrderNum", 20, "bulkOutBoundOrderNum", 0,
                "returnOrderNum", 3, "transOrderNum", 0, "workOrderNum", 0, "claimNum", 0, "interceptNum", 0));
    }

    @PostMapping("/order/count/analysis")
    public R<List<Map<String, Object>>> orderCountAnalysis() {
        return R.ok(List.of(
                Map.of("orderType", 1, "orderCount", "10", "yoyRate", 0, "totRate", 5.5),
                Map.of("orderType", 3, "orderCount", "200", "yoyRate", 0, "totRate", -3.2)));
    }

    @PostMapping("/balance")
    public R<Map<String, Object>> balance() {
        return R.ok(Map.of("recordedAmount", 1000, "rentStockFee", 800, "outStockFee", 150,
                "inStockFee", 50, "workOrderFee", 0, "otherFee", 0, "currencyCode", "EUR"));
    }
}
