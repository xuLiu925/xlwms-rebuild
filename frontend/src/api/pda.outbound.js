// PDA 出库作业接口（路径/参数/请求体按 APK ApiService 还原）。
// 其中 pick / review4packageV2 / morePieceV2 / confirmDelivery 在真实系统是写操作：
// 只允许对接本地后端（vue.config.js 代理到 localhost:8080），绝不要把 API_TARGET 指向生产网关。
import request from './request'

export const pickList = (pickType = 0, whCode = 'WH01') => request.get('/pda/wave/pickList', { params: { pickType, whCode } })
export const wavePageCell = (waveNo, current = 1, size = 50, whCode = 'WH01') =>
  request.get('/pda/wave/pageCell', { params: { current, size, waveNo, whCode } })
export const scanCell = body => request.post('/pda/wave/scanCell', body)
export const scanSku = body => request.post('/pda/wave/scanSku', body)
export const wavePick = body => request.post('/pda/wave/pick', body)

export const get4MorePiece = (deliveryNo, whCode = 'WH01') => request.get('/pda/review/get4MorePiece', { params: { deliveryNo, finishReview: 0, whCode } })
export const morePiece = body => request.post('/pda/review/morePieceV2', body)
export const confirmDelivery = body => request.post('/pda/review/confirmDelivery', body)

// 本地演示专用（真实系统没有）
export const resetDemo = () => request.post('/demo/outbound/reset')
