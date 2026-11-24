package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 分区计算（工具箱的分区测试与运费试算共用）。规则完全虚构，仅用于本地演示：
 * 取起点与终点邮编前三位之差的绝对值，按承运商的步长折算成 2~8 区。
 */
@Service
public class ZoneCalculator {

    /** 仓库对应的虚构发货邮编。 */
    public static final Map<String, String> ORIGIN_ZIP = Map.of("WH01", "91000", "WH02", "08000");
    /** 承运商 → 每跨一区的前三位邮编差（虚构）。 */
    public static final Map<String, Integer> CARRIER_STEP = Map.of("示例承运商A", 150, "示例承运商B", 120, "示例承运商C", 180);

    public record Zone(String carrier, String originZip, String destZip, int zone, int diff, int step) {
    }

    public static String checkZip(String zip, String label) {
        String z = zip == null ? "" : zip.trim();
        if (!z.matches("\\d{5}")) {
            throw new BizException(label + "需为 5 位数字（本地演示仅支持 US 邮编格式）");
        }
        return z;
    }

    public Zone zone(String carrier, String originZip, String destZip) {
        Integer step = CARRIER_STEP.get(carrier);
        if (step == null) {
            throw new BizException("未知承运商：" + carrier);
        }
        String o = checkZip(originZip, "起始邮编");
        String d = checkZip(destZip, "目的邮编");
        int diff = Math.abs(Integer.parseInt(o.substring(0, 3)) - Integer.parseInt(d.substring(0, 3)));
        int zone = Math.min(8, 2 + diff / step);
        return new Zone(carrier, o, d, zone, diff, step);
    }
}
