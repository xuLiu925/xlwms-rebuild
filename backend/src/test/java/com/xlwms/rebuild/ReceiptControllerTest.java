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

/** 原批次 1 的 4 个收货接口，改为接到 ReceiptStore 后的有状态测试。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class ReceiptControllerTest {
    @Autowired
    MockMvc mvc;

    @BeforeEach
    void reset() throws Exception {
        mvc.perform(post("/gateway/wms/demo/reset"));
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post("/gateway/wms" + url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions orderDtl(String asnNo) throws Exception {
        return mvc.perform(get("/gateway/wms/pda/receipt/getOrderDtl").param("asnNo", asnNo));
    }

    @Test
    void skuReceiptLooseAndOnPallet() throws Exception {
        postJson("/pda/receipt/scanPallet", "{\"palletCode\":\"PL-0009\"}").andExpect(jsonPath("$.data.status").value(0));
        postJson("/pda/receipt/skuReceipt", "{\"asnNo\":\"RA2611000005\",\"rowVersion\":\"1\","
                + "\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":10}],"
                + "\"skuPalletList\":[{\"palletCode\":\"PL-0009\",\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":5},{\"productSku\":\"SKU-0003\",\"receiptQty\":6}]}]}")
                .andExpect(jsonPath("$.data").value("PA2611000005"));
        orderDtl("RA2611000005")
                .andExpect(jsonPath("$.data.status").value(11))
                .andExpect(jsonPath("$.data.receiptQty").value(21));
        postJson("/pda/receipt/scanPallet", "{\"palletCode\":\"PL-0009\"}").andExpect(jsonPath("$.data.status").value(1));

        // 超收（散收 + 托盘合计）整体拒绝，数量不变
        postJson("/pda/receipt/skuReceipt", "{\"asnNo\":\"RA2611000005\",\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":3}],"
                + "\"skuPalletList\":[{\"palletCode\":\"PL-0010\",\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":3}]}]}")
                .andExpect(jsonPath("$.msg").value("SKU SKU-0006 收货数量超出预报，剩余可收 5"));
        orderDtl("RA2611000005").andExpect(jsonPath("$.data.receiptQty").value(21));
        postJson("/pda/receipt/skuReceipt", "{\"asnNo\":\"RA2611000005\",\"rowVersion\":\"1\",\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":1}]}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/pda/receipt/skuReceipt", "{\"asnNo\":\"RA2611000005\",\"skuPalletList\":[{\"palletCode\":\"PL-0009\",\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":1}]}]}")
                .andExpect(jsonPath("$.msg").value("托盘号为空或已被占用：PL-0009"));
        postJson("/pda/receipt/skuReceipt", "{\"asnNo\":\"RA2611000001\",\"skuList\":[{\"productSku\":\"SKU-0001\",\"receiptQty\":1}]}")
                .andExpect(jsonPath("$.msg").value("入库单 RA2611000001 不是按 SKU 预报"));

        // 托盘上的散件可以整托上架
        postJson("/pda/shelf/submitPalletShelf", "{\"palletCode\":\"PL-0009\",\"shelfCellNo\":\"E-01-02\"}").andExpect(jsonPath("$.code").value(200));
        postJson("/pda/stock/check", "{\"cellNo\":\"E-01-02\",\"productSku\":\"SKU-0003\"}").andExpect(jsonPath("$.data.useStkQty").value("6"));
        postJson("/pda/receipt/scanPallet", "{\"palletCode\":\"PL-0009\"}").andExpect(jsonPath("$.data.status").value(0));
    }

    @Test
    void wholeOrderReceiptReceivesEverythingRemaining() throws Exception {
        postJson("/pda/receipt/skuReceipt", "{\"asnNo\":\"RA2611000005\",\"skuList\":[{\"productSku\":\"SKU-0006\",\"receiptQty\":4}]}");
        postJson("/pda/receipt/wholeOrderReceipt", "{\"asnNo\":\"RA2611000005\"}").andExpect(jsonPath("$.code").value(200));
        orderDtl("RA2611000005")
                .andExpect(jsonPath("$.data.status").value(15))
                .andExpect(jsonPath("$.data.receiptQty").value(26));
        postJson("/pda/receipt/wholeOrderReceipt", "{\"asnNo\":\"RA2611000005\"}").andExpect(jsonPath("$.msg").value("入库单已完成收货：RA2611000005"));

        postJson("/pda/receipt/wholeOrderReceipt", "{\"asnNo\":\"RA2611000002\",\"rowVersion\":\"1\"}").andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/gateway/wms/pda/receipt/listBoxType").param("asnNo", "RA2611000002"))
                .andExpect(jsonPath("$.data.records[0].status").value(2))
                .andExpect(jsonPath("$.data.records[1].status").value(2));
    }

    @Test
    void getBoxTypeInfoUsesMeasuredSizeWhenPresent() throws Exception {
        postJson("/pda/receipt/getBoxTypeInfo", "{\"asnNo\":\"RA2611000002\"}")
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].length").value("60.00"));
        postJson("/pda/receipt/getBoxTypeInfo", "{\"asnNo\":\"RA2611000002\",\"boxTypeList\":[\"BOX-9\"]}").andExpect(jsonPath("$.code").value(500));
        postJson("/blReceipt/measureBoxType", "{\"asnNo\":\"RA2611000002\",\"wmsBoxTypeInfoDTOList\":[{\"boxType\":\"BOX-0011-A\",\"wmsBoxLength\":\"91\",\"wmsBoxWidth\":\"55\",\"wmsBoxHeight\":\"30\",\"wmsBoxWeight\":\"21\"}]}");
        postJson("/pda/receipt/getBoxTypeInfo", "{\"asnNo\":\"RA2611000002\",\"boxTypeList\":[\"BOX-0011-A\"]}")
                .andExpect(jsonPath("$.data[0].length").value("91.00"));
        postJson("/pda/receipt/scanPallet", "{\"palletCode\":\" \"}").andExpect(jsonPath("$.code").value(500));
    }
}
