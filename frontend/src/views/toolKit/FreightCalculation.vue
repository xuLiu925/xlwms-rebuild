<template>
  <div class="page">
    <h3>运费试算</h3>
    <p class="hint">渠道、费率、材积除数全部虚构，仅演示“实重/材积重取大 → 分区 → 基础运费 + 燃油费”的计算流程。</p>
    <el-form inline size="small" @submit.native.prevent="calc">
      <el-form-item label="发货仓">
        <el-select v-model="form.whCode" style="width:100px"><el-option label="WH01" value="WH01" /><el-option label="WH02" value="WH02" /></el-select>
      </el-form-item>
      <el-form-item label="目的邮编"><el-input v-model="form.zipCode" placeholder="5 位数字" style="width:110px" /></el-form-item>
      <el-form-item label="重量(kg)"><el-input-number v-model="form.weightKg" :min="0.01" :max="1000" :precision="2" /></el-form-item>
      <el-form-item label="长×宽×高(cm)">
        <el-input-number v-model="form.lengthCm" :min="0" :max="1000" controls-position="right" style="width:90px" />
        <el-input-number v-model="form.widthCm" :min="0" :max="1000" controls-position="right" style="width:90px" />
        <el-input-number v-model="form.heightCm" :min="0" :max="1000" controls-position="right" style="width:90px" />
      </el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">试算</el-button></el-form-item>
    </el-form>
    <p v-if="result" class="hint">起始邮编 {{ result.originZip }} → 目的邮编 {{ result.destZip }}</p>
    <el-table v-if="result" :data="result.quotes" border size="small">
      <el-table-column prop="channelName" label="渠道" width="110" />
      <el-table-column prop="carrier" label="承运商" width="120" />
      <el-table-column prop="zone" label="分区" width="60" align="right" />
      <el-table-column prop="volumeWeightKg" label="材积重(kg)" width="100" align="right" />
      <el-table-column prop="chargeWeightKg" label="计费重(kg)" width="100" align="right" />
      <el-table-column prop="baseFee" label="基础运费" width="90" align="right" />
      <el-table-column prop="fuelFee" label="燃油费" width="80" align="right" />
      <el-table-column label="合计" width="110" align="right">
        <template slot-scope="{row}"><b v-if="row.available">{{ row.totalFee }} {{ row.currency }}</b><span v-else class="muted">{{ row.reason }}</span></template>
      </el-table-column>
      <el-table-column prop="days" label="时效" />
    </el-table>
  </div>
</template>
<script>
import * as api from '../../api/work'
export default {
  data: () => ({ form: { whCode: 'WH01', zipCode: '10001', weightKg: 1.2, lengthCm: 30, widthCm: 20, heightCm: 15 }, result: null }),
  methods: {
    async calc () {
      try { this.result = await api.freightCalculate(this.form) } catch (e) { this.result = null; this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #909399; font-size: 12px }
.muted { color: #909399 }
</style>
