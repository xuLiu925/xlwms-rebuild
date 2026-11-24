package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.FreightStore;
import com.xlwms.rebuild.service.FreightStore.BoxLine;
import com.xlwms.rebuild.service.FreightStore.BoxWave;
import com.xlwms.rebuild.service.FreightStore.FreightOrder;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.BoxType;
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
 * PDA 按箱拣货（大货）：/gateway/wms/pda/wave/ 下 8 个接口——bulk/pickWay、boxPick/directPage、boxPageCell、boxScanCell、
 * scanBox、boxPick、recommendCell/、scanBoxBarcode。路径、参数名、请求体（BoxBigReq、WaveBoxScanCellReq、WaveOutReqBody、
 * ScanBoxBarcodeReq）与响应字段（PickWayBean、DirectPageBean、CellDetail、WaveSkuResp、WavePickSubmitResp、RecommendCell、
 * ScanBoxBarcodeSizeRsp）取自 APK；流转规则是本项目假设，见 {@link FreightStore}。数据虚构，不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/wave")
public class WaveBoxController {

    public record BoxBigPickItem(String boxType, int pickQty) {
    }

    public record BoxBigReq(List<BoxBigPickItem> boxList, String cellNo, String customerCode, String deliveryNo, String sourceNo,
                            String waveNo, String whCode) {
    }

    public record WaveBoxScanCellReq(String barcode, String cellNo, String customerCode, String sku, String waveNo, String whCode) {
    }

    public record WaveOutReqBody(String boxType, String cellNo, String customerCode, String waveNo, String whCode) {
    }

    public record ScanBoxBarcodeReq(String boxBarcode, String customerCode, String deliveryNo, String whCode) {
    }

    private final FreightStore freight;
    private final StockStore stock;

    public WaveBoxController(FreightStore freight, StockStore stock) {
        this.freight = freight;
        this.stock = stock;
    }

    private Map<String, Object> cellDetail(BoxWave w, BoxLine l) {
        int totalPlan = w.lines.stream().filter(x -> x.boxType.equals(l.boxType)).mapToInt(x -> x.planQty).sum();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxType", l.boxType);
        m.put("cellNo", l.cellNo);
        m.put("customerCode", w.customerCode);
        m.put("customizeBarcode", "");
        m.put("pickQty", l.pickQty);
        m.put("planQty", l.planQty);
        m.put("productQuality", 1);
        m.put("productSku", stock.boxType(l.boxType).sku());
        m.put("realQty", stock.qtyOf(l.cellNo, stock.boxType(l.boxType).sku(), l.boxType, 1));
        m.put("totalPlanQty", totalPlan);
        return m;
    }

