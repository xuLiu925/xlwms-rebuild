package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.jayway.jsonpath.JsonPath;
import com.xlwms.rebuild.service.InboundWorkStore;
import com.xlwms.rebuild.service.OutboundSampleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 批次 3：本地登录、入库/出库作业页、库龄、全局设置、运费试算、分区测试。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class WorkPagesTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    InboundWorkStore inbound;
    @Autowired
    OutboundSampleStore outbound;

    @BeforeEach
    void reset() {
        inbound.reset();
        outbound.reset();
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post("/gateway/wms" + url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // ---------- 登录 ----------

    @Test
    void localLoginIssuesTokenAndUserInfo() throws Exception {
        String body = postJson("/auth/login", "{\"account\":\"demo\",\"password\":\"x\"}")
                .andExpect(jsonPath("$.data.token").isString()).andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.data.token");
        mvc.perform(get("/gateway/wms/auth/getUserInfo").header("Authorization", token))
                .andExpect(jsonPath("$.data.account").value("demo"));
        postJson("/auth/login", "{\"account\":\"demo\"}").andExpect(jsonPath("$.code").value(500));
        mvc.perform(get("/gateway/wms/auth/getUserInfo").header("Authorization", "bogus")).andExpect(jsonPath("$.code").value(500));
    }

    // ---------- 入库 ----------

    @Test
    void receiveThenPutawayMovesAsnStatus() throws Exception {
        postJson("/inboundWork/receive", "{\"asnNo\":\"ASNW26110002\",\"barcode\":\"BC00010004\",\"qty\":3}")
                .andExpect(jsonPath("$.data.status").value(11));
        postJson("/inboundWork/receive", "{\"asnNo\":\"ASNW26110002\",\"barcode\":\"SKU-0004\",\"qty\":6}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/inboundWork/receive", "{\"asnNo\":\"ASNW26110002\",\"barcode\":\"SKU-0004\",\"qty\":5}")
                .andExpect(jsonPath("$.data.status").value(15));
        postJson("/inboundWork/putaway", "{\"asnNo\":\"ASNW26110002\",\"sku\":\"SKU-0004\",\"cellCode\":\"01-01-01\",\"qty\":8}")
                .andExpect(jsonPath("$.data.status").value(30))
                .andExpect(jsonPath("$.data.lines[0].cells['01-01-01']").value(8));
    }

    @Test
    void putawayRejectsLockedOrForeignCell() throws Exception {
        // 02-01-05 在 WH01 且为锁定；01-01-04 属于 WH02
        postJson("/inboundWork/putaway", "{\"asnNo\":\"ASNW26110003\",\"sku\":\"SKU-0007\",\"cellCode\":\"02-01-05\",\"qty\":1}")
                .andExpect(jsonPath("$.msg").value("库位已锁定，不能上架：02-01-05"));
        postJson("/inboundWork/putaway", "{\"asnNo\":\"ASNW26110003\",\"sku\":\"SKU-0007\",\"cellCode\":\"01-01-04\",\"qty\":1}")
                .andExpect(jsonPath("$.code").value(500));
        mvc.perform(get("/gateway/wms/inboundWork/pendingShelf")).andExpect(jsonPath("$.data.length()").value(2));
        mvc.perform(get("/gateway/wms/inboundWork/cells").param("whCode", "WH01"))
                .andExpect(jsonPath("$.data[?(@ == '02-01-05')]").isEmpty());
    }

    // ---------- 出库 ----------

    @Test
    void secondSortAssignsGridsInOrder() throws Exception {
        postJson("/outboundWork/secondSort/scan", "{\"waveNo\":\"WV26110004\",\"barcode\":\"BC00010008\"}")
                .andExpect(jsonPath("$.data.gridNo").value(1));
        postJson("/outboundWork/secondSort/scan", "{\"waveNo\":\"WV26110004\",\"barcode\":\"BC00010008\"}")
                .andExpect(jsonPath("$.data.gridNo").value(1));
        postJson("/outboundWork/secondSort/scan", "{\"waveNo\":\"WV26110004\",\"barcode\":\"BC00010008\"}")
                .andExpect(jsonPath("$.data.gridNo").value(2))
                .andExpect(jsonPath("$.data.grids[0].sortQty").value(2));
        postJson("/outboundWork/secondSort/scan", "{\"waveNo\":\"WV26110004\",\"barcode\":\"BC00010008\"}")
                .andExpect(jsonPath("$.code").value(500));
        // 单品波次与未拣完波次都不能分拣
        postJson("/outboundWork/secondSort/scan", "{\"waveNo\":\"WV26110003\",\"barcode\":\"BC00010006\"}").andExpect(jsonPath("$.code").value(500));
        postJson("/outboundWork/secondSort/scan", "{\"waveNo\":\"WV26110002\",\"barcode\":\"BC00010004\"}").andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void reviewByScanThenWeighAndShip() throws Exception {
        postJson("/outboundWork/weight", "{\"no\":\"TRK0000000402\",\"weightKg\":1.2}").andExpect(jsonPath("$.code").value(500));
        postJson("/outboundWork/review/scan", "{\"no\":\"TRK0000000402\",\"barcode\":\"BC00010008\"}")
                .andExpect(jsonPath("$.data.status").value(10));
        postJson("/outboundWork/review/scan", "{\"no\":\"OB2611000402\",\"barcode\":\"BC00010009\"}")
                .andExpect(jsonPath("$.data.status").value(20));
        postJson("/outboundWork/weight", "{\"no\":\"OB2611000402\",\"weightKg\":80}").andExpect(jsonPath("$.code").value(500));
        postJson("/outboundWork/weight", "{\"no\":\"OB2611000402\",\"weightKg\":1.25}")
                .andExpect(jsonPath("$.data.status").value(30))
                .andExpect(jsonPath("$.data.weightKg").value(1.25));
    }

    @Test
    void boxReviewPerPack() throws Exception {
        postJson("/outboundWork/boxReview/scan", "{\"packNo\":\"OB2611000401-1\"}")
                .andExpect(jsonPath("$.data.status").value(10))
                .andExpect(jsonPath("$.data.packs[0].reviewStatus").value(1));
        postJson("/outboundWork/boxReview/scan", "{\"packNo\":\"OB2611000401-1\"}").andExpect(jsonPath("$.msg").value("该包裹已复核"));
        postJson("/outboundWork/boxReview/scan", "{\"packNo\":\"OB2611000401-2\"}").andExpect(jsonPath("$.data.status").value(20));
        // 未拣完波次的包裹不能复核
        postJson("/outboundWork/boxReview/scan", "{\"packNo\":\"OB2611000201-1\"}").andExpect(jsonPath("$.code").value(500));
    }

    // ---------- 统计 / 设置 / 工具 ----------

    @Test
    void stockAgeSummaryAndPage() throws Exception {
        postJson("/stockAge/summary", "{}")
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[4].bucketName").value("180 天以上"));
        postJson("/stockAge/page", "{\"bucket\":\"0\",\"size\":100}")
                .andExpect(jsonPath("$.data.records[?(@.bucket != 0)]").isEmpty());
    }

    @Test
    void globalSettingValidatesAndSaves() throws Exception {
        try {
            postJson("/globalSetting/save", "{\"waveIntervalMin\":60,\"weightUnit\":\"lb\"}")
                    .andExpect(jsonPath("$.data[?(@.key=='waveIntervalMin')].value").value(60))
                    .andExpect(jsonPath("$.data[?(@.key=='weightUnit')].value").value("lb"));
            postJson("/globalSetting/save", "{\"waveIntervalMin\":1}").andExpect(jsonPath("$.code").value(500));
            postJson("/globalSetting/save", "{\"weightUnit\":\"g\"}").andExpect(jsonPath("$.code").value(500));
            postJson("/globalSetting/save", "{\"nope\":1}").andExpect(jsonPath("$.msg").value("未知配置项：nope"));
            // 校验失败不应部分生效
            postJson("/globalSetting/save", "{\"currency\":\"EUR\",\"overReceiptRate\":99}").andExpect(jsonPath("$.code").value(500));
            mvc.perform(get("/gateway/wms/globalSetting/get")).andExpect(jsonPath("$.data[?(@.key=='currency')].value").value("USD"));
        } finally {
            postJson("/globalSetting/save", "{\"waveIntervalMin\":30,\"weightUnit\":\"kg\"}");
        }
    }

    @Test
    void freightCalculationUsesChargeWeightAndZone() throws Exception {
        postJson("/freight/calculate", "{\"whCode\":\"WH01\",\"zipCode\":\"10001\",\"weightKg\":1.2,\"lengthCm\":30,\"widthCm\":20,\"heightCm\":15}")
                .andExpect(jsonPath("$.data.quotes[?(@.channelName=='示例渠道1')].zone").value(7))
                .andExpect(jsonPath("$.data.quotes[?(@.channelName=='示例渠道1')].chargeWeightKg").value(2.0))
                .andExpect(jsonPath("$.data.quotes[?(@.channelName=='示例渠道1')].totalFee").value(13.01));
        postJson("/freight/calculate", "{\"zipCode\":\"10001\",\"weightKg\":50}")
                .andExpect(jsonPath("$.data.quotes[3].available").value(false));
        postJson("/freight/calculate", "{\"zipCode\":\"ABC\",\"weightKg\":1}").andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void zoneTestPerCarrier() throws Exception {
        postJson("/zoneTest/query", "{\"carrier\":\"示例承运商B\",\"whCode\":\"WH01\",\"destZip\":\"10001\"}")
                .andExpect(jsonPath("$.data.zone").value(8));
        postJson("/zoneTest/query", "{\"carrier\":\"示例承运商C\",\"originZip\":\"91000\",\"destZip\":\"10001\"}")
                .andExpect(jsonPath("$.data.zone").value(6));
        postJson("/zoneTest/query", "{\"carrier\":\"未知\",\"destZip\":\"10001\"}").andExpect(jsonPath("$.code").value(500));
    }
}
