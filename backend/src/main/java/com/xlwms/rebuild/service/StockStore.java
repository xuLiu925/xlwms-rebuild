package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;

/**
 * PDA 库内作业（/pda/stock/**、盘点、按箱拣货）共用的内存库存，重启或 reset 后恢复。数据全部虚构。
 * <ul>
 *   <li>一行库存 = 仓库 + 库位 + 客户 + SKU + 箱型(boxType，空串表示散件) + 品质</li>
 *   <li>品质 productQuality：1 良品 / 2 次品（编码是本项目假设）</li>
 *   <li>按箱库存的数量单位是“箱”，散件库存是“件”</li>
 *   <li>库位只能是 {@link #cells()} 中登记的库位；{@link #LOCKED_CELLS} 中的库位锁定，不能移入</li>
 * </ul>
 */
@Service
public class StockStore {

    public static final String WH = "WH01";
    public static final Map<Integer, String> QUALITY = Map.of(1, "良品", 2, "次品");
    public static final Set<String> LOCKED_CELLS = Set.of("E-09-09");

    /** 产品主数据（虚构）：sku → [品名, 条码, FNSKU, 客户代码, 客户名, 长cm, 宽cm, 高cm, 重kg] */
    public record Product(String sku, String name, String barcode, String fnsku, String customerCode, String customerName,
                          double length, double width, double height, double weight) {
    }

    /** 箱型主数据（虚构）：箱型 → 所装 SKU、每箱件数、外箱尺寸。 */
    public record BoxType(String boxType, String sku, int pcsPerBox, double length, double width, double height, double weight) {
    }

    public static class Row {
        public final long id;
        public final String cellNo;
        public final String productSku;
        public final String boxType;
        public final int productQuality;
        public int qty;
        public int freezeQty;

        Row(long id, String cellNo, String productSku, String boxType, int productQuality, int qty) {
            this.id = id;
            this.cellNo = cellNo;
            this.productSku = productSku;
            this.boxType = boxType;
            this.productQuality = productQuality;
            this.qty = qty;
        }
    }

    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, BoxType> boxTypes = new LinkedHashMap<>();
    private final Set<String> cells = new TreeSet<>();
    private final List<Row> rows = new ArrayList<>();
    private long seq;

    public StockStore() {
        reset();
    }

    public synchronized void reset() {
        products.clear();
        boxTypes.clear();
        cells.clear();
        rows.clear();
        seq = 0;
        product("SKU-0001", "示例收纳盒 1", "C001", "示例客户1");
        product("SKU-0002", "示例数据线 2", "C001", "示例客户1");
        product("SKU-0003", "示例T恤 3", "C001", "示例客户1");
        product("SKU-0004", "示例水壶 4", "C002", "示例客户2");
        product("SKU-0005", "示例台灯 5", "C002", "示例客户2");
        product("SKU-0006", "示例手机壳 6", "C003", "示例客户3");
        product("SKU-0008", "示例笔记本 8", "C004", "示例客户4");
        product("SKU-0009", "示例马克杯 9", "C004", "示例客户4");
        product("SKU-0010", "示例收纳箱 10", "C005", "示例客户5");
        product("SKU-0011", "示例折叠椅 11", "C005", "示例客户5");
        // 新品（批次 5）：入库后需要测量尺寸重量，见 NewProductStore
        product("SKU-0012", "示例保温杯 12", "C001", "示例客户1");
        product("SKU-0013", "示例毛巾 13", "C002", "示例客户2");
        boxTypes.put("BOX-0010-A", new BoxType("BOX-0010-A", "SKU-0010", 12, 60, 40, 40, 14.5));
        boxTypes.put("BOX-0011-A", new BoxType("BOX-0011-A", "SKU-0011", 4, 90, 55, 30, 21.0));
        cells.addAll(List.of("A-01-01", "A-01-02", "A-02-01", "B-01-01", "B-01-03", "C-03-02", "D-01-01", "D-01-02",
                "E-01-01", "E-01-02", "E-09-09", "F-01-01", "F-01-02", "F-02-01"));
        add("A-01-01", "SKU-0001", "", 1, 20);
        add("A-01-01", "SKU-0004", "", 1, 5);
        add("A-01-02", "SKU-0002", "", 1, 15);
        add("A-02-01", "SKU-0003", "", 1, 8);
        add("A-02-01", "SKU-0003", "", 2, 2);
        add("B-01-01", "SKU-0004", "", 1, 30);
        add("B-01-03", "SKU-0005", "", 1, 12);
        add("C-03-02", "SKU-0006", "", 1, 40);
        add("D-01-01", "SKU-0008", "", 1, 25);
        add("D-01-02", "SKU-0009", "", 1, 18);
        add("F-01-01", "SKU-0010", "BOX-0010-A", 1, 6);
        add("F-01-02", "SKU-0010", "BOX-0010-A", 1, 4);
        add("F-02-01", "SKU-0011", "BOX-0011-A", 1, 10);
    }

