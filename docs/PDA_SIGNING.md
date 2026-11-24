# PDA 请求头与签名算法（离线反编译还原）

> 来源：对 `_private/pda.apk`（`com.lingxing.wms` v1.8.3 / versionCode 2026112301，release/product）
> 的本机 jadx 离线反编译产物 `_private/jadx-out/`，只读分析，未调用任何真实接口。
> 相关类：`com.lingxing.wms.app.network.MyHeadInterceptor`、`ClientChannelSigner`、
> `com.lingxing.common.network.interceptor.trackid.TrackKeyGenerator`、`app.config.EnvironmentManager`。

## 1. 域名 / BaseURL

| 用途 | 值 |
|---|---|
| 主域（API_URL / 默认生产域） | `https://pda.xlwms.com/` |
| 备用域（SECONDARY_API_URL） | `https://wms-xl.xlwms.com/` |

`EnvironmentManager` 在主备域之间切换（`getNextProductionDomain`），当前域由
`RetrofitUrlManager` 的 globalDomain 决定。网页端业务前缀 `/gateway/wms`，PDA 走同一网关。

## 2. 每个请求实际附加的 HTTP 头（MyHeadInterceptor）

`MyHeadInterceptor.intercept()` 对**所有**请求统一加头，顺序如下：

| 头名 | 值 | 条件 |
|---|---|---|
| `Authorization` | `Bearer <token>` | 请求未带 `isLogin` 头且本地已有 token；无 token 时清会话并跳登录页 |
| `Content-Type` | `application/json;charset=UTF-8` | 总是 |
| `Origin` | 当前生产域（去尾 `/`），如 `https://pda.xlwms.com` | 总是 |
| `terminalType` | `pda` | 总是（常量 `UtilsAppDelegate.LOG_TAG = "pda"`） |
| `Referer` | `<当前域>/wms-pda-web-ui/` | 总是 |
| `X-Client-Channel` | 见 §3 | 仅当请求带 `isLogin` 头（非空）时 |
| `Track-Key` | 见 §4 | 仅对需签名的方法/请求体（见 §4） |
| `Accept-Language` 等 | 由 `LanguageHelper.applyLanguageHeaders` 按当前语言设置（默认 `zh-cn`） | 总是 |

内部控制头 `isLogin`：调用方用它标记"这是登录态/渠道签名请求"，拦截器读取后会
`removeHeader("isLogin")`，不会发到服务端。它同时决定是否加 `Authorization` 与 `X-Client-Channel`。

> **更正旧文档**：此前在 `docs/*` 里按字符串推断的 `App-Id / appKey / appVersion /
> Channel / Client-Type / Device-Type / deviceId / whCode` **并非 API 请求头**——它们来自
> 内置埋点 SDK（`com.lingxing.wms.analytics.AnalyticsEvent / AnalyticsIdentity`）的字段，
> 以及 `Constants.WH_CODE="whCode"`（业务请求体字段）。真正的 API 头只有上表这些。

## 3. X-Client-Channel（ClientChannelSigner，渠道签名）

```
appKey   = BuildConfig.CLIENT_CHANNEL_APP_KEY   // 22 位 base64url 串，存于 APK（本机 _private 可查，未写入本仓库）
secret   = BuildConfig.CLIENT_CHANNEL_SECRET    // 43 位 base64url 串，同上
timestamp = System.currentTimeMillis()          // 毫秒
nonce    = 16 字节 SecureRandom 的小写 hex（32 字符）
mac      = HmacSHA256(key=secret, msg=appKey + timestamp + nonce)   // 注意三段直接拼接，无分隔符
sig_b64  = Base64(mac, flags = NO_PADDING | NO_WRAP | URL_SAFE)     // Android flag 11

X-Client-Channel = appKey + ":" + timestamp + ":" + nonce + ":" + sig_b64
```

- Base64 flag `11` = `NO_PADDING(1) | NO_WRAP(2) | URL_SAFE(8)`。
- appKey 或 secret 为空则返回空串，不加该头。

## 4. Track-Key（TrackKeyGenerator，请求体签名，v2）

由 `MyHeadInterceptor.addTrackKeyHeader` 触发，仅当：
- method ∈ {get, delete, post, put, patch}（大小写无关），且
- 无请求体时：method ∈ {get, delete, post} 才签；
- 有请求体时：非 multipart/form-data（上传请求跳过）。
- 若请求已自带 `Track-Key` 头则不覆盖。

签名材料与算法（`secret = BuildConfig.SIGN_SECRET`，44 位 base64 串，存于 APK）：

```
// 1) body 规范化 normalizeBody(body):
//    - trim；空串按空处理
//    - 能解析为 JSON 对象/数组时：递归对所有对象键按 TreeMap 升序排序后再 gson 序列化
//      （数组保持顺序，仅排序对象键）；否则原样
// 2) 构造待签串 base:
//    method 统一大写 M
//    若 M==GET，或 (M==POST 且 规范化 body 为空):
//        base = M + normalizeSignPath(path)
//    否则:
//        base = M + SHA256Hex(规范化 body)          // 小写 hex
// 3) Track-Key = "v2:" + Base64( HmacSHA256(key=secret, msg=base), NO_WRAP )   // flag 2，含 '='，不换行
```

`normalizeSignPath(path)`：
- 取 `?` 之前部分，确保以 `/` 开头；
- `=="/api/gateway"` 或 `=="/gateway"` → 返回 `"/"`；
- 以 `"/api/gateway/"` 开头 → 去掉前缀 `/api/gateway`；
- 以 `"/gateway/"` 开头 → 去掉前缀 `/gateway`；
- 否则原样返回。

即：GET 与"空体 POST"按**路径**签名，其余按**排序后请求体的 SHA-256**签名。
拦截器在计算后，若规范化 body 与原 body 不同，会用规范化 body 替换请求体再发送。

## 5. 密钥说明（安全）

`CLIENT_CHANNEL_APP_KEY / CLIENT_CHANNEL_SECRET / SIGN_SECRET` 三个常量是**写死在 APK
BuildConfig 里的真实密钥**，本机可在 `_private/jadx-out` 查到。为避免把可伪造生产请求签名的
密钥提交进仓库，**本文件及 docs/ 下不写出其字面值**。重建后端只需实现同样算法；
本地联调可用自定义测试密钥，切勿用这三个真实值去请求生产网关。
