package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.PutawayStore;
import com.xlwms.rebuild.service.ReceiptStore;
import com.xlwms.rebuild.service.StockStore;
import com.xlwms.rebuild.service.StockStore.BoxType;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 其他接口：首页 /pda/home、菜单 /pda/menu/list、仓库 /pda/warehouse/{page,listParams}、
 * 系统参数 /system/sysBaseParamsDtl/findParams、版本检查 /system/androidVersionRelease/validateVersion、
 * 按箱移库 /blMoveStock/boxMoveStock（都在 /gateway/wms 下）。
 * <p>路径、参数与字段取自 APK。菜单编码 114001–114008 取自 APK 的 MenuPermissionCode；参数项 itemNo/paramsNo 取自 SettingConstants；
 * 参数值、仓库、版本号、首页统计口径、版本检查的 code 含义都是本项目占位。
 */
@RestController
@RequestMapping("/gateway/wms")
public class PdaMiscController {

    /** WmsSysParamItemBean */
    public record ParamItem(String itemNo, String itemValue, String paramsNo) {
    }

    /** WmsSystemParamReq */
    public record WmsSystemParamReq(List<ParamItem> itemList, String whCode) {
    }

    public record BoxMoveStockBoxReq(String boxType, String customerCode, String customizeBarcode, Integer moveQty, String remark,
                                     String sourceCellNo, String targetCellNo) {
    }

    public record BoxMoveStockReturnReq(String customerCode, String fnsku, Integer moveQty, String productName, Integer productQuality,
                                        String productSku, String remark, String sourceCellNo, String targetCellNo) {
    }

    public record BoxMoveStockSkuReq(String customerCode, Integer moveQty, String productName, Integer productQuality, String productSku,
                                     String remark, String sourceCellNo, Integer stockBatchId, String targetCellNo) {
    }

    /** BoxMoveStockReq */
    public record BoxMoveStockReq(List<BoxMoveStockBoxReq> boxList, List<BoxMoveStockReturnReq> returnList,
                                  List<BoxMoveStockSkuReq> skuList, String whCode) {
    }

    /** 菜单：编码取自 APK MenuPermissionCode，中文名按枚举名翻译。 */
    static final String[][] MENUS = {
            {"114001", "收货管理", "Receive"}, {"114002", "上架管理", "Shelving"}, {"114003", "出库管理", "Outbound"},
            {"114004", "拣货管理", "Picking"}, {"114005", "退货管理", "Return"}, {"114006", "库存管理", "Inventory"},
            {"114007", "复核管理", "Review"}, {"114008", "拍照管理", "Photo"}};

    static final String[][] WAREHOUSES = {{"WH01", "示例一号仓", "Demo Warehouse 1"}, {"WH02", "示例二号仓", "Demo Warehouse 2"}};

    /** paramsNo|itemNo → 值（占位） */
    static final Map<String, String> PARAMS = new LinkedHashMap<>();

    static {
        PARAMS.put("pda_config|receiving_overage_limit", "0");
        PARAMS.put("pda_config|pda_review_auto_submit", "1");
        PARAMS.put("pda_config|pda_review_outbound", "0");
        PARAMS.put("pda_config|pda_stock_select_cell_enable", "1");
        PARAMS.put("pda_config|pick_without_scan_cell", "0");
        PARAMS.put("wms_config|pallet_management_enable", "1");
        PARAMS.put("wms_config|one_inbound_one_shelf", "0");
        PARAMS.put("system_unit|measure_unit", "1");
    }

    static final String LATEST_VERSION = "2.0.0";

    private final ReceiptStore receipts;
    private final PutawayStore putaway;
    private final OutboundSampleStore outbound;
    private final StockStore stock;

    public PdaMiscController(ReceiptStore receipts, PutawayStore putaway, OutboundSampleStore outbound, StockStore stock) {
        this.receipts = receipts;
        this.putaway = putaway;
        this.outbound = outbound;
        this.stock = stock;
    }

