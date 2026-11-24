package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.dto.stock.StockReqs.StockAdjustReq;
import com.xlwms.rebuild.dto.stock.StockReqs.StockCellListReq;
import com.xlwms.rebuild.dto.stock.StockReqs.StockCellQualityReq;
import com.xlwms.rebuild.dto.stock.StockReqs.StockCheckReq;
import com.xlwms.rebuild.dto.stock.StockReqs.StockDetailReq;
import com.xlwms.rebuild.dto.stock.StockReqs.StockMoveReq;
import com.xlwms.rebuild.dto.stock.StockReqs.StockSearchReq;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.BoxType;
import com.xlwms.rebuild.service.StockStore.Product;
import com.xlwms.rebuild.service.StockStore.Row;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 库内作业：/gateway/wms/pda/stock/** 共 10 个接口。路径、请求参数名、请求体与响应字段取自 APK
 * （ApiService + data.model.bean）；业务规则与编码（品质、adjustFlag、queryType 等）是本项目假设。
 * 数据在 {@link StockStore}（内存、虚构），不连生产。
 */
@RestController
@RequestMapping("/gateway/wms/pda/stock")
public class PdaStockController {

    private final StockStore store;

    public PdaStockController(StockStore store) {
        this.store = store;
    }

    private static <T> Map<String, Object> records(List<T> all, int current, int size) {
        return PageUtil.page(all, current <= 0 ? 1 : current, size <= 0 ? 20 : size);
    }

