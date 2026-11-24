package com.xlwms.rebuild;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlwms.rebuild.list.ListResourceRegistry;
import com.xlwms.rebuild.list.ListResourceRegistry.Column;
import com.xlwms.rebuild.list.ListResourceRegistry.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 批次 8：通用列表页本地增删改。会改 lp_* 表，所以测试后重建上下文（重新灌数据）。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenericEditControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ListResourceRegistry registry;
    @Autowired
    ObjectMapper om;

    private ResultActions call(String path, Object body) throws Exception {
        return mvc.perform(post("/gateway/wms/" + path).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)));
    }

    private JsonNode json(ResultActions a) throws Exception {
        return om.readTree(a.andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
    }

    private int total(String key) throws Exception {
        return json(call(key + "/page", Map.of("size", 1))).at("/data/total").asInt();
    }

    /** 按列类型造一条合法记录。 */
    private static Map<String, Object> validBody(Resource r, String tag) {
        Map<String, Object> b = new LinkedHashMap<>();
        for (Column c : r.columns()) {
            if (c.auto() != null) {
                continue;
            }
            Object v = switch (c.type()) {
                case "int" -> c.enumLabels() != null ? c.enumLabels().keySet().iterator().next() : 3;
                case "num", "money" -> "12.50";
                case "rate" -> "0.123456";
                default -> "T" + tag + "-" + c.name();
            };
            b.put(c.name(), v);
        }
        return b;
    }

    @Test
    void everyEditableResourceSupportsAddUpdateDelete() throws Exception {
        List<Resource> editable = registry.all().values().stream().filter(Resource::editable).toList();
        assertThat(editable).hasSize(23);
        for (Resource r : editable) {
            int before = total(r.key());
            JsonNode added = json(call(r.key() + "/add", validBody(r, "A")));
            assertThat(added.at("/code").asInt()).as(r.key() + " add: " + added).isEqualTo(200);
            String id = added.at("/data/id").asText();
            assertThat(total(r.key())).as(r.key()).isEqualTo(before + 1);

            // 新记录排在第一页第一条（按 id 倒序），自动时间列已填
            JsonNode first = json(call(r.key() + "/page", Map.of("size", 1))).at("/data/records/0");
            assertThat(first.at("/id").asText()).isEqualTo(id);
            for (Column c : r.columns()) {
                if ("create".equals(c.auto()) || "update".equals(c.auto())) {
                    assertThat(first.get(c.name()).asText()).as(r.key() + "." + c.name()).matches("\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d");
                }
            }

            Map<String, Object> upd = validBody(r, "B");
            upd.put("id", id);
            assertThat(json(call(r.key() + "/update", upd)).at("/code").asInt()).as(r.key() + " update").isEqualTo(200);

            assertThat(json(call(r.key() + "/delete", Map.of("ids", List.of(id)))).at("/data/deleted").asInt()).isEqualTo(1);
            assertThat(total(r.key())).isEqualTo(before);
        }
    }

    @Test
    void readOnlyResourceRejectsEdits() throws Exception {
        call("requestLog/add", Map.of("requestNo", "X")).andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("该页面只读，不支持编辑"));
        call("globalParcel/delete", Map.of("ids", List.of("1"))).andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void uniqueKeyIsEnforced() throws Exception {
        Map<String, Object> b = validBody(registry.find("customer").orElseThrow(), "A");
        b.put("customerCode", "C001");
        call("customer/add", b).andExpect(jsonPath("$.code").value(500)).andExpect(jsonPath("$.msg").value("customerCode 已存在: C001"));
        assertThat(total("customer")).isEqualTo(12);
    }

    @Test
    void updateKeepsOwnUniqueValueAndUntouchedFields() throws Exception {
        JsonNode row = json(call("customer/page", Map.of("keyword", "C001"))).at("/data/records/0");
        String id = row.at("/id").asText();
        call("customer/update", Map.of("id", id, "customerCode", "C001", "contactName", "改过的联系人"))
                .andExpect(jsonPath("$.code").value(200));
        JsonNode after = json(call("customer/page", Map.of("keyword", "C001"))).at("/data/records/0");
        assertThat(after.at("/contactName").asText()).isEqualTo("改过的联系人");
        assertThat(after.at("/customerName").asText()).isEqualTo(row.at("/customerName").asText());
        assertThat(after.at("/createTime").asText()).isEqualTo(row.at("/createTime").asText());
    }

    @Test
    void validationRejectsBadValues() throws Exception {
        Resource cus = registry.find("customer").orElseThrow();
        Map<String, Object> b = validBody(cus, "A");
        b.put("customerName", " ");
        call("customer/add", b).andExpect(jsonPath("$.msg").value("customerName 不能为空"));

        b = validBody(cus, "A");
        b.put("status", 7);
        call("customer/add", b).andExpect(jsonPath("$.msg").value("status 取值不在可选范围内"));

        b = validBody(cus, "A");
        b.put("balance", "-1");
        call("customer/add", b).andExpect(jsonPath("$.msg").value("balance 必须在 0~100000000 之间"));

        b = validBody(cus, "A");
        b.put("balance", "1.234");
        call("customer/add", b).andExpect(jsonPath("$.msg").value("balance 最多 2 位小数"));

        b = validBody(registry.find("seedWall").orElseThrow(), "A");
        b.put("rowCount", "abc");
        call("seedWall/add", b).andExpect(jsonPath("$.msg").value("rowCount 必须是整数"));
        assertThat(total("customer")).isEqualTo(12);
    }

    @Test
    void deleteIsAllOrNothing() throws Exception {
        String id = json(call("customer/page", Map.of("size", 1))).at("/data/records/0/id").asText();
        call("customer/delete", Map.of("ids", List.of(id, "999"))).andExpect(jsonPath("$.msg").value("记录不存在: 999"));
        assertThat(total("customer")).isEqualTo(12);
    }

    @Test
    void addAfterDeletingAllStillAllocatesInRange() throws Exception {
        JsonNode recs = json(call("brand/page", Map.of("size", 50))).at("/data/records");
        List<String> ids = new java.util.ArrayList<>();
        recs.forEach(n -> ids.add(n.at("/id").asText()));
        call("brand/delete", Map.of("ids", ids)).andExpect(jsonPath("$.data.deleted").value(4));
        Resource brand = registry.find("brand").orElseThrow();
        long id = json(call("brand/add", validBody(brand, "A"))).at("/data/id").asLong();
        assertThat(id).isEqualTo(brand.idBase() + 1);
    }
}
