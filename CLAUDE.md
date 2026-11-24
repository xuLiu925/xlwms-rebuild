# CLAUDE.md — xlwms-rebuild 交接说明

## 项目目标
对某海外仓 WMS（网页端 wms.xlwms.com 与 PDA 安装包 com.lingxing.wms）的**对外可见接口约定**做独立重建：
Java 后端（Spring Boot 3 / Java 17）+ Vue 2 前端。当前全部是虚构示例数据的骨架，不含厂商源码。

## 最高优先级：生产环境安全（必须遵守）
- 用户登录的是**公司生产账号**，任何写入都可能造成真实损失。
- 绝不对 wms.xlwms.com 或其网关发起任何会改数据的请求；不点击页面里的新建/提交/导入/打印/生成波次/扫描/拣货等按钮；
  不主动调用 PDA 的 175 个接口（大量是写操作，见 docs/PDA_API.md）。
- GET 不等于只读；`docs/PDA_API.md` 里的 `read?` 只是按方法名推断。
- 验证接口行为只能用测试租户/沙箱，或离线分析文件。不要在生产账号上登录 PDA 安装包试。
- 不把真实业务数据（客户名、订单、库存、收件人、金额）写进代码、文档或提交。示例数据一律虚构。
- 不输入、不保存任何账号密码或 token。

## 已完成
- 站点：登录页与前端为 Vue 2 + Element UI（webpack 分包，无 sourcemap）；业务接口前缀 `/gateway/wms`，响应信封 `{code:200,data,msg:"操作成功"}`；
  分页为 `data.records[]`；19 位 ID 以字符串返回。后端很可能是 Spring Boot + MyBatis-Plus（推断，无直接证据）。
- 已确认的网页接口：首页 `/home/{stockSummary,todoData,balance,order/count/analysis}`；入库 `/blAsn/{status/statistics,queryInTotal,page}`；
  一件代发 `/blDelivery/{queryInTotal,page}`、`/productInfo/batchQuery`；库存 `/stockSku/{page,skuTotal}`。请求体参数**未抓取**。
- 状态码：入库 10 在途/11 收货中/15 已收货/30 已上架/90 已取消。
- PDA 安装包：5 个 dex，Retrofit `ApiService` 共 175 个接口（POST 96/GET 79），路径与参数类型在 `docs/PDA_API.md`；
  请求头相关字符串：Authorization、App-Id、appKey、appVersion、Channel/X-Client-Channel、Client-Type、Device-Type、deviceId、whCode；
  `ClientChannelSigner` 含 nonce，疑似请求签名，**算法未还原**。
- 签名与请求头已还原（2026-11-24，见 `docs/PDA_SIGNING.md`）：真正的 API 头只有 `Authorization: Bearer`、
  `Content-Type`、`Origin`、`terminalType: pda`、`Referer: .../wms-pda-web-ui/`、语言头，以及两个签名头
  `X-Client-Channel`（HmacSHA256，ClientChannelSigner）和 `Track-Key`（v2，TrackKeyGenerator）。
  旧文档里的 App-Id/appKey/appVersion/Channel/Client-Type/Device-Type/deviceId **是埋点 SDK 字段，不是 API 头**。
  签名密钥写死在 APK BuildConfig（本机 _private 可查），**未写入本仓库**。
  请求体字段：`data.model.bean` 下 71 个 `*Req` 类共 434 字段已导出到 `docs/PDA_REQUEST_BODIES.md`。
- 代码：`backend/`（统一信封、Long→String、首页/入库/库存三组示例接口 + 入库收货 `ReceiptController`、5 个测试）；`frontend/`（路由表、登录与首页、入库管理页、API 封装）。
- 入库收货 DTO（2026-11-24）：`backend/.../dto/receipt/` 下按反编译字段建了 `WholeOrderReceiptReq`、
  `PalletReceiptScanReq`、`SkuForecastReceiptReq`（含 Sku/SkuPallet/PalletSku 内部类）、`BoxTypeInfoReq`；
  当初由 `ReceiptController` 实现 `/gateway/wms/pda/receipt/{scanPallet,getBoxTypeInfo,skuReceipt,wholeOrderReceipt}` 的无状态骨架；
  该类已在批次 5 之后删除，这 4 个接口并入 `PdaReceiptFlowController`（见下文）。真实路径取自 ApiService（`/gateway/wms/pda/...`，与网页端 `/gateway/wms` 同网关）。
