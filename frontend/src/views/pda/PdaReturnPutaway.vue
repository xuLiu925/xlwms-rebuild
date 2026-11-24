<template>
  <div>
    <template v-if="!order">
      <el-input v-model="keyword" class="pda-scan" placeholder="扫描退件单号 / 快递单号" clearable @keyup.enter.native="search" />
      <div v-if="!list.length" class="pda-empty">没有待上架的退件单<div class="pda-muted">先到“退件收货”收一张</div></div>
      <div v-for="o in list" :key="o.orderNo" class="pda-card clickable" @click="open(o)">
        <div class="pda-row"><b>{{ o.orderNo }}</b><span class="pda-tag warn">待上架</span></div>
        <div class="pda-muted">{{ o.customerName }} · 快递 {{ o.expressNo || '—' }}</div>
      </div>
    </template>

    <template v-else>
      <div class="pda-card">
        <div class="pda-row"><b>{{ order.orderNo }}</b><span class="pda-tag" :class="finished ? 'ok' : 'warn'">{{ finished ? '已完成' : '待上架' }}</span></div>
        <div class="pda-muted">{{ order.customerName }}</div>
        <el-button type="text" size="mini" @click="close">切换退件单</el-button>
      </div>
      <div v-if="finished" class="pda-ok">全部上架完成，库存已更新。</div>
      <div v-for="r in rows" :key="r.skuId + '-' + r.productQuality" class="pda-card" :class="{ done: !r.waitPutawayQty }">
        <div class="pda-row"><b>{{ r.skuId }}</b>
          <span><span class="pda-tag" :class="{ warn: r.productQuality === 2 }">{{ r.productQuality === 2 ? '次品' : '良品' }}</span>
            <span class="pda-muted"> 待上架 {{ r.waitPutawayQty }}</span></span></div>
        <div class="pda-muted">{{ r.productName }}<template v-if="r.productQuality === 2"> · 次品只能放 E 区</template></div>
        <template v-if="r.waitPutawayQty">
          <div class="pda-row line">
            <el-input v-model="r.cell" size="small" placeholder="扫描库位" />
            <el-input-number v-model="r.qty" size="small" :min="1" :max="r.waitPutawayQty" class="pda-qty" controls-position="right" />
          </div>
          <div class="pda-actions"><el-button type="primary" size="small" @click="submit(r)">确认上架</el-button></div>
        </template>
      </div>
    </template>
  </div>
</template>
<script>
import * as pda from '../../api/pda'
export default {
  data: () => ({ list: [], keyword: '', order: null, rows: [] }),
  computed: { finished () { return this.rows.length > 0 && this.rows.every(r => !r.waitPutawayQty) } },
  created () { this.loadList() },
  methods: {
    async loadList () {
      try { this.list = (await pda.returnPutawayList()).records } catch (e) { this.$message.error(e.message) }
    },
    async search () {
      if (!this.keyword.trim()) return this.loadList()
      try {
        const found = await pda.returnPutawayScan(this.keyword.trim())
        if (found.length === 1) return this.open(found[0])
        this.list = found
      } catch (e) { this.$message.error(e.message) }
    },
    // 逐个产品取按品质拆分的待上架明细（PDA 上是扫产品条码取的，这里一次取全）
    async open (o) {
      try {
        const products = (await pda.returnProducts(o.orderNo)).records
        const details = await Promise.all(products.filter(p => p.receiveQty > 0).map(p => pda.returnPutawayDetail(o.orderNo, p.barcode)))
        this.rows = details.flat().map(d => ({ ...d, cell: d.cellNo || '', qty: d.waitPutawayQty }))
        this.order = o
      } catch (e) { this.$message.error(e.message) }
    },
    close () { this.order = null; this.loadList() },
    async submit (r) {
      const cell = r.cell.trim()
      if (!cell) return this.$message.warning('请扫描库位')
      try {
        await pda.returnPutawayCell(cell, r.productQuality)
        await pda.returnPutawaySubmit({
          returnNo: this.order.orderNo, customerCode: this.order.customerCode, barcode: r.barcode, skuId: r.skuId,
          productQuality: r.productQuality, cellNo: cell, qty: r.qty, waitPutawayQty: r.waitPutawayQty
        })
        this.$message.success(r.skuId + ' 上架 ' + r.qty + ' 件到 ' + cell)
        this.open(this.order)
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.line { margin-top: 8px; }
</style>
