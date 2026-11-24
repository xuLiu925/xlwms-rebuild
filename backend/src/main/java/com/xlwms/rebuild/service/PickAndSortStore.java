package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
import com.xlwms.rebuild.service.OutboundSampleStore.Pack;
import com.xlwms.rebuild.service.OutboundSampleStore.PackItem;
import com.xlwms.rebuild.service.OutboundSampleStore.PickLine;
import com.xlwms.rebuild.service.OutboundSampleStore.Wave;
import com.xlwms.rebuild.service.StockStore.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 边拣边分（拣货车分格）。接口与字段取自 APK 的 PickAndSort*；规则是本项目假设：
 * <ul>
 *   <li>只有多品波次（pickWay=2）且未拣完的波次能绑拣货车；一个波次只绑一辆车，一辆车同时只服务一个波次。</li>
 *   <li>波次里每个出库单占一格，gridSerial 按出库单顺序从 1 开始，gridNo = 车号-两位格号。</li>
 *   <li>扫一次 SKU 按格号从小到大分配到还缺这个 SKU 的格子；实际数量 = min(期望数, 拣货行剩余, 各格缺口合计)。</li>
 *   <li>拣货数量同步写回 {@link OutboundSampleStore} 的波次拣货行（与普通按件拣货共用状态，拣完后出库单进入待复核）。</li>
 *   <li>格子状态 state：EMPTY 未开始 / PICKING 部分 / FULL 已满。</li>
 * </ul>
 */
@Component
public class PickAndSortStore {

    public static class Grid {
        public final int gridSerial;
        public final String gridNo;
        public final String deliveryNo;
        /** sku → 需要数 */
        public final Map<String, Integer> need = new LinkedHashMap<>();
        /** sku → 已分到本格的数 */
        public final Map<String, Integer> picked = new LinkedHashMap<>();

        Grid(int gridSerial, String gridNo, String deliveryNo) {
            this.gridSerial = gridSerial;
            this.gridNo = gridNo;
            this.deliveryNo = deliveryNo;
        }

        public int needQty() {
            return need.values().stream().mapToInt(Integer::intValue).sum();
        }

        public int pickedQty() {
            return picked.values().stream().mapToInt(Integer::intValue).sum();
        }

        public int lack(String sku) {
            return need.getOrDefault(sku, 0) - picked.getOrDefault(sku, 0);
        }

        public String state() {
            int p = pickedQty();
            return p == 0 ? "EMPTY" : p >= needQty() ? "FULL" : "PICKING";
        }
    }

    public record Allocation(Grid grid, int quantity) {
    }

    public record ScanResult(Product product, PickLine line, Wave wave, int effectiveQty, List<Allocation> allocations) {
    }

    private final OutboundSampleStore outbound;
    private final StockStore stock;
    /** waveNo → 拣货车号 */
    private final Map<String, String> carts = new HashMap<>();
    /** waveNo → 格子 */
    private final Map<String, List<Grid>> grids = new HashMap<>();

    public PickAndSortStore(OutboundSampleStore outbound, StockStore stock) {
        this.outbound = outbound;
        this.stock = stock;
    }

    public synchronized void reset() {
        carts.clear();
        grids.clear();
    }

    public synchronized Wave bindCart(String waveNo, String pickCartNo) {
        if (pickCartNo == null || pickCartNo.isBlank()) {
            throw new BizException("请扫描拣货车");
        }
        String cart = pickCartNo.trim();
        Wave w = outbound.wave(waveNo);
        if (w.pickWay != 2) {
            throw new BizException("只有多品波次支持边拣边分");
        }
        if (w.pickStatus == 2) {
            throw new BizException("波次已拣货完成");
        }
        String bound = carts.get(waveNo);
        if (bound != null && !bound.equals(cart)) {
            throw new BizException("波次已绑定拣货车 " + bound);
        }
        carts.forEach((wv, c) -> {
            if (c.equals(cart) && !wv.equals(waveNo)) {
                throw new BizException("拣货车 " + cart + " 正在用于波次 " + wv);
            }
        });
        if (bound == null) {
            carts.put(waveNo, cart);
            List<Grid> list = new ArrayList<>();
            for (int i = 0; i < w.deliveryNos.size(); i++) {
                Delivery d = outbound.delivery(w.deliveryNos.get(i));
                Grid g = new Grid(i + 1, String.format("%s-%02d", cart, i + 1), d.deliveryNo);
                for (Pack p : d.packs) {
                    for (PackItem it : p.items) {
                        g.need.merge(it.sku, it.qty, Integer::sum);
                    }
                }
                list.add(g);
            }
            grids.put(waveNo, list);
        }
        return w;
    }

    public synchronized String cartOf(String waveNo) {
        return carts.get(waveNo);
    }

    public synchronized List<Grid> grids(String waveNo) {
        outbound.wave(waveNo);
        List<Grid> list = grids.get(waveNo);
        if (list == null) {
            throw new BizException("波次未绑定拣货车：" + waveNo);
        }
        return list;
    }

    public synchronized Grid grid(String waveNo, int gridSerial) {
        return grids(waveNo).stream().filter(g -> g.gridSerial == gridSerial).findFirst()
                .orElseThrow(() -> new BizException("格子不存在：" + gridSerial));
    }

    public synchronized ScanResult scan(String waveNo, String barcode, String cellNo, String currentSku, int desiredQty) {
        List<Grid> list = grids(waveNo);
        Product p = stock.findProduct(barcode == null ? "" : barcode.trim());
        if (p == null) {
            throw new BizException("条码不存在：" + barcode);
        }
        if (currentSku != null && !currentSku.isBlank() && !currentSku.equals(p.sku())) {
            throw new BizException("扫描的商品不是当前拣货的 SKU：" + currentSku);
        }
        Wave w = outbound.wave(waveNo);
        PickLine line = outbound.line(w, p.sku(), cellNo);
        int want = desiredQty <= 0 ? 1 : desiredQty;
        int lineLeft = line.planQty - line.pickQty;
        int gridLack = list.stream().mapToInt(g -> Math.max(0, g.lack(p.sku()))).sum();
        int effective = Math.min(want, Math.min(lineLeft, gridLack));
        if (effective <= 0) {
            throw new BizException("该 SKU 已拣完：" + p.sku());
        }
        outbound.pick(waveNo, p.sku(), line.cellNo, effective);
        List<Allocation> allocations = new ArrayList<>();
        int left = effective;
        for (Grid g : list) {
            int give = Math.min(left, Math.max(0, g.lack(p.sku())));
            if (give > 0) {
                g.picked.merge(p.sku(), give, Integer::sum);
                allocations.add(new Allocation(g, give));
                left -= give;
            }
            if (left == 0) {
                break;
            }
        }
        return new ScanResult(p, line, w, effective, allocations);
    }
}
