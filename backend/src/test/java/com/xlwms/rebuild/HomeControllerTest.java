package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void stockSummaryUsesEnvelope() throws Exception {
        mvc.perform(post("/gateway/wms/home/stockSummary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"))
                .andExpect(jsonPath("$.data.inStockNum").value("1000"));
    }

    @Test
    void longIdIsSerializedAsString() throws Exception {
        mvc.perform(post("/gateway/wms/blAsn/page"))
                .andExpect(jsonPath("$.data.records[0].id").value("1"));
    }
}
