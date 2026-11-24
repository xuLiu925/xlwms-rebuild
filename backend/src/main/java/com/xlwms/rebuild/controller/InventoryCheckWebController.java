package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.InventoryCheckStore;
import com.xlwms.rebuild.service.InventoryCheckStore.Detail;
import com.xlwms.rebuild.service.InventoryCheckStore.Order;
import com.xlwms.rebuild.service.StockStore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 批次 9：网页端“库内-盘点”页（/stock/checking）。和 PDA 盘点（/pda/inventoryCheck/**）共用 {@link InventoryCheckStore}，
 * 网页端新建的单 PDA 能直接盘，PDA 盘完网页端能看到盘盈盘亏。路径、字段与编码都是本项目占位，真实网页接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/inventoryCheck")
public class InventoryCheckWebController {

    static final Map<Integer, String> STATUS = new LinkedHashMap<>();
    static final Map<Integer, String> TYPE = Map.of(1, "明盘", 2, "盲盘");

    static {
        STATUS.put(0, "待盘点");
        STATUS.put(1, "盘点中");
        STATUS.put(2, "已完成");
        STATUS.put(3, "已取消");
    }

    private final InventoryCheckStore store;
    private final StockStore stock;

    public InventoryCheckWebController(InventoryCheckStore store, StockStore stock) {
        this.store = store;
        this.stock = stock;
    }

    private static List<Detail> details(Order o) {
        return o.cells.values().stream().flatMap(c -> c.details.stream()).toList();
    }

    private Map<String, Object> summary(Order o) {
        List<Detail> ds = details(o);
        int profit = 0;
        int loss = 0;
        int diffRows = 0;
        for (Detail d : ds) {
            if (d.solidStock == null) {
                continue;
            }
            int diff = d.solidStock - d.bookStock;
            if (diff != 0) {
                diffRows++;
            }
            profit += Math.max(diff, 0);
            loss += Math.max(-diff, 0);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("orderSn", o.orderSn);
        m.put("checkType", o.checkType);
        m.put("checkTypeName", TYPE.get(o.checkType));
        m.put("status", o.status);
        m.put("statusName", STATUS.get(o.status));
        m.put("cellCount", o.cellNos.size());
        m.put("completedCellCount", o.completedCellCount());
        m.put("diffRows", diffRows);
        m.put("profitQty", profit);
        m.put("lossQty", loss);
        m.put("cellNos", String.join(",", o.cellNos));
        m.put("remark", o.remark);
        m.put("createTime", o.createTime);
        return m;
    }

    private List<Order> filtered(Map<String, Object> body, boolean withStatus) {
        String kw = PageUtil.strParam(body, "keyword");
        String type = PageUtil.strParam(body, "checkType");
        String status = PageUtil.strParam(body, "status");
        return store.list().stream()
                .filter(o -> kw.isEmpty() || o.orderSn.contains(kw) || o.cellNos.stream().anyMatch(c -> c.contains(kw)))
                .filter(o -> type.isEmpty() || String.valueOf(o.checkType).equals(type))
                .filter(o -> !withStatus || status.isEmpty() || String.valueOf(o.status).equals(status))
                .sorted(Comparator.comparing((Order o) -> o.orderSn).reversed())
                .toList();
    }

    @PostMapping("/statistics")
    public R<List<Map<String, Object>>> statistics(@RequestBody(required = false) Map<String, Object> body) {
        List<Order> all = filtered(body, false);
        List<Map<String, Object>> out = new ArrayList<>();
        out.add(Map.of("status", "", "statusName", "全部", "statusSum", all.size()));
        STATUS.forEach((k, v) -> out.add(Map.of("status", String.valueOf(k), "statusName", v,
                "statusSum", all.stream().filter(o -> o.status == k).count())));
        return R.ok(out);
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> rows = filtered(body, true).stream().map(this::summary).toList();
        return R.ok(PageUtil.page(rows, Math.max(1, PageUtil.longParam(body, "current", 1)), PageUtil.longParam(body, "size", 20)));
    }

    /** 明细：按库位列出账面数、实盘数与差异。盘点开始前没有账面快照，只列库位。 */
    @PostMapping("/detail")
    public R<Map<String, Object>> detail(@RequestBody Map<String, Object> body) {
        Order o = store.get(PageUtil.strParam(body, "orderSn"));
        List<Map<String, Object>> cells = new ArrayList<>();
        for (String cellNo : o.cellNos) {
            var c = o.cells.get(cellNo);
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("cellNo", cellNo);
            cm.put("completed", c != null && c.completed);
            List<Map<String, Object>> items = new ArrayList<>();
            if (c != null) {
                for (Detail d : c.details) {
                    Map<String, Object> dm = new LinkedHashMap<>();
                    dm.put("sku", d.sku);
                    dm.put("productName", stock.product(d.sku).name());
                    dm.put("productQuality", d.productQuality);
                    dm.put("productQualityName", StockStore.QUALITY.get(d.productQuality));
                    dm.put("bookStock", d.bookStock);
                    dm.put("solidStock", d.solidStock);
                    dm.put("diff", d.solidStock == null ? null : d.solidStock - d.bookStock);
                    dm.put("remark", d.remark);
                    items.add(dm);
                }
            }
            cm.put("items", items);
            cells.add(cm);
        }
        Map<String, Object> m = summary(o);
        m.put("cells", cells);
        return R.ok(m);
    }

    /** 新建盘点单可选的库位：全部登记库位，标出已被未完成盘点单占用的。 */
    @PostMapping("/cells")
    public R<List<Map<String, Object>>> cells() {
        Map<String, String> busy = store.list().stream().filter(o -> o.status == 0 || o.status == 1)
                .flatMap(o -> o.cellNos.stream().map(c -> Map.entry(c, o.orderSn)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
        Set<String> all = stock.cells();
        return R.ok(all.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("cellNo", c);
            m.put("busyOrderSn", busy.getOrDefault(c, ""));
            return m;
        }).toList());
    }

    @PostMapping("/create")
    public R<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Object raw = body.get("cellNos");
        if (!(raw instanceof List<?> list)) {
            throw new BizException("请选择要盘点的库位");
        }
        int type;
        try {
            type = Integer.parseInt(PageUtil.strParam(body, "checkType"));
        } catch (NumberFormatException e) {
            throw new BizException("盘点方式只能是 1 明盘 / 2 盲盘");
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Order o = store.create(type, list.stream().map(String::valueOf).toList(), PageUtil.strParam(body, "remark"), now);
        return R.ok(summary(o));
    }

    @PostMapping("/cancel")
    public R<Map<String, Object>> cancel(@RequestBody Map<String, Object> body) {
        return R.ok(summary(store.cancel(PageUtil.strParam(body, "orderSn"))));
    }
}
