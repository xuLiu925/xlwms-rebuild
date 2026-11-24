# PDA 请求体字段参考（离线反编译还原）

> 来源：`_private/jadx-out/` 中 `com.lingxing.wms.data.model.bean` 下所有 `*Req.java`
> （Kotlin data class 反编译）。字段取自 `@SerializedName` 注解；无注解的 data class
> 字段名即 JSON 键（Gson 默认）。只读分析，未调用任何真实接口。
>
> 说明：
> - 类型中 `List<X>` 的元素 `X` 多为同包内的嵌套 bean，详见对应源码。
> - 标注 `(字段名 xxx)` 表示 `@SerializedName` 的 JSON 键与 Java 字段名不同。
> - 这里只列**请求体**类（`*Req`）。响应体 `*Resp/*Rsp`、列表 bean 等未全部展开。
> - 对应接口路径见 `docs/PDA_API.md`；请求头与签名见 `docs/PDA_SIGNING.md`。

共 71 个请求体类，434 个字段。

### AllNeedNewProductMaintainReq

| JSON 字段 | 类型 |
|---|---|
| `asnNo` | `String` |
| `customerCode` | `String` |
| `whCode` | `String` |

### BatchMeasureBoxTypeReq

| JSON 字段 | 类型 |
|---|---|
| `boxTypeInfoList` | `List<MeasureBoxType>` |
| `whCode` | `String` |

### BoxBigReq

| JSON 字段 | 类型 |
|---|---|
| `boxList` | `List<BoxBigPickItem>` |
| `cellNo` | `String` |
| `customerCode` | `String` |
| `deliveryNo` | `String` |
| `sourceNo` | `String` |
| `waveNo` | `String` |
| `whCode` | `String` |

### BoxMoveStockBoxReq

| JSON 字段 | 类型 |
|---|---|
| `boxType` | `String` |
| `customerCode` | `String` |
| `customizeBarcode` | `String` |
| `moveQty` | `int` |
| `remark` | `String` |
| `sourceCellNo` | `String` |
| `targetCellNo` | `String` |

### BoxMoveStockReq

| JSON 字段 | 类型 |
|---|---|
| `boxList` | `List<BoxMoveStockBoxReq>` |
| `returnList` | `List<BoxMoveStockReturnReq>` |
| `skuList` | `List<BoxMoveStockSkuReq>` |
| `whCode` | `String` |

### BoxMoveStockReturnReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `fnsku` | `String` |
| `moveQty` | `int` |
| `productName` | `String` |
| `productQuality` | `int` |
| `productSku` | `String` |
| `remark` | `String` |
| `sourceCellNo` | `String` |
| `targetCellNo` | `String` |

### BoxMoveStockSkuReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `moveQty` | `int` |
| `productName` | `String` |
| `productQuality` | `int` |
| `productSku` | `String` |
| `remark` | `String` |
| `sourceCellNo` | `String` |
| `stockBatchId` | `int` |
| `targetCellNo` | `String` |

### BoxPickReq

| JSON 字段 | 类型 |
|---|---|
| `boxType` | `String` |
| `cellNo` | `String` |
| `creator` | `String` |
| `creatorName` | `String` |
| `customerCode` | `String` |
| `deliveryNo` | `String` |
| `pickQty` | `int` |
| `waveNo` | `String` |
| `whCode` | `String` |

### BoxTypeInfoReq

| JSON 字段 | 类型 |
|---|---|
| `asnNo` | `String` |
| `boxTypeList` | `List<? extends Object>` |
| `customerCode` | `String` |
| `whCode` | `String` |

### BulkDeliveryConfirmReq

| JSON 字段 | 类型 |
|---|---|
| `confirmDeliveryDTOList` | `List<BulkDeliveryConfirmItem>` |
| `pallets` | `Integer` |
| `whCode` | `String` |

### CommitCompleteOrderReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `customerCode` | `String` |
| `inboundType` | `int` |
| `putawayNo` | `String` |
| `whCode` | `String` |

### CommitOrderReq

