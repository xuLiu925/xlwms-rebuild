<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo" @click="$router.push('/dashboard')">WMS 重建版</div>
      <el-menu :default-active="$route.path" router unique-opened background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/dashboard">首页</el-menu-item>
        <el-submenu v-for="m in menu" :key="m.path" :index="m.path">
          <template slot="title">{{ m.title }}</template>
          <el-menu-item v-for="c in m.children" :key="c.path" :index="c.path">
            {{ c.title }}<span v-if="c.stub" class="stub">待实现</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/pdaDemo">PDA 出库模拟</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header" height="48px">
        <span class="crumb">{{ crumb }}</span>
        <span class="user">{{ account }}<el-button type="text" @click="logout">退出</el-button></span>
      </el-header>
      <el-main class="main"><router-view :key="$route.path" /></el-main>
    </el-container>
  </el-container>
</template>
<script>
import request from '../../api/request'
export default {
  props: { menu: { type: Array, required: true } },
  data: () => ({ account: localStorage.getItem('account') || '' }),
  computed: {
    crumb () {
      for (const m of this.menu) {
        const c = m.children.find(x => x.path === this.$route.path)
        if (c) return m.title + ' / ' + c.title
      }
      return ''
    }
  },
  methods: {
    async logout () {
      try { await request.post('/auth/logout') } catch (e) { /* 后端未连通时只清本地 */ }
      localStorage.removeItem('token'); localStorage.removeItem('account')
      this.$router.push('/login')
    }
  }
}
</script>
<style scoped>
.layout { height: 100vh }
.aside { background: #304156; overflow-y: auto }
.aside .el-menu { border-right: none }
.logo { color: #fff; font-weight: 600; height: 48px; line-height: 48px; padding-left: 20px; cursor: pointer }
.stub { margin-left: 6px; font-size: 11px; color: #909399 }
.header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #e6e6e6 }
.crumb { color: #606266; font-size: 14px }
.user { color: #606266; font-size: 13px }
.user .el-button { margin-left: 12px }
.main { padding: 0; background: #fff }
</style>
