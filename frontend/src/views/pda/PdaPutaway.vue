<template>
  <div>
    <template v-if="!detail">
      <el-input v-model="keyword" class="pda-scan" placeholder="扫描上架单号 / 入库单号 / 箱唛" clearable @keyup.enter.native="search" />
      <div v-if="!list.length" class="pda-empty">没有待上架的单据<div class="pda-muted">先到“收货”收一张入库单</div></div>
      <div v-for="o in list" :key="o.putawayNo" class="pda-card clickable" @click="open(o.putawayNo)">
        <div class="pda-row"><b>{{ o.putawayNo }}</b><span class="pda-tag" :class="{ warn: o.status === 1 }">{{ o.status === 1 ? '上架中' : '待上架' }}</span></div>
        <div class="pda-muted">{{ o.customerName }} · 入库单 {{ o.asnNo }} · {{ o.forecastTypeName }}</div>
      </div>
    </template>

    <template v-else>
      <div class="pda-card">
        <div class="pda-row"><b>{{ detail.putawayNo }}</b><span class="pda-tag" :class="{ ok: done }">{{ done ? '已完成' : '上架中' }}</span></div>
        <div class="pda-muted">客户 {{ detail.customerCode }} · 来源 {{ detail.sourceNo || '—' }}</div>
        <el-button type="text" size="mini" @click="close">切换上架单</el-button>
      </div>
      <div v-if="done" class="pda-ok">这张上架单已全部上架，库存已更新。</div>

      <template v-if="skus.length">
        <div class="pda-section">按 SKU 上架</div>
        <div v-for="s in skus" :key="s.productSku" class="pda-card">
          <div class="pda-row"><b>{{ s.productSku }}</b><span class="pda-muted">待上架 {{ s.receiptQty - s.putawayQty }}</span></div>
          <div class="pda-muted">{{ s.productName }}<template v-if="s.recommendCellList.length"> · 推荐 {{ s.recommendCellList.map(c => c.cellNo).join('、') }}</template></div>
          <div class="pda-row line">
            <el-input v-model="s.cellNo" size="small" placeholder="扫描库位" />
            <el-input-number v-model="s.qty" size="small" :min="1" :max="s.receiptQty - s.putawayQty" class="pda-qty" controls-position="right" />
          </div>
          <div class="pda-actions"><el-button type="primary" size="small" @click="confirm(s)">确认上架</el-button></div>
        </div>
      </template>
      <template v-if="boxTypes.length">
        <div class="pda-section">按箱型（用下方整单上架）</div>
        <div v-for="t in boxTypes" :key="t.boxType" class="pda-card">
          <div class="pda-row"><b>{{ t.boxType }}</b><span class="pda-muted">待上架 {{ t.waitShelfBoxQty }} 箱</span></div>
          <div class="pda-muted">推荐 {{ t.recommendCell || '—' }}</div>
        </div>
      </template>

      <template v-if="!done">
        <div class="pda-section">整单上架</div>
        <div class="pda-card">
          <div class="pda-muted">把剩余未上架的货（含整箱、箱型）全部放到一个库位。</div>
          <div class="pda-row line">
            <el-input v-model="allCell" size="small" placeholder="扫描库位" />
            <el-button type="warning" plain size="small" @click="completeAll">整单上架</el-button>
          </div>
        </div>
      </template>
    </template>
  </div>
</template>
<script>
import * as pda from '../../api/pda'
export default {
  data: () => ({ list: [], keyword: '', detail: null, skus: [], boxTypes: [], allCell: '' }),
  computed: { done () { return this.detail && this.detail.putAwayStatus === 1 } },
  created () { this.loadList() },
  methods: {
    async loadList () {
      try { this.list = (await pda.shelfList()).records } catch (e) { this.$message.error(e.message) }
    },
    async search () {
      if (!this.keyword.trim()) return this.loadList()
      try {
        const found = await pda.shelfSearch(this.keyword.trim())
        if (found.length === 1) return this.open(found[0].putawayNo)
        this.list = found
      } catch (e) { this.$message.error(e.message) }
    },
    async open (putawayNo) {
      try {
        const d = await pda.shelfDetail(putawayNo)
        this.detail = d
        this.skus = d.skuList.records.map(s => ({
          ...s, cellNo: s.recommendCellList.length ? s.recommendCellList[0].cellNo : '', qty: s.receiptQty - s.putawayQty
        }))
        this.boxTypes = d.boxList.records.length ? d.boxList.records[0].boxTypeList : []
      } catch (e) { this.$message.error(e.message) }
    },
    close () { this.detail = null; this.loadList() },
    async confirm (s) {
      if (!s.cellNo.trim()) return this.$message.warning('请扫描库位')
      try {
        await pda.shelfScanCell(s.cellNo.trim(), this.detail.putawayNo)
        await pda.shelfConfirm({
          putawayNo: this.detail.putawayNo, customerCode: this.detail.customerCode,
          skuList: [{ productSku: s.productSku, cellNo: s.cellNo.trim(), putawayQty: s.qty }]
        })
        this.$message.success(s.productSku + ' 上架 ' + s.qty + ' 件到 ' + s.cellNo.trim())
        this.open(this.detail.putawayNo)
      } catch (e) { this.$message.error(e.message) }
    },
    async completeAll () {
      if (!this.allCell.trim()) return this.$message.warning('请扫描库位')
      try {
        await pda.shelfScanCell(this.allCell.trim(), this.detail.putawayNo)
        await pda.shelfCompleteAll({ putawayNo: this.detail.putawayNo, customerCode: this.detail.customerCode, cellNo: this.allCell.trim() })
        this.$message.success('已全部上架到 ' + this.allCell.trim())
        this.open(this.detail.putawayNo)
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.line { margin-top: 8px; }
</style>
