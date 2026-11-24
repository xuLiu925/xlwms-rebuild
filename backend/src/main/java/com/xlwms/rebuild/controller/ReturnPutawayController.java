package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.returnputaway.ReturnPutawayScanReq;
import com.xlwms.rebuild.dto.returnputaway.ShelvesInfoReq;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退货上架相关接口的示例实现（PDA /gateway/wms/pda/return/putaway/*）。
 * 路径、请求体与查询参数按 APK 的 ApiService 还原；返回全部为虚构示例数据，不连接任何生产系统。
 * 真实系统中 submit 等为写操作，这里仅做协议骨架供前端联调。
 * 注：销毁 destruct（/return/putaway/destruct）属破坏性操作，未在此实现。
 */
@RestController
@RequestMapping("/gateway/wms/pda/return/putaway")
public class ReturnPutawayController {

    /** 扫描条码，返回匹配的退货上架单列表（示例数据）。 */
    @PostMapping("/scanBarcode")
    public R<List<Map<String, Object>>> scanBarcode(@RequestBody ReturnPutawayScanReq req) {
        return R.ok(List.of(sampleOrder(req.getScanContent())));
    }

    /** 扫描单号，返回匹配的退货上架单列表（示例数据）。 */
    @PostMapping("/scanOrderNo")
    public R<List<Map<String, Object>>> scanOrderNo(@RequestBody ReturnPutawayScanReq req) {
        return R.ok(List.of(sampleOrder(req.getScanContent())));
    }

    /** 扫描库位，校验库位是否可放对应品质的货（示例：恒返回可用）。 */
    @GetMapping("/scanCell")
    public R<Map<String, Object>> scanCell(
            @RequestParam String cellNo,
            @RequestParam(required = false) String productQuality,
            @RequestParam(required = false) String whCode) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cellNo", cellNo);
        data.put("usable", true);
        data.put("productQuality", productQuality);
        return R.ok(data);
    }

    /** 退货上架单分页列表（示例数据）。 */
    @GetMapping("/listOrder")
    public R<Map<String, Object>> listOrder(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String whCode) {
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("records", List.of(sampleOrder("RT0000000000XX")));
        page.put("total", 1);
        page.put("size", size);
        page.put("current", current);
        return R.ok(page);
    }

    /** 按条码查退货上架明细（示例数据）。 */
    @GetMapping("/detail/scanBarcode")
    public R<List<Map<String, Object>>> detailScanBarcode(
            @RequestParam(required = false) String whCode,
            @RequestParam String barcode,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String returnNo) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("barcode", barcode);
        detail.put("returnNo", returnNo);
        detail.put("skuId", "SKU-EXAMPLE-001");
        detail.put("productName", "示例商品");
        detail.put("waitPutawayQty", 10);
        detail.put("productQuality", 1);
        return R.ok(List.of(detail));
    }

    /** 退货上架提交（示例：回显上架数量并返回处理结果）。 */
    @PostMapping("/submit")
    public R<Map<String, Object>> submit(@RequestBody ShelvesInfoReq req) {
        int batchCount = req.getPutawayExpirationSubmitDTOList() == null
                ? 0 : req.getPutawayExpirationSubmitDTOList().size();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("returnNo", req.getReturnNo());
        data.put("skuId", req.getSkuId());
        data.put("putawayQty", req.getQty());
        data.put("batchCount", batchCount);
        data.put("success", true);
        return R.ok(data);
    }

    private Map<String, Object> sampleOrder(String scanContent) {
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("returnNo", "RT0000000000XX");
        order.put("scanContent", scanContent);
        order.put("customerCode", "C001");
        order.put("whCode", "WH01");
        order.put("waitPutawayQty", 10);
        order.put("status", 10);
        order.put("statusName", "待上架");
        return order;
    }
}
