<template>
  <view class="result-page">
    <!-- 成功状态 -->
    <template v-if="success">
      <view class="icon-wrap success">
        <text class="icon">&#x2713;</text>
      </view>
      <view class="title">支付成功</view>
      <view class="desc">订单已支付，商家将尽快发货</view>
      <view class="amount" v-if="amount > 0">
        <text class="amount-symbol">¥</text>
        <text class="amount-num">{{ (amount / 100).toFixed(2) }}</text>
      </view>
      <view class="order-no" v-if="orderNo">订单号：{{ orderNo }}</view>
    </template>

    <!-- 失败状态 -->
    <template v-else>
      <view class="icon-wrap fail">
        <text class="icon">&#x2715;</text>
      </view>
      <view class="title">支付失败</view>
      <view class="desc">{{ failReason || '支付遇到问题，请重新尝试' }}</view>
      <view class="amount" v-if="amount > 0">
        <text class="amount-symbol">¥</text>
        <text class="amount-num">{{ (amount / 100).toFixed(2) }}</text>
      </view>
    </template>

    <view class="btn-group">
      <view v-if="success" class="btn-primary" @tap="goOrderDetail">查看订单</view>
      <view v-else class="btn-primary" @tap="goPay">重新支付</view>
      <view class="btn-default" @tap="goHome">返回首页</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'

const success = ref(true)
const amount = ref(0)
const orderNo = ref('')
const failReason = ref('')

useLoad((query) => {
  success.value = query.success !== 'false'
  amount.value = Number(query.amount || 0)
  orderNo.value = (query.orderNo as string) || ''
  failReason.value = (query.reason as string) || ''
})

function goOrderDetail() {
  if (orderNo.value) {
    Taro.redirectTo({ url: `/pages/order/detail?orderNo=${orderNo.value}` })
  } else {
    Taro.redirectTo({ url: '/pages/order/list' })
  }
}

function goPay() {
  if (orderNo.value) {
    Taro.redirectTo({ url: `/pages/payment/pay?orderNo=${orderNo.value}` })
  } else {
    Taro.redirectTo({ url: '/pages/order/list' })
  }
}

function goHome() {
  Taro.switchTab({ url: '/pages/index/index' })
}
</script>

<style scoped>
.result-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120px;
  min-height: 100vh;
  background: #f8fafc;
}

.icon-wrap {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32px;
}

.icon-wrap.success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  box-shadow: 0 8px 24px rgba(34, 197, 94, 0.3);
}

.icon-wrap.fail {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
}

.icon {
  font-size: 64px;
  color: #fff;
  font-weight: 700;
}

.title {
  font-size: 40px;
  font-weight: 700;
  color: #1e293b;
}

.desc {
  font-size: 26px;
  color: #64748b;
  margin-top: 16px;
}

.amount {
  display: flex;
  align-items: baseline;
  margin-top: 32px;
}

.amount-symbol {
  font-size: 32px;
  color: #ef4444;
  font-weight: 700;
}

.amount-num {
  font-size: 56px;
  color: #ef4444;
  font-weight: 800;
}

.order-no {
  font-size: 26px;
  color: #94a3b8;
  margin-top: 16px;
  padding: 8px 24px;
  background: #f1f5f9;
  border-radius: 8px;
}

.btn-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  margin-top: 80px;
  width: 100%;
  padding: 0 60px;
  box-sizing: border-box;
}

.btn-primary {
  width: 100%;
  text-align: center;
  padding: 24px 0;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  border-radius: 40px;
  font-size: 30px;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.3);
}

.btn-default {
  width: 100%;
  text-align: center;
  padding: 24px 0;
  background: #fff;
  color: #475569;
  border-radius: 40px;
  font-size: 30px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
}
</style>
