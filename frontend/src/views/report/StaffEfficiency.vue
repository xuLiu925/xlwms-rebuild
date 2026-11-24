<template>
  <report-view name="staffEfficiency" chart-title="每日作业总件数" unit="件" :columns="columns" />
</template>
<script>
import ReportView from './ReportView.vue'
// 报表-人效看板。指标口径为本项目占位，数据为演示推算
export default {
  components: { ReportView },
  data: () => ({
    columns: [
      { prop: 'rank', label: '排名', width: 60, num: true }, { prop: 'staffName', label: '员工', width: 110 },
      { prop: 'receiveQty', label: '收货件数', num: true }, { prop: 'putawayQty', label: '上架件数', num: true },
      { prop: 'pickQty', label: '拣货件数', num: true }, { prop: 'reviewQty', label: '复核件数', num: true },
      { prop: 'totalQty', label: '合计件数', num: true }, { prop: 'workHours', label: '工时(小时)', num: true },
      { prop: 'qtyPerHour', label: '人效(件/小时)', width: 120, num: true }
    ]
  })
}
</script>
