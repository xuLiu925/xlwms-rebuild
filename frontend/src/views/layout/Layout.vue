<template>
  <div class="app-wrapper" :class="{ collapsed }">
    <!-- 左侧菜单 -->
    <aside class="sidebar">
      <div class="sidebar-logo" @click="$router.push('/dashboard')">
        <span class="logo-mark">W</span>
        <span v-show="!collapsed" class="logo-title">WMS 重建版</span>
      </div>
      <el-scrollbar class="sidebar-menu" wrap-class="sidebar-menu-wrap">
        <el-menu :default-active="basePath" :collapse="collapsed" :collapse-transition="false" router unique-opened>
          <el-menu-item index="/dashboard">
            <i class="el-icon-s-home menu-icon" /><span slot="title">首页</span>
          </el-menu-item>
          <template v-for="m in menu">
            <el-menu-item v-if="m.link" :key="m.key" :index="m.path">
              <i :class="m.icon" class="menu-icon" /><span slot="title">{{ m.title }}<span v-if="m.stub" class="stub">待实现</span></span>
            </el-menu-item>
            <el-submenu v-else :key="m.key" :index="m.path" popper-class="sidebar-popper">
              <template slot="title">
                <i :class="m.icon" class="menu-icon" /><span class="submenu-title">{{ m.title }}</span>
              </template>
              <el-menu-item v-for="c in m.children" :key="c.path" :index="c.path">
                {{ c.title }}<span v-if="c.stub" class="stub">待实现</span>
              </el-menu-item>
            </el-submenu>
          </template>
          <el-menu-item index="/pda">
            <i class="el-icon-mobile-phone menu-icon" /><span slot="title">PDA 模拟</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
      <div class="hamburger" :title="collapsed ? '展开菜单' : '收起菜单'" @click="collapsed = !collapsed">
        <i :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'" />
      </div>
    </aside>

    <section class="main-container">
      <!-- 顶部导航：已打开页面的页签 + 用户 -->
      <header class="nav-header">
        <div class="tags-view">
          <div v-for="t in tags" :key="t.path" class="tags-view-item" :class="{ active: t.path === basePath }"
            :title="t.title" @click="go(t.path)">
            <span class="route-title">{{ t.title }}</span>
            <i v-if="t.path !== '/dashboard'" class="el-icon-close" @click.stop="close(t.path)" />
          </div>
        </div>
        <div class="user-operation">
          <span class="local-badge">本地演示</span>
          <el-dropdown trigger="click" @command="onCommand">
            <span class="avatar-wrapper">
              <i class="el-icon-user-solid user-avatar" />
              <span>{{ account || '未登录' }}</span>
              <i class="el-icon-arrow-down" />
            </span>
            <el-dropdown-menu slot="dropdown" class="user-header-dropdown">
              <el-dropdown-item command="closeOthers">关闭其他页签</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </header>
      <main class="app-main"><router-view :key="basePath" /></main>
    </section>
  </div>
</template>
<script>
import request from '../../api/request'

const TAGS_KEY = 'wms-tags'

export default {
  props: { menu: { type: Array, required: true } },
  data () {
    let saved = []
    try { saved = JSON.parse(sessionStorage.getItem(TAGS_KEY)) || [] } catch (e) { saved = [] }
    return {
      account: localStorage.getItem('account') || '',
      collapsed: localStorage.getItem('wms-collapsed') === '1',
      tags: saved.length ? saved : [{ path: '/dashboard', title: '首页' }]
    }
  },
  computed: {
    // PDA 模拟的子页面（/pda/xxx）共用一个页签和菜单项，切换子页面时外壳不重建
    basePath () { const p = this.$route.path; return p.startsWith('/pda/') ? '/pda' : p }
  },
  watch: {
    $route: { immediate: true, handler () { this.addTag(this.basePath) } },
    tags (v) { sessionStorage.setItem(TAGS_KEY, JSON.stringify(v)) },
    collapsed (v) { localStorage.setItem('wms-collapsed', v ? '1' : '0') }
  },
  methods: {
    titleOf (path) {
      if (path === '/dashboard') return '首页'
      if (path === '/pdaDemo') return 'PDA 出库模拟'
      if (path === '/pda') return 'PDA 模拟'
      for (const m of this.menu) {
        if (m.link && m.path === path) return m.title
        const c = (m.children || []).find(x => x.path === path)
        if (c) return c.title
      }
      return path
    },
    addTag (path) {
      if (!this.tags.some(t => t.path === path)) this.tags = this.tags.concat({ path, title: this.titleOf(path) })
    },
    go (path) { if (path !== this.basePath) this.$router.push(path) },
    close (path) {
      const i = this.tags.findIndex(t => t.path === path)
      this.tags = this.tags.filter(t => t.path !== path)
      if (path === this.basePath) this.go((this.tags[i] || this.tags[i - 1] || { path: '/dashboard' }).path)
    },
    onCommand (cmd) {
      if (cmd === 'logout') this.logout()
      if (cmd === 'closeOthers') this.tags = this.tags.filter(t => t.path === '/dashboard' || t.path === this.basePath)
    },
    async logout () {
      try { await request.post('/auth/logout') } catch (e) { /* 后端未连通时只清本地 */ }
      localStorage.removeItem('token'); localStorage.removeItem('account'); sessionStorage.removeItem(TAGS_KEY)
      this.$router.push('/login')
    }
  }
}
</script>
<style lang="scss" scoped>
$side-bg: #060d1e;
$logo-bg: #07183e;
$nav-bg: #07183d;
$active: #3154f3;

