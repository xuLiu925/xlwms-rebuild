import request from './request'
// 基础数据各只读页接口；接口路径与请求体均为本项目占位，真实接口未抓取
export const productPage = (body = {}) => request.post('/basicProduct/page', body)
export const areaPage = (body = {}) => request.post('/basicArea/page', body)
export const cellPage = (body = {}) => request.post('/basicCell/page', body)
export const trayPage = (body = {}) => request.post('/basicTray/page', body)
export const packMaterialPage = (body = {}) => request.post('/basicPackMaterial/page', body)