| JSON 字段 | 类型 |
|---|---|
| `appendixList` | `List<Appendix>` |
| `customerCode` | `String` |
| `orderType` | `int` |
| `packageList` | `List<Package>` |
| `productList` | `List<ReceiveScanBarcodeResp>` |
| `remark` | `String` |
| `whCode` | `String` |
| `checked` | `boolean` |
| `height` | `double` |
| `heightBs` | `double` |
| `length` | `double` |
| `lengthBs` | `double` |
| `reach` | `int` |
| `sizeUnit` | `String` |
| `tempCellNo` | `String` |
| `trackNo` | `String` |
| `unitMark` | `int` |
| `weight` | `double` |
| `weightBs` | `double` |
| `weightUnit` | `String` |
| `width` | `double` |
| `widthBs` | `double` |

### CreateClaimReq

| JSON 字段 | 类型 |
|---|---|
| `appendixList` | `List<Object>` |
| `cellNo` | `String` |
| `claimCustomerCode` | `String` |
| `expressNo` | `String` |
| `operateType` | `Integer` |
| `productList` | `List<Object>` |
| `remark` | `String` |
| `whCode` | `String` |

### DeliveryConfirmReq

| JSON 字段 | 类型 |
|---|---|
| `deliveryNoList` | `List<String>` |
| `whCode` | `String` |

### DestructReq

| JSON 字段 | 类型 |
|---|---|
| `barcode` | `String` |
| `customerCode` | `String` |
| `handleAppendixList` | `List<Appendix>` |
| `handleRemark` | `String` |
| `productName` | `String` |
| `putawayExpirationSubmitDTOList` | `List<DestructSubmit>` |
| `qty` | `int` |
| `returnNo` | `String` |
| `skuId` | `String` |
| `unitMark` | `int` |
| `weight` | `int` |
| `weightBs` | `int` |
| `whCode` | `String` |

### DeviceBillPrepareReq

| JSON 字段 | 类型 |
|---|---|
| `bizNo` | `String` |
| `bizType` | `String` |
| `whCode` | `String` |

### DeviceCellPrepareReq

| JSON 字段 | 类型 |
|---|---|
| `cellNos` | `List<String>` |
| `whCode` | `String` |

### DeviceResolveReq

| JSON 字段 | 类型 |
|---|---|
| `bizNo` | `String` |
| `bizType` | `String` |
| `whCode` | `String` |

### DeviceStockLocateReq

| JSON 字段 | 类型 |
|---|---|
| `bizType` | `String` |
| `skuCellPairs` | `List<SkuCellPair>` |
| `whCode` | `String` |
| `cellNo` | `String` |
| `sku` | `String` |

### DeviceTaskDetailReq

| JSON 字段 | 类型 |
|---|---|
| `bizNo` | `String` |
| `bizType` | `String` |
| `whCode` | `String` |

### DeviceWavePrepareReq

| JSON 字段 | 类型 |
|---|---|
| `bizNo` | `String` |
| `bizType` | `String` |
| `whCode` | `String` |

### DeviceWaveSubOrderResolveReq

| JSON 字段 | 类型 |
|---|---|
| `outOrderNo` | `String` |
| `subSeqNo` | `int` |
| `waveNo` | `String` |
| `whCode` | `String` |

### FbaConfirmSignReq

| JSON 字段 | 类型 |
|---|---|
| `ascs` | `String` |
| `current` | `long` |
| `customerCode` | `String` |
| `customerName` | `String` |
| `descs` | `String` |
| `logisticsCarrier` | `String` |
| `size` | `long` |
| `temporaryCellNo` | `String` |
| `trackNo` | `String` |
| `whCode` | `String` |

### FbaReturnShelvesReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `customerCode` | `String` |
| `expressNo` | `String` |
| `fnsku` | `String` |
| `fritTaskNo` | `String` |
| `logisticsCarrier` | `String` |
| `productQuality` | `int` |
| `productSku` | `String` |
| `putawayQty` | `int` |
| `sourceNo` | `String` |
| `whCode` | `String` |

### FinishPickTaskReq

| JSON 字段 | 类型 |
|---|---|
| `acceptLanguage` | `String` |
| `customerCode` | `String` |
| `frstTaskNo` | `String` |
| `whCode` | `String` |

### InventoryCheckCellSubmitReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `dtlList` | `List<InventoryCheckDetailSubmit>` |
| `orderSn` | `String` |
| `whCode` | `String` |

