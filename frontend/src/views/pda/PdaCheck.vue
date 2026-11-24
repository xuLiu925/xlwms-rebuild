<template>
  <div>
    <template v-if="!order">
      <div v-if="!list.length" class="pda-empty">没有盘点单<div class="pda-muted">可在网页端“库内-盘点”新建</div></div>
      <div v-for="o in list" :key="o.orderSn" class="pda-card clickable" @click="open(o)">
        <div class="pda-row"><b>{{ o.orderSn }}</b>
          <span><span class="pda-tag">{{ o.checkType === 2 ? '盲盘' : '明盘' }}</span><span class="pda-tag" :class="tagClass(o.status)">{{ statusName(o.status) }}</span></span></div>
        <div class="pda-muted">{{ o.remark || '—' }} · 已盘 {{ o.completedCellCount }}/{{ o.totalCellCount }} 个库位</div>
      </div>
    </template>

    <template v-else-if="!cell">
      <div class="pda-card">
        <div class="pda-row"><b>{{ order.orderSn }}</b><span class="pda-tag" :class="tagClass(order.status)">{{ statusName(order.status) }}</span></div>
        <div class="pda-muted">{{ order.checkType === 2 ? '盲盘：不显示账面数' : '明盘' }} · 已盘 {{ order.completedCellCount }}/{{ order.totalCellCount }}</div>
        <el-button type="text" size="mini" @click="close">切换盘点单</el-button>
      </div>
      <div v-if="order.status === 0" class="pda-actions"><el-button type="primary" @click="start">开始盘点</el-button></div>
      <template v-else>
        <el-input v-model="cellCode" class="pda-scan" placeholder="扫描库位" clearable @keyup.enter.native="pickCell(cellCode)" />
        <div v-for="c in order.cellList" :key="c.cellNo" class="pda-card clickable" :class="{ done: c.completed }" @click="pickCell(c.cellNo)">
          <div class="pda-row"><b>{{ c.cellNo }}</b><span class="pda-tag" :class="{ ok: c.completed }">{{ c.completed ? '已盘' : '未盘' }}</span></div>
          <div class="pda-muted">{{ c.areaNo }} · {{ c.dtlList.length }} 个明细</div>
        </div>
        <div v-if="order.status === 1" class="pda-actions"><el-button type="success" @click="complete">完成盘点</el-button></div>
        <div v-if="order.status === 2" class="pda-ok">盘点已完成，实盘数已写回库存。</div>
      </template>
    </template>

    <!-- 盘一个库位 -->
    <template v-else>
      <div class="pda-card">
        <div class="pda-row"><b>库位 {{ cell.cellNo }}</b><el-button type="text" size="mini" @click="cell = null">返回</el-button></div>
        <div class="pda-muted">逐个填实盘数；账面上没有的产品先扫条码加进来。未填的按 0 提交。</div>
      </div>
      <el-input v-model="skuCode" class="pda-scan" placeholder="扫描产品条码 / SKU" clearable @keyup.enter.native="scanSku" />
      <div v-for="d in lines" :key="d.itemId" class="pda-card">
        <div class="pda-row">
          <div>{{ d.sku }}<div class="pda-muted">{{ d.productName }}<template v-if="order.checkType !== 2"> · 账面 {{ d.bookStock }}</template></div></div>
          <el-input-number v-model="d.qty" size="mini" :min="0" class="pda-qty" controls-position="right" />
        </div>
      </div>
      <div v-if="!lines.length" class="pda-empty">库位没有账面库存</div>
      <div class="pda-actions"><el-button type="primary" :disabled="order.status !== 1" @click="submitCell">提交本库位</el-button></div>
    </template>
  </div>
</template>
<script>
import * as pda from '../../api/pda'
const STATUS = { 0: '待盘点', 1: '盘点中', 2: '已完成' }
export default {
  data: () => ({ list: [], order: null, cell: null, cellCode: '', skuCode: '', lines: [] }),
  created () { this.loadList() },
  methods: {
    statusName (s) { return STATUS[s] || s },
    tagClass (s) { return s === 1 ? 'warn' : s === 2 ? 'ok' : '' },
    async loadList () {
      try { this.list = (await pda.checkList()).records } catch (e) { this.$message.error(e.message) }
    },
    async open (o) { await this.reload(o.orderSn) },
    close () { this.order = null; this.cell = null; this.loadList() },
    async reload (orderSn = this.order.orderSn) {
      try { this.order = await pda.checkOverview(orderSn) } catch (e) { this.$message.error(e.message) }
    },
    // start / complete 的返回是错误列表（InventoryCheckError），空列表表示成功
    showErrors (errs) {
      if (!errs.length) return false
      this.$message.error(errs.map(e => e.cause + (e.cellNo ? '：' + e.cellNo : '')).join('；'))
      return true
    },
    async start () {
      try {
        if (!this.showErrors(await pda.checkStart(this.order.orderSn))) this.$message.success('已开始盘点，账面数已快照')
        this.reload()
      } catch (e) { this.$message.error(e.message) }
    },
    pickCell (cellNo) {
      const c = this.order.cellList.find(x => x.cellNo === (cellNo || '').trim())
      if (!c) return this.$message.error('库位不在本盘点单中：' + cellNo)
      this.cell = c
      this.cellCode = ''
      this.lines = c.dtlList.map(d => ({ ...d, qty: d.solidStock === '' ? (this.order.checkType === 2 ? 0 : d.bookStock) : Number(d.solidStock) }))
    },
    async scanSku () {
      const code = this.skuCode.trim()
      if (!code) return
      try {
        const found = await pda.checkScanSku({ orderSn: this.order.orderSn, cellNo: this.cell.cellNo, scanCode: code })
        found.forEach(d => { if (!this.lines.some(l => l.itemId === d.itemId)) this.lines.push({ ...d, qty: 1 }) })
        this.skuCode = ''
      } catch (e) { this.$message.error(e.message) }
    },
    async submitCell () {
      try {
        this.order = await pda.checkSubmitCell({
          orderSn: this.order.orderSn, cellNo: this.cell.cellNo,
          dtlList: this.lines.map(l => ({ itemId: l.itemId, sku: l.sku, solidStock: l.qty, remark: '' }))
        })
        this.$message.success('库位 ' + this.cell.cellNo + ' 已提交')
        this.cell = null
      } catch (e) { this.$message.error(e.message) }
    },
    async complete () {
      try { await this.$confirm('完成后按实盘数调整库存（盘盈盘亏直接生效）。确定完成？', '完成盘点', { type: 'warning' }) } catch (e) { return }
      try {
        if (!this.showErrors(await pda.checkComplete(this.order.orderSn))) this.$message.success('盘点完成，库存已调整')
        this.reload()
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
