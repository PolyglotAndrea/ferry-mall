<template>
  <view class="result-page">
    <view class="result-icon" v-if="success">&#x2713;</view>
    <view class="result-icon fail" v-else>&#x2715;</view>
    <view class="result-title">{{ success ? '支付成功' : '支付失败' }}</view>
    <view class="result-desc" v-if="success">订单已支付，商家将尽快发货</view>
    <view class="result-desc" v-else>支付遇到问题，请重新尝试</view>
    <view class="amount" v-if="amount">¥{{ (amount / 100).toFixed(2) }}</view>
    <view class="btn-group">
      <view class="btn-primary" @tap="goOrders">查看订单</view>
      <view class="btn-default" @tap="goHome">回到首页</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'

const success = ref(true)
const amount = ref(0)

useLoad((query) => {
  success.value = query.success !== 'false'
  amount.value = Number(query.amount || 0)
})

function goOrders() { Taro.redirectTo({ url: '/pages/order/list' }) }
function goHome() { Taro.switchTab({ url: '/pages/index/index' }) }
</script>

<style scoped>
.result-page { display: flex; flex-direction: column; align-items: center; padding-top: 160px; min-height: 100vh; background: #f8fafc; }
.result-icon { width: 120px; height: 120px; border-radius: 50%; background: #22c55e; color: #fff; font-size: 60px; text-align: center; line-height: 120px; }
.result-icon.fail { background: #ef4444; }
.result-title { font-size: 40px; font-weight: 700; margin-top: 32px; }
.result-desc { font-size: 26px; color: #64748b; margin-top: 16px; }
.amount { font-size: 48px; color: #ef4444; font-weight: 800; margin-top: 24px; }
.btn-group { display: flex; gap: 24px; margin-top: 60px; }
.btn-primary { padding: 18px 48px; background: #2563eb; color: #fff; border-radius: 36px; font-size: 28px; }
.btn-default { padding: 18px 48px; background: #fff; color: #475569; border-radius: 36px; font-size: 28px; border: 1px solid #e2e8f0; }
</style>
