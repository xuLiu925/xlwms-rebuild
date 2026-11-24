<template>
  <div class="page">
    <h3>波次管理</h3>
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="单号">
        <el-input v-model="query.keyword" clearable placeholder="波次号 / 出库单号" style="width:220px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float:right">
        <router-link to="/pdaDemo"><el-button type="text">去 PDA 作业模拟 →</el-button></router-link>
      </el-form-item>
    </el-form>

    <el-tabs v-model="status" @tab-click="search">
      <el-tab-pane name="all" :label="'全部 ' + allCount" />
      <el-tab-pane v-for="s in stats" :key="s.status" :name="String(s.status)" :label="s.statusName + ' ' + s.statusSum" />
    </el-tabs>

    <el-table v-loading="loading" :data="rows" border size="small" row-key="waveNo">
      <el-table-column type="expand">
        <template slot-scope="{row}">
          <div class="expand">
            <div class="sub">
              <h4>拣货明细</h4>
              <el-table :data="row.lines" size="mini" border>
                <el-table-column prop="cellNo" label="库位" width="90" />
                <el-table-column prop="productSku" label="SKU" width="100" />
                <el-table-column prop="productName" label="产品名称" />
                <el-table-column label="已拣/应拣" width="90"><template slot-scope="s">{{ s.row.pickQty }}/{{ s.row.planQty }}</template></el-table-column>
              </el-table>
            </div>
            <div class="sub">
              <h4>出库单</h4>
              <el-table :data="row.deliveries" size="mini" border>
                <el-table-column prop="deliveryNo" label="出库单号" width="130" />
                <el-table-column prop="expressNo" label="跟踪号" />
                <el-table-column label="复核" width="70"><template slot-scope="s">{{ s.row.scanNum }}/{{ s.row.skuNum }}</template></el-table-column>
                <el-table-column label="状态" width="80"><template slot-scope="s"><el-tag size="mini" :type="deliveryTag(s.row.status)">{{ s.row.statusName }}</el-tag></template></el-table-column>
              </el-table>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="waveNo" label="波次号" min-width="120" />
      <el-table-column prop="customerName" label="客户" min-width="100" />
      <el-table-column prop="pickWayName" label="拣货方式" width="100" />
      <el-table-column prop="pickCartNo" label="拣货车" width="90" />
      <el-table-column prop="orderCount" label="订单数" width="70" />
      <el-table-column prop="skuKinds" label="SKU 种类" width="80" />
      <el-table-column label="拣货进度" min-width="160">
        <template slot-scope="{row}">
          <el-progress :percentage="row.planQty ? Math.round(row.pickQty * 100 / row.planQty) : 0" :status="row.pickStatus === 2 ? 'success' : undefined" />
          <span class="qty">{{ row.pickQty }}/{{ row.planQty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}"><el-tag size="mini" :type="['info', '', 'success'][row.pickStatus]">{{ row.pickStatusName }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="150" />
    </el-table>

    <el-pagination class="pager" background layout="total, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size="query.size" @current-change="load" />
    <p class="hint">网页端波次页的真实接口路径未抓取，这里对接的是本地后端的占位接口；状态编码也是占位。</p>
  </div>
</template>
<script>
import * as api from '../../api/wave'

const emptyQuery = () => ({ keyword: '', current: 1, size: 20 })

export default {
  data: () => ({ query: emptyQuery(), status: 'all', stats: [], rows: [], total: 0, loading: false }),
  computed: { allCount () { return this.stats.reduce((n, s) => n + Number(s.statusSum), 0) } },
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.loadStats(); this.load() },
    reset () { this.query = emptyQuery(); this.status = 'all'; this.search() },
    async loadStats () {
      try { this.stats = await api.waveStatistics() } catch (e) { this.$message.error('统计加载失败：' + e.message) }
    },
    async load () {
      this.loading = true
      try {
        // 全部页签用 name=all，避免与状态 0 的页签 key 冲突
        const page = await api.wavePage({ ...this.query, status: this.status === 'all' ? '' : this.status })
        this.rows = page.records; this.total = Number(page.total)
      } catch (e) {
        this.rows = []; this.total = 0; this.$message.error('波次加载失败：' + e.message)
      } finally { this.loading = false }
    },
    deliveryTag (s) { return { 0: 'info', 10: 'warning', 20: '', 30: 'success' }[s] }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.expand { display: flex; gap: 16px; padding: 0 12px }
.sub { flex: 1 }
.sub h4 { margin: 4px 0 8px }
.qty { font-size: 12px; color: #909399 }
.pager { margin-top: 12px; text-align: right }
.hint { color: #909399; font-size: 12px }
</style>
