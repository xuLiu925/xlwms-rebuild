import request from './request'
// 批次 9：报表（人效看板 / 入库效率 / 出库效率）。接口路径、请求体（startDate/endDate/whCode）与指标口径为本项目占位，数据为演示推算
export const reportOf = (name, body = {}) => request.post('/report/' + name, body)
