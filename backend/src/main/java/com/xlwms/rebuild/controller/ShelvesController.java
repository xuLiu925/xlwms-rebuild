package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.shelves.BoxShelvesReq;
import com.xlwms.rebuild.dto.shelves.SkuShelvesReq;
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
 * 上架相关接口的示例实现（PDA /gateway/wms/pda/shelf/v2/*）。
 * 路径、请求体与查询参数按 APK 的 ApiService 还原；返回全部为虚构示例数据，不连接生产。
 * 两个 GET 为只读（扫码取上架信息、推荐库位），两个 POST 在真实系统是写操作（上架确认/整箱上架），
 * 这里仅做协议骨架供前端联调。
 */
@RestController
@RequestMapping("/gateway/wms/pda/shelf/v2")
public class ShelvesController {

    /** 扫描商品条码，返回该条码的上架校验信息（示例数据）。 */
    @GetMapping("/scanBarcode")
    public R<Map<String, Object>> scanBarcode(
            @RequestParam String barcode,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String putawayNo,
            @RequestParam(required = false) String whCode) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("barcode", barcode);
        data.put("productSku", "SKU-EXAMPLE-001");
        data.put("productName", "示例商品");
        data.put("putawayNo", putawayNo);
        data.put("waitPutawayQty", 100);
        data.put("recommendCell", "A-01-01");
        return R.ok(data);
    }

    /** 推荐库位分页列表（示例数据）。 */
    @GetMapping("/getRecommendCell/v2")
    public R<Map<String, Object>> getRecommendCell(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String putawayNo,
            @RequestParam(required = false) String whCode,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String areaTypes) {
        Map<String, Object> cell = new LinkedHashMap<>();
        cell.put("cellNo", "A-01-01");
        cell.put("areaType", "PICK");
        cell.put("availableQty", 500);
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("records", List.of(cell));
        page.put("total", 1);
        page.put("size", size);
        page.put("current", current);
        return R.ok(page);
    }

    /** 按 SKU 上架确认（示例：回显上架条数并返回上架状态）。 */
    @PostMapping("/confirm")
    public R<Map<String, Object>> confirm(@RequestBody SkuShelvesReq req) {
        int count = req.getSkuList() == null ? 0 : req.getSkuList().size();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("putawayNo", req.getPutawayNo());
        data.put("putawaySkuCount", count);
        // PutAwayStatus：是否全部上架完成
        data.put("finished", true);
        data.put("success", true);
        return R.ok(data);
    }

    /** 整箱上架提交（示例：回显箱数并返回上架状态）。 */
    @PostMapping("/box/submitShelf")
    public R<Map<String, Object>> submitShelf(@RequestBody BoxShelvesReq req) {
        int count = req.getBoxBarcodeList() == null ? 0 : req.getBoxBarcodeList().size();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("putawayNo", req.getPutawayNo());
        data.put("putawayBoxCount", count);
        data.put("finished", true);
        data.put("success", true);
        return R.ok(data);
    }
}
