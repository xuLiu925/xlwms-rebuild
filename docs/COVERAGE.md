# 功能覆盖度与分批计划

目标：把重建版补到覆盖原 WMS 的全部模块。约束不变（见 CLAUDE.md）：数据全部虚构、不连生产。
网页端请求体没抓到，所以网页端的接口路径、请求体字段与状态编码大多是**本项目占位**；PDA 端的路径与请求体字段来自 APK 静态还原。

状态说明：✅ 专门实现 · 🟦 通用列表页（规格在 `tools/list-pages/spec*.mjs`，只读） · ⬜ 占位（页面显示"待实现"）

## 汇总（2026-11-24，批次 3 完成后）

| 范围 | 总数 | ✅ | 🟦 | ⬜ |
|---|---|---|---|---|
| 网页端子路由 | 79 | 21 | 58 | 0 |
| PDA 接口（ApiService） | 175 | 27 | — | 148 |

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

1. **批次 1（已完成）**：通用只读列表页机制 + 15 页。
2. **批次 2（已完成）**：剩余列表类网页页面 43 页，网页端覆盖到 69/79。规格按模块拆成 `spec-orders`、`spec-finance`、`spec-settings`，
   生成器加了自检（key/route 唯一、ID 区间不重叠、ID 不超过 JS 安全整数、每个资源最多一个页签列），测试期望值也由生成器输出。
3. **批次 3（已完成）**：剩余 10 个网页端作业/配置/工具页，左侧菜单布局（，菜单由路由表生成），
   本地演示登录（，不校验不保存密码），以及不依赖 MySQL 的 H2 本地运行方式（ 的 backend-h2）。网页端 79/79 都有页面。
4. **批次 4**：PDA 库内与出库补全：stock ×10、inventoryCheck ×6、delivery ×8、wave 按箱拣货 ×8、review 剩余 1，接入现有 `OutboundSampleStore` 的状态流转。
5. **批次 5**：PDA 收货/上架补全：receipt ×18、shelf ×20、newProduct ×5。
6. **批次 6**：PDA 退货：return receive/create ×17、destruct、blReturnIn ×24。
7. **批次 7**：其他 PDA 接口（device、pickAndSort、appendix、messageBoard、auth/home/menu/warehouse/system），以及网页端的本地增删改（只写本地库）。

## 批次 3 页面的数据与规则

| 页面 | 后端 | 状态存放 | 说明 |
|---|---|---|---|
| 收货上架、上架 |   |  内存 | 3 张虚构入库单；上架库位查本地 basic_cell，锁定库位或跨仓库位会被拒 |
| 二次分拣、复核、按箱复核、称重出库 |   |  内存 | 新增已拣完的多品波次 WV26110004；称重要求已复核，称重后即出库 |
| 库龄分析 |   | 读 stock_sku | 表里没有入库日期，库龄由 SKU 哈希推算，仅演示 |
| 全局设置 |   | 内存 | 10 个假设的配置项，按类型和范围校验，校验失败整体不生效 |
| 运费试算、分区测试 |  +  | 无状态 | 渠道、费率、材积除数、分区规则全部虚构 |
| 登录 |   | 内存令牌 | 账号密码非空即可；不校验、不保存密码；其他接口不鉴权 |

所有路径与字段都是本项目占位，真实网页接口未抓取。

## 已知限制（通用列表页）

- 后端未连通时前端退回内置 mock（每页前 5 行），此时只有状态页签会过滤，下拉筛选和关键字不生效。
- 下拉筛选的选项取自虚构数据里出现过的值，不是真实字典。

## 怎么加一个通用列表页

1. 在对应的 `tools/list-pages/spec-*.mjs` 里加一项（key、route、title、idBase、count、columns、row）。
   `idBase` 用 `ID(n)`，n 取下一个未用的编号。`route` 必须是 `frontend/src/router/index.js` 中 `modules` 里的子路由，且不在 `pages` 里。
2. 在项目根目录运行 `node tools/list-pages/gen.mjs`，会重新生成建表、数据、注册表、前端配置和 `backend/src/test/resources/list-expected.csv`。
3. 跑 `mvn test`。不需要手改测试。