    /** 首页三项待办：未收完的入库单、未上完的上架单、未出库的出库单。 */
    @GetMapping("/pda/home")
    public R<Map<String, Object>> home(@RequestParam(required = false) String whCode) {
        long receipt = receipts.list().stream().filter(a -> a.status() != 15).count();
        long shelf = putaway.list().stream().filter(o -> o.status() != 2).count();
        long out = outbound.waves().stream().flatMap(w -> w.deliveryNos.stream()).map(outbound::delivery).filter(d -> d.status < 30).count();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("outBoundCount", (int) out);
        m.put("putAwayCount", (int) shelf);
        m.put("receiptCount", (int) receipt);
        return R.ok(m);
    }

    @GetMapping("/pda/menu/list")
    public R<Map<String, Object>> menu(@RequestParam(required = false) String whCode) {
        List<Map<String, Object>> children = new ArrayList<>();
        for (String[] m : MENUS) {
            Map<String, Object> c = new LinkedHashMap<>();
            c.put("applicationCode", "PDA");
            c.put("children", List.of());
            c.put("code", m[0]);
            c.put("customerCode", "");
            c.put("description", "");
            c.put("hasHidden", "0");
            c.put("nameCn", m[1]);
            c.put("nameEn", m[2]);
            children.add(c);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("children", children);
        root.put("code", "PDA");
        root.put("customerCode", "");
        return R.ok(Map.of("menuResourceList", List.of(root)));
    }

    @GetMapping("/pda/warehouse/page")
    public R<Map<String, Object>> warehouses(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) Integer status) {
        List<Map<String, Object>> all = new ArrayList<>();
        for (String[] w : WAREHOUSES) {
            all.add(Map.of("tenantCode", "T-DEMO", "whCode", w[0], "whNameCn", w[1], "whNameEn", w[2]));
        }
        int from = Math.max(0, (Math.max(current, 1) - 1) * Math.max(size, 1));
        return R.ok(Map.of("records", from >= all.size() ? List.of() : all.subList(from, Math.min(all.size(), from + Math.max(size, 1)))));
    }

    private static void requireWh(String whCode) {
        if (whCode != null && !whCode.isBlank() && java.util.Arrays.stream(WAREHOUSES).noneMatch(w -> w[0].equals(whCode))) {
            throw new BizException("仓库不存在：" + whCode);
        }
    }

    /** 批量取参数值；未知参数项返回 itemValue=null。 */
    @PostMapping("/pda/warehouse/listParams")
    public R<List<ParamItem>> listParams(@RequestBody WmsSystemParamReq req) {
        requireWh(req.whCode());
        if (req.itemList() == null || req.itemList().isEmpty()) {
            throw new BizException("请指定参数项");
        }
        return R.ok(req.itemList().stream()
                .map(i -> new ParamItem(i.itemNo(), PARAMS.get(i.paramsNo() + "|" + i.itemNo()), i.paramsNo())).toList());
    }

    @GetMapping("/system/sysBaseParamsDtl/findParams")
    public R<Map<String, Object>> findParams(@RequestParam String itemNo, @RequestParam String paramsNo,
                                             @RequestParam(required = false) String whCode) {
        requireWh(whCode);
        String v = PARAMS.get(paramsNo + "|" + itemNo);
        if (v == null) {
            throw new BizException("参数不存在：" + paramsNo + "/" + itemNo);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("itemNo", itemNo);
        m.put("itemValue", v);
        m.put("tenantCode", "T-DEMO");
        m.put("whCode", whCode);
        return R.ok(m);
    }

    static int compareVersion(String a, String b) {
        String[] x = a.split("\\."), y = b.split("\\.");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int p = i < x.length ? parse(x[i]) : 0, q = i < y.length ? parse(y[i]) : 0;
            if (p != q) {
                return Integer.compare(p, q);
            }
        }
        return 0;
    }

