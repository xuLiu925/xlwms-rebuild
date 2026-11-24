<template>
  <work-layout title="复核验货" :empty="!order" empty-text="请扫描出库单号开始复核">
    <template #side>
      <div class="work-tip"><i class="el-icon-warning" />本地演示：示例 OB2611000301（单件）、OB2611000401（两个包裹）。与 PDA 出库模拟共用状态。</div>
      <div class="work-field">
        <div class="work-label">出库单号/快递单号</div>
        <el-input v-model="no" placeholder="请扫描" clearable @keyup.enter.native="load" />
      </div>
      <div class="work-field">
        <div class="work-label">Barcode</div>
        <el-input ref="barcode" v-model="barcode" placeholder="请扫描" :disabled="!order || order.status !== 10" @keyup.enter.native="scan" />
      </div>
    </template>
    <template #footer>
      <el-button type="text" @click="reset">重置出库演示数据</el-button>
    </template>
    <el-alert v-if="order && order.status >= 20" class="done" :closable="false" type="success" show-icon
      :title="order.status === 20 ? '复核完成，可到“称重”页称重出库' : '已出库'" />
    <order-card :order="order" />
  </work-layout>
</template>
<script>
import * as api from '../../api/work'
import OrderCard from './OrderCard.vue'
import WorkLayout from '../common/WorkLayout.vue'
export default {
  components: { OrderCard, WorkLayout },
  data: () => ({ no: '', barcode: '', order: null }),
  methods: {
    async load () {
      if (!this.no.trim()) return
      try { this.order = await api.reviewOrder(this.no.trim()); this.$nextTick(() => this.$refs.barcode && this.$refs.barcode.focus()) } catch (e) { this.order = null; this.$message.error(e.message) }
    },
    async scan () {
      if (!this.barcode.trim()) return
      try { this.order = await api.reviewScan({ no: this.order.deliveryNo, barcode: this.barcode.trim() }); this.barcode = '' } catch (e) { this.$message.error(e.message) }
    },
    async reset () { await api.resetOutbound(); this.order = null; this.no = ''; this.$message.success('已重置') }
  }
}
</script>
<style scoped>
.done { margin-bottom: 12px }
</style>
