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
        return page(all, longParam(body, "current", 1), longParam(body, "size", 20));
    }

    /** 分页并附带 pages（PDA 的分页 bean 有 pages 字段）。 */
    public static <T> Map<String, Object> page(List<T> all, long current, long size) {
        current = Math.max(1, current);
        size = Math.min(200, Math.max(1, size));
        int from = (int) Math.min(all.size(), (current - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        // 用 int 输出，避免 Long 被全局配置序列化成字符串
        return Map.of("records", all.subList(from, to), "total", all.size(), "size", (int) size, "current", (int) current,
                "pages", (int) ((all.size() + size - 1) / size));
    }
}
