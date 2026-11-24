// 批次 9：参考菜单里有、公开路由表里没有的 4 个列表页（换标、转运、序列号、作业视频）。
// 路径、字段、编码均为本项目占位，数据虚构。作业视频只有元数据，没有视频文件。
import { pad, pick, time, WH, CUS, cus, CARRIERS, no, ID } from './helpers.mjs'

export const extra = [
  {
    key: 'swapBarcode', route: 'outbound/swapBarcode', title: '换标', idBase: ID(43), count: 12,
    columns: [
      ['taskNo', '换标单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['customerName', '客户名称', 'str', { width: 110 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['sku', 'SKU', 'str', { kw: true, width: 110 }],
      ['oldBarcode', '原标签', 'str', { width: 130 }],
      ['newBarcode', '新标签', 'str', { width: 130 }],
      ['qty', '数量', 'int', { width: 70 }],
      ['doneQty', '已换数量', 'int', { width: 90 }],
      ['status', '状态', 'int', { enum: { 10: ['待处理', 'info'], 20: ['处理中', 'warning'], 30: ['已完成', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 20, 30, 30, 90], i)
      const qty = 10 + (i % 6) * 5
      return { taskNo: no('SB', i), ...CUS(i), whCode: pick(WH, i), sku: 'SKU-' + pad((i % 11) + 1, 4),
        oldBarcode: 'BC0001' + pad((i % 11) + 1, 4), newBarcode: 'FN-DEMO-' + pad(100 + i, 4), qty,
        doneQty: s === 30 ? qty : s === 20 ? Math.floor(qty / 2) : 0, status: s, createTime: time(i) }
    }
  },
  {
    key: 'transfer', route: 'transfer/index', title: '转运', idBase: ID(44), count: 14,
    columns: [
      ['transferNo', '转运单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['destination', '目的地', 'str', { filter: true, width: 110 }],
      ['carrier', '承运商', 'str', { filter: true, width: 120 }],
      ['trackingNo', '跟踪号', 'str', { kw: true, width: 140 }],
      ['boxCount', '箱数', 'int', { width: 70 }],
      ['weightKg', '重量(kg)', 'num', { width: 100 }],
      ['status', '状态', 'int', { enum: { 10: ['待入库', 'info'], 20: ['已入库', 'warning'], 30: ['已出库', 'primary'], 40: ['已签收', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 20, 30, 40, 40, 90], i)
      return { transferNo: no('TF', i), customerCode: cus(i), whCode: pick(WH, i), destination: pick(['示例仓 X1', '示例仓 X2', '示例仓 X3'], i),
        carrier: pick(CARRIERS, i), trackingNo: s >= 30 && s <= 40 ? 'TRK' + pad(300000 + i * 13, 8) : '', boxCount: 3 + (i % 8),
        weightKg: (35.5 + i * 7.25).toFixed(2), status: s, createTime: time(i) }
    }
  },
  {
    key: 'stockSerial', route: 'stock/serial', title: '序列号管理', idBase: ID(45), count: 20,
    columns: [
      ['serialNo', '序列号', 'str', { kw: true, width: 160 }],
      ['sku', 'SKU', 'str', { kw: true, width: 110 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['cellNo', '库位', 'str', { width: 100 }],
      ['sourceNo', '入库单号', 'str', { kw: true, width: 150 }],
      ['outboundNo', '出库单号', 'str', { width: 150 }],
      ['status', '状态', 'int', { enum: { 10: ['在库', 'success'], 20: ['已出库', 'info'], 30: ['已冻结', 'warning'] }, tabs: true, width: 90 }],
      ['inTime', '入库时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 10, 20, 10, 30], i)
      return { serialNo: 'SN-DEMO-' + pad(i + 1, 6), sku: 'SKU-' + pad((i % 6) + 1, 4), customerCode: cus(i), whCode: pick(WH, i),
        cellNo: s === 20 ? '' : pick(['A-01-01', 'A-01-02', 'B-01-01', 'C-03-02'], i), sourceNo: no('ASN', i % 8),
        outboundNo: s === 20 ? no('OB', i) : '', status: s, inTime: time(i) }
    }
  },
  {
    key: 'workVideo', route: 'stock/workVideo', title: '作业视频', idBase: ID(46), count: 16,
    columns: [
      ['videoNo', '视频编号', 'str', { kw: true, width: 150 }],
      ['bizType', '作业类型', 'str', { filter: true, width: 100 }],
      ['bizNo', '关联单号', 'str', { kw: true, width: 150 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['operator', '操作人', 'str', { filter: true, width: 100 }],
      ['durationSec', '时长(秒)', 'int', { width: 90 }],
      ['sizeMb', '大小(MB)', 'num', { width: 100 }],
      ['status', '状态', 'int', { enum: { 1: ['已上传', 'success'], 0: ['上传失败', 'danger'] }, tabs: true, width: 90 }],
      ['recordTime', '录制时间', 'time', { width: 160 }]
    ],
    row: i => {
      const type = pick(['收货', '复核', '打包', '退件'], i)
      const prefix = { 收货: 'ASN', 复核: 'OB', 打包: 'OB', 退件: 'RN' }[type]
      return { videoNo: no('VD', i), bizType: type, bizNo: no(prefix, i), whCode: pick(WH, i), operator: '示例员工' + ((i % 6) + 1),
        durationSec: 30 + (i * 17) % 240, sizeMb: (12.5 + (i * 9) % 80).toFixed(2), status: i % 7 === 6 ? 0 : 1, recordTime: time(i) }
    }
  }
]
