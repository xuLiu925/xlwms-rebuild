<template>
  <div class="page">
    <h3>按箱库存</h3>
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="搜索">
        <el-input v-model="query.keyword" clearable placeholder="箱号 / SKU / 名称 / 库位" style="width:220px" />
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
      <el-table-column prop="boxNo" label="箱号" width="140" />
      <el-table-column prop="sku" label="SKU" width="110" />
      <el-table-column prop="productName" label="产品名称" min-width="150" />
      <el-table-column prop="customerName" label="客户" min-width="100" />
      <el-table-column prop="cellCode" label="库位" width="100" />
      <el-table-column prop="boxSpec" label="箱规(cm)" width="110" />
      <el-table-column label="库存属性" width="90">
        <template slot-scope="{row}"><el-tag size="mini" :type="row.stockAttr === '正品' ? 'success' : 'warning'">{{ row.stockAttr }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="qty" label="数量" width="80" align="right" />
      <el-table-column prop="shelfTime" label="上架时间" width="160" />
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/stock'
import { boxRecords, boxSummary } from '../../mock/stock'

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
      return [{ label: '箱数', value: s.boxCount ?? '-' }, { label: '总数量', value: s.totalQty ?? '-' }]
    }
  },
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.loadSummary(); this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async loadSummary () {
      const { keyword, customerCode, stockAttr } = this.query
      try { this.summary = await api.boxTotal({ keyword, customerCode, stockAttr }) } catch (e) { this.summary = boxSummary }
    },
    async load () {
      this.loading = true
      try {
        const page = await api.boxPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = boxRecords; this.total = boxRecords.length; this.usingMock = true
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
.pager { margin-top: 12px; text-align: right }
.hint { color: #e6a23c; font-size: 12px }
</style>