### InventoryCheckListReq

| JSON 字段 | 类型 |
|---|---|
| `current` | `int` |
| `orderSn` | `String` |
| `size` | `int` |
| `whCode` | `String` |

### InventoryCheckOrderReq

| JSON 字段 | 类型 |
|---|---|
| `orderSn` | `String` |
| `whCode` | `String` |

### InventoryCheckScanReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `orderSn` | `String` |
| `scanCode` | `String` |
| `whCode` | `String` |

### InventoryPreCheckReq

| JSON 字段 | 类型 |
|---|---|
| `appendixList` | `List<InventoryPreCheckAppendix>` |
| `customerCode` | `String` |
| `customerName` | `String` |
| `expressNo` | `String` |
| `logisticsCarrier` | `String` |
| `productList` | `List<InventoryPreCheckProduct>` |
| `receiptRemark` | `String` |
| `temporaryCellNo` | `String` |
| `whCode` | `String` |

### JudgeCallReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `customerCode` | `String` |
| `expressNo` | `String` |
| `fritTaskNo` | `String` |
| `logisticsCarrier` | `String` |
| `productQuality` | `int` |
| `sourceNo` | `String` |
| `whCode` | `String` |

### MessageBoardReplyReq

| JSON 字段 | 类型 |
|---|---|
| `appendixList` | `List<Appendix>` |
| `content` | `String` |
| `customerCode` | `String` |
| `orderType` | `int` |
| `whCode` | `String` |
| `workNo` | `String` |

### MessageBoardSearchReq

| JSON 字段 | 类型 |
|---|---|
| `scanCode` | `String` |
| `tenantCode` | `String` |
| `whCode` | `String` |

### NeedNewProductMaintainReq

| JSON 字段 | 类型 |
|---|---|
| `asnNo` | `String` |
| `customerCode` | `String` |
| `skuList` | `List<Object>` |
| `whCode` | `String` |

### PalletReceiptScanReq

| JSON 字段 | 类型 |
|---|---|
| `palletCode` | `String` |
| `whCode` | `String` |

### PalletShelfScanReq

| JSON 字段 | 类型 |
|---|---|
| `palletCode` | `String` |
| `whCode` | `String` |

### PalletShelfSubmitReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `palletCode` | `String` |
| `putawayNo` | `String` |
| `shelfCellNo` | `String` |
| `sourceNo` | `String` |
| `whCode` | `String` |

### PlatformReturnCommitReq

| JSON 字段 | 类型 |
|---|---|
| `appendixList` | `List<Appendix>` |
| `customerCode` | `String` |
| `orderType` | `int` |
| `packageList` | `List<CommitOrderReq.Package>` |
| `platformReturn` | `PlatformReturnReceiptPayload` |
| `whCode` | `String` |

### PostTaskOrderReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `customerCode` | `String` |
| `fnsku` | `String` |
| `frstTaskNo` | `String` |
| `pickQty` | `int` |
| `whCode` | `String` |

### QueryInterceptingReq

| JSON 字段 | 类型 |
|---|---|
| `orderNo` | `String` |
| `tenantCode` | `String` |
| `whCode` | `String` |

### ReceiveAndShelfReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `detailList` | `List<Detail>` |
| `expressNo` | `String` |
| `fritTaskNo` | `String` |
| `logisticsCarrier` | `String` |
| `whCode` | `String` |
| `cellNo` | `String` |
| `fnsku` | `String` |
| `sku` | `String` |

### ReceiveScanBarcodeReq

| JSON 字段 | 类型 |
|---|---|
| `barcode` | `String` |
| `customerCode` | `String` |
| `tenantCode` | `String` |
| `whCode` | `String` |

### ReturnCreateTypeReq

| JSON 字段 | 类型 |
|---|---|
| `includePlatformReturn` | `boolean` |
| `trackNo` | `String` |
| `whCode` | `String` |

### ReturnPutawayScanReq

| JSON 字段 | 类型 |
|---|---|
| `scanContent` | `String` |
| `whCode` | `String` |

### Review4packageReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `deliveryNo` | `String` |
| `oneClickPass` | `boolean` |
| `packNo` | `String` |
| `reviewList` | `List<Review>` |
| `whCode` | `String` |
| `barcode` | `String` |
| `productSnNoList` | `List<String>` |
| `qty` | `int` |

