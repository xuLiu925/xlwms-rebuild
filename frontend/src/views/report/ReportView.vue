<template>
  <div class="page">
    <el-form inline @submit.native.prevent="load">
      <el-form-item>
        <el-date-picker v-model="range" type="daterange" value-format="yyyy-MM-dd" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期" :clearable="false" :picker-options="pickerOptions" style="width:260px" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="whCode" clearable placeholder="全部仓库" style="width:130px">
          <el-option label="WH01" value="WH01" /><el-option label="WH02" value="WH02" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" native-type="submit">查询</el-button></el-form-item>
    </el-form>
    <p class="hint">演示数据：没有真实作业流水，数字按日期和仓库推算，同样的条件总得到同样的结果。区间最多 31 天。</p>

    <div v-loading="loading" class="cards">
      <div v-for="c in data.cards" :key="c.label" class="card">
        <div class="label">{{ c.label }}</div>
        <div class="num">{{ fmt(c.value) }}<span class="unit">{{ c.unit }}</span></div>
      </div>
    </div>

    <!-- 单序列柱状图：每天一根，悬停显示数值；下方表格即数据视图 -->
    <div class="chart-box">
      <div class="chart-title">{{ chartTitle }}</div>
      <div class="chart">
        <div class="y-axis">
          <span v-for="t in ticks" :key="t" :style="{ bottom: t / yMax * 100 + '%' }">{{ fmt(t) }}</span>
        </div>
        <div class="plot">
          <div v-for="t in ticks" :key="'g' + t" class="grid" :style="{ bottom: t / yMax * 100 + '%' }" />
          <div class="bars">
            <div v-for="(p, i) in series" :key="p.date" class="slot" @mouseenter="hover = i" @mouseleave="hover = -1">
              <div class="bar" :class="{ dim: hover >= 0 && hover !== i }" :style="{ height: p.value / yMax * 100 + '%' }" />
              <div v-if="hover === i" class="tip" :style="tipStyle(i)">
                <div class="tip-date">{{ p.date }}</div>
                <div><b>{{ fmt(p.value) }}</b> {{ unit }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="x-axis">
        <span v-for="(p, i) in series" :key="'x' + p.date" :style="{ visibility: showX(i) ? 'visible' : 'hidden' }">{{ p.date.slice(5) }}</span>
      </div>
    </div>

    <el-table v-loading="loading" :data="data.rows" border class="table">
      <el-table-column v-for="c in columns" :key="c.prop" :prop="c.prop" :label="c.label" :min-width="c.width || 100"
        :align="c.num ? 'right' : 'left'">
        <template slot-scope="{row}">{{ c.num ? fmt(row[c.prop]) : row[c.prop] }}{{ c.suffix || '' }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import { reportOf } from '../../api/report'
const iso = d => new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10)
export default {
  props: {
    name: { type: String, required: true },
    columns: { type: Array, required: true },
    chartTitle: { type: String, required: true },
    unit: { type: String, default: '' }
  },
  data () {
    const end = new Date()
    const start = new Date(end.getTime() - 6 * 86400000)
    return {
      range: [iso(start), iso(end)], whCode: '', data: { cards: [], series: [], rows: [] }, loading: false, hover: -1,
      pickerOptions: { disabledDate: d => d.getTime() > Date.now() }
    }
  },
  computed: {
    series () { return this.data.series || [] },
    // 纵轴取整到“好看”的刻度，4 段
    yMax () {
      const max = Math.max(1, ...this.series.map(p => p.value))
      const step = Math.pow(10, Math.floor(Math.log10(max / 4)))
      const nice = [1, 2, 2.5, 5, 10].map(m => m * step).find(s => s * 4 >= max)
      return nice * 4
    },
    ticks () { return [0, 1, 2, 3, 4].map(i => this.yMax / 4 * i) }
  },
  created () { this.load() },
  methods: {
    // 后端 long 按字符串输出（防 JS 精度丢失），这里统一转成数字再加千分位
    fmt (v) {
      const n = typeof v === 'string' && /^-?\d+(\.\d+)?$/.test(v) ? Number(v) : v
      return typeof n === 'number' ? n.toLocaleString('zh-CN', { maximumFractionDigits: 2 }) : v
    },
    // 日期多时隔几个显示一个，避免横轴文字重叠
    showX (i) { const n = this.series.length; const every = Math.ceil(n / 10); return i % every === 0 || i === n - 1 },
    tipStyle (i) { return i > this.series.length / 2 ? { right: '50%' } : { left: '50%' } },
    async load () {
      this.loading = true
      try {
        this.data = await reportOf(this.name, { startDate: this.range[0], endDate: this.range[1], whCode: this.whCode })
      } catch (e) {
        this.$message.error(e.message)
      } finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.cards { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 16px; }
.card { flex: 1 1 160px; background: #fff; border-radius: 4px; padding: 16px; }
.card .label { font-size: 13px; color: #55585f; }
.card .num { font-size: 24px; font-weight: 600; color: #0b1019; margin-top: 8px; white-space: nowrap; }
.card .unit { font-size: 12px; font-weight: 400; color: #888c94; margin-left: 4px; }
.chart-box { background: #fff; border-radius: 4px; padding: 16px 16px 8px; margin-top: 16px; }
.chart-title { font-size: 14px; font-weight: 600; color: #0b1019; margin-bottom: 12px; }
.chart { display: flex; height: 220px; }
.y-axis { position: relative; width: 56px; flex: none; }
.y-axis span { position: absolute; right: 8px; transform: translateY(50%); font-size: 12px; color: #888c94; }
.plot { position: relative; flex: 1; border-bottom: 1px solid #d5d8de; }
.grid { position: absolute; left: 0; right: 0; border-top: 1px solid #f0f2f5; }
.bars { position: absolute; inset: 0; display: flex; align-items: flex-end; }
.slot { position: relative; flex: 1; height: 100%; display: flex; align-items: flex-end; justify-content: center; padding: 0 1px; cursor: default; }
.bar { width: 60%; max-width: 32px; background: #005bf5; border-radius: 4px 4px 0 0; transition: opacity .15s; }
.bar.dim { opacity: .45; }
.tip { position: absolute; top: 8px; z-index: 2; white-space: nowrap; background: #fff; border: 1px solid #e6e8eb;
  box-shadow: 0 4px 12px rgba(0, 0, 0, .08); border-radius: 4px; padding: 6px 10px; font-size: 12px; color: #0b1019; pointer-events: none; }
.tip-date { color: #888c94; margin-bottom: 2px; }
.x-axis { display: flex; margin-left: 56px; padding-top: 6px; }
.x-axis span { flex: 1; text-align: center; font-size: 12px; color: #888c94; }
.table { margin-top: 16px; }
</style>
