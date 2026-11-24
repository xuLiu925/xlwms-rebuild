# 功能覆盖度与分批计划

目标：把重建版补到覆盖原 WMS 的全部模块。约束不变（见 CLAUDE.md）：数据全部虚构、不连生产。
网页端请求体没抓到，所以网页端的接口路径、请求体字段与状态编码大多是**本项目占位**；PDA 端的路径与请求体字段来自 APK 静态还原。

状态说明：✅ 专门实现 · 🟦 通用列表页（规格在 `tools/list-pages/spec*.mjs`；批次 8 起其中 23 页可本地增删改） · ⬜ 占位（页面显示"待实现"）

## 汇总（2026-11-24，批次 8 完成后）

| 范围 | 总数 | ✅ | 🟦 | ⬜ |
|---|---|---|---|---|
| 网页端子路由 | 79 | 21 | 58 | 0 |
| PDA 接口（ApiService） | 175 | 175 | — | 0 |

PDA 的 175 是 ApiService 方法数；其中 `receipt/listBoxType` 有两个方法共用一个路径，按方法计 2 个。

左侧菜单（2026-11-24 起）按参考系统菜单分组，见 `frontend/src/config/menuGroups.js`。参考菜单里另有 9 个路径不在公开路由表的 79 个子路由里，
本项目只放了“待实现”占位，不计入上表：`outbound/swapBarcode`、`transfer/index`、`report/{staffEfficiency,inbound,outbound}`、
`stock/{defective,checking,serial,workVideo}`。参考菜单的“退件”“工单”指向本项目的 `globalOrder/returnOrder`、`globalOrder/workOrder`。

## 网页端（按 router 模块）

| 模块 | 子路由 | 状态 |
|---|---|---|
| customer 客户 | list 客户列表、recharge 充值审核 | 🟦 ×2 |
| globalOrder 全局订单 | asn、parcel、freight、returnOrder、directMailOrder 直邮订单、directMailBox 直邮箱、claim 理赔、workOrder 工单 | 🟦 ×8 |
| stock 库存 | sku / box / return | ✅ ×3 |
| basicData 基础数据 | product / area / cell / tray / packMaterial | ✅ ×5 |
| | seedWall 播种墙、pickCart 拣货车 | 🟦 ×2 |
| inbound 入库 | ASN | ✅ |
| | arrivalScan 到货登记、deliveryGoods 送货预约、palletReceive 托盘收货记录、palletForming 组托记录、newProduct 新品维护、inventoryClaim 库存认领 | 🟦 ×6 |
| | receivePutaway 收货上架、shelf 上架 | ✅ ×2（批次 3，内存有状态） |
| outbound 出库 | parcel / wave | ✅ ×2 |
| | freight 大货出库、waveRule 波次规则、scanForm 交接单、split 拆单记录、swapOrder 换单记录、abnormal 异常订单、intercept 拦截订单 | 🟦 ×7 |
| | secondSort 二次分拣、review 复核、boxReview 按箱复核、weight 称重出库 | ✅ ×4（批次 3，与 PDA 出库共用状态） |
| returnGoods 退货 | warehousing 退货入库、rebrand 换标任务、outbound 退货出库 | 🟦 ×3 |
| logisticsManage 物流 | channel 渠道、channelGroup 渠道分组 | 🟦 ×2 |
| logisticsPrice 物流报价 | fuelPlan 燃油费、zoneTemp 分区模板、surcharge 附加费、freightTemp 运费模板 | 🟦 ×4 |
| quotesReceivable 应收报价 | logisticsPriceSetting、operateBill 操作费、warehouseRent 仓租、quotationScheme 报价方案 | 🟦 ×4 |
| receivableSettlement 应收结算 | businessExpenses 业务费用、billsReceivable 应收账单、expend 扣款记录、billTemplate 账单模板 | 🟦 ×4 |
| statisticalAnalysis 统计 | inventoryAgeAnalysis 库龄分析 | ✅（批次 3） |
| auth 权限 | account 账号、role 角色 | 🟦 ×2 |
| setting 设置 | exchangeRate、notice、printTemplate、brand、agency、erpAuth、msgNotice、warningMsg、receiveAccount | 🟦 ×9 |
| | globalSetting 全局设置 | ✅（批次 3，内存保存） |
| toolKit 工具箱 | requestLog、taskCenter、errorPool 异常池、labelRecord 面单记录、pushRecord 推送记录 | 🟦 ×5 |
| | freightCalculation 运费试算、zoneTest 分区测试 | ✅ ×2（批次 3，虚构费率与分区规则） |

