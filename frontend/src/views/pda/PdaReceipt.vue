<template>
  <div>
    <!-- 入库单列表 -->
    <template v-if="!order">
      <el-input v-model="keyword" class="pda-scan" placeholder="扫描入库单号 / 来源单号 / 跟踪号" clearable @keyup.enter.native="search" />
      <div v-if="!list.length" class="pda-empty">没有待收货的入库单</div>
      <div v-for="a in list" :key="a.asnNo" class="pda-card clickable" @click="open(a.asnNo)">
        <div class="pda-row"><b>{{ a.asnNo }}</b><span class="pda-tag">{{ a.forecastTypeName }}</span></div>
        <div class="pda-muted">{{ a.customerName }} · {{ a.skuNum }} 个 SKU · 已收 {{ a.receiptQty }} 件 · {{ statusName(a.status) }}</div>
      </div>
    </template>

    <!-- 单据作业 -->
    <template v-else>
      <div class="pda-card">
        <div class="pda-row"><b>{{ order.asnNo }}</b><span class="pda-tag">{{ order.forecastTypeName }}</span></div>
        <div class="pda-muted">{{ order.customerName }} · 已收 {{ order.receiptQty }} / {{ order.asnQty }} 件
          <template v-if="order.forecastType === 1"> · 未收 {{ order.unReceiptBoxCount }} 箱</template></div>
        <el-button type="text" size="mini" @click="close">切换入库单</el-button>
      </div>
      <div v-if="putawayNo" class="pda-ok">已生成上架单 {{ putawayNo }}，可到“上架”继续作业。</div>

      <!-- 按箱：扫箱唛，逐箱确认 -->
      <template v-if="order.forecastType === 1">
        <el-input v-model="boxCode" class="pda-scan" placeholder="扫描箱唛，如 BX-0001-01" clearable @keyup.enter.native="scanBox" />
        <div v-if="box" class="pda-card">
          <div class="pda-row"><b>{{ box.boxBarcode }}</b><span :class="['pda-tag', box.receiptFlag ? 'ok' : '']">{{ box.receiptFlag ? '已收' : '未收' }}</span></div>
          <div v-for="s in boxSkus" :key="s.productSku" class="pda-row line">
            <div>{{ s.productSku }}<div class="pda-muted">{{ s.productName }} · 预报 {{ s.asnQty }}</div></div>
            <el-input-number v-model="s.qty" size="mini" :min="0" class="pda-qty" controls-position="right" />
          </div>
          <div class="pda-actions"><el-button type="primary" :disabled="!!box.receiptFlag" @click="submitBox">确认收货</el-button></div>
        </div>
      </template>

      <!-- 按箱型：每个箱型填实收箱数 -->
      <template v-else-if="order.forecastType === 2">
        <div v-for="t in boxTypes" :key="t.boxType" class="pda-card" :class="{ done: t.realReceiptQty >= t.needReceiptQty }">
          <div class="pda-row">
            <div>{{ t.boxType }}<div class="pda-muted">{{ t.skuList[0].productSku }} × {{ t.skuList[0].asnQty }}/箱 · 已收 {{ t.realReceiptQty }}/{{ t.needReceiptQty }} 箱</div></div>
            <el-input-number v-model="t.qty" size="mini" :min="0" class="pda-qty" controls-position="right" />
          </div>
        </div>
        <div class="pda-actions"><el-button type="primary" @click="submitBoxTypes">确认收货</el-button></div>
      </template>

      <!-- 按 SKU：每个 SKU 填本次实收件数 -->
      <template v-else>
        <div v-for="s in skus" :key="s.productSku" class="pda-card" :class="{ done: s.receiptQty >= s.asnQty }">
          <div class="pda-row">
            <div>{{ s.productSku }}<div class="pda-muted">{{ s.productName }} · 已收 {{ s.receiptQty }}/{{ s.asnQty }}</div></div>
            <el-input-number v-model="s.qty" size="mini" :min="0" class="pda-qty" controls-position="right" />
          </div>
        </div>
        <div class="pda-actions"><el-button type="primary" @click="submitSkus">确认收货</el-button></div>
      </template>

      <div class="pda-section">完成</div>
      <div class="pda-actions">
        <el-button @click="finish">完成收货</el-button>
        <el-button type="warning" plain @click="wholeOrder">整单收货</el-button>
      </div>
      <div class="pda-muted">完成收货：未收的视为少收；整单收货：剩余部分全部按预报收完。</div>
    </template>
  </div>
