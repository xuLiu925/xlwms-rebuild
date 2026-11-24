import request from './request'
// 观察自一件代发页（/outbound/parcel）加载时的只读请求；请求体未抓取
export const parcelTotal = (body = {}) => request.post('/blDelivery/queryInTotal', body)
export const parcelPage = (body = {}) => request.post('/blDelivery/page', body)
export const productInfoBatchQuery = (body = {}) => request.post('/productInfo/batchQuery', body)
// 页面状态页签：待处理/待拣货/待复核/待出库/已出库/异常/已取消
// 列表列：出库单号、客户、订单品种类型、物流渠道、承运商、国家、跟踪号、包裹数、销售平台、
// 揽收状态、物流状态、发货时限、波次号、面单打印状态 等