## PDA 端（按路径一级模块）

| 模块 | 接口数 | 已实现 | 说明 |
|---|---|---|---|
| receipt 收货 | 22 | 22 | 全部在 `PdaReceiptFlowController` + `ReceiptStore`（批次 1 的 4 个骨架已并入并改为有状态） |
| shelf 上架 | 24 | 24 | 全部在 `PdaShelfFlowController` + `PutawayStore`，含 /shelf/selectCell（批次 1 的 4 个 /v2 骨架已并入并改为有状态） |
| return 退货收货/上架/创建 | 24 | 24 | 批次 6，`ReturnFlowController` + `ReturnStore`：receive 11、create 6（commitOrder 两个方法共用路径）、putaway 7 含 destruct；原 putaway 骨架已删除 |
| blReturnIn FBA 退货 | 24 | 24 | 批次 6，`FbaReturnController` + `FbaReturnStore`：签收、清点、上架、认领、退货拣货 |
| wave 波次拣货 | 16 | 16 | 按件拣货 8 个（`WaveController`）+ 按箱拣货 8 个（批次 4，`WaveBoxController`） |
| stock 库内 | 10 | 10 | 批次 4，`PdaStockController` + `StockStore`：查询、调整、移库、整库位移库 |
| device 设备任务 | 9 | 9 | 批次 7，`PdaDeviceController` + `DeviceStore`：4 个准备接口在 /pda/device 下，5 个任务接口在 /gateway/wms/device 下 |
| delivery 出库确认 | 8 | 8 | 批次 4，`PdaDeliveryController`：出库确认 3 个 + 大货复核 5 个 |
| review 复核 | 6 | 6 | 批次 4 补了 getCustomerCode4More |
| inventoryCheck 盘点 | 6 | 6 | 批次 4，`PdaInventoryCheckController` + `InventoryCheckStore` |
| newProduct 新品维护 | 5 | 5 | 批次 5，`PdaNewProductController` + `NewProductStore` |
| pickAndSort 边拣边分 | 4 | 4 | 批次 7，`PickAndSortController` + `PickAndSortStore`，拣货数写回波次 |
| appendix 附件 | 4 | 4 | 批次 7，`AppendixController` + `AppendixStore`，只记元数据、不存文件 |
| 其他（auth/home/menu/warehouse/system/messageBoard/common/blReceipt/blMoveStock） | 13 | 13 | 批次 5 做了 blReceipt/measureBoxType；批次 7 补齐其余 12 个（`AuthController` 与网页端共用、`PdaMiscController`、`MessageBoardController`、`AppendixController` 的 /gateway/common/pic/upload） |

## 分批计划

1. **批次 1（已完成）**：通用只读列表页机制 + 15 页。
2. **批次 2（已完成）**：剩余列表类网页页面 43 页，网页端覆盖到 69/79。规格按模块拆成 `spec-orders`、`spec-finance`、`spec-settings`，
   生成器加了自检（key/route 唯一、ID 区间不重叠、ID 不超过 JS 安全整数、每个资源最多一个页签列），测试期望值也由生成器输出。
3. **批次 3（已完成）**：剩余 10 个网页端作业/配置/工具页，左侧菜单布局（`views/layout/Layout.vue`，菜单由路由表生成），
   本地演示登录（`/auth/login`，不校验不保存密码），以及不依赖 MySQL 的 H2 本地运行方式（`.claude/launch.json` 的 backend-h2）。网页端 79/79 都有页面。
4. **批次 4（已完成）**：PDA 库内与出库补全 33 个接口：stock ×10、inventoryCheck ×6、delivery ×8、wave 按箱拣货 ×8、review 剩余 1。
   PDA 端覆盖到 60/175。规则见下方“批次 4 的数据与规则”。
5. **批次 5（已完成）**：PDA 收货/上架/新品维护 44 个接口（receipt ×18 方法、shelf ×20、newProduct ×5、blReceipt/measureBoxType），PDA 覆盖到 104/175。
   随后把批次 1 的 8 个收货/上架骨架接口也接入了 Store，PDA 收货/上架不再有无状态骨架。
