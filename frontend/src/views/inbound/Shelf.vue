<template>
  <div class="page">
    <h3>上架</h3>
    <p class="hint">已收货、待上架的明细（来自本地入库作业示例数据，与“收货上架”页共用）。锁定库位不能上架。</p>
    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="asnNo" label="入库单" width="140" />
      <el-table-column prop="customerName" label="客户" width="100" />
      <el-table-column prop="whCode" label="仓库" width="70" />
      <el-table-column prop="sku" label="SKU" width="100" />
      <el-table-column prop="productName" label="品名" />
      <el-table-column prop="pendingShelfQty" label="待上架" width="80" align="right" />
      <el-table-column label="操作" width="380">
        <template slot-scope="{row}">
          <el-select v-model="row.cellCode" size="mini" filterable placeholder="库位" style="width:120px">
            <el-option v-for="c in cells[row.whCode] || []" :key="c" :label="c" :value="c" />
          </el-select>
          <el-input-number v-model="row.qty" size="mini" :min="1" :max="row.pendingShelfQty" style="width:110px" />
          <el-button size="mini" type="success" @click="doPutaway(row)">确认上架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <p v-if="!loading && !rows.length" class="hint">暂无待上架明细。可在“收货上架”页先收货，或重置演示数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/work'
export default {
  data: () => ({ rows: [], cells: {}, loading: false }),
  created () { this.load() },
  methods: {
    async load () {
      this.loading = true
      try {
        const list = await api.pendingShelf()
        this.rows = list.map(r => ({ ...r, cellCode: '', qty: r.pendingShelfQty }))
        for (const wh of new Set(list.map(r => r.whCode))) {
          if (!this.cells[wh]) this.$set(this.cells, wh, await api.shelfCells(wh))
        }
      } catch (e) { this.$message.error('后端未连通：' + e.message) } finally { this.loading = false }
    },
    async doPutaway (row) {
      if (!row.cellCode) return this.$message.warning('请选择库位')
      try {
        await api.putaway({ asnNo: row.asnNo, sku: row.sku, cellCode: row.cellCode, qty: row.qty })
        this.$message.success('上架成功'); this.load()
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
</style>
