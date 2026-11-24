# xlwms-rebuild

对某 WMS 系统（网页端与 PDA）公开可见接口约定的**独立重建骨架**：Java 后端 + Vue 2 前端。
所有数据均为虚构示例，不包含任何真实业务数据，也不包含厂商源码。

## 结构
- `backend/`：Spring Boot 3 / Java 17。统一响应信封 `{code,data,msg}`、Long 以字符串输出，
  已实现首页、入库管理列表、产品库存列表三组只读示例接口（路径前缀 `/gateway/wms`）。
- `frontend/`：Vue 2 + Element UI，路由表与页面占位，开发时代理 `/gateway` 到后端 8080。
- `docs/`：站点与 PDA 安装包的静态分析记录、PDA 接口清单。

## 运行
```
cd backend && mvn spring-boot:run          # http://localhost:8080
cd frontend && npm install && npm run serve
```
前端设置 `VUE_APP_MOCK=1` 可完全使用本地示例数据。

## 状态
后端与前端均未在作者环境编译运行过，首次使用请先 `mvn test` / `npm run build` 验证。
