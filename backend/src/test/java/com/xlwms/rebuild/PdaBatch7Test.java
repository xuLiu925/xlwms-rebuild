package com.xlwms.rebuild;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.jayway.jsonpath.JsonPath;
import com.xlwms.rebuild.service.AppendixStore;
import com.xlwms.rebuild.service.MessageBoardStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 批次 7：设备任务、边拣边分、附件、留言板、登录/首页/菜单/仓库/参数/版本、按箱移库。数据全部虚构。 */
@SpringBootTest
@AutoConfigureMockMvc
class PdaBatch7Test {
    @Autowired
    MockMvc mvc;
    @Autowired
    AppendixStore appendix;
    @Autowired
    MessageBoardStore messageBoard;

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

    private ResultActions qty(String cell, String sku, String boxType, int quality) throws Exception {
        return postJson("/pda/stock/check", "{\"cellNo\":\"" + cell + "\",\"productSku\":\"" + sku + "\",\"boxType\":\"" + boxType
                + "\",\"productQuality\":" + quality + "}");
    }

    private static MockMultipartFile file(String name, int size) {
        return new MockMultipartFile("files", name, "application/octet-stream", new byte[size]);
    }

    // ---------- 设备任务 ----------

    @Test
    void deviceCellAndStockPrepare() throws Exception {
        getUrl("/device/config/tenantEnabled", "whCode", "WH01")
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.specialCellNos[1]").value("B-01-03"));
        // 普通库位不需要设备
        postJson("/pda/device/cell/prepare", "{\"cellNos\":[\"A-01-01\"]}")
                .andExpect(jsonPath("$.data.canProceed").value(true))
                .andExpect(jsonPath("$.data.deviceStatus").value("NONE"))
                .andExpect(jsonPath("$.data.latestTaskId").value(nullValue()));
        postJson("/pda/device/cell/prepare", "{\"cellNos\":[\"B-01-01\"]}")
                .andExpect(jsonPath("$.data.deviceStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.bizType").value("CELL"));
        postJson("/pda/device/cell/prepare", "{\"cellNos\":[\"NOPE\"]}").andExpect(jsonPath("$.code").value(500));
        // 故障库位：异常，跳状态页；处理后放行
        postJson("/pda/device/cell/prepare", "{\"cellNos\":[\"B-01-03\"]}")
                .andExpect(jsonPath("$.data.canProceed").value(false))
                .andExpect(jsonPath("$.data.redirectToStatusPage").value(true))
                .andExpect(jsonPath("$.data.bizNo").value("CELL:B-01-03"));
        postJson("/device/tasks/detail", "{\"bizType\":\"CELL\",\"bizNo\":\"CELL:B-01-03\"}")
                .andExpect(jsonPath("$.data.bizStatus").value("WAITING"))
                .andExpect(jsonPath("$.data.lines[0].exceptionType").value("BIN_NOT_ARRIVED"))
                .andExpect(jsonPath("$.data.lines[0].requestedQty").value(12));
        postJson("/device/tasks/resolve", "{\"bizType\":\"CELL\",\"bizNo\":\"CELL:B-01-03\"}").andExpect(jsonPath("$.code").value(200));
        postJson("/device/tasks/resolve", "{\"bizType\":\"CELL\",\"bizNo\":\"CELL:B-01-03\"}").andExpect(jsonPath("$.code").value(500));
        postJson("/pda/device/cell/prepare", "{\"cellNos\":[\"B-01-03\"]}")
                .andExpect(jsonPath("$.data.canProceed").value(true))
                .andExpect(jsonPath("$.data.deviceStatus").value("COMPLETED"));
        // 按 SKU + 库位定位
        postJson("/pda/device/stock/locate", "{\"bizType\":\"MOVE\",\"skuCellPairs\":[{\"sku\":\"SKU-0004\",\"cellNo\":\"B-01-01\"}]}")
                .andExpect(jsonPath("$.data.canProceed").value(true))
                .andExpect(jsonPath("$.data.bizType").value("MOVE"));
        postJson("/pda/device/stock/locate", "{\"skuCellPairs\":[{\"sku\":\"SKU-0001\",\"cellNo\":\"B-01-01\"}]}")
                .andExpect(jsonPath("$.code").value(500));
        postJson("/device/tasks/detail", "{\"bizType\":\"CELL\",\"bizNo\":\"NOPE\"}").andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void deviceWaveAndBill() throws Exception {
        postJson("/pda/device/wave/prepare", "{\"bizNo\":\"WV26110001\",\"bizType\":\"WAVE\"}")
                .andExpect(jsonPath("$.data.deviceStatus").value("NONE"));
        postJson("/pda/device/wave/prepare", "{\"bizNo\":\"WV26110002\",\"bizType\":\"WAVE\"}")
                .andExpect(jsonPath("$.data.canProceed").value(false))
                .andExpect(jsonPath("$.data.exceptionMsg").value("设备料箱未到位：B-01-03"));
        getUrl("/device/tasks/wave", "waveNo", "WV26110002")
                .andExpect(jsonPath("$.data.lines.length()").value(4))
                .andExpect(jsonPath("$.data.lines[1].outOrderNo").value("OB2611000201"))
                .andExpect(jsonPath("$.data.lines[1].subSeqNo").value(2))
                .andExpect(jsonPath("$.data.lines[1].success").value(false))
                .andExpect(jsonPath("$.data.lines[2].seqNo").value(2));
        String sub = "{\"waveNo\":\"WV26110002\",\"outOrderNo\":\"%s\",\"subSeqNo\":%d}";
        postJson("/device/tasks/wave/subOrder/resolve", String.format(sub, "OB2611000201", 1)).andExpect(jsonPath("$.code").value(500));
        postJson("/device/tasks/wave/subOrder/resolve", String.format(sub, "OB2611000201", 2)).andExpect(jsonPath("$.code").value(200));
        getUrl("/device/tasks/wave", "waveNo", "WV26110002").andExpect(jsonPath("$.data.deviceStatus").value("EXCEPTION"));
        postJson("/device/tasks/wave/subOrder/resolve", String.format(sub, "OB2611000202", 2)).andExpect(jsonPath("$.code").value(200));
        getUrl("/device/tasks/wave", "waveNo", "WV26110002")
                .andExpect(jsonPath("$.data.deviceStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.lines[3].taskStatus").value("RESOLVED"));
        // 重复准备返回同一任务
        postJson("/pda/device/wave/prepare", "{\"bizNo\":\"WV26110002\",\"bizType\":\"WAVE\"}")
                .andExpect(jsonPath("$.data.canProceed").value(true));
        postJson("/pda/device/bill/prepare", "{\"bizNo\":\"OB2611000101\",\"bizType\":\"DELIVERY\"}")
                .andExpect(jsonPath("$.data.deviceStatus").value("NONE"));
        postJson("/pda/device/bill/prepare", "{\"bizNo\":\"OB2611000201\",\"bizType\":\"DELIVERY\"}")
                .andExpect(jsonPath("$.data.deviceStatus").value("EXCEPTION"));
        postJson("/pda/device/bill/prepare", "{\"bizNo\":\"OB2611000201\",\"bizType\":\"RECEIPT\"}").andExpect(jsonPath("$.code").value(500));
    }

    // ---------- 边拣边分 ----------

    @Test
    void pickAndSortFillsGridsAndFinishesWave() throws Exception {
        getUrl("/pda/pickAndSort/progress", "waveNo", "WV26110002").andExpect(jsonPath("$.code").value(500));
        postJson("/pda/pickAndSort/bindCart", "{\"waveNo\":\"WV26110001\",\"pickCartNo\":\"CART-09\"}")
                .andExpect(jsonPath("$.msg").value("只有多品波次支持边拣边分"));
        postJson("/pda/pickAndSort/bindCart", "{\"waveNo\":\"WV26110004\",\"pickCartNo\":\"CART-09\"}").andExpect(jsonPath("$.code").value(500));
        postJson("/pda/pickAndSort/bindCart", "{\"waveNo\":\"WV26110002\",\"pickCartNo\":\"CART-09\"}")
                .andExpect(jsonPath("$.data.pickCartNo").value("CART-09"));
        postJson("/pda/pickAndSort/bindCart", "{\"waveNo\":\"WV26110002\",\"pickCartNo\":\"CART-10\"}")
                .andExpect(jsonPath("$.msg").value("波次已绑定拣货车 CART-09"));
        getUrl("/pda/pickAndSort/progress", "waveNo", "WV26110002")
                .andExpect(jsonPath("$.data.validGridCount").value(2))
                .andExpect(jsonPath("$.data.fullCount").value(0))
                .andExpect(jsonPath("$.data.grids[0].gridNo").value("CART-09-01"))
                .andExpect(jsonPath("$.data.grids[0].needQty").value(3));

        String scan = "{\"waveNo\":\"WV26110002\",\"barcode\":\"%s\",\"cellNo\":\"%s\",\"desiredQty\":%d%s}";
        postJson("/pda/pickAndSort/scanSku", String.format(scan, "BC00010004", "B-01-01", 1, ",\"currentProductSku\":\"SKU-0005\""))
                .andExpect(jsonPath("$.code").value(500));
        postJson("/pda/pickAndSort/scanSku", String.format(scan, "BC00010004", "B-01-01", 3, ""))
                .andExpect(jsonPath("$.data.effectiveQty").value(3))
                .andExpect(jsonPath("$.data.allocations.length()").value(2))
                .andExpect(jsonPath("$.data.allocations[0].quantity").value(2))
                .andExpect(jsonPath("$.data.allocations[1].gridSerial").value(2))
                .andExpect(jsonPath("$.data.pickQty").value(3))
                .andExpect(jsonPath("$.data.totalPlanQty").value(5));
        getUrl("/pda/pickAndSort/gridDetail", "waveNo", "WV26110002", "gridSerial", "1")
                .andExpect(jsonPath("$.data.deliveryNo").value("OB2611000201"))
                .andExpect(jsonPath("$.data.skuList[0].pickedQty").value(2))
                .andExpect(jsonPath("$.data.skuList[1].pickedQty").value(0));
        // 期望 5 件，但拣货行只剩 2
        postJson("/pda/pickAndSort/scanSku", String.format(scan, "BC00010005", "B-01-03", 5, ""))
                .andExpect(jsonPath("$.data.effectiveQty").value(2));
        getUrl("/pda/pickAndSort/progress", "waveNo", "WV26110002")
                .andExpect(jsonPath("$.data.fullCount").value(2))
                .andExpect(jsonPath("$.data.grids[1].state").value("FULL"));
        // 拣完后出库单进入待复核（与普通拣货共用状态）
        postJson("/pda/messageBoard/search", "{\"scanCode\":\"OB2611000202\"}").andExpect(jsonPath("$.data[0].statusName").value("待复核"));
        postJson("/pda/pickAndSort/scanSku", String.format(scan, "BC00010005", "B-01-03", 1, "")).andExpect(jsonPath("$.code").value(500));
    }

    // ---------- 附件 / 图片 ----------

    @Test
    void appendixUploadIsAtomicAndRemovable() throws Exception {
        getUrl("/pda/appendix/appendixTypeList", "bizCode", "RETURN")
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[1].bizAppendixTypeName").value("商品照片"));
        getUrl("/pda/appendix/appendixTypeList", "bizCode", "NOPE").andExpect(jsonPath("$.code").value(500));

        var up = multipart("/gateway/wms/pda/appendix/upload").file(file("a.png", 100)).file(file("b.JPG", 200))
                .param("bizNo", "RN2611000001").param("bizCode", "RETURN").param("customerCode", "C001").param("bizAppendixType", "2");
        String body = mvc.perform(up).andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].bizAppendixTypeName").value("商品照片"))
                .andExpect(jsonPath("$.data[1].fileExt").value("jpg"))
                .andExpect(jsonPath("$.data[1].fileSize").value("200"))
                .andReturn().getResponse().getContentAsString();
        String key = JsonPath.read(body, "$.data[0].fileKey");
        // 一批里有一个不合规，整批都不登记
        mvc.perform(multipart("/gateway/wms/pda/appendix/upload").file(file("c.png", 10)).file(file("d.exe", 10))
                        .param("bizNo", "RN2611000001").param("bizCode", "RETURN").param("bizAppendixType", "2"))
                .andExpect(jsonPath("$.msg").value("不支持的文件类型：exe"));
        mvc.perform(multipart("/gateway/wms/pda/appendix/upload").file(file("c.png", 10))
                        .param("bizNo", "RN2611000001").param("bizCode", "RETURN").param("bizAppendixType", "9"))
                .andExpect(jsonPath("$.code").value(500));
        mvc.perform(multipart("/gateway/wms/pda/appendix/upload").file(file("c.png", 10))
                        .param("bizCode", "RETURN").param("bizAppendixType", "2"))
                .andExpect(jsonPath("$.code").value(500));
        assertThat(appendix.byBizNo("RN2611000001")).hasSize(2);

        getUrl("/pda/appendix/remove", "fileKey", key).andExpect(jsonPath("$.code").value(200));
        getUrl("/pda/appendix/remove", "fileKey", key).andExpect(jsonPath("$.code").value(500));
        assertThat(appendix.byBizNo("RN2611000001")).hasSize(1);

        mvc.perform(multipart("/gateway/common/pic/upload").file(file("p.webp", 50)))
                .andExpect(jsonPath("$.data.fileExt").value("webp"))
                .andExpect(jsonPath("$.data.fileSize").value(50));
        mvc.perform(multipart("/gateway/common/pic/upload").file(file("p.pdf", 50))).andExpect(jsonPath("$.code").value(500));
        mvc.perform(multipart("/gateway/common/pic/upload").file(file("p.png", 0))).andExpect(jsonPath("$.msg").value("文件为空：p.png"));
        mvc.perform(multipart("/gateway/wms/pda/appendix/uploadOther").file(new MockMultipartFile("file", "log.zip", "zip", new byte[30])))
                .andExpect(jsonPath("$.data.originName").value("log.zip"));
    }

