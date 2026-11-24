<template>
  <report-view name="outbound" chart-title="每日订单数" unit="单" :columns="columns" />
</template>
<script>
import ReportView from './ReportView.vue'
// 报表-出库效率。指标口径为本项目占位，数据为演示推算
export default {
  components: { ReportView },
  data: () => ({
    columns: [
      { prop: 'date', label: '日期', width: 110 }, { prop: 'orders', label: '订单数', num: true },
      { prop: 'pickQty', label: '拣货件数', num: true }, { prop: 'shippedOrders', label: '出库单数', num: true },
      { prop: 'avgShipHours', label: '平均出库时长(小时)', width: 150, num: true },
      { prop: 'sameDayRate', label: '当日出库率', width: 110, num: true, suffix: '%' }
    ]
  })
}
</script>
