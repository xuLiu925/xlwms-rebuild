<template>
  <div class="page">
    <h3>PDA 作业模拟（本地）
      <el-button size="mini" style="margin-left:12px" @click="resetAll">重置演示数据</el-button>
    </h3>
    <p class="hint">模拟 PDA 安装包里的波次拣货与出库复核流程，调用的是按安装包还原的 /pda/wave、/pda/review 接口，
      只连本地后端的内存示例数据，重启后端或点“重置”即恢复。</p>

    <div class="phones">
      <!-- 波次拣货 -->
      <div class="phone">
        <div class="bar">波次拣货</div>
        <div class="body">
          <template v-if="!wave">
            <div v-if="!waves.length" class="empty">没有待拣波次</div>
            <div v-for="w in waves" :key="w.waveNo" class="item" @click="openWave(w)">
              <b>{{ w.waveNo }}</b><span class="tag">{{ w.pickWay === 1 ? '单品' : '多品' }}</span>
              <div class="muted">{{ w.customerName }} · {{ w.orderCount }} 单 · {{ w.totalCount }} 件 · {{ w.pickCartNo }}</div>
            </div>
          </template>
          <template v-else>
            <div class="head"><el-button type="text" @click="closeWave">‹ 返回</el-button><b>{{ wave.waveNo }}</b></div>
            <div v-for="c in cells" :key="c.cellNo + c.productSku" class="item" :class="{ done: c.pickQty === c.planQty }" @click="fillCell(c)">
              <b>{{ c.cellNo }}</b> {{ c.productSku }}<span class="right">{{ c.pickQty }}/{{ c.planQty }}</span>
            </div>
            <el-input v-model="pick.cellNo" size="small" placeholder="扫描库位" class="mt" @keyup.enter.native="doScanCell" />
            <el-input v-model="pick.barcode" size="small" placeholder="扫描产品条码" class="mt" @keyup.enter.native="doScanSku" />
            <div v-if="pick.target" class="muted mt">待拣 {{ pick.target.productSku }}：{{ pick.target.left }} 件</div>
            <el-input-number v-model="pick.qty" size="small" :min="1" class="mt" />
            <el-button type="primary" size="small" class="mt" :disabled="!pick.verified" @click="doPick">确认拣货</el-button>
            <div v-if="pickDone" class="ok mt">波次已拣货完成，出库单进入待复核</div>
          </template>
        </div>
      </div>

      <!-- 出库复核 -->
      <div class="phone">
        <div class="bar">出库复核（一票多件）</div>
        <div class="body">
          <el-input v-model="review.deliveryNo" size="small" placeholder="扫描出库单号，如 OB2611000301" @keyup.enter.native="loadReview" />
          <template v-if="order">
            <div class="muted mt">{{ order.customerName }} · 跟踪号 {{ order.expressNo }}</div>
            <div class="muted">已扫 {{ order.scanOrderSkuQty }}/{{ order.orderSkuAllQty }} · 状态 {{ statusName(order.status) }}</div>
            <div v-for="s in order.skuList" :key="s.productSku" class="item" :class="{ done: s.scanQty === s.allQty }" @click="review.barcode = s.barcode">
              {{ s.productSku }} <span class="muted">{{ s.barcode }}</span><span class="right">{{ s.scanQty }}/{{ s.allQty }}</span>
            </div>
            <el-input v-model="review.barcode" size="small" placeholder="扫描产品条码（每次 1 件）" class="mt" @keyup.enter.native="doReview(false)" />
            <div class="mt">
              <el-button size="small" @click="doReview(true)">一键通过</el-button>
              <el-button type="primary" size="small" :disabled="order.status !== 20" @click="doConfirm">确认出库</el-button>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import * as pda from '../../api/pda.outbound'

const STATUS = { 0: '待拣货', 10: '待复核', 20: '已复核', 30: '已出库' } // 占位编码，与后端 OutboundSampleStore 一致
const emptyPick = () => ({ cellNo: '', barcode: '', qty: 1, target: null, verified: false })

