// 基础数据各只读页的兜底数据，全部虚构
export const productRecords = [
  { id: '6000000000000001', sku: 'SKU-0001', barcode: 'BC00010001', productName: '示例数据线 1', category: '电子配件', customerCode: 'C002', customerName: '示例客户2', lengthMm: 105, widthMm: 83, heightMm: 62, weightG: 70, status: 1, statusName: '启用', createTime: '2026-11-02 08:01:00' },
  { id: '6000000000000006', sku: 'SKU-0006', barcode: 'BC00010006', productName: '示例文件夹 6', category: '家居', customerCode: 'C001', customerName: '示例客户1', lengthMm: 130, widthMm: 98, heightMm: 72, weightG: 170, status: 0, statusName: '停用', createTime: '2026-11-07 08:06:00' }
]
export const areaRecords = [
  { id: '7000000000000001', areaCode: 'AREA-01', areaName: 'A区存储', whCode: 'WH01', areaType: '存储区', cellCount: 5, status: 1, statusName: '启用', remark: '示例库区 1' },
  { id: '7000000000000002', areaCode: 'AREA-02', areaName: 'B区拣选', whCode: 'WH02', areaType: '拣选区', cellCount: 6, status: 1, statusName: '启用', remark: '示例库区 2' }
]
export const cellRecords = [
  { id: '8000000000000001', cellCode: '01-01-01', areaCode: 'AREA-01', areaName: 'A区存储', whCode: 'WH01', cellType: '货架位', usageStatus: 1, usageName: '占用', createTime: '2026-11-02 08:00:00' },
  { id: '8000000000000003', cellCode: '01-01-03', areaCode: 'AREA-03', areaName: 'C区暂存', whCode: 'WH01', cellType: '托盘位', usageStatus: 0, usageName: '空闲', createTime: '2026-11-04 08:00:00' }
]
export const trayRecords = [
  { id: '9000000000000001', trayNo: 'TRAY000001', whCode: 'WH01', areaCode: 'AREA-01', cellCode: '01-01-01', status: 1, statusName: '在用', skuKinds: 2, totalQty: 15, bindTime: '2026-11-02 09:00:00' },
  { id: '9000000000000002', trayNo: 'TRAY000002', whCode: 'WH02', areaCode: 'AREA-02', cellCode: '', status: 0, statusName: '空闲', skuKinds: 0, totalQty: 0, bindTime: '' }
]
export const packMaterialRecords = [
  { id: '10000000000000001', materialCode: 'PM-001', materialName: '小号纸箱', materialType: '纸箱', spec: '20x15x10', unit: '个', stockQty: 137, status: 1, statusName: '启用' },
  { id: '10000000000000004', materialCode: 'PM-004', materialName: '快递袋S', materialType: '快递袋', spec: '25x35', unit: '个', stockQty: 248, status: 1, statusName: '启用' }
]
