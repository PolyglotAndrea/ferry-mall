<template>
  <view class="bind-page">
    <view class="form-card">
      <view class="form-row">
        <text class="label">手机号</text>
        <input v-model="mobile" type="number" placeholder="请输入手机号" maxlength="11" />
      </view>
      <view class="form-row">
        <text class="label">验证码</text>
        <input v-model="code" type="number" placeholder="请输入验证码" maxlength="6" />
        <text class="code-btn" :class="{ disabled: counting }" @tap="sendCode">{{ codeText }}</text>
      </view>
    </view>
    <view class="submit-btn" @tap="onSubmit">绑定手机号</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref, computed } from 'vue'
import { sendBindCode, bindPhone } from '@/api/member'

const mobile = ref('')
const code = ref('')
const counting = ref(false)
const count = ref(60)

const codeText = computed(() => counting.value ? `${count.value}s` : '获取验证码')

async function sendCode() {
  if (counting.value) return
  if (!mobile.value || mobile.value.length !== 11) {
    Taro.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  try {
    await sendBindCode(mobile.value)
    Taro.showToast({ title: '验证码已发送', icon: 'success' })
    counting.value = true
    count.value = 60
    const timer = setInterval(() => {
      count.value--
      if (count.value <= 0) {
        clearInterval(timer)
        counting.value = false
      }
    }, 1000)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '发送失败', icon: 'none' })
  }
}

async function onSubmit() {
  if (!mobile.value || !code.value) {
    Taro.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  try {
    await bindPhone(mobile.value, code.value)
    Taro.showToast({ title: '绑定成功', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '绑定失败', icon: 'none' })
  }
}
</script>

<style scoped>
.bind-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.form-card { padding: 0 24px; background: #fff; border-radius: 16px; }
.form-row { display: flex; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; }
.form-row:last-child { border-bottom: 0; }
.label { font-size: 28px; font-weight: 600; width: 140px; flex-shrink: 0; }
input { flex: 1; font-size: 28px; }
.code-btn { font-size: 24px; color: #2563eb; padding: 8px 16px; }
.code-btn.disabled { color: #94a3b8; }
.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #2563eb; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
