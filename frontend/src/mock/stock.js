// 后端不可用时的兜底数据，全部虚构
export const skuRecords = [
  { id: '1', sku: 'SKU-0001', barcode: 'BC00010001', productName: '示例收纳盒 1', category: '家居', customerCode: 'C001', customerName: '示例客户1', stockAttr: '正品', totalQty: 42, availableQty: 42, lockedQty: 0 },
  { id: '2', sku: 'SKU-0002', barcode: 'BC00010002', productName: '示例数据线 2', category: '电子配件', customerCode: 'C002', customerName: '示例客户2', stockAttr: '正品', totalQty: 79, availableQty: 69, lockedQty: 10 }
]
export const skuSummary = { skuCount: 2, totalQty: 121, availableQty: 111, lockedQty: 10 }

// 按箱库存兜底数据，全部虚构
export const boxRecords = [
  { id: '4000000000000001', boxNo: 'LPN2611000001', sku: 'SKU-0001', productName: '示例数据线 1', customerCode: 'C002', customerName: '示例客户2', cellCode: 'A-01-01', boxSpec: '50x40x30', stockAttr: '正品', qty: 23, shelfTime: '2026-11-02 09:01:00' },
  { id: '4000000000000002', boxNo: 'LPN2611000002', sku: 'SKU-0002', productName: '示例T恤 2', customerCode: 'C003', customerName: '示例客户3', cellCode: 'A-01-02', boxSpec: '40x30x30', stockAttr: '正品', qty: 26, shelfTime: '2026-11-03 09:02:00' }
]
export const boxSummary = { boxCount: 2, totalQty: 49 }
// 退货库存兜底数据，全部虚构
export const returnRecords = [
  { id: '5000000000000001', returnNo: 'RMA2611000001', trackingNo: 'TRK9900000001', sku: 'SKU-0001', productName: '示例数据线 1', customerCode: 'C002', customerName: '示例客户2', qty: 2, reason: '地址错误', status: 20, statusName: '已上架', cellCode: 'R-01-01', receiveTime: '2026-11-02 14:01:00' },
  { id: '5000000000000002', returnNo: 'RMA2611000002', trackingNo: 'TRK9900000002', sku: 'SKU-0002', productName: '示例T恤 2', customerCode: 'C003', customerName: '示例客户3', qty: 3, reason: '商品破损', status: 30, statusName: '待销毁', cellCode: '', receiveTime: '2026-11-03 14:02:00' }
]
export const returnSummary = { returnCount: 2, totalQty: 5 }