.app-wrapper { display: flex; height: 100vh; overflow: hidden; }

.sidebar {
  width: 170px; flex-shrink: 0; display: flex; flex-direction: column; background: $side-bg; transition: width .28s;
  .collapsed & { width: 56px; }
}
.sidebar-logo {
  height: 48px; flex-shrink: 0; display: flex; align-items: center; gap: 8px; padding: 0 12px 0 12px; background: $logo-bg; cursor: pointer;
  .collapsed & { justify-content: center; padding: 0; }
}
.logo-mark {
  width: 28px; height: 28px; border-radius: 4px; background: #005bf5; color: #fff; font-weight: 700; font-size: 16px;
  display: inline-flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.logo-title { color: #fff; font-weight: 600; font-size: 16px; white-space: nowrap; }

.sidebar-menu { flex: 1; padding-top: 8px; ::v-deep .sidebar-menu-wrap { overflow-x: hidden; } }
.hamburger {
  height: 40px; flex-shrink: 0; display: flex; align-items: center; padding-left: 18px; color: #a6abb4; font-size: 18px; cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, .06);
  &:hover { color: #fff; }
}

// 菜单：圆角高亮块（左右各缩 8px），子菜单标题 40px 半透明，菜单项 36px
::v-deep .el-menu {
  background-color: $side-bg; border-right: none;
  .menu-icon { margin-right: 8px; font-size: 16px; color: #fff; width: 18px; text-align: center; }
  .el-submenu { margin-bottom: 4px; }
  .el-submenu__title, > .el-menu-item {
    height: 40px; line-height: 40px; color: #fff !important; opacity: .8; position: relative; z-index: 1; padding: 0 12px !important;
    &:hover { opacity: 1; background: none !important; }
    &:hover::before { content: ''; position: absolute; top: 0; bottom: 0; left: 8px; right: 8px; border-radius: 4px; background: rgba(255, 255, 255, .1); z-index: -1; }
  }
  .el-submenu__title .el-submenu__icon-arrow { right: 16px; color: rgba(255, 255, 255, .6); font-weight: 600; }
  .el-submenu.is-active > .el-submenu__title { opacity: 1; font-weight: 600; }
  .el-submenu .el-menu { background: transparent; }
  .el-submenu .el-menu-item {
    min-width: 0; height: 36px; line-height: 36px; margin-top: 2px; padding-left: 38px !important; padding-right: 12px !important;
    color: #fff; opacity: .8; position: relative; z-index: 1; font-size: 13px; background: none !important;
    &:hover { opacity: 1; }
    &:hover::after { content: ''; position: absolute; top: 0; bottom: 0; left: 8px; right: 8px; border-radius: 4px; background: rgba(255, 255, 255, .1); z-index: -1; }
    &.is-active { opacity: 1; font-weight: 600; }
    &.is-active::after { content: ''; position: absolute; top: 0; bottom: 0; left: 8px; right: 8px; border-radius: 4px; background: $active; z-index: -1; }
  }
  > .el-menu-item.is-active { opacity: 1; font-weight: 600; }
  > .el-menu-item.is-active::before { content: ''; position: absolute; top: 0; bottom: 0; left: 8px; right: 8px; border-radius: 4px; background: $active !important; z-index: -1; }
  &.el-menu--collapse { width: 56px; .el-submenu__title, > .el-menu-item { padding: 0 19px !important; } }
}
.stub { margin-left: 6px; font-size: 11px; opacity: .5; }

.main-container { flex: 1; min-width: 0; display: flex; flex-direction: column; }

// 顶部导航 48px，页签贴底，选中页签白底黑字
.nav-header {
  height: 48px; flex-shrink: 0; display: flex; align-items: center; background: $nav-bg; color: rgba(255, 255, 255, .8); padding-right: 16px;
}
.tags-view { flex: 1; min-width: 0; align-self: flex-end; display: flex; height: 40px; overflow: hidden; }
.tags-view-item {
  position: relative; display: inline-flex; align-items: center; flex-shrink: 1; min-width: 72px; width: 160px; padding: 0 8px 0 12px;
  font-size: 14px; color: rgba(255, 255, 255, .8); cursor: pointer; box-sizing: border-box; border-radius: 4px 4px 0 0;
  &::after { content: ''; position: absolute; right: -1px; top: 13px; width: 1px; height: 14px; background: rgba(255, 255, 255, .3); }
  &:hover { background: #202f50; }
  .route-title { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .el-icon-close { margin-left: 4px; padding: 1px; border-radius: 50%; visibility: hidden; &:hover { background: rgba(255, 255, 255, .3); } }
  &:hover .el-icon-close { visibility: visible; }
  &.active {
    background: #fff; color: #0b1019; font-weight: 600; z-index: 2;
    &::after { display: none; }
    .el-icon-close { visibility: visible; color: #55585f; &:hover { background: rgba(11, 16, 25, .06); } }
  }
}
.user-operation { display: flex; align-items: center; gap: 12px; height: 100%; }
.local-badge { font-size: 12px; padding: 2px 8px; border-radius: 4px; background: rgba(245, 135, 24, .2); color: #f8a54f; }
.avatar-wrapper { display: inline-flex; align-items: center; gap: 6px; cursor: pointer; color: rgba(255, 255, 255, .8); font-size: 14px; }
.user-avatar { font-size: 16px; padding: 4px; border-radius: 50%; background: rgba(255, 255, 255, .3); }

.app-main { flex: 1; overflow: auto; background: #f0f2f5; }
</style>
