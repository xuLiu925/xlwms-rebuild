<template>
  <div class="page">
    <el-tabs v-if="config.tabs" v-model="tab" @tab-click="search">
      <el-tab-pane v-for="s in stats" :key="s.status" :name="s.status === '' ? 'all' : String(s.status)" ><span slot="label">{{ s.statusName }}<span class="label-num">({{ s.statusSum }})</span></span></el-tab-pane>
    </el-tabs>
    <!-- 查询条：和参考系统一样不用外置标签，字段名写在占位文字里 -->
    <el-form inline @submit.native.prevent="search">
      <el-form-item v-if="kwLabels">
        <el-input v-model="query.keyword" clearable :placeholder="kwLabels" suffix-icon="el-icon-search" style="width:240px" />
      </el-form-item>
      <el-form-item v-for="c in filterCols" :key="c.name">
        <el-select v-model="query[c.name]" clearable :placeholder="c.label" style="width:140px">
          <el-option v-for="o in options(c)" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button class="btn-reset" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="canEdit" class="list-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="openForm(null)">新建</el-button>
      <el-button :disabled="!selected.length" @click="remove(selected)">删除{{ selected.length ? '（' + selected.length + '）' : '' }}</el-button>
    </div>
    <el-table v-loading="loading" :data="rows" border @selection-change="v => (selected = v)">
      <el-table-column v-if="canEdit" type="selection" width="44" />
      <el-table-column v-for="c in config.columns" :key="c.name" :label="c.label" :min-width="c.width"
        :align="numeric(c) ? 'right' : 'left'" show-overflow-tooltip>
        <template slot-scope="{row}">
          <el-tag v-if="c.enum" size="mini" :type="tagType(c, row[c.name])">{{ row[c.name + 'Name'] }}</el-tag>
          <span v-else>{{ row[c.name] }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="canEdit" label="操作" width="110" fixed="right">
        <template slot-scope="{row}">
          <span class="ops">
            <el-button type="text" @click="openForm(row)">编辑</el-button>
            <el-button type="text" class="text-danger" @click="remove([row])">删除</el-button>
          </span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据{{ config.editable ? '，暂不能编辑' : '' }}。</p>

    <!-- 批次 8：本地新建/编辑，只写本地开发库；表单由列定义生成，时间列由后端维护 -->
    <el-dialog :title="(form.id ? '编辑' : '新建') + config.title" :visible.sync="formVisible" width="560px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" @submit.native.prevent="save">
        <el-form-item v-for="c in formCols" :key="c.name" :label="c.label" :prop="c.name">
          <el-select v-if="c.enum" v-model="form[c.name]" style="width:100%">
            <el-option v-for="o in options(c)" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
          <el-input-number v-else-if="numeric(c)" v-model="form[c.name]" :min="0" :max="c.type === 'int' ? 1000000 : 99999999"
            :precision="precision(c)" :step="c.type === 'rate' ? 0.01 : 1" controls-position="right" style="width:100%" />
          <el-input v-else v-model="form[c.name]" maxlength="128" clearable />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button class="btn-reset" @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from '../../api/request'

// 通用列表页（editable 的资源可本地新建/编辑/删除）：页面由 config（tools/list-pages/spec.mjs 生成）驱动，接口 POST /{key}/page 与 /{key}/statistics。
export default {
  props: { config: { type: Object, required: true } },
  data () {
    return { query: this.emptyQuery(), tab: 'all', stats: [], rows: [], total: 0, loading: false, usingMock: false,
      selected: [], formVisible: false, form: {}, saving: false }
  },
  computed: {
    kwLabels () { return this.config.columns.filter(c => c.kw).map(c => c.label).join(' / ') },
    // 页签列不再重复出现在下拉筛选里
    canEdit () { return this.config.editable && !this.usingMock },
    formCols () { return this.config.columns.filter(c => !c.auto) },
    rules () {
      const r = {}
      this.formCols.filter(c => c.required).forEach(c => {
        r[c.name] = [{ required: true, message: '请填写' + c.label, trigger: c.enum ? 'change' : 'blur' }]
      })
      return r
    },
    filterCols () { return this.config.columns.filter(c => (c.filter || c.enum) && c.name !== this.config.tabs) }
  },
  watch: { config () { this.reset() } },
  created () { this.search() },
  methods: {
    emptyQuery () {
      const q = { keyword: '', current: 1, size: 20 }
      this.config.columns.forEach(c => { if (c.filter || c.enum) q[c.name] = '' })
      return q
    },
    numeric (c) { return ['int', 'num', 'money', 'rate'].includes(c.type) },
    options (c) {
      if (c.enum) return Object.entries(c.enum).map(([v, [label]]) => ({ value: v, label }))
      return (this.config.filterOptions[c.name] || []).map(v => ({ value: v, label: String(v) }))
    },
    // 新建时枚举默认值：有“启用”选项就选它，否则选第一个
    defaultEnum (c) {
      const keys = Object.keys(c.enum)
      return keys.find(k => c.enum[k][0] === '启用') || keys[0]
    },
    precision (c) { return c.type === 'int' ? 0 : c.type === 'rate' ? 6 : 2 },
    openForm (row) {
      const f = { id: row ? row.id : '' }
      this.formCols.forEach(c => {
        const v = row ? row[c.name] : undefined
        if (c.enum) f[c.name] = v === undefined || v === null ? this.defaultEnum(c) : String(v)
        else if (this.numeric(c)) f[c.name] = v === undefined || v === null || v === '' ? undefined : Number(v)
        else f[c.name] = v || ''
      })
      this.form = f
      this.formVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    save () {
      this.$refs.form.validate(async ok => {
        if (!ok) return
        const body = { ...this.form }
        if (!body.id) delete body.id
        this.formCols.forEach(c => { if (body[c.name] === undefined) body[c.name] = '' })
        this.saving = true
        try {
          await request.post('/' + this.config.key + (body.id ? '/update' : '/add'), body)
          this.$message.success('已保存')
          this.formVisible = false
          this.load()
        } catch (e) {
          this.$message.error(e.message)
        } finally { this.saving = false }
      })
    },
    async remove (rows) {
      try {
        await this.$confirm('确定删除选中的 ' + rows.length + ' 条记录？删除后不可恢复（重启后端会重新灌入示例数据）。', '删除', { type: 'warning' })
      } catch (e) { return }
      try {
        await request.post('/' + this.config.key + '/delete', { ids: rows.map(r => r.id) })
        this.$message.success('已删除')
        this.load()
      } catch (e) {
        this.$message.error(e.message)
      }
    },
    tagType (c, v) { const e = c.enum[v]; return e ? e[1] : 'info' },
    body () {
      const b = { ...this.query }
      if (this.config.tabs) b[this.config.tabs] = this.tab === 'all' ? '' : this.tab
      return b
    },
    search () { this.query.current = 1; this.load() },
    reset () { this.query = this.emptyQuery(); this.tab = 'all'; this.search() },
    async load () {
      this.loading = true
      try {
        const base = '/' + this.config.key
        const [page, stats] = await Promise.all([
          request.post(base + '/page', this.body()),
          this.config.tabs ? request.post(base + '/statistics', { ...this.query, [this.config.tabs]: '' }) : Promise.resolve([])
        ])
        this.rows = page.records; this.total = Number(page.total); this.stats = stats; this.usingMock = false
      } catch (e) {
        const t = this.config.tabs
        this.rows = t && this.tab !== 'all' ? this.config.mock.filter(r => String(r[t]) === this.tab) : this.config.mock
        this.total = this.rows.length
        this.stats = t ? [{ status: '', statusName: '全部', statusSum: this.config.mock.length }].concat(
          Object.entries(this.config.columns.find(c => c.name === t).enum).map(([v, [label]]) =>
            ({ status: v, statusName: label, statusSum: this.config.mock.filter(r => String(r[t]) === v).length }))) : []
        this.usingMock = true
      } finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.hint { color: #f58718; font-size: 12px }
.list-toolbar { padding: 12px 0 0; }
.list-toolbar + .el-table { margin-top: 12px; }
.ops .el-button { padding: 0; }
.ops .el-button + .el-button { margin-left: 12px; }
.text-danger { color: #f5222d; }
</style>
