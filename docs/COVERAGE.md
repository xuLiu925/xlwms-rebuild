# 功能覆盖度与分批计划

目标：把重建版补到覆盖原 WMS 的全部模块。约束不变（见 CLAUDE.md）：数据全部虚构、不连生产。
网页端请求体没抓到，所以网页端的接口路径、请求体字段与状态编码大多是**本项目占位**；PDA 端的路径与请求体字段来自 APK 静态还原。

状态说明：✅ 专门实现 · 🟦 通用列表页（规格在 `tools/list-pages/spec*.mjs`，只读） · ⬜ 占位（页面显示"待实现"）

## 汇总（2026-11-24，批次 5 完成后）

| 范围 | 总数 | ✅ | 🟦 | ⬜ |
|---|---|---|---|---|
| 网页端子路由 | 79 | 21 | 58 | 0 |
| PDA 接口（ApiService） | 175 | 104 | — | 71 |

PDA 的 175 是 ApiService 方法数；其中 `receipt/listBoxType` 有两个方法共用一个路径，按方法计 2 个。

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
| receipt 收货 | 22 | 22 | 批次 1 的 4 个是无状态骨架（`ReceiptController`）；批次 5 补 18 个（`PdaReceiptFlowController` + `ReceiptStore`） |
| shelf 上架 | 24 | 24 | 批次 1 的 4 个 /v2 是无状态骨架（`ShelvesController`）；批次 5 补 20 个（`PdaShelfFlowController` + `PutawayStore`，含 /shelf/selectCell） |
| return 退货收货/上架/创建 | 24 | 6 | putaway 6 个（destruct 未做） |
| blReturnIn FBA 退货 | 24 | 0 | |
| wave 波次拣货 | 16 | 16 | 按件拣货 8 个（`WaveController`）+ 按箱拣货 8 个（批次 4，`WaveBoxController`） |
| stock 库内 | 10 | 10 | 批次 4，`PdaStockController` + `StockStore`：查询、调整、移库、整库位移库 |
| device 设备任务 | 9 | 0 | |
| delivery 出库确认 | 8 | 8 | 批次 4，`PdaDeliveryController`：出库确认 3 个 + 大货复核 5 个 |
| review 复核 | 6 | 6 | 批次 4 补了 getCustomerCode4More |
| inventoryCheck 盘点 | 6 | 6 | 批次 4，`PdaInventoryCheckController` + `InventoryCheckStore` |
| newProduct 新品维护 | 5 | 5 | 批次 5，`PdaNewProductController` + `NewProductStore` |
| pickAndSort 边拣边分 | 4 | 0 | |
| appendix 附件 | 4 | 0 | 上传类，本地只做假实现 |
| 其他（auth/home/menu/warehouse/system/messageBoard/common/blReceipt/blMoveStock） | 13 | 1 | 批次 5 做了 blReceipt/measureBoxType |

## 分批计划

1. **批次 1（已完成）**：通用只读列表页机制 + 15 页。
2. **批次 2（已完成）**：剩余列表类网页页面 43 页，网页端覆盖到 69/79。规格按模块拆成 `spec-orders`、`spec-finance`、`spec-settings`，
   生成器加了自检（key/route 唯一、ID 区间不重叠、ID 不超过 JS 安全整数、每个资源最多一个页签列），测试期望值也由生成器输出。
3. **批次 3（已完成）**：剩余 10 个网页端作业/配置/工具页，左侧菜单布局（`views/layout/Layout.vue`，菜单由路由表生成），
   本地演示登录（`/auth/login`，不校验不保存密码），以及不依赖 MySQL 的 H2 本地运行方式（`.claude/launch.json` 的 backend-h2）。网页端 79/79 都有页面。
4. **批次 4（已完成）**：PDA 库内与出库补全 33 个接口：stock ×10、inventoryCheck ×6、delivery ×8、wave 按箱拣货 ×8、review 剩余 1。
   PDA 端覆盖到 60/175。规则见下方“批次 4 的数据与规则”。
5. **批次 5（已完成）**：PDA 收货/上架/新品维护 44 个接口（receipt ×18 方法、shelf ×20、newProduct ×5、blReceipt/measureBoxType），PDA 覆盖到 104/175。
   **遗留**：批次 1 的 8 个收货/上架接口仍是无状态骨架，没有接到 `ReceiptStore`/`PutawayStore`，后续应改成有状态实现。
6. **批次 6**：PDA 退货：return receive/create ×17、destruct、blReturnIn ×24。
7. **批次 7**：其他 PDA 接口（device、pickAndSort、appendix、messageBoard、auth/home/menu/warehouse/system），以及网页端的本地增删改（只写本地库）。

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

- **4 张示例入库单**：RA2611000001 按箱预报（3 个箱唛，含新品 SKU-0012）；RA2611000002 按箱型预报；RA2611000003 按箱型、中转入库；
  RA2611000004 按箱预报、托盘收货（含新品 SKU-0013）。forecastType 1 按箱 / 2 按箱型；inboundType 1 普通 / 2 中转。
- **收货**：状态沿用 10 在途 / 11 收货中 / 15 已收货；不允许超收；一个箱唛只收一次；“完成收货”接口允许少收后直接置为 15。
  带了 rowVersion 就必须与当前一致（每次变更加 1）。多箱 / 多托盘提交先全部校验再收货，任一项不通过则整体不生效；
  `batchReceipt/submit` 和 `measureBoxType/batch` 按单处理，失败的单写进返回列表，成功的单照常生效。
- **上架**：putawayType 1 按 SKU / 2 按箱型 / 3 整托。按 SKU 上架只累计数量；整箱上架要求箱内每个 SKU 的待上架数都够，
  避免同一批货上架两次。`commitCompleteOrder` 把剩余全部上架到一个库位。`batch/submit` 里的 expectedPendingPutawayQty 与服务端不一致时该单失败（提示刷新）。
  推荐库位：已有该 SKU 库存的库位优先，否则第一个空库位。锁定库位 E-09-09 一律拒绝。
- **托盘**：托盘号在上架完成前不能被另一次收货复用；`preCheckPalletList` 返回目标库位上已有其他托盘的冲突，空列表表示可提交。
- **新品**：SKU-0012、SKU-0013 初始为待测量；`receiptMeasure` 先校验全部再记录（0~300 cm、0~500 kg），测量后从待维护清单移除。

## 已知限制（通用列表页）

- 后端未连通时前端退回内置 mock（每页前 5 行），此时只有状态页签会过滤，下拉筛选和关键字不生效。
- 下拉筛选的选项取自虚构数据里出现过的值，不是真实字典。

## 怎么加一个通用列表页

1. 在对应的 `tools/list-pages/spec-*.mjs` 里加一项（key、route、title、idBase、count、columns、row）。
   `idBase` 用 `ID(n)`，n 取下一个未用的编号。`route` 必须是 `frontend/src/router/index.js` 中 `modules` 里的子路由，且不在 `pages` 里。
2. 在项目根目录运行 `node tools/list-pages/gen.mjs`，会重新生成建表、数据、注册表、前端配置和 `backend/src/test/resources/list-expected.csv`。
3. 跑 `mvn test`。不需要手改测试。
