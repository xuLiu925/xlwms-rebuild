package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 网页端入库作业（收货上架 /inbound/receivePutaway、上架 /inbound/shelf）的内存示例数据，重启或 reset 后恢复。
 * 数据全部虚构。入库单状态沿用网页端观察到的编码 10 在途 / 11 收货中 / 15 已收货 / 30 已上架，
 * 但“何时切换状态”的规则是本项目假设：有收货未收完=11，全部收完=15，全部上架=30。
 */
@Service
public class InboundWorkStore {

    public static final Map<Integer, String> ASN_STATUS = Map.of(10, "在途", 11, "收货中", 15, "已收货", 30, "已上架");

    public static class Line {
        public final String sku;
        public final String barcode;
        public final String productName;
        public final int planQty;
        public int receivedQty;
        public int shelvedQty;
        /** 上架记录：库位 → 数量 */
        public final Map<String, Integer> cells = new LinkedHashMap<>();

        Line(String sku, String barcode, String productName, int planQty) {
            this.sku = sku;
            this.barcode = barcode;
            this.productName = productName;
            this.planQty = planQty;
        }

        public int pendingShelfQty() {
            return receivedQty - shelvedQty;
        }
    }

    public static class Asn {
        public final String asnNo;
        public final String customerCode;
        public final String customerName;
        public final String whCode;
        public final List<Line> lines = new ArrayList<>();

        Asn(String asnNo, String customerCode, String customerName, String whCode) {
            this.asnNo = asnNo;
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.whCode = whCode;
        }

        public int status() {
            int plan = lines.stream().mapToInt(l -> l.planQty).sum();
            int rec = lines.stream().mapToInt(l -> l.receivedQty).sum();
            int shelf = lines.stream().mapToInt(l -> l.shelvedQty).sum();
            if (shelf == plan) {
                return 30;
            }
            if (rec == plan) {
                return 15;
            }
            return rec > 0 ? 11 : 10;
        }
    }

    private final Map<String, Asn> asns = new LinkedHashMap<>();

    public InboundWorkStore() {
        reset();
    }

    public synchronized void reset() {
        asns.clear();
        Asn a1 = asn("ASNW26110001", "C001", "示例客户1", "WH01");
        a1.lines.add(new Line("SKU-0001", "BC00010001", "示例收纳盒 1", 10));
        a1.lines.add(new Line("SKU-0002", "BC00010002", "示例数据线 2", 6));
        Asn a2 = asn("ASNW26110002", "C002", "示例客户2", "WH01");
        a2.lines.add(new Line("SKU-0004", "BC00010004", "示例水壶 4", 8));
        // 第 3 单已全部收货、部分上架，用来演示“上架”页
        Asn a3 = asn("ASNW26110003", "C003", "示例客户3", "WH01");
        Line l31 = new Line("SKU-0006", "BC00010006", "示例手机壳 6", 12);
        l31.receivedQty = 12;
        l31.shelvedQty = 5;
        l31.cells.put("01-01-01", 5);
        a3.lines.add(l31);
        Line l32 = new Line("SKU-0007", "BC00010007", "示例雨伞 7", 4);
        l32.receivedQty = 4;
        a3.lines.add(l32);
    }

    private Asn asn(String no, String cc, String cn, String wh) {
        Asn a = new Asn(no, cc, cn, wh);
        asns.put(no, a);
        return a;
    }

    public synchronized List<Asn> list() {
        return new ArrayList<>(asns.values());
    }

    public synchronized Asn get(String asnNo) {
        return Optional.ofNullable(asns.get(asnNo == null ? "" : asnNo.trim()))
                .orElseThrow(() -> new BizException("入库单不存在：" + asnNo));
    }

    /** 按条码或 SKU 收货。 */
    public synchronized Line receive(String asnNo, String code, int qty) {
        Asn a = get(asnNo);
        Line l = a.lines.stream().filter(x -> x.barcode.equals(code) || x.sku.equals(code)).findFirst()
                .orElseThrow(() -> new BizException("条码/SKU 不在该入库单中：" + code));
        if (qty <= 0) {
            throw new BizException("收货数量必须大于 0");
        }
        if (l.receivedQty + qty > l.planQty) {
            throw new BizException("超出预报数量，剩余可收 " + (l.planQty - l.receivedQty));
        }
        l.receivedQty += qty;
        return l;
    }

    public synchronized Line putaway(String asnNo, String sku, String cellCode, int qty) {
        Asn a = get(asnNo);
        Line l = a.lines.stream().filter(x -> x.sku.equals(sku)).findFirst()
                .orElseThrow(() -> new BizException("SKU 不在该入库单中：" + sku));
        if (qty <= 0) {
            throw new BizException("上架数量必须大于 0");
        }
        if (qty > l.pendingShelfQty()) {
            throw new BizException("超出待上架数量，剩余 " + l.pendingShelfQty());
        }
        l.shelvedQty += qty;
        l.cells.merge(cellCode, qty, Integer::sum);
        return l;
    }
}
