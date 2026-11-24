import request from './request'
// 以下均为只读查询（POST 仅为传参方式）。请求体参数未抓取，暂不传。
export const stockSummary = () => request.post('/home/stockSummary')
export const todoData = () => request.post('/home/todoData')
export const orderCountAnalysis = () => request.post('/home/order/count/analysis')
export const balance = () => request.post('/home/balance')
