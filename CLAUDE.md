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
  `ReceiptController` 实现 `/gateway/wms/pda/receipt/{scanPallet,getBoxTypeInfo,skuReceipt,wholeOrderReceipt}`，
  全部返回虚构示例数据，不连生产。真实路径取自 ApiService（`/gateway/wms/pda/...`，与网页端 `/gateway/wms` 同网关）。
- 上架 DTO（2026-11-24）：`dto/shelves/` 下 `SkuShelvesReq`（含 SkuShelvesItem）、`BoxShelvesReq`（含 BoxShelvesItem）；
  `ShelvesController` 实现 `/gateway/wms/pda/shelf/v2/{scanBarcode(GET),getRecommendCell/v2(GET),confirm,box/submitShelf}`，
  同样虚构数据、不连生产。
- 退货上架 DTO（2026-11-24）：`dto/returnputaway/` 下 `ReturnPutawayScanReq`、`ShelvesInfoReq`（含 ShelvesSubmit）、`Appendix`；
  `ReturnPutawayController` 实现 `/gateway/wms/pda/return/putaway/{scanBarcode,scanOrderNo,scanCell(GET),listOrder(GET),detail/scanBarcode(GET),submit}`，
  虚构数据、不连生产。销毁 `destruct`（破坏性操作）未实现。测试共 11 个通过。
- 只读列表页（2026-11-24）：前端 `views/outbound/Parcel.vue`（一件代发，状态页签+筛选+分页）、`views/stock/Sku.vue`（产品库存，汇总卡片+筛选+分页），
  路由里用 `pages` 映射已实现页面。后端 `DeliveryController`（`/blDelivery/{queryInTotal,page}`）与 `StockController` 改为内存分页虚构数据（`common/PageUtil`）。
  **注意**：这两组的请求体字段（keyword/customerCode/status/current/size）、records 字段名、一件代发状态编码（10/20/30/40/50/80/90）都是本项目占位，真实值未抓取。
  本地运行配置在 `.claude/launch.json`（后端 8080、前端 8081）。测试共 16 个通过。
- 出库作业（2026-11-24）：按 APK 还原波次拣货 `/pda/wave/{pickList,page,pageCell,skuDetail,scanCell,scanSku,pick,queryIntercepting}`
  与复核 `/pda/review/{get4piecePackage,review4packageV2,get4MorePiece,morePieceV2,confirmDelivery}`（`WaveController`、`ReviewController`，
  DTO 在 `dto/wave`、`dto/review`，用 Java record）。响应字段按 APK 的 WavePageBean/PiecePackageBean/MorePieceBean/PutAwayStatus 等还原。
  数据在 `service/OutboundSampleStore`（内存、有状态：拣完→可复核→复核完→可出库，重启恢复）。业务异常 `BizException` → `{code:500,msg}`（错误码为占位）。
  网页端 `views/outbound/Wave.vue` 对接**占位路径** `/wave/{statistics,page}`（真实网页接口未抓取）；`/pdaDemo` 是本地 PDA 作业模拟页，
  `/demo/outbound/reset` 是本地演示专用接口。波次/出库单状态编码、拣货方式编码、流转规则都是本项目假设。测试共 22 个通过。
- 官方文档：2026-11-24 搜索未找到领星海外仓 WMS 的公开帮助中心或开放 API 文档（apifox 上搜到的“海外仓开放平台”属于 jcex，不是领星）。

## 未知 / 未验证
- 构建已修通（2026-11-24）：后端 JDK 17 下 `mvn test` 2 个测试通过；前端 Node 22 下 `npm install && npm run build` 成功（已提交 package-lock.json）。
  注意 `Map.of` 最多 10 对键值，超过请用 LinkedHashMap。前后端都还没实际启动联调过。
- 所有接口的请求体字段、鉴权头名称与值、PDA 请求签名算法。
- 页面真实布局与业务规则。dex 只解析了类/方法签名与注解，方法体未反编译。

## 建议的下一步（按顺序）
1. ~~修通后端 `mvn test` 与前端构建~~（已完成）。
2. ~~jadx 离线反编译还原请求体字段 + 签名/请求头逻辑~~（已完成，见 `docs/PDA_SIGNING.md`、`docs/PDA_REQUEST_BODIES.md`）。
   仍可补：`viewmodel/request` 下 33 个类的交互字段、各 `*Resp/*Rsp` 响应体、嵌套 bean 的内层字段。
3. 按请求体类补全后端 DTO；逐步用 MyBatis-Plus + 本地 MySQL 替换示例数据（仅本地开发库）。
4. 前端把 router 里的占位页逐个实现，先做只读列表页。

## 数据层（2026-11-24：示例数据改为本地 MySQL + MyBatis-Plus）
- 只迁了三个只读列表页的数据集：`stock_sku`（库存）、`bl_delivery`（一件代发）、`bl_asn`（入库）。
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
- 仓库：计划推到私有仓库 `xuLiu925/xlwms-rebuild`（用户账号下其他仓库都是公开的，**不要推到公开仓库**）。
- PDA 安装包与反编译产物放 `_private/`，已在 .gitignore 中，不要提交。

## 可直接用的提示词
- 修通构建：「读 CLAUDE.md。在 backend 跑 mvn test，在 frontend 跑 npm install && npm run build，修复报错，不要联网访问 wms.xlwms.com。」
- 反编译 PDA：「读 CLAUDE.md。用本机 jadx 离线反编译 _private/ 下的 apk 到 _private/jadx-out，只读文件。
  还原 ApiService 各方法的请求体类字段，以及 ClientChannelSigner/MyHeadInterceptor 的签名与请求头逻辑，把结论写入 docs/，不要调用任何真实接口。」
- 补后端：「读 CLAUDE.md 和 docs/PDA_API.md。根据还原出的请求体字段，为入库收货相关接口补 DTO 与示例实现，数据虚构，不连生产。」
- 补前端：「读 CLAUDE.md。实现 frontend 里一件代发和产品库存两个只读列表页，对接后端示例接口，字段参考 src/api 与 docs/ANALYSIS.md。」