    private static int parse(String s) {
        try {
            return Integer.parseInt(s.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** code：0 已是最新 / 1 有新版本（占位）；没有安装包下载，url 为空。 */
    @GetMapping("/system/androidVersionRelease/validateVersion")
    public R<Map<String, Object>> validateVersion(@RequestParam(required = false) String businessType,
                                                  @RequestParam(required = false) String deviceType,
                                                  @RequestParam(required = false) String version) {
        boolean newer = version == null || version.isBlank() || compareVersion(LATEST_VERSION, version.trim()) > 0;
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("changelog", "本地演示版本，无安装包");
        info.put("fileKey", "");
        info.put("packageName", "demo.wms.pda");
        info.put("releaseTime", "2026-11-24 00:00:00");
        info.put("size", 0);
        info.put("status", 1);
        info.put("updateMode", 0);
        info.put("url", "");
        info.put("versionCode", LATEST_VERSION.replace(".", ""));
        info.put("versionName", LATEST_VERSION);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", newer ? 1 : 0);
        m.put("versionInfo", newer ? info : null);
        return R.ok(m);
    }

    /** 一行移库：[来源, 目标, sku, 箱型, 品质, 数量] */
    private record Move(String source, String target, String sku, String boxType, int quality, int qty) {
    }

    private Product checkProduct(String sku, String customerCode) {
        Product p = stock.product(sku);
        if (customerCode != null && !customerCode.isBlank() && !customerCode.equals(p.customerCode())) {
            throw new BizException(sku + " 不属于客户 " + customerCode);
        }
        return p;
    }

    private static int qty(Integer q) {
        if (q == null || q <= 0) {
            throw new BizException("移库数量必须大于 0");
        }
        return q;
    }

    private static int quality(Integer q) {
        int v = q == null || q == 0 ? 1 : q;
        if (!StockStore.QUALITY.containsKey(v)) {
            throw new BizException("品质只能是 1 良品 / 2 次品");
        }
        return v;
    }

    /** 按箱 / 按 SKU / 退货库存混合移库：先整批校验（库位、客户、可用数合计、次品库位），全部通过才执行。 */
    @PostMapping("/blMoveStock/boxMoveStock")
    public R<Map<String, Object>> boxMoveStock(@RequestBody BoxMoveStockReq req) {
        List<Move> moves = new ArrayList<>();
        for (BoxMoveStockBoxReq b : nz(req.boxList())) {
            BoxType bt = stock.boxType(b.boxType());
            checkProduct(bt.sku(), b.customerCode());
            moves.add(new Move(b.sourceCellNo(), b.targetCellNo(), bt.sku(), bt.boxType(), 1, qty(b.moveQty())));
        }
        for (BoxMoveStockSkuReq s : nz(req.skuList())) {
            checkProduct(s.productSku(), s.customerCode());
            moves.add(new Move(s.sourceCellNo(), s.targetCellNo(), s.productSku(), "", quality(s.productQuality()), qty(s.moveQty())));
        }
        for (BoxMoveStockReturnReq r : nz(req.returnList())) {
            Product p = checkProduct(r.productSku(), r.customerCode());
            if (r.fnsku() != null && !r.fnsku().isBlank() && !r.fnsku().equals(p.fnsku())) {
                throw new BizException("FNSKU 与产品不符：" + r.fnsku());
            }
            moves.add(new Move(r.sourceCellNo(), r.targetCellNo(), r.productSku(), "", quality(r.productQuality()), qty(r.moveQty())));
        }
        if (moves.isEmpty()) {
            throw new BizException("请添加要移库的明细");
        }
        Map<String, Integer> need = new LinkedHashMap<>();
        for (Move m : moves) {
            stock.requireCell(m.source(), false);
            stock.requireQualityCell(m.target(), m.quality());
            if (Objects.equals(m.source().trim(), m.target().trim())) {
                throw new BizException("源库位与目标库位相同：" + m.source());
            }
            need.merge(m.source().trim() + "|" + m.sku() + "|" + m.boxType() + "|" + m.quality(), m.qty(), Integer::sum);
        }
        for (Map.Entry<String, Integer> e : need.entrySet()) {
            String[] k = e.getKey().split("\\|", -1);
            int avail = stock.availOf(k[0], k[1], k[2], Integer.parseInt(k[3]));
            if (e.getValue() > avail) {
                throw new BizException("库位 " + k[0] + " 的 " + k[1] + (k[2].isEmpty() ? "" : "（" + k[2] + "）")
                        + " 可用 " + avail + "，本次要移 " + e.getValue());
            }
        }
        for (Move m : moves) {
            stock.move(m.source().trim(), m.target().trim(), m.sku(), m.boxType(), m.quality(), m.qty());
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200);
        r.put("msg", "移库成功，共 " + moves.size() + " 行");
        return R.ok(r);
    }

    private static <T> List<T> nz(List<T> l) {
        return l == null ? List.of() : l;
    }
}