6. **批次 6（已完成）**：PDA 退货 42 个接口（return receive ×11、create ×6、destruct，blReturnIn ×24），并把批次 1 的 6 个退货上架骨架改为有状态。
   PDA 覆盖到 146/175，剩下的都在批次 7。
7. **批次 7（已完成）**：其余 29 个 PDA 接口（device ×9、pickAndSort ×4、appendix ×4、messageBoard ×2、auth ×2、home、menu、warehouse ×2、system ×2、
   common/pic/upload、blMoveStock/boxMoveStock），PDA 覆盖到 175/175。规则见下方“批次 7 的数据与规则”。
8. **批次 8（已完成）**：网页端通用列表页本地增删改。58 个通用列表页中 23 个基础资料 / 报价 / 设置类页面可新建、编辑、删除，
   其余 35 个日志、单据、流水类页面保持只读。规则见下方“批次 8 的数据与规则”。

## 批次 3 页面的数据与规则

| 页面 | 后端 | 状态存放 | 说明 |
|---|---|---|---|
| 收货上架、上架 | `InboundWorkController` `/inboundWork/**` | `InboundWorkStore` 内存 | 3 张虚构入库单；上架库位查本地 basic_cell，锁定库位或跨仓库位会被拒 |
| 二次分拣、复核、按箱复核、称重出库 | `OutboundWorkController` `/outboundWork/**` | `OutboundSampleStore` 内存 | 新增已拣完的多品波次 WV26110004；称重要求已复核，称重后即出库 |
| 库龄分析 | `StockAgeController` `/stockAge/{summary,page}` | 读 stock_sku | 表里没有入库日期，库龄由 SKU 哈希推算，仅演示 |
| 全局设置 | `GlobalSettingController` `/globalSetting/{get,save}` | 内存 | 10 个假设的配置项，按类型和范围校验，校验失败整体不生效 |
| 运费试算、分区测试 | `ToolKitController` + `ZoneCalculator` | 无状态 | 渠道、费率、材积除数、分区规则全部虚构 |
| 登录 | `AuthController` `/auth/{login,getUserInfo,logout}` | 内存令牌 | 账号密码非空即可；不校验、不保存密码；其他接口不鉴权 |

所有路径与字段都是本项目占位，真实网页接口未抓取。

## 批次 4 的数据与规则（PDA）

路径、查询参数名、请求体与响应字段取自 APK（ApiService 与 data.model.bean，离线反编译）；下面的业务规则和编码都是本项目假设。

- **库存**（`StockStore`，内存）：一行 = 库位 + SKU + 箱型（空串为散件）+ 品质（1 良品 / 2 次品）。按箱库存以“箱”计数。
  库位 E-09-09 锁定，不能移入。`stock/adjust` 的 adjustFlag：1 调增 / 2 调减，带了 beforeStkQty 就必须与当前库存一致。
  `stock/search` 的 queryType：1 SKU/条码、2 FNSKU、3 箱型、4 库位，不传则全部。`stock/stockCellQuality` 按“整库位移库”实现（真实语义未确认）。
- **盘点**（`InventoryCheckStore`）：两张单，IC2611000001 明盘、IC2611000002 盲盘（盲盘不下发账面数）。status：0 待盘点 / 1 盘点中 / 2 已完成。
  start 时拍账面快照；扫到账面没有的产品会新增一行（盘盈）；所有库位提交后才能 complete，complete 把实盘数写回库存。只盘散件。
- **大货出库**（`FreightStore`）：按箱波次 BW26110001 含两张大货单 FO2611000001/0002。按箱拣货会扣减对应库位的按箱库存；
  拣完 → 待复核(10) → 大货复核要求每个箱型实出等于应出 → 已复核(20) → 出库确认 → 已出库(30)。
- **出库确认**：`delivery/getConfirmInfo` 的 outboundType 1 = 一件代发（`OutboundSampleStore`），2 = 大货；`confirmDelivery`/`bulkConfirmDelivery`
  按单号自动区分两类，整批先校验再出库，任何一单不满足就整批不生效。