</template>
<script>
import * as pda from '../../api/pda'
const STATUS = { 10: '待收货', 11: '收货中', 15: '已收货' }
export default {
  data: () => ({ list: [], keyword: '', order: null, putawayNo: '', boxCode: '', box: null, boxSkus: [], boxTypes: [], skus: [] }),
  created () { this.loadList() },
  methods: {
    statusName (s) { return STATUS[s] || s },
    async loadList () {
      try { this.list = await pda.receiptList() } catch (e) { this.$message.error(e.message) }
    },
    async search () {
      if (!this.keyword.trim()) return this.loadList()
      try {
        const found = await pda.receiptSearch(this.keyword.trim())
        if (found.length === 1) return this.open(found[0].asnNo)
        this.list = found
        if (!found.length) this.$message.warning('没有找到入库单')
      } catch (e) { this.$message.error(e.message) }
    },
    async open (asnNo) {
      this.putawayNo = ''
      this.box = null
      this.boxCode = ''
      await this.refresh(asnNo)
    },
    close () { this.order = null; this.loadList() },
    // 每次收货后重新取单据：rowVersion 会变，旧版本提交会被拒（乐观锁）
    async refresh (asnNo = this.order.asnNo) {
      try {
        this.order = await pda.receiptOrder(asnNo)
        if (this.order.forecastType === 2) {
          const page = await pda.receiptBoxTypes(asnNo)
          this.boxTypes = page.records.map(t => ({ ...t, qty: Math.max(0, t.needReceiptQty - t.realReceiptQty) }))
        } else if (this.order.forecastType === 3) {
          const page = await pda.receiptSkuPage(asnNo)
          this.skus = page.records.map(s => ({ ...s, qty: Math.max(0, s.asnQty - s.receiptQty) }))
        }
      } catch (e) { this.$message.error(e.message) }
    },
    base () {
      const o = this.order
      return { asnNo: o.asnNo, rowVersion: o.rowVersion, customerCode: o.customerCode, sourceNo: o.sourceNo }
    },
    async run (fn, ok) {
      try {
        const r = await fn()
        if (typeof r === 'string' && r) this.putawayNo = r
        this.$message.success(ok)
        await this.refresh()
      } catch (e) { this.$message.error(e.message) }
    },
    async scanBox () {
      if (!this.boxCode.trim()) return
      try {
        const [b] = await pda.receiptBox(this.order.asnNo, this.boxCode.trim())
        this.box = b
        this.boxSkus = b.skuList.map(s => ({ ...s, qty: s.asnQty }))
      } catch (e) { this.box = null; this.$message.error(e.message) }
    },
    submitBox () {
      const boxList = [{ boxBarcode: this.box.boxBarcode, skuList: this.boxSkus.map(s => ({ productSku: s.productSku, productName: s.productName, receiptQty: s.qty })) }]
      this.run(() => pda.receiveBoxes({ ...this.base(), boxList }), '箱 ' + this.box.boxBarcode + ' 已收货').then(() => {
        this.box = null
        this.boxCode = ''
      })
    },
    submitBoxTypes () {
      const boxTypeDTOS = this.boxTypes.filter(t => t.qty > 0).map(t => ({ boxType: t.boxType, receiptQty: t.qty }))
      if (!boxTypeDTOS.length) return this.$message.warning('请填写实收箱数')
      this.run(() => pda.receiveBoxTypes({ ...this.base(), boxTypeDTOS }), '已收货')
    },
    submitSkus () {
      const skuList = this.skus.filter(s => s.qty > 0).map(s => ({ productSku: s.productSku, receiptQty: s.qty }))
      if (!skuList.length) return this.$message.warning('请填写实收件数')
      this.run(() => pda.receiveSkus({ ...this.base(), skuList }), '已收货')
    },
    async finish () {
      try { await this.$confirm('完成后未收的部分视为少收，不能再收。确定完成收货？', '完成收货', { type: 'warning' }) } catch (e) { return }
      // 按箱型有专门的完成接口；按箱与按 SKU 都走 finishWholeBoxReceipt（空箱列表 = 只完成不收货）
      const fn = this.order.forecastType === 2
        ? () => pda.finishBoxTypes({ ...this.base(), boxTypeDTOS: [] })
        : () => pda.finishBoxes({ ...this.base(), boxList: [] })
      this.run(fn, '收货已完成')
    },
    async wholeOrder () {
      try { await this.$confirm('剩余部分全部按预报数量收货并完成？', '整单收货', { type: 'warning' }) } catch (e) { return }
      this.run(() => pda.receiveWholeOrder(this.base()), '整单收货完成')
    }
  }
}
</script>
<style scoped>
.line { padding: 6px 0; border-top: 1px solid #f0f2f5; }
</style>
