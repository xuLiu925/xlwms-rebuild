<template>
  <report-view name="inbound" chart-title="每日收货件数" unit="件" :columns="columns" />
</template>
<script>
import ReportView from './ReportView.vue'
// 报表-入库效率。指标口径为本项目占位，数据为演示推算
export default {
  components: { ReportView },
  data: () => ({
    columns: [
      { prop: 'date', label: '日期', width: 110 }, { prop: 'arrivalOrders', label: '到货单数', num: true },
      { prop: 'receiveQty', label: '收货件数', num: true }, { prop: 'putawayQty', label: '上架件数', num: true },
      { prop: 'avgPutawayHours', label: '平均上架时长(小时)', width: 150, num: true },
      { prop: 'onTimeRate', label: '24 小时上架率', width: 120, num: true, suffix: '%' }
    ]
  })
}
</script>
