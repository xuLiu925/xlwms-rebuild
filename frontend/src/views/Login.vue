<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-head">
        <span class="brand"><span class="logo-mark">W</span>WMS 重建版</span>
        <span class="lang">中文</span>
      </div>
      <h1>登录</h1>
      <el-form label-position="top" @submit.native.prevent="submit">
        <el-form-item label="账号名">
          <el-input v-model="form.account" placeholder="任意账号（本地演示）" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="任意密码（不校验、不保存）" />
        </el-form-item>
        <el-button class="submit" type="primary" native-type="submit" :loading="loading">登录</el-button>
      </el-form>
      <p class="tip">本地演示登录：只连本地后端，账号密码非空即可，不会校验或保存。真实系统的验证码未实现。</p>
      <p v-if="error" class="err">{{ error }}
        <el-button type="text" @click="offline">离线进入（使用前端示例数据）</el-button>
      </p>
    </div>
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
<style lang="scss" scoped>
.login-page { min-height: 100vh; background: #04183f; display: flex; align-items: center; justify-content: center; }
.login-card { width: 308px; background: #fff; border-radius: 8px; padding: 32px 30px 28px; box-sizing: border-box; }
.login-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.brand { display: inline-flex; align-items: center; gap: 8px; font-weight: 600; font-size: 15px; color: #0b1019; }
.logo-mark {
  width: 26px; height: 26px; border-radius: 4px; background: #005bf5; color: #fff; font-weight: 700; font-size: 15px;
  display: inline-flex; align-items: center; justify-content: center;
}
.lang { font-size: 12px; color: #55585f; }
h1 { margin: 0 0 20px; font-size: 24px; font-weight: 700; color: #0b1019; }
::v-deep .el-form-item { margin-bottom: 18px; }
::v-deep .el-form-item__label { padding: 0 0 6px; line-height: 16px; font-size: 12px; color: #55585f; }
::v-deep .el-input__inner { height: 40px; line-height: 40px; font-size: 14px; }
.submit { width: 100%; height: 40px; margin-top: 6px; font-size: 16px; background: #0f37ff; border-color: #0f37ff; }
.tip { margin: 16px 0 0; color: #888c94; font-size: 12px; line-height: 1.6; }
.err { color: #df3148; font-size: 13px; }
</style>
