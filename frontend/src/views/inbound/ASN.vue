<template>
  <div class="page">
    <el-tabs v-model="status"><el-tab-pane v-for="s in stats" :key="s.status" :name="String(s.status)" ><span slot="label">{{ s.statusName }}<span class="label-num">({{ s.statusSum }})</span></span></el-tab-pane></el-tabs>
    <el-table :data="rows" border>
      <el-table-column prop="asnNo" label="入库单号" />
      <el-table-column prop="customerName" label="客户" />
      <el-table-column label="已收箱数/总箱数"><template slot-scope="{row}">{{ row.receiptCount }}/{{ row.boxCount }}</template></el-table-column>
      <el-table-column prop="skuQtySum" label="产品数量" />
      <el-table-column prop="etaTime" label="预计到达日期" />
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>
  </div>
</template>
<script>
import { asnStatistics, asnRecords } from '../../mock/inbound'
import * as api from '../../api/inbound'
export default {
  data: () => ({ status: '', stats: asnStatistics, all: asnRecords }),
  computed: { rows () { return this.status ? this.all.filter(r => String(r.status) === this.status) : this.all } },
  async created () {
    if (process.env.VUE_APP_MOCK === '1') return
    try { this.stats = await api.asnStatistics(); this.all = (await api.asnPage({})).records } catch (e) { /* 保留 mock */ }
  }
}
</script>
