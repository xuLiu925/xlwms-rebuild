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
class ReturnPutawayControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void scanBarcodeReturnsOrders() throws Exception {
        mvc.perform(post("/gateway/wms/pda/return/putaway/scanBarcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scanContent\":\"BC-001\",\"whCode\":\"WH01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].scanContent").value("BC-001"))
                .andExpect(jsonPath("$.data[0].statusName").value("待上架"));
    }

    @Test
    void listOrderIsPaged() throws Exception {
        mvc.perform(get("/gateway/wms/pda/return/putaway/listOrder").param("whCode", "WH01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].returnNo").value("RT0000000000XX"));
    }

    @Test
    void submitCountsBatches() throws Exception {
        String body = "{\"returnNo\":\"RT1\",\"skuId\":\"S1\",\"qty\":7,"
                + "\"putawayExpirationSubmitDTOList\":[{\"cellNo\":\"A-01-01\",\"qty\":7}]}";
        mvc.perform(post("/gateway/wms/pda/return/putaway/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.putawayQty").value(7))
                .andExpect(jsonPath("$.data.batchCount").value(1))
                .andExpect(jsonPath("$.data.success").value(true));
    }
}
