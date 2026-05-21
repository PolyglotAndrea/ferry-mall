<template>
  <view class="coupon-page">
    <view class="section-title">可用优惠券</view>
    <view v-if="coupons.length === 0" class="empty">暂无优惠券</view>
    <view v-for="c in coupons" :key="c.id" class="coupon card">
      <view class="amount">¥{{ (c.discountCent / 100).toFixed(0) }}</view>
      <view class="info">
        <view class="name">{{ c.name }}</view>
        <view class="threshold">满 {{ (c.thresholdCent / 100).toFixed(0) }} 元可用</view>
      </view>
      <view class="action" @tap="receive(c.id)">领取</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getAvailableCoupons, receiveCoupon } from '@/api/coupon'
import type { CouponResp } from '@/api/coupon'

const coupons = ref<CouponResp[]>([])

onMounted(async () => {
  try { coupons.value = await getAvailableCoupons() } catch { coupons.value = [] }
})

async function receive(id: number) {
  try {
    await receiveCoupon(id)
    Taro.showToast({ title: '领取成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '领取失败', icon: 'none' })
  }
}
</script>

<style>
.coupon-page { padding: 20px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.empty { text-align: center; color: #94a3b8; padding: 80px 0; }
.coupon { display: flex; align-items: center; padding: 24px; margin-bottom: 16px; border-radius: 16px; background: linear-gradient(135deg, #fef3c7, #fde68a); }
.amount { font-size: 48px; font-weight: 800; color: #d97706; width: 120px; text-align: center; }
.info { flex: 1; margin-left: 16px; }
.name { font-size: 30px; font-weight: 700; }
.threshold { font-size: 24px; color: #92400e; margin-top: 8px; }
.action { background: #d97706; color: #fff; padding: 12px 28px; border-radius: 32px; font-size: 26px; font-weight: 600; }
</style>
