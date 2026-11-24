import request from './request'
// 网页端波次管理（/outbound/wave）。注意：真实接口路径未抓取，/wave/* 是本项目占位路径，只对接本地后端。
export const waveStatistics = () => request.post('/wave/statistics')
export const wavePage = (body = {}) => request.post('/wave/page', body) // data.records[]
// pickStatus（占位）：0 待拣货 / 1 拣货中 / 2 已完成