### ReviewBulkConfirmDeliveryReq

| JSON 字段 | 类型 |
|---|---|
| `boxDtlList` | `List<ReviewBulkBoxDtl>` |
| `customerCode` | `String` |
| `deliveryMsgCreateDTO` | `ReviewDeliveryMsgCreateReq` |
| `deliveryNo` | `String` |
| `skuDtlList` | `List<ReviewBulkSkuDtl>` |
| `sourceNo` | `String` |
| `whCode` | `String` |

### ReviewConfirmDeliveryReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `deliveryNo` | `String` |
| `whCode` | `String` |

### ReviewDeliveryMsgCreateReq

| JSON 字段 | 类型 |
|---|---|
| `attachType` | `int` |
| `content` | `String` |
| `customerCode` | `String` |
| `orderType` | `int` |
| `sourceNo` | `String` |
| `whCode` | `String` |
| `wmsAppendixDTOList` | `List<Appendix>` |

### ReviewMorePieceReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `deliveryNo` | `String` |
| `oneClickPass` | `boolean` |
| `reviewList` | `List<ReviewMorePieceItem>` |
| `whCode` | `String` |

### ScanBoxBarcodeReq

| JSON 字段 | 类型 |
|---|---|
| `boxBarcode` | `String` |
| `customerCode` | `String` |
| `deliveryNo` | `String` |
| `whCode` | `String` |

### ScanProductSnNoReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `productSku` | `String` |
| `productSnNo` | `String` |
| `remark` | `String` |
| `returnNo` | `String` |
| `whCode` | `String` |

### SkuDetailPageReq

| JSON 字段 | 类型 |
|---|---|
| `asnNo` | `String` |
| `current` | `int` |
| `customerCode` | `String` |
| `size` | `int` |
| `whCode` | `String` |

### SkuForecastReceiptReq

| JSON 字段 | 类型 |
|---|---|
| `asnNo` | `String` |
| `customerCode` | `String` |
| `rowVersion` | `String` |
| `skuList` | `List<Sku>` |
| `skuPalletList` | `List<SkuPallet>` |
| `sourceNo` | `String` |
| `whCode` | `String` |
| `expiryTime` | `String` |
| `productSku` | `String` |
| `productionTime` | `String` |
| `receiptQty` | `int` |
| `palletCode` | `String` |
| `palletHeight` | `double` |
| `palletHeightBs` | `double` |
| `palletLength` | `double` |
| `palletLengthBs` | `double` |
| `palletWeight` | `double` |
| `palletWeightBs` | `double` |
| `palletWidth` | `double` |
| `palletWidthBs` | `double` |
| `remark` | `String` |

### StockAdjustReq

| JSON 字段 | 类型 |
|---|---|
| `adjustFlag` | `Integer` |
| `adjustQty` | `String` |
| `adjustType` | `Integer` |
| `beforeStkQty` | `String` |
| `boxType` | `String` |
| `cellNo` | `String` |
| `customerCode` | `String` |
| `fnsku` | `String` |
| `productName` | `String` |
| `productQuality` | `Integer` |
| `productSku` | `String` |
| `remark` | `String` |
| `whCode` | `String` |

### StockCellListReq

| JSON 字段 | 类型 |
|---|---|
| `cellNo` | `String` |
| `current` | `int` |
| `customerCode` | `String` |
| `size` | `int` |
| `type` | `Integer` |
| `whCode` | `String` |

### StockCellQualityReq

| JSON 字段 | 类型 |
|---|---|
| `sourceCellNo` | `String` |
| `targetCellNo` | `String` |
| `whCode` | `String` |

### StockCheckReq

| JSON 字段 | 类型 |
|---|---|
| `adjustType` | `Integer` |
| `boxType` | `String` |
| `cellNo` | `String` |
| `customerCode` | `String` |
| `fnsku` | `String` |
| `productQuality` | `Integer` |
| `productSku` | `String` |
| `whCode` | `String` |

### StockDetailReq

