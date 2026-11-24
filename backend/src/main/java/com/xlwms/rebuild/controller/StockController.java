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

/** 产品库存页（/stock/sku，按产品）接口，只读，全部为虚构示例数据；请求体字段名为本项目占位。 */
@RestController
@RequestMapping("/gateway/wms/stockSku")
public class StockController {

    private static final List<Map<String, Object>> ALL = buildSample();

    private static List<Map<String, Object>> buildSample() {
        String[] categories = {"家居", "电子配件", "服饰", "户外"};
        String[] names = {"示例收纳盒", "示例数据线", "示例T恤", "示例水壶", "示例台灯", "示例手机壳"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= 28; i++) {
            int total = (i * 37) % 500 + 5;
            int locked = i % 5 == 0 ? Math.min(total, i * 2) : 0;
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("id", 2_000_000_000_000_000L + i);
            r.put("sku", String.format("SKU-%04d", i));
            r.put("barcode", String.format("BC%08d", 10_000 + i));
            r.put("productName", names[i % names.length] + " " + i);
            r.put("category", categories[i % categories.length]);
            r.put("customerCode", "C00" + (i % 3 + 1));
            r.put("customerName", "示例客户" + (i % 3 + 1));
            r.put("stockAttr", i % 7 == 0 ? "次品" : "正品");
            r.put("totalQty", total);
            r.put("availableQty", total - locked);
            r.put("lockedQty", locked);
            list.add(r);
        }
        return list;
    }

    private static List<Map<String, Object>> filter(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword").toLowerCase();
        String customer = PageUtil.strParam(body, "customerCode");
        String attr = PageUtil.strParam(body, "stockAttr");
        return ALL.stream()
                .filter(r -> customer.isEmpty() || customer.equals(r.get("customerCode")))
                .filter(r -> attr.isEmpty() || attr.equals(r.get("stockAttr")))
                .filter(r -> keyword.isEmpty()
                        || r.get("sku").toString().toLowerCase().contains(keyword)
                        || r.get("barcode").toString().toLowerCase().contains(keyword)
                        || r.get("productName").toString().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(PageUtil.page(filter(body), body));
    }

    /** 按当前筛选条件汇总。 */
    @PostMapping("/skuTotal")
    public R<Map<String, Object>> skuTotal(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> rows = filter(body);
        return R.ok(Map.of(
                "skuCount", rows.size(),
                "totalQty", sum(rows, "totalQty"),
                "availableQty", sum(rows, "availableQty"),
                "lockedQty", sum(rows, "lockedQty")));
    }

    private static int sum(List<Map<String, Object>> rows, String key) {
        return rows.stream().mapToInt(r -> (Integer) r.get(key)).sum();
    }
}
