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

/** 批次 6：PDA 退件（收货 / 新建 / 上架 / 销毁）与 FBA 退货（签收 / 清点 / 上架 / 认领 / 拣货）。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class PdaBatch6Test {
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

    private ResultActions qty(String cell, String sku, int quality) throws Exception {
        return postJson("/stock/check", "{\"cellNo\":\"" + cell + "\",\"productSku\":\"" + sku + "\",\"productQuality\":" + quality + "}");
    }

    // ---------- 退件收货 → 上架 / 销毁 ----------

    @Test
    void returnReceiveThenPutawayAndDestroy() throws Exception {
        getUrl("/return/receive/listOrder").andExpect(jsonPath("$.data.total").value(2));
        getUrl("/return/receive/scanOrderNo", "scanContent", "OB2611000101").andExpect(jsonPath("$.data[0].orderNo").value("RN2611000001"));
        getUrl("/return/receive/scanBarcode", "scanContent", "RTRK000002-2").andExpect(jsonPath("$.data[0].orderNo").value("RN2611000002"));
        getUrl("/return/receive/scanBarcode", "scanContent", "BC00010004").andExpect(jsonPath("$.data[0].orderNo").value("RN2611000002"));
        getUrl("/return/receive/scanBarcode", "scanContent", "NOPE").andExpect(jsonPath("$.code").value(500));
        getUrl("/return/receive/detail", "orderNo", "RN2611000001").andExpect(jsonPath("$.data.customerName").value("示例客户1"));
        getUrl("/return/receive/listProduct", "orderNo", "RN2611000001")
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[0].forecastQty").value(2));
        getUrl("/return/receive/listPackage", "orderNo", "RN2611000002")
                .andExpect(jsonPath("$.data.allQty").value("2"))
                .andExpect(jsonPath("$.data.waitReachQty").value("2"));
        getUrl("/return/receive/detail/scanBarcode", "orderNo", "RN2611000001", "scanContent", "BC00010003")
                .andExpect(jsonPath("$.data.forecast").value(false));
        getUrl("/return/receive/detail/scanBarcode", "orderNo", "RN2611000001", "scanContent", "BC00010004").andExpect(jsonPath("$.code").value(500));
        getUrl("/return/receive/detail/scanExpressNo", "orderNo", "RN2611000001", "scanContent", "RTRK000001")
                .andExpect(jsonPath("$.data.packNo").value("RN2611000001-1"));
        postJson("/return/receive/scanProductSnNo", "{\"returnNo\":\"RN2611000001\",\"productSku\":\"SKU-0001\",\"productSnNo\":\"SN-1\"}")
                .andExpect(jsonPath("$.code").value(200));
        postJson("/return/receive/scanProductSnNo", "{\"returnNo\":\"RN2611000001\",\"productSku\":\"SKU-0001\",\"productSnNo\":\"SN-1\"}")
                .andExpect(jsonPath("$.msg").value("序列号已扫描：SN-1"));

        String skus = "\"skuList\":[{\"productSku\":\"SKU-0001\",\"goodQty\":1,\"defectiveQty\":1},{\"productSku\":\"SKU-0003\",\"goodQty\":1}]";
        postJson("/return/receive/submit", "{\"orderNo\":\"RN2611000001\",\"packageList\":[{\"packNo\":\"RN2611000001-1\",\"reach\":\"0\"}]," + skus + "}")
                .andExpect(jsonPath("$.msg").value("至少要有一个包裹到货"));
        postJson("/return/receive/submit", "{\"orderNo\":\"RN2611000001\",\"packageList\":[{\"packNo\":\"RN2611000001-1\",\"reach\":\"1\",\"tempCellNo\":\"E-09-09\"}]," + skus + "}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/return/receive/submit", "{\"orderNo\":\"RN2611000001\",\"packageList\":[{\"packNo\":\"RN2611000001-1\",\"reach\":\"1\",\"tempCellNo\":\"E-01-01\"}]," + skus + "}")
                .andExpect(jsonPath("$.code").value(200));
        postJson("/return/receive/submit", "{\"orderNo\":\"RN2611000001\",\"packageList\":[{\"packNo\":\"RN2611000001-1\",\"reach\":\"1\"}]," + skus + "}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/return/receive/listOrder").andExpect(jsonPath("$.data.total").value(1));

        getUrl("/return/putaway/listOrder").andExpect(jsonPath("$.data.total").value(2));
        postJson("/return/putaway/scanOrderNo", "{\"scanContent\":\"RN2611000001\"}").andExpect(jsonPath("$.data[0].status").value("20"));
        postJson("/return/putaway/scanBarcode", "{\"scanContent\":\"BC00010001\"}").andExpect(jsonPath("$.data[0].orderNo").value("RN2611000001"));
        getUrl("/return/putaway/detail/scanBarcode", "barcode", "BC00010001", "returnNo", "RN2611000001")
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[1].productQuality").value(2))
                .andExpect(jsonPath("$.data[1].cellNo").value("E-01-01"));
        getUrl("/return/putaway/scanCell", "cellNo", "A-01-01", "productQuality", "2").andExpect(jsonPath("$.msg").value("次品只能上架到 E 区库位：A-01-01"));
        getUrl("/return/putaway/scanCell", "cellNo", "A-01-01", "productQuality", "1").andExpect(jsonPath("$.code").value(200));

        postJson("/return/putaway/submit", "{\"returnNo\":\"RN2611000001\",\"barcode\":\"BC00010001\",\"productQuality\":1,\"cellNo\":\"A-01-01\",\"qty\":2}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/return/putaway/submit", "{\"returnNo\":\"RN2611000001\",\"barcode\":\"BC00010001\",\"productQuality\":1,\"cellNo\":\"A-01-01\",\"qty\":1}")
                .andExpect(jsonPath("$.data.status").value(20));
        qty("A-01-01", "SKU-0001", 1).andExpect(jsonPath("$.data.useStkQty").value("21"));
        postJson("/return/putaway/destruct", "{\"returnNo\":\"RN2611000001\",\"barcode\":\"BC00010001\",\"qty\":1}")
                .andExpect(jsonPath("$.msg").value("销毁必须填写处理说明"));
        postJson("/return/putaway/destruct", "{\"returnNo\":\"RN2611000001\",\"barcode\":\"BC00010001\",\"qty\":1,\"handleRemark\":\"外包装破损\"}")
                .andExpect(jsonPath("$.data.status").value(20));
        postJson("/return/putaway/submit", "{\"returnNo\":\"RN2611000001\",\"skuId\":\"SKU-0003\",\"productQuality\":1,"
                + "\"putawayExpirationSubmitDTOList\":[{\"cellNo\":\"A-02-01\",\"qty\":1}]}")
                .andExpect(jsonPath("$.data.status").value(30));
        qty("A-02-01", "SKU-0003", 1).andExpect(jsonPath("$.data.useStkQty").value("9"));

        postJson("/return/putaway/submit", "{\"returnNo\":\"RN2611000003\",\"barcode\":\"BC00010006\",\"productQuality\":2,\"cellNo\":\"E-01-01\",\"qty\":1}")
                .andExpect(jsonPath("$.data.status").value(20));
        qty("E-01-01", "SKU-0006", 2).andExpect(jsonPath("$.data.useStkQty").value("1"));
    }

    // ---------- PDA 新建退件 ----------

    @Test
    void createReturnOrderOnPda() throws Exception {
        getUrl("/return/create/getLastOrderType").andExpect(jsonPath("$.data").value(1));
        postJson("/return/create/updateLastOrderType", "{\"tab\":3}").andExpect(jsonPath("$.code").value(200));
        getUrl("/return/create/getLastOrderType").andExpect(jsonPath("$.data").value(3));
        postJson("/return/create/updateLastOrderType", "{\"tab\":9}").andExpect(jsonPath("$.code").value(500));
        postJson("/return/create/type", "{\"trackNo\":\"RTRK000002\"}")
                .andExpect(jsonPath("$.data.createType").value(2))
                .andExpect(jsonPath("$.data.customerCode").value("C002"));
        postJson("/return/create/type", "{\"trackNo\":\"NEWTRK1\"}").andExpect(jsonPath("$.data.createType").value(1));
        postJson("/return/create/scanBarcode", "{\"barcode\":\"BC00010008\",\"customerCode\":\"C001\"}").andExpect(jsonPath("$.code").value(500));
        postJson("/return/create/scanBarcode", "{\"barcode\":\"BC00010008\",\"customerCode\":\"C004\"}").andExpect(jsonPath("$.data.fnsku").value("FN-DEMO-0008"));

        String pkg = "\"packageList\":[{\"trackNo\":\"NEWTRK1\",\"tempCellNo\":\"E-01-02\"}]";
        postJson("/return/create/commitOrder", "{\"customerCode\":\"C004\",\"orderType\":3," + pkg + ",\"productList\":[{\"productSku\":\"SKU-0001\",\"goodQty\":1}]}")
                .andExpect(jsonPath("$.msg").value("SKU SKU-0001 不属于客户 C004"));
        postJson("/return/create/commitOrder", "{\"customerCode\":\"C004\"," + pkg + ",\"platformReturn\":{}}")
                .andExpect(jsonPath("$.msg").value("本地演示不支持平台退货建单"));
        postJson("/return/create/commitOrder", "{\"customerCode\":\"C004\",\"orderType\":3," + pkg + ",\"productList\":[{\"productSku\":\"SKU-0008\",\"goodQty\":2}]}")
                .andExpect(jsonPath("$.data").value("RN2611000004"));
        postJson("/return/create/commitOrder", "{\"customerCode\":\"C004\",\"orderType\":3," + pkg + ",\"productList\":[{\"productSku\":\"SKU-0008\",\"goodQty\":2}]}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/return/putaway/listOrder").andExpect(jsonPath("$.data.total").value(2));
    }

    // ---------- FBA 退货：签收 → 清点 → 上架 ----------

    @Test
    void fbaSignCountPutaway() throws Exception {
        getUrl("/blReturnIn/customerList").andExpect(jsonPath("$.data.length()").value(5));
        postJson("/blReturnIn/confirmSign", "{\"trackNo\":\"UNKNOWN\"}").andExpect(jsonPath("$.msg").value("没有该快递的 FBA 退货预报，请用“签收并清点”：UNKNOWN"));
        postJson("/blReturnIn/confirmSign", "{\"trackNo\":\"FBARTN0001\",\"temporaryCellNo\":\"E-01-01\"}").andExpect(jsonPath("$.code").value(200));
        postJson("/blReturnIn/confirmSign", "{\"trackNo\":\"FBARTN0001\"}").andExpect(jsonPath("$.code").value(500));
        getUrl("/blReturnIn/signDetail", "fritTaskNo", "FR2611000001").andExpect(jsonPath("$.data.skuList.length()").value(2));
        postJson("/blReturnIn/updateStatusInventory", "{\"fritTaskNo\":\"FR2611000001\",\"detailList\":[{\"fnsku\":\"FN-DEMO-0001\",\"receiptQtyGood\":\"1\"}]}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/blReturnIn/updateStatusSign", "{\"fritTaskNo\":\"FR2611000001\",\"signSkuList\":[{\"fnsku\":\"FN-DEMO-0004\"}]}").andExpect(jsonPath("$.code").value(500));
        postJson("/blReturnIn/updateStatusSign", "{\"fritTaskNo\":\"FR2611000001\",\"signSkuList\":[{\"fnsku\":\"FN-DEMO-0001\"}]}").andExpect(jsonPath("$.code").value(200));

        getUrl("/blReturnIn/listInventoryFnSku", "expressNo", "FBARTN0001")
                .andExpect(jsonPath("$.data.status").value(20))
                .andExpect(jsonPath("$.data.fnSkuList.length()").value(2));
        getUrl("/blReturnIn/scanFnsku", "fnsku", "FN-DEMO-0001", "customerCode", "C001").andExpect(jsonPath("$.data.forecast").value(true));
        getUrl("/blReturnIn/scanFnsku", "fnsku", "FN-DEMO-0004", "customerCode", "C001").andExpect(jsonPath("$.code").value(500));
        getUrl("/blReturnIn/addFnSku", "fritTaskNo", "FR2611000001", "fnsku", "FN-DEMO-0003").andExpect(jsonPath("$.data.isNewAdd").value(true));
        getUrl("/blReturnIn/addFnSku", "fritTaskNo", "FR2611000001", "fnsku", "FN-DEMO-0003").andExpect(jsonPath("$.code").value(500));
        String good = "{\"fnsku\":\"FN-DEMO-0001\",\"receiptQtyGood\":\"2\",\"receiptQtyBad\":\"1\"},{\"fnsku\":\"FN-DEMO-0003\",\"receiptQtyGood\":\"1\"}";
        postJson("/blReturnIn/updateStatusInventory", "{\"fritTaskNo\":\"FR2611000001\",\"detailList\":[" + good + ",{\"fnsku\":\"FN-DEMO-0005\",\"receiptQtyGood\":\"1\"}]}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/blReturnIn/updateStatusInventory", "{\"fritTaskNo\":\"FR2611000001\",\"detailList\":[" + good + "]}")
                .andExpect(jsonPath("$.data.status").value(30));

        getUrl("/blReturnIn/selectPutAwayList").andExpect(jsonPath("$.data.length()").value(2));
        getUrl("/blReturnIn/selectPutAway", "no", "FBA-RM-0001").andExpect(jsonPath("$.data[0].fritTaskNo").value("FR2611000001"));
        getUrl("/blReturnIn/selectProductQuality", "fritTaskNo", "FR2611000001", "fnsku", "FN-DEMO-0001")
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].recommendCell").value("A-01-01"));
        postJson("/blReturnIn/judgeCell", "{\"cellNo\":\"A-01-01\",\"productQuality\":2}").andExpect(jsonPath("$.code").value(500));
        postJson("/blReturnIn/judgeCell", "{\"cellNo\":\"E-01-02\",\"productQuality\":2}").andExpect(jsonPath("$.code").value(200));
        getUrl("/blReturnIn/listPutAwayFnSku", "fritTaskNo", "FR2611000001").andExpect(jsonPath("$.data.fnSkuList[0].receiptQtyGood").value(2));

        postJson("/blReturnIn/updateStatusShelf", "{\"fritTaskNo\":\"FR2611000001\",\"fnsku\":\"FN-DEMO-0001\",\"productQuality\":1,\"putawayQty\":2,\"cellNo\":\"A-01-01\"}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(0));
        postJson("/blReturnIn/updateStatusShelf", "{\"fritTaskNo\":\"FR2611000001\",\"fnsku\":\"FN-DEMO-0001\",\"productQuality\":2,\"putawayQty\":1,\"cellNo\":\"E-01-02\"}");
        postJson("/blReturnIn/updateStatusShelf", "{\"fritTaskNo\":\"FR2611000001\",\"fnsku\":\"FN-DEMO-0003\",\"productQuality\":1,\"putawayQty\":1,\"cellNo\":\"A-02-01\"}")
                .andExpect(jsonPath("$.data.putAwayStatus").value(1));
        qty("A-01-01", "SKU-0001", 1).andExpect(jsonPath("$.data.useStkQty").value("22"));
        qty("E-01-02", "SKU-0001", 2).andExpect(jsonPath("$.data.useStkQty").value("1"));
        qty("A-02-01", "SKU-0003", 1).andExpect(jsonPath("$.data.useStkQty").value("9"));

        postJson("/blReturnIn/signAndInventory", "{\"customerCode\":\"C003\",\"expressNo\":\"NEWFBA1\",\"productList\":[{\"fnsku\":\"FN-DEMO-0006\",\"positiveQty\":2}]}")
                .andExpect(jsonPath("$.code").value(200));
        postJson("/blReturnIn/signAndInventory", "{\"customerCode\":\"C003\",\"expressNo\":\"NEWFBA1\",\"productList\":[{\"fnsku\":\"FN-DEMO-0006\",\"positiveQty\":2}]}")
                .andExpect(jsonPath("$.code").value(500));
        getUrl("/blReturnIn/selectPutAwayList").andExpect(jsonPath("$.data.length()").value(2));
        getUrl("/blReturnIn/scanCell", "cellNo", "E-01-01").andExpect(jsonPath("$.data").value("E-01-01"));
    }

    // ---------- 认领与退货拣货 ----------

    @Test
    void claimAndRemovalPick() throws Exception {
        getUrl("/blReturnIn/claim/scanBarcode", "scanContent", "BC00010009", "customerCode", "C004").andExpect(jsonPath("$.data[0].fnsku").value("FN-DEMO-0009"));
        getUrl("/blReturnIn/claim/scanBarcode", "scanContent", "BC00010009", "customerCode", "C001").andExpect(jsonPath("$.code").value(500));
        postJson("/blReturnIn/createClaim", "{\"claimCustomerCode\":\"C004\",\"expressNo\":\"CLM1\",\"cellNo\":\"E-01-01\",\"productList\":[{\"productSku\":\"SKU-0009\",\"goodQty\":1}]}")
                .andExpect(jsonPath("$.code").value(200));
        getUrl("/return/putaway/listOrder").andExpect(jsonPath("$.data.records[?(@.orderType==4)].expressNo").value("CLM1"));

        postJson("/blReturnIn/pick/finishTaskOrder", "{\"frstTaskNo\":\"PK2611000001\"}").andExpect(jsonPath("$.msg").value("还没有拣货，不能结束任务"));
        getUrl("/blReturnIn/pick/listTaskOrder")
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].fnskuList[0].pickQty").value(3));
        getUrl("/blReturnIn/pick/selectTaskOrder", "frstTaskNo", "PK2611000001").andExpect(jsonPath("$.data.fnskuList.length()").value(2));
        getUrl("/blReturnIn/pick/verifyCellNo", "cellNo", "D-01-01", "customerCode", "C004").andExpect(jsonPath("$.code").value(200));
        getUrl("/blReturnIn/pick/verifyCellNo", "cellNo", "A-01-01", "customerCode", "C004").andExpect(jsonPath("$.code").value(500));
        getUrl("/blReturnIn/pick/verifyBarcode", "cellNo", "D-01-01", "fnsku", "FN-DEMO-0008", "frstTaskNo", "PK2611000001")
                .andExpect(jsonPath("$.data.pickQty").value(3));
        postJson("/blReturnIn/pick/postTaskOrder", "{\"frstTaskNo\":\"PK2611000001\",\"fnsku\":\"FN-DEMO-0008\",\"cellNo\":\"D-01-01\",\"pickQty\":4}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/blReturnIn/pick/postTaskOrder", "{\"frstTaskNo\":\"PK2611000001\",\"fnsku\":\"FN-DEMO-0008\",\"cellNo\":\"D-01-01\",\"pickQty\":3}")
                .andExpect(jsonPath("$.data.pickStatus").value(1));
        qty("D-01-01", "SKU-0008", 1).andExpect(jsonPath("$.data.useStkQty").value("22"));
        postJson("/blReturnIn/pick/finishTaskOrder", "{\"frstTaskNo\":\"PK2611000001\"}").andExpect(jsonPath("$.code").value(200));
        getUrl("/blReturnIn/pick/listTaskOrder").andExpect(jsonPath("$.data.length()").value(0));
        postJson("/blReturnIn/pick/finishTaskOrder", "{\"frstTaskNo\":\"PK2611000001\"}").andExpect(jsonPath("$.code").value(500));
    }
}
