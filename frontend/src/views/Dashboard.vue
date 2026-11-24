<template>
  <div style="padding:24px">
    <h3>首页</h3>
    <el-row :gutter="16">
      <el-col :span="6"><el-card>入库待处理：{{ todo.inBoundOrderNum }}</el-card></el-col>
      <el-col :span="6"><el-card>一件代发：{{ todo.smallOutBoundOrderNum }}</el-card></el-col>
      <el-col :span="6"><el-card>退件待入库：{{ todo.returnOrderNum }}</el-card></el-col>
      <el-col :span="6"><el-card>工单待审核：{{ todo.workOrderNum }}</el-card></el-col>
    </el-row>
    <el-card style="margin-top:16px">在库总数 {{ stock.inStockNum }} / 可用SKU {{ stock.availableSkuNum }}</el-card>
    <el-card style="margin-top:16px">业务费用 {{ bal.recordedAmount }} {{ bal.currencyCode }}（仓租 {{ bal.rentStockFee }}，出库 {{ bal.outStockFee }}）</el-card>
  </div>
</template>
<script>
import mock from '../mock/home'
import * as api from '../api/home'
export default {
  data: () => ({ todo: mock.todoData, stock: mock.stockSummary, bal: mock.balance }),
  async created () {
    if (process.env.VUE_APP_MOCK === '1') return
    try { [this.todo, this.stock, this.bal] = await Promise.all([api.todoData(), api.stockSummary(), api.balance()]) } catch (e) { /* 保留 mock */ }
  }
}
</script>
