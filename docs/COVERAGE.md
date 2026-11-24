# 功能覆盖度与分批计划

目标是把重建版补到覆盖原 WMS 的全部模块。约束不变（见 CLAUDE.md）：数据全部虚构、不连生产、
网页端请求体没抓到，所以网页端的接口路径、请求体字段与状态编码大多是**本项目占位**；PDA 端路径与请求体字段来自 APK 静态还原。

状态说明：✅ 专门实现 · 🟦 通用列表页（`tools/list-pages/spec.mjs` 生成，只读） · ⬜ 占位（显示"待实现"）

## 汇总（2026-11-24）

| 范围 | 总数 | ✅ | 🟦 | ⬜ |
|---|---|---|---|---|
| 网页端子路由 | 79 | 11 | 15 | 53 |
| PDA 接口（ApiService） | 175 | 27 | — | 148 |

## 网页端（按 router 模块）

| 模块 | 子路由 | 状态 |
|---|---|---|
| customer 客户 | list 客户列表 | 🟦 |
| | recharge 充值审核 | 🟦 |
| globalOrder 全局订单 | asn / parcel / freight / returnOrder / directMailOrder / directMailBox / claim / workOrder | ⬜ ×8（批次 2） |
| stock 库存 | sku / box / return | ✅ ×3 |
| basicData 基础数据 | product / area / cell / tray / packMaterial | ✅ ×5 |
| | seedWall 播种墙、pickCart 拣货车 | 🟦 ×2 |
| inbound 入库 | ASN | ✅ |
| | arrivalScan / deliveryGoods / receivePutaway / palletReceive / shelf / palletForming / newProduct / inventoryClaim | ⬜ ×8（列表类批次 2，作业类批次 3） |
| outbound 出库 | parcel / wave | ✅ ×2 |
| | abnormal 异常订单、intercept 拦截订单 | 🟦 ×2 |
| | freight / waveRule / secondSort / review / boxReview / weight / scanForm / split / swapOrder | ⬜ ×9 |
| returnGoods 退货 | warehousing 退货入库 | 🟦 |
| | rebrand / outbound | ⬜ ×2 |
| logisticsManage 物流 | channel 渠道、channelGroup 渠道分组 | 🟦 ×2 |
| logisticsPrice 物流报价 | fuelPlan / zoneTemp / surcharge / freightTemp | ⬜ ×4 |
| quotesReceivable 报价 | logisticsPriceSetting / operateBill / warehouseRent / quotationScheme | ⬜ ×4 |
| receivableSettlement 结算 | businessExpenses / billsReceivable / expend / billTemplate | ⬜ ×4 |
| statisticalAnalysis 统计 | inventoryAgeAnalysis | ⬜ |
| auth 权限 | account 账号、role 角色 | 🟦 ×2 |
| setting 设置 | exchangeRate 汇率、notice 公告 | 🟦 ×2 |
| | globalSetting / printTemplate / brand / agency / erpAuth / msgNotice / warningMsg / receiveAccount | ⬜ ×8 |
| toolKit 工具箱 | requestLog 请求日志、taskCenter 任务中心 | 🟦 ×2 |
| | freightCalculation / errorPool / labelRecord / pushRecord / zoneTest | ⬜ ×5 |

## PDA 端（按路径一级模块）

| 模块 | 接口数 | 已实现 | 说明 |
|---|---|---|---|
| receipt 收货 | 22 | 4 | scanPallet / getBoxTypeInfo / skuReceipt / wholeOrderReceipt |
| shelf 上架 | 24 | 4 | v2 scanBarcode / getRecommendCell / confirm / box/submitShelf |
| return 退货收货/上架/创建 | 24 | 6 | putaway 6 个（destruct 未做） |
| blReturnIn FBA 退货 | 24 | 0 | |
| wave 波次拣货 | 16 | 8 | 按件拣货；按箱拣货 boxPick 系列未做 |
| stock 库内 | 10 | 0 | 查询、移库、调整、盘点 |
| device 设备任务 | 9 | 0 | |
| delivery 出库确认 | 8 | 0 | |
| review 复核 | 6 | 5 | getCustomerCode4More 未做 |
| inventoryCheck 盘点 | 6 | 0 | |
| newProduct 新品维护 | 5 | 0 | |
| pickAndSort 边拣边分 | 4 | 0 | |
| appendix 附件 | 4 | 0 | 上传类，本地只做假实现 |
| 其他（auth/home/menu/warehouse/system/messageBoard/common/blReceipt/blMoveStock） | 13 | 0 | |

## 分批计划

1. **批次 1（已完成）**：通用只读列表页机制 + 15 页（客户、充值、播种墙、拣货车、账号、角色、渠道、渠道分组、汇率、公告、请求日志、任务中心、退货入库、异常订单、拦截订单）。
2. **批次 2**：剩余“列表类”网页页面全部走通用机制（globalOrder ×8、物流报价 ×4、报价 ×4、结算 ×4、设置 ×7、工具箱 ×3、入库/出库/退货中的列表页），目标网页端覆盖到 ~70/79。
3. **批次 3**：网页端“作业/配置类”页面（收货上架、复核、称重、二次分拣、运费试算、分区测试、全局设置、库龄分析），以及左侧菜单布局与本地登录。
4. **批次 4**：PDA 库内与出库补全——stock ×10、inventoryCheck ×6、delivery ×8、wave 按箱拣货 ×8、review 剩余 1，接入现有 `OutboundSampleStore` 的状态流转。
5. **批次 5**：PDA 收货/上架补全——receipt ×18、shelf ×20、newProduct ×5。
6. **批次 6**：PDA 退货——return receive/create ×17、destruct、blReturnIn ×24。
7. **批次 7**：其他 PDA（device、pickAndSort、appendix、messageBoard、auth/home/menu/warehouse/system）与网页端本地增删改（只写本地库）。

## 怎么加一个通用列表页

1. 在 `tools/list-pages/spec.mjs` 的 `resources` 里加一项（key、route、title、idBase、columns、count、row）。
   `idBase` 要小于 9e15（JS 安全整数），且与已有项不重叠。`route` 必须是 `frontend/src/router/index.js` 里 `modules` 中的子路由。
2. 项目根目录运行 `node tools/list-pages/gen.mjs`，重新生成 `schema-list.sql`、`data-list.sql`、`list-resources.json`、`listPages.generated.js`。
3. 在 `GenericListControllerTest.everyResourcePages` 里加一行期望总数，跑 `mvn test`。
