<template>
  <work-layout title="包裹复核" :empty="!order" empty-text="请扫描包裹号">
    <template #side>
      <div class="work-tip"><i class="el-icon-warning" />整箱视为复核通过（本项目假设的规则）。示例：OB2611000401-1、OB2611000401-2、OB2611000402-1。</div>
      <div class="work-field">
        <div class="work-label">包裹号</div>
        <el-input v-model="packNo" placeholder="请扫描" clearable @keyup.enter.native="scan" />
      </div>
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
  data: () => ({ packNo: '', order: null }),
  methods: {
    async scan () {
      if (!this.packNo.trim()) return
      try {
        this.order = await api.boxReviewScan({ packNo: this.packNo.trim() })
        this.$message.success(this.order.status === 20 ? '整单复核完成' : '包裹已复核')
        this.packNo = ''
      } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
