<template>
  <div>
    <el-radio-group v-model="mode" size="small" class="mode" @change="reset">
      <el-radio-button label="sku">查产品</el-radio-button>
      <el-radio-button label="cell">查库位</el-radio-button>
    </el-radio-group>
    <el-input v-model="code" class="pda-scan" :placeholder="mode === 'sku' ? '扫描产品条码 / SKU / FNSKU' : '扫描库位'" clearable
      @keyup.enter.native="query" />

    <!-- 查产品：汇总 + 所在库位 -->
    <template v-if="mode === 'sku' && product">
      <div class="pda-card">
        <div class="pda-row"><b>{{ product.name }}</b><span class="pda-muted">可用 {{ product.validCount }}</span></div>
        <div class="pda-muted">{{ product.productName }} · {{ product.customerName }} · 条码 {{ product.barcode }}</div>
      </div>
      <div class="pda-section">所在库位（点击查看并移库）</div>
      <div v-if="!cells.length" class="pda-empty">没有在库库存</div>
      <div v-for="c in cells" :key="c" class="pda-card clickable" @click="openCell(c)"><b>{{ c }}</b></div>
    </template>

    <!-- 查库位：明细 + 移库 -->
    <template v-if="mode === 'cell' && cellNo">
      <div class="pda-section">库位 {{ cellNo }}</div>
      <div v-if="!items.length" class="pda-empty">库位没有库存</div>
      <div v-for="it in items" :key="it.skuId" class="pda-card">
        <div class="pda-row"><b>{{ it.productSku }}</b>
          <span><span class="pda-tag" :class="{ warn: it.productQuality === 2 }">{{ it.productQualityName }}</span><span v-if="it.boxType" class="pda-tag">{{ it.boxType }}</span></span></div>
        <div class="pda-muted">{{ it.productName }} · 可用 {{ it.useStkQty }}{{ it.boxType ? ' 箱' : ' 件' }}<template v-if="it.freezeQty"> · 冻结 {{ it.freezeQty }}</template></div>
        <el-button type="text" size="mini" :disabled="!it.useStkQty" @click="startMove(it)">移库</el-button>
        <div v-if="move && move.skuId === it.skuId" class="pda-row line">
          <el-input v-model="move.target" size="small" placeholder="目标库位" />
          <el-input-number v-model="move.qty" size="small" :min="1" :max="it.useStkQty" class="pda-qty" controls-position="right" />
        </div>
        <div v-if="move && move.skuId === it.skuId" class="pda-actions">
          <el-button size="small" @click="move = null">取消</el-button>
          <el-button type="primary" size="small" @click="doMove(it)">确认移库</el-button>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import * as pda from '../../api/pda'
export default {
  data: () => ({ mode: 'sku', code: '', product: null, cells: [], cellNo: '', items: [], move: null }),
  methods: {
    reset () { this.code = ''; this.product = null; this.cells = []; this.cellNo = ''; this.items = []; this.move = null },
    async query () {
      const q = this.code.trim()
      if (!q) return
      try {
        if (this.mode === 'sku') {
          const r = await pda.stockSearch(q)
          this.product = r.productList.records[0] || null
          if (!this.product) return this.$message.warning('没有找到产品：' + q)
          this.cells = (await pda.cellsOfSku(this.product.name)).records
        } else {
          await this.loadCell(q)
        }
      } catch (e) { this.$message.error(e.message) }
    },
    openCell (c) { this.mode = 'cell'; this.code = c; this.loadCell(c) },
    async loadCell (c) {
      try {
        this.items = (await pda.stockOfCell(c)).records
        this.cellNo = c
        this.move = null
      } catch (e) { this.cellNo = ''; this.$message.error(e.message) }
    },
    startMove (it) { this.move = { skuId: it.skuId, target: '', qty: it.useStkQty } },
    async doMove (it) {
      if (!this.move.target.trim()) return this.$message.warning('请扫描目标库位')
      try {
        await pda.stockMove({
          sourceCellNo: this.cellNo, targetCellNo: this.move.target.trim(), productSku: it.productSku, boxType: it.boxType,
          productQuality: it.productQuality, customerCode: it.customerCode, moveQty: String(this.move.qty), moveType: 1
        })
        this.$message.success('已移 ' + this.move.qty + ' 到 ' + this.move.target.trim())
        this.loadCell(this.cellNo)
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.mode { margin-bottom: 8px; display: flex; }
.mode ::v-deep .el-radio-button { flex: 1; }
.mode ::v-deep .el-radio-button__inner { width: 100%; }
.line { margin-top: 8px; }
</style>