- 上架 DTO（2026-11-24）：`dto/shelves/` 下 `SkuShelvesReq`（含 SkuShelvesItem）、`BoxShelvesReq`（含 BoxShelvesItem）；
  当初的 `ShelvesController`（`/pda/shelf/v2/{scanBarcode,getRecommendCell/v2,confirm,box/submitShelf}` 骨架）同样已删除，并入 `PdaShelfFlowController`。
- 退货上架 DTO（2026-11-24）：`dto/returnputaway/` 下 `ReturnPutawayScanReq`、`ShelvesInfoReq`（含 ShelvesSubmit）、`Appendix`；
  当初的 `ReturnPutawayController` 骨架已在批次 6 删除，退货上架（含本地模拟的 destruct）并入 `ReturnFlowController`。
- 只读列表页（2026-11-24）：前端 `views/outbound/Parcel.vue`（一件代发，状态页签+筛选+分页）、`views/stock/Sku.vue`（产品库存，汇总卡片+筛选+分页），
  路由里用 `pages` 映射已实现页面。后端 `DeliveryController`（`/blDelivery/{queryInTotal,page}`）与 `StockController` 改为内存分页虚构数据（`common/PageUtil`）。
  **注意**：这两组的请求体字段（keyword/customerCode/status/current/size）、records 字段名、一件代发状态编码（10/20/30/40/50/80/90）都是本项目占位，真实值未抓取。
  本地运行配置在 `.claude/launch.json`（后端 8080、前端 8081）。测试共 16 个通过。
- 库存只读页补充（2026-11-24）：新增按箱库存 `views/stock/Box.vue` 与退货库存 `views/stock/Return.vue`，
  路由 `pages` 注册 `stock/box`、`stock/return`。后端 `StockBoxController`（`/gateway/wms/stockBox/{page,boxTotal}`）、
  `StockReturnController`（`/gateway/wms/stockReturn/{page,returnTotal}`），对应实体/Mapper/表 `stock_box`(18 行)、`stock_return`(15 行)，
  走 MyBatis-Plus + 本地 MySQL/H2，全部虚构数据。**注意**：这两组的接口路径、请求体字段、退货状态编码
  （10 待上架/20 已上架/30 待销毁/40 已销毁）都是本项目占位，真实接口未抓取。测试共 26 个通过。
- 基础数据只读页（2026-11-24）：新增 5 个 `views/basicData/` 页——产品资料 `Product.vue`、库区 `Area.vue`、库位 `Cell.vue`、
  托盘 `Tray.vue`、包材 `PackMaterial.vue`，路由 `pages` 注册 `basicData/{product,area,cell,tray,packMaterial}`。
  后端 `Basic{Product,Area,Cell,Tray,PackMaterial}Controller`（`/gateway/wms/basic{Product,Area,Cell,Tray,PackMaterial}/page`），
  对应实体/Mapper/表 `basic_product`(20)、`basic_area`(6)、`basic_cell`(24)、`basic_tray`(15)、`basic_pack_material`(10)，
  走 MyBatis-Plus + 本地 MySQL/H2，全部虚构数据。前端 `api/basic.js`、`mock/basic.js` 配套。
  **注意**：接口路径、请求体字段与各状态/类型编码（产品 0 停用/1 启用、库位占用 0 空闲/1 占用/2 锁定、托盘 0 空闲/1 在用等）均为本项目占位，真实接口未抓取。测试共 31 个通过。
- 出库作业（2026-11-24）：按 APK 还原波次拣货 `/pda/wave/{pickList,page,pageCell,skuDetail,scanCell,scanSku,pick,queryIntercepting}`
  与复核 `/pda/review/{get4piecePackage,review4packageV2,get4MorePiece,morePieceV2,confirmDelivery}`（`WaveController`、`ReviewController`，
  DTO 在 `dto/wave`、`dto/review`，用 Java record）。响应字段按 APK 的 WavePageBean/PiecePackageBean/MorePieceBean/PutAwayStatus 等还原。
  数据在 `service/OutboundSampleStore`（内存、有状态：拣完→可复核→复核完→可出库，重启恢复）。业务异常 `BizException` → `{code:500,msg}`（错误码为占位）。
  网页端 `views/outbound/Wave.vue` 对接**占位路径** `/wave/{statistics,page}`（真实网页接口未抓取）；`/pdaDemo` 是本地 PDA 作业模拟页，
  `/demo/outbound/reset` 是本地演示专用接口。波次/出库单状态编码、拣货方式编码、流转规则都是本项目假设。测试共 22 个通过。
