<template>
  <div class="page">
    <p class="hint">按库龄分段统计在库数量。演示数据的库龄天数由 SKU 推算，不是真实入库日期。</p>
    <el-form inline size="small" @submit.native.prevent="load">
      <el-form-item><el-input v-model="query.keyword" clearable placeholder="SKU / 品名" style="width:180px" /></el-form-item>
      <el-form-item><el-input v-model="query.customerCode" clearable style="width:120px"  placeholder="客户代码"/></el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">查询</el-button></el-form-item>
    </el-form>

    <div class="bars">
      <div v-for="b in summary" :key="b.bucket" :class="['bar', { active: String(b.bucket) === query.bucket }]" @click="pick(b)">
        <div class="label">{{ b.bucketName }}</div>
        <div class="track"><div class="fill" :style="{ width: pct(b) + '%', background: colors[b.bucket] }" /></div>
        <div class="num">{{ b.qty }} 件 · {{ b.skuCount }} 个 SKU</div>
      </div>
    </div>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="sku" label="SKU" width="120" />
      <el-table-column prop="productName" label="品名" />
      <el-table-column prop="customerName" label="客户" width="110" />
      <el-table-column prop="totalQty" label="在库数量" width="90" align="right" />
      <el-table-column prop="ageDays" label="库龄(天)" width="90" align="right" />
      <el-table-column prop="bucketName" label="分段" width="110" />
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total"
      :current-page.sync="query.current" :page-size="query.size" @current-change="loadPage" />
  </div>
</template>
<script>
import * as api from '../../api/work'
export default {
  data: () => ({
    query: { keyword: '', customerCode: '', bucket: '', current: 1, size: 10 },
    summary: [], rows: [], total: 0, loading: false,
    colors: ['#67c23a', '#95d475', '#e6a23c', '#f89898', '#f56c6c']
  }),
  computed: { maxQty () { return Math.max(1, ...this.summary.map(b => b.qty)) } },
  created () { this.load() },
  methods: {
    pct (b) { return Math.round(b.qty * 100 / this.maxQty) },
    pick (b) { this.query.bucket = this.query.bucket === String(b.bucket) ? '' : String(b.bucket); this.query.current = 1; this.loadPage() },
    async load () {
      this.query.current = 1
      try { this.summary = await api.stockAgeSummary(this.query) } catch (e) { this.$message.error('后端未连通：' + e.message) }
      this.loadPage()
    },
    async loadPage () {
      this.loading = true
      try { const p = await api.stockAgePage(this.query); this.rows = p.records; this.total = p.total } catch (e) { /* 已提示 */ } finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
.bars { margin: 8px 0 16px; max-width: 760px }
.bar { display: flex; align-items: center; padding: 4px 6px; cursor: pointer; border-radius: 4px }
.bar.active { background: #ecf5ff }
.label { width: 90px; font-size: 13px; color: #606266 }
.track { flex: 1; height: 14px; background: #f2f6fc; border-radius: 7px; overflow: hidden }
.fill { height: 100% }
.num { width: 160px; text-align: right; font-size: 12px; color: #606266 }
.pager { margin-top: 12px; text-align: right }
</style>
