<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>Ferry Mall Admin</h2>
      <el-form :model="form" @keyup.enter="submit">
        <el-form-item><el-input v-model="form.username" placeholder="账号：admin" /></el-form-item>
        <el-form-item><el-input v-model="form.password" placeholder="密码：admin123" type="password" show-password /></el-form-item>
        <el-button type="primary" class="login-button" @click="submit">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ username: 'admin', password: 'admin123' })
async function submit() {
  await userStore.login(form.username, form.password)
  router.push('/dashboard')
}
</script>
<style scoped>
.login-page { height: 100vh; display: grid; place-items: center; background: linear-gradient(135deg, #dbeafe, #eef2ff); }
.login-card { width: 360px; }
.login-button { width: 100%; }
</style>
