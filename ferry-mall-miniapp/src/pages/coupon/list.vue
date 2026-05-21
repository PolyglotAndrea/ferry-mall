<template>
  <view class="coupon-page">
    <view class="page-header">
      <view class="header-title">领券中心</view>
      <view class="header-sub">限时优惠券等你来领</view>
    </view>
    <view class="coupon-list">
      <view v-if="coupons.length === 0" class="empty">
        <text class="empty-icon">&#x1F381;</text>
        <text class="empty-text">暂无可用优惠券</text>
      </view>
      <view v-for="c in coupons" :key="c.id" class="coupon-card">
        <view class="coupon-left">
          <view class="amount">
            <text class="currency">¥</text>
            <text class="num">{{ (c.discountCent / 100).toFixed(0) }}</text>
          </view>
          <view class="threshold">满{{ (c.thresholdCent / 100).toFixed(0) }}元可用</view>
        </view>
        <view class="coupon-divider"></view>
        <view class="coupon-right">
          <view class="name">{{ c.name }}</view>
          <view class="desc">全场通用优惠券</view>
        </view>
        <view class="coupon-action" :class="{ received: receivedMap[c.id] }" @tap="receive(c.id)">
          {{ receivedMap[c.id] ? '已领取' : '立即领取' }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref, reactive } from 'vue'
import { getAvailableCoupons, receiveCoupon, type CouponResp } from '@/api/coupon'

const coupons = ref<CouponResp[]>([])
const receivedMap = reactive<Record<number, boolean>>({})

async function fetch() {
  try {
    coupons.value = await getAvailableCoupons()
  } catch {
    coupons.value = []
  }
}
onShow(() => fetch())

async function receive(id: number) {
  if (receivedMap[id]) return
  try {
    await receiveCoupon(id)
    receivedMap[id] = true
    Taro.showToast({ title: '领取成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '领取失败', icon: 'none' })
  }
}
</script>

<style scoped>
.coupon-page { min-height: 100vh; background: #f8fafc; }
.page-header { padding: 40px 32px; background: linear-gradient(135deg, #f59e0b, #d97706); }
.header-title { font-size: 40px; font-weight: 800; color: #fff; }
.header-sub { font-size: 26px; color: rgba(255,255,255,0.85); margin-top: 8px; }
.coupon-list { padding: 20px; }
.empty { display: flex; flex-direction: column; align-items: center; padding: 120px 0; }
.empty-icon { font-size: 80px; }
.empty-text { font-size: 28px; color: #94a3b8; margin-top: 16px; }
.coupon-card { display: flex; align-items: center; padding: 0; background: #fff; border-radius: 16px; margin-bottom: 16px; overflow: hidden; position: relative; }
.coupon-left { width: 200px; padding: 24px 0; text-align: center; background: linear-gradient(135deg, #fef3c7, #fde68a); flex-shrink: 0; }
.amount { display: flex; align-items: baseline; justify-content: center; }
.currency { font-size: 28px; color: #d97706; font-weight: 700; }
.num { font-size: 52px; font-weight: 800; color: #d97706; }
.threshold { font-size: 22px; color: #92400e; margin-top: 6px; }
.coupon-divider { width: 1px; height: 100px; background: #f1f5f9; }
.coupon-right { flex: 1; padding: 24px; min-width: 0; }
.name { font-size: 30px; font-weight: 700; }
.desc { font-size: 24px; color: #94a3b8; margin-top: 8px; }
.coupon-action { width: 140px; padding: 12px 0; margin-right: 20px; background: #d97706; color: #fff; border-radius: 32px; font-size: 26px; font-weight: 600; text-align: center; flex-shrink: 0; }
.coupon-action.received { background: #cbd5e1; }
</style>
