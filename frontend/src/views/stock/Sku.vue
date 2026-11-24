<template>
  <div class="page">
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="产品">
        <el-input v-model="query.keyword" clearable placeholder="SKU / 条码 / 名称" style="width:220px" />
      </el-form-item>
      <el-form-item label="客户">
        <el-select v-model="query.customerCode" clearable placeholder="全部" style="width:140px">
          <el-option v-for="c in customers" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="库存属性">
        <el-select v-model="query.stockAttr" clearable placeholder="全部" style="width:110px">
          <el-option label="正品" value="正品" /><el-option label="次品" value="次品" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="summary">
      <div v-for="c in cards" :key="c.label" class="card"><div class="num">{{ c.value }}</div><div class="label">{{ c.label }}</div></div>
    </div>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="sku" label="SKU" width="110" />
      <el-table-column prop="barcode" label="产品条码" width="120" />
      <el-table-column prop="productName" label="产品名称" min-width="150" />
      <el-table-column prop="category" label="产品分类" width="100" />
      <el-table-column prop="customerName" label="客户" min-width="100" />
      <el-table-column label="库存属性" width="90">
        <template slot-scope="{row}"><el-tag size="mini" :type="row.stockAttr === '正品' ? 'success' : 'warning'">{{ row.stockAttr }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="totalQty" label="总库存" width="90" align="right" />
      <el-table-column prop="availableQty" label="可用库存" width="90" align="right" />
      <el-table-column label="锁定库存" width="90" align="right">
        <template slot-scope="{row}"><span :class="{ locked: row.lockedQty > 0 }">{{ row.lockedQty }}</span></template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/stock'
import { skuRecords, skuSummary } from '../../mock/stock'

const emptyQuery = () => ({ keyword: '', customerCode: '', stockAttr: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(),
    summary: {},
    rows: [],
    total: 0,
    loading: false,
    usingMock: false,
    customers: [1, 2, 3].map(i => ({ value: 'C00' + i, label: '示例客户' + i }))
  }),
  computed: {
    cards () {
      const s = this.summary
      return [
        { label: 'SKU 数', value: s.skuCount ?? '-' }, { label: '总库存', value: s.totalQty ?? '-' },
        { label: '可用库存', value: s.availableQty ?? '-' }, { label: '锁定库存', value: s.lockedQty ?? '-' }
      ]
    }
  },
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.loadSummary(); this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async loadSummary () {
      const { keyword, customerCode, stockAttr } = this.query
      try { this.summary = await api.skuTotal({ keyword, customerCode, stockAttr }) } catch (e) { this.summary = skuSummary }
    },
    async load () {
      this.loading = true
      try {
        const page = await api.skuPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = skuRecords; this.total = skuRecords.length; this.usingMock = true
      } finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.summary { display: flex; gap: 12px; margin-bottom: 12px }
.card { flex: 1; padding: 12px 16px; background: #f5f7fa; border-radius: 4px }
.num { font-size: 20px; font-weight: 600 }
.label { color: #909399; font-size: 12px; margin-top: 4px }
.locked { color: #e6a23c; font-weight: 600 }
.pager { margin-top: 12px; text-align: right }
.hint { color: #e6a23c; font-size: 12px }
</style>
