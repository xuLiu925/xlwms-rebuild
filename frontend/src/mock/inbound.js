// 字段与真实 records[] 一致，值为虚构
export const asnStatistics = [
  { status: 10, statusName: '在途', statusSum: 3 }, { status: 11, statusName: '收货中', statusSum: 0 },
  { status: 15, statusName: '已收货', statusSum: 1 }, { status: 30, statusName: '已上架', statusSum: 20 }, { status: 90, statusName: '已取消', statusSum: 0 }
]
export const asnRecords = [{
  id: '1', asnNo: 'ASN0000000000XX', sourceNo: 'IB000000000XX', inboundType: 1, forecastTypeName: '按箱唛',
  customerCode: 'C001', customerName: '示例客户', whCode: 'WH01', whCodeName: '示例仓', status: 10, arriveMethod: 2,
  boxCount: 10, receiptCount: 0, skuQtySum: 100, putawayQty: 0, trackingNo: '', referenceNo: '', etaTime: '2026-12-01',
  productList: [{ productSku: 'SKU-1', productName: '示例商品', qty: 100, barcode: 'BC-1', maintained: true }],
  remark: '', createTime: '2026-11-01 10:00:00'
}]
