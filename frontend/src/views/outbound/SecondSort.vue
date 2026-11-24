<template>
  <div class="page">
    <p class="hint">多品波次拣完后，在播种墙上把产品分到各出库单的格口。示例波次 WV26110004 已拣完；条码 BC00010008、BC00010009。</p>
    <el-form inline size="small" @submit.native.prevent="scan">
      <el-form-item label="波次">
        <el-select v-model="waveNo" placeholder="选择波次" style="width:220px" @change="loadWave">
          <el-option v-for="w in waves" :key="w.waveNo" :label="w.waveNo + '（' + w.pickStatusName + '）'" :value="w.waveNo" />
        </el-select>
      </el-form-item>
      <el-form-item label="扫描条码"><el-input v-model="barcode" style="width:180px" :disabled="!wave" /></el-form-item>
      <el-form-item><el-button type="primary" native-type="submit" :disabled="!wave">分拣</el-button></el-form-item>
    </el-form>
    <el-alert v-if="lastGrid" :closable="false" type="success" :title="'请放入 ' + lastGrid + ' 号格口（' + lastDelivery + '）'" />
    <div v-if="wave" class="grids">
      <el-card v-for="g in wave.grids" :key="g.gridNo" :class="['grid', { hit: g.gridNo === lastGrid, done: g.sortQty === g.qty }]" shadow="never">
        <div class="no">{{ g.gridNo }}</div>
        <div>{{ g.deliveryNo }}</div>
        <div class="qty">{{ g.sortQty }} / {{ g.qty }}</div>
      </el-card>
    </div>
  </div>
</template>
<script>
import * as api from '../../api/work'
export default {
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
      try {
        const r = await api.sortScan({ waveNo: this.waveNo, barcode: this.barcode.trim() })
        this.wave = r; this.lastGrid = r.gridNo; this.lastDelivery = r.deliveryNo; this.barcode = ''
      } catch (e) { this.lastGrid = null; this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
.grids { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 16px }
.grid { width: 170px; text-align: center }
.grid .no { font-size: 28px; font-weight: 600 }
.grid .qty { color: #606266; margin-top: 4px }
.grid.hit { border-color: #409eff; background: #ecf5ff }
.grid.done { border-color: #67c23a }
</style>
