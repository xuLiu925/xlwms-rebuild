package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.DefectiveStore;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.Product;
import com.xlwms.rebuild.service.StockStore.Row;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 批次 9：网页端“库内-次品处理”页（/stock/defective）。读写 {@link StockStore} 的次品库存，与 PDA 库内、退货上架共用。
 * 路径、字段都是本项目占位，真实网页接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/stockDefective")
public class DefectiveController {

    private final StockStore stock;
    private final DefectiveStore store;

    public DefectiveController(StockStore stock, DefectiveStore store) {
        this.stock = stock;
        this.store = store;
    }

    private Map<String, Object> row(Row r) {
        Product p = stock.product(r.productSku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.id);
        m.put("cellNo", r.cellNo);
        m.put("sku", r.productSku);
        m.put("productName", p.name());
        m.put("customerCode", p.customerCode());
        m.put("customerName", p.customerName());
        m.put("boxType", r.boxType);
        m.put("qty", r.qty);
        m.put("freezeQty", r.freezeQty);
        m.put("availQty", r.qty - r.freezeQty);
        return m;
    }

    /** 次品库存列表：关键字匹配 SKU / 品名 / 库位，可按客户筛选；附带汇总。 */
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        String kw = PageUtil.strParam(body, "keyword").toUpperCase();
        String customer = PageUtil.strParam(body, "customerCode");
        List<Map<String, Object>> rows = stock.find(null, customer, null, null, 2).stream().map(this::row)
                .filter(m -> kw.isEmpty() || m.get("sku").toString().toUpperCase().contains(kw)
                        || m.get("cellNo").toString().toUpperCase().contains(kw) || m.get("productName").toString().contains(kw))
                .toList();
        Map<String, Object> page = new LinkedHashMap<>(PageUtil.page(rows, body));
        page.put("totalQty", rows.stream().mapToInt(m -> (int) m.get("qty")).sum());
        page.put("skuCount", rows.stream().map(m -> m.get("sku")).distinct().count());
        page.put("customers", stock.customers());
        return R.ok(page);
    }

    private static int qty(Map<String, Object> body) {
        try {
            return Integer.parseInt(PageUtil.strParam(body, "qty"));
        } catch (NumberFormatException e) {
            throw new BizException("处理数量必须是整数");
        }
    }

    @PostMapping("/toGood")
    public R<DefectiveStore.Record> toGood(@RequestBody Map<String, Object> body) {
        return R.ok(store.toGood(PageUtil.strParam(body, "cellNo"), PageUtil.strParam(body, "sku"), PageUtil.strParam(body, "boxType"),
                qty(body), PageUtil.strParam(body, "targetCellNo"), PageUtil.strParam(body, "remark")));
    }

    @PostMapping("/destroy")
    public R<DefectiveStore.Record> destroy(@RequestBody Map<String, Object> body) {
        return R.ok(store.destroy(PageUtil.strParam(body, "cellNo"), PageUtil.strParam(body, "sku"), PageUtil.strParam(body, "boxType"),
                qty(body), PageUtil.strParam(body, "remark")));
    }

    @PostMapping("/records")
    public R<Map<String, Object>> records(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(PageUtil.page(store.records(), body));
    }
}
