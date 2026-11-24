package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 原批次 1 的 4 个 /pda/shelf/v2 接口，改为接到 PutawayStore 后的有状态测试。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class ShelvesControllerTest {
    @Autowired
    MockMvc mvc;

    @BeforeEach
    void reset() throws Exception {
        mvc.perform(post("/gateway/wms/demo/reset"));
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post("/gateway/wms" + url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions qty(String cell, String sku) throws Exception {
        return postJson("/pda/stock/check", "{\"cellNo\":\"" + cell + "\",\"productSku\":\"" + sku + "\"}");
    }

    @Test
    void skuPutawayConfirm() throws Exception {
        postJson("/pda/receipt/wholeOrderReceipt", "{\"asnNo\":\"RA2611000005\"}");
        mvc.perform(get("/gateway/wms/pda/shelf/v2/scanBarcode").param("barcode", "BC00010006").param("putawayNo", "PA2611000005"))
                .andExpect(jsonPath("$.data.receiptQty").value(20))
                .andExpect(jsonPath("$.data.recommendCell").value("C-03-02"));
        mvc.perform(get("/gateway/wms/pda/shelf/v2/getRecommendCell/v2").param("putawayNo", "PA2611000005"))
                .andExpect(jsonPath("$.data.skuList.total").value(2))
                .andExpect(jsonPath("$.data.skuList.records[0].recommendCellList[0].cellNo").value("C-03-02"))
                .andExpect(jsonPath("$.data.boxList.total").value(0));

        // 同一 SKU 两行合计超了 → 整体拒绝，库存不变
        postJson("/pda/shelf/v2/confirm", "{\"putawayNo\":\"PA2611000005\",\"skuList\":[{\"cellNo\":\"C-03-02\",\"productSku\":\"SKU-0006\",\"putawayQty\":15},"
                + "{\"cellNo\":\"E-01-01\",\"productSku\":\"SKU-0006\",\"putawayQty\":6}]}")
                .andExpect(jsonPath("$.code").value(500));
        qty("C-03-02", "SKU-0006").andExpect(jsonPath("$.data.useStkQty").value("40"));

        postJson("/pda/shelf/v2/confirm", "{\"putawayNo\":\"PA2611000005\",\"skuList\":[{\"cellNo\":\"C-03-02\",\"productSku\":\"SKU-0006\",\"putawayQty\":15},"
                + "{\"cellNo\":\"E-01-01\",\"productSku\":\"SKU-0003\",\"putawayQty\":6}]}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(0))
                .andExpect(jsonPath("$.data.putawayNo").value("PA2611000005"));
        qty("C-03-02", "SKU-0006").andExpect(jsonPath("$.data.useStkQty").value("55"));
        postJson("/pda/shelf/v2/confirm", "{\"putawayNo\":\"PA2611000005\",\"skuList\":[{\"cellNo\":\"E-09-09\",\"productSku\":\"SKU-0006\",\"putawayQty\":5}]}")
                .andExpect(jsonPath("$.msg").value("库位已锁定：E-09-09"));
        postJson("/pda/shelf/v2/confirm", "{\"putawayNo\":\"PA2611000005\",\"skuList\":[{\"cellNo\":\"C-03-02\",\"productSku\":\"SKU-0006\",\"putawayQty\":5}]}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(1));
    }

    @Test
    void boxSubmitShelfToDifferentCells() throws Exception {
        postJson("/pda/receipt/wholeOrderReceipt", "{\"asnNo\":\"RA2611000001\"}");
        postJson("/pda/shelf/v2/box/submitShelf", "{\"putawayNo\":\"PA2611000001\",\"boxBarcodeList\":[{\"boxBarcode\":\"BX-0001-01\",\"cellNo\":\"A-01-02\"},"
                + "{\"boxBarcode\":\"BX-0001-01\",\"cellNo\":\"E-01-02\"}]}")
                .andExpect(jsonPath("$.msg").value("箱唛不存在、已上架或重复：BX-0001-01"));
        postJson("/pda/shelf/v2/box/submitShelf", "{\"putawayNo\":\"PA2611000001\",\"boxBarcodeList\":[{\"boxBarcode\":\"BX-0001-01\",\"cellNo\":\"A-01-02\"},"
                + "{\"boxBarcode\":\"BX-0001-02\",\"cellNo\":\"E-01-02\"}]}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(0));
        qty("A-01-02", "SKU-0002").andExpect(jsonPath("$.data.useStkQty").value("20"));
        qty("E-01-02", "SKU-0001").andExpect(jsonPath("$.data.useStkQty").value("10"));
        mvc.perform(get("/gateway/wms/pda/shelf/v2/getRecommendCell/v2").param("putawayNo", "PA2611000001"))
                .andExpect(jsonPath("$.data.skuList.total").value(1))
                .andExpect(jsonPath("$.data.skuList.records[0].productSku").value("SKU-0012"));
        postJson("/pda/shelf/v2/box/submitShelf", "{\"putawayNo\":\"PA2611000001\",\"boxBarcodeList\":[{\"boxBarcode\":\"BX-0001-01\",\"cellNo\":\"A-01-02\"}]}")
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void recommendCellListsBoxTypes() throws Exception {
        postJson("/pda/receipt/BoxTypeReceipt", "{\"asnNo\":\"RA2611000002\",\"boxTypeDTOS\":[{\"boxType\":\"BOX-0011-A\",\"receiptQty\":2}]}");
        mvc.perform(get("/gateway/wms/pda/shelf/v2/getRecommendCell/v2").param("putawayNo", "PA2611000002"))
                .andExpect(jsonPath("$.data.putawayType").value(2))
                .andExpect(jsonPath("$.data.boxList.records[0].boxTypeList[0].waitShelfBoxQty").value(2))
                .andExpect(jsonPath("$.data.boxList.records[0].boxTypeList[0].recommendCell").value("F-02-01"));
    }
}
