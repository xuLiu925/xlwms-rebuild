package com.xlwms.rebuild.list;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.list.ListResourceRegistry.Column;
import com.xlwms.rebuild.list.ListResourceRegistry.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 批次 8：通用列表页的本地增删改，{@code POST /gateway/wms/{resource}/{add,update,delete}}。
 * 只对注册表里 editable=true 的资源开放（基础资料、报价、设置类），只写本地开发库的 lp_* 表。
 * 路径、请求体字段与校验规则都是本项目占位，真实网页接口未抓取。
 * 和 {@link GenericListController} 一样，表名列名只取自注册表，值全部走绑定参数。
 */
@RestController
@RequestMapping("/gateway/wms/{resource}")
public class GenericEditController {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final BigDecimal MAX_DECIMAL = new BigDecimal("100000000");
    /** 每个资源的 ID 区间宽度（批次 2 起为 1e13，批次 1 更宽），新建 ID 不得越过。 */
    private static final long ID_SPAN = 10_000_000_000_000L;

    private final ListResourceRegistry registry;
    private final NamedParameterJdbcTemplate jdbc;

    public GenericEditController(ListResourceRegistry registry, NamedParameterJdbcTemplate jdbc) {
        this.registry = registry;
        this.jdbc = jdbc;
    }

    private Resource editable(String key) {
        Resource r = registry.find(key).orElseThrow(() -> new BizException("未知的列表资源: " + key));
        if (!r.editable()) {
            throw new BizException("该页面只读，不支持编辑");
        }
        return r;
    }

    /** 把请求体里表单可编辑的字段校验并转成列值；新建时缺必填字段报错，编辑时缺的字段保持原值。 */
    private Map<Column, Object> values(Resource r, Map<String, Object> body, boolean creating) {
        Map<Column, Object> out = new LinkedHashMap<>();
        for (Column c : r.columns()) {
            if (c.auto() != null) {
                continue;
            }
            boolean present = body.containsKey(c.name());
            if (!present && !creating) {
                continue;
            }
            Object raw = body.get(c.name());
            String s = raw == null ? "" : raw.toString().trim();
            if (s.isEmpty()) {
                if (c.required()) {
                    throw new BizException(c.name() + " 不能为空");
                }
                out.put(c, "str".equals(c.type()) ? "" : null);
                continue;
            }
            out.put(c, convert(c, s));
        }
        return out;
    }

    private static Object convert(Column c, String s) {
        switch (c.type()) {
            case "str" -> {
                if (s.length() > 128) {
                    throw new BizException(c.name() + " 不能超过 128 个字符");
                }
                return s;
            }
            case "int" -> {
                int v;
                try {
                    v = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new BizException(c.name() + " 必须是整数");
                }
                if (c.enumLabels() != null) {
                    if (!c.enumLabels().containsKey(String.valueOf(v))) {
                        throw new BizException(c.name() + " 取值不在可选范围内");
                    }
                } else if (v < 0 || v > 1_000_000) {
                    throw new BizException(c.name() + " 必须在 0~1000000 之间");
                }
                return v;
            }
            case "num", "money", "rate" -> {
                BigDecimal v;
                try {
                    v = new BigDecimal(s);
                } catch (NumberFormatException e) {
                    throw new BizException(c.name() + " 必须是数字");
                }
                if (v.signum() < 0 || v.compareTo(MAX_DECIMAL) >= 0) {
                    throw new BizException(c.name() + " 必须在 0~100000000 之间");
                }
                int scale = "rate".equals(c.type()) ? 6 : 2;
                if (v.stripTrailingZeros().scale() > scale) {
                    throw new BizException(c.name() + " 最多 " + scale + " 位小数");
                }
                return v;
            }
            default -> throw new BizException(c.name() + " 不可编辑");
        }
    }

    private void checkUnique(Resource r, Map<Column, Object> vals, Long selfId) {
        if (r.unique() == null) {
            return;
        }
        Column u = r.column(r.unique()).orElseThrow();
        if (!vals.containsKey(u)) {
            return;
        }
        MapSqlParameterSource p = new MapSqlParameterSource("v", vals.get(u)).addValue("id", selfId == null ? -1L : selfId);
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM " + r.table() + " WHERE " + u.column() + " = :v AND id <> :id", p, Integer.class);
        if (n != null && n > 0) {
            throw new BizException(u.name() + " 已存在: " + vals.get(u));
        }
    }