- **序列号**：`delivery/review/getSysBaseParam` 固定返回 false（不要求扫序列号）；`scanProductSn` 只做校验和回显，响应字段是占位。
- **重置**：`POST /gateway/wms/demo/reset` 把所有内存示例数据恢复初始状态（本地演示专用）。

## 批次 5 的数据与规则（PDA 收货 / 上架 / 新品）

路径、查询参数名、请求体与响应字段取自 APK；规则与编码是本项目假设。流程：收货（`ReceiptStore`）→ 自动生成上架单（`PutawayStore`，单号 PA + 入库单号后段）→ 上架写入 `StockStore`。

- **5 张示例入库单**：RA2611000001 按箱预报（3 个箱唛，含新品 SKU-0012）；RA2611000002 按箱型预报；RA2611000003 按箱型、中转入库；
  RA2611000004 按箱预报、托盘收货（含新品 SKU-0013）；RA2611000005 按 SKU 预报（`skuReceipt` 可多次累计、可带托盘）。
  forecastType 1 按箱 / 2 按箱型 / 3 按 SKU；inboundType 1 普通 / 2 中转。按 SKU 预报的单没有箱，boxCount 计为 1。
- **原批次 1 的 4 个收货接口**：`scanPallet` 返回托盘是否可用（status 0 空闲 / 1 已绑定未上架）；`getBoxTypeInfo` 返回箱规（有实测值用实测值）；
  `skuReceipt` 散收与托盘收的合计一起校验，返回上架单号；`wholeOrderReceipt` 把剩余全部按预报收完并完成收货。
- **原批次 1 的 4 个 /v2 上架接口**：`scanBarcode` 与 v1 相同；`getRecommendCell/v2` 返回待上架 SKU 与箱型及推荐库位；
  `confirm` 同一 SKU 多行合计不得超过待上架数；`box/submitShelf` 每箱可去不同库位，箱唛不能重复。都是先全部校验再统一上架。
- **收货**：状态沿用 10 在途 / 11 收货中 / 15 已收货；不允许超收；一个箱唛只收一次；“完成收货”接口允许少收后直接置为 15。
  带了 rowVersion 就必须与当前一致（每次变更加 1）。多箱 / 多托盘提交先全部校验再收货，任一项不通过则整体不生效；
  `batchReceipt/submit` 和 `measureBoxType/batch` 按单处理，失败的单写进返回列表，成功的单照常生效。
- **上架**：putawayType 1 按 SKU / 2 按箱型 / 3 整托。按 SKU 上架只累计数量；整箱上架要求箱内每个 SKU 的待上架数都够，
  避免同一批货上架两次。`commitCompleteOrder` 把剩余全部上架到一个库位。`batch/submit` 里的 expectedPendingPutawayQty 与服务端不一致时该单失败（提示刷新）。
  推荐库位：已有该 SKU 库存的库位优先，否则第一个空库位。锁定库位 E-09-09 一律拒绝。
- **托盘**：托盘号在上架完成前不能被另一次收货复用；`preCheckPalletList` 返回目标库位上已有其他托盘的冲突，空列表表示可提交。
- **新品**：SKU-0012、SKU-0013 初始为待测量；`receiptMeasure` 先校验全部再记录（0~300 cm、0~500 kg），测量后从待维护清单移除。

## 批次 6 的数据与规则（PDA 退件 / FBA 退货）

路径、查询参数名、请求体与响应字段取自 APK；规则与编码是本项目假设。上架写入 `StockStore`，拣货从 `StockStore` 扣减。

- **品质与库位**：productQuality 1 良品 / 2 次品；**次品只能上架到 E 区库位**（`StockStore.requireQualityCell`），良品不限。
- **退件单**（`ReturnStore`）：status 10 待收货 / 20 待上架 / 30 已完成；orderType 1 买家退货 / 2 拒收 / 3 PDA 新建 / 4 认领。
  示例单 RN2611000001、0002 待收货，RN2611000003 已收货待上架（良 1 次 1）。
  - 收货：至少一个包裹到货；暂存库位必须有效；可以收预报外的产品（但必须属于同一客户）；序列号在单内不能重复。
  - PDA 新建：跟踪号不能和已有退件单重复，产品必须属于所选客户，新单直接是待上架。`create/type` 的 createType 1 新建 / 2 已有退件单。
    平台退货建单（PlatformReturnCommitReq 的 platformReturn）本地不支持；APK 里该方法多带的一个请求头名字未还原，未读取。
  - 上架：可一次分到多个库位，合计不超过待上架数。销毁只针对次品、必须填处理说明、只在本地内存扣减待处理数。
