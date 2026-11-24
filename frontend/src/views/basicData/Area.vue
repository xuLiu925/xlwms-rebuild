<template>
  <div class="page">
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="搜索">
        <el-input v-model="query.keyword" clearable placeholder="库区编码 / 名称" style="width:200px" />
      </el-form-item>
      <el-form-item label="仓库">
        <el-select v-model="query.whCode" clearable placeholder="全部" style="width:120px">
          <el-option label="WH01" value="WH01" /><el-option label="WH02" value="WH02" />
        </el-select>
      </el-form-item>
      <el-form-item label="库区类型">
        <el-select v-model="query.areaType" clearable placeholder="全部" style="width:120px">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="areaCode" label="库区编码" width="120" />
      <el-table-column prop="areaName" label="库区名称" min-width="140" />
      <el-table-column prop="whCode" label="仓库" width="90" />
      <el-table-column prop="areaType" label="类型" width="100" />
      <el-table-column prop="cellCount" label="库位数" width="90" align="right" />
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}"><el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.statusName }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="120" />
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/basic'
import { areaRecords } from '../../mock/basic'

const emptyQuery = () => ({ keyword: '', whCode: '', areaType: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(), rows: [], total: 0, loading: false, usingMock: false,
    types: ['存储区', '拣选区', '暂存区', '退货区']
  }),
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async load () {
      this.loading = true
      try {
        const page = await api.areaPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = areaRecords; this.total = areaRecords.length; this.usingMock = true
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
