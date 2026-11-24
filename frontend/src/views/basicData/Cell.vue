<template>
  <div class="page">
    <h3>库位</h3>
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="搜索">
        <el-input v-model="query.keyword" clearable placeholder="库位编码 / 库区名称" style="width:200px" />
      </el-form-item>
      <el-form-item label="库区">
        <el-select v-model="query.areaCode" clearable placeholder="全部" style="width:130px">
          <el-option v-for="a in areas" :key="a" :label="a" :value="a" />
        </el-select>
      </el-form-item>
      <el-form-item label="占用状态">
        <el-select v-model="query.usageStatus" clearable placeholder="全部" style="width:110px">
          <el-option label="空闲" :value="0" /><el-option label="占用" :value="1" /><el-option label="锁定" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="cellCode" label="库位编码" width="130" />
      <el-table-column prop="areaCode" label="库区编码" width="110" />
      <el-table-column prop="areaName" label="库区名称" min-width="130" />
      <el-table-column prop="whCode" label="仓库" width="90" />
      <el-table-column prop="cellType" label="库位类型" width="100" />
      <el-table-column label="占用状态" width="90">
        <template slot-scope="{row}"><el-tag size="mini" :type="usageType(row.usageStatus)">{{ row.usageName }}</el-tag></template>
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
import { cellRecords } from '../../mock/basic'

const emptyQuery = () => ({ keyword: '', areaCode: '', usageStatus: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(), rows: [], total: 0, loading: false, usingMock: false,
    areas: ['AREA-01', 'AREA-02', 'AREA-03']
  }),
  created () { this.search() },
  methods: {
    usageType (s) { return { 0: 'success', 1: 'warning', 2: 'danger' }[s] || 'info' },
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async load () {
      this.loading = true
      try {
        const page = await api.cellPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = cellRecords; this.total = cellRecords.length; this.usingMock = true
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
