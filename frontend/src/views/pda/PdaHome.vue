<template>
  <div>
    <div class="tiles">
      <div v-for="t in tiles" :key="t.path" class="tile" @click="$router.push(t.path)">
        <i :class="t.icon" :style="{ color: t.color }" />
        <div class="name">{{ t.name }}</div>
        <div class="desc">{{ t.desc }}</div>
      </div>
    </div>
    <div class="pda-section">演示数据</div>
    <div class="pda-card">
      <div class="pda-muted">收货会生成上架单，上架写入库存，盘点完成会按实盘数调整库存，各页面共用同一份内存数据。</div>
      <el-button size="small" class="reset" @click="reset">重置全部演示数据</el-button>
    </div>
  </div>
</template>
<script>
import { resetAll } from '../../api/pda'
export default {
  data: () => ({
    tiles: [
      { path: '/pda/receipt', name: '收货', desc: '按箱 / 按箱型 / 按 SKU', icon: 'el-icon-download', color: '#005bf5' },
      { path: '/pda/putaway', name: '上架', desc: '按 SKU 或整单上架', icon: 'el-icon-top', color: '#28a864' },
      { path: '/pda/check', name: '盘点', desc: '明盘 / 盲盘', icon: 'el-icon-finished', color: '#f58718' },
      { path: '/pda/stock', name: '库存查询', desc: '查产品、查库位、移库', icon: 'el-icon-search', color: '#7a4cf5' },
      { path: '/pdaDemo', name: '拣货与复核', desc: '波次拣货、一票多件复核', icon: 'el-icon-shopping-cart-2', color: '#df3148' }
    ]
  }),
  methods: {
    async reset () {
      try {
        await this.$confirm('把收货、上架、库存、盘点等全部内存示例数据恢复到初始状态？', '重置演示数据', { type: 'warning' })
      } catch (e) { return }
      try { await resetAll(); this.$message.success('已重置') } catch (e) { this.$message.error(e.message) }
    }
  }
}
</script>
<style scoped>
.tiles { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.tile { background: #fff; border-radius: 8px; padding: 14px 12px; cursor: pointer; }
.tile:hover { box-shadow: 0 0 0 1px #005bf5 inset; }
.tile i { font-size: 24px; }
.name { font-size: 15px; font-weight: 600; margin-top: 6px; }
.desc { font-size: 12px; color: #888c94; margin-top: 2px; }
.reset { margin-top: 8px; width: 100%; }
</style>