- **FBA 退货任务**（`FbaReturnStore`）：status 0 待签收 / 10 已签收 / 20 待清点 / 30 待上架 / 40 已完成。
  FR2611000001 待签收（预报 FN-DEMO-0001×3、0002×2），FR2611000002 已清点待上架。
  - 签收分两步：`confirmSign` 扫快递（0→10），`updateStatusSign` 确认明细（10→20）；没有预报的包裹用 `signAndInventory` 一步建成待上架。
  - 清点可追加预报外的 FNSKU（`addFnSku`，isNewAdd=true）；FNSKU 必须属于任务的客户。
  - 认领 `createClaim` 在退件单里建一张 orderType=4 的待上架单，之后走退件上架。客户权限字段为占位（全部 true）。
  - 退货拣货任务 PK2611000001：pickStatus 0 待拣 / 1 拣货中 / 2 已完成；`finishTaskOrder` 允许少拣后结束，但至少要拣过一件。

## 批次 7 的数据与规则（PDA 其他接口）

路径、查询参数名、表单字段、请求体与响应字段取自 APK；下面的规则、状态串和编码都是本项目假设，除非另有说明。

- **设备任务**（`DeviceStore`）：设备库位固定为 B-01-01、B-01-03（tenantEnabled 返回的 specialCellNos），vendor 为占位串。
  - 只有涉及设备库位的作业才建任务；不涉及时直接放行（deviceStatus=NONE、canProceed=true、latestTaskId=null）。
  - 设备“瞬间”处理完：B-01-03 上的行模拟异常（exceptionType BIN_NOT_ARRIVED，料箱未到位），其余行成功。
  - deviceStatus：NONE / EXCEPTION / COMPLETED；bizStatus：WAITING / READY；行 taskStatus：SUCCESS / FAILED / RESOLVED。
  - 同一 bizType + bizNo 重复准备返回同一个任务。cell/prepare 的 bizNo 为 `CELL:库位,库位`；bill/prepare 本地只支持 bizType=DELIVERY（出库单）；
    wave/prepare 逐个出库单、逐个明细建行（seqNo = 出库单序号，subSeqNo = 明细序号），库位取自波次拣货行。
  - tasks/resolve 一次处理全部异常行；tasks/wave/subOrder/resolve 处理一行。没有待处理异常时报错。
- **边拣边分**（`PickAndSortStore`）：只有多品、未拣完的波次能绑车（示例里是 WV26110002）；一个波次一辆车，一辆车同时只服务一个波次。
  - 每个出库单一格，gridSerial 从 1 开始，gridNo = 车号-两位格号；格子 state：EMPTY / PICKING / FULL。
  - 扫一次按格号从小到大分配给还缺这个 SKU 的格子；实际数量 = min(期望数, 拣货行剩余, 各格缺口合计)。
  - 拣货数写回 `OutboundSampleStore` 的波次拣货行，与普通按件拣货共用状态，拣完后出库单变为待复核。不扣 `StockStore`（与按件拣货一致）。
- **附件与图片**（`AppendixStore`）：只在内存记录元数据，**不保存文件内容**，url 是 `local-demo://` 占位串，不能下载。
  - bizCode：RETURN / OUTBOUND / INBOUND / MESSAGE_BOARD，各自的附件类型见 `AppendixStore.TYPES`（全部占位）。
  - 附件 jpg/jpeg/png/gif/webp/pdf，图片上传只收前五种，日志上传 zip/log/txt；单个不超过 10MB、不能为空；一批最多 9 个，整批校验通过才登记。
  - `appendix/remove` 在 APK 里是 GET 但会删除数据，本地照做。
- **留言板**（`MessageBoardStore`）：search 在入库单、出库单、退件单里按单号 / 来源单号 / 跟踪号查找。
  reply 的 orderType 1 入库单 / 2 出库单 / 3 退件单（占位）；内容 1–500 字，单据必须存在、客户一致，附件 fileKey 必须已上传。
