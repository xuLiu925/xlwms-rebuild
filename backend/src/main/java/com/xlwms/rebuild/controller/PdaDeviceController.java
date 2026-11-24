package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.DeviceStore;
import com.xlwms.rebuild.service.DeviceStore.Line;
import com.xlwms.rebuild.service.DeviceStore.Task;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PDA 设备任务 9 个接口：/gateway/wms/pda/device/{stock/locate,bill/prepare,cell/prepare,wave/prepare}
 * 与 /gateway/wms/device/{config/tenantEnabled,tasks/detail,tasks/wave,tasks/resolve,tasks/wave/subOrder/resolve}。
 * 路径、请求体与响应字段取自 APK（Device*Req / Device*Resp / DeviceTaskLineResp）；模拟规则见 {@link DeviceStore}。
 */
@RestController
public class PdaDeviceController {

    public record SkuCellPair(String cellNo, String sku) {
    }

    /** DeviceStockLocateReq */
    public record DeviceStockLocateReq(String bizType, List<SkuCellPair> skuCellPairs, String whCode) {
    }

    /** DeviceBillPrepareReq / DeviceWavePrepareReq / DeviceTaskDetailReq / DeviceResolveReq 字段相同 */
    public record BizReq(String bizNo, String bizType, String whCode) {
    }

    /** DeviceCellPrepareReq */
    public record DeviceCellPrepareReq(List<String> cellNos, String whCode) {
    }

    /** DeviceWaveSubOrderResolveReq */
    public record DeviceWaveSubOrderResolveReq(String outOrderNo, int subSeqNo, String waveNo, String whCode) {
    }

    private final DeviceStore store;

    public PdaDeviceController(DeviceStore store) {
        this.store = store;
    }

    /** Device*PrepareResp / DeviceStockLocateResp；withBiz 时带 bizNo/bizType/whCode（cell/prepare 与 stock/locate 的响应有这三个字段）。 */
    private static Map<String, Object> prepareResp(Task t, boolean withBiz) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (withBiz) {
            m.put("bizNo", t.bizNo);
            m.put("bizType", t.bizType);
            m.put("whCode", t.whCode);
        }
        m.put("canProceed", t.canProceed());
        m.put("deviceStatus", t.deviceStatus());
        m.put("exceptionMsg", t.exceptionMsg());
        m.put("latestTaskId", t.lines.isEmpty() ? null : t.id);
        m.put("redirectToStatusPage", !t.canProceed());
        return m;
    }

    /** DeviceTaskDetailResp */
    private static Map<String, Object> detailResp(Task t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("bizNo", t.bizNo);
        m.put("bizStatus", t.canProceed() ? "READY" : "WAITING");
        m.put("bizType", t.bizType);
        m.put("deviceStatus", t.deviceStatus());
        m.put("exceptionMsg", t.exceptionMsg());
        m.put("latestTaskId", t.lines.isEmpty() ? null : t.id);
        List<Map<String, Object>> lines = new ArrayList<>();
        for (Line l : t.lines) {
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("callbackTime", l.callbackTime == 0 ? null : l.callbackTime);
            x.put("deviceStatus", l.done() ? "COMPLETED" : "EXCEPTION");
            x.put("exceptionMsg", l.exceptionMsg);
            x.put("exceptionType", l.exceptionType);
            x.put("outOrderNo", l.outOrderNo);
            x.put("processedQty", l.processedQty);
            x.put("requestedQty", l.requestedQty);
            x.put("resolved", l.resolved);
            x.put("sentTime", l.sentTime);
            x.put("seqNo", l.seqNo);
            x.put("sku", l.sku);
            x.put("subSeqNo", l.subSeqNo);
            x.put("success", l.success);
            x.put("taskId", l.taskId);
            x.put("taskStatus", l.taskStatus());
            lines.add(x);
        }
        m.put("lines", lines);
        return m;
    }

    @GetMapping("/gateway/wms/device/config/tenantEnabled")
    public R<Map<String, Object>> tenantEnabled(@RequestParam(required = false) String whCode) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("enabled", true);
        m.put("specialCellNos", DeviceStore.DEVICE_CELLS);
        m.put("vendor", DeviceStore.VENDOR);
        return R.ok(m);
    }

    @PostMapping("/gateway/wms/pda/device/stock/locate")
    public R<Map<String, Object>> locate(@RequestBody DeviceStockLocateReq req) {
        List<String[]> pairs = req.skuCellPairs() == null ? List.of()
                : req.skuCellPairs().stream().map(p -> new String[] {p.sku(), p.cellNo()}).toList();
        return R.ok(prepareResp(store.locateStock(req.bizType(), pairs, req.whCode()), true));
    }

    @PostMapping("/gateway/wms/pda/device/cell/prepare")
    public R<Map<String, Object>> prepareCell(@RequestBody DeviceCellPrepareReq req) {
        return R.ok(prepareResp(store.prepareCells(req.cellNos(), req.whCode()), true));
    }

    @PostMapping("/gateway/wms/pda/device/bill/prepare")
    public R<Map<String, Object>> prepareBill(@RequestBody BizReq req) {
        return R.ok(prepareResp(store.prepareBill(req.bizType(), req.bizNo(), req.whCode()), false));
    }

    @PostMapping("/gateway/wms/pda/device/wave/prepare")
    public R<Map<String, Object>> prepareWave(@RequestBody BizReq req) {
        return R.ok(prepareResp(store.prepareWave(req.bizType(), req.bizNo(), req.whCode()), false));
    }

    @PostMapping("/gateway/wms/device/tasks/detail")
    public R<Map<String, Object>> detail(@RequestBody BizReq req) {
        return R.ok(detailResp(store.detail(req.bizType(), req.bizNo())));
    }

    @GetMapping("/gateway/wms/device/tasks/wave")
    public R<Map<String, Object>> waveDetail(@RequestParam String waveNo, @RequestParam(required = false) String whCode) {
        return R.ok(detailResp(store.detail("WAVE", waveNo)));
    }

    @PostMapping("/gateway/wms/device/tasks/resolve")
    public R<Object> resolve(@RequestBody BizReq req) {
        store.resolve(req.bizType(), req.bizNo());
        return R.ok(null);
    }

    @PostMapping("/gateway/wms/device/tasks/wave/subOrder/resolve")
    public R<Object> resolveSubOrder(@RequestBody DeviceWaveSubOrderResolveReq req) {
        store.resolveWaveLine(req.waveNo(), req.outOrderNo(), req.subSeqNo());
        return R.ok(null);
    }
}
