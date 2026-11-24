package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.InventoryCheckStore;
import com.xlwms.rebuild.service.InventoryCheckStore.CheckError;
import com.xlwms.rebuild.service.InventoryCheckStore.Detail;
import com.xlwms.rebuild.service.InventoryCheckStore.Order;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 盘点：/gateway/wms/pda/inventoryCheck/{list,start,overview,scanSku,submitCell,complete}。
 * 路径、请求体（InventoryCheck*Req）与响应字段（InventoryCheckListItem / Overview / Detail / Error）取自 APK；
 * 状态编码与流转规则是本项目假设，见 {@link InventoryCheckStore}。数据虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/inventoryCheck")
public class PdaInventoryCheckController {

    /** InventoryCheckListReq */
    public record ListReq(int current, String orderSn, int size, String whCode) {
    }

    /** InventoryCheckOrderReq */
    public record OrderReq(String orderSn, String whCode) {
    }

    /** InventoryCheckScanReq */
    public record ScanReq(String cellNo, String orderSn, String scanCode, String whCode) {
    }

    /** InventoryCheckDetailSubmit */
    public record DetailSubmit(String areaNo, int bookStock, String cellNo, String customerCode, String itemId, int productQuality,
                               String remark, String sku, Integer solidStock) {
    }

    /** InventoryCheckCellSubmitReq */
    public record CellSubmitReq(String cellNo, List<DetailSubmit> dtlList, String orderSn, String whCode) {
    }

    private final InventoryCheckStore store;
    private final StockStore stock;

    public PdaInventoryCheckController(InventoryCheckStore store, StockStore stock) {
        this.store = store;
        this.stock = stock;
    }

    private Map<String, Object> detail(Order o, Detail d) {
        Product p = stock.product(d.sku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("areaNo", d.cellNo.substring(0, 1) + "区");
        m.put("barcode", p.barcode());
        // 盲盘不下发账面数
        m.put("bookStock", o.checkType == 2 ? 0 : d.bookStock);
        m.put("cellNo", d.cellNo);
        m.put("customerCode", p.customerCode());
        m.put("customerName", p.customerName());
        m.put("itemId", d.itemId);
        m.put("lockedStock", false);
        m.put("pictureUrl", "");
        m.put("productName", p.name());
        m.put("productQuality", d.productQuality);
        m.put("remark", d.remark);
        m.put("sku", d.sku);
        m.put("solidStock", d.solidStock == null ? "" : String.valueOf(d.solidStock));
        m.put("verifyErrorCause", "");
        return m;
    }

    private static Map<String, Object> listItem(Order o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("checkType", o.checkType);
        m.put("completedCellCount", o.completedCellCount());
        m.put("createTime", o.createTime);
        m.put("orderSn", o.orderSn);
        m.put("remark", o.remark);
        m.put("status", o.status);
        m.put("totalCellCount", o.cellNos.size());
        return m;
    }

    private Map<String, Object> overview(Order o) {
        Map<String, Object> m = listItem(o);
        m.remove("createTime");
        List<Map<String, Object>> cells = new ArrayList<>();
        for (String cellNo : o.cellNos) {
            InventoryCheckStore.Cell c = o.cells.get(cellNo);
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("areaNo", cellNo.substring(0, 1) + "区");
            cm.put("cellNo", cellNo);
            cm.put("completed", c != null && c.completed);
            cm.put("dtlList", c == null ? List.of() : c.details.stream().map(d -> detail(o, d)).toList());
            cells.add(cm);
        }
        m.put("cellList", cells);
        return m;
    }

    private static List<Map<String, Object>> errors(List<CheckError> es) {
        return es.stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("cause", e.cause());
            m.put("cellNo", e.cellNo());
            m.put("customerCode", e.customerCode());
            m.put("customerName", e.customerName());
            m.put("sku", e.sku());
            return m;
        }).toList();
    }

    @PostMapping("/list")
    public R<Map<String, Object>> list(@RequestBody ListReq req) {
        List<Map<String, Object>> all = store.list().stream()
                .filter(o -> req.orderSn() == null || req.orderSn().isBlank() || o.orderSn.contains(req.orderSn().trim()))
                .map(PdaInventoryCheckController::listItem).toList();
        return R.ok(PageUtil.page(all, Math.max(1, req.current()), req.size() <= 0 ? 20 : req.size()));
    }

    @PostMapping("/start")
    public R<List<Map<String, Object>>> start(@RequestBody OrderReq req) {
        return R.ok(errors(store.start(req.orderSn())));
    }

    @PostMapping("/overview")
    public R<Map<String, Object>> overview(@RequestBody OrderReq req) {
        return R.ok(overview(store.get(req.orderSn())));
    }

    @PostMapping("/scanSku")
    public R<List<Map<String, Object>>> scanSku(@RequestBody ScanReq req) {
        Order o = store.get(req.orderSn());
        return R.ok(store.scan(req.orderSn(), req.cellNo(), req.scanCode()).stream().map(d -> detail(o, d)).toList());
    }

    @PostMapping("/submitCell")
    public R<Map<String, Object>> submitCell(@RequestBody CellSubmitReq req) {
        List<Object[]> entries = new ArrayList<>();
        if (req.dtlList() != null) {
            for (DetailSubmit d : req.dtlList()) {
                entries.add(new Object[] {d.itemId() == null || d.itemId().isBlank() ? null : d.itemId(), d.sku(), d.solidStock(), d.remark()});
            }
        }
        return R.ok(overview(store.submitCell(req.orderSn(), req.cellNo(), entries)));
    }

    @PostMapping("/complete")
    public R<List<Map<String, Object>>> complete(@RequestBody OrderReq req) {
        return R.ok(errors(store.complete(req.orderSn())));
    }
}
