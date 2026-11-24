package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.StockSku;
import com.xlwms.rebuild.mapper.StockSkuMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计分析-库龄分析页（/statisticalAnalysis/inventoryAgeAnalysis）。读本地 stock_sku 表（虚构）。
 * stock_sku 没有入库日期，库龄天数由 SKU 字符串确定性推算（SKU 哈希打散后对 240 取模），只为演示；
 * 路径、分段（0-30/31-60/61-90/91-180/180+ 天）与字段均为本项目占位。
 */
@RestController
@RequestMapping("/gateway/wms/stockAge")
public class StockAgeController {

    private static final int[][] BUCKETS = {{0, 30}, {31, 60}, {61, 90}, {91, 180}, {181, Integer.MAX_VALUE}};
    private static final String[] LABELS = {"0-30 天", "31-60 天", "61-90 天", "91-180 天", "180 天以上"};

    private final StockSkuMapper mapper;

    public StockAgeController(StockSkuMapper mapper) {
        this.mapper = mapper;
    }

    static int ageDays(String sku) {
        // 乘一个奇数常量打散相邻 SKU 的哈希，让演示数据分布到各个分段
        return Math.floorMod((sku == null ? 0 : sku.hashCode()) * 0x9E3779B1, 240);
    }

    static int bucket(int days) {
        for (int i = 0; i < BUCKETS.length; i++) {
            if (days >= BUCKETS[i][0] && days <= BUCKETS[i][1]) {
                return i;
            }
        }
        return BUCKETS.length - 1;
    }

    private List<Map<String, Object>> rows(Map<String, Object> body) {
        String customerCode = PageUtil.strParam(body, "customerCode");
        String keyword = PageUtil.strParam(body, "keyword");
        LambdaQueryWrapper<StockSku> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(customerCode), StockSku::getCustomerCode, customerCode);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(StockSku::getSku, keyword).or().like(StockSku::getProductName, keyword));
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (StockSku s : mapper.selectList(w)) {
            int days = ageDays(s.getSku());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("sku", s.getSku());
            m.put("productName", s.getProductName());
            m.put("customerCode", s.getCustomerCode());
            m.put("customerName", s.getCustomerName());
            m.put("totalQty", s.getTotalQty());
            m.put("ageDays", days);
            m.put("bucket", bucket(days));
            m.put("bucketName", LABELS[bucket(days)]);
            out.add(m);
        }
        out.sort(Comparator.comparingInt((Map<String, Object> m) -> (int) m.get("ageDays")).reversed());
        return out;
    }

    @PostMapping("/summary")
    public R<List<Map<String, Object>>> summary(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> rows = rows(body);
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = 0; i < LABELS.length; i++) {
            final int b = i;
            List<Map<String, Object>> in = rows.stream().filter(r -> (int) r.get("bucket") == b).toList();
            out.add(Map.of("bucket", b, "bucketName", LABELS[b], "skuCount", in.size(),
                    "qty", in.stream().mapToInt(r -> r.get("totalQty") == null ? 0 : (int) r.get("totalQty")).sum()));
        }
        return R.ok(out);
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> rows = rows(body);
        String bucket = PageUtil.strParam(body, "bucket");
        if (!bucket.isEmpty()) {
            rows = rows.stream().filter(r -> String.valueOf(r.get("bucket")).equals(bucket)).toList();
        }
        return R.ok(PageUtil.page(rows, body));
    }
}
