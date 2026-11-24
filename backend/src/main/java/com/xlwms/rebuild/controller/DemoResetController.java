package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.AppendixStore;
import com.xlwms.rebuild.service.DeviceStore;
import com.xlwms.rebuild.service.FbaReturnStore;
import com.xlwms.rebuild.service.FreightStore;
import com.xlwms.rebuild.service.InboundWorkStore;
import com.xlwms.rebuild.service.InventoryCheckStore;
import com.xlwms.rebuild.service.NewProductStore;
import com.xlwms.rebuild.service.MessageBoardStore;
import com.xlwms.rebuild.service.OutboundSampleStore;
import com.xlwms.rebuild.service.PickAndSortStore;
import com.xlwms.rebuild.service.ReceiptStore;
import com.xlwms.rebuild.service.ReturnStore;
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
    private final ReceiptStore receipts;
    private final NewProductStore newProducts;
    private final ReturnStore returns;
    private final FbaReturnStore fbaReturns;
    private final DeviceStore devices;
    private final PickAndSortStore pickAndSort;
    private final AppendixStore appendix;
    private final MessageBoardStore messageBoard;

    public DemoResetController(OutboundSampleStore outbound, InboundWorkStore inbound, StockStore stock,
                               InventoryCheckStore inventoryCheck, FreightStore freight,
                               ReceiptStore receipts, NewProductStore newProducts,
                               ReturnStore returns, FbaReturnStore fbaReturns,
                               DeviceStore devices, PickAndSortStore pickAndSort,
                               AppendixStore appendix, MessageBoardStore messageBoard) {
        this.outbound = outbound;
        this.inbound = inbound;
        this.stock = stock;
        this.inventoryCheck = inventoryCheck;
        this.freight = freight;
        this.receipts = receipts;
        this.newProducts = newProducts;
        this.returns = returns;
        this.fbaReturns = fbaReturns;
        this.devices = devices;
        this.pickAndSort = pickAndSort;
        this.appendix = appendix;
        this.messageBoard = messageBoard;
    }

    @PostMapping("/gateway/wms/demo/reset")
    public R<Boolean> resetAll() {
        stock.reset();
        inventoryCheck.reset();
        freight.reset();
        // 收货重置会顺带清空上架单
        receipts.reset();
        newProducts.reset();
        returns.reset();
        fbaReturns.reset();
        outbound.reset();
        inbound.reset();
        devices.reset();
        pickAndSort.reset();
        appendix.reset();
        messageBoard.reset();
        return R.ok(true);
    }
}