    private static long id(Map<String, Object> body) {
        Object raw = body == null ? null : body.get("id");
        try {
            return Long.parseLong(String.valueOf(raw));
        } catch (NumberFormatException e) {
            throw new BizException("id 格式错误");
        }
    }

    private void requireExists(Resource r, long id) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM " + r.table() + " WHERE id = :id", Map.of("id", id), Integer.class);
        if (n == null || n == 0) {
            throw new BizException("记录不存在: " + id);
        }
    }

    @PostMapping("/add")
    @Transactional
    public synchronized R<Map<String, Object>> add(@PathVariable String resource, @RequestBody(required = false) Map<String, Object> body) {
        Resource r = editable(resource);
        Map<Column, Object> vals = values(r, body == null ? Map.of() : body, true);
        checkUnique(r, vals, null);
        MapSqlParameterSource p = new MapSqlParameterSource("lo", r.idBase()).addValue("hi", r.idBase() + ID_SPAN);
        Long max = jdbc.queryForObject("SELECT MAX(id) FROM " + r.table() + " WHERE id > :lo AND id < :hi", p, Long.class);
        long newId = (max == null ? r.idBase() : max) + 1;
        if (newId >= r.idBase() + ID_SPAN) {
            throw new BizException("ID 已用尽");
        }
        String now = LocalDateTime.now().format(TS);
        List<String> cols = new ArrayList<>(List.of("id"));
        List<String> params = new ArrayList<>(List.of(":id"));
        MapSqlParameterSource ins = new MapSqlParameterSource("id", newId);
        for (Column c : r.columns()) {
            cols.add(c.column());
            params.add(":" + c.column());
            Object v = c.auto() == null ? vals.get(c) : ("none".equals(c.auto()) ? "" : now);
            ins.addValue(c.column(), v);
        }
        jdbc.update("INSERT INTO " + r.table() + " (" + String.join(",", cols) + ") VALUES (" + String.join(",", params) + ")", ins);
        return R.ok(Map.of("id", newId));
    }

    @PostMapping("/update")
    @Transactional
    public synchronized R<Map<String, Object>> update(@PathVariable String resource, @RequestBody(required = false) Map<String, Object> body) {
        Resource r = editable(resource);
        long id = id(body);
        requireExists(r, id);
        Map<Column, Object> vals = values(r, body, false);
        checkUnique(r, vals, id);
        List<String> sets = new ArrayList<>();
        MapSqlParameterSource p = new MapSqlParameterSource("id", id);
        vals.forEach((c, v) -> {
            sets.add(c.column() + " = :" + c.column());
            p.addValue(c.column(), v);
        });
        String now = LocalDateTime.now().format(TS);
        r.columns().stream().filter(c -> "update".equals(c.auto())).forEach(c -> {
            sets.add(c.column() + " = :" + c.column());
            p.addValue(c.column(), now);
        });
        if (!sets.isEmpty()) {
            jdbc.update("UPDATE " + r.table() + " SET " + String.join(", ", sets) + " WHERE id = :id", p);
        }
        return R.ok(Map.of("id", id));
    }

    /** 删除：请求体 {ids:[...]}，任一 ID 不存在则整批不删。 */
    @PostMapping("/delete")
    @Transactional
    public synchronized R<Map<String, Object>> delete(@PathVariable String resource, @RequestBody(required = false) Map<String, Object> body) {
        Resource r = editable(resource);
        Object raw = body == null ? null : body.get("ids");
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            throw new BizException("ids 不能为空");
        }
        List<Long> ids = new ArrayList<>();
        for (Object o : list) {
            ids.add(id(Map.of("id", String.valueOf(o))));
        }
        ids.forEach(i -> requireExists(r, i));
        int n = jdbc.update("DELETE FROM " + r.table() + " WHERE id IN (:ids)", Map.of("ids", ids));
        return R.ok(Map.of("deleted", n));
    }
}
