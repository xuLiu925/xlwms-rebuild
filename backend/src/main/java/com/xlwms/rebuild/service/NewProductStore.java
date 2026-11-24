package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * 新品维护（/pda/newProduct/**）：记录哪些 SKU 还没有仓库实测的尺寸重量，以及测量结果。内存、虚构。
 * 规则是本项目假设：初始 SKU-0012、SKU-0013 为待测量新品；测量后从待维护清单移除。
 */
@Service
public class NewProductStore {

    /** 实测结果，单位 cm / kg；Bs 后缀为英制（in / lb），由公制换算。 */
    public record Measure(double length, double width, double height, double weight) {
    }

    private final Set<String> pending = new LinkedHashSet<>();
    private final Map<String, Measure> measured = new LinkedHashMap<>();

    public NewProductStore() {
        reset();
    }

    public synchronized void reset() {
        pending.clear();
        measured.clear();
        pending.add("SKU-0012");
        pending.add("SKU-0013");
    }

    public synchronized boolean isNew(String sku) {
        return pending.contains(sku);
    }

    public synchronized Measure measure(String sku) {
        return measured.get(sku);
    }

    public synchronized void record(String sku, double l, double w, double h, double kg) {
        if (l <= 0 || w <= 0 || h <= 0 || kg <= 0 || l > 300 || w > 300 || h > 300 || kg > 500) {
            throw new BizException("SKU " + sku + " 的尺寸/重量超出范围（本地演示限制：0~300 cm，0~500 kg）");
        }
        measured.put(sku, new Measure(l, w, h, kg));
        pending.remove(sku);
    }
}
