<template>
  <div>
    <template v-if="!order">
      <el-input v-model="keyword" class="pda-scan" placeholder="扫描退件单号 / 快递单号 / 产品条码" clearable @keyup.enter.native="search" />
      <div v-if="!list.length" class="pda-empty">没有待收货的退件单</div>
      <div v-for="o in list" :key="o.orderNo" class="pda-card clickable" @click="open(o)">
        <div class="pda-row"><b>{{ o.orderNo }}</b><span class="pda-tag">{{ typeName(o.orderType) }}</span></div>
        <div class="pda-muted">{{ o.customerName }} · 快递 {{ o.expressNo || '—' }}</div>
      </div>
    </template>

    <template v-else>
      <div class="pda-card">
        <div class="pda-row"><b>{{ order.orderNo }}</b><span class="pda-tag">{{ typeName(order.orderType) }}</span></div>
        <div class="pda-muted">{{ order.customerName }} · 快递 {{ order.expressNo || '—' }}</div>
        <el-button type="text" size="mini" @click="close">切换退件单</el-button>
      </div>

      <div class="pda-section">包裹到货（勾选已到的包裹）</div>
      <div v-for="p in packages" :key="p.packNo" class="pda-card">
        <div class="pda-row">
          <el-checkbox v-model="p.arrived">{{ p.trackNo || p.packNo }}</el-checkbox>
          <span class="pda-muted">{{ p.packNo }}</span>
        </div>
      </div>

      <div class="pda-section">清点产品（良品 / 次品）</div>
      <div v-for="s in products" :key="s.productSku" class="pda-card">
        <div class="pda-row"><b>{{ s.productSku }}</b><span class="pda-muted">预报 {{ s.forecastQty }}</span></div>
        <div class="pda-muted">{{ s.productName }}</div>
        <div class="pda-row line">
          <span class="lbl">良品</span><el-input-number v-model="s.good" size="mini" :min="0" class="pda-qty" controls-position="right" />
          <span class="lbl">次品</span><el-input-number v-model="s.bad" size="mini" :min="0" class="pda-qty" controls-position="right" />
        </div>
      </div>
      <el-input v-model="remark" size="small" placeholder="收货备注（可选）" maxlength="128" />
      <div class="pda-actions"><el-button type="primary" @click="submit">确认收货</el-button></div>
      <div class="pda-muted">收货后退件单变为待上架；次品上架时只能放 E 区。</div>
    </template>
  </div>
</template>
<script>
import * as pda from '../../api/pda'
const TYPE = { 1: '买家退货', 2: '拒收', 3: 'PDA 新建', 4: '认领' }
export default {
  data: () => ({ list: [], keyword: '', order: null, packages: [], products: [], remark: '' }),
  created () { this.loadList() },
  methods: {
    typeName (t) { return TYPE[t] || '退件' },
    async loadList () {
      try { this.list = (await pda.returnReceiveList()).records } catch (e) { this.$message.error(e.message) }
    },
    async search () {
      if (!this.keyword.trim()) return this.loadList()
      try {
        const found = await pda.returnReceiveScan(this.keyword.trim())
        if (found.length === 1) return this.open(found[0])
        this.list = found
      } catch (e) { this.$message.error(e.message) }
    },
    async open (o) {
      try {
        const [pk, pr] = await Promise.all([pda.returnPackages(o.orderNo), pda.returnProducts(o.orderNo)])
        this.packages = pk.pkgList.map(p => ({ ...p, arrived: true }))
        this.products = pr.records.map(s => ({ ...s, good: s.forecastQty, bad: 0 }))
        this.remark = ''
        this.order = o
      } catch (e) { this.$message.error(e.message) }
    },
    close () { this.order = null; this.loadList() },
    async submit () {
      try {
        await pda.returnReceiveSubmit({
          orderNo: this.order.orderNo, customerCode: this.order.customerCode, orderType: this.order.orderType, remark: this.remark,
          packageList: this.packages.map(p => ({ packNo: p.packNo, trackNo: p.trackNo, reach: p.arrived ? '1' : '0', tempCellNo: '' })),
          skuList: this.products.map(s => ({ productSku: s.productSku, goodQty: s.good, defectiveQty: s.bad }))
        })
        this.$message.success('退件 ' + this.order.orderNo + ' 已收货，可到“退件上架”继续')
        this.close()
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.line { margin-top: 8px; justify-content: flex-start; }
.lbl { font-size: 12px; color: #55585f; }
.line .pda-qty { width: 96px !important; }
</style>
