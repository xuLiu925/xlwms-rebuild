<template>
  <work-layout title="二次分拣" :empty="!wave" empty-text="扫描波次单号开始分拣" empty-icon="el-icon-s-grid">
    <template #side>
      <div class="work-tip"><i class="el-icon-warning" />多品波次拣完后，把产品分到各出库单的格口。示例波次 WV26110004 已拣完；条码 BC00010008、BC00010009。</div>
      <div class="work-field">
        <div class="work-label">波次单号</div>
        <el-select v-model="waveNo" filterable placeholder="请扫描或选择" @change="loadWave">
          <el-option v-for="w in waves" :key="w.waveNo" :label="w.waveNo + '（' + w.pickStatusName + '）'" :value="w.waveNo" />
        </el-select>
      </div>
      <div class="work-field">
        <div class="work-label">Barcode</div>
        <el-input v-model="barcode" placeholder="请扫描" :disabled="!wave" @keyup.enter.native="scan" />
      </div>
    </template>
    <template v-if="wave">
      <div class="work-section-title">播种墙 · {{ wave.waveNo }}</div>
      <div v-if="lastGrid" class="hit-tip">请放入 <b>{{ lastGrid }}</b> 号格口（{{ lastDelivery }}）</div>
      <div class="grids">
        <div v-for="g in wave.grids" :key="g.gridNo" :class="['grid', { hit: g.gridNo === lastGrid, done: g.sortQty === g.qty }]">
          <div class="no">{{ g.gridNo }}</div>
          <div class="dn">{{ g.deliveryNo }}</div>
          <div class="qty">{{ g.sortQty }} / {{ g.qty }}</div>
        </div>
      </div>
    </template>
  </work-layout>
</template>
<script>
import * as api from '../../api/work'
import WorkLayout from '../common/WorkLayout.vue'
export default {
  components: { WorkLayout },
  data: () => ({ waves: [], waveNo: '', wave: null, barcode: '', lastGrid: null, lastDelivery: '' }),
  async created () {
    try { this.waves = await api.sortWaves() } catch (e) { this.$message.error('后端未连通：' + e.message) }
  },
  methods: {
    async loadWave () {
      this.lastGrid = null
      try { this.wave = await api.sortWave(this.waveNo) } catch (e) { this.$message.error(e.message) }
    },
    async scan () {
      if (!this.barcode.trim()) return
      try {
        const r = await api.sortScan({ waveNo: this.waveNo, barcode: this.barcode.trim() })
        this.wave = r; this.lastGrid = r.gridNo; this.lastDelivery = r.deliveryNo; this.barcode = ''
      } catch (e) { this.lastGrid = null; this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.hit-tip { background: rgba(0, 91, 245, .08); color: #0b1019; border-radius: 4px; padding: 10px 12px; margin-bottom: 12px }
.hit-tip b { color: #005bf5; font-size: 18px }
.grids { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px }
.grid { border: 1px solid #e6e8eb; border-radius: 4px; padding: 16px; text-align: center; background: #fff }
.grid .no { font-size: 32px; font-weight: 600; color: #0b1019 }
.grid .dn { color: #55585f; font-size: 12px; margin-top: 4px }
.grid .qty { color: #0b1019; margin-top: 8px; font-weight: 600 }
.grid.hit { border-color: #005bf5; background: rgba(0, 91, 245, .06) }
.grid.done { border-color: #28a864 }
.grid.done .qty { color: #28a864 }
</style>