- 全功能补全计划（2026-11-24）：用户要求把重建版补到覆盖原 WMS 全部模块，清单与分批计划见 `docs/COVERAGE.md`
  （网页端 79 个子路由、PDA 175 个接口逐项标状态）。批次 1 已完成：通用只读列表页机制——
  规格 `tools/list-pages/spec.mjs` → `node tools/list-pages/gen.mjs` 生成 `db/schema-list.sql`、`db/data-list.sql`（`lp_*` 表）、
  `db/list-resources.json`、`frontend/src/config/listPages.generated.js`；后端 `list/GenericListController`
  （`POST /gateway/wms/{key}/{page,statistics}`，标识符只取自注册表，值走绑定参数，专门 Controller 的路径优先），
  前端 `views/common/GenericList.vue`，路由对未在 `pages` 中的子路由自动套用。已上 15 页（见 COVERAGE.md）。
  这些页的路径、字段、状态编码全部是本项目占位。
- 批次 2（2026-11-24）：再加 43 个通用列表页（全局订单 8、入库 6、出库 5、退货 2、物流报价 4、应收报价 4、应收结算 4、设置 7、工具箱 3），
  网页端覆盖 69/79。规格拆为 `spec.mjs`（批次 1 + 汇总）、`spec-orders/finance/settings.mjs`，公共工具在 `helpers.mjs`（`ID(n)` 分配 ID）。
  生成器新增 `num` 类型、规格自检，并输出 `backend/src/test/resources/list-expected.csv` 供参数化测试读取。测试共 98 个通过。
  剩余 10 个网页页面是作业/配置类（见 COVERAGE.md 批次 3）。
- 批次 3（2026-11-24）：网页端 79/79 都有页面。新增左侧菜单布局 `views/layout/Layout.vue`（菜单由 router 的 modules 生成，
  中文名在 `config/menu.js`）、本地演示登录 `AuthController`（不校验不保存密码，其他接口不鉴权）、10 个作业/配置/工具页：
  收货上架/上架（`InboundWorkStore` 内存 + basic_cell 库位校验）、二次分拣/复核/按箱复核/称重出库（扩展 `OutboundSampleStore`，
  新增已拣完多品波次 WV26110004）、库龄分析、全局设置、运费试算、分区测试（`ZoneCalculator`，规则虚构）。细节见 COVERAGE.md。
  **不装 MySQL 也能跑后端**：`.claude/launch.json` 的 `backend-h2`（带测试类路径 + `src/test/resources/application-h2.yml`），
  已用它和前端联调过登录、复核、称重出库、运费试算、上架、库龄等页面。
- 批次 4（2026-11-24）：PDA 再补 33 个接口，PDA 覆盖 60/175。库内 `/pda/stock/**` ×10（`PdaStockController` + `StockStore`）、
  盘点 `/pda/inventoryCheck/**` ×6（`InventoryCheckStore`，complete 时把实盘数写回库存）、出库确认与大货复核 `/pda/delivery/**` ×8
  （`PdaDeliveryController`）、按箱拣货 ×8（`WaveBoxController` + `FreightStore`，拣货扣减按箱库存）、`review/getCustomerCode4More`。
  路径/参数/字段取自 APK，业务规则和编码是本项目假设（见 COVERAGE.md“批次 4 的数据与规则”）。`POST /demo/reset` 重置全部内存数据。
- 批次 5（2026-11-24）：PDA 再补 44 个接口，PDA 覆盖 104/175。收货 18 个方法 + `blReceipt/measureBoxType`
  （`PdaReceiptFlowController` + `ReceiptStore`，4 张虚构入库单）、上架 20 个（`PdaShelfFlowController` + `PutawayStore`，上架写入 `StockStore`）、
  新品维护 5 个（`PdaNewProductController` + `NewProductStore`）。收货自动生成上架单。`StockStore` 新增新品 SKU-0012/0013 与 `putIn`。
  批次 1 的 8 个收货/上架骨架接口随后已接入 Store（删除 `ReceiptController`、`ShelvesController`，并入两个 Flow 控制器，
  返回类型改为 APK 的 ScanPalletStatus / BoxTypeInfoRsp / String / RecommendCellListBean / PutAwayStatus）；
  为让 skuReceipt 有真实数据，新增第 5 张入库单 RA2611000005（forecastType 3 按 SKU 预报）。至此 PDA 收货/上架接口全部有状态。
  规则见 COVERAGE.md“批次 5 的数据与规则”。
