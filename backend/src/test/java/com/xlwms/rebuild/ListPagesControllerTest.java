package com.xlwms.rebuild;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ListPagesControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void deliveryPageFiltersAndPages() throws Exception {
        mvc.perform(post("/gateway/wms/blDelivery/page").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"50\",\"current\":1,\"size\":2}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.records[0].status").value(50))
                .andExpect(jsonPath("$.data.records[0].id").isString());
    }

    @Test
    void deliveryPageWorksWithoutBody() throws Exception {
        mvc.perform(post("/gateway/wms/blDelivery/page"))
                .andExpect(jsonPath("$.data.total").value(36));
    }

    @Test
    void deliveryTotalsSumToAll() throws Exception {
        mvc.perform(post("/gateway/wms/blDelivery/queryInTotal"))
                .andExpect(jsonPath("$.data.length()").value(7))
                .andExpect(jsonPath("$.data[0].statusName").value("待处理"));
    }

    @Test
    void stockPageKeywordFilter() throws Exception {
        mvc.perform(post("/gateway/wms/stockSku/page").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"keyword\":\"sku-0001\"}"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].sku").value("SKU-0001"));
    }

    @Test
    void stockTotalMatchesFilter() throws Exception {
        mvc.perform(post("/gateway/wms/stockSku/skuTotal").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockAttr\":\"次品\"}"))
                .andExpect(jsonPath("$.data.skuCount").value(4));
    }
}
