package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.xlwms.rebuild.service.OutboundSampleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 出库作业：波次拣货 → 复核 → 确认出库 的完整示例流程。 */
@SpringBootTest
@AutoConfigureMockMvc
class OutboundFlowTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    OutboundSampleStore store;

    @BeforeEach
    void reset() {
        store.reset();
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void pickListAndPagesUseApkFieldNames() throws Exception {
        mvc.perform(get("/gateway/wms/pda/wave/pickList").param("pickType", "0").param("whCode", "WH01"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].waveNo").value("WV26110001"))
                .andExpect(jsonPath("$.data[0].orderCount").value(3));
        mvc.perform(get("/gateway/wms/pda/wave/page").param("waveNo", "WV26110002"))
                .andExpect(jsonPath("$.data.skuPage.records[0].productSku").value("SKU-0004"))
                .andExpect(jsonPath("$.data.skuPage.records[0].cellList[0].cellNo").value("B-01-01"))
                .andExpect(jsonPath("$.data.skuPage.pages").value(1));
        mvc.perform(get("/gateway/wms/pda/wave/pageCell").param("waveNo", "WV26110002"))
                .andExpect(jsonPath("$.data.cellPageList.records[1].cellNo").value("B-01-03"))
                .andExpect(jsonPath("$.data.cellPageList.records[1].totalPlanQty").value(5));
    }

    @Test
    void scanCellThenSkuThenPick() throws Exception {
        postJson("/gateway/wms/pda/wave/scanCell", "{\"waveNo\":\"WV26110002\",\"cellNo\":\"B-01-01\"}")
                .andExpect(jsonPath("$.data.productSku").value("SKU-0004"))
                .andExpect(jsonPath("$.data.productQty").value(3));
        postJson("/gateway/wms/pda/wave/scanSku", "{\"waveNo\":\"WV26110002\",\"cellNo\":\"B-01-01\",\"barcode\":\"BC00010004\"}")
                .andExpect(jsonPath("$.data.planQty").value(3))
                .andExpect(jsonPath("$.data.pickQty").value(0));
        postJson("/gateway/wms/pda/wave/pick", "{\"waveNo\":\"WV26110002\",\"cellNo\":\"B-01-01\",\"productSku\":\"SKU-0004\",\"pickQty\":3}")
                .andExpect(jsonPath("$.data.pickStatus").value(1));
        postJson("/gateway/wms/pda/wave/pick", "{\"waveNo\":\"WV26110002\",\"cellNo\":\"B-01-01\",\"productSku\":\"SKU-0004\",\"pickQty\":1}")
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void reviewIsBlockedUntilWavePicked() throws Exception {
        postJson("/gateway/wms/pda/review/morePieceV2", "{\"deliveryNo\":\"OB2611000201\",\"oneClickPass\":true}")
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("所在波次尚未拣货完成，不能复核"));
    }

    @Test
    void fullFlowPickReviewConfirm() throws Exception {
        postJson("/gateway/wms/pda/wave/pick", "{\"waveNo\":\"WV26110002\",\"cellNo\":\"B-01-01\",\"productSku\":\"SKU-0004\",\"pickQty\":3}");
        postJson("/gateway/wms/pda/wave/pick", "{\"waveNo\":\"WV26110002\",\"cellNo\":\"B-01-03\",\"productSku\":\"SKU-0005\",\"pickQty\":2}")
                .andExpect(jsonPath("$.data.pickStatus").value(2));

        mvc.perform(get("/gateway/wms/pda/review/get4MorePiece").param("deliveryNo", "OB2611000201"))
                .andExpect(jsonPath("$.data.orderSkuAllQty").value(3))
                .andExpect(jsonPath("$.data.skuTypeQty").value(2))
                .andExpect(jsonPath("$.data.status").value(10));
        // 先扫一部分，未完成
        postJson("/gateway/wms/pda/review/morePieceV2",
                "{\"deliveryNo\":\"OB2611000201\",\"reviewList\":[{\"barcode\":\"BC00010004\",\"qty\":2}]}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(0));
        // 未复核完不能出库
        postJson("/gateway/wms/pda/review/confirmDelivery", "{\"deliveryNo\":\"OB2611000201\"}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/gateway/wms/pda/review/morePieceV2",
                "{\"deliveryNo\":\"OB2611000201\",\"reviewList\":[{\"barcode\":\"BC00010005\",\"qty\":1}]}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(1));
        postJson("/gateway/wms/pda/review/confirmDelivery", "{\"deliveryNo\":\"OB2611000201\"}")
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value(30));
    }

    @Test
    void packageReviewByExpressNo() throws Exception {
        mvc.perform(get("/gateway/wms/pda/review/get4piecePackage").param("expressNo", "TRK0000000301"))
                .andExpect(jsonPath("$.data.deliveryNo").value("OB2611000301"))
                .andExpect(jsonPath("$.data.piecePackage.packNo").value("OB2611000301-1"))
                .andExpect(jsonPath("$.data.packageList[0].skuList[0].barcode").value("BC00010006"));
        postJson("/gateway/wms/pda/review/review4packageV2",
                "{\"deliveryNo\":\"OB2611000301\",\"packNo\":\"OB2611000301-1\",\"oneClickPass\":true}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(1));
    }

    @Test
    void webWavePageReflectsPicking() throws Exception {
        postJson("/gateway/wms/pda/wave/pick", "{\"waveNo\":\"WV26110001\",\"cellNo\":\"A-01-01\",\"productSku\":\"SKU-0001\",\"pickQty\":1}");
        postJson("/gateway/wms/wave/page", "{\"keyword\":\"WV26110001\"}")
                .andExpect(jsonPath("$.data.records[0].pickStatusName").value("拣货中"))
                .andExpect(jsonPath("$.data.records[0].pickQty").value(1))
                .andExpect(jsonPath("$.data.records[0].deliveries.length()").value(3));
        postJson("/gateway/wms/wave/statistics", "{}")
                .andExpect(jsonPath("$.data[1].statusSum").value(1));
    }
}
