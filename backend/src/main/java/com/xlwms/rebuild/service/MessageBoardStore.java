package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.service.OutboundSampleStore.Delivery;
import com.xlwms.rebuild.service.ReceiptStore.Asn;
import com.xlwms.rebuild.service.ReturnStore.Order;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 留言板（拍照留言）。接口与字段取自 APK 的 MessageBoard*；规则是本项目假设：
 * <ul>
 *   <li>search 按扫描内容在入库单（单号/来源单号/跟踪号）、出库单（单号/来源单号/快递单号）、退件单（单号/快递单号/原出库单号）中查找。</li>
 *   <li>orderType：1 入库单 / 2 出库单 / 3 退件单（占位编码）。</li>
 *   <li>reply 要求内容非空且不超过 500 字，单据必须存在、客户必须一致，附件 fileKey 必须是已上传的附件。</li>
 * </ul>
 */
@Component
public class MessageBoardStore {

    public static final Map<Integer, String> ORDER_TYPE = Map.of(1, "入库单", 2, "出库单", 3, "退件单");

    /** 找到的单据：orderType、单号、客户、状态、跳转信息。 */
    public record Hit(int orderType, String orderNo, String customerCode, String customerName, String trackNo, int status,
                      String statusName, String asnNo, String deliveryNo, String sourceNo) {
    }

    public record Message(long id, int orderType, String workNo, String customerCode, String content, List<String> fileKeys,
                          String createTime) {
    }

    private final ReceiptStore receipts;
    private final OutboundSampleStore outbound;
    private final ReturnStore returns;
    private final AppendixStore appendix;
    private final List<Message> messages = new ArrayList<>();
    private long seq;

    public MessageBoardStore(ReceiptStore receipts, OutboundSampleStore outbound, ReturnStore returns, AppendixStore appendix) {
        this.receipts = receipts;
        this.outbound = outbound;
        this.returns = returns;
        this.appendix = appendix;
    }

    public synchronized void reset() {
        messages.clear();
        seq = 0;
    }

    private static boolean eq(String a, String code) {
        return a != null && a.equalsIgnoreCase(code);
    }

    public synchronized List<Hit> search(String scanCode) {
        if (scanCode == null || scanCode.isBlank()) {
            throw new BizException("请扫描单号");
        }
        String c = scanCode.trim();
        List<Hit> hits = new ArrayList<>();
        for (Asn a : receipts.list()) {
            if (eq(a.asnNo, c) || eq(a.sourceNo, c) || eq(a.trackingNo, c)) {
                hits.add(new Hit(1, a.asnNo, a.customerCode, a.customerName, a.trackingNo, a.status(),
                        ReceiptStore.STATUS.get(a.status()), a.asnNo, null, a.sourceNo));
            }
        }
        for (var w : outbound.waves()) {
            for (String no : w.deliveryNos) {
                Delivery d = outbound.delivery(no);
                if (eq(d.deliveryNo, c) || eq(d.sourceNo, c) || eq(d.expressNo, c)) {
                    hits.add(new Hit(2, d.deliveryNo, d.customerCode, d.customerName, d.expressNo, d.status,
                            OutboundSampleStore.DELIVERY_STATUS.get(d.status), null, d.deliveryNo, d.sourceNo));
                }
            }
        }
        for (int st : ReturnStore.STATUS.keySet()) {
            for (Order o : returns.list(st)) {
                if (eq(o.orderNo, c) || eq(o.expressNo, c) || eq(o.deliveryNo, c)) {
                    hits.add(new Hit(3, o.orderNo, o.customerCode, o.customerName, o.expressNo, o.status,
                            ReturnStore.STATUS.get(o.status), null, o.deliveryNo, o.deliveryNo));
                }
            }
        }
        return hits;
    }

    private Hit find(int orderType, String workNo) {
        return search(workNo).stream().filter(h -> h.orderType() == orderType && h.orderNo().equals(workNo.trim())).findFirst()
                .orElseThrow(() -> new BizException("未找到" + ORDER_TYPE.getOrDefault(orderType, "单据") + "：" + workNo));
    }

    public synchronized Message reply(int orderType, String workNo, String customerCode, String content, List<String> fileKeys) {
        if (!ORDER_TYPE.containsKey(orderType)) {
            throw new BizException("不支持的单据类型：" + orderType);
        }
        if (content == null || content.isBlank()) {
            throw new BizException("请输入留言内容");
        }
        if (content.length() > 500) {
            throw new BizException("留言内容不能超过 500 字");
        }
        if (workNo == null || workNo.isBlank()) {
            throw new BizException("缺少单号");
        }
        Hit h = find(orderType, workNo);
        if (customerCode != null && !customerCode.isBlank() && !customerCode.equals(h.customerCode())) {
            throw new BizException("客户与单据不一致");
        }
        for (String k : fileKeys) {
            if (appendix.get(k) == null) {
                throw new BizException("附件不存在：" + k);
            }
        }
        Message m = new Message(++seq, orderType, h.orderNo(), h.customerCode(), content.trim(), List.copyOf(fileKeys),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        messages.add(m);
        return m;
    }

    public synchronized List<Message> messages(String workNo) {
        return messages.stream().filter(m -> m.workNo().equals(workNo)).toList();
    }
}
