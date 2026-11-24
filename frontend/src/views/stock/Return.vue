<template>
  <div class="page">
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item>
        <el-input v-model="query.keyword" clearable placeholder="退货单号 / 追踪号 / SKU / 名称" style="width:240px" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.customerCode" clearable placeholder="客户" style="width:140px">
          <el-option v-for="c in customers" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.status" clearable placeholder="状态" style="width:120px">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button class="btn-reset" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="summary">
      <div v-for="c in cards" :key="c.label" class="card"><div class="num">{{ c.value }}</div><div class="label">{{ c.label }}</div></div>
    </div>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="returnNo" label="退货单号" width="150" />
      <el-table-column prop="trackingNo" label="追踪号" width="140" />
      <el-table-column prop="sku" label="SKU" width="110" />
      <el-table-column prop="productName" label="产品名称" min-width="150" />
      <el-table-column prop="customerName" label="客户" min-width="100" />
      <el-table-column prop="qty" label="数量" width="70" align="right" />
      <el-table-column prop="reason" label="退货原因" width="100" />
      <el-table-column label="状态" width="90">
        <template slot-scope="{row}"><el-tag size="mini" :type="statusType(row.status)">{{ row.statusName }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="cellCode" label="库位" width="100" />
      <el-table-column prop="receiveTime" label="收货时间" width="160" />
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/stock'
import { returnRecords, returnSummary } from '../../mock/stock'

const emptyQuery = () => ({ keyword: '', customerCode: '', status: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(),
    summary: {},
    rows: [],
    total: 0,
    loading: false,
    usingMock: false,
    customers: [1, 2, 3].map(i => ({ value: 'C00' + i, label: '示例客户' + i })),
    // 状态编码为本项目占位
    statusOptions: [
      { value: 10, label: '待上架' }, { value: 20, label: '已上架' },
      { value: 30, label: '待销毁' }, { value: 40, label: '已销毁' }
    ]
  }),
  computed: {
    cards () {
      const s = this.summary
      return [{ label: '退货单数', value: s.returnCount ?? '-' }, { label: '总数量', value: s.totalQty ?? '-' }]
    }
  },
  created () { this.search() },
  methods: {
    statusType (code) { return { 10: 'info', 20: 'success', 30: 'warning', 40: 'danger' }[code] || '' },
    search () { this.query.current = 1; this.loadSummary(); this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async loadSummary () {
      const { keyword, customerCode, status } = this.query
      try { this.summary = await api.returnTotal({ keyword, customerCode, status }) } catch (e) { this.summary = returnSummary }
    },
    async load () {
      this.loading = true
      try {
        const page = await api.returnPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = returnRecords; this.total = returnRecords.length; this.usingMock = true
      } finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.summary { display: flex; gap: 12px; margin-bottom: 12px }
.card { flex: 1; padding: 12px 16px; background: #f5f6f9; border-radius: 4px }
.num { font-size: 20px; font-weight: 600 }
.label { color: #888c94; font-size: 12px; margin-top: 4px }
.pager { margin-top: 12px; text-align: right }
.hint { color: #f58718; font-size: 12px }
</style>
