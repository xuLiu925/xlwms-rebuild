// 与真实响应字段一致，数值为虚构
export default {
  stockSummary: { inStockNum: '1000', availableSkuNum: '100', skuAmount: '990', boxAmount: 8, returnAmount: 2 },
  todoData: { inBoundOrderNum: 10, smallOutBoundOrderNum: 20, bulkOutBoundOrderNum: 0, returnOrderNum: 3, transOrderNum: 0, workOrderNum: 0, returnInBoundOrderNum: 1, claimNum: 0, interceptNum: 0, toBePickedWave: 1 },
  orderCountAnalysis: [
    { orderType: 1, orderCount: '10', yoyRate: 0, totRate: 5.5 },
    { orderType: 3, orderCount: '200', yoyRate: 0, totRate: -3.2 }
  ],
  balance: { recordedAmount: 1000, rentStockFee: 800, outStockFee: 150, inStockFee: 50, workOrderFee: 0, otherFee: 0, currencyCode: 'EUR' }
}
