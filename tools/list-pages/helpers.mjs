// 规格文件共用的虚构数据小工具。所有输出都是虚构示例值。
export const pad = (n, w = 3) => String(n).padStart(w, '0')
export const pick = (arr, i) => arr[i % arr.length]
export const day = (i, base = 1) => `2026-11-${pad(((i + base - 1) % 23) + 1, 2)}`
export const time = (i, base = 1, h = 9) => `${day(i, base)} ${pad((h + i) % 24, 2)}:${pad((i * 7) % 60, 2)}:00`
export const WH = ['WH01', 'WH02']
export const CUS = i => ({ customerCode: 'C' + pad((i % 5) + 1), customerName: '示例客户' + ((i % 5) + 1) })
export const cus = i => 'C' + pad((i % 5) + 1)
export const ON_OFF = { 1: ['启用', 'success'], 0: ['停用', 'info'] }
export const OK_FAIL = { 1: ['成功', 'success'], 0: ['失败', 'danger'] }
export const CARRIERS = ['示例承运商A', '示例承运商B', '示例承运商C']
export const CHANNELS = ['示例渠道1', '示例渠道2', '示例渠道3', '示例渠道4']
export const no = (prefix, i, w = 4) => prefix + '2026110' + pad(i + 1, w)
export const fix2 = v => Number(v).toFixed(2)
/** 批次 2 起的 ID 基数：4.6e15 起，每个资源占 1e13，保证小于 JS 安全整数 9.007e15。 */
export const ID = n => 4600000000000000 + n * 10000000000000
