<template>
  <div class="page">
    <p class="hint">配置项是本项目假设的示例，只保存在本地后端内存里，重启后恢复默认值。</p>
    <el-form v-if="items.length" label-width="160px" size="small" style="max-width:560px">
      <template v-for="g in groups">
        <el-divider :key="'g-' + g" content-position="left">{{ g }}</el-divider>
        <el-form-item v-for="i in items.filter(x => x.group === g)" :key="i.key" :label="i.label">
          <el-switch v-if="i.type === 'bool'" v-model="form[i.key]" />
          <el-input-number v-else-if="i.type === 'int'" v-model="form[i.key]" :min="i.min" :max="i.max" />
          <el-select v-else v-model="form[i.key]" style="width:200px">
            <el-option v-for="o in i.options" :key="o" :label="o" :value="o" />
          </el-select>
        </el-form-item>
      </template>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
        <el-button @click="load">撤销修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import * as api from '../../api/work'
export default {
  data: () => ({ items: [], form: {}, saving: false }),
  computed: { groups () { return [...new Set(this.items.map(i => i.group))] } },
  created () { this.load() },
  methods: {
    apply (list) {
      this.items = list
      this.form = Object.fromEntries(list.map(i => [i.key, i.value]))
    },
    async load () {
      try { this.apply(await api.getGlobalSetting()) } catch (e) { this.$message.error('后端未连通：' + e.message) }
    },
    async save () {
      this.saving = true
      try { this.apply(await api.saveGlobalSetting(this.form)); this.$message.success('已保存（本地内存）') } catch (e) { this.$message.error(e.message) } finally { this.saving = false }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.hint { color: #888c94; font-size: 12px }
</style>