- 批次 6（2026-11-24）：PDA 退货 42 个接口，PDA 覆盖 146/175。退件 `/pda/return/{receive,create,putaway}/**` 共 24 个
  （`ReturnFlowController` + `ReturnStore`，含本地模拟的 destruct，原退货上架骨架删除）；FBA 退货 `/pda/blReturnIn/**` 24 个
  （`FbaReturnController` + `FbaReturnStore`：签收、清点、上架、认领、退货拣货）。新规则：次品只能上 E 区（`StockStore.requireQualityCell`）。
  规则见 COVERAGE.md“批次 6 的数据与规则”。测试共 122 个通过（`mvn clean test`；删测试类后不 clean 会把旧报告算进去）。
  变异检查：去掉次品库位规则后有 2 个用例失败。
- 批次 7（2026-11-24）：其余 29 个 PDA 接口，**PDA 覆盖 175/175**。设备任务 9 个（`PdaDeviceController` + `DeviceStore`，
  设备库位 B-01-01/B-01-03，B-01-03 模拟异常，resolve 后放行）、边拣边分 4 个（`PickAndSortController` + `PickAndSortStore`，
  绑车后按格分配，拣货数写回 `OutboundSampleStore` 波次）、附件 4 个 + `/gateway/common/pic/upload`（`AppendixController` + `AppendixStore`，
  只记元数据、不存文件）、留言板 2 个（`MessageBoardController` + `MessageBoardStore`）、`PdaMiscController`（首页、菜单、仓库、参数、版本检查、
  按箱移库 `/blMoveStock/boxMoveStock`，整批校验后执行）。PDA 登录 `/auth/{login,getUserInfo}` 与网页端共用 `AuthController`
  （同时接受 account / loginAccount，去掉 `Bearer ` 前缀）。规则见 COVERAGE.md“批次 7 的数据与规则”。
  测试共 129 个通过（`mvn clean test`）。变异检查：把按箱移库的“同一来源合计”改成逐行检查后 1 个用例失败。
- 批次 8（2026-11-24）：网页端通用列表页本地增删改。23 个基础资料/报价/设置类页面（`spec.mjs` 的 `editable`）可新建、编辑、删除，
  其余 35 个日志/单据/流水类页面仍只读。后端 `list/GenericEditController`（`POST /gateway/wms/{key}/{add,update,delete}`，占位路径，
  只写本地 lp_* 表，重启恢复），生成器给注册表加了 editable/unique/idBase 与列的 required/auto；前端 `GenericList.vue` 加新建/编辑/删除。
  校验与唯一键规则见 COVERAGE.md“批次 8 的数据与规则”。测试共 136 个通过（`mvn clean test`），已在浏览器里联调过客户列表的新建、编辑、删除。
  `.claude/launch.json` 新增 `backend-h2-alt`（18090，8090 被系统保留）+ `frontend-alt`（8091，`API_TARGET` 指向 18090），用于 8080/8081 被占用时联调。
- 批次 9（2026-11-24）：参考菜单里另有、公开路由表里没有的 9 个页面全部实现，左侧菜单不再有“待实现”占位。
  盘点 `views/stock/Checking.vue`（`InventoryCheckWebController`，与 PDA 盘点共用 `InventoryCheckStore`，新增已取消状态）、
  次品处理 `views/stock/Defective.vue`（`DefectiveController` + `DefectiveStore`，读写 `StockStore` 次品库存）、
  报表 `views/report/{StaffEfficiency,InboundEfficiency,OutboundEfficiency}.vue`（共用 `ReportView.vue`；`ReportController` 按日期确定性推算，仅演示），
  以及 4 个通用列表页（换标、转运、序列号、作业视频，`tools/list-pages/spec-extra.mjs`）。规则见 COVERAGE.md“批次 9 的数据与规则”。
  测试共 148 个通过（`mvn clean test`），已在浏览器里联调过 9 个页面（盘点新建→PDA 盘点→盘盈显示）。
