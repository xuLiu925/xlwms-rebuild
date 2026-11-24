<template>
  <div class="login"><h2>WMS</h2>
    <el-form @submit.native.prevent="submit">
      <el-input v-model="form.account" placeholder="账号名" />
      <el-input v-model="form.password" type="password" placeholder="密码" style="margin-top:12px" />
      <el-button type="primary" native-type="submit" style="margin-top:12px;width:100%">登录</el-button>
    </el-form></div>
</template>
<script>
export default {
  data: () => ({ form: { account: '', password: '' } }),
  methods: { submit () { /* TODO: 对接登录接口（登录页还使用腾讯云验证码 TJCaptcha） */ localStorage.setItem('token', 'dev'); this.$router.push(this.$route.query.redirect || '/') } }
}
</script>
<style>.login{width:320px;margin:120px auto}</style>
