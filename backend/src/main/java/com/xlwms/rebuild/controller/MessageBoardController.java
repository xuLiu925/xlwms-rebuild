package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.MessageBoardStore;
import com.xlwms.rebuild.service.MessageBoardStore.Hit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 留言板：/gateway/wms/pda/messageBoard/{search,reply}。请求体 MessageBoardSearchReq / MessageBoardReplyReq、
 * 响应 MessageBoardSearchBean（含 redirectData）取自 APK；查找与校验规则见 {@link MessageBoardStore}。
 */
@RestController
@RequestMapping("/gateway/wms/pda/messageBoard")
public class MessageBoardController {

    public record MessageBoardSearchReq(String scanCode, String tenantCode, String whCode) {
    }

    /** 回复时附件只用到 fileKey；Appendix 其余字段忽略。 */
    public record AppendixRef(String fileKey) {
    }

    public record MessageBoardReplyReq(List<AppendixRef> appendixList, String content, String customerCode, Integer orderType,
                                       String whCode, String workNo) {
    }

    private final MessageBoardStore store;

    public MessageBoardController(MessageBoardStore store) {
        this.store = store;
    }

    @PostMapping("/search")
    public R<List<Map<String, Object>>> search(@RequestBody MessageBoardSearchReq req) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Hit h : store.search(req.scanCode())) {
            Map<String, Object> redirect = new LinkedHashMap<>();
            redirect.put("asnNo", h.asnNo());
            redirect.put("deliveryNo", h.deliveryNo());
            redirect.put("inboundType", h.orderType() == 1 ? 1 : null);
            redirect.put("outboundWay", h.orderType() == 2 ? 1 : null);
            redirect.put("sourceNo", h.sourceNo());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("customerCode", h.customerCode());
            m.put("customerName", h.customerName());
            m.put("orderNo", h.orderNo());
            m.put("orderNoPrefix", h.orderNo().substring(0, 2));
            m.put("redirectData", redirect);
            m.put("status", h.status());
            m.put("statusName", h.statusName());
            m.put("tenantCode", req.tenantCode() == null ? "T-DEMO" : req.tenantCode());
            m.put("trackNo", h.trackNo());
            m.put("whCode", req.whCode());
            out.add(m);
        }
        return R.ok(out);
    }

    @PostMapping("/reply")
    public R<Object> reply(@RequestBody MessageBoardReplyReq req) {
        List<String> keys = req.appendixList() == null ? List.of()
                : req.appendixList().stream().map(AppendixRef::fileKey).toList();
        store.reply(req.orderType() == null ? 0 : req.orderType(), req.workNo(), req.customerCode(), req.content(), keys);
        return R.ok(null);
    }
}
