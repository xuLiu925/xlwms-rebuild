package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 产品库存页接口，只读，示例数据。 */
@RestController
@RequestMapping("/gateway/wms/stockSku")
public class StockController {

    @PostMapping("/page")
    public R<Map<String, Object>> page() {
        Map<String, Object> rec = Map.of("barcode", "SKU-0001", "productName", "示例商品", "customerName", "示例客户",
                "stockAttr", "正品", "totalQty", 5, "availableQty", 5, "lockedQty", 0);
        return R.ok(Map.of("records", List.of(rec), "total", 1, "size", 20, "current", 1));
    }

    @PostMapping("/skuTotal")
    public R<Map<String, Object>> skuTotal() {
        return R.ok(Map.of("totalQty", 5, "availableQty", 5, "lockedQty", 0));
    }
}
