<template>
  <div class="page">
    <h3>复核 <el-button size="mini" @click="reset">重置出库演示数据</el-button></h3>
    <p class="hint">扫出库单号或快递单号，再逐件扫产品条码。示例：OB2611000301（单件）、OB2611000401（两个包裹）。与 PDA 出库模拟共用本地状态。</p>
    <el-form inline size="small" @submit.native.prevent="load">
      <el-form-item label="出库单/快递单号"><el-input v-model="no" style="width:200px" /></el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">查询</el-button></el-form-item>
    </el-form>
    <el-form v-if="order && order.status === 10" inline size="small" @submit.native.prevent="scan">
      <el-form-item label="扫描条码"><el-input ref="barcode" v-model="barcode" style="width:200px" /></el-form-item>
      <el-form-item><el-button type="success" native-type="submit">确认</el-button></el-form-item>
    </el-form>
    <el-alert v-if="order && order.status >= 20" :closable="false" type="success" :title="order.status === 20 ? '复核完成，可到“称重出库”页称重出库' : '已出库'" />
    <order-card :order="order" class="card" />
  </div>
</template>
<script>
import * as api from '../../api/work'
import OrderCard from './OrderCard.vue'
export default {
  components: { OrderCard },
  data: () => ({ no: '', barcode: '', order: null }),
  methods: {
    async load () {
      try { this.order = await api.reviewOrder(this.no.trim()); this.$nextTick(() => this.$refs.barcode && this.$refs.barcode.focus()) } catch (e) { this.order = null; this.$message.error(e.message) }
    },
    async scan () {
      try { this.order = await api.reviewScan({ no: this.order.deliveryNo, barcode: this.barcode.trim() }); this.barcode = '' } catch (e) { this.$message.error(e.message) }
    },
    async reset () { await api.resetOutbound(); this.order = null; this.$message.success('已重置') }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
.card { margin-top: 12px }
</style>
