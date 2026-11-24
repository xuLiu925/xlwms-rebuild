package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BasicCell;
import com.xlwms.rebuild.mapper.BasicCellMapper;
import com.xlwms.rebuild.service.InboundWorkStore;
import com.xlwms.rebuild.service.InboundWorkStore.Asn;
import com.xlwms.rebuild.service.InboundWorkStore.Line;
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
 * 网页端入库作业：收货上架页与上架页。路径 /gateway/wms/inboundWork/** 与字段全是本项目占位（真实网页接口未抓取）。
 * 数据在 {@link InboundWorkStore}（内存、虚构）；上架库位校验读本地 basic_cell 表：库位必须存在且不是锁定状态（usage_status=2）。
 */
@RestController
@RequestMapping("/gateway/wms/inboundWork")
public class InboundWorkController {

    private final InboundWorkStore store;
    private final BasicCellMapper cellMapper;

    public InboundWorkController(InboundWorkStore store, BasicCellMapper cellMapper) {
        this.store = store;
        this.cellMapper = cellMapper;
    }

    static Map<String, Object> lineView(Line l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("sku", l.sku);
        m.put("barcode", l.barcode);
        m.put("productName", l.productName);
        m.put("planQty", l.planQty);
        m.put("receivedQty", l.receivedQty);
        m.put("shelvedQty", l.shelvedQty);
        m.put("pendingShelfQty", l.pendingShelfQty());
        m.put("cells", l.cells);
        return m;
    }

    static Map<String, Object> asnView(Asn a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asnNo", a.asnNo);
        m.put("customerCode", a.customerCode);
        m.put("customerName", a.customerName);
        m.put("whCode", a.whCode);
        m.put("status", a.status());
        m.put("statusName", InboundWorkStore.ASN_STATUS.get(a.status()));
        m.put("lines", a.lines.stream().map(InboundWorkController::lineView).toList());
        return m;
    }

    @GetMapping("/asnList")
    public R<List<Map<String, Object>>> asnList() {
        return R.ok(store.list().stream().map(InboundWorkController::asnView).toList());
    }

    @GetMapping("/asn")
    public R<Map<String, Object>> asn(@RequestParam String asnNo) {
        return R.ok(asnView(store.get(asnNo)));
    }

    @PostMapping("/receive")
    public R<Map<String, Object>> receive(@RequestBody Map<String, Object> body) {
        String asnNo = PageUtil.strParam(body, "asnNo");
        store.receive(asnNo, PageUtil.strParam(body, "barcode"), (int) PageUtil.longParam(body, "qty", 1));
        return R.ok(asnView(store.get(asnNo)));
    }

    /** 待上架明细：所有入库单里已收货未上架的行。 */
    @GetMapping("/pendingShelf")
    public R<List<Map<String, Object>>> pendingShelf() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Asn a : store.list()) {
            for (Line l : a.lines) {
                if (l.pendingShelfQty() > 0) {
                    Map<String, Object> m = lineView(l);
                    m.put("asnNo", a.asnNo);
                    m.put("customerName", a.customerName);
                    m.put("whCode", a.whCode);
                    out.add(m);
                }
            }
        }
        return R.ok(out);
    }

    /** 可上架库位（未锁定），给页面做推荐下拉。 */
    @GetMapping("/cells")
    public R<List<String>> cells(@RequestParam(defaultValue = "WH01") String whCode) {
        return R.ok(cellMapper.selectList(new LambdaQueryWrapper<BasicCell>()
                        .eq(BasicCell::getWhCode, whCode).ne(BasicCell::getUsageStatus, 2).orderByAsc(BasicCell::getCellCode))
                .stream().map(BasicCell::getCellCode).toList());
    }

    @PostMapping("/putaway")
    public R<Map<String, Object>> putaway(@RequestBody Map<String, Object> body) {
        String asnNo = PageUtil.strParam(body, "asnNo");
        String cellCode = PageUtil.strParam(body, "cellCode");
        Asn a = store.get(asnNo);
        BasicCell cell = cellMapper.selectOne(new LambdaQueryWrapper<BasicCell>()
                .eq(BasicCell::getCellCode, cellCode).eq(BasicCell::getWhCode, a.whCode).last("LIMIT 1"));
        if (cell == null) {
            throw new BizException("库位不存在或不属于仓库 " + a.whCode + "：" + cellCode);
        }
        if (Integer.valueOf(2).equals(cell.getUsageStatus())) {
            throw new BizException("库位已锁定，不能上架：" + cellCode);
        }
        store.putaway(asnNo, PageUtil.strParam(body, "sku"), cellCode, (int) PageUtil.longParam(body, "qty", 0));
        return R.ok(asnView(a));
    }

    /** 本地演示专用：恢复入库作业示例数据。 */
    @PostMapping("/reset")
    public R<Boolean> reset() {
        store.reset();
        return R.ok(true);
    }
}