    private void product(String sku, String name, String cc, String cn) {
        int n = Integer.parseInt(sku.substring(4));
        products.put(sku, new Product(sku, name, String.format("BC0001%04d", n), String.format("FN-DEMO-%04d", n), cc, cn,
                10 + n, 8 + n * 0.5, 5 + n * 0.3, Math.round((0.2 + n * 0.1) * 100) / 100.0));
    }

    private Row add(String cell, String sku, String boxType, int quality, int qty) {
        Row r = new Row(++seq, cell, sku, boxType, quality, qty);
        rows.add(r);
        return r;
    }

    // ---------- 查询 ----------

    public synchronized Set<String> cells() {
        return new TreeSet<>(cells);
    }

    public synchronized Product product(String sku) {
        Product p = products.get(sku);
        if (p == null) {
            throw new BizException("产品不存在：" + sku);
        }
        return p;
    }

    /** 按 SKU / 条码 / FNSKU 找产品，找不到返回 null。 */
    public synchronized Product findProduct(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String c = code.trim();
        return products.values().stream()
                .filter(p -> p.sku().equalsIgnoreCase(c) || p.barcode().equals(c) || p.fnsku().equalsIgnoreCase(c))
                .findFirst().orElse(null);
    }

    public synchronized BoxType boxType(String boxType) {
        BoxType b = boxTypes.get(boxType);
        if (b == null) {
            throw new BizException("箱型不存在：" + boxType);
        }
        return b;
    }

