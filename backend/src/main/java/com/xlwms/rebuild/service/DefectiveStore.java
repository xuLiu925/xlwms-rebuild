package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 批次 9：网页端“库内-次品处理”（/stock/defective）。直接操作 {@link StockStore} 里品质为次品（2）的库存，
 * 处理记录只存内存，重启或 reset 后清空。规则是本项目假设：
 * <ul>
 *   <li>转良品：从次品库存扣减，加到目标库位的良品库存；目标库位必须存在、未锁定</li>
 *   <li>销毁：从次品库存扣减，必须填处理说明</li>
 *   <li>数量不能超过可用数（库存 - 冻结）；先校验全部条件再改库存</li>
 * </ul>
 */
@Service
public class DefectiveStore {

    public record Record(long id, String type, String cellNo, String targetCellNo, String sku, String boxType, int qty,
                         String remark, String time) {
    }

    private final StockStore stock;
    private final List<Record> records = new ArrayList<>();
    private long seq;

    public DefectiveStore(StockStore stock) {
        this.stock = stock;
    }

    public synchronized void reset() {
        records.clear();
        seq = 0;
    }

    public synchronized List<Record> records() {
        List<Record> out = new ArrayList<>(records);
        java.util.Collections.reverse(out);
        return out;
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private void checkQty(String cellNo, String sku, String boxType, int qty) {
        if (qty <= 0) {
            throw new BizException("处理数量必须大于 0");
        }
        int avail = stock.availOf(cellNo, sku, boxType, 2);
        if (qty > avail) {
            throw new BizException("库位 " + cellNo + " 的次品可用数不足，可用 " + avail);
        }
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim();
    }

    public synchronized Record toGood(String cellNo, String sku, String boxType, int qty, String targetCellNo, String remark) {
        cellNo = norm(cellNo);
        sku = norm(sku);
        boxType = norm(boxType);
        String target = targetCellNo == null || targetCellNo.isBlank() ? cellNo : targetCellNo.trim();
        checkQty(cellNo, sku, boxType, qty);
        stock.requireCell(target, true);
        stock.deduct(cellNo, sku, boxType, 2, qty);
        stock.putIn(target, sku, boxType, 1, qty);
        return log("转良品", cellNo, target, sku, boxType, qty, remark);
    }

    public synchronized Record destroy(String cellNo, String sku, String boxType, int qty, String remark) {
        if (remark == null || remark.isBlank()) {
            throw new BizException("销毁必须填写处理说明");
        }
        cellNo = norm(cellNo);
        sku = norm(sku);
        boxType = norm(boxType);
        checkQty(cellNo, sku, boxType, qty);
        stock.deduct(cellNo, sku, boxType, 2, qty);
        return log("销毁", cellNo, "", sku, boxType, qty, remark);
    }

    private Record log(String type, String cellNo, String target, String sku, String boxType, int qty, String remark) {
        Record r = new Record(++seq, type, cellNo, target, sku, boxType, qty, remark == null ? "" : remark.trim(), now());
        records.add(r);
        return r;
    }
}
