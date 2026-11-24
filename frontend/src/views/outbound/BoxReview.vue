<template>
  <div class="page">
    <p class="hint">扫包裹号，整箱视为复核通过（本项目假设的规则）。示例：OB2611000401-1、OB2611000401-2、OB2611000402-1。</p>
    <el-form inline size="small" @submit.native.prevent="scan">
      <el-form-item><el-input v-model="packNo" style="width:220px"  placeholder="包裹号"/></el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">复核</el-button></el-form-item>
    </el-form>
    <order-card :order="order" />
  </div>
</template>
<script>
import * as api from '../../api/work'
import OrderCard from './OrderCard.vue'
export default {
  components: { OrderCard },
  data: () => ({ packNo: '', order: null }),
  methods: {
    async scan () {
      try {
        this.order = await api.boxReviewScan({ packNo: this.packNo.trim() })
        this.$message.success(this.order.status === 20 ? '整单复核完成' : '包裹已复核')
        this.packNo = ''
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
</style>
