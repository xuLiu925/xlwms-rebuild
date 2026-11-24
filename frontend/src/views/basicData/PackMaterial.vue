<template>
  <div class="page">
    <h3>包材</h3>
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="搜索">
        <el-input v-model="query.keyword" clearable placeholder="包材编码 / 名称" style="width:200px" />
      </el-form-item>
      <el-form-item label="包材类型">
        <el-select v-model="query.materialType" clearable placeholder="全部" style="width:120px">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width:110px">
          <el-option label="启用" :value="1" /><el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="materialCode" label="包材编码" width="120" />
      <el-table-column prop="materialName" label="名称" min-width="140" />
      <el-table-column prop="materialType" label="类型" width="100" />
      <el-table-column prop="spec" label="规格" width="120" />
      <el-table-column prop="unit" label="单位" width="70" />
      <el-table-column prop="stockQty" label="库存" width="90" align="right" />
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}"><el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.statusName }}</el-tag></template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/basic'
import { packMaterialRecords } from '../../mock/basic'

const emptyQuery = () => ({ keyword: '', materialType: '', status: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(), rows: [], total: 0, loading: false, usingMock: false,
    types: ['纸箱', '快递袋', '气泡袋', '封箱胶']
  }),
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async load () {
      this.loading = true
      try {
        const page = await api.packMaterialPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = packMaterialRecords; this.total = packMaterialRecords.length; this.usingMock = true
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
