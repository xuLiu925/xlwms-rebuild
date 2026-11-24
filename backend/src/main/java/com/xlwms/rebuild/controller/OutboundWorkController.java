package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.review.ReviewItem;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
import com.xlwms.rebuild.service.OutboundSampleStore.Pack;
import com.xlwms.rebuild.service.OutboundSampleStore.PackItem;
import com.xlwms.rebuild.service.OutboundSampleStore.Wave;
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
 * 网页端出库作业：二次分拣、复核、按箱复核、称重出库。路径 /gateway/wms/outboundWork/** 与字段是本项目占位
 * （真实网页接口未抓取）。与 PDA 拣货/复核接口共用 {@link OutboundSampleStore} 的内存状态，数据虚构。
 */
@RestController
@RequestMapping("/gateway/wms/outboundWork")
public class OutboundWorkController {

    private final OutboundSampleStore store;

    public OutboundWorkController(OutboundSampleStore store) {
        this.store = store;
    }

    private static Map<String, Object> deliveryView(Delivery d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("deliveryNo", d.deliveryNo);
        m.put("expressNo", d.expressNo);
        m.put("waveNo", d.waveNo);
        m.put("customerName", d.customerName);
        m.put("logisticsChannel", d.logisticsChannel);
        m.put("status", d.status);
        m.put("statusName", OutboundSampleStore.DELIVERY_STATUS.get(d.status));
        m.put("skuNum", d.skuNum());
        m.put("scanNum", d.scanNum());
        m.put("weightKg", d.weightKg);
        List<Map<String, Object>> packs = new ArrayList<>();
        for (Pack p : d.packs) {
            Map<String, Object> pm = new LinkedHashMap<>();
            pm.put("packNo", p.packNo);
            pm.put("reviewStatus", p.reviewStatus);
            pm.put("items", p.items.stream().map(i -> {
                Map<String, Object> im = new LinkedHashMap<>();
                im.put("sku", i.sku);
                im.put("barcode", i.barcode);
                im.put("productName", i.productName);
                im.put("qty", i.qty);
                im.put("scanQty", i.scanQty);
                im.put("sortQty", i.sortQty);
                return im;
            }).toList());
            packs.add(pm);
        }
        m.put("packs", packs);
        return m;
    }

    private Map<String, Object> waveView(Wave w) {
        List<Map<String, Object>> grids = new ArrayList<>();
        for (int i = 0; i < w.deliveryNos.size(); i++) {
            Delivery d = store.delivery(w.deliveryNos.get(i));
            int qty = d.packs.stream().flatMap(p -> p.items.stream()).mapToInt(it -> it.qty).sum();
            int sorted = d.packs.stream().flatMap(p -> p.items.stream()).mapToInt(it -> it.sortQty).sum();
            grids.add(Map.of("gridNo", i + 1, "deliveryNo", d.deliveryNo, "qty", qty, "sortQty", sorted));
        }
        return Map.of("waveNo", w.waveNo, "customerName", w.customerName, "pickStatus", w.pickStatus,
                "pickStatusName", OutboundSampleStore.PICK_STATUS.get(w.pickStatus), "grids", grids);
    }

    /** 可二次分拣的波次：多品拣货且已拣完。 */
    @GetMapping("/secondSort/waves")
    public R<List<Map<String, Object>>> sortWaves() {
        return R.ok(store.waves().stream().filter(w -> w.pickWay == 2).map(this::waveView).toList());
    }

    @GetMapping("/secondSort/wave")
    public R<Map<String, Object>> sortWave(@RequestParam String waveNo) {
        return R.ok(waveView(store.wave(waveNo)));
    }

    @PostMapping("/secondSort/scan")
    public R<Map<String, Object>> sortScan(@RequestBody Map<String, Object> body) {
        String waveNo = PageUtil.strParam(body, "waveNo");
        Object[] r = store.sortScan(waveNo, PageUtil.strParam(body, "barcode"));
        Map<String, Object> m = new LinkedHashMap<>(waveView(store.wave(waveNo)));
        m.put("gridNo", r[0]);
        m.put("deliveryNo", ((Delivery) r[1]).deliveryNo);
        return R.ok(m);
    }

    /** 按出库单号或快递单号取出库单（复核、按箱复核、称重页共用）。 */
    @GetMapping("/order")
    public R<Map<String, Object>> order(@RequestParam String no) {
        return R.ok(deliveryView(store.lookup(no)));
    }

    /** 逐件扫描复核：条码记到第一个还没扫满的包裹上。 */
    @PostMapping("/review/scan")
    public R<Map<String, Object>> reviewScan(@RequestBody Map<String, Object> body) {
        Delivery d = store.lookup(PageUtil.strParam(body, "no"));
        String barcode = PageUtil.strParam(body, "barcode");
        Pack target = null;
        for (Pack p : d.packs) {
            for (PackItem it : p.items) {
                if (it.barcode.equals(barcode) && it.scanQty < it.qty) {
                    target = p;
                    break;
                }
            }
            if (target != null) {
                break;
            }
        }
        if (target == null) {
            throw new BizException("条码不属于该出库单，或已扫满：" + barcode);
        }
        store.review(d.deliveryNo, target.packNo, false, List.of(new ReviewItem(barcode, List.of(), 1)));
        return R.ok(deliveryView(d));
    }

    @PostMapping("/boxReview/scan")
    public R<Map<String, Object>> boxReview(@RequestBody Map<String, Object> body) {
        return R.ok(deliveryView(store.reviewPack(PageUtil.strParam(body, "packNo"))));
    }

    @PostMapping("/weight")
    public R<Map<String, Object>> weight(@RequestBody Map<String, Object> body) {
        double kg;
        try {
            kg = Double.parseDouble(PageUtil.strParam(body, "weightKg"));
        } catch (NumberFormatException e) {
            throw new BizException("重量格式错误");
        }
        return R.ok(deliveryView(store.weighAndShip(PageUtil.strParam(body, "no"), kg)));
    }
}
