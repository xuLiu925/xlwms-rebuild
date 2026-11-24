package com.xlwms.rebuild.common;

import java.util.List;
import java.util.Map;

/** 示例接口用的内存分页，返回结构与观察到的 MyBatis-Plus 分页 {records,total,size,current} 一致。 */
public final class PageUtil {
    private PageUtil() {
    }

    public static long longParam(Map<String, Object> body, String key, long def) {
        Object v = body == null ? null : body.get(key);
        if (v == null || v.toString().isBlank()) {
            return def;
        }
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String strParam(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? "" : v.toString().trim();
    }

    public static <T> Map<String, Object> page(List<T> all, Map<String, Object> body) {
        long current = Math.max(1, longParam(body, "current", 1));
        long size = Math.min(200, Math.max(1, longParam(body, "size", 20)));
        int from = (int) Math.min(all.size(), (current - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        return Map.of("records", all.subList(from, to), "total", all.size(), "size", size, "current", current);
    }
}
