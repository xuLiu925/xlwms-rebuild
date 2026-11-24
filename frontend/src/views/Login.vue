<template>
  <div class="login">
    <h2>WMS（本地重建版）</h2>
    <el-form @submit.native.prevent="submit">
      <el-input v-model="form.account" placeholder="账号（任意，本地演示）" />
      <el-input v-model="form.password" type="password" placeholder="密码（任意，不校验不保存）" style="margin-top:12px" />
      <el-button type="primary" native-type="submit" :loading="loading" style="margin-top:12px;width:100%">登录</el-button>
    </el-form>
    <p class="tip">本地演示登录：只连本地后端，账号密码非空即可，不会校验或保存。真实系统的验证码未实现。</p>
    <p v-if="error" class="err">{{ error }}
      <el-button type="text" @click="offline">离线进入（使用前端示例数据）</el-button>
    </p>
  </div>
</template>
<script>
import request from '../api/request'
export default {
  data: () => ({ form: { account: '', password: '' }, loading: false, error: '' }),
  methods: {
    async submit () {
      this.loading = true; this.error = ''
      try {
        const r = await request.post('/auth/login', this.form)
        localStorage.setItem('token', r.token)
        localStorage.setItem('account', r.account)
        this.go()
      } catch (e) {
        this.error = '登录失败：' + e.message
      } finally {
        this.form.password = ''
        this.loading = false
      }
    },
    offline () { localStorage.setItem('token', 'offline-demo'); localStorage.setItem('account', '离线演示'); this.go() },
    go () { this.$router.push(this.$route.query.redirect || '/') }
  }
}
</script>
<style>
.login { width: 340px; margin: 120px auto }
.login .tip { color: #909399; font-size: 12px; line-height: 1.6 }
.login .err { color: #f56c6c; font-size: 13px }
</style>
