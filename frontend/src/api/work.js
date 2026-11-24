import request from './request'
// 批次 3 作业/配置/工具页接口。全部只连本地后端；路径、字段是本项目占位，真实网页接口未抓取。

// 入库作业
export const asnList = () => request.get('/inboundWork/asnList')
export const asnDetail = asnNo => request.get('/inboundWork/asn', { params: { asnNo } })
export const receive = body => request.post('/inboundWork/receive', body)
export const pendingShelf = () => request.get('/inboundWork/pendingShelf')
export const shelfCells = whCode => request.get('/inboundWork/cells', { params: { whCode } })
export const putaway = body => request.post('/inboundWork/putaway', body)
export const resetInbound = () => request.post('/inboundWork/reset')

// 出库作业（与 PDA 出库模拟共用同一份本地状态）
export const sortWaves = () => request.get('/outboundWork/secondSort/waves')
export const sortWave = waveNo => request.get('/outboundWork/secondSort/wave', { params: { waveNo } })
export const sortScan = body => request.post('/outboundWork/secondSort/scan', body)
export const reviewOrder = no => request.get('/outboundWork/order', { params: { no } })
export const reviewScan = body => request.post('/outboundWork/review/scan', body)
export const boxReviewScan = body => request.post('/outboundWork/boxReview/scan', body)
export const weighAndShip = body => request.post('/outboundWork/weight', body)
export const resetOutbound = () => request.post('/demo/outbound/reset')

// 统计 / 设置 / 工具
export const stockAgeSummary = body => request.post('/stockAge/summary', body)
export const stockAgePage = body => request.post('/stockAge/page', body)
export const getGlobalSetting = () => request.get('/globalSetting/get')
export const saveGlobalSetting = body => request.post('/globalSetting/save', body)
export const freightCalculate = body => request.post('/freight/calculate', body)
export const zoneQuery = body => request.post('/zoneTest/query', body)