- 批次 10（2026-11-24）：PDA 模拟界面 `/pda`（左侧菜单“PDA 模拟”）。`views/pda/PdaShell.vue` 手机外壳 + `PdaHome`、`PdaReceipt`（按箱/按箱型/按 SKU 收货）、
  `PdaPutaway`（按 SKU 上架、整单上架）、`PdaCheck`（明盘/盲盘）、`PdaStock`（查产品、查库位、移库），接口封装 `api/pda.js`，只连本地后端。
  界面是自绘示意，不是原样复刻。后端无改动，测试仍 148 个。细节见 COVERAGE.md“批次 10：PDA 模拟界面”。
- 批次 11（2026-11-24）：PDA 模拟补 `PdaReturnReceive`（退件收货：包裹到货 + 良次品清点）、`PdaReturnPutaway`（退件上架，次品只能 E 区），
  已联调：收 RN2611000001 → 次品放 A 区被拒 → 放 E-01-01 成功 → 网页端次品处理可见。
- 界面风格（2026-11-24）：参考领星 WMS 的页面样式重做前端外观。用户在浏览器面板里自己登录后，只做了只读观察
  （截图、读计算样式），没有点任何按钮、没有提交，真实数据未写入任何文件。
  - 主题：`frontend/src/styles/element-variables.scss`（Element SCSS 变量，主色 #005bf5），需要开发依赖 `sass@~1.32`、`sass-loader@13`。
  - 全局样式：`styles/global.scss`。控件 36px / 14px，表头 40px，数据行 44px；页签条和查询表单是贴边白条，其余内容在灰底上留 16px，分页接在表格下方。
    规则写在 `.app-main > :not(.dashboard)` 下，自动套用到所有页面；页面里纯文字的 `<h3>` 标题已去掉，页面名由顶部页签显示。
  - 布局：`views/layout/Layout.vue`。侧栏 170px（#060d1e，可收起到 56px），顶部导航 48px（#07183d）带已打开页面的页签。
  - 登录页、首页也已重做。查询区不用外置标签，字段名写在框内提示文字里（通用列表页和 16 个自定义页面）；
    数字框和运费试算、分区测试这类带默认值的表单仍保留外置标签。“重置”按钮用 `btn-reset` 灰底。
  - 品牌：**不使用**领星的名称、标志和 iconfont，用“WMS 重建版”和自绘的 W 方块，图标用 Element 自带的。
  - 菜单分组：`frontend/src/config/menuGroups.js`。前 9 组（入库、出库、退件、转运、工单、报表、FBA退货、库内、基础数据）按参考系统菜单的
    顺序、组名、子菜单名排列（参考账号权限受限，只看到这些），其余模块排在后面。参考菜单里有、路由表没有的 9 个路径自动生成“待实现”占位路由；
    路由表的 79 个页面都必须出现在菜单里（漏了会在控制台警告）。
  - 作业页（2026-11-24）：复核验货、包裹复核、称重、二次分拣、收货上架改为参考系统的左右两栏布局，外壳组件 `views/common/WorkLayout.vue`
    （左栏 300px：标题 24px、提示、48px 扫描框、底部按钮；右栏白色结果区，没数据时显示空状态）。参考页面同样只读观察。
    其余：状态页签数量为灰色小字（`.label-num`），页面开头的 `p.hint` 显示为贴边浅蓝提示条，竖排表单放白色卡片，
    “待实现”占位页为居中空状态；各页局部样式里的 Element 旧配色已换成新配色。
- 官方文档：2026-11-24 搜索未找到领星海外仓 WMS 的公开帮助中心或开放 API 文档（apifox 上搜到的“海外仓开放平台”属于 jcex，不是领星）。

## 未知 / 未验证
- 构建已修通（2026-11-24）：后端 JDK 17 下 `mvn test` 通过；前端 Node 22 下 `npm install && npm run build` 成功（已提交 package-lock.json）。
  注意 `Map.of` 最多 10 对键值，超过请用 LinkedHashMap。前后端已用 H2（backend-h2）联调过，尚未在真实 MySQL 上跑。
- 所有接口的请求体字段、鉴权头名称与值、PDA 请求签名算法。
- 页面真实布局与业务规则。dex 只解析了类/方法签名与注解，方法体未反编译。

