<template>
  <div class="page">
    <h3>{{ config.title }}</h3>
    <el-tabs v-if="config.tabs" v-model="tab" @tab-click="search">
      <el-tab-pane v-for="s in stats" :key="s.status" :name="s.status === '' ? 'all' : String(s.status)" :label="s.statusName + ' ' + s.statusSum" />
    </el-tabs>
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item v-if="kwLabels" label="搜索">
        <el-input v-model="query.keyword" clearable :placeholder="kwLabels" style="width:220px" />
      </el-form-item>
      <el-form-item v-for="c in filterCols" :key="c.name" :label="c.label">
        <el-select v-model="query[c.name]" clearable placeholder="全部" style="width:130px">
          <el-option v-for="o in options(c)" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column v-for="c in config.columns" :key="c.name" :label="c.label" :width="c.width"
        :align="numeric(c) ? 'right' : 'left'">
        <template slot-scope="{row}">
          <el-tag v-if="c.enum" size="mini" :type="tagType(c, row[c.name])">{{ row[c.name + 'Name'] }}</el-tag>
          <span v-else>{{ row[c.name] }}</span>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import request from '../../api/request'

// 通用只读列表页：页面由 config（tools/list-pages/spec.mjs 生成）驱动，接口 POST /{key}/page 与 /{key}/statistics。
export default {
  props: { config: { type: Object, required: true } },
  data () {
    return { query: this.emptyQuery(), tab: 'all', stats: [], rows: [], total: 0, loading: false, usingMock: false }
  },
  computed: {
    kwLabels () { return this.config.columns.filter(c => c.kw).map(c => c.label).join(' / ') },
    // 页签列不再重复出现在下拉筛选里
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
    numeric (c) { return ['int', 'money', 'rate'].includes(c.type) },
    options (c) {
      if (c.enum) return Object.entries(c.enum).map(([v, [label]]) => ({ value: v, label }))
      return (this.config.filterOptions[c.name] || []).map(v => ({ value: v, label: String(v) }))
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
.page { padding: 24px }
.pager { margin-top: 12px; text-align: right }
.hint { color: #e6a23c; font-size: 12px }
</style>
