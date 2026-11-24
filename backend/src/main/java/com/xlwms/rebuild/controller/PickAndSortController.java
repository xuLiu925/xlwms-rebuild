package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.PickAndSortStore;
import com.xlwms.rebuild.service.PickAndSortStore.Allocation;
import com.xlwms.rebuild.service.PickAndSortStore.Grid;
import com.xlwms.rebuild.service.PickAndSortStore.ScanResult;
import java.util.ArrayList;
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
 * PDA 边拣边分：/gateway/wms/pda/pickAndSort/{bindCart,progress,gridDetail,scanSku}。
 * 路径、参数与字段取自 APK（PickAndSort*Req/Resp，进度里的 fullCount / validGridCount 按 @SerializedName）；规则见 {@link PickAndSortStore}。
 */
@RestController
@RequestMapping("/gateway/wms/pda/pickAndSort")
public class PickAndSortController {

    /** PickAndSortBindCartReq */
    public record BindCartReq(String pickCartNo, String waveNo, String whCode) {
    }

    /** PickAndSortScanSkuReq */
    public record ScanSkuReq(String barcode, String cellNo, String currentProductSku, Integer desiredQty, String waveNo, String whCode) {
    }

    private final PickAndSortStore store;

    public PickAndSortController(PickAndSortStore store) {
        this.store = store;
    }

    @PostMapping("/bindCart")
    public R<Map<String, Object>> bindCart(@RequestBody BindCartReq req) {
        store.bindCart(req.waveNo(), req.pickCartNo());
        return R.ok(Map.of("pickCartNo", store.cartOf(req.waveNo()), "waveNo", req.waveNo()));
    }

    @GetMapping("/progress")
    public R<Map<String, Object>> progress(@RequestParam String waveNo, @RequestParam(required = false) String whCode) {
        List<Grid> grids = store.grids(waveNo);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Grid g : grids) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("deliveryNo", g.deliveryNo);
            m.put("gridNo", g.gridNo);
            m.put("gridSerial", g.gridSerial);
            m.put("needQty", g.needQty());
            m.put("pickedQty", g.pickedQty());
            m.put("state", g.state());
            list.add(m);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("fullCount", (int) grids.stream().filter(g -> "FULL".equals(g.state())).count());
        r.put("grids", list);
        r.put("validGridCount", (int) grids.stream().filter(g -> g.needQty() > 0).count());
        return R.ok(r);
    }

    @GetMapping("/gridDetail")
    public R<Map<String, Object>> gridDetail(@RequestParam String waveNo, @RequestParam int gridSerial,
                                             @RequestParam(required = false) String whCode) {
        Grid g = store.grid(waveNo, gridSerial);
        List<Map<String, Object>> skus = new ArrayList<>();
        g.need.forEach((sku, need) -> skus.add(Map.of("needQty", need, "pickedQty", g.picked.getOrDefault(sku, 0), "productSku", sku)));
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("deliveryNo", g.deliveryNo);
        r.put("gridNo", g.gridNo);
        r.put("gridSerial", g.gridSerial);
        r.put("skuList", skus);
        return R.ok(r);
    }

    @PostMapping("/scanSku")
    public R<Map<String, Object>> scanSku(@RequestBody ScanSkuReq req) {
        ScanResult s = store.scan(req.waveNo(), req.barcode(), req.cellNo(), req.currentProductSku(),
                req.desiredQty() == null ? 1 : req.desiredQty());
        List<Map<String, Object>> alloc = new ArrayList<>();
        for (Allocation a : s.allocations()) {
            alloc.add(Map.of("deliveryNo", a.grid().deliveryNo, "gridNo", a.grid().gridNo,
                    "gridSerial", a.grid().gridSerial, "quantity", a.quantity()));
        }
        Grid first = s.allocations().get(0).grid();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("allocations", alloc);
        r.put("barcode", s.product().barcode());
        r.put("customerCode", s.product().customerCode());
        r.put("deliveryNo", first.deliveryNo);
        r.put("effectiveQty", s.effectiveQty());
        r.put("gridNo", first.gridNo);
        r.put("gridSerial", first.gridSerial);
        r.put("pickQty", s.line().pickQty);
        r.put("planQty", s.line().planQty);
        r.put("productName", s.product().name());
        r.put("productSku", s.product().sku());
        r.put("totalPlanQty", s.wave().planQty());
        r.put("whCode", req.whCode());
        return R.ok(r);
    }
}
