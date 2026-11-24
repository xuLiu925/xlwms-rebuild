import request from './request'
// 观察自入库管理页加载时的只读请求（请求体参数未抓取）
export const asnStatistics = () => request.post('/blAsn/status/statistics')
export const asnTotal = (body = {}) => request.post('/blAsn/queryInTotal', body)
export const asnPage = (body = {}) => request.post('/blAsn/page', body) // data.records[]
// status: 10 在途, 11 收货中, 15 已收货, 30 已上架, 90 已取消
export const ASN_STATUS = { 10: '在途', 11: '收货中', 15: '已收货', 30: '已上架', 90: '已取消' }
