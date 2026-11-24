<template>
  <div class="page">
    <el-form inline size="small" @submit.native.prevent="search">
      <el-form-item label="搜索">
        <el-input v-model="query.keyword" clearable placeholder="SKU / 条码 / 名称" style="width:220px" />
      </el-form-item>
      <el-form-item label="客户">
        <el-select v-model="query.customerCode" clearable placeholder="全部" style="width:140px">
          <el-option v-for="c in customers" :key="c.value" :label="c.label" :value="c.value" />
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
      <el-table-column prop="sku" label="SKU" width="110" />
      <el-table-column prop="barcode" label="条码" width="120" />
      <el-table-column prop="productName" label="产品名称" min-width="150" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="customerName" label="客户" min-width="100" />
      <el-table-column label="尺寸(mm)" width="140">
        <template slot-scope="{row}">{{ row.lengthMm }}×{{ row.widthMm }}×{{ row.heightMm }}</template>
      </el-table-column>
      <el-table-column prop="weightG" label="重量(g)" width="90" align="right" />
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
import { productRecords } from '../../mock/basic'

const emptyQuery = () => ({ keyword: '', customerCode: '', status: '', current: 1, size: 20 })

export default {
  data: () => ({
    query: emptyQuery(), rows: [], total: 0, loading: false, usingMock: false,
    customers: [1, 2, 3].map(i => ({ value: 'C00' + i, label: '示例客户' + i }))
  }),
  created () { this.search() },
  methods: {
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async load () {
      this.loading = true
      try {
        const page = await api.productPage(this.query)
        this.rows = page.records; this.total = Number(page.total); this.usingMock = false
      } catch (e) {
        this.rows = productRecords; this.total = productRecords.length; this.usingMock = true
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
