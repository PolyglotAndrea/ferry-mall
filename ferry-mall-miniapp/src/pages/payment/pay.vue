<template>
  <view class="pay-page" v-if="order">
    <view class="pay-card">
      <view class="pay-amount">¥{{ (order.payAmountCent / 100).toFixed(2) }}</view>
      <view class="pay-desc">订单号：{{ order.orderNo }}</view>
    </view>
    <view class="channel-card">
      <view class="channel-title">选择支付方式</view>
      <view class="channel" :class="{ active: channel === 'wx' }" @tap="channel = 'wx'">
        <text class="channel-icon">&#x1F4B3;</text>
        <view class="channel-info">
          <view>微信支付</view>
          <view class="channel-desc">推荐使用微信支付</view>
        </view>
        <text class="channel-check">{{ channel === 'wx' ? '&#x2713;' : '' }}</text>
      </view>
      <view class="channel" :class="{ active: channel === 'mock' }" @tap="channel = 'mock'">
        <text class="channel-icon">&#x1F9F7;</text>
        <view class="channel-info">
          <view>模拟支付</view>
          <view class="channel-desc">测试环境专用</view>
        </view>
        <text class="channel-check">{{ channel === 'mock' ? '&#x2713;' : '' }}</text>
      </view>
    </view>
    <view class="pay-btn" @tap="onPay">确认支付</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { getOrderDetail, type OrderResp } from '@/api/order'
import { preparePayment, type PaymentPrepareResp } from '@/api/payment'

const order = ref<OrderResp>()
const channel = ref('wx')

useLoad(async (query) => {
  if (query.orderNo) {
    order.value = await getOrderDetail(query.orderNo as string)
  }
})

async function onPay() {
  if (!order.value) return
  try {
    const prep = await preparePayment(order.value.orderNo, channel.value)
    const orderNo = order.value.orderNo
    const amount = order.value.payAmountCent
    if (channel.value === 'mock') {
      Taro.redirectTo({ url: `/pages/payment/result?success=true&amount=${amount}&orderNo=${orderNo}` })
    } else {
      // 真实微信支付
      Taro.requestPayment({
        timeStamp: String(Date.now()),
        nonceStr: prep.paymentNo,
        package: prep.mockPayload,
        signType: 'RSA',
        paySign: prep.paymentNo,
        success: () => {
          Taro.redirectTo({ url: `/pages/payment/result?success=true&amount=${amount}&orderNo=${orderNo}` })
        },
        fail: () => {
          Taro.redirectTo({ url: `/pages/payment/result?success=false&amount=${amount}&orderNo=${orderNo}` })
        }
      })
    }
  } catch (e: any) {
    Taro.showToast({ title: e.message || '支付失败', icon: 'none' })
  }
}
</script>

<style scoped>
.pay-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.pay-card { padding: 60px 24px; background: linear-gradient(135deg, #2563eb, #1d4ed8); border-radius: 16px; text-align: center; margin-bottom: 24px; }
.pay-amount { font-size: 64px; font-weight: 800; color: #fff; }
.pay-desc { font-size: 26px; color: rgba(255,255,255,0.8); margin-top: 16px; }
.channel-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 24px; }
.channel-title { font-size: 30px; font-weight: 700; margin-bottom: 20px; }
.channel { display: flex; align-items: center; gap: 16px; padding: 24px; border-radius: 12px; border: 2px solid #f1f5f9; margin-bottom: 16px; }
.channel.active { border-color: #2563eb; background: #eff6ff; }
.channel-icon { font-size: 48px; }
.channel-info { flex: 1; }
.channel-info view:first-child { font-size: 28px; font-weight: 700; }
.channel-desc { font-size: 24px; color: #94a3b8; margin-top: 4px; }
.channel-check { width: 40px; height: 40px; border-radius: 50%; background: #2563eb; color: #fff; text-align: center; line-height: 40px; font-size: 24px; }
.pay-btn { text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
