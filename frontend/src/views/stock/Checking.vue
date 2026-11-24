<template>
  <div class="page">
    <el-tabs v-model="tab" @tab-click="search">
      <el-tab-pane v-for="s in stats" :key="s.status" :name="s.status === '' ? 'all' : s.status">
        <span slot="label">{{ s.statusName }}<span class="label-num">({{ s.statusSum }})</span></span>
      </el-tab-pane>
    </el-tabs>
    <el-form inline @submit.native.prevent="search">
      <el-form-item><el-input v-model="query.keyword" clearable placeholder="盘点单号 / 库位" suffix-icon="el-icon-search" style="width:220px" /></el-form-item>
      <el-form-item>
        <el-select v-model="query.checkType" clearable placeholder="盘点方式" style="width:140px">
          <el-option label="明盘" value="1" /><el-option label="盲盘" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button class="btn-reset" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
    <p class="hint">网页端新建的盘点单可直接在 PDA 盘点；PDA 完成后实盘数写回库存，这里显示盘盈盘亏。演示数据只存内存，重启后恢复。</p>

    <div class="list-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="openCreate">新建盘点单</el-button>
    </div>
    <el-table v-loading="loading" :data="rows" border>
      <el-table-column label="盘点单号" min-width="140">
        <template slot-scope="{row}"><el-button type="text" class="link" @click="openDetail(row)">{{ row.orderSn }}</el-button></template>
      </el-table-column>
      <el-table-column prop="checkTypeName" label="盘点方式" width="90" />
      <el-table-column label="状态" width="90">
        <template slot-scope="{row}"><el-tag size="mini" :type="statusTag[row.status]">{{ row.statusName }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="cellNos" label="盘点库位" min-width="180" show-overflow-tooltip />
      <el-table-column label="进度" width="90" align="right">
        <template slot-scope="{row}">{{ row.completedCellCount }} / {{ row.cellCount }}</template>
      </el-table-column>
      <el-table-column prop="diffRows" label="差异行" width="80" align="right" />
      <el-table-column label="盘盈 / 盘亏" width="110" align="right">
        <template slot-scope="{row}"><span class="up">+{{ row.profitQty }}</span> / <span class="down">-{{ row.lossQty }}</span></template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="110" fixed="right">
        <template slot-scope="{row}">
          <span class="ops">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="text" class="text-danger" @click="cancel(row)">取消</el-button>
          </span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total, sizes, prev, pager, next" :total="total"
      :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />

    <el-dialog title="新建盘点单" :visible.sync="createVisible" width="560px" :close-on-click-modal="false">
      <el-form ref="createForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="盘点方式" prop="checkType">
          <el-radio-group v-model="form.checkType">
            <el-radio label="1">明盘</el-radio><el-radio label="2">盲盘（PDA 不显示账面数）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="盘点库位" prop="cellNos">
          <el-select v-model="form.cellNos" multiple filterable placeholder="选择库位" style="width:100%">
            <el-option v-for="c in cells" :key="c.cellNo" :value="c.cellNo" :disabled="!!c.busyOrderSn"
              :label="c.cellNo + (c.busyOrderSn ? '（' + c.busyOrderSn + ' 未完成）' : '')" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" maxlength="128" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button class="btn-reset" @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="create">保存</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="'盘点单 ' + (detail.orderSn || '')" :visible.sync="detailVisible" width="min(820px, 94vw)">
      <div class="summary">
        <div class="card"><div class="num">{{ detail.statusName }}</div><div class="label">状态</div></div>
        <div class="card"><div class="num">{{ detail.completedCellCount }} / {{ detail.cellCount }}</div><div class="label">已盘库位</div></div>
        <div class="card"><div class="num up">+{{ detail.profitQty }}</div><div class="label">盘盈件数</div></div>
        <div class="card"><div class="num down">-{{ detail.lossQty }}</div><div class="label">盘亏件数</div></div>
      </div>
      <el-table :data="detailRows" border size="small" max-height="400" :span-method="spanCell">
        <el-table-column prop="cellNo" label="库位" width="100" />
        <el-table-column label="库位状态" width="90">
          <template slot-scope="{row}"><el-tag size="mini" :type="row.completed ? 'success' : 'info'">{{ row.completed ? '已盘' : '未盘' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="sku" label="SKU" width="110" />
        <el-table-column prop="productName" label="品名" min-width="120" />
        <el-table-column prop="productQualityName" label="品质" width="70" />
        <el-table-column prop="bookStock" label="账面" width="70" align="right" />
        <el-table-column label="实盘" width="70" align="right">
          <template slot-scope="{row}">{{ row.solidStock === null || row.solidStock === undefined ? '' : row.solidStock }}</template>
        </el-table-column>
        <el-table-column label="差异" width="70" align="right">
          <template slot-scope="{row}"><span :class="row.diff > 0 ? 'up' : row.diff < 0 ? 'down' : ''">{{ fmtDiff(row.diff) }}</span></template>
        </el-table-column>
      </el-table>
      <p v-if="detail.status === 0" class="hint">盘点尚未开始：PDA 点“开始盘点”时才对库位库存拍账面快照。</p>
    </el-dialog>
  </div>
</template>
<script>
import * as api from '../../api/stock'
const emptyQuery = () => ({ keyword: '', checkType: '', current: 1, size: 20 })
export default {
  data: () => ({
    query: emptyQuery(), tab: 'all', stats: [], rows: [], total: 0, loading: false,
    statusTag: { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' },
    createVisible: false, saving: false, cells: [], form: { checkType: '1', cellNos: [], remark: '' },
    rules: { cellNos: [{ type: 'array', required: true, message: '请选择要盘点的库位', trigger: 'change' }] },
    detailVisible: false, detail: {}
  }),
  computed: {
    // 明细按库位展开成行；库位里没有明细时也占一行
    detailRows () {
      return (this.detail.cells || []).flatMap(c => c.items.length
        ? c.items.map((it, i) => ({ ...it, cellNo: c.cellNo, completed: c.completed, span: i === 0 ? c.items.length : 0 }))
        : [{ cellNo: c.cellNo, completed: c.completed, span: 1, sku: '—', diff: null }])
    }
  },
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.tab = 'all'; this.search() },
    async load () {
      this.loading = true
      const body = { ...this.query, status: this.tab === 'all' ? '' : this.tab }
      try {
        const [page, stats] = await Promise.all([api.checkPage(body), api.checkStatistics(this.query)])
        this.rows = page.records; this.total = page.total; this.stats = stats
      } catch (e) {
        this.$message.error('后端未连通：' + e.message)
      } finally { this.loading = false }
    },
    fmtDiff (d) { return d === null || d === undefined ? '' : d > 0 ? '+' + d : String(d) },
    spanCell ({ row, columnIndex }) {
      if (columnIndex > 1) return [1, 1]
      return row.span ? [row.span, 1] : [0, 0]
    },
    async openCreate () {
      try { this.cells = await api.checkCells() } catch (e) { return this.$message.error(e.message) }
      this.form = { checkType: '1', cellNos: [], remark: '' }
      this.createVisible = true
      this.$nextTick(() => this.$refs.createForm.clearValidate())
    },
    create () {
      this.$refs.createForm.validate(async ok => {
        if (!ok) return
        this.saving = true
        try {
          const o = await api.checkCreate(this.form)
          this.$message.success('已新建 ' + o.orderSn)
          this.createVisible = false
          this.load()
        } catch (e) { this.$message.error(e.message) } finally { this.saving = false }
      })
    },
    async openDetail (row) {
      try { this.detail = await api.checkDetail(row.orderSn); this.detailVisible = true } catch (e) { this.$message.error(e.message) }
    },
    async cancel (row) {
      try { await this.$confirm('确定取消盘点单 ' + row.orderSn + '？取消后 PDA 不再显示该单。', '取消盘点单', { type: 'warning' }) } catch (e) { return }
      try { await api.checkCancel(row.orderSn); this.$message.success('已取消'); this.load() } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.list-toolbar { padding: 12px 0 0; }
.list-toolbar + .el-table { margin-top: 12px; }
.ops .el-button { padding: 0; }
.ops .el-button + .el-button { margin-left: 12px; }
.link { padding: 0; }
.text-danger { color: #f5222d; }
.up { color: #28a864; }
.down { color: #df3148; }
.summary { display: flex; gap: 12px; margin-bottom: 12px; }
.card { flex: 1; padding: 12px 16px; background: #f5f6f9; border-radius: 4px; }
.card .num { font-size: 20px; font-weight: 600; }
.card .label { font-size: 12px; color: #888c94; margin-top: 4px; }
</style>
