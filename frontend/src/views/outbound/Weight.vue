<template>
  <div class="page">
    <p class="hint">扫已复核的出库单号或快递单号，录入重量后确认出库（本地演示，重量限制 0~70 kg）。</p>
    <el-form inline size="small" @submit.native.prevent="ship">
      <el-form-item label="出库单/快递单号"><el-input v-model="no" style="width:200px" @change="load" /></el-form-item>
      <el-form-item label="重量(kg)"><el-input-number v-model="weightKg" :min="0.01" :max="70" :precision="2" :step="0.1" /></el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">称重并出库</el-button></el-form-item>
    </el-form>
    <order-card :order="order" />
  </div>
</template>
<script>
import * as api from '../../api/work'
import OrderCard from './OrderCard.vue'
export default {
  components: { OrderCard },
  data: () => ({ no: '', weightKg: 0.5, order: null }),
  methods: {
    async load () {
      if (!this.no.trim()) return
      try { this.order = await api.reviewOrder(this.no.trim()) } catch (e) { this.order = null; this.$message.error(e.message) }
    },
    async ship () {
      try {
        this.order = await api.weighAndShip({ no: this.no.trim(), weightKg: this.weightKg })
        this.$message.success('已出库')
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
</style>
