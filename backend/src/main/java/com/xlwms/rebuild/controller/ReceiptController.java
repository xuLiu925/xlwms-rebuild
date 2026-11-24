package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.receipt.BoxTypeInfoReq;
import com.xlwms.rebuild.dto.receipt.PalletReceiptScanReq;
import com.xlwms.rebuild.dto.receipt.SkuForecastReceiptReq;
import com.xlwms.rebuild.dto.receipt.WholeOrderReceiptReq;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 入库收货相关接口的示例实现（PDA /gateway/wms/pda/receipt/*）。
 * 请求体结构按 APK 反编译还原（docs/PDA_REQUEST_BODIES.md），返回全部为虚构示例数据，
 * 不连接任何生产系统；真实厂商接口多为写操作，这里只做协议骨架，便于前端联调。
 */
@RestController
@RequestMapping("/gateway/wms/pda/receipt")
public class ReceiptController {

    /** 扫描托盘，返回该托盘下的待收货 SKU（示例数据）。 */
    @PostMapping("/scanPallet")
    public R<Map<String, Object>> scanPallet(@RequestBody PalletReceiptScanReq req) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("palletCode", req.getPalletCode());
        data.put("whCode", req.getWhCode());
        data.put("asnNo", "ASN0000000000XX");
        data.put("customerCode", "C001");
        Map<String, Object> sku = new LinkedHashMap<>();
        sku.put("productSku", "SKU-EXAMPLE-001");
        sku.put("productName", "示例商品");
        sku.put("forecastQty", 100);
        sku.put("receivedQty", 0);
        data.put("skuList", List.of(sku));
        return R.ok(data);
    }

    /** 查询箱型信息（示例数据）。 */
    @PostMapping("/getBoxTypeInfo")
    public R<List<Map<String, Object>>> getBoxTypeInfo(@RequestBody BoxTypeInfoReq req) {
        Map<String, Object> box = new LinkedHashMap<>();
        box.put("boxType", "BT-01");
        box.put("customBoxType", "");
        box.put("wmsBoxLength", "40");
        box.put("wmsBoxWidth", "30");
        box.put("wmsBoxHeight", "25");
        box.put("wmsBoxWeight", "0.5");
        box.put("wmsUnitMark", 1);
        return R.ok(List.of(box));
    }

    /** 按 SKU 预报收货提交（示例：回显提交条数并给出新版本号）。 */
    @PostMapping("/skuReceipt")
    public R<Map<String, Object>> skuReceipt(@RequestBody SkuForecastReceiptReq req) {
        int skuCount = req.getSkuList() == null ? 0 : req.getSkuList().size();
        int palletCount = req.getSkuPalletList() == null ? 0 : req.getSkuPalletList().size();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("asnNo", req.getAsnNo());
        data.put("receiptSkuCount", skuCount);
        data.put("receiptPalletCount", palletCount);
        // 乐观锁：返回新版本号供下一次提交回传
        data.put("rowVersion", "2");
        data.put("success", true);
        return R.ok(data);
    }

    /** 整单收货提交（示例：回显单号并置为已收货状态 15）。 */
    @PostMapping("/wholeOrderReceipt")
    public R<Map<String, Object>> wholeOrderReceipt(@RequestBody WholeOrderReceiptReq req) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("asnNo", req.getAsnNo());
        data.put("sourceNo", req.getSourceNo());
        data.put("status", 15);
        data.put("statusName", "已收货");
        data.put("rowVersion", "2");
        data.put("success", true);
        return R.ok(data);
    }
}