export default {
  data: () => ({ waves: [], wave: null, cells: [], pick: emptyPick(), pickDone: false, review: { deliveryNo: '', barcode: '' }, order: null }),
  created () { this.loadWaves() },
  methods: {
    statusName (s) { return STATUS[s] },
    fail (e) { this.$message.error(e.message) },
    async loadWaves () { try { this.waves = await pda.pickList(0) } catch (e) { this.fail(e) } },
    async openWave (w) { this.wave = w; this.pick = emptyPick(); this.pickDone = false; await this.loadCells() },
    closeWave () { this.wave = null; this.loadWaves() },
    async loadCells () {
      try { this.cells = (await pda.wavePageCell(this.wave.waveNo)).cellPageList.records } catch (e) { this.fail(e) }
    },
    fillCell (c) { if (c.pickQty < c.planQty) { this.pick = { ...emptyPick(), cellNo: c.cellNo }; this.doScanCell() } },
    async doScanCell () {
      try {
        const r = await pda.scanCell({ waveNo: this.wave.waveNo, cellNo: this.pick.cellNo, whCode: 'WH01' })
        this.pick.target = { productSku: r.productSku, left: r.productQty }
        this.pick.qty = r.productQty; this.pick.verified = false
      } catch (e) { this.pick.target = null; this.fail(e) }
    },
    async doScanSku () {
      try {
        await pda.scanSku({ waveNo: this.wave.waveNo, cellNo: this.pick.cellNo, barcode: this.pick.barcode, whCode: 'WH01' })
        this.pick.verified = true
        this.$message.success('条码校验通过')
      } catch (e) { this.pick.verified = false; this.fail(e) }
    },
    async doPick () {
      try {
        const r = await pda.wavePick({
          waveNo: this.wave.waveNo, cellNo: this.pick.cellNo, productSku: this.pick.target.productSku,
          pickQty: this.pick.qty, customerCode: this.wave.customerCode, whCode: 'WH01'
        })
        this.$message.success('拣货成功')
        this.pick = emptyPick(); this.pickDone = r.pickStatus === 2
        await this.loadCells()
      } catch (e) { this.fail(e) }
    },
    async loadReview () {
      try { this.order = await pda.get4MorePiece(this.review.deliveryNo.trim()) } catch (e) { this.order = null; this.fail(e) }
    },
    async doReview (oneClickPass) {
      try {
        await pda.morePiece({
          deliveryNo: this.order.deliveryNo, customerCode: this.order.customerCode, whCode: 'WH01', oneClickPass,
          reviewList: oneClickPass ? [] : [{ barcode: this.review.barcode.trim(), qty: 1, productSnNoList: [] }]
        })
        this.review.barcode = ''
        await this.loadReview()
      } catch (e) { this.fail(e) }
    },
    async doConfirm () {
      try {
        await pda.confirmDelivery({ deliveryNo: this.order.deliveryNo, customerCode: this.order.customerCode, whCode: 'WH01' })
        this.$message.success('已确认出库')
        await this.loadReview()
      } catch (e) { this.fail(e) }
    },
    async resetAll () {
      try {
        await pda.resetDemo()
        this.wave = null; this.order = null; this.review = { deliveryNo: '', barcode: '' }; this.pickDone = false
        await this.loadWaves(); this.$message.success('已重置')
      } catch (e) { this.fail(e) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #888c94; font-size: 12px; max-width: 760px }
.phones { display: flex; gap: 24px; flex-wrap: wrap }
.phone { width: 340px; border: 8px solid #0b1019; border-radius: 24px; overflow: hidden; background: #f5f6f9 }
.bar { background: #005bf5; color: #fff; padding: 10px 14px; font-weight: 600 }
.body { padding: 12px; min-height: 460px; display: flex; flex-direction: column }
.item { background: #fff; border-radius: 6px; padding: 8px 10px; margin-bottom: 6px; cursor: pointer; font-size: 13px }
.item.done { opacity: .5 }
.head { display: flex; align-items: center; gap: 8px; margin-bottom: 6px }
.tag { margin-left: 6px; font-size: 11px; color: #005bf5 }
.right { float: right; font-weight: 600 }
.muted { color: #888c94; font-size: 12px }
.mt { margin-top: 8px }
.ok { color: #28a864; font-size: 13px }
.empty { color: #888c94; text-align: center; margin-top: 40px }
</style>
