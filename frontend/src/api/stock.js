import request from './request'
// 观察自产品库存页（/stock/sku）加载时的只读请求；请求体未抓取
export const skuPage = (body = {}) => request.post('/stockSku/page', body)
export const skuTotal = (body = {}) => request.post('/stockSku/skuTotal', body)
// 列：产品条码、产品名称、产品分类、客户、库存属性(正品等)、总库存、可用库存、锁定库存
