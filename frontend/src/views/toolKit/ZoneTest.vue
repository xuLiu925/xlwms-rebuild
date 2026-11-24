<template>
  <div class="page">
    <p class="hint">按承运商查询起止邮编对应的分区。分区规则是虚构的演示规则。</p>
    <el-form inline size="small" @submit.native.prevent="query">
      <el-form-item label="承运商">
        <el-select v-model="form.carrier" style="width:140px">
          <el-option v-for="c in carriers" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="起始邮编"><el-input v-model="form.originZip" placeholder="留空用仓库邮编" style="width:140px" /></el-form-item>
      <el-form-item label="仓库">
        <el-select v-model="form.whCode" style="width:100px"><el-option label="WH01" value="WH01" /><el-option label="WH02" value="WH02" /></el-select>
      </el-form-item>
      <el-form-item label="目的邮编"><el-input v-model="form.destZip" style="width:110px" /></el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">查询</el-button></el-form-item>
    </el-form>
    <el-card v-if="result" shadow="never" class="res">
      <div class="zone">{{ result.zone }} 区</div>
      <div>{{ result.carrier }}：{{ result.originZip }} → {{ result.destZip }}</div>
      <div class="hint">{{ result.rule }}</div>
    </el-card>
  </div>
</template>
<script>
import * as api from '../../api/work'
export default {
  data: () => ({
    carriers: ['示例承运商A', '示例承运商B', '示例承运商C'],
    form: { carrier: '示例承运商A', originZip: '', whCode: 'WH01', destZip: '10001' },
    result: null
  }),
  methods: {
    async query () {
      try { this.result = await api.zoneQuery(this.form) } catch (e) { this.result = null; this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #888c94; font-size: 12px }
.res { max-width: 420px; margin-top: 12px }
.zone { font-size: 32px; font-weight: 600; color: #005bf5 }
</style>
