<template>
  <div class="dashboard">
    <section class="block">
      <div class="block-title">待办事项</div>
      <div class="tiles">
        <div v-for="t in todos" :key="t.label" class="tile">
          <div class="tile-label"><i :class="t.icon" />{{ t.label }}</div>
          <div class="tile-num">{{ t.value }}</div>
        </div>
      </div>
    </section>
    <div class="row">
      <section class="block half">
        <div class="block-title">库存概览</div>
        <div class="kv"><span>在库总数</span><b>{{ stock.inStockNum }}</b></div>
        <div class="kv"><span>可用 SKU</span><b>{{ stock.availableSkuNum }}</b></div>
      </section>
      <section class="block half">
        <div class="block-title">业务费用<span class="unit">{{ bal.currencyCode }}</span></div>
        <div class="kv"><span>已入账</span><b>{{ bal.recordedAmount }}</b></div>
        <div class="kv"><span>仓租</span><b>{{ bal.rentStockFee }}</b></div>
        <div class="kv"><span>出库费</span><b>{{ bal.outStockFee }}</b></div>
      </section>
    </div>
  </div>
</template>
<script>
import mock from '../mock/home'
import * as api from '../api/home'
export default {
  data: () => ({ todo: mock.todoData, stock: mock.stockSummary, bal: mock.balance }),
  computed: {
    todos () {
      return [
        { label: '入库待处理', value: this.todo.inBoundOrderNum, icon: 'el-icon-download' },
        { label: '一件代发', value: this.todo.smallOutBoundOrderNum, icon: 'el-icon-upload2' },
        { label: '退件待入库', value: this.todo.returnOrderNum, icon: 'el-icon-refresh-left' },
        { label: '工单待审核', value: this.todo.workOrderNum, icon: 'el-icon-s-claim' }
      ]
    }
  },
  async created () {
    if (process.env.VUE_APP_MOCK === '1') return
    try { [this.todo, this.stock, this.bal] = await Promise.all([api.todoData(), api.stockSummary(), api.balance()]) } catch (e) { /* 保留 mock */ }
  }
}
</script>
<style lang="scss" scoped>
// 首页不用整页白卡片，而是灰底上的多个白色区块
.dashboard { padding: 12px; }
.block { background: #fff; border-radius: 4px; padding: 16px 20px; margin-bottom: 12px; }
.block-title { font-size: 16px; font-weight: 600; color: #0b1019; margin-bottom: 16px; .unit { margin-left: 6px; font-size: 12px; font-weight: 400; color: #888c94; } }
.tiles { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.tile { background: #f5f6f9; border-radius: 4px; padding: 16px; }
.tile-label { font-size: 12px; color: #55585f; i { margin-right: 6px; color: #005bf5; } }
.tile-num { margin-top: 8px; font-size: 24px; font-weight: 600; color: #0b1019; font-family: Avenir, 'Helvetica Neue', Arial, sans-serif; }
.row { display: flex; gap: 12px; }
.half { flex: 1; }
.kv { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f0f2f5; font-size: 13px; color: #55585f;
  b { color: #0b1019; font-weight: 600; } &:last-child { border-bottom: none; } }
</style>
