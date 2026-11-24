<template>
  <div class="page">
    <el-tabs v-model="tab" @tab-click="switchTab">
      <el-tab-pane name="stock" label="次品库存" />
      <el-tab-pane name="records" label="处理记录" />
    </el-tabs>
    <template v-if="tab === 'stock'">
      <el-form inline @submit.native.prevent="search">
        <el-form-item><el-input v-model="query.keyword" clearable placeholder="SKU / 品名 / 库位" suffix-icon="el-icon-search" style="width:220px" /></el-form-item>
        <el-form-item>
          <el-select v-model="query.customerCode" clearable placeholder="客户" style="width:160px">
            <el-option v-for="(name, code) in customers" :key="code" :value="code" :label="code + ' ' + name" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">查询</el-button>
          <el-button class="btn-reset" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      <p class="hint">次品库存与 PDA 库内、退货上架共用。转良品会加到目标库位的良品库存；销毁直接扣减，需填写处理说明。演示数据只存内存。</p>
      <div class="summary">
        <div class="card"><div class="num">{{ total }}</div><div class="label">次品库存行</div></div>
        <div class="card"><div class="num">{{ skuCount }}</div><div class="label">SKU 数</div></div>
        <div class="card"><div class="num">{{ totalQty }}</div><div class="label">次品件数</div></div>
      </div>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column prop="cellNo" label="库位" width="100" />
        <el-table-column prop="sku" label="SKU" width="110" />
        <el-table-column prop="productName" label="品名" min-width="130" />
        <el-table-column label="客户" min-width="120"><template slot-scope="{row}">{{ row.customerCode }} {{ row.customerName }}</template></el-table-column>
        <el-table-column label="箱型" width="110"><template slot-scope="{row}">{{ row.boxType || '散件' }}</template></el-table-column>
        <el-table-column prop="qty" label="库存" width="70" align="right" />
        <el-table-column prop="freezeQty" label="冻结" width="70" align="right" />
        <el-table-column prop="availQty" label="可用" width="70" align="right" />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="{row}">
            <span class="ops">
              <el-button type="text" :disabled="!row.availQty" @click="open('toGood', row)">转良品</el-button>
              <el-button type="text" class="text-danger" :disabled="!row.availQty" @click="open('destroy', row)">销毁</el-button>
            </span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination background layout="total, prev, pager, next" :total="total"
        :current-page.sync="query.current" :page-size="query.size" @current-change="load" />
    </template>
    <template v-else>
      <el-table v-loading="loading" :data="records" border class="records">
        <el-table-column prop="time" label="处理时间" width="160" />
        <el-table-column label="处理方式" width="90">
          <template slot-scope="{row}"><el-tag size="mini" :type="row.type === '销毁' ? 'danger' : 'success'">{{ row.type }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="cellNo" label="库位" width="100" />
        <el-table-column prop="targetCellNo" label="转入库位" width="100" />
        <el-table-column prop="sku" label="SKU" width="110" />
        <el-table-column prop="qty" label="数量" width="70" align="right" />
        <el-table-column prop="remark" label="处理说明" min-width="160" show-overflow-tooltip />
      </el-table>
      <el-pagination background layout="total, prev, pager, next" :total="recordTotal"
        :current-page.sync="recordQuery.current" :page-size="recordQuery.size" @current-change="loadRecords" />
    </template>

    <el-dialog :title="form.type === 'toGood' ? '转良品' : '销毁次品'" :visible.sync="visible" width="480px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="次品">{{ form.cellNo }} · {{ form.sku }}（可用 {{ form.max }}）</el-form-item>
        <el-form-item label="数量" prop="qty">
          <el-input-number v-model="form.qty" :min="1" :max="form.max" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-form-item v-if="form.type === 'toGood'" label="转入库位">
          <el-input v-model="form.targetCellNo" placeholder="留空表示原库位" clearable />
        </el-form-item>
        <el-form-item label="处理说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="128" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button class="btn-reset" @click="visible = false">取消</el-button>
        <el-button :type="form.type === 'toGood' ? 'primary' : 'danger'" :loading="saving" @click="submit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import * as api from '../../api/stock'
const emptyQuery = () => ({ keyword: '', customerCode: '', current: 1, size: 20 })
export default {
  data: () => ({
    tab: 'stock', query: emptyQuery(), rows: [], total: 0, totalQty: 0, skuCount: 0, customers: {}, loading: false,
    records: [], recordTotal: 0, recordQuery: { current: 1, size: 20 },
    visible: false, saving: false, form: {}
  }),
  computed: {
    rules () {
      return {
        qty: [{ required: true, message: '请填写数量', trigger: 'blur' }],
        remark: this.form.type === 'destroy' ? [{ required: true, message: '销毁必须填写处理说明', trigger: 'blur' }] : []
      }
    }
  },
  created () { this.search() },
  methods: {
    switchTab () { this.tab === 'stock' ? this.load() : this.loadRecords() },
    search () { this.query.current = 1; this.load() },
    reset () { this.query = emptyQuery(); this.search() },
    async load () {
      this.loading = true
      try {
        const p = await api.defectivePage(this.query)
        Object.assign(this, { rows: p.records, total: p.total, totalQty: p.totalQty, skuCount: p.skuCount, customers: p.customers })
      } catch (e) { this.$message.error('后端未连通：' + e.message) } finally { this.loading = false }
    },
    async loadRecords () {
      this.loading = true
      try { const p = await api.defectiveRecords(this.recordQuery); this.records = p.records; this.recordTotal = p.total } catch (e) {
        this.$message.error(e.message)
      } finally { this.loading = false }
    },
    open (type, row) {
      this.form = { type, cellNo: row.cellNo, sku: row.sku, boxType: row.boxType, max: row.availQty, qty: row.availQty, targetCellNo: '', remark: '' }
      this.visible = true
      this.$nextTick(() => this.$refs.form.clearValidate())
    },
    submit () {
      this.$refs.form.validate(async ok => {
        if (!ok) return
        this.saving = true
        try {
          const { type, ...body } = this.form
          await (type === 'toGood' ? api.defectiveToGood(body) : api.defectiveDestroy(body))
          this.$message.success(type === 'toGood' ? '已转为良品' : '已销毁')
          this.visible = false
          this.load()
        } catch (e) { this.$message.error(e.message) } finally { this.saving = false }
      })
    }
  }
}
</script>
<style scoped>
.ops .el-button { padding: 0; }
.ops .el-button + .el-button { margin-left: 12px; }
.text-danger { color: #f5222d; }
.text-danger.is-disabled { color: #c0c4cc; }
.records { margin-top: 16px; }
.summary { display: flex; gap: 12px; margin: 12px 16px; }
.card { flex: 1; padding: 12px 16px; background: #fff; border-radius: 4px; }
.card .num { font-size: 20px; font-weight: 600; }
.card .label { font-size: 12px; color: #888c94; margin-top: 4px; }
</style>
