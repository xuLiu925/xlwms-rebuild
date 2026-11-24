package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.FreightStore;
import com.xlwms.rebuild.service.InboundWorkStore;
import com.xlwms.rebuild.service.InventoryCheckStore;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.StockStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** 本地演示专用：把所有内存示例数据恢复到初始状态（真实系统没有这个接口）。 */
@RestController
public class DemoResetController {

    private final OutboundSampleStore outbound;
    private final InboundWorkStore inbound;
    private final StockStore stock;
    private final InventoryCheckStore inventoryCheck;
    private final FreightStore freight;

    public DemoResetController(OutboundSampleStore outbound, InboundWorkStore inbound, StockStore stock,
                               InventoryCheckStore inventoryCheck, FreightStore freight) {
        this.outbound = outbound;
        this.inbound = inbound;
        this.stock = stock;
        this.inventoryCheck = inventoryCheck;
        this.freight = freight;
    }

    @PostMapping("/gateway/wms/demo/reset")
    public R<Boolean> resetAll() {
        stock.reset();
        inventoryCheck.reset();
        freight.reset();
        outbound.reset();
        inbound.reset();
        return R.ok(true);
    }
}
