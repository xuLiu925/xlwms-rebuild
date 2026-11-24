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

/** 批次 4：PDA 库内（stock）、盘点（inventoryCheck）、出库确认与大货复核（delivery）、按箱拣货（wave box）。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class PdaBatch4Test {
    @Autowired
    MockMvc mvc;

    @BeforeEach
    void reset() throws Exception {
        mvc.perform(post("/gateway/wms/demo/reset"));
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post("/gateway/wms/pda" + url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions getUrl(String url, String... kv) throws Exception {
        var b = get("/gateway/wms/pda" + url);
        for (int i = 0; i < kv.length; i += 2) {
            b = b.param(kv[i], kv[i + 1]);
        }
        return mvc.perform(b);
    }

    private ResultActions stockCheck(String cell, String sku) throws Exception {
        return postJson("/stock/check", "{\"cellNo\":\"" + cell + "\",\"productSku\":\"" + sku + "\"}");
    }

    // ---------- stock ----------

    @Test
    void stockQueries() throws Exception {
        getUrl("/stock/count", "cellNo", "A-01-01", "whCode", "WH01")
                .andExpect(jsonPath("$.data.skuCount").value(2))
                .andExpect(jsonPath("$.data.boxCount").value(0));
        getUrl("/stock/count", "cellNo", "F-01-01").andExpect(jsonPath("$.data.boxCount").value(6));
        getUrl("/stock/cell/list", "productSku", "SKU-0004", "size", "10", "current", "1")
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.records[0]").value("A-01-01"));
        postJson("/stock/cellList", "{\"cellNo\":\"A-01-01\",\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[0].customerStockCellList.records[0].useStkQty").value(20));
        postJson("/stock/customer/cellList", "{\"cellNo\":\"A-02-01\",\"customerCode\":\"C001\",\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[1].productQualityName").value("次品"));
        postJson("/stock/detail", "{\"productSku\":\"SKU-0004\",\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.totalStock").value(35))
                .andExpect(jsonPath("$.data.stockList.records.length()").value(2));
        postJson("/stock/search", "{\"queryParam\":\"BC00010006\",\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.productList.records[0].validCount").value(40))
                .andExpect(jsonPath("$.data.stockCellList.total").value(0));
        postJson("/stock/search", "{\"queryParam\":\"BOX-0010-A\",\"queryType\":3,\"current\":1,\"size\":10}")
                .andExpect(jsonPath("$.data.stockBoxList.records[0].validCount").value(10));
    }

    @Test
    void adjustMoveAndMoveCell() throws Exception {
        postJson("/stock/adjust", "{\"cellNo\":\"A-01-02\",\"productSku\":\"SKU-0002\",\"adjustFlag\":2,\"adjustQty\":\"5\",\"beforeStkQty\":\"15\"}")
                .andExpect(jsonPath("$.code").value(200));
        stockCheck("A-01-02", "SKU-0002").andExpect(jsonPath("$.data.useStkQty").value("10"));
        // beforeStkQty 过期 → 拒绝
        postJson("/stock/adjust", "{\"cellNo\":\"A-01-02\",\"productSku\":\"SKU-0002\",\"adjustFlag\":1,\"adjustQty\":\"1\",\"beforeStkQty\":\"15\"}")
                .andExpect(jsonPath("$.code").value(500));
        // 调减到负数 → 拒绝
        postJson("/stock/adjust", "{\"cellNo\":\"A-01-02\",\"productSku\":\"SKU-0002\",\"adjustFlag\":2,\"adjustQty\":\"11\"}")
                .andExpect(jsonPath("$.code").value(500));

        postJson("/stock/move", "{\"sourceCellNo\":\"B-01-01\",\"targetCellNo\":\"E-01-01\",\"productSku\":\"SKU-0004\",\"moveQty\":\"10\"}")
                .andExpect(jsonPath("$.code").value(200));
        stockCheck("B-01-01", "SKU-0004").andExpect(jsonPath("$.data.useStkQty").value("20"));
        stockCheck("E-01-01", "SKU-0004").andExpect(jsonPath("$.data.useStkQty").value("10"));
        postJson("/stock/move", "{\"sourceCellNo\":\"B-01-01\",\"targetCellNo\":\"E-09-09\",\"productSku\":\"SKU-0004\",\"moveQty\":\"1\"}")
                .andExpect(jsonPath("$.msg").value("库位已锁定：E-09-09"));
        postJson("/stock/move", "{\"sourceCellNo\":\"B-01-01\",\"targetCellNo\":\"E-01-02\",\"productSku\":\"SKU-0004\",\"moveQty\":\"999\"}")
                .andExpect(jsonPath("$.code").value(500));

        postJson("/stock/stockCellQuality", "{\"sourceCellNo\":\"A-02-01\",\"targetCellNo\":\"E-01-02\"}").andExpect(jsonPath("$.code").value(200));
        getUrl("/stock/count", "cellNo", "A-02-01").andExpect(jsonPath("$.data.skuCount").value(0));
        postJson("/stock/customer/cellList", "{\"cellNo\":\"E-01-02\",\"current\":1,\"size\":10}").andExpect(jsonPath("$.data.total").value(2));
    }

    // ---------- inventoryCheck ----------

    @Test
    void inventoryCheckReconcilesStock() throws Exception {
        String order = "{\"orderSn\":\"IC2611000001\",\"whCode\":\"WH01\"}";
        postJson("/inventoryCheck/list", "{\"current\":1,\"size\":10}").andExpect(jsonPath("$.data.total").value(2));
        postJson("/inventoryCheck/scanSku", "{\"orderSn\":\"IC2611000001\",\"cellNo\":\"A-01-01\",\"scanCode\":\"BC00010001\"}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/inventoryCheck/start", order).andExpect(jsonPath("$.data.length()").value(0));
        postJson("/inventoryCheck/overview", order)
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.cellList[0].dtlList.length()").value(2))
                .andExpect(jsonPath("$.data.cellList[0].dtlList[0].bookStock").value(20));
        postJson("/inventoryCheck/scanSku", "{\"orderSn\":\"IC2611000001\",\"cellNo\":\"A-01-01\",\"scanCode\":\"BC00010001\"}")
                .andExpect(jsonPath("$.data[0].sku").value("SKU-0001"));
        // 账面没有的产品：盘盈新增一行
        postJson("/inventoryCheck/scanSku", "{\"orderSn\":\"IC2611000001\",\"cellNo\":\"A-01-02\",\"scanCode\":\"SKU-0005\"}")
                .andExpect(jsonPath("$.data[0].bookStock").value(0));
        postJson("/inventoryCheck/submitCell", "{\"orderSn\":\"IC2611000001\",\"cellNo\":\"A-01-01\",\"dtlList\":[{\"sku\":\"SKU-0001\",\"solidStock\":18},{\"sku\":\"SKU-0004\",\"solidStock\":5}]}")
                .andExpect(jsonPath("$.data.completedCellCount").value(1));
        postJson("/inventoryCheck/complete", order)
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].cause").value("库位未盘点"));
        postJson("/inventoryCheck/submitCell", "{\"orderSn\":\"IC2611000001\",\"cellNo\":\"A-01-02\",\"dtlList\":[{\"sku\":\"SKU-0002\",\"solidStock\":15},{\"sku\":\"SKU-0005\",\"solidStock\":2}]}");
        postJson("/inventoryCheck/submitCell", "{\"orderSn\":\"IC2611000001\",\"cellNo\":\"A-02-01\",\"dtlList\":[]}");
        postJson("/inventoryCheck/complete", order).andExpect(jsonPath("$.data.length()").value(0));
        stockCheck("A-01-01", "SKU-0001").andExpect(jsonPath("$.data.useStkQty").value("18"));
        stockCheck("A-01-02", "SKU-0005").andExpect(jsonPath("$.data.useStkQty").value("2"));
        stockCheck("A-02-01", "SKU-0003").andExpect(jsonPath("$.data.useStkQty").value("0"));
        postJson("/inventoryCheck/start", order).andExpect(jsonPath("$.data[0].cause").value("盘点单已完成"));
    }

    @Test
    void blindCheckHidesBookStock() throws Exception {
        String order = "{\"orderSn\":\"IC2611000002\"}";
        postJson("/inventoryCheck/start", order);
        postJson("/inventoryCheck/overview", order).andExpect(jsonPath("$.data.cellList[0].dtlList[0].bookStock").value(0));
    }

    // ---------- 按箱拣货 → 大货复核 → 出库确认 ----------

    @Test
    void boxPickReviewAndConfirm() throws Exception {
        getUrl("/wave/bulk/pickWay", "billNo", "FO2611000002", "whCode", "WH01")
                .andExpect(jsonPath("$.data.waveNo").value("BW26110001"))
                .andExpect(jsonPath("$.data.orderCount").value(2))
                .andExpect(jsonPath("$.data.totalCount").value(11));
        getUrl("/wave/boxPick/directPage", "waveNo", "BW26110001")
                .andExpect(jsonPath("$.data.skuPage.records[0].boxType").value("BOX-0010-A"))
                .andExpect(jsonPath("$.data.skuPage.records[0].planQty").value(8))
                .andExpect(jsonPath("$.data.skuPage.records[0].cellList.length()").value(2));
        getUrl("/wave/boxPageCell", "waveNo", "BW26110001")
                .andExpect(jsonPath("$.data.cellPageList.total").value(3))
                .andExpect(jsonPath("$.data.cellPageList.records[0].realQty").value(6));
        postJson("/wave/boxScanCell", "{\"waveNo\":\"BW26110001\",\"cellNo\":\"F-02-01\"}")
                .andExpect(jsonPath("$.data[0].boxType").value("BOX-0011-A"));
        postJson("/wave/scanBox", "{\"waveNo\":\"BW26110001\",\"cellNo\":\"F-01-01\",\"boxType\":\"BOX-0010-A\"}")
                .andExpect(jsonPath("$.data.totalPlanQty").value(8));

        // 拣货前不能复核
        getUrl("/delivery/review/scanOutbound", "paramNo", "FO2611000001").andExpect(jsonPath("$.code").value(500));

        postJson("/wave/boxPick", "{\"waveNo\":\"BW26110001\",\"cellNo\":\"F-01-01\",\"boxList\":[{\"boxType\":\"BOX-0010-A\",\"pickQty\":5}]}")
                .andExpect(jsonPath("$.data.pickStatus").value(1));
        postJson("/wave/boxPick", "{\"waveNo\":\"BW26110001\",\"cellNo\":\"F-01-02\",\"boxList\":[{\"boxType\":\"BOX-0010-A\",\"pickQty\":4}]}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/wave/recommendCell/", "waveNo", "BW26110001").andExpect(jsonPath("$.data.length()").value(2));
        postJson("/wave/boxPick", "{\"waveNo\":\"BW26110001\",\"cellNo\":\"F-01-02\",\"boxList\":[{\"boxType\":\"BOX-0010-A\",\"pickQty\":3}]}");
        postJson("/wave/boxPick", "{\"waveNo\":\"BW26110001\",\"cellNo\":\"F-02-01\",\"boxList\":[{\"boxType\":\"BOX-0011-A\",\"pickQty\":3}]}")
                .andExpect(jsonPath("$.data.pickStatus").value(2));
        // 按箱库存被扣减
        getUrl("/stock/count", "cellNo", "F-01-01").andExpect(jsonPath("$.data.boxCount").value(1));
        getUrl("/stock/count", "cellNo", "F-02-01").andExpect(jsonPath("$.data.boxCount").value(7));

        postJson("/wave/scanBoxBarcode", "{\"boxBarcode\":\"BOX-0011-A\",\"deliveryNo\":\"FO2611000002\"}").andExpect(jsonPath("$.code").value(500));
        postJson("/wave/scanBoxBarcode", "{\"boxBarcode\":\"BOX-0011-A\",\"deliveryNo\":\"FO2611000001\"}")
                .andExpect(jsonPath("$.data[0].boxLength").value("90.00"));

        getUrl("/delivery/review/scanOutbound", "paramNo", "SO2611000001")
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].waitOutBoundCount").value(5));
        getUrl("/delivery/review/getSysBaseParam", "whCode", "WH01").andExpect(jsonPath("$.data").value(false));
        postJson("/delivery/review/bulkConfirmDelivery", "{\"deliveryNo\":\"FO2611000001\",\"boxDtlList\":[{\"boxType\":\"BOX-0010-A\",\"realOutBoundCount\":5}]}")
                .andExpect(jsonPath("$.msg").value("箱型 BOX-0011-A 实出 0 箱，应出 3 箱"));
        postJson("/delivery/review/bulkConfirmDelivery", "{\"deliveryNo\":\"FO2611000001\",\"boxDtlList\":[{\"boxType\":\"BOX-0010-A\",\"realOutBoundCount\":5},{\"boxType\":\"BOX-0011-A\",\"realOutBoundCount\":3}]}")
                .andExpect(jsonPath("$.code").value(200));

        getUrl("/delivery/getConfirmInfo", "paramNo", "FO2611000001", "outboundType", "2")
                .andExpect(jsonPath("$.data[0].boxQty").value(8))
                .andExpect(jsonPath("$.data[0].boxTypeQty").value(2));
        // 整批校验：FO2 还没复核，整批都不出库
        postJson("/delivery/bulkConfirmDelivery", "{\"pallets\":2,\"confirmDeliveryDTOList\":[{\"deliveryNo\":\"FO2611000001\"},{\"deliveryNo\":\"FO2611000002\"}]}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/delivery/getConfirmInfo", "paramNo", "FO2611000001", "outboundType", "2").andExpect(jsonPath("$.code").value(200));
        postJson("/delivery/bulkConfirmDelivery", "{\"pallets\":2,\"confirmDeliveryDTOList\":[{\"deliveryNo\":\"FO2611000001\"}]}")
                .andExpect(jsonPath("$.code").value(200));
        getUrl("/delivery/getConfirmInfo", "paramNo", "FO2611000001", "outboundType", "2").andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void parcelConfirmDeliveryAndCustomerCode4More() throws Exception {
        getUrl("/review/getCustomerCode4More", "deliveryNo", "OB2611000401", "whCode", "WH01")
                .andExpect(jsonPath("$.data.delivery.deliveryNo").value("OB2611000401"))
                .andExpect(jsonPath("$.data.express[0].expressNo").value("TRK0000000401"))
                .andExpect(jsonPath("$.data.orderType").value(2));
        // 一件代发：复核完成后才能确认出库
        postJson("/delivery/confirmDelivery", "{\"deliveryNoList\":[\"OB2611000301\"]}").andExpect(jsonPath("$.code").value(500));
        postJson("/review/review4packageV2", "{\"deliveryNo\":\"OB2611000301\",\"oneClickPass\":true}");
        getUrl("/delivery/getConfirmInfo", "paramNo", "TRK0000000301", "outboundType", "1")
                .andExpect(jsonPath("$.data[0].deliveryNo").value("OB2611000301"));
        postJson("/delivery/confirmDelivery", "{\"deliveryNoList\":[\"OB2611000301\"]}").andExpect(jsonPath("$.code").value(200));
        getUrl("/delivery/getConfirmInfo", "paramNo", "OB2611000301", "outboundType", "1").andExpect(jsonPath("$.code").value(500));
    }
}
