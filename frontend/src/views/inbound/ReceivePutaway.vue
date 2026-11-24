<template>
  <work-layout title="收货上架" :empty="!asn" empty-text="请扫描入库单号开始收货" empty-icon="el-icon-download">
    <template #side>
      <div class="work-tip"><i class="el-icon-warning" />本地演示：扫入库单 → 扫条码收货 → 选库位上架。数据只在本地后端内存中，重启即恢复。</div>
      <div class="work-field">
        <div class="work-label">入库单号</div>
        <el-select v-model="asnNo" filterable allow-create placeholder="请扫描或选择" @change="load">
          <el-option v-for="a in asns" :key="a.asnNo" :label="a.asnNo + '（' + a.statusName + '）'" :value="a.asnNo" />
        </el-select>
      </div>
      <div class="work-field">
        <div class="work-label">Barcode / SKU</div>
        <el-input ref="code" v-model="code" placeholder="请扫描" :disabled="!asn" @keyup.enter.native="doReceive" />
      </div>
      <div class="work-field">
        <div class="work-label">收货数量</div>
        <el-input-number v-model="qty" :min="1" :controls="false" :disabled="!asn" />
      </div>
      <div class="work-actions">
        <el-button type="primary" :disabled="!asn || !code.trim()" @click="doReceive">确认收货</el-button>
      </div>
    </template>
    <template #footer>
      <el-button type="text" @click="reset">重置入库演示数据</el-button>
    </template>

    <template v-if="asn">
      <el-descriptions :column="4" size="small" border>
        <el-descriptions-item label="入库单">{{ asn.asnNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ asn.customerName }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ asn.whCode }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag size="mini">{{ asn.statusName }}</el-tag></el-descriptions-item>
      </el-descriptions>
      <el-table :data="asn.lines" border class="tbl">
        <el-table-column prop="sku" label="SKU" width="110" />
        <el-table-column prop="barcode" label="条码" width="120" />
        <el-table-column prop="productName" label="品名" min-width="120" />
        <el-table-column prop="planQty" label="预报" width="70" align="right" />
        <el-table-column prop="receivedQty" label="已收" width="70" align="right" />
        <el-table-column prop="shelvedQty" label="已上架" width="80" align="right" />
        <el-table-column label="上架" width="360">
          <template slot-scope="{row}">
            <template v-if="row.pendingShelfQty > 0">
              <el-select v-model="shelf[row.sku].cellCode" size="mini" filterable placeholder="库位" style="width:120px">
                <el-option v-for="c in cells" :key="c" :label="c" :value="c" />
              </el-select>
              <el-input-number v-model="shelf[row.sku].qty" size="mini" :min="1" :max="row.pendingShelfQty" style="width:110px" />
              <el-button size="mini" type="primary" plain @click="doPutaway(row)">上架</el-button>
            </template>
            <span v-else class="muted">{{ cellText(row) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </work-layout>
</template>
<script>
import * as api from '../../api/work'
import WorkLayout from '../common/WorkLayout.vue'
export default {
  components: { WorkLayout },
  data: () => ({ asns: [], asnNo: '', asn: null, code: '', qty: 1, cells: [], shelf: {} }),
  async created () { await this.loadList() },
  methods: {
    async loadList () {
      try { this.asns = await api.asnList() } catch (e) { this.$message.error('后端未连通：' + e.message) }
    },
    async load () {
      if (!this.asnNo) return
      try {
        this.setAsn(await api.asnDetail(this.asnNo)); this.cells = await api.shelfCells(this.asn.whCode)
        this.$nextTick(() => this.$refs.code && this.$refs.code.focus())
      } catch (e) { this.asn = null; this.$message.error(e.message) }
    },
    setAsn (a) {
      const shelf = {}
      a.lines.forEach(l => { shelf[l.sku] = (this.shelf[l.sku] && this.shelf[l.sku].cellCode) ? { cellCode: this.shelf[l.sku].cellCode, qty: l.pendingShelfQty || 1 } : { cellCode: '', qty: l.pendingShelfQty || 1 } })
      this.shelf = shelf
      this.asn = a
    },
    async doReceive () {
      if (!this.asn || !this.code.trim()) return
      try {
        this.setAsn(await api.receive({ asnNo: this.asn.asnNo, barcode: this.code.trim(), qty: this.qty }))
        this.$message.success('收货成功'); this.code = ''; this.qty = 1; this.loadList()
      } catch (e) { this.$message.error(e.message) }
    },
    async doPutaway (row) {
      const s = this.shelf[row.sku]
      if (!s.cellCode) return this.$message.warning('请选择库位')
      try {
        this.setAsn(await api.putaway({ asnNo: this.asn.asnNo, sku: row.sku, cellCode: s.cellCode, qty: s.qty }))
        this.$message.success('上架成功'); this.loadList()
      } catch (e) { this.$message.error(e.message) }
    },
    cellText (row) { return Object.entries(row.cells).map(([c, q]) => c + '×' + q).join('，') || '—' },
    async reset () { await api.resetInbound(); this.asn = null; this.asnNo = ''; this.loadList() }
  }
}
</script>
<style scoped>
.tbl { margin-top: 12px }
.muted { color: #888c94 }
</style>
