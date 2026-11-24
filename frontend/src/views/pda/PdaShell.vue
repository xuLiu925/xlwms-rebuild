<template>
  <div class="page pda-page">
    <p class="hint">PDA 模拟：按安装包还原的接口路径与请求体，只连本地后端的内存示例数据。安装包是网页套壳（wms-pda-web-ui），
      这里的界面是本项目自绘的手机尺寸示意，不是原样复刻。</p>
    <div class="pda-wrap">
      <div class="pda">
        <div class="pda-bar">
          <span class="pda-back" :class="{ hidden: isHome }" @click="back"><i class="el-icon-arrow-left" /></span>
          <span class="pda-title">{{ title }}</span>
          <span class="pda-wh">WH01</span>
        </div>
        <div class="pda-body"><router-view /></div>
      </div>
    </div>
  </div>
</template>
<script>
// 子页面在 router 里注册为 /pda 的 children，标题取 meta.title
export default {
  computed: {
    isHome () { return this.$route.path === '/pda' },
    title () { return this.$route.meta.title || 'PDA' }
  },
  methods: {
    back () { this.$router.push('/pda') }
  }
}
</script>
<style lang="scss">
.pda-wrap { display: flex; justify-content: center; padding: 16px 0 24px; }
.pda {
  width: 360px; height: 680px; background: #f2f4f7; border-radius: 24px; border: 10px solid #1d2433;
  box-shadow: 0 8px 24px rgba(0, 0, 0, .18); display: flex; flex-direction: column; overflow: hidden;
}
.pda-bar {
  height: 48px; flex: none; background: #005bf5; color: #fff; display: flex; align-items: center; padding: 0 8px;
  .pda-back { width: 32px; font-size: 18px; cursor: pointer; &.hidden { visibility: hidden; } }
  .pda-title { flex: 1; text-align: center; font-size: 16px; font-weight: 600; }
  .pda-wh { width: 40px; font-size: 12px; text-align: right; opacity: .85; }
}
.pda-body { flex: 1; overflow-y: auto; padding: 12px; font-size: 14px; color: #0b1019; }
// 子页面共用的小组件样式
.pda-card { background: #fff; border-radius: 8px; padding: 10px 12px; margin-bottom: 8px; }
.pda-card.clickable { cursor: pointer; &:hover { box-shadow: 0 0 0 1px #005bf5 inset; } }
.pda-card.done { opacity: .55; }
.pda-row { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.pda-muted { color: #888c94; font-size: 12px; margin-top: 2px; }
.pda-tag { display: inline-block; font-size: 11px; padding: 0 6px; border-radius: 4px; background: #e6effe; color: #005bf5; margin-left: 6px; }
.pda-tag.warn { background: #fff4e6; color: #f58718; }
.pda-tag.ok { background: #e8f7ef; color: #28a864; }
.pda-scan { margin-bottom: 8px; .el-input__inner { height: 44px; font-size: 15px; } }
.pda-section { font-size: 13px; color: #55585f; margin: 12px 0 6px; font-weight: 600; }
.pda-actions { display: flex; gap: 8px; margin-top: 8px; .el-button { flex: 1; margin: 0; height: 40px; } }
.pda-empty { text-align: center; color: #888c94; padding: 32px 0; }
.pda-ok { background: #e8f7ef; color: #28a864; border-radius: 6px; padding: 8px 10px; margin: 8px 0; font-size: 13px; }
.pda-qty { width: 110px !important; flex: none; }
</style>