    private static int parseQty(String v, String label) {
        try {
            int n = Integer.parseInt(v == null ? "" : v.trim());
            if (n <= 0) {
                throw new BizException(label + "必须大于 0");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new BizException(label + "格式错误：" + v);
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    private static int quality(Integer q) {
        return q == null ? 1 : q;
    }

    /** StockCellItemResp */
    private Map<String, Object> item(Row r) {
        Product p = store.product(r.productSku);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("barcode", p.barcode());
        m.put("boxType", r.boxType);
        m.put("customerCode", p.customerCode());
        m.put("customizeBarcode", "");
        m.put("fnsku", p.fnsku());
        m.put("freezeQty", r.freezeQty);
        m.put("noCheckStkQty", 0);
        m.put("pictureUrl", "");
        m.put("productName", p.name());
        m.put("productQuality", r.productQuality);
        m.put("productQualityName", StockStore.QUALITY.get(r.productQuality));
        m.put("productSku", r.productSku);
        m.put("skuId", String.valueOf(r.id));
        m.put("swapStkQty", 0);
        m.put("useStkQty", r.qty - r.freezeQty);
        return m;
    }

    /** WarehouseModel（按库位汇总一个 SKU / 箱型的库存）。 */
    private static Map<String, Object> warehouse(String cellNo, List<Row> rs) {
        int total = rs.stream().mapToInt(r -> r.qty).sum();
        int lock = rs.stream().mapToInt(r -> r.freezeQty).sum();
        int packed = rs.stream().filter(r -> !r.boxType.isEmpty()).mapToInt(r -> r.qty).sum();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("area", cellNo.substring(0, 1) + "区");
        m.put("inventoryCount", String.valueOf(total));
        m.put("lockCount", String.valueOf(lock));
        m.put("name", cellNo);
        m.put("packedCount", String.valueOf(packed));
        m.put("unPackedCount", String.valueOf(total - packed));
        m.put("validCount", String.valueOf(total - lock));
        return m;
    }

    @GetMapping("/count")
    public R<Map<String, Object>> count(@RequestParam String cellNo, @RequestParam(required = false) String whCode) {
        store.requireCell(cellNo, false);
        List<Row> rs = store.find(cellNo, null, null, null, null);
        return R.ok(Map.of(
                "boxCount", rs.stream().filter(r -> !r.boxType.isEmpty()).mapToInt(r -> r.qty).sum(),
                "fnskuCount", (int) rs.stream().filter(r -> r.boxType.isEmpty()).map(r -> r.productSku).distinct().count(),
                "skuCount", (int) rs.stream().map(r -> r.productSku).distinct().count()));
    }

    /** 有符合条件库存的库位号列表（Records&lt;String&gt;）。stockType 本项目假设：1 散件 / 2 按箱，空 = 全部。 */
    @GetMapping("/cell/list")
    public R<Map<String, Object>> cellList(@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "1") int current,
                                           @RequestParam(required = false) String whCode, @RequestParam(required = false) String customerCode,
                                           @RequestParam(required = false) Integer type, @RequestParam(required = false) String cellNo,
                                           @RequestParam(required = false) Integer stockType, @RequestParam(required = false) String productSku,
                                           @RequestParam(required = false) String boxType, @RequestParam(required = false) String fnsku) {
        String sku = productSku;
        if ((sku == null || sku.isBlank()) && fnsku != null && !fnsku.isBlank()) {
            Product p = store.findProduct(fnsku);
            sku = p == null ? "__none__" : p.sku();
        }
        List<String> cells = store.find(null, customerCode, sku, boxType == null || boxType.isBlank() ? null : boxType, null).stream()
                .filter(r -> stockType == null || (stockType == 2) == !r.boxType.isEmpty())
                .map(r -> r.cellNo)
                .filter(c -> cellNo == null || cellNo.isBlank() || c.contains(cellNo.trim()))
                .distinct().sorted().toList();
        return R.ok(records(cells, current, size));
    }

    /** 一个库位的库存，按客户分组（Records&lt;StockCellCustomerResp&gt;）。 */
    @PostMapping("/cellList")
    public R<Map<String, Object>> cellListByCustomer(@RequestBody StockCellListReq req) {
        store.requireCell(req.cellNo(), false);
        Map<String, List<Row>> byCustomer = store.find(req.cellNo(), req.customerCode(), null, null, null).stream()
                .collect(Collectors.groupingBy(r -> store.product(r.productSku).customerCode(), LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> out = new ArrayList<>();
        byCustomer.forEach((cc, rs) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("customerCode", cc);
            m.put("customerName", store.product(rs.get(0).productSku).customerName());
            m.put("customerStockCellList", records(rs.stream().map(this::item).toList(), 1, 200));
            out.add(m);
        });
        return R.ok(records(out, req.current(), req.size()));
    }

    /** 一个库位下某客户的库存明细（Records&lt;StockCellItemResp&gt;）。 */
    @PostMapping("/customer/cellList")
    public R<Map<String, Object>> customerCellList(@RequestBody StockCellListReq req) {
        store.requireCell(req.cellNo(), false);
        return R.ok(records(store.find(req.cellNo(), req.customerCode(), null, null, null).stream().map(this::item).toList(),
                req.current(), req.size()));
    }

    /** 一个 SKU（或箱型）在各库位的分布（StockDetailResp）。 */
    @PostMapping("/detail")
    public R<Map<String, Object>> detail(@RequestBody StockDetailReq req) {
        String sku = req.productSku();
        if ((sku == null || sku.isBlank())) {
            Product p = store.findProduct(req.fnsku() != null && !req.fnsku().isBlank() ? req.fnsku() : req.customizeBarcode());
            if (p == null && (req.boxType() == null || req.boxType().isBlank())) {
                throw new BizException("请提供 productSku、fnsku 或 boxType");
            }
            sku = p == null ? null : p.sku();
        }
        List<Row> rs = store.find(null, req.customerCode(), sku, req.boxType() == null || req.boxType().isBlank() ? null : req.boxType(),
                req.productQuality());
        Map<String, List<Row>> byCell = rs.stream().collect(Collectors.groupingBy(r -> r.cellNo, java.util.TreeMap::new, Collectors.toList()));
        List<Map<String, Object>> list = new ArrayList<>();
        byCell.forEach((cell, cr) -> list.add(warehouse(cell, cr)));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("noCheckStkQty", "0");
        m.put("stockList", records(list, req.current(), req.size()));
        m.put("swapStkQty", "0");
        m.put("totalStock", rs.stream().mapToInt(r -> r.qty).sum());
        m.put("totalUseStkQty", rs.stream().mapToInt(r -> r.qty - r.freezeQty).sum());
        return R.ok(m);
    }

    /** 综合查询（StockSearchResp：productList / fnskuList / stockBoxList / stockCellList 四组分页）。 */
    @PostMapping("/search")
    public R<Map<String, Object>> search(@RequestBody StockSearchReq req) {
        String q = nz(req.queryParam());
        if (q.isEmpty()) {
            throw new BizException("请输入查询内容");
        }
        Integer t = req.queryType();
        List<Map<String, Object>> productList = new ArrayList<>();
        List<Map<String, Object>> fnskuList = new ArrayList<>();
        List<Map<String, Object>> boxList = new ArrayList<>();
        List<Map<String, Object>> cellList = new ArrayList<>();
        Product p = store.findProduct(q);
        if (p != null && (t == null || t == 1)) {
            List<Row> rs = store.find(null, null, p.sku(), "", null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("barcode", p.barcode());
            m.put("customerCode", p.customerCode());
            m.put("customerName", p.customerName());
            m.put("height", p.height());
            m.put("length", p.length());
            m.put("lockCount", rs.stream().mapToInt(r -> r.freezeQty).sum());
            m.put("name", p.sku());
            m.put("pictureUrl", "");
            m.put("productName", p.name());
            m.put("productQuality", 1);
            m.put("validCount", rs.stream().mapToInt(r -> r.qty - r.freezeQty).sum());
            m.put("weight", p.weight());
            m.put("width", p.width());
            productList.add(m);
        }
        if (p != null && (t == null || t == 2)) {
            List<Row> rs = store.find(null, null, p.sku(), null, null);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("barcode", p.barcode());
            m.put("customerCode", p.customerCode());
            m.put("customerName", p.customerName());
            m.put("lockCount", rs.stream().mapToInt(r -> r.freezeQty).sum());
            m.put("name", p.fnsku());
            m.put("packedCount", rs.stream().filter(r -> !r.boxType.isEmpty()).mapToInt(r -> r.qty).sum());
            m.put("pictureUrl", "");
            m.put("productName", p.name());
            m.put("productQuality", 1);
            m.put("skuName", p.sku());
            m.put("unPackedCount", rs.stream().filter(r -> r.boxType.isEmpty()).mapToInt(r -> r.qty).sum());
            fnskuList.add(m);
        }
        if (t == null || t == 3) {
            store.find(null, null, null, null, null).stream().filter(r -> r.boxType.equalsIgnoreCase(q))
                    .collect(Collectors.groupingBy(r -> r.boxType, LinkedHashMap::new, Collectors.toList()))
                    .forEach((bt, rs) -> {
                        BoxType b = store.boxType(bt);
                        Product bp = store.product(b.sku());
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("customCode", "");
                        m.put("customerCode", bp.customerCode());
                        m.put("customerName", bp.customerName());
                        m.put("height", b.height());
                        m.put("length", b.length());
                        m.put("lockCount", rs.stream().mapToInt(r -> r.freezeQty).sum());
                        m.put("name", bt);
                        m.put("validCount", rs.stream().mapToInt(r -> r.qty - r.freezeQty).sum());
                        m.put("weight", b.weight());
                        m.put("width", b.width());
                        boxList.add(m);
                    });
        }
        if ((t == null || t == 4) && store.cells().contains(q)) {
            cellList.add(warehouse(q, store.find(q, null, null, null, null)));
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fnskuList", records(fnskuList, req.current(), req.size()));
        m.put("productList", records(productList, req.current(), req.size()));
        m.put("stockBoxList", records(boxList, req.current(), req.size()));
        m.put("stockCellList", records(cellList, req.current(), req.size()));
        return R.ok(m);
    }

    /** 查当前可用数量（调整、移库前用；StockCheckResp）。 */
    @PostMapping("/check")
    public R<Map<String, Object>> check(@RequestBody StockCheckReq req) {
        store.requireCell(req.cellNo(), false);
        String sku = resolveSku(req.productSku(), req.fnsku(), req.boxType());
        int q = quality(req.productQuality());
        return R.ok(Map.of("cellNo", req.cellNo(), "productQuality", q,
                "useStkQty", String.valueOf(store.qtyOf(req.cellNo(), sku, nz(req.boxType()), q))));
    }

    private String resolveSku(String productSku, String fnsku, String boxType) {
        if (productSku != null && !productSku.isBlank()) {
            return store.product(productSku.trim()).sku();
        }
        if (boxType != null && !boxType.isBlank()) {
            return store.boxType(boxType.trim()).sku();
        }
        Product p = store.findProduct(fnsku);
        if (p == null) {
            throw new BizException("请提供 productSku、fnsku 或 boxType");
        }
        return p.sku();
    }

    /** 库存调整。beforeStkQty 必须与当前库存一致（防止并发覆盖），否则提示刷新。 */
    @PostMapping("/adjust")
    public R<Object> adjust(@RequestBody StockAdjustReq req) {
        String sku = resolveSku(req.productSku(), req.fnsku(), req.boxType());
        String bt = nz(req.boxType());
        int q = quality(req.productQuality());
        int current = store.qtyOf(nz(req.cellNo()), sku, bt, q);
        if (req.beforeStkQty() != null && !req.beforeStkQty().isBlank() && !req.beforeStkQty().trim().equals(String.valueOf(current))) {
            throw new BizException("库存已变化（当前 " + current + "），请刷新后重试");
        }
        int qty = parseQty(req.adjustQty(), "调整数量");
        int flag = req.adjustFlag() == null ? 1 : req.adjustFlag();
        if (flag != 1 && flag != 2) {
            throw new BizException("adjustFlag 只能是 1（调增）或 2（调减）");
        }
        store.setQty(nz(req.cellNo()), sku, bt, q, flag == 1 ? current + qty : current - qty);
        return R.ok(null);
    }

    @PostMapping("/move")
    public R<Object> move(@RequestBody StockMoveReq req) {
        String sku = resolveSku(req.productSku(), req.fnsku(), req.boxType());
        store.move(nz(req.sourceCellNo()), nz(req.targetCellNo()), sku, nz(req.boxType()), quality(req.productQuality()),
                parseQty(req.moveQty(), "移库数量"));
        return R.ok(null);
    }

    @PostMapping("/stockCellQuality")
    public R<Object> moveCell(@RequestBody StockCellQualityReq req) {
        store.moveCell(nz(req.sourceCellNo()), nz(req.targetCellNo()));
        return R.ok(null);
    }

}
