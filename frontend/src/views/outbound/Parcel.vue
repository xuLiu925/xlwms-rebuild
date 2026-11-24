<template>
  <div class="page">
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item>
        <el-input v-model="query.keyword" clearable placeholder="出库单号 / 跟踪号 / 波次号" style="width:240px" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.customerCode" clearable placeholder="客户" style="width:140px">
          <el-option v-for="c in customers" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button class="btn-reset" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-tabs v-model="status" @tab-click="search">
      <el-tab-pane name="" :label="'全部 ' + allCount" />
      <el-tab-pane v-for="s in stats" :key="s.status" :name="String(s.status)" :label="s.statusName + ' ' + s.statusSum" />
    </el-tabs>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="deliveryNo" label="出库单号" min-width="130" />
      <el-table-column prop="customerName" label="客户" min-width="100" />
      <el-table-column prop="skuKind" label="订单品种类型" width="110" />
      <el-table-column prop="channelName" label="物流渠道" min-width="100" />
      <el-table-column prop="carrier" label="承运商" width="90" />
      <el-table-column prop="countryCode" label="国家" width="60" />
      <el-table-column label="跟踪号" min-width="120"><template slot-scope="{row}">{{ row.trackingNo || '-' }}</template></el-table-column>
      <el-table-column prop="parcelQty" label="包裹数" width="70" />
      <el-table-column prop="platform" label="销售平台" min-width="90" />
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}"><el-tag size="mini" :type="tagType(row.status)">{{ row.statusName }}</el-tag></template>
      </el-table-column>
      <el-table-column label="波次号" min-width="110"><template slot-scope="{row}">{{ row.waveNo || '-' }}</template></el-table-column>
      <el-table-column label="面单" width="70"><template slot-scope="{row}">{{ row.labelPrinted ? '已打印' : '未打印' }}</template></el-table-column>
      <el-table-column prop="shipDeadline" label="发货时限" width="150" />
      <el-table-column prop="createTime" label="创建时间" width="150" />
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/outbound'
import { parcelStatistics, parcelRecords } from '../../mock/outbound'

const emptyQuery = () => ({ keyword: '', customerCode: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(),
    status: '',
    stats: [],
    rows: [],
    total: 0,
    loading: false,
    usingMock: false,
    customers: [1, 2, 3].map(i => ({ value: 'C00' + i, label: '示例客户' + i }))
  }),
  computed: { allCount () { return this.stats.reduce((n, s) => n + Number(s.statusSum), 0) } },
  created () { this.search() },
  methods: {
    body () { return { ...this.query, status: this.status } },
    search () { this.query.current = 1; this.loadStats(); this.load() },
    reset () { this.query = emptyQuery(); this.status = ''; this.search() },
    async loadStats () {
      try { this.stats = await api.parcelTotal({ keyword: this.query.keyword, customerCode: this.query.customerCode }) } catch (e) { this.stats = parcelStatistics }
    },
    async load () {
      this.loading = true
      try {
        const page = await api.parcelPage(this.body())
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = this.status ? parcelRecords.filter(r => String(r.status) === this.status) : parcelRecords
        this.total = this.rows.length; this.usingMock = true
      } finally { this.loading = false }
    },
    tagType (s) { return { 50: 'success', 80: 'danger', 90: 'info' }[s] || '' }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.pager { margin-top: 12px; text-align: right }
.hint { color: #e6a23c; font-size: 12px }
</style>
