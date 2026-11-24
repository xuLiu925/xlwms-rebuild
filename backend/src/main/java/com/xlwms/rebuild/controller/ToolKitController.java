package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.ZoneCalculator;
import com.xlwms.rebuild.service.ZoneCalculator.Zone;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工具箱：运费试算（/toolKit/freightCalculation）与分区测试（/toolKit/zoneTest）。
 * 渠道、费率、材积除数、燃油费率全部虚构；路径与字段为本项目占位，真实计费规则未知。
 */
@RestController
public class ToolKitController {

    /** 虚构渠道：承运商、材积除数、首重(1kg)价、续重每 kg 价、燃油费率、时效、限重。 */
    record Channel(String name, String carrier, int dimFactor, double firstKg, double perKg, double fuelRate, String days, double maxKg) {
    }

    private static final List<Channel> CHANNELS = List.of(
            new Channel("示例渠道1", "示例承运商A", 5000, 6.5, 1.8, 0.12, "2-5 天", 30),
            new Channel("示例渠道2", "示例承运商B", 6000, 5.9, 2.1, 0.15, "3-6 天", 30),
            new Channel("示例渠道3", "示例承运商C", 5000, 8.0, 1.2, 0.10, "4-8 天", 68),
            new Channel("示例渠道4", "示例承运商A", 8000, 12.0, 0.9, 0.12, "5-10 天", 68));

    private final ZoneCalculator zones;

    public ToolKitController(ZoneCalculator zones) {
        this.zones = zones;
    }

    private static double num(Map<String, Object> body, String key, String label, boolean required) {
        String v = PageUtil.strParam(body, key);
        if (v.isEmpty()) {
            if (required) {
                throw new BizException("请填写" + label);
            }
            return 0;
        }
        try {
            double d = Double.parseDouble(v);
            if (d < 0 || d > 1000) {
                throw new BizException(label + "超出范围");
            }
            return d;
        } catch (NumberFormatException e) {
            throw new BizException(label + "格式错误");
        }
    }

    private static BigDecimal money(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }

    @PostMapping("/gateway/wms/freight/calculate")
    public R<Map<String, Object>> calculate(@RequestBody Map<String, Object> body) {
        String whCode = PageUtil.strParam(body, "whCode");
        String origin = ZoneCalculator.ORIGIN_ZIP.get(whCode.isEmpty() ? "WH01" : whCode);
        if (origin == null) {
            throw new BizException("未知仓库：" + whCode);
        }
        String destZip = ZoneCalculator.checkZip(PageUtil.strParam(body, "zipCode"), "目的邮编");
        double kg = num(body, "weightKg", "重量", true);
        if (kg <= 0) {
            throw new BizException("重量必须大于 0");
        }
        double l = num(body, "lengthCm", "长", false);
        double w = num(body, "widthCm", "宽", false);
        double h = num(body, "heightCm", "高", false);

        List<Map<String, Object>> quotes = new ArrayList<>();
        for (Channel c : CHANNELS) {
            Zone z = zones.zone(c.carrier(), origin, destZip);
            double volKg = l * w * h / c.dimFactor();
            // 计费重：实重与材积重取大，向上取整到 0.5kg
            double charge = Math.ceil(Math.max(kg, volKg) * 2) / 2;
            Map<String, Object> q = new LinkedHashMap<>();
            q.put("channelName", c.name());
            q.put("carrier", c.carrier());
            q.put("zone", z.zone());
            q.put("volumeWeightKg", money(volKg));
            q.put("chargeWeightKg", money(charge));
            q.put("days", c.days());
            if (charge > c.maxKg()) {
                q.put("available", false);
                q.put("reason", "超过渠道限重 " + c.maxKg() + " kg");
            } else {
                double base = (c.firstKg() + Math.max(0, charge - 1) * c.perKg()) * (1 + 0.08 * (z.zone() - 2));
                double fuel = base * c.fuelRate();
                q.put("available", true);
                q.put("baseFee", money(base));
                q.put("fuelFee", money(fuel));
                q.put("totalFee", money(base + fuel));
                q.put("currency", "USD");
            }
            quotes.add(q);
        }
        quotes.sort(Comparator.comparing((Map<String, Object> q) -> !(boolean) q.get("available"))
                .thenComparing(q -> q.get("totalFee") == null ? BigDecimal.valueOf(Long.MAX_VALUE) : (BigDecimal) q.get("totalFee")));
        return R.ok(Map.of("originZip", origin, "destZip", destZip, "quotes", quotes));
    }

    @PostMapping("/gateway/wms/zoneTest/query")
    public R<Map<String, Object>> zone(@RequestBody Map<String, Object> body) {
        String origin = PageUtil.strParam(body, "originZip");
        if (origin.isEmpty()) {
            origin = ZoneCalculator.ORIGIN_ZIP.getOrDefault(PageUtil.strParam(body, "whCode"), "91000");
        }
        Zone z = zones.zone(PageUtil.strParam(body, "carrier"), origin, PageUtil.strParam(body, "destZip"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("carrier", z.carrier());
        m.put("originZip", z.originZip());
        m.put("destZip", z.destZip());
        m.put("zone", z.zone());
        m.put("rule", "邮编前三位相差 " + z.diff() + "，每 " + z.step() + " 跨一区，2 区起、8 区封顶（虚构规则）");
        return R.ok(m);
    }
}
