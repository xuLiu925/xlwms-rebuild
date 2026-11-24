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

/** 批次 5：PDA 收货 → 上架（写入库存）与新品维护。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class PdaBatch5Test {
    @Autowired
    MockMvc mvc;

    @BeforeEach
    void reset() throws Exception {
        mvc.perform(post("/gateway/wms/demo/reset"));
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post("/gateway/wms" + url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions getUrl(String url, String... kv) throws Exception {
        var b = get("/gateway/wms" + url);
        for (int i = 0; i < kv.length; i += 2) {
            b = b.param(kv[i], kv[i + 1]);
        }
        return mvc.perform(b);
    }

    private ResultActions qty(String cell, String sku) throws Exception {
        return postJson("/pda/stock/check", "{\"cellNo\":\"" + cell + "\",\"productSku\":\"" + sku + "\"}");
    }

    // ---------- 收货查询 ----------

    @Test
    void receiptQueries() throws Exception {
        getUrl("/pda/receipt/getAllReceiptList").andExpect(jsonPath("$.data.length()").value(4));
        getUrl("/pda/receipt/getAsn", "param", "CS2611000001").andExpect(jsonPath("$.data[0].asnNo").value("RA2611000001"));
        getUrl("/pda/receipt/getAsn", "param", "nope").andExpect(jsonPath("$.code").value(500));
        getUrl("/pda/receipt/getOrderDtl", "asnNo", "RA2611000001")
                .andExpect(jsonPath("$.data.asnQty").value(29))
                .andExpect(jsonPath("$.data.boxCount").value(3))
                .andExpect(jsonPath("$.data.status").value(10));
        postJson("/pda/receipt/getOrderDtl/skuDetailPage", "{\"asnNo\":\"RA2611000001\",\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.records[?(@.productSku=='SKU-0012')].isNew").value(1));
        getUrl("/pda/receipt/v2/getBoxDtl", "asnNo", "RA2611000001", "barcode", "BC00010001").andExpect(jsonPath("$.data.length()").value(2));
        getUrl("/pda/receipt/listBoxType", "asnNo", "RA2611000002").andExpect(jsonPath("$.data.total").value(2));
        getUrl("/pda/receipt/getBoxTypeDtl", "asnNo", "RA2611000002", "boxType", "BOX-0010-A")
                .andExpect(jsonPath("$.data[0].length").value("60.00"))
                .andExpect(jsonPath("$.data[0].needReceiptQty").value(4));
        getUrl("/pda/receipt/batchReceipt/getAsnDtl", "param", "RA2611000002").andExpect(jsonPath("$.data[0].boxTypeList.length()").value(2));
    }

    // ---------- 按箱收货 → 按 SKU / 整箱 / 一键上架 ----------

    @Test
    void wholeBoxReceiptThenPutaway() throws Exception {
        postJson("/pda/receipt/wholeBoxReceipt", "{\"asnNo\":\"RA2611000001\",\"rowVersion\":\"1\",\"boxList\":["
                + "{\"boxBarcode\":\"BX-0001-01\",\"skuList\":[{\"productSku\":\"SKU-0001\",\"receiptQty\":9},{\"productSku\":\"SKU-0002\",\"receiptQty\":5}]},"
                + "{\"boxBarcode\":\"BX-0001-02\"}]}")
                .andExpect(jsonPath("$.data").value("PA2611000001"));
        getUrl("/pda/receipt/getOrderDtl", "asnNo", "RA2611000001")
                .andExpect(jsonPath("$.data.status").value(11))
                .andExpect(jsonPath("$.data.rowVersion").value("3"));
        // 重复收、超收、旧版本号都拒绝
        postJson("/pda/receipt/wholeBoxReceipt", "{\"asnNo\":\"RA2611000001\",\"boxList\":[{\"boxBarcode\":\"BX-0001-01\"}]}")
                .andExpect(jsonPath("$.msg").value("该箱已收货：BX-0001-01"));
        postJson("/pda/receipt/wholeBoxReceipt", "{\"asnNo\":\"RA2611000001\",\"boxList\":[{\"boxBarcode\":\"BX-0001-03\",\"skuList\":[{\"productSku\":\"SKU-0012\",\"receiptQty\":5}]}]}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/pda/receipt/wholeBoxReceipt", "{\"asnNo\":\"RA2611000001\",\"rowVersion\":\"1\",\"boxList\":[{\"boxBarcode\":\"BX-0001-03\"}]}")
                .andExpect(jsonPath("$.code").value(500));
        // 少收一箱也可以完成收货
        postJson("/pda/receipt/finishWholeBoxReceipt", "{\"asnNo\":\"RA2611000001\"}").andExpect(jsonPath("$.data").value("PA2611000001"));
        getUrl("/pda/receipt/getAllReceiptList").andExpect(jsonPath("$.data.length()").value(3));

        getUrl("/pda/shelf/merge/page").andExpect(jsonPath("$.data.records[0].putawayNo").value("PA2611000001"));
        getUrl("/pda/shelf/merge/scanContent", "content", "BX-0001-02").andExpect(jsonPath("$.data[0].putawayType").value(1));
        getUrl("/pda/shelf/scanBarcode", "barcode", "BC00010001", "putawayNo", "PA2611000001")
                .andExpect(jsonPath("$.data.receiptQty").value(19))
                .andExpect(jsonPath("$.data.recommendCell").value("A-01-01"));
        getUrl("/pda/shelf/box/listBoxBarCode", "putawayNo", "PA2611000001")
                .andExpect(jsonPath("$.data.boxItems.length()").value(2))
                .andExpect(jsonPath("$.data.boxItems[0].status").value(0));
        getUrl("/pda/shelf/v3/box/scanBoxBarcode", "boxBarcode", "BX-0001-02", "putawayNo", "PA2611000001")
                .andExpect(jsonPath("$.data[0].skuItems[0].receiptQty").value(10));

        postJson("/pda/shelf/batch/submit", "{\"cellNo\":\"E-01-01\",\"orderList\":[{\"putawayNo\":\"PA2611000001\",\"boxList\":[{\"boxBarcode\":\"BX-0001-02\"}]}]}")
                .andExpect(jsonPath("$.data.successQty").value(1))
                .andExpect(jsonPath("$.data.failedQty").value(0));
        qty("E-01-01", "SKU-0001").andExpect(jsonPath("$.data.useStkQty").value("10"));
        getUrl("/pda/shelf/v2/scanShelfByCellNo", "putawayNo", "PA2611000001", "cellNo", "A-01-01")
                .andExpect(jsonPath("$.data.skuList.length()").value(2));
        getUrl("/pda/shelf/onoPieceCompleteOrder", "putawayNo", "PA2611000001").andExpect(jsonPath("$.data.pendingSkuQty").value(14));
        postJson("/pda/shelf/commitCompleteOrder", "{\"putawayNo\":\"PA2611000001\",\"cellNo\":\"E-09-09\"}").andExpect(jsonPath("$.msg").value("库位已锁定：E-09-09"));
        postJson("/pda/shelf/commitCompleteOrder", "{\"putawayNo\":\"PA2611000001\",\"cellNo\":\"A-01-01\"}").andExpect(jsonPath("$.code").value(200));
        qty("A-01-01", "SKU-0001").andExpect(jsonPath("$.data.useStkQty").value("29"));
        qty("A-01-01", "SKU-0002").andExpect(jsonPath("$.data.useStkQty").value("5"));
        getUrl("/pda/shelf/merge/page").andExpect(jsonPath("$.data.total").value(0));
    }

    // ---------- 按箱型收货 → 中转按箱型上架 / 批量上架 ----------

    @Test
    void boxTypeReceiptThenCrossDockAndBatchPutaway() throws Exception {
        postJson("/pda/receipt/BoxTypeReceipt", "{\"asnNo\":\"RA2611000003\",\"boxTypeDTOS\":[{\"boxType\":\"BOX-0010-A\",\"receiptQty\":4}]}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/pda/receipt/BoxTypeReceipt", "{\"asnNo\":\"RA2611000003\",\"boxTypeDTOS\":[{\"boxType\":\"BOX-0010-A\",\"receiptQty\":3}]}")
                .andExpect(jsonPath("$.data").value("PA2611000003"));
        getUrl("/pda/shelf/crossDockCompleteOrder", "putawayNo", "PA2611000003").andExpect(jsonPath("$.data.pendingBoxQty").value(3));
        getUrl("/pda/shelf/v2/crossDock/scanBoxType", "boxType", "BOX-0010-A", "putawayNo", "PA2611000003")
                .andExpect(jsonPath("$.data[0].waitShelfBoxQty").value(3))
                .andExpect(jsonPath("$.data[0].recommendCell").value("F-01-01"));
        postJson("/pda/shelf/v2/crossDock/submitBoxType", "{\"putawayNo\":\"PA2611000003\",\"boxTypeList\":[{\"boxType\":\"BOX-0010-A\",\"boxQty\":2,\"cellNo\":\"F-01-02\"},{\"boxType\":\"BOX-0010-A\",\"boxQty\":2,\"cellNo\":\"E-01-02\"}]}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/pda/stock/count", "cellNo", "F-01-02").andExpect(jsonPath("$.data.boxCount").value(4));
        postJson("/pda/shelf/v2/crossDock/submitBoxType", "{\"putawayNo\":\"PA2611000003\",\"boxTypeList\":[{\"boxType\":\"BOX-0010-A\",\"boxQty\":2,\"cellNo\":\"F-01-02\"},{\"boxType\":\"BOX-0010-A\",\"boxQty\":1,\"cellNo\":\"E-01-02\"}]}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(1));
        getUrl("/pda/stock/count", "cellNo", "F-01-02").andExpect(jsonPath("$.data.boxCount").value(6));

        postJson("/pda/receipt/finishBoxTypeReceipt", "{\"asnNo\":\"RA2611000002\",\"boxTypeDTOS\":[{\"boxType\":\"BOX-0011-A\",\"receiptQty\":2}]}")
                .andExpect(jsonPath("$.code").value(200));
        getUrl("/pda/receipt/getOrderDtl", "asnNo", "RA2611000002").andExpect(jsonPath("$.data.status").value(15));
        getUrl("/pda/shelf/crossDockCompleteOrder", "putawayNo", "PA2611000002").andExpect(jsonPath("$.code").value(500));
        postJson("/pda/shelf/batch/scanBox", "{\"barcodes\":[\"BOX-0011-A\",\"NOPE\"]}")
                .andExpect(jsonPath("$.data[0].candidateList[0].pendingPutawayQty").value(2))
                .andExpect(jsonPath("$.data[1].candidateList.length()").value(0));
        postJson("/pda/shelf/batch/scanCell", "{\"cellNo\":\"E-09-09\"}").andExpect(jsonPath("$.code").value(500));
        postJson("/pda/shelf/batch/submit", "{\"cellNo\":\"F-02-01\",\"orderList\":[{\"putawayNo\":\"PA2611000002\",\"boxTypeList\":[{\"boxType\":\"BOX-0011-A\",\"putawayQty\":2,\"expectedPendingPutawayQty\":1}]}]}")
                .andExpect(jsonPath("$.data.failedList[0].failReason").value("箱型 BOX-0011-A 待上架数量已变化，请刷新"));
        postJson("/pda/shelf/batch/submit", "{\"cellNo\":\"F-02-01\",\"orderList\":[{\"putawayNo\":\"PA2611000002\",\"boxTypeList\":[{\"boxType\":\"BOX-0011-A\",\"putawayQty\":2,\"expectedPendingPutawayQty\":2}]}]}")
                .andExpect(jsonPath("$.data.successQty").value(2));
        getUrl("/pda/stock/count", "cellNo", "F-02-01").andExpect(jsonPath("$.data.boxCount").value(12));
    }

    // ---------- 托盘收货 → 整托上架 ----------

    @Test
    void palletReceiptThenPalletShelf() throws Exception {
        postJson("/pda/receipt/scanPalletSku", "{\"asnNo\":\"RA2611000004\",\"barcode\":\"BC00010005\"}").andExpect(jsonPath("$.data.length()").value(1));
        // 有一个无效箱唛 → 整个托盘收货都不生效
        postJson("/pda/receipt/boxPalletReceipt", "{\"asnNo\":\"RA2611000004\",\"palletList\":[{\"palletCode\":\"PL-0001\",\"palletBoxList\":[{\"boxBarcode\":\"BX-0004-01\"},{\"boxBarcode\":\"BX-9999\"}]}]}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/pda/receipt/v2/getBoxDtl", "asnNo", "RA2611000004", "boxBarcode", "BX-0004-01").andExpect(jsonPath("$.data[0].receiptFlag").value(0));
        postJson("/pda/receipt/boxPalletReceipt", "{\"asnNo\":\"RA2611000001\",\"palletList\":[{\"palletCode\":\"PL-0002\",\"palletBoxList\":[{\"boxBarcode\":\"BX-0001-01\"}]}]}")
                .andExpect(jsonPath("$.msg").value("入库单 RA2611000001 不是托盘收货单"));
        postJson("/pda/receipt/boxPalletReceipt", "{\"asnNo\":\"RA2611000004\",\"palletList\":[{\"palletCode\":\"PL-0001\",\"palletBoxList\":[{\"boxBarcode\":\"BX-0004-01\"},{\"boxBarcode\":\"BX-0004-02\"}]}]}")
                .andExpect(jsonPath("$.code").value(200));
        postJson("/pda/receipt/boxPalletReceipt", "{\"asnNo\":\"RA2611000004\",\"palletList\":[{\"palletCode\":\"PL-0001\",\"palletBoxList\":[]}]}")
                .andExpect(jsonPath("$.msg").value("托盘已被占用：PL-0001"));

        postJson("/pda/shelf/scanPalletCode", "{\"palletCode\":\"PL-0001\"}")
                .andExpect(jsonPath("$.data.putawayNo").value("PA2611000004"))
                .andExpect(jsonPath("$.data.recommendCell").value("A-01-01"));
        getUrl("/pda/shelf/merge/page", "putAwayType", "3").andExpect(jsonPath("$.data.total").value(1));
        postJson("/pda/shelf/preCheckPalletList", "{\"putawayNo\":\"PA2611000004\",\"palletList\":[{\"palletCode\":\"PL-0001\",\"shelfCellNo\":\"E-01-01\"}]}")
                .andExpect(jsonPath("$.data.length()").value(0));
        postJson("/pda/shelf/submitPalletShelf", "{\"palletCode\":\"PL-0001\",\"shelfCellNo\":\"E-01-01\"}").andExpect(jsonPath("$.data.putawayNo").value("PA2611000004"));
        qty("E-01-01", "SKU-0004").andExpect(jsonPath("$.data.useStkQty").value("12"));
        qty("E-01-01", "SKU-0013").andExpect(jsonPath("$.data.useStkQty").value("3"));
        postJson("/pda/shelf/scanPalletCode", "{\"palletCode\":\"PL-0001\"}").andExpect(jsonPath("$.msg").value("托盘已上架到 E-01-01"));
    }

    // ---------- 批量收货、箱规测量 ----------

    @Test
    void batchReceiptAndMeasure() throws Exception {
        postJson("/pda/receipt/batchReceipt/submit", "{\"boxList\":[{\"asnNo\":\"RA2611000001\",\"boxList\":[{\"boxBarcode\":\"BX-0001-03\",\"skuList\":[{\"productSku\":\"SKU-0012\",\"receiptQty\":99}]}]}],"
                + "\"boxTypeList\":[{\"asnNo\":\"RA2611000002\",\"boxTypeDTOS\":[{\"boxType\":\"BOX-0010-A\",\"receiptQty\":4}]}]}")
                .andExpect(jsonPath("$.data.failedList.length()").value(1))
                .andExpect(jsonPath("$.data.failedList[0].asnNo").value("RA2611000001"));
        getUrl("/pda/receipt/listBoxType", "asnNo", "RA2611000002", "boxType", "BOX-0010-A")
                .andExpect(jsonPath("$.data.records[0].realReceiptQty").value(4))
                .andExpect(jsonPath("$.data.records[0].status").value(2));

        postJson("/pda/receipt/measureBoxType/batch", "{\"boxTypeInfoList\":["
                + "{\"asnNo\":\"RA2611000002\",\"wmsBoxTypeInfoDTOList\":[{\"boxType\":\"BOX-0010-A\",\"wmsBoxLength\":\"61\",\"wmsBoxWidth\":\"40\",\"wmsBoxHeight\":\"40\",\"wmsBoxWeight\":\"15\"}]},"
                + "{\"asnNo\":\"RA2611000003\",\"wmsBoxTypeInfoDTOList\":[{\"boxType\":\"BOX-0011-A\",\"wmsBoxLength\":\"1\",\"wmsBoxWidth\":\"1\",\"wmsBoxHeight\":\"1\",\"wmsBoxWeight\":\"1\"}]}]}")
                .andExpect(jsonPath("$.data[0].errorMsg").value(""))
                .andExpect(jsonPath("$.data[1].errorMsg").value("箱型不属于入库单 RA2611000003：BOX-0011-A"));
        getUrl("/pda/receipt/getBoxTypeDtl", "asnNo", "RA2611000002", "boxType", "BOX-0010-A").andExpect(jsonPath("$.data[0].length").value("61.00"));
        postJson("/blReceipt/measureBoxType", "{\"asnNo\":\"RA2611000002\",\"wmsBoxTypeInfoDTOList\":[{\"boxType\":\"BOX-0011-A\",\"wmsBoxLength\":\"abc\"}]}")
                .andExpect(jsonPath("$.code").value(500));
    }

    // ---------- 新品维护 ----------

    @Test
    void newProductMaintenance() throws Exception {
        postJson("/pda/newProduct/getNeedMaintain", "{\"asnNo\":\"RA2611000001\"}")
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].productSku").value("SKU-0012"))
                .andExpect(jsonPath("$.data[0].length").value(""));
        postJson("/pda/newProduct/listNeedNewProductMaintain", "[{\"asnNo\":\"RA2611000001\"},{\"asnNo\":\"RA2611000004\"}]")
                .andExpect(jsonPath("$.data.length()").value(2));
        postJson("/pda/newProduct/getNeedNewProductMaintain", "{\"asnNo\":\"RA2611000004\",\"skuList\":[\"SKU-0005\"]}")
                .andExpect(jsonPath("$.data.length()").value(0));
        postJson("/pda/newProduct/receiptMeasure", "[{\"productSku\":\"SKU-0012\",\"wmsLength\":\"12\",\"wmsWidth\":\"8\",\"wmsHeight\":\"20\",\"wmsWeight\":\"0\"}]")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/pda/newProduct/receiptMeasure", "[{\"productSku\":\"SKU-0012\",\"wmsLength\":\"12\",\"wmsWidth\":\"8\",\"wmsHeight\":\"20\",\"wmsWeight\":\"0.35\"}]")
                .andExpect(jsonPath("$.code").value(200));
        postJson("/pda/newProduct/getNeedMaintain", "{\"asnNo\":\"RA2611000001\"}").andExpect(jsonPath("$.data.length()").value(0));
        postJson("/pda/receipt/getOrderDtl/skuDetailPage", "{\"asnNo\":\"RA2611000001\",\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.records[?(@.productSku=='SKU-0012')].isNew").value(0));
        postJson("/pda/newProduct/returnInNeedMaintain", "{\"skuFnSkuList\":[{\"fnsku\":\"FN-DEMO-0013\"},{\"productSku\":\"SKU-0001\"}]}")
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].productSku").value("SKU-0013"));
    }

    @Test
    void selectCellSkipsLocked() throws Exception {
        getUrl("/shelf/selectCell", "cellNo", "E")
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[0].isNullCell").value(1));
    }
}