- **登录**：`AuthController` 网页端与 PDA 共用，同时接受 account 与 loginAccount，不校验也不保存密码；getUserInfo 同时返回网页端占位字段和 APK 的 UserInfo 字段，
  PDA 的 `Authorization: Bearer xxx` 会去掉前缀。
- **首页**：receiptCount = 未收完的入库单，putAwayCount = 未上完的上架单，outBoundCount = 未出库的出库单（统计口径占位）。
- **菜单**：一个根节点 code=PDA（占位），下挂 8 个子菜单，编码 114001–114008 取自 APK 的 MenuPermissionCode，中文名按枚举名翻译。
- **仓库与参数**：仓库 WH01、WH02（虚构）。参数项 itemNo / paramsNo 取自 APK 的 SettingConstants，取值全部占位（见 `PdaMiscController.PARAMS`）；
  listParams 对未知参数项返回 itemValue=null，findParams 对未知参数项报错。这些参数目前**不影响**收货、复核等流程。
- **版本检查**：最新版本占位为 2.0.0；code 1 有新版本 / 0 已是最新（占位），没有安装包，url 为空。查询参数名 version 按 APK 常量推断。
- **按箱移库**（`/blMoveStock/boxMoveStock`）：boxList（按箱，数量为箱数）、skuList、returnList 可混合提交；先整批校验
  （库位存在、目标未锁定、源目标不同、客户一致、FNSKU 一致、同一来源的数量合计不超过可用数、次品只能去 E 区），全部通过才执行。

## 批次 8 的数据与规则（通用列表页增删改）

- 可编辑的资源在 `tools/list-pages/spec.mjs` 的 `editable` 集合里：customer、seedWall、pickCart、account、role、channel、channelGroup、
  exchangeRate、notice、waveRule、fuelPlan、zoneTemp、surcharge、freightTemp、logisticsPriceSetting、operateBill、warehouseRent、
  quotationScheme、billTemplate、printTemplate、brand、agency、receiveAccount。
- 接口（占位）：`POST /gateway/wms/{key}/add`（返回 `{id}`）、`/update`（请求体带 id，只改传了的字段）、`/delete`（`{ids:[...]}`，任一不存在则整批不删），
  在 `list/GenericEditController`。只读资源调用返回“该页面只读，不支持编辑”。只写本地库 lp_* 表，后端重启后恢复示例数据。
- 校验（本项目假设）：关键字列与枚举列必填；文本最长 128；整数 0~1000000；金额/数值 0~1e8 且最多 2 位小数，汇率类最多 6 位；枚举值必须在可选范围内。
  第一个关键字列若在示例数据中唯一，就作为唯一键（如客户代码、渠道代码）；汇率、物流报价设置没有唯一键。
- 时间列不在表单里：createTime / publishTime 新建时填当前时间，updateTime 每次保存都填，其他时间列（如最近登录）新建时为空、编辑时不变。
- 新建 ID 取该资源 ID 区间（idBase 起 1e13 宽）内的最大值 + 1。
- 前端 `GenericList.vue`：可编辑页面显示“新建”“删除”按钮、勾选列和“编辑 / 删除”操作列，表单由列定义生成；后端未连通、用内置 mock 时不显示编辑入口。

## 已知限制（通用列表页）

- 后端未连通时前端退回内置 mock（每页前 5 行），此时只有状态页签会过滤，下拉筛选和关键字不生效。
- 下拉筛选的选项取自虚构数据里出现过的值，不是真实字典。

## 怎么加一个通用列表页

1. 在对应的 `tools/list-pages/spec-*.mjs` 里加一项（key、route、title、idBase、count、columns、row）。
   `idBase` 用 `ID(n)`，n 取下一个未用的编号。`route` 必须是 `frontend/src/router/index.js` 中 `modules` 里的子路由，且不在 `pages` 里。
2. 在项目根目录运行 `node tools/list-pages/gen.mjs`，会重新生成建表、数据、注册表、前端配置和 `backend/src/test/resources/list-expected.csv`。
3. 跑 `mvn test`。不需要手改测试。
4. 如果这一页要能增删改，把 key 加进 `spec.mjs` 的 `editable`，并把 `GenericEditControllerTest` 里的可编辑资源数（目前 23）改掉。
