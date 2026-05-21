<template>
  <view class="coupon-page">
    <view class="tabs">
      <view v-for="t in tabs" :key="t.status"
        class="tab" :class="{ active: activeStatus === t.status }" @tap="activeStatus = t.status"
      >
        {{ t.label }}
      </view>
    </view>
    <view class="coupon-list">
      <view v-for="c in filteredCoupons" :key="c.id" class="coupon-card"
        :class="{ disabled: c.status !== 1 }"
      >
        <view class="coupon-left">
          <view class="amount">
            <text class="currency">¥</text>
            <text class="num">{{ (c.discountCent / 100).toFixed(0) }}</text>
          </view>
          <view class="threshold">满{{ (c.thresholdCent / 100).toFixed(0) }}元可用</view>
        </view>
        <view class="coupon-divider"></view>
        <view class="coupon-right">
          <view class="name">{{ c.couponName }}</view>
          <view class="expire">有效期至 {{ formatDate(c.expireTime) }}</view>
        </view>
        <view class="status-badge" :class="{ used: c.status === 2, expired: c.status !== 1 && c.status !== 2 }">
          {{ c.status === 1 ? '未使用' : c.status === 2 ? '已使用' : '已过期' }}
        </view>
      </view>
      <view v-if="filteredCoupons.length === 0" class="empty">
        <text class="empty-icon">&#x1F9F7;</text>
        <text class="empty-text">暂无优惠券</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getMyCoupons, type MemberCoupon } from '@/api/coupon'

const tabs = [
  { label: '未使用', status: 1 },
  { label: '已使用', status: 2 },
  { label: '已过期', status: 0 },
]
const activeStatus = ref(1)
const coupons = ref<MemberCoupon[]>([])

const filteredCoupons = computed(() => coupons.value.filter(c => c.status === activeStatus.value))

onMounted(async () => {
  try {
    coupons.value = await getMyCoupons()
  } catch {
    coupons.value = []
  }
})

function formatDate(d: string): string {
  if (!d) return ''
  return d.slice(0, 10)
}
</script>

<style scoped>
.coupon-page { min-height: 100vh; background: #f8fafc; }
.tabs { display: flex; background: #fff; border-bottom: 1px solid #f1f5f9; }
.tab { flex: 1; text-align: center; padding: 24px 0; font-size: 28px; color: #64748b; }
.tab.active { color: #2563eb; font-weight: 700; border-bottom: 4px solid #2563eb; }
.coupon-list { padding: 20px; }
.coupon-card { display: flex; align-items: center; padding: 0; background: #fff; border-radius: 16px; margin-bottom: 16px; overflow: hidden; position: relative; }
.coupon-card.disabled { opacity: 0.6; }
.coupon-left { width: 200px; padding: 24px 0; text-align: center; background: linear-gradient(135deg, #fef3c7, #fde68a); flex-shrink: 0; }
.amount { display: flex; align-items: baseline; justify-content: center; }
.currency { font-size: 28px; color: #d97706; font-weight: 700; }
.num { font-size: 52px; font-weight: 800; color: #d97706; }
.threshold { font-size: 22px; color: #92400e; margin-top: 6px; }
.coupon-divider { width: 1px; height: 100px; background: #f1f5f9; }
.coupon-right { flex: 1; padding: 24px; min-width: 0; }
.name { font-size: 30px; font-weight: 700; }
.expire { font-size: 22px; color: #94a3b8; margin-top: 8px; }
.status-badge { width: 120px; padding: 8px 0; margin-right: 20px; background: #fef3c7; color: #d97706; border-radius: 12px; font-size: 22px; font-weight: 600; text-align: center; flex-shrink: 0; }
.status-badge.used { background: #dcfce7; color: #16a34a; }
.status-badge.expired { background: #f1f5f9; color: #94a3b8; }
.empty { display: flex; flex-direction: column; align-items: center; padding: 120px 0; }
.empty-icon { font-size: 80px; }
.empty-text { text-align: center; color: #94a3b8; padding: 120px 0; font-size: 28px; }
</style>