| JSON 字段 | 类型 |
|---|---|
| `boxType` | `String` |
| `current` | `int` |
| `customerCode` | `String` |
| `customerName` | `String` |
| `customizeBarcode` | `String` |
| `fnsku` | `String` |
| `productQuality` | `Integer` |
| `productSku` | `String` |
| `size` | `int` |
| `type` | `Integer` |
| `whCode` | `String` |

### StockMoveReq

| JSON 字段 | 类型 |
|---|---|
| `boxType` | `String` |
| `customerCode` | `String` |
| `customizeBarcode` | `String` |
| `fnsku` | `String` |
| `moveQty` | `String` |
| `moveType` | `Integer` |
| `productName` | `String` |
| `productQuality` | `Integer` |
| `productSku` | `String` |
| `remark` | `String` |
| `sourceCellNo` | `String` |
| `targetCellNo` | `String` |
| `whCode` | `String` |

### StockSearchReq

| JSON 字段 | 类型 |
|---|---|
| `current` | `int` |
| `queryParam` | `String` |
| `queryType` | `Integer` |
| `searchCell` | `Integer` |
| `size` | `int` |
| `whCode` | `String` |

### UpdateLastOrderTypeReq

| JSON 字段 | 类型 |
|---|---|
| `tab` | `int` |
| `whCode` | `String` |

### UpdateStatusInventoryReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `customerName` | `String` |
| `detailList` | `List<Detail>` |
| `expressNo` | `String` |
| `fritTaskNo` | `String` |
| `logisticsCarrier` | `String` |
| `receiptRemark` | `String` |
| `temporaryCellNo` | `String` |
| `whCode` | `String` |
| `fnsku` | `String` |
| `productName` | `String` |
| `receiptQtyBad` | `String` |
| `receiptQtyGood` | `String` |
| `sku` | `String` |
| `skuId` | `String` |

### WaveBoxScanCellReq

| JSON 字段 | 类型 |
|---|---|
| `barcode` | `String` |
| `cellNo` | `String` |
| `customerCode` | `String` |
| `sku` | `String` |
| `waveNo` | `String` |
| `whCode` | `String` |

### WholeOrderReceiptReq

| JSON 字段 | 类型 |
|---|---|
| `asnNo` | `String` |
| `customerCode` | `String` |
| `rowVersion` | `String` |
| `sourceNo` | `String` |
| `whCode` | `String` |

### WmsSystemParamReq

| JSON 字段 | 类型 |
|---|---|
| `itemList` | `List<WmsSysParamItemBean>` |
| `whCode` | `String` |

### PickAndSortBindCartReq

| JSON 字段 | 类型 |
|---|---|
| `pickCartNo` | `String` |
| `waveNo` | `String` |
| `whCode` | `String` |

### PickAndSortScanSkuReq

| JSON 字段 | 类型 |
|---|---|
| `barcode` | `String` |
| `cellNo` | `String` |
| `currentProductSku` | `String` |
| `desiredQty` | `int` |
| `waveNo` | `String` |
| `whCode` | `String` |

### BoxShelvesReq

| JSON 字段 | 类型 |
|---|---|
| `boxBarcodeList` | `List<BoxShelvesItem>` |
| `customerCode` | `String` |
| `putawayNo` | `String` |
| `whCode` | `String` |

### ShelvesInfoReq

| JSON 字段 | 类型 |
|---|---|
| `barcode` | `String` |
| `cellNo` | `String` |
| `customerCode` | `String` |
| `forecastQty` | `Integer` |
| `handleAppendixList` | `List<Appendix>` |
| `handleRemark` | `String` |
| `productName` | `String` |
| `productQuality` | `Integer` |
| `putawayExpirationSubmitDTOList` | `List<ShelvesSubmit>` |
| `qty` | `int` |
| `returnNo` | `String` |
| `skuId` | `String` |
| `waitPutawayQty` | `int` |
| `whCode` | `String` |

### SkuShelvesReq

| JSON 字段 | 类型 |
|---|---|
| `customerCode` | `String` |
| `putawayNo` | `String` |
| `skuList` | `List<SkuShelvesItem>` |
| `whCode` | `String` |

### TrfShelvesReq

| JSON 字段 | 类型 |
|---|---|
| `boxTypeList` | `List<TrfShelvesItem>` |
| `customerCode` | `String` |
| `putawayNo` | `String` |
| `whCode` | `String` |

