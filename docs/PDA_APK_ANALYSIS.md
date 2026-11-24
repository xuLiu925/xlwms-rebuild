# PDA 安装包静态分析（仅解包读取，未安装、未运行）

## 已观察
- 包名 `com.lingxing.wms`，约 23 MB，5 个 dex，签名证书为自签（CN=lx_wms）。
- 技术栈：Kotlin + 原生界面（Jetpack Compose / DataBinding / Fragment）+ AgentWeb(WebView) 壳；扫码用华为 HMS ScanKit 与自带 libscannative；MMKV 存储；Bugly 崩溃上报；自研网络层 `com.asinking.net`。
- 入口：Splash → Login → SelectWh（选仓库）→ Main（NewMainFragment）。
- 内置环境地址：`https://pda.xlwms.com/`、`https://wms-xl.xlwms.com/`；网关前缀 `/gateway/wms`（与网页端一致，登录同为 `/auth/login`、`/auth/getUserInfo`）。
- PDA 专用接口路径（仅路径，参数未知）：
  - 首页/菜单：`/pda/home`、`/pda/menu/list`、`/pda/warehouse/page`
  - 收货：`/pda/receipt/getAsn`；上架：`/pda/shelf/scanCell`、`/shelf/selectCell`
  - 库存：`/pda/stock/{search,detail,cellList,count,check,adjust,move}`
  - 拣货波次：`/pda/wave/{page,pageCell,pick,pickList,boxPick,scanBox,scanCell,scanSku,skuDetail}`
  - 另有 `/device/tasks/wave`、`/gateway/common/pic/upload`、`/events/collect`
- 功能模块（Fragment 名推断）：收货（整箱/批量/中转/代发/新品信息/效期）、上架、拣货（波次/大件/边拣边分）、复核验货、库存查询/调整/转移/盘点、设备状态、拍照留言。
- 权限：相机、存储、安装包、网络、录音/震动等；含 `REQUEST_INSTALL_PACKAGES`（应用内更新）。

## 未知
- 请求体、鉴权头、签名/加密细节；dex 未反编译成源码，以上均来自字符串与类名。
- 各界面真实布局与业务校验规则。
- 本分析不建议在生产环境安装或运行该包去验证接口；PDA 的扫码、拣货、调整、移库等接口都会写入库存数据。

## 类结构导出（dex 解析，非源码）
- `docs/pda_skeleton/`：`com.lingxing.wms` 下 5857 个类、82 个包的类/字段/方法签名（由自写的只读 dex 解析器生成）。
- 方法体、注解（Retrofit 的 URL 与 HTTP 方法）、字符串常量未还原，因此无法由此得到完整源码或每个接口的路径映射。
- 关键类：`app.network.ApiService`（Retrofit 接口，约 170 个方法，含收货/上架/拣货/复核/盘点/移库等）、`NetworkApi`、`MyHeadInterceptor`（请求头）、`ClientChannelSigner`（请求签名/nonce）、`viewmodel.request`（各业务请求）。
- 这些接口大量是写操作（commitOrder、boxMoveStock、completeInventoryCheck、destruct 等），不应对生产环境调用。

## 接口还原（注解解析）
- `docs/PDA_API.md` / `docs/pda_api.json`：175 个接口（POST 96 / GET 79），含 HTTP 方法、路径、参数类型；其中 62 个按名称推断为只读，113 个视为可能写入。
- `src/api/pda.readonly.js`：只为推断只读的接口生成了客户端；写入类接口刻意未生成。
- 请求头相关字符串（出现在 dex 中，值与组合方式未还原）：Authorization、App-Id、appKey、appVersion、Channel/CHANNEL、X-Client-Channel、Client-Type、Device-Type、deviceId、deviceModel、language、whCode。`ClientChannelSigner` 含 nonce 生成，疑似请求签名，算法未还原。
- 请求体类（如 BatchReceiptSubmitReq）的字段已在 `pda_skeleton/` 中，可在需要时补全类型。
