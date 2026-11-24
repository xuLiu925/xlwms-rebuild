package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 批次 9：报表-人效看板 / 入库效率 / 出库效率（/report/{staffEfficiency,inbound,outbound}）。
 * 没有真实作业流水，数字由“日期 + 仓库 + 指标名”确定性推算，同样的查询条件总是得到同样的结果，只为演示。
 * 路径、请求体（startDate/endDate/whCode）、指标口径都是本项目占位，真实网页接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/report")
public class ReportController {

    static final int MAX_DAYS = 31;
    static final List<String> STAFF = List.of("示例员工1", "示例员工2", "示例员工3", "示例员工4", "示例员工5", "示例员工6");

    /** 查询区间：默认截至今天的最近 7 天；开始不能晚于结束，最多 31 天。 */
    record Range(LocalDate start, LocalDate end, String wh) {
        List<LocalDate> days() {
            List<LocalDate> out = new ArrayList<>();
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                out.add(d);
            }
            return out;
        }
    }

    static Range range(Map<String, Object> body) {
        LocalDate end = date(PageUtil.strParam(body, "endDate"), LocalDate.now());
        LocalDate start = date(PageUtil.strParam(body, "startDate"), end.minusDays(6));
        if (start.isAfter(end)) {
            throw new BizException("开始日期不能晚于结束日期");
        }
        if (ChronoUnit.DAYS.between(start, end) + 1 > MAX_DAYS) {
            throw new BizException("查询区间最多 " + MAX_DAYS + " 天");
        }
        String wh = PageUtil.strParam(body, "whCode");
        if (!wh.isEmpty() && !wh.equals("WH01") && !wh.equals("WH02")) {
            throw new BizException("未知的仓库：" + wh);
        }
        return new Range(start, end, wh);
    }

    private static LocalDate date(String s, LocalDate def) {
        if (s.isEmpty()) {
            return def;
        }
        try {
            return LocalDate.parse(s.length() > 10 ? s.substring(0, 10) : s);
        } catch (DateTimeParseException e) {
            throw new BizException("日期格式应为 yyyy-MM-dd：" + s);
        }
    }

    /** 确定性伪随机：返回 [lo, hi] 之间的整数。不选仓库时取两个仓库之和。 */
    static int gen(Range r, LocalDate d, String metric, int lo, int hi) {
        if (r.wh().isEmpty()) {
            return gen1("WH01", d, metric, lo, hi) + gen1("WH02", d, metric, lo, hi);
        }
        return gen1(r.wh(), d, metric, lo, hi);
    }

    private static int gen1(String wh, LocalDate d, String metric, int lo, int hi) {
        int h = (wh + "|" + d + "|" + metric).hashCode() * 0x9E3779B1;
        return lo + Math.floorMod(h ^ (h >>> 15), hi - lo + 1);
    }

    private static BigDecimal ratio(long a, long b, int scale) {
        return b == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(a).divide(BigDecimal.valueOf(b), scale, RoundingMode.HALF_UP);
    }

    private static Map<String, Object> card(String label, Object value, String unit) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", label);
        m.put("value", value);
        m.put("unit", unit);
        return m;
    }

    private static Map<String, Object> result(Range r, List<Map<String, Object>> cards, List<Map<String, Object>> series,
                                              List<Map<String, Object>> rows) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("startDate", r.start().toString());
        m.put("endDate", r.end().toString());
        m.put("cards", cards);
        m.put("series", series);
        m.put("rows", rows);
        return m;
    }

    /** 人效看板：每人的收货、上架、拣货、复核件数与工时；series 为每天的总件数。 */
    @PostMapping("/staffEfficiency")
    public R<Map<String, Object>> staffEfficiency(@RequestBody(required = false) Map<String, Object> body) {
        Range r = range(body);
        List<Map<String, Object>> rows = new ArrayList<>();
        long totalQty = 0;
        long totalMinutes = 0;
        for (String staff : STAFF) {
            int receive = 0;
            int putaway = 0;
            int pick = 0;
            int review = 0;
            int minutes = 0;
            for (LocalDate d : r.days()) {
                receive += gen(r, d, staff + "收货", 0, 120);
                putaway += gen(r, d, staff + "上架", 0, 100);
                pick += gen(r, d, staff + "拣货", 20, 260);
                review += gen(r, d, staff + "复核", 10, 180);
                minutes += gen(r, d, staff + "工时", 360, 540);
            }
            int qty = receive + putaway + pick + review;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("staffName", staff);
            m.put("receiveQty", receive);
            m.put("putawayQty", putaway);
            m.put("pickQty", pick);
            m.put("reviewQty", review);
            m.put("totalQty", qty);
            m.put("workHours", ratio(minutes, 60, 1));
            m.put("qtyPerHour", ratio(qty * 60L, minutes, 1));
            rows.add(m);
            totalQty += qty;
            totalMinutes += minutes;
        }
        rows.sort((a, b) -> ((BigDecimal) b.get("qtyPerHour")).compareTo((BigDecimal) a.get("qtyPerHour")));
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).put("rank", i + 1);
        }
        List<Map<String, Object>> series = new ArrayList<>();
        for (LocalDate d : r.days()) {
            int sum = 0;
            for (String staff : STAFF) {
                sum += gen(r, d, staff + "收货", 0, 120) + gen(r, d, staff + "上架", 0, 100)
                        + gen(r, d, staff + "拣货", 20, 260) + gen(r, d, staff + "复核", 10, 180);
            }
            series.add(Map.of("date", d.toString(), "value", sum));
        }
        List<Map<String, Object>> cards = List.of(
                card("作业人数", STAFF.size(), "人"),
                card("作业总件数", totalQty, "件"),
                card("总工时", ratio(totalMinutes, 60, 1), "小时"),
                card("平均人效", ratio(totalQty * 60, totalMinutes, 1), "件/小时"));
        return R.ok(result(r, cards, series, rows));
    }

    /** 入库效率：每天的到货单数、收货件数、上架件数、平均上架时长、24 小时内上架率。 */
    @PostMapping("/inbound")
    public R<Map<String, Object>> inbound(@RequestBody(required = false) Map<String, Object> body) {
        Range r = range(body);
        List<Map<String, Object>> rows = new ArrayList<>();
        long orders = 0;
        long receive = 0;
        long putaway = 0;
        long onTime = 0;
        long hoursX10 = 0;
        for (LocalDate d : r.days()) {
            int o = gen(r, d, "到货单", 2, 12);
            int rq = gen(r, d, "收货件数", 200, 1500);
            int pq = Math.min(rq, rq - gen(r, d, "未上架", 0, 150));
            int ot = o - gen(r, d, "超时单", 0, 2) * (o > 2 ? 1 : 0);
            int hx10 = gen(r, d, "上架时长", 30, 300);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", d.toString());
            m.put("arrivalOrders", o);
            m.put("receiveQty", rq);
            m.put("putawayQty", pq);
            m.put("avgPutawayHours", ratio(hx10, 10, 1));
            m.put("onTimeRate", ratio(ot * 100L, o, 1));
            rows.add(m);
            orders += o;
            receive += rq;
            putaway += pq;
            onTime += ot;
            hoursX10 += hx10;
        }
        List<Map<String, Object>> series = rows.stream().map(m -> Map.of("date", m.get("date"), "value", m.get("receiveQty"))).toList();
        List<Map<String, Object>> cards = List.of(
                card("到货单数", orders, "单"),
                card("收货件数", receive, "件"),
                card("上架件数", putaway, "件"),
                card("24 小时上架率", ratio(onTime * 100, orders, 1), "%"),
                card("平均上架时长", ratio(hoursX10, 10L * rows.size(), 1), "小时"));
        return R.ok(result(r, cards, series, rows));
    }

    /** 出库效率：每天的订单数、拣货件数、出库单数、平均出库时长、当日出库率。 */
    @PostMapping("/outbound")
    public R<Map<String, Object>> outbound(@RequestBody(required = false) Map<String, Object> body) {
        Range r = range(body);
        List<Map<String, Object>> rows = new ArrayList<>();
        long orders = 0;
        long pick = 0;
        long shipped = 0;
        long hoursX10 = 0;
        for (LocalDate d : r.days()) {
            int o = gen(r, d, "订单", 80, 400);
            int s = o - gen(r, d, "未出库", 0, 30);
            int pq = o + gen(r, d, "多件", 20, 300);
            int hx10 = gen(r, d, "出库时长", 20, 200);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", d.toString());
            m.put("orders", o);
            m.put("pickQty", pq);
            m.put("shippedOrders", s);
            m.put("avgShipHours", ratio(hx10, 10, 1));
            m.put("sameDayRate", ratio(s * 100L, o, 1));
            rows.add(m);
            orders += o;
            pick += pq;
            shipped += s;
            hoursX10 += hx10;
        }
        List<Map<String, Object>> series = rows.stream().map(m -> Map.of("date", m.get("date"), "value", m.get("orders"))).toList();
        List<Map<String, Object>> cards = List.of(
                card("订单数", orders, "单"),
                card("拣货件数", pick, "件"),
                card("出库单数", shipped, "单"),
                card("当日出库率", ratio(shipped * 100, orders, 1), "%"),
                card("平均出库时长", ratio(hoursX10, 10L * rows.size(), 1), "小时"));
        return R.ok(result(r, cards, series, rows));
    }
}
