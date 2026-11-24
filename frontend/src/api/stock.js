import request from './request'
// 观察自产品库存页（/stock/sku）加载时的只读请求；请求体未抓取
export const skuPage = (body = {}) => request.post('/stockSku/page', body)
export const skuTotal = (body = {}) => request.post('/stockSku/skuTotal', body)
// 列：产品条码、产品名称、产品分类、客户、库存属性(正品等)、总库存、可用库存、锁定库存

// 按箱库存页（/stock/box）：接口路径与请求体为本项目占位，真实接口未抓取
export const boxPage = (body = {}) => request.post('/stockBox/page', body)
export const boxTotal = (body = {}) => request.post('/stockBox/boxTotal', body)
// 退货库存页（/stock/return）：接口路径、请求体与状态编码为本项目占位
export const returnPage = (body = {}) => request.post('/stockReturn/page', body)
export const returnTotal = (body = {}) => request.post('/stockReturn/returnTotal', body)

// 批次 9：库内-盘点（/stock/checking，与 PDA 盘点共用数据）、次品处理（/stock/defective）。接口路径与字段为本项目占位
export const checkStatistics = (body = {}) => request.post('/inventoryCheck/statistics', body)
export const checkPage = (body = {}) => request.post('/inventoryCheck/page', body)
export const checkDetail = orderSn => request.post('/inventoryCheck/detail', { orderSn })
export const checkCells = () => request.post('/inventoryCheck/cells', {})
export const checkCreate = body => request.post('/inventoryCheck/create', body)
export const checkCancel = orderSn => request.post('/inventoryCheck/cancel', { orderSn })
export const defectivePage = (body = {}) => request.post('/stockDefective/page', body)
export const defectiveToGood = body => request.post('/stockDefective/toGood', body)
export const defectiveDestroy = body => request.post('/stockDefective/destroy', body)
export const defectiveRecords = (body = {}) => request.post('/stockDefective/records', body)
