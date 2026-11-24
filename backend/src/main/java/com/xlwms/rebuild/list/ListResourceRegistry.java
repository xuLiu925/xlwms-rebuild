package com.xlwms.rebuild.list;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * 通用列表页注册表，加载 db/list-resources.json（由 tools/list-pages/gen.mjs 生成）。
 * 表名与列名只能来自这里，启动时校验为安全标识符，请求参数永远不会拼进 SQL 标识符位置。
 */
@Component
public class ListResourceRegistry {

    private static final Pattern IDENT = Pattern.compile("[a-z][a-z0-9_]*");

    /** auto：time 列的维护方式，create=新建时填当前时间，update=每次保存都填，none=不在表单里；非 time 列为 null。 */
    public record Column(String name, String column, String type, boolean kw, boolean filter, Map<String, String> enumLabels,
                         boolean required, String auto) {
    }

    /** editable：是否允许本地新建/编辑/删除（批次 8）；unique：唯一键字段名，可为 null；idBase：新建 ID 的起点。 */
    public record Resource(String key, String table, List<Column> columns, String tabsColumn,
                           boolean editable, String unique, long idBase) {
        public Optional<Column> column(String name) {
            return columns.stream().filter(c -> c.name().equals(name)).findFirst();
        }
    }

    private final Map<String, Resource> resources = new LinkedHashMap<>();

    public ListResourceRegistry(ObjectMapper objectMapper) throws IOException {
        try (InputStream in = new ClassPathResource("db/list-resources.json").getInputStream()) {
            List<Resource> list = objectMapper.readValue(in, new TypeReference<>() { });
            for (Resource r : list) {
                check(r.table());
                r.columns().forEach(c -> check(c.column()));
                resources.put(r.key(), r);
            }
        }
    }

    private static void check(String ident) {
        if (!IDENT.matcher(ident).matches()) {
            throw new IllegalStateException("list-resources.json 中的标识符不合法: " + ident);
        }
    }

    public Optional<Resource> find(String key) {
        return Optional.ofNullable(resources.get(key));
    }

    public Map<String, Resource> all() {
        return resources;
    }
}
