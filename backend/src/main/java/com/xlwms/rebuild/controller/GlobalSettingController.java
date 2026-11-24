package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设置-全局设置页（/setting/globalSetting）。配置项、分组、取值范围全是本项目假设（真实配置项未知），
 * 保存在内存里，重启恢复默认值。保存时只接受已定义的键，并按类型和范围校验。
 */
@RestController
@RequestMapping("/gateway/wms/globalSetting")
public class GlobalSettingController {

    /** 配置项定义：type = select | bool | int。 */
    record Item(String group, String key, String label, String type, List<String> options, int min, int max, Object defaultValue) {
    }

    private static final List<Item> ITEMS = List.of(
            new Item("基础", "defaultWhCode", "默认仓库", "select", List.of("WH01", "WH02"), 0, 0, "WH01"),
            new Item("基础", "weightUnit", "重量单位", "select", List.of("kg", "lb"), 0, 0, "kg"),
            new Item("基础", "sizeUnit", "尺寸单位", "select", List.of("cm", "in"), 0, 0, "cm"),
            new Item("基础", "currency", "本位币", "select", List.of("USD", "CNY", "EUR"), 0, 0, "USD"),
            new Item("入库", "allowOverReceipt", "允许超收", "bool", null, 0, 0, false),
            new Item("入库", "overReceiptRate", "超收比例(%)", "int", null, 0, 50, 5),
            new Item("出库", "autoWave", "自动生成波次", "bool", null, 0, 0, true),
            new Item("出库", "waveIntervalMin", "自动波次间隔(分钟)", "int", null, 5, 240, 30),
            new Item("出库", "reviewMode", "复核方式", "select", List.of("逐件扫描", "一键复核"), 0, 0, "逐件扫描"),
            new Item("库存", "stockWarningDays", "库龄预警天数", "int", null, 30, 365, 90));

    private final Map<String, Object> values = new LinkedHashMap<>();

    public GlobalSettingController() {
        ITEMS.forEach(i -> values.put(i.key(), i.defaultValue()));
    }

    @GetMapping("/get")
    public synchronized R<List<Map<String, Object>>> get() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Item i : ITEMS) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("group", i.group());
            m.put("key", i.key());
            m.put("label", i.label());
            m.put("type", i.type());
            m.put("options", i.options());
            m.put("min", i.min());
            m.put("max", i.max());
            m.put("value", values.get(i.key()));
            out.add(m);
        }
        return R.ok(out);
    }

    @PostMapping("/save")
    public synchronized R<List<Map<String, Object>>> save(@RequestBody Map<String, Object> body) {
        Map<String, Object> next = new LinkedHashMap<>(values);
        for (Map.Entry<String, Object> e : body.entrySet()) {
            Item i = ITEMS.stream().filter(x -> x.key().equals(e.getKey())).findFirst()
                    .orElseThrow(() -> new BizException("未知配置项：" + e.getKey()));
            next.put(i.key(), validate(i, e.getValue()));
        }
        values.clear();
        values.putAll(next);
        return get();
    }

    private static Object validate(Item i, Object v) {
        switch (i.type()) {
            case "bool" -> {
                if (v instanceof Boolean b) {
                    return b;
                }
                throw new BizException(i.label() + " 需为是/否");
            }
            case "int" -> {
                int n;
                try {
                    n = Integer.parseInt(String.valueOf(v));
                } catch (NumberFormatException ex) {
                    throw new BizException(i.label() + " 需为整数");
                }
                if (n < i.min() || n > i.max()) {
                    throw new BizException(i.label() + " 需在 " + i.min() + "~" + i.max() + " 之间");
                }
                return n;
            }
            default -> {
                if (!i.options().contains(String.valueOf(v))) {
                    throw new BizException(i.label() + " 取值不合法：" + v);
                }
                return String.valueOf(v);
            }
        }
    }
}