## 建议的下一步（按顺序）
1. ~~修通后端 `mvn test` 与前端构建~~（已完成）。
2. ~~jadx 离线反编译还原请求体字段 + 签名/请求头逻辑~~（已完成，见 `docs/PDA_SIGNING.md`、`docs/PDA_REQUEST_BODIES.md`）。
   仍可补：`viewmodel/request` 下 33 个类的交互字段、各 `*Resp/*Rsp` 响应体、嵌套 bean 的内层字段。
3. 按请求体类补全后端 DTO；逐步用 MyBatis-Plus + 本地 MySQL 替换示例数据（仅本地开发库）。
4. 前端把 router 里的占位页逐个实现，先做只读列表页。

## 数据层（2026-11-24：示例数据改为本地 MySQL + MyBatis-Plus）
- 已迁的只读列表页数据集：`stock_sku`（库存）、`bl_delivery`（一件代发）、`bl_asn`（入库），后补 `stock_box`（按箱库存）、`stock_return`（退货库存）。
  对应 `entity/`、`mapper/`（BaseMapper）、`StockController`/`DeliveryController`/`InboundController` 改为 MyBatis-Plus 查询 + 分页插件。
  PDA 收货/上架/退货/波次仍是回显骨架，未入库。
- 建表与灌数据：启动时 `spring.sql.init` 跑 `resources/db/schema.sql` + `data.sql`（UTF-8，全部虚构，每次启动重建）。
- 连接：库 `xlwms_dev`、`createDatabaseIfNotExist=true`；账号用环境变量 `${MYSQL_USER:root}` / `${MYSQL_PASSWORD:}`，
  **密码不写进仓库**。本机 root 有密码，跑前先设环境变量，例如：
  `MYSQL_PASSWORD=xxx mvn spring-boot:run`（Git Bash）或在 PowerShell `$env:MYSQL_PASSWORD='xxx'` 再 `mvn spring-boot:run`。
- 测试：`mvn test` 用 H2（MySQL 兼容模式，`IGNORECASE=TRUE`）复用同一套 schema/data，不依赖真实 MySQL；共 22 个测试通过。
- 未做：没能在真实 MySQL 上实跑验证（缺 root 密码，按规则没要）；逻辑已由 H2 同构验证。

## 本机环境（用户电脑，Windows）
- 项目：`D:\java\xlwms-rebuild`；JDK 17/21 在 `D:\java\java-17.0.1`、`D:\java\jdk-21`；Maven `D:\java\apache-maven-3.9.9`；
  本地 MySQL 8.3 `D:\java\mysql-8.3.0-winx64`；Node `D:\java\nodejs`；jadx 相关工具在 `~/tools/jadx-headless-mcp`。
- 用户用 Git Bash：路径要写 `/d/java/...`，**不要写反斜杠路径**（上次因此在 `D:\java` 误建了 .git，需确认已删除：`ls /d/java/.git` 应不存在）。
- 该目录所有者显示为系统账户，git 会报 dubious ownership。只对项目目录加信任：
  `git config --global --add safe.directory D:/java/xlwms-rebuild`，不要对 `D:/java` 或 `*` 加。
- 仓库：`xuLiu925/xlwms-rebuild`，**是公开仓库**。2026-11-24 用户明确决定保持公开并继续推送。推送前仍要扫一遍待推送的提交：
  不能含 `_private/`、APK、签名密钥、令牌、账号密码、个人信息或真实业务数据（客户名、单号、库存、仓库名等）。
- PDA 安装包与反编译产物放 `_private/`，已在 .gitignore 中，不要提交。

## 可直接用的提示词
- 修通构建：「读 CLAUDE.md。在 backend 跑 mvn test，在 frontend 跑 npm install && npm run build，修复报错，不要联网访问 wms.xlwms.com。」
- 反编译 PDA：「读 CLAUDE.md。用本机 jadx 离线反编译 _private/ 下的 apk 到 _private/jadx-out，只读文件。
  还原 ApiService 各方法的请求体类字段，以及 ClientChannelSigner/MyHeadInterceptor 的签名与请求头逻辑，把结论写入 docs/，不要调用任何真实接口。」
- 补后端：「读 CLAUDE.md 和 docs/PDA_API.md。根据还原出的请求体字段，为入库收货相关接口补 DTO 与示例实现，数据虚构，不连生产。」
- 补前端：「读 CLAUDE.md。实现 frontend 里一件代发和产品库存两个只读列表页，对接后端示例接口，字段参考 src/api 与 docs/ANALYSIS.md。」
