<template>
  <!-- 作业页外壳：左侧 300px 操作栏（标题、提示、扫描框、底部按钮），右侧白色结果区；布局参考领星 WMS 的复核/称重页 -->
  <div class="work-page">
    <aside class="work-side">
      <div class="work-title">
        <span>{{ title }}</span>
        <slot name="title-extra" />
      </div>
      <div class="work-body"><slot name="side" /></div>
      <div v-if="$slots.footer" class="work-footer"><slot name="footer" /></div>
    </aside>
    <section class="work-content">
      <slot v-if="!empty" />
      <div v-else class="work-empty">
        <div class="work-empty-icon"><i :class="emptyIcon" /></div>
        <div class="work-empty-text">{{ emptyText }}</div>
      </div>
    </section>
  </div>
</template>
<script>
export default {
  props: {
    title: { type: String, required: true },
    empty: { type: Boolean, default: false },
    emptyText: { type: String, default: '请扫描单号开始作业' },
    emptyIcon: { type: String, default: 'el-icon-full-screen' }
  }
}
</script>
<style lang="scss">
// 不加 scoped：side 插槽里的扫描框、提示等要用这些类
.app-main > .work-page {
  display: flex; min-height: 100%; padding: 0 !important; background: transparent;
}
.work-side {
  width: 300px; flex-shrink: 0; background: #fff; display: flex; flex-direction: column; box-sizing: border-box;
}
.work-title {
  padding: 16px 16px 8px; font-size: 24px; font-weight: 600; color: #0b1019; line-height: 32px;
  display: flex; align-items: center; justify-content: space-between; gap: 8px;
}
.work-body { flex: 1; padding: 8px 16px 16px; }
.work-footer { padding: 12px 16px 16px; border-top: 1px solid #f0f2f5; }
.work-content {
  flex: 1; min-width: 0; margin: 16px; background: #fff; border-radius: 4px; padding: 16px; box-sizing: border-box; overflow: auto;
}
.work-empty { height: 100%; min-height: 360px; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.work-empty-icon {
  width: 80px; height: 80px; border-radius: 50%; background: #f0f2f5; color: #005bf5; font-size: 32px;
  display: flex; align-items: center; justify-content: center;
}
.work-empty-text { margin-top: 16px; font-size: 24px; font-weight: 600; color: #888c94; }

// 侧栏里的字段：标签在上、粗体；扫描框 48px / 18px，禁用时浅灰
.work-field { margin-bottom: 16px; }
.work-label { font-size: 14px; font-weight: 600; color: #0b1019; line-height: 22px; margin-bottom: 8px; }
.work-field .el-input__inner { height: 48px; line-height: 48px; font-size: 18px; }
.work-field .el-input.is-disabled .el-input__inner { background: #f5f6f9; border-color: #e6e8eb; }
.work-field .el-input-number, .work-field .el-select { width: 100%; }
.work-field .el-input-number .el-input__inner { text-align: left; padding-left: 15px; }
.work-tip {
  background: #fff5e0; color: #0b1019; border-radius: 4px; padding: 10px 12px; font-size: 12px; line-height: 18px; margin-bottom: 16px;
  i { color: #f58718; margin-right: 6px; }
}
.work-actions .el-button { width: 100%; height: 40px; font-size: 14px; margin: 0 0 8px; }
.work-actions .el-button + .el-button { margin-left: 0; }
.work-section-title { font-size: 16px; font-weight: 600; color: #0b1019; margin: 0 0 12px; }
</style>
