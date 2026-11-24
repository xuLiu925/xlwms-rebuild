<template>
  <work-layout title="称重" :empty="!order" empty-text="请扫描出库单号/物流跟踪号开始称重">
    <template #side>
      <div class="work-field">
        <div class="work-label">出库单号/物流跟踪号</div>
        <el-input v-model="no" placeholder="请扫描" clearable @change="load" />
      </div>
      <div class="work-field">
        <div class="work-label">重量(kg)</div>
        <el-input-number v-model="weightKg" :min="0.01" :max="70" :precision="2" :step="0.1" :controls="false" :disabled="!order" />
      </div>
      <div class="work-actions">
        <el-button type="primary" :disabled="!order || order.status !== 20" @click="ship">称重完成</el-button>
        <el-button class="btn-reset" @click="clear">重置</el-button>
      </div>
      <div class="work-tip"><i class="el-icon-warning" />只能称已复核的出库单；本地演示限制 0~70 kg。</div>
    </template>
    <order-card :order="order" />
  </work-layout>
</template>
<script>
import * as api from '../../api/work'
import OrderCard from './OrderCard.vue'
import WorkLayout from '../common/WorkLayout.vue'
export default {
  components: { OrderCard, WorkLayout },
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
    },
    clear () { this.no = ''; this.order = null; this.weightKg = 0.5 }
  }
}
</script>
