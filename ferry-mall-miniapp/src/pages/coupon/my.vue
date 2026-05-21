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
        <view class="amount">¥{{ (c.discountCent / 100).toFixed(0) }}</view>
        <view class="info">
          <view class="name">{{ c.couponName }}</view>
          <view class="threshold">满 {{ (c.thresholdCent / 100).toFixed(0) }} 元可用</view>
          <view class="expire">有效期至 {{ formatDate(c.expireTime) }}</view>
        </view>
        <view class="status-badge" :class="{ used: c.status === 2, expired: c.status !== 1 && c.status !== 2 }">
          {{ c.status === 1 ? '未使用' : c.status === 2 ? '已使用' : '已过期' }}
        </view>
      </view>
      <view v-if="filteredCoupons.length === 0" class="empty">暂无优惠券</view>
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
  try { coupons.value = await getMyCoupons() } catch { coupons.value = [] }
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
.coupon-card { display: flex; align-items: center; padding: 24px; background: linear-gradient(135deg, #fef3c7, #fde68a); border-radius: 16px; margin-bottom: 16px; }
.coupon-card.disabled { background: #f1f5f9; opacity: 0.7; }
.amount { font-size: 48px; font-weight: 800; color: #d97706; width: 120px; text-align: center; }
.info { flex: 1; margin-left: 16px; }
.name { font-size: 30px; font-weight: 700; }
.threshold { font-size: 24px; color: #92400e; margin-top: 8px; }
.expire { font-size: 22px; color: #a16207; margin-top: 6px; }
.status-badge { font-size: 22px; background: #fff; color: #d97706; padding: 6px 16px; border-radius: 12px; }
.status-badge.used { color: #22c55e; }
.status-badge.expired { color: #94a3b8; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; font-size: 28px; }
</style>
