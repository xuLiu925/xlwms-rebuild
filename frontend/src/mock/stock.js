// 后端不可用时的兜底数据，全部虚构
export const skuRecords = [
  { id: '1', sku: 'SKU-0001', barcode: 'BC00010001', productName: '示例收纳盒 1', category: '家居', customerCode: 'C001', customerName: '示例客户1', stockAttr: '正品', totalQty: 42, availableQty: 42, lockedQty: 0 },
  { id: '2', sku: 'SKU-0002', barcode: 'BC00010002', productName: '示例数据线 2', category: '电子配件', customerCode: 'C002', customerName: '示例客户2', stockAttr: '正品', totalQty: 79, availableQty: 69, lockedQty: 10 }
]
export const skuSummary = { skuCount: 2, totalQty: 121, availableQty: 111, lockedQty: 10 }