    private Map<String, Object> pageBean(BoxWave w, String key, Map<String, Object> page) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("cellPageList", "cellPageList".equals(key) ? page : null);
        m.put("customerCode", w.customerCode);
        m.put("pickStatus", w.pickStatus);
        m.put("skuPage", "skuPage".equals(key) ? page : null);
        m.put("tenantCode", "DEMO");
        m.put("waveNo", w.waveNo);
        m.put("whCode", StockStore.WH);
        return m;
    }

    /** 扫波次号或大货单号，判断拣货方式（PickWayBean）。 */
    @GetMapping("/bulk/pickWay")
    public R<Map<String, Object>> pickWay(@RequestParam String billNo, @RequestParam(required = false) String whCode) {
        BoxWave w = freight.waveByBill(billNo);
        FreightOrder first = freight.order(w.deliveryNos.get(0));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customerCode", w.customerCode);
        m.put("customerName", w.customerName);
        m.put("deliveryNo", w.deliveryNos.size() == 1 ? first.deliveryNo : "");
        m.put("deviceStatus", "");
        m.put("orderCount", w.deliveryNos.size());
        m.put("pickCartNo", "");
        m.put("pickWay", 3);
        m.put("sourceNo", w.deliveryNos.size() == 1 ? first.sourceNo : "");
        m.put("tenantCode", "DEMO");
        m.put("totalCount", w.planQty());
        m.put("waveNo", w.waveNo);
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    /** 按箱型汇总（skuPage.records = DirectRecord{boxType, cellList[{cellNo,pickQty,planQty}], isExpand, pickQty, planQty}）。 */
    @GetMapping("/boxPick/directPage")
    public R<Map<String, Object>> directPage(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                             @RequestParam String waveNo, @RequestParam(required = false) String whCode) {
        BoxWave w = freight.wave(waveNo);
        Map<String, Map<String, Object>> byType = new LinkedHashMap<>();
        for (BoxLine l : w.lines) {
            Map<String, Object> r = byType.computeIfAbsent(l.boxType, k -> {
                Map<String, Object> n = new LinkedHashMap<>();
                n.put("boxType", k);
                n.put("cellList", new ArrayList<Map<String, Object>>());
                n.put("isExpand", false);
                n.put("pickQty", 0);
                n.put("planQty", 0);
                return n;
            });
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cells = (List<Map<String, Object>>) r.get("cellList");
            cells.add(Map.of("cellNo", l.cellNo, "pickQty", l.pickQty, "planQty", l.planQty));
            r.put("pickQty", (int) r.get("pickQty") + l.pickQty);
            r.put("planQty", (int) r.get("planQty") + l.planQty);
        }
        return R.ok(pageBean(w, "skuPage", PageUtil.page(new ArrayList<>(byType.values()), current, size)));
    }

    /** 按库位列出（cellPageList.records = CellDetail）。 */
    @GetMapping("/boxPageCell")
    public R<Map<String, Object>> boxPageCell(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                              @RequestParam String waveNo, @RequestParam(required = false) String whCode) {
        BoxWave w = freight.wave(waveNo);
        return R.ok(pageBean(w, "cellPageList", PageUtil.page(w.lines.stream().map(l -> cellDetail(w, l)).toList(), current, size)));
    }

    @PostMapping("/boxScanCell")
    public R<List<Map<String, Object>>> boxScanCell(@RequestBody WaveBoxScanCellReq req) {
        BoxWave w = freight.wave(req.waveNo());
        return R.ok(freight.linesAt(w, req.cellNo()).stream().map(l -> cellDetail(w, l)).toList());
    }

    /** 在库位上扫箱型（WaveSkuResp）。 */
    @PostMapping("/scanBox")
    public R<Map<String, Object>> scanBox(@RequestBody WaveOutReqBody req) {
        BoxWave w = freight.wave(req.waveNo());
        BoxLine l = freight.linesAt(w, req.cellNo()).stream().filter(x -> x.boxType.equals(req.boxType())).findFirst()
                .orElseThrow(() -> new BizException("库位 " + req.cellNo() + " 没有箱型 " + req.boxType() + " 的拣货任务"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxType", l.boxType);
        m.put("customerCode", w.customerCode);
        m.put("pickQty", l.pickQty);
        m.put("planQty", l.planQty);
        m.put("productSku", stock.boxType(l.boxType).sku());
        m.put("totalPlanQty", w.lines.stream().filter(x -> x.boxType.equals(l.boxType)).mapToInt(x -> x.planQty).sum());
        m.put("whCode", StockStore.WH);
        return R.ok(m);
    }

    /** 提交按箱拣货（WavePickSubmitResp）。boxList 中每个箱型分别扣减该库位的按箱库存。 */
    @PostMapping("/boxPick")
    public R<Map<String, Object>> boxPick(@RequestBody BoxBigReq req) {
        if (req.boxList() == null || req.boxList().isEmpty()) {
            throw new BizException("boxList 不能为空");
        }
        BoxWave w = null;
        for (BoxBigPickItem it : req.boxList()) {
            w = freight.pick(req.waveNo(), req.cellNo(), it.boxType(), it.pickQty());
        }
        return R.ok(Map.of("deviceSpecialCell", false, "pickStatus", w.pickStatus, "waveNo", w.waveNo));
    }

    /** 推荐下一个要去的库位：还有待拣箱数的任务（List&lt;RecommendCell&gt;）。 */
    @GetMapping("/recommendCell/")
    public R<List<Map<String, Object>>> recommendCell(@RequestParam String waveNo, @RequestParam(required = false) String whCode) {
        BoxWave w = freight.wave(waveNo);
        List<Map<String, Object>> out = new ArrayList<>();
        for (BoxLine l : w.lines) {
            if (l.pickQty < l.planQty) {
                BoxType b = stock.boxType(l.boxType);
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("barcode", stock.product(b.sku()).barcode());
                m.put("cellNo", l.cellNo);
                m.put("customerCode", w.customerCode);
                m.put("productQty", l.planQty - l.pickQty);
                m.put("productQuality", 1);
                m.put("productSku", b.sku());
                out.add(m);
            }
        }
        return R.ok(out);
    }

    /** 扫外箱条码取箱规（List&lt;ScanBoxBarcodeSizeRsp&gt;）。本地以箱型编号当外箱条码；Bs 后缀字段按英制换算。 */
    @PostMapping("/scanBoxBarcode")
    public R<List<Map<String, Object>>> scanBoxBarcode(@RequestBody ScanBoxBarcodeReq req) {
        BoxType b = stock.boxType(req.boxBarcode() == null ? "" : req.boxBarcode().trim());
        if (req.deliveryNo() != null && !req.deliveryNo().isBlank() && !freight.order(req.deliveryNo()).boxes.containsKey(b.boxType())) {
            throw new BizException("箱型不属于该大货单：" + b.boxType());
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boxHeight", fmt(b.height()));
        m.put("boxHeightBs", fmt(b.height() / 2.54));
        m.put("boxLength", fmt(b.length()));
        m.put("boxLengthBs", fmt(b.length() / 2.54));
        m.put("boxType", b.boxType());
        m.put("boxWeight", fmt(b.weight()));
        m.put("boxWeightBs", fmt(b.weight() * 2.20462));
        m.put("boxWeightUnit", "kg");
        m.put("boxWeightUnitBs", "lb");
        m.put("boxWidth", fmt(b.width()));
        m.put("boxWidthBs", fmt(b.width() / 2.54));
        m.put("sizeUnit", "cm");
        m.put("sizeUnitBs", "in");
        return R.ok(List.of(m));
    }

    private static String fmt(double v) {
        return String.format("%.2f", v);
    }
}
