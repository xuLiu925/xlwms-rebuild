# 站点分析：wms.xlwms.com（领星 WMS）

范围：仅分析未登录可公开访问的资源；/dashboard 会重定向到 /login?redirect=/dashboard。

## 已观察（Observed）
- 技术栈：Vue 2 + Element UI 系（自有 lingxingUI 包），Vue CLI/webpack 分包（约 194 个 chunk），无 sourcemap。
- 入口：app / chunk-libs / chunk-lingxingUI；登录页另有 login chunk、腾讯云 TJCaptcha 验证码。
- 第三方：GA、Hotjar、Lodop 打印控件（本地 8000/18000/8443 端口）、iconfont。
- 路由：15 个顶级模块，含入库/出库/退货/库存/物流报价/结算/设置等（见 src/router/index.js）。
- 首页接口路径：/home/stockSummary、/home/balance、/home/todoData、/home/order/count/*。

## 未知（Unknown）
- 所有接口的请求参数、响应结构、鉴权头格式（需登录后抓包）。
- 登录接口与验证码校验流程。
- 各页面的真实 UI 与业务规则；本项目页面均为占位。

## 下一步
提供测试账号后在你自己的浏览器会话中抓取接口，我再补全 API 层和页面。

## 登录后补充（只读观察，未触发任何写操作）
- 业务接口前缀 `/gateway/wms`，响应信封 `{code:200,data,msg}`。
- 登录相关：`/auth/login`、`/auth/sendVerifyCode`、`/auth/verifyCodeLogin`、`/auth/getUserInfo`、`/auth/allResource/list`（菜单权限）。
- 首页 4 个接口字段已写入 `src/mock/home.js`（数值虚构），`src/api/home.js` 为对应只读调用。
- 首页数据通过 `whCode` 区分仓库；页面自身会上报 `/events/collect`。
- 尚未确认：各接口请求体、鉴权头名称（日志中被脱敏）、除首页外的页面。
- 安全约定：生产账号，仅被动读取已发出请求的响应；不点击按钮、不主动发请求。

## 入库管理页（/inbound/ASN，只读观察）
- 接口：`/blAsn/status/statistics`（状态计数）、`/blAsn/queryInTotal`、`/blAsn/page`（`data.records[]`，含 productList 嵌套）。
- 状态码：10 在途 / 11 收货中 / 15 已收货 / 30 已上架 / 90 已取消。
- 请求体（筛选/分页参数）未抓取。页面的新建、导出等按钮未触碰。

## 一件代发（/outbound/parcel，只读观察）
- 接口：`/blDelivery/queryInTotal`、`/blDelivery/page`、`/productInfo/batchQuery`。
- 页面含大量写操作入口（生成波次、快捷出库、打印、导入等），均未触碰；本项目也未实现。
- 列表含收件人等个人信息，未保留任何真实数据。

## 产品背景（公开资料）
- 领星 WMS 面向海外仓，覆盖入库、出库、库内、报价与结算、预警、报表、多渠道对接；具体功能以官方介绍为准。

## 产品库存（/stock/sku，只读观察）
- 接口：`/stockSku/page`、`/stockSku/skuTotal`。页签：按产品/按库位/库存流水/批次管理。未触碰任何按钮。
- PDA 安装包分析见 `docs/PDA_APK_ANALYSIS.md`。