    // ---------- 留言板 ----------

    @Test
    void messageBoardSearchAndReply() throws Exception {
        postJson("/pda/messageBoard/search", "{\"scanCode\":\"RA2611000001\"}")
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].orderNoPrefix").value("RA"))
                .andExpect(jsonPath("$.data[0].redirectData.asnNo").value("RA2611000001"));
        postJson("/pda/messageBoard/search", "{\"scanCode\":\"TRK0000000201\"}")
                .andExpect(jsonPath("$.data[0].orderNo").value("OB2611000201"))
                .andExpect(jsonPath("$.data[0].redirectData.deliveryNo").value("OB2611000201"));
        postJson("/pda/messageBoard/search", "{\"scanCode\":\"RN2611000003\"}").andExpect(jsonPath("$.data[0].statusName").value("待上架"));
        postJson("/pda/messageBoard/search", "{\"scanCode\":\"NOPE\"}").andExpect(jsonPath("$.data.length()").value(0));

        String key = JsonPath.read(mvc.perform(multipart("/gateway/common/pic/upload").file(file("m.png", 10)))
                .andReturn().getResponse().getContentAsString(), "$.data.fileKey");
        String reply = "{\"orderType\":%d,\"workNo\":\"%s\",\"customerCode\":\"%s\",\"content\":\"%s\",\"appendixList\":[%s]}";
        postJson("/pda/messageBoard/reply", String.format(reply, 2, "OB2611000201", "C002", " ", "")).andExpect(jsonPath("$.code").value(500));
        postJson("/pda/messageBoard/reply", String.format(reply, 2, "OB2611000201", "C001", "破损", "")).andExpect(jsonPath("$.msg").value("客户与单据不一致"));
        postJson("/pda/messageBoard/reply", String.format(reply, 1, "OB2611000201", "", "破损", "")).andExpect(jsonPath("$.code").value(500));
        postJson("/pda/messageBoard/reply", String.format(reply, 2, "OB2611000201", "C002", "破损", "{\"fileKey\":\"nope\"}"))
                .andExpect(jsonPath("$.code").value(500));
        postJson("/pda/messageBoard/reply", String.format(reply, 2, "OB2611000201", "C002", "外箱破损", "{\"fileKey\":\"" + key + "\"}"))
                .andExpect(jsonPath("$.code").value(200));
        assertThat(messageBoard.messages("OB2611000201")).singleElement()
                .satisfies(m -> assertThat(m.fileKeys()).containsExactly(key));
    }

    // ---------- 登录 / 首页 / 菜单 / 仓库 / 参数 / 版本 ----------

    @Test
    void loginHomeMenuWarehouseParamsVersion() throws Exception {
        String token = JsonPath.read(postJson("/auth/login", "{\"loginAccount\":\"demo\",\"password\":\"x\",\"businessType\":\"PDA\"}")
                .andReturn().getResponse().getContentAsString(), "$.data.token");
        mvc.perform(get("/gateway/wms/auth/getUserInfo").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.data.loginAccount").value("demo"))
                .andExpect(jsonPath("$.data.roleNameList[0]").value("本地演示角色"));
        postJson("/auth/login", "{\"account\":\"web\",\"password\":\"x\"}").andExpect(jsonPath("$.data.account").value("web"));
        postJson("/auth/login", "{\"loginAccount\":\"demo\"}").andExpect(jsonPath("$.code").value(500));

        getUrl("/pda/home").andExpect(jsonPath("$.data.outBoundCount").value(9))
                .andExpect(jsonPath("$.data.receiptCount").value(greaterThan(0)));
        getUrl("/pda/menu/list")
                .andExpect(jsonPath("$.data.menuResourceList[0].children.length()").value(8))
                .andExpect(jsonPath("$.data.menuResourceList[0].children[0].code").value("114001"));
        getUrl("/pda/warehouse/page", "current", "2", "size", "1").andExpect(jsonPath("$.data.records[0].whCode").value("WH02"));
        getUrl("/pda/warehouse/page", "current", "3", "size", "1").andExpect(jsonPath("$.data.records.length()").value(0));

        postJson("/pda/warehouse/listParams", "{\"whCode\":\"WH01\",\"itemList\":[{\"itemNo\":\"receiving_overage_limit\",\"paramsNo\":\"pda_config\"},"
                + "{\"itemNo\":\"nope\",\"paramsNo\":\"pda_config\"}]}")
                .andExpect(jsonPath("$.data[0].itemValue").value("0"))
                .andExpect(jsonPath("$.data[1].itemValue").value(nullValue()));
        postJson("/pda/warehouse/listParams", "{\"whCode\":\"WH99\",\"itemList\":[{\"itemNo\":\"x\",\"paramsNo\":\"y\"}]}")
                .andExpect(jsonPath("$.msg").value("仓库不存在：WH99"));
        getUrl("/system/sysBaseParamsDtl/findParams", "itemNo", "measure_unit", "paramsNo", "system_unit", "whCode", "WH01")
                .andExpect(jsonPath("$.data.itemValue").value("1"));
        getUrl("/system/sysBaseParamsDtl/findParams", "itemNo", "x", "paramsNo", "y").andExpect(jsonPath("$.code").value(500));

        getUrl("/system/androidVersionRelease/validateVersion", "version", "1.9.10")
                .andExpect(jsonPath("$.data.code").value(1))
                .andExpect(jsonPath("$.data.versionInfo.versionName").value("2.0.0"));
        getUrl("/system/androidVersionRelease/validateVersion", "version", "2.0.0").andExpect(jsonPath("$.data.code").value(0));
    }

    // ---------- 按箱移库 ----------

    @Test
    void boxMoveStockValidatesWholeBatch() throws Exception {
        String ok = "{\"boxList\":[{\"boxType\":\"BOX-0010-A\",\"customerCode\":\"C005\",\"moveQty\":2,\"sourceCellNo\":\"F-01-01\",\"targetCellNo\":\"F-01-02\"}],"
                + "\"skuList\":[{\"productSku\":\"SKU-0001\",\"moveQty\":5,\"sourceCellNo\":\"A-01-01\",\"targetCellNo\":\"A-01-02\"}],"
                + "\"returnList\":[{\"productSku\":\"SKU-0003\",\"fnsku\":\"FN-DEMO-0003\",\"productQuality\":2,\"moveQty\":1,"
                + "\"sourceCellNo\":\"A-02-01\",\"targetCellNo\":\"E-01-01\"}]}";
        postJson("/blMoveStock/boxMoveStock", ok).andExpect(jsonPath("$.data.msg").value("移库成功，共 3 行"));
        qty("F-01-01", "SKU-0010", "BOX-0010-A", 1).andExpect(jsonPath("$.data.useStkQty").value("4"));
        qty("F-01-02", "SKU-0010", "BOX-0010-A", 1).andExpect(jsonPath("$.data.useStkQty").value("6"));
        qty("A-01-02", "SKU-0001", "", 1).andExpect(jsonPath("$.data.useStkQty").value("5"));
        qty("E-01-01", "SKU-0003", "", 2).andExpect(jsonPath("$.data.useStkQty").value("1"));

        // 两行合计 16 超过可用 15：整批不执行
        String over = "{\"skuList\":[{\"productSku\":\"SKU-0001\",\"moveQty\":8,\"sourceCellNo\":\"A-01-01\",\"targetCellNo\":\"A-02-01\"},"
                + "{\"productSku\":\"SKU-0001\",\"moveQty\":8,\"sourceCellNo\":\"A-01-01\",\"targetCellNo\":\"B-01-01\"}]}";
        postJson("/blMoveStock/boxMoveStock", over).andExpect(jsonPath("$.msg").value("库位 A-01-01 的 SKU-0001 可用 15，本次要移 16"));
        qty("A-01-01", "SKU-0001", "", 1).andExpect(jsonPath("$.data.useStkQty").value("15"));
        qty("A-02-01", "SKU-0001", "", 1).andExpect(jsonPath("$.data.useStkQty").value("0"));

        String one = "{\"skuList\":[{\"productSku\":\"%s\",\"customerCode\":\"%s\",\"productQuality\":%d,\"moveQty\":1,"
                + "\"sourceCellNo\":\"%s\",\"targetCellNo\":\"%s\"}]}";
        postJson("/blMoveStock/boxMoveStock", String.format(one, "SKU-0003", "", 2, "A-02-01", "F-01-01"))
                .andExpect(jsonPath("$.msg").value("次品只能上架到 E 区库位：F-01-01"));
        postJson("/blMoveStock/boxMoveStock", String.format(one, "SKU-0001", "", 1, "A-01-01", "E-09-09")).andExpect(jsonPath("$.code").value(500));
        postJson("/blMoveStock/boxMoveStock", String.format(one, "SKU-0001", "C002", 1, "A-01-01", "A-01-02")).andExpect(jsonPath("$.code").value(500));
        postJson("/blMoveStock/boxMoveStock", String.format(one, "SKU-0001", "", 1, "A-01-01", "A-01-01")).andExpect(jsonPath("$.code").value(500));
        postJson("/blMoveStock/boxMoveStock", "{}").andExpect(jsonPath("$.code").value(500));
    }
}