    /** 有库存（qty>0）的行快照，按条件过滤；参数为空表示不限。 */
    public synchronized List<Row> find(String cellNo, String customerCode, String sku, String boxType, Integer quality) {
        List<Row> out = new ArrayList<>();
        for (Row r : rows) {
            if (r.qty <= 0) {
                continue;
            }
            if (notBlank(cellNo) && !r.cellNo.equalsIgnoreCase(cellNo.trim())) {
                continue;
            }
            if (notBlank(customerCode) && !product(r.productSku).customerCode().equals(customerCode.trim())) {
                continue;
            }
            if (notBlank(sku) && !r.productSku.equalsIgnoreCase(sku.trim())) {
                continue;
            }
            if (boxType != null && !r.boxType.equals(boxType.trim())) {
                continue;
            }
            if (quality != null && r.productQuality != quality) {
                continue;
            }
            out.add(r);
        }
        return out;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    public synchronized void requireCell(String cellNo, boolean forPutIn) {
        if (cellNo == null || !cells.contains(cellNo.trim())) {
            throw new BizException("库位不存在：" + cellNo);
        }
        if (forPutIn && LOCKED_CELLS.contains(cellNo.trim())) {
            throw new BizException("库位已锁定：" + cellNo);
        }
    }

    private Row exact(String cellNo, String sku, String boxType, int quality) {
        return rows.stream().filter(r -> r.cellNo.equals(cellNo) && r.productSku.equals(sku)
                && r.boxType.equals(boxType) && r.productQuality == quality).findFirst().orElse(null);
    }

    public synchronized int qtyOf(String cellNo, String sku, String boxType, int quality) {
        Row r = exact(cellNo, sku, boxType, quality);
        return r == null ? 0 : r.qty;
    }

    // ---------- 变更 ----------

    /** 调整库存到 newQty（盘点、库存调整用）。返回调整前数量。 */
    public synchronized int setQty(String cellNo, String sku, String boxType, int quality, int newQty) {
        requireCell(cellNo, false);
        product(sku);
        if (newQty < 0) {
            throw new BizException("调整后库存不能为负数");
        }
        Row r = exact(cellNo, sku, boxType, quality);
        int before = r == null ? 0 : r.qty;
        if (r == null) {
            if (newQty > 0) {
                add(cellNo, sku, boxType, quality, newQty);
            }
        } else {
            if (newQty < r.freezeQty) {
                throw new BizException("调整后库存小于冻结数量 " + r.freezeQty);
            }
            r.qty = newQty;
        }
        return before;
    }

    /** 入库上架：把 qty 加到目标库位（目标库位必须存在且未锁定）。 */
    public synchronized void putIn(String cellNo, String sku, String boxType, int quality, int qty) {
        requireCell(cellNo, true);
        product(sku);
        if (qty <= 0) {
            throw new BizException("上架数量必须大于 0");
        }
        Row r = exact(cellNo.trim(), sku, boxType, quality);
        if (r == null) {
            add(cellNo.trim(), sku, boxType, quality, qty);
        } else {
            r.qty += qty;
        }
    }

    /** 有某 SKU 库存的库位（推荐上架库位用），按库位号排序。 */
    public synchronized List<String> cellsHolding(String sku) {
        return rows.stream().filter(r -> r.qty > 0 && r.productSku.equals(sku)).map(r -> r.cellNo).distinct().sorted().toList();
    }

    /** 没有任何库存、也未锁定的库位。 */
    public synchronized List<String> emptyCells() {
        return cells.stream().filter(c -> !LOCKED_CELLS.contains(c) && rows.stream().noneMatch(r -> r.qty > 0 && r.cellNo.equals(c))).toList();
    }

    /** 扣减库存（拣货用）。 */
    public synchronized void deduct(String cellNo, String sku, String boxType, int quality, int qty) {
        Row r = exact(cellNo, sku, boxType, quality);
        int avail = r == null ? 0 : r.qty - r.freezeQty;
        if (qty <= 0 || qty > avail) {
            throw new BizException("库位 " + cellNo + " 可用库存不足，可用 " + avail);
        }
        r.qty -= qty;
    }

    /** 移库：从源库位移到目标库位。 */
    public synchronized void move(String source, String target, String sku, String boxType, int quality, int qty) {
        requireCell(source, false);
        requireCell(target, true);
        if (Objects.equals(source.trim(), target.trim())) {
            throw new BizException("源库位与目标库位相同");
        }
        deduct(source.trim(), sku, boxType, quality, qty);
        Row t = exact(target.trim(), sku, boxType, quality);
        if (t == null) {
            add(target.trim(), sku, boxType, quality, qty);
        } else {
            t.qty += qty;
        }
    }

    /** 整库位移库：源库位全部可用库存移到目标库位。返回移动的行数。 */
    public synchronized int moveCell(String source, String target) {
        List<Row> src = find(source, null, null, null, null);
        if (src.isEmpty()) {
            throw new BizException("源库位没有库存：" + source);
        }
        requireCell(target, true);
        for (Row r : src) {
            if (r.freezeQty > 0) {
                throw new BizException("源库位有冻结库存，不能整库位移动");
            }
        }
        for (Row r : src) {
            move(source, target, r.productSku, r.boxType, r.productQuality, r.qty);
        }
        return src.size();
    }
}
