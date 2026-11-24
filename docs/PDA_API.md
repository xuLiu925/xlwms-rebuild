# PDA 接口清单（从 dex 注解静态还原）

来源：`com.lingxing.wms.app.network.ApiService` 的 Retrofit 注解，共 175 个方法。
`read?` 仅按方法名保守推断为查询；其余一律视为**可能写入生产数据**。GET 不等于无副作用，推断不是证明。

| 类型 | HTTP | 路径 | 方法 | 参数 |
|---|---|---|---|---|
| write/unknown | POST | `/gateway/common/pic/upload` | uploadPic | HashMap, List |
| read? | GET | `/gateway/wms/auth/getUserInfo` | getUserInfo |  |
| write/unknown | POST | `/gateway/wms/auth/login` | login | LoginReqBean |
| write/unknown | POST | `/gateway/wms/blMoveStock/boxMoveStock` | boxMoveStock | BoxMoveStockReq |
| write/unknown | POST | `/gateway/wms/blReceipt/measureBoxType` | measureBoxType | MeasureBoxType |
| read? | GET | `/gateway/wms/device/config/tenantEnabled` | getDeviceTenantEnabled | String |
| read? | POST | `/gateway/wms/device/tasks/detail` | getDeviceTaskDetail | DeviceTaskDetailReq |
| write/unknown | POST | `/gateway/wms/device/tasks/resolve` | resolveDeviceTask | DeviceResolveReq |
| read? | GET | `/gateway/wms/device/tasks/wave` | getDeviceWaveTaskDetail | String, String |
| write/unknown | POST | `/gateway/wms/device/tasks/wave/subOrder/resolve` | resolveDeviceWaveSubOrder | DeviceWaveSubOrderResolveReq |
| read? | GET | `/gateway/wms/pda/appendix/appendixTypeList` | appendixTypeList | String, String |
| write/unknown | GET | `/gateway/wms/pda/appendix/remove` | removeFile | String, String, String, String, String, String, String |
| write/unknown | POST | `/gateway/wms/pda/appendix/upload` | uploadFile | HashMap, List |
| write/unknown | POST | `/gateway/wms/pda/appendix/uploadOther` | uploadLogFile | List |
| write/unknown | GET | `/gateway/wms/pda/blReturnIn/addFnSku` | addFnSku | String, String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/blReturnIn/claim/scanBarcode` | scanClaimBarcode | String, String, String |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/confirmSign` | confirmSign | FbaConfirmSignReq |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/createClaim` | createClaim | CreateClaimReq |
| read? | GET | `/gateway/wms/pda/blReturnIn/customerList` | customerList | String |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/judgeCell` | judgeCell | JudgeCallReq |
| read? | GET | `/gateway/wms/pda/blReturnIn/listInventoryFnSku` | listInventoryFnSku | String, String, String, String, String |
| read? | GET | `/gateway/wms/pda/blReturnIn/listPutAwayFnSku` | listPutAwayFnSku | String, String, String, String, String, String |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/pick/finishTaskOrder` | finishTaskOrder | FinishPickTaskReq |
| read? | GET | `/gateway/wms/pda/blReturnIn/pick/listTaskOrder` | listTaskOrder | String |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/pick/postTaskOrder` | postTaskOrder | PostTaskOrderReq |
| read? | GET | `/gateway/wms/pda/blReturnIn/pick/selectTaskOrder` | selectTaskOrder | String, String |
| write/unknown | GET | `/gateway/wms/pda/blReturnIn/pick/verifyBarcode` | verifyBarcode | String, String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/blReturnIn/pick/verifyCellNo` | verifyCellNo | String, String, String |
| write/unknown | GET | `/gateway/wms/pda/blReturnIn/scanCell` | scanCell | String, String |
| write/unknown | GET | `/gateway/wms/pda/blReturnIn/scanFnsku` | scanFnsku | String, String, String |
| read? | GET | `/gateway/wms/pda/blReturnIn/selectProductQuality` | selectProductQuality | String, String, String, String, String, String, String |
| read? | GET | `/gateway/wms/pda/blReturnIn/selectPutAway` | selectPutAway | String, String |
| read? | GET | `/gateway/wms/pda/blReturnIn/selectPutAwayList` | selectPutAwayList | String |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/signAndInventory` | signAndInventory | InventoryPreCheckReq |
| read? | GET | `/gateway/wms/pda/blReturnIn/signDetail` | signDetail | String, String, String |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/updateStatusInventory` | updateStatusInventory | UpdateStatusInventoryReq |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/updateStatusShelf` | fbaUpdateStatusShelf | FbaReturnShelvesReq |
| write/unknown | POST | `/gateway/wms/pda/blReturnIn/updateStatusSign` | updateStatusSign | UpdateStatusSign |
| write/unknown | POST | `/gateway/wms/pda/delivery/bulkConfirmDelivery` | deliverConfirm | BulkDeliveryConfirmReq |
| write/unknown | POST | `/gateway/wms/pda/delivery/confirmDelivery` | deliverConfirm | DeliveryConfirmReq |
| write/unknown | GET | `/gateway/wms/pda/delivery/getConfirmInfo` | getConfirmInfo | String, String, I |
| write/unknown | POST | `/gateway/wms/pda/delivery/review/bulkConfirmDelivery` | bulkConfirmDelivery | ReviewBulkConfirmDeliveryReq |
| read? | GET | `/gateway/wms/pda/delivery/review/getOutboundDetail` | getOutboundDetail | String, String, String, String |
| read? | GET | `/gateway/wms/pda/delivery/review/getSysBaseParam` | getSysBaseParam | String |
| write/unknown | GET | `/gateway/wms/pda/delivery/review/scanOutbound` | scanOutbound | String, String |
| write/unknown | GET | `/gateway/wms/pda/delivery/review/scanProductSn` | reviewScanProductSn | String, String, String, String, String, String, List |
| write/unknown | POST | `/gateway/wms/pda/device/bill/prepare` | prepareDeviceBill | DeviceBillPrepareReq |
| write/unknown | POST | `/gateway/wms/pda/device/cell/prepare` | prepareDeviceCell | DeviceCellPrepareReq |
| write/unknown | POST | `/gateway/wms/pda/device/stock/locate` | locateDeviceStock | DeviceStockLocateReq |
| write/unknown | POST | `/gateway/wms/pda/device/wave/prepare` | prepareDeviceWave | DeviceWavePrepareReq |
| read? | GET | `/gateway/wms/pda/home` | getHomeInfo | String |
| write/unknown | POST | `/gateway/wms/pda/inventoryCheck/complete` | completeInventoryCheck | InventoryCheckOrderReq |
| read? | POST | `/gateway/wms/pda/inventoryCheck/list` | inventoryCheckList | InventoryCheckListReq |
| write/unknown | POST | `/gateway/wms/pda/inventoryCheck/overview` | inventoryCheckOverview | InventoryCheckOrderReq |
| write/unknown | POST | `/gateway/wms/pda/inventoryCheck/scanSku` | scanInventoryCheckSku | InventoryCheckScanReq |
| write/unknown | POST | `/gateway/wms/pda/inventoryCheck/start` | startInventoryCheck | InventoryCheckOrderReq |
| write/unknown | POST | `/gateway/wms/pda/inventoryCheck/submitCell` | submitInventoryCheckCell | InventoryCheckCellSubmitReq |
| read? | GET | `/gateway/wms/pda/menu/list` | getMenuList | String |
| write/unknown | POST | `/gateway/wms/pda/messageBoard/reply` | replyMessageBoard | MessageBoardReplyReq |
| read? | POST | `/gateway/wms/pda/messageBoard/search` | searchMessageBoard | MessageBoardSearchReq |
| read? | POST | `/gateway/wms/pda/newProduct/getNeedMaintain` | getNeedMaintain | AllNeedNewProductMaintainReq |
| read? | POST | `/gateway/wms/pda/newProduct/getNeedNewProductMaintain` | getNeedNewProductMaintain | NeedNewProductMaintainReq |
| read? | POST | `/gateway/wms/pda/newProduct/listNeedNewProductMaintain` | listNeedNewProductMaintain | List |
| write/unknown | POST | `/gateway/wms/pda/newProduct/receiptMeasure` | receiptMeasure | List |
| write/unknown | POST | `/gateway/wms/pda/newProduct/returnInNeedMaintain` | returnInNeedMaintain | SkuFnSkuListBean |
| write/unknown | POST | `/gateway/wms/pda/pickAndSort/bindCart` | pickAndSortBindCart | PickAndSortBindCartReq |
| read? | GET | `/gateway/wms/pda/pickAndSort/gridDetail` | pickAndSortGridDetail | String, String, I |
| write/unknown | GET | `/gateway/wms/pda/pickAndSort/progress` | pickAndSortProgress | String, String |
| write/unknown | POST | `/gateway/wms/pda/pickAndSort/scanSku` | pickAndSortScanSku | PickAndSortScanSkuReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/BoxTypeReceipt` | boxTypeReceipt | BoxTypeReceipt |
| read? | GET | `/gateway/wms/pda/receipt/batchReceipt/getAsnDtl` | getAsnDtl | String, String |
| write/unknown | POST | `/gateway/wms/pda/receipt/batchReceipt/submit` | batchReceiptSubmit | BatchReceiptSubmitReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/boxPalletReceipt` | boxPalletReceipt | BoxPalletReceiptReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/boxTypePalletReceipt` | boxTypePalletReceipt | BoxTypePalletReceiptReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/finishBoxTypeReceipt` | finishBoxReceipt | BoxTypeReceipt |
| write/unknown | POST | `/gateway/wms/pda/receipt/finishWholeBoxReceipt` | finishWholeBoxReceipt | SubmitWholeReceipt |
| read? | GET | `/gateway/wms/pda/receipt/getAllReceiptList` | getAllReceiptList | String |
| read? | GET | `/gateway/wms/pda/receipt/getAsn` | getAsnByNo | String, String |
| read? | GET | `/gateway/wms/pda/receipt/getBoxTypeDtl` | getBoxTypeDtl | String, String, String, String |
| read? | POST | `/gateway/wms/pda/receipt/getBoxTypeInfo` | getBoxTypeInfo | BoxTypeInfoReq |
| read? | GET | `/gateway/wms/pda/receipt/getOrderDtl` | getOrderDtl | String, String, String |
| read? | POST | `/gateway/wms/pda/receipt/getOrderDtl/skuDetailPage` | getOrderDtlSkuDetailPage | SkuDetailPageReq |
| read? | GET | `/gateway/wms/pda/receipt/listBoxType` | listBoxType | String, String, I, I, String, String |
| read? | GET | `/gateway/wms/pda/receipt/listBoxType` | listBoxType2 | String, String, I, I, String, String |
| write/unknown | POST | `/gateway/wms/pda/receipt/measureBoxType/batch` | batchMeasureBoxType | BatchMeasureBoxTypeReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/scanPallet` | scanPallet | PalletReceiptScanReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/scanPalletSku` | scanPalletSku | ScanPalletSkuReq |
| write/unknown | POST | `/gateway/wms/pda/receipt/skuReceipt` | skuReceipt | SkuForecastReceiptReq |
| read? | GET | `/gateway/wms/pda/receipt/v2/getBoxDtl` | getBoxDtl2 | String, String, String, String, String |
| write/unknown | POST | `/gateway/wms/pda/receipt/wholeBoxReceipt` | submitWholeReceipt | SubmitWholeReceipt |
| write/unknown | POST | `/gateway/wms/pda/receipt/wholeOrderReceipt` | wholeOrderReceipt | WholeOrderReceiptReq |
| write/unknown | POST | `/gateway/wms/pda/return/create/commitOrder` | commitOrder | CommitOrderReq |
| write/unknown | POST | `/gateway/wms/pda/return/create/commitOrder` | commitOrder | PlatformReturnCommitReq, String |
| read? | GET | `/gateway/wms/pda/return/create/getLastOrderType` | getLastOrderType | String |
| write/unknown | POST | `/gateway/wms/pda/return/create/scanBarcode` | receiveScanBarcode | ReceiveScanBarcodeReq |
| write/unknown | POST | `/gateway/wms/pda/return/create/type` | returnCreateType | ReturnCreateTypeReq, String |
| write/unknown | POST | `/gateway/wms/pda/return/create/updateLastOrderType` | updateLastOrderType | UpdateLastOrderTypeReq |
| write/unknown | POST | `/gateway/wms/pda/return/putaway/destruct` | destruct | DestructReq |
| read? | GET | `/gateway/wms/pda/return/putaway/detail/scanBarcode` | scanBarcodeReturnShelvesDetail | String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/return/putaway/listOrder` | returnShelvesListOrder | I, I, String |
| write/unknown | POST | `/gateway/wms/pda/return/putaway/scanBarcode` | returnScanBarcode | ReturnPutawayScanReq |
| write/unknown | GET | `/gateway/wms/pda/return/putaway/scanCell` | returnScanCell | String, String, String |
| write/unknown | POST | `/gateway/wms/pda/return/putaway/scanOrderNo` | returnScanOrderNo | ReturnPutawayScanReq |
| write/unknown | POST | `/gateway/wms/pda/return/putaway/submit` | returnShelvesSubmit | ShelvesInfoReq |
| read? | GET | `/gateway/wms/pda/return/receive/detail` | receiveDetail | String, String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/detail/scanBarcode` | returnDetailScanBarcode | String, String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/detail/scanExpressNo` | returnDetailScanExpressNo | String, String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/listOrder` | receiveListOrder | I, I, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/listPackage` | returnListPackage | String, String, String |
| read? | GET | `/gateway/wms/pda/return/receive/listProduct` | listProduct | I, I, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/scanBarcode` | receiveScanBarcode | String, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/scanCell` | receiveScanCell | String, String |
| write/unknown | GET | `/gateway/wms/pda/return/receive/scanOrderNo` | receiveScanOrderNo | String, String |
| write/unknown | POST | `/gateway/wms/pda/return/receive/scanProductSnNo` | scanProductSnNo | ScanProductSnNoReq |
| write/unknown | POST | `/gateway/wms/pda/return/receive/submit` | receiveSubmit | ReceiptMailSubmit |
| write/unknown | POST | `/gateway/wms/pda/review/confirmDelivery` | confirmDelivery | ReviewConfirmDeliveryReq |
| read? | GET | `/gateway/wms/pda/review/get4MorePiece` | get4MorePiece | String, Integer, String, String |
| read? | GET | `/gateway/wms/pda/review/get4piecePackage` | get4piecePackage | String, String, Integer, String, String |
| read? | GET | `/gateway/wms/pda/review/getCustomerCode4More` | getCustomerCode4More | String, String |
| write/unknown | POST | `/gateway/wms/pda/review/morePieceV2` | morePiece | ReviewMorePieceReq |
| write/unknown | POST | `/gateway/wms/pda/review/review4packageV2` | review4package | Review4packageReq |
| write/unknown | POST | `/gateway/wms/pda/shelf/batch/scanBox` | batchPutawayScanBox | BatchPutawayScanBoxReq |
| write/unknown | POST | `/gateway/wms/pda/shelf/batch/scanCell` | batchPutawayScanCell | BatchPutawayScanCellReq |
| read? | POST | `/gateway/wms/pda/shelf/batch/skuList` | batchPutawaySkuList | BatchPutawaySkuQueryReq |
| write/unknown | POST | `/gateway/wms/pda/shelf/batch/submit` | batchPutawaySubmit | BatchPutawaySubmitReq |
| read? | GET | `/gateway/wms/pda/shelf/box/listBoxBarCode` | listBoxBarCode | String, String, String |
| write/unknown | POST | `/gateway/wms/pda/shelf/commitCompleteOrder` | commitCompleteOrder | CommitCompleteOrderReq |
| write/unknown | GET | `/gateway/wms/pda/shelf/crossDockCompleteOrder` | crossDockCompleteOrder | String, String, String |
| read? | GET | `/gateway/wms/pda/shelf/merge/page` | getMergeShelfPage | I, I, I, String |
| read? | GET | `/gateway/wms/pda/shelf/merge/scanContent` | get03VMergeShelfWay | String, I, I, I, String |
| write/unknown | GET | `/gateway/wms/pda/shelf/onoPieceCompleteOrder` | onoPieceCompleteOrder | String, String, String |
| read? | POST | `/gateway/wms/pda/shelf/preCheckPalletList` | preCheckPalletList | PreCheckPalletListReq |
| write/unknown | GET | `/gateway/wms/pda/shelf/scanBarcode` | scanBarcode | String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/shelf/scanCell` | scanCell | String, String, String, String, I |
| write/unknown | POST | `/gateway/wms/pda/shelf/scanPalletCode` | scanPalletCode | PalletShelfScanReq |
| write/unknown | POST | `/gateway/wms/pda/shelf/submitPalletShelf` | submitPalletShelf | PalletShelfSubmitReq |
| write/unknown | POST | `/gateway/wms/pda/shelf/v2/box/submitShelf` | submitShelfV2 | BoxShelvesReq |
| write/unknown | POST | `/gateway/wms/pda/shelf/v2/confirm` | shelfConfirmV2 | SkuShelvesReq |
| write/unknown | GET | `/gateway/wms/pda/shelf/v2/crossDock/scanBoxType` | checkBoxTypeNoV2 | String, String, String, String |
| write/unknown | POST | `/gateway/wms/pda/shelf/v2/crossDock/submitBoxType` | submitBoxTypeV2 | TrfShelvesReq |
| read? | GET | `/gateway/wms/pda/shelf/v2/getRecommendCell/v2` | getRecommendCellList | I, I, String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/shelf/v2/scanBarcode` | scanBarcodeV2 | String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/shelf/v2/scanShelfByCellNo` | scanShelfByCellNo | String, String, String, String |
| write/unknown | GET | `/gateway/wms/pda/shelf/v3/box/scanBoxBarcode` | scanBoxBarcodeV3 | String, String, String, String |
| write/unknown | POST | `/gateway/wms/pda/stock/adjust` | stockAdjust | StockAdjustReq |
| read? | GET | `/gateway/wms/pda/stock/cell/list` | getStockCellList | I, I, String, String, I, String, Integer, String, String, String |
| read? | POST | `/gateway/wms/pda/stock/cellList` | stockCellList | StockCellListReq |
| write/unknown | POST | `/gateway/wms/pda/stock/check` | stockCheck | StockCheckReq |
| read? | GET | `/gateway/wms/pda/stock/count` | stockCount | String, String |
| read? | POST | `/gateway/wms/pda/stock/customer/cellList` | stockCustomerCellList | StockCellListReq |
| read? | POST | `/gateway/wms/pda/stock/detail` | stockDetail | StockDetailReq |
| write/unknown | POST | `/gateway/wms/pda/stock/move` | stockMove | StockMoveReq |
| write/unknown | POST | `/gateway/wms/pda/stock/search` | stockSearch | StockSearchReq |
| write/unknown | POST | `/gateway/wms/pda/stock/stockCellQuality` | stockCellQuality | StockCellQualityReq |
| read? | POST | `/gateway/wms/pda/warehouse/listParams` | listParams | WmsSystemParamReq |
| read? | GET | `/gateway/wms/pda/warehouse/page` | getWhList | I, I, I |
| write/unknown | GET | `/gateway/wms/pda/wave/boxPageCell` | boxPageCell | I, I, String, String |
| write/unknown | POST | `/gateway/wms/pda/wave/boxPick` | boxPick | BoxBigReq |
| read? | GET | `/gateway/wms/pda/wave/boxPick/directPage` | directPage | I, I, String, String |
| write/unknown | POST | `/gateway/wms/pda/wave/boxScanCell` | boxScanCell | WaveBoxScanCellReq |
| write/unknown | GET | `/gateway/wms/pda/wave/bulk/pickWay` | pickWay | String, String |
| read? | GET | `/gateway/wms/pda/wave/page` | getWavePage | I, I, String, String |
| read? | GET | `/gateway/wms/pda/wave/pageCell` | getWavePageCell | I, I, String, String |
| write/unknown | POST | `/gateway/wms/pda/wave/pick` | waveSubmit | WavePickReqBean |
| read? | GET | `/gateway/wms/pda/wave/pickList` | getPickList | I, String |
| read? | POST | `/gateway/wms/pda/wave/queryIntercepting` | queryIntercepting | QueryInterceptingReq |
| write/unknown | GET | `/gateway/wms/pda/wave/recommendCell/` | recommendCell | String, String |
| write/unknown | POST | `/gateway/wms/pda/wave/scanBox` | scanBox | WaveOutReqBody |
| write/unknown | POST | `/gateway/wms/pda/wave/scanBoxBarcode` | scanBoxBarcodeV3 | ScanBoxBarcodeReq |
| write/unknown | POST | `/gateway/wms/pda/wave/scanCell` | waveScanCell | WaveReqBody |
| write/unknown | POST | `/gateway/wms/pda/wave/scanSku` | waveScanSku | WaveReqBody |
| read? | GET | `/gateway/wms/pda/wave/skuDetail` | skuDetail | String, String, String |
| read? | GET | `/gateway/wms/shelf/selectCell` | selectShelfCell | I, I, I, String, String |
| write/unknown | GET | `/gateway/wms/system/androidVersionRelease/validateVersion` | checkUpdate | String, String, String |
| read? | GET | `/gateway/wms/system/sysBaseParamsDtl/findParams` | sysBaseParamsDtl | String, String, String |
