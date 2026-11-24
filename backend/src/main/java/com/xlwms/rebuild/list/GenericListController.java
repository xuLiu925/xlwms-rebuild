package com.xlwms.rebuild.list;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.list.ListResourceRegistry.Column;
import com.xlwms.rebuild.list.ListResourceRegistry.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用只读列表页接口：{@code POST /gateway/wms/{resource}/page} 与 {@code /statistics}。
 * 资源定义见 tools/list-pages/spec.mjs；路径、请求体字段（keyword/各筛选字段/current/size）与状态编码
 * 全是本项目占位，真实接口未抓取。已有专门 Controller 的路径（如 /blAsn/page）优先于这里的通配。
 * 数据来自本地库 lp_* 表，全部虚构。
 */
@RestController
@RequestMapping("/gateway/wms/{resource}")
public class GenericListController {

    private final ListResourceRegistry registry;
    private final NamedParameterJdbcTemplate jdbc;

    public GenericListController(ListResourceRegistry registry, NamedParameterJdbcTemplate jdbc) {
        this.registry = registry;
        this.jdbc = jdbc;
    }

    private Resource resource(String key) {
        return registry.find(key).orElseThrow(() -> new BizException("未知的列表资源: " + key));
    }

    /** 拼 WHERE：关键字对 kw 列做 LIKE，筛选列做等值；标识符只取自注册表，值全部走绑定参数。 */
    private String where(Resource r, Map<String, Object> body, MapSqlParameterSource p, String skipColumn) {
        List<String> conds = new ArrayList<>();
        String keyword = PageUtil.strParam(body, "keyword");
        if (!keyword.isEmpty()) {
            List<String> likes = r.columns().stream().filter(Column::kw).map(c -> c.column() + " LIKE :kw").toList();
            if (!likes.isEmpty()) {
                conds.add("(" + String.join(" OR ", likes) + ")");
                p.addValue("kw", "%" + keyword + "%");
            }
        }
        for (Column c : r.columns()) {
            if (!c.filter() || c.name().equals(skipColumn)) {
                continue;
            }
            String v = PageUtil.strParam(body, c.name());
            if (v.isEmpty()) {
                continue;
            }
            String param = "f_" + c.column();
            conds.add(c.column() + " = :" + param);
            p.addValue(param, "int".equals(c.type()) ? parseInt(v) : v);
        }
        return conds.isEmpty() ? "" : " WHERE " + String.join(" AND ", conds);
    }

    private static Object parseInt(String v) {
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new BizException("参数格式错误: " + v);
        }
    }

    /** 列名转回 camelCase，并给枚举列补 xxxName 文案。 */
    private Map<String, Object> toRecord(Resource r, Map<String, Object> row) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", row.get("id"));
        for (Column c : r.columns()) {
            Object v = row.get(c.column());
            m.put(c.name(), v);
            if (c.enumLabels() != null) {
                m.put(c.name() + "Name", v == null ? "" : c.enumLabels().getOrDefault(v.toString(), v.toString()));
            }
        }
        return m;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@PathVariable String resource, @RequestBody(required = false) Map<String, Object> body) {
        Resource r = resource(resource);
        long current = Math.max(1, PageUtil.longParam(body, "current", 1));
        long size = Math.min(200, Math.max(1, PageUtil.longParam(body, "size", 20)));
        MapSqlParameterSource p = new MapSqlParameterSource();
        String where = where(r, body, p, null);
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM " + r.table() + where, p, Integer.class);
        p.addValue("limit", size).addValue("offset", (current - 1) * size);
        List<Map<String, Object>> records = jdbc.queryForList(
                "SELECT * FROM " + r.table() + where + " ORDER BY id DESC LIMIT :limit OFFSET :offset", p)
                .stream().map(row -> toRecord(r, row)).toList();
        int t = total == null ? 0 : total;
        return R.ok(Map.of("records", records, "total", t, "size", (int) size, "current", (int) current,
                "pages", (int) ((t + size - 1) / size)));
    }

    /** 状态页签计数：按 tabsColumn 分组，其他筛选条件照常生效；返回结构仿入库页 {status,statusName,statusSum}。 */
    @PostMapping("/statistics")
    public R<List<Map<String, Object>>> statistics(@PathVariable String resource, @RequestBody(required = false) Map<String, Object> body) {
        Resource r = resource(resource);
        if (r.tabsColumn() == null) {
            return R.ok(List.of());
        }
        Column tab = r.column(r.tabsColumn()).orElseThrow();
        MapSqlParameterSource p = new MapSqlParameterSource();
        String where = where(r, body, p, tab.name());
        Map<String, Integer> counts = new LinkedHashMap<>();
        jdbc.query("SELECT " + tab.column() + " AS s, COUNT(*) AS n FROM " + r.table() + where + " GROUP BY " + tab.column(), p,
                rs -> {
                    counts.put(rs.getString("s"), rs.getInt("n"));
                });
        List<Map<String, Object>> out = new ArrayList<>();
        out.add(Map.of("status", "", "statusName", "全部", "statusSum", counts.values().stream().mapToInt(Integer::intValue).sum()));
        tab.enumLabels().forEach((k, label) -> out.add(Map.of("status", k, "statusName", label, "statusSum", counts.getOrDefault(k, 0))));
        return R.ok(out);
    }
}
