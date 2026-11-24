package com.xlwms.rebuild;

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
class ReceiptControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void scanPalletEchoesRequestFields() throws Exception {
        mvc.perform(post("/gateway/wms/pda/receipt/scanPallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"palletCode\":\"PLT-001\",\"whCode\":\"WH01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"))
                .andExpect(jsonPath("$.data.palletCode").value("PLT-001"))
                .andExpect(jsonPath("$.data.skuList[0].productSku").value("SKU-EXAMPLE-001"));
    }

    @Test
    void skuReceiptCountsSubmittedItems() throws Exception {
        String body = "{\"asnNo\":\"ASN1\",\"skuList\":[{\"productSku\":\"S1\",\"receiptQty\":5}],"
                + "\"skuPalletList\":[]}";
        mvc.perform(post("/gateway/wms/pda/receipt/skuReceipt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.receiptSkuCount").value(1))
                .andExpect(jsonPath("$.data.rowVersion").value("2"));
    }

    @Test
    void wholeOrderReceiptSetsReceivedStatus() throws Exception {
        mvc.perform(post("/gateway/wms/pda/receipt/wholeOrderReceipt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asnNo\":\"ASN1\",\"sourceNo\":\"IB1\",\"rowVersion\":\"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(15))
                .andExpect(jsonPath("$.data.statusName").value("已收货"));
    }
}
