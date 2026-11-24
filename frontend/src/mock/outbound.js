// 后端不可用时的兜底数据，全部虚构；字段名为本项目占位（真实 records 字段未抓取）
export const parcelStatistics = [
  { status: 10, statusName: '待处理', statusSum: 1 }, { status: 20, statusName: '待拣货', statusSum: 1 },
  { status: 30, statusName: '待复核', statusSum: 0 }, { status: 40, statusName: '待出库', statusSum: 0 },
  { status: 50, statusName: '已出库', statusSum: 0 }, { status: 80, statusName: '异常', statusSum: 0 },
  { status: 90, statusName: '已取消', statusSum: 0 }
]
export const parcelRecords = [
  { id: '1', deliveryNo: 'OB2611000001', customerCode: 'C001', customerName: '示例客户1', skuKind: '一单一件', channelName: '示例快递A', carrier: 'CarrierA', countryCode: 'DE', trackingNo: '', parcelQty: 1, platform: '示例平台1', status: 10, statusName: '待处理', waveNo: '', labelPrinted: false, shipDeadline: '2026-11-26 23:59:59', createTime: '2026-11-20 09:00:00' },
  { id: '2', deliveryNo: 'OB2611000002', customerCode: 'C002', customerName: '示例客户2', skuKind: '一单多件', channelName: '示例快递B', carrier: 'CarrierB', countryCode: 'FR', trackingNo: '', parcelQty: 2, platform: '独立站', status: 20, statusName: '待拣货', waveNo: 'WV26110001', labelPrinted: false, shipDeadline: '2026-11-27 23:59:59', createTime: '2026-11-21 10:30:00' }
]
