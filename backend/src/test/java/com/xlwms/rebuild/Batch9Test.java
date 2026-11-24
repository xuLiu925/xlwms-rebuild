package com.xlwms.rebuild;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlwms.rebuild.service.StockStore;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 批次 9：盘点（网页端）、次品处理、报表。内存数据，每个用例前 /demo/reset。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class Batch9Test {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @Autowired
    StockStore stock;

    private JsonNode call(String path, Object body) throws Exception {
        String s = mvc.perform(post("/gateway/wms/" + path).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return om.readTree(s);
    }

    private JsonNode ok(String path, Object body) throws Exception {
        JsonNode n = call(path, body);
        assertThat(n.at("/code").asInt()).as(path + " → " + n).isEqualTo(200);
        return n.at("/data");
    }

    private String err(String path, Object body) throws Exception {
        JsonNode n = call(path, body);
        assertThat(n.at("/code").asInt()).as(path + " → " + n).isEqualTo(500);
        return n.at("/msg").asText();
    }

    @BeforeEach
    void reset() throws Exception {
        ok("demo/reset", Map.of());
    }

    // ---------- 盘点 ----------

    @Test
    void checkingListsSeedOrdersWithStatusTabs() throws Exception {
        JsonNode page = ok("inventoryCheck/page", Map.of());
        assertThat(page.at("/total").asInt()).isEqualTo(2);
        assertThat(page.at("/records/0/orderSn").asText()).isEqualTo("IC2611000002");
        assertThat(page.at("/records/0/checkTypeName").asText()).isEqualTo("盲盘");
        JsonNode stats = ok("inventoryCheck/statistics", Map.of());
        assertThat(stats.at("/0/statusSum").asInt()).isEqualTo(2);
        assertThat(stats.at("/1/statusName").asText()).isEqualTo("待盘点");
        assertThat(stats.at("/1/statusSum").asInt()).isEqualTo(2);
    }

    @Test
    void webCreatedOrderCanBeCountedOnPdaAndShowsDiff() throws Exception {
        assertThat(err("inventoryCheck/create", Map.of("checkType", 1, "cellNos", List.of("A-01-01"))))
                .isEqualTo("库位 A-01-01 已在未完成的盘点单 IC2611000001 中");
        assertThat(err("inventoryCheck/create", Map.of("checkType", 1, "cellNos", List.of("Z-99-99")))).isEqualTo("库位不存在：Z-99-99");
        assertThat(err("inventoryCheck/create", Map.of("checkType", 3, "cellNos", List.of("D-01-01")))).contains("盘点方式");

        JsonNode created = ok("inventoryCheck/create", Map.of("checkType", 1, "cellNos", List.of("D-01-01"), "remark", "网页新建"));
        String sn = created.at("/orderSn").asText();
        assertThat(sn).isEqualTo("IC2611000003");
        JsonNode cells = ok("inventoryCheck/cells", Map.of());
        boolean busy = false;
        for (JsonNode c : cells) {
            if (c.at("/cellNo").asText().equals("D-01-01")) {
                busy = c.at("/busyOrderSn").asText().equals(sn);
            }
        }
        assertThat(busy).isTrue();

        // PDA 盘点：开始 → 提交实盘 23（账面 25）→ 完成
        assertThat(ok("pda/inventoryCheck/start", Map.of("orderSn", sn)).size()).isZero();
        ok("pda/inventoryCheck/submitCell", Map.of("orderSn", sn, "cellNo", "D-01-01",
                "dtlList", List.of(Map.of("sku", "SKU-0008", "solidStock", 23))));
        assertThat(ok("pda/inventoryCheck/complete", Map.of("orderSn", sn)).size()).isZero();

        JsonNode detail = ok("inventoryCheck/detail", Map.of("orderSn", sn));
        assertThat(detail.at("/statusName").asText()).isEqualTo("已完成");
        assertThat(detail.at("/lossQty").asInt()).isEqualTo(2);
        assertThat(detail.at("/cells/0/items/0/bookStock").asInt()).isEqualTo(25);
        assertThat(detail.at("/cells/0/items/0/diff").asInt()).isEqualTo(-2);
    }

    @Test
    void cancelOnlyPendingAndHiddenFromPda() throws Exception {
        ok("pda/inventoryCheck/start", Map.of("orderSn", "IC2611000001"));
        assertThat(err("inventoryCheck/cancel", Map.of("orderSn", "IC2611000001"))).isEqualTo("只有待盘点的单能取消");
        assertThat(ok("inventoryCheck/cancel", Map.of("orderSn", "IC2611000002")).at("/statusName").asText()).isEqualTo("已取消");
        assertThat(ok("pda/inventoryCheck/list", Map.of("current", 1, "size", 20)).at("/total").asInt()).isEqualTo(1);
        assertThat(ok("pda/inventoryCheck/start", Map.of("orderSn", "IC2611000002")).at("/0/cause").asText()).isEqualTo("盘点单已取消");
        // 取消后库位释放，可以再建
        ok("inventoryCheck/create", Map.of("checkType", 2, "cellNos", List.of("B-01-01")));
    }

    // ---------- 次品处理 ----------

    @Test
    void defectiveListsQualityTwoStock() throws Exception {
        // 初始只有 A-02-01 的 SKU-0003 次品 2 件；PDA 次品上架到 E 区后也会出现在这里
        JsonNode page = ok("stockDefective/page", Map.of());
        assertThat(page.at("/total").asInt()).isEqualTo(1);
        assertThat(page.at("/totalQty").asInt()).isEqualTo(2);
        assertThat(page.at("/records/0/productName").asText()).isEqualTo("示例T恤 3");
        stock.putIn("E-01-01", "SKU-0006", "", 2, 4);
        assertThat(ok("stockDefective/page", Map.of("keyword", "e-01")).at("/total").asInt()).isEqualTo(1);
        assertThat(ok("stockDefective/page", Map.of("customerCode", "C003")).at("/totalQty").asInt()).isEqualTo(4);
        assertThat(ok("stockDefective/page", Map.of()).at("/total").asInt()).isEqualTo(2);
    }

    @Test
    void toGoodMovesStockAndDestroyDeducts() throws Exception {
        ok("stockDefective/toGood", Map.of("cellNo", "A-02-01", "sku", "SKU-0003", "qty", 1));
        assertThat(stock.qtyOf("A-02-01", "SKU-0003", "", 1)).isEqualTo(9);
        assertThat(stock.qtyOf("A-02-01", "SKU-0003", "", 2)).isEqualTo(1);
        assertThat(err("stockDefective/destroy", Map.of("cellNo", "A-02-01", "sku", "SKU-0003", "qty", 1)))
                .isEqualTo("销毁必须填写处理说明");
        assertThat(err("stockDefective/destroy", Map.of("cellNo", "A-02-01", "sku", "SKU-0003", "qty", 2, "remark", "破损")))
                .isEqualTo("库位 A-02-01 的次品可用数不足，可用 1");
        ok("stockDefective/destroy", Map.of("cellNo", "A-02-01", "sku", "SKU-0003", "qty", 1, "remark", "破损"));
        assertThat(ok("stockDefective/page", Map.of()).at("/total").asInt()).isZero();
        JsonNode rec = ok("stockDefective/records", Map.of());
        assertThat(rec.at("/total").asInt()).isEqualTo(2);
        assertThat(rec.at("/records/0/type").asText()).isEqualTo("销毁");
        assertThat(rec.at("/records/1/targetCellNo").asText()).isEqualTo("A-02-01");
    }

    @Test
    void toGoodRejectsLockedTargetWithoutChangingStock() throws Exception {
        assertThat(err("stockDefective/toGood", Map.of("cellNo", "A-02-01", "sku", "SKU-0003", "qty", 1, "targetCellNo", "E-09-09")))
                .isEqualTo("库位已锁定：E-09-09");
        assertThat(ok("stockDefective/page", Map.of()).at("/totalQty").asInt()).isEqualTo(2);
        assertThat(ok("stockDefective/records", Map.of()).at("/total").asInt()).isZero();
    }

    // ---------- 报表 ----------

    @Test
    void reportsAreDeterministicAndRangeChecked() throws Exception {
        Map<String, Object> q = Map.of("startDate", "2026-11-01", "endDate", "2026-11-07", "whCode", "WH01");
        for (String path : List.of("report/staffEfficiency", "report/inbound", "report/outbound")) {
            JsonNode a = ok(path, q);
            assertThat(a.at("/series").size()).as(path).isEqualTo(7);
            assertThat(a.at("/cards").size()).as(path).isGreaterThanOrEqualTo(4);
            assertThat(ok(path, q)).as(path).isEqualTo(a);
            assertThat(err(path, Map.of("startDate", "2026-11-08", "endDate", "2026-11-07"))).isEqualTo("开始日期不能晚于结束日期");
            assertThat(err(path, Map.of("startDate", "2026-10-01", "endDate", "2026-11-07"))).isEqualTo("查询区间最多 31 天");
            assertThat(err(path, Map.of("whCode", "WH09"))).isEqualTo("未知的仓库：WH09");
        }
        JsonNode staff = ok("report/staffEfficiency", q);
        assertThat(staff.at("/rows").size()).isEqualTo(6);
        assertThat(staff.at("/rows/0/rank").asInt()).isEqualTo(1);
        assertThat(staff.at("/rows/0/qtyPerHour").decimalValue()).isGreaterThanOrEqualTo(staff.at("/rows/5/qtyPerHour").decimalValue());
    }

    @Test
    void allWarehousesIsSumOfBoth() throws Exception {
        Map<String, Object> base = Map.of("startDate", "2026-11-03", "endDate", "2026-11-03");
        int w1 = ok("report/outbound", Map.of("startDate", "2026-11-03", "endDate", "2026-11-03", "whCode", "WH01")).at("/rows/0/orders").asInt();
        int w2 = ok("report/outbound", Map.of("startDate", "2026-11-03", "endDate", "2026-11-03", "whCode", "WH02")).at("/rows/0/orders").asInt();
        assertThat(ok("report/outbound", base).at("/rows/0/orders").asInt()).isEqualTo(w1 + w2);
    }
}
