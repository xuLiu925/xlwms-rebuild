package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 通用只读列表页（tools/list-pages/spec.mjs 生成的 lp_* 表）的接口测试。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class GenericListControllerTest {
    @Autowired
    MockMvc mvc;

    private ResultActions call(String path, String body) throws Exception {
        return mvc.perform(post("/gateway/wms/" + path).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @ParameterizedTest
    @CsvSource({"customer,12", "customerRecharge,16", "seedWall,8", "pickCart,10", "account,12", "role,6", "channel,14",
            "channelGroup,5", "exchangeRate,8", "notice,9", "requestLog,20", "taskCenter,12", "returnOrder,18",
            "outboundAbnormal,11", "outboundIntercept,10"})
    void everyResourcePages(String key, int total) throws Exception {
        call(key + "/page", "{\"current\":1,\"size\":5}")
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(total))
                .andExpect(jsonPath("$.data.records.length()").value(Math.min(5, total)))
                .andExpect(jsonPath("$.data.records[0].id").isString());
    }

    @Test
    void enumFilterAddsName() throws Exception {
        call("customer/page", "{\"status\":0}")
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[0].statusName").value("停用"));
    }

    @Test
    void keywordSearchesKwColumns() throws Exception {
        call("requestLog/page", "{\"keyword\":\"创建出库单\"}").andExpect(jsonPath("$.data.total").value(7));
    }

    @Test
    void combinedFilters() throws Exception {
        call("returnOrder/page", "{\"whCode\":\"WH01\",\"status\":\"30\"}").andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    void unknownFieldsAreIgnored() throws Exception {
        call("customer/page", "{\"customer_code; DROP TABLE lp_customer\":\"x\",\"contactName\":\"不在筛选列\"}")
                .andExpect(jsonPath("$.data.total").value(12));
    }

    @Test
    void statisticsCountsPerStatus() throws Exception {
        call("returnOrder/statistics", "{}")
                .andExpect(jsonPath("$.data[0].statusName").value("全部"))
                .andExpect(jsonPath("$.data[0].statusSum").value(18))
                .andExpect(jsonPath("$.data[?(@.status=='30')].statusSum").value(7));
    }

    @Test
    void unknownResourceIsBizError() throws Exception {
        call("noSuchThing/page", "{}").andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void dedicatedControllersStillWin() throws Exception {
        call("basicTray/page", "{\"status\":1}").andExpect(jsonPath("$.data.total").value(8));
    }
}
