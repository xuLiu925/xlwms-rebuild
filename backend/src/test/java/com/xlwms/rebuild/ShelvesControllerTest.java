package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ShelvesControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void scanBarcodeReturnsPutawayInfo() throws Exception {
        mvc.perform(get("/gateway/wms/pda/shelf/v2/scanBarcode")
                        .param("barcode", "BC-001")
                        .param("whCode", "WH01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.barcode").value("BC-001"))
                .andExpect(jsonPath("$.data.recommendCell").value("A-01-01"));
    }

    @Test
    void confirmCountsSkuItems() throws Exception {
        String body = "{\"putawayNo\":\"PA1\",\"skuList\":[{\"cellNo\":\"A-01-01\",\"productSku\":\"S1\",\"putawayQty\":3}]}";
        mvc.perform(post("/gateway/wms/pda/shelf/v2/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.putawaySkuCount").value(1))
                .andExpect(jsonPath("$.data.finished").value(true));
    }

    @Test
    void submitShelfCountsBoxes() throws Exception {
        String body = "{\"putawayNo\":\"PA1\",\"boxBarcodeList\":["
                + "{\"boxBarcode\":\"B1\",\"cellNo\":\"A-01-01\"},"
                + "{\"boxBarcode\":\"B2\",\"cellNo\":\"A-01-02\"}]}";
        mvc.perform(post("/gateway/wms/pda/shelf/v2/box/submitShelf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.putawayBoxCount").value(2));
    }
}
