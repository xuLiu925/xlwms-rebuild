<template>
  <div class="page">
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item>
        <el-input v-model="query.keyword" clearable placeholder="托盘号 / 库位" style="width:200px" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.whCode" clearable placeholder="仓库" style="width:120px">
          <el-option label="WH01" value="WH01" /><el-option label="WH02" value="WH02" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.status" clearable placeholder="状态" style="width:110px">
          <el-option label="空闲" :value="0" /><el-option label="在用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button class="btn-reset" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border size="small">
      <el-table-column prop="trayNo" label="托盘号" width="130" />
      <el-table-column prop="whCode" label="仓库" width="90" />
      <el-table-column prop="areaCode" label="库区" width="110" />
      <el-table-column prop="cellCode" label="库位" width="120" />
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}"><el-tag size="mini" :type="row.status === 1 ? 'warning' : 'success'">{{ row.statusName }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="skuKinds" label="SKU 种类" width="90" align="right" />
      <el-table-column prop="totalQty" label="总数量" width="90" align="right" />
      <el-table-column prop="bindTime" label="绑定时间" width="160" />
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page.sync="query.current" :page-size.sync="query.size" :page-sizes="[10, 20, 50]"
      @current-change="load" @size-change="search" />
    <p v-if="usingMock" class="hint">后端未连通，当前显示前端内置示例数据。</p>
  </div>
</template>
<script>
import * as api from '../../api/basic'
import { trayRecords } from '../../mock/basic'

const emptyQuery = () => ({ keyword: '', whCode: '', status: '', current: 1, size: 20 })

export default {
  data: () => ({ query: emptyQuery(), rows: [], total: 0, loading: false, usingMock: false }),
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async load () {
      this.loading = true
      try {
        const page = await api.trayPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = trayRecords; this.total = trayRecords.length; this.usingMock = true
      } finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.page { padding: 24px }
.pager { margin-top: 12px; text-align: right }
.hint { color: #f58718; font-size: 12px }
</style>
