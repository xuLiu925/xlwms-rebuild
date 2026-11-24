<template>
  <div v-if="order">
    <el-descriptions :column="4" size="small" border>
      <el-descriptions-item label="出库单">{{ order.deliveryNo }}</el-descriptions-item>
      <el-descriptions-item label="快递单号">{{ order.expressNo }}</el-descriptions-item>
      <el-descriptions-item label="波次">{{ order.waveNo }}</el-descriptions-item>
      <el-descriptions-item label="状态"><el-tag size="mini" :type="order.status === 30 ? 'success' : order.status === 20 ? 'primary' : 'warning'">{{ order.statusName }}</el-tag></el-descriptions-item>
      <el-descriptions-item label="客户">{{ order.customerName }}</el-descriptions-item>
      <el-descriptions-item label="渠道">{{ order.logisticsChannel }}</el-descriptions-item>
      <el-descriptions-item label="已扫/应扫">{{ order.scanNum }} / {{ order.skuNum }}</el-descriptions-item>
      <el-descriptions-item label="重量">{{ order.weightKg == null ? '—' : order.weightKg + ' kg' }}</el-descriptions-item>
    </el-descriptions>
    <el-table :data="rows" border size="small" class="tbl">
      <el-table-column prop="packNo" label="包裹" width="160" />
      <el-table-column label="包裹复核" width="90">
        <template slot-scope="{row}"><el-tag size="mini" :type="row.reviewStatus ? 'success' : 'info'">{{ row.reviewStatus ? '已复核' : '未复核' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="sku" label="SKU" width="110" />
      <el-table-column prop="barcode" label="条码" width="120" />
      <el-table-column prop="productName" label="品名" />
      <el-table-column label="已扫/应发" width="100" align="right">
        <template slot-scope="{row}">{{ row.scanQty }} / {{ row.qty }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
// 复核 / 按箱复核 / 称重页共用的出库单信息卡（只展示）
export default {
  props: { order: { type: Object, default: null } },
  computed: {
    rows () { return this.order ? this.order.packs.flatMap(p => p.items.map(i => ({ ...i, packNo: p.packNo, reviewStatus: p.reviewStatus }))) : [] }
  }
}
</script>
<style scoped>.tbl { margin-top: 12px }</style>
