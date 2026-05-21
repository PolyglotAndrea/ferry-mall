<template>
  <view class="integral-page">
    <view class="header">
      <view class="total-label">当前积分</view>
      <view class="total-points">{{ profile?.points || 0 }}</view>
    </view>
    <view class="list-card">
      <view class="list-title">积分明细</view>
      <view v-for="item in records" :key="item.id" class="record-item">
        <view class="record-left">
          <view class="record-reason">{{ item.reason || '积分变动' }}</view>
          <view class="record-time">{{ formatDate(item.createdAt) }}</view>
        </view>
        <view class="record-right">
          <text class="change" :class="{ plus: item.changeCount > 0 }">
            {{ item.changeCount > 0 ? '+' : '' }}{{ item.changeCount }}
          </text>
          <view class="current">余额 {{ item.currentPoints }}</view>
        </view>
      </view>
      <view v-if="records.length === 0" class="empty">
        <text class="empty-icon">&#x1F4B8;</text>
        <text class="empty-text">暂无积分记录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getIntegralRecords, type IntegralRecord } from '@/api/integral'
import { useUserStore } from '@/stores/user'

const records = ref<IntegralRecord[]>([])
const user = useUserStore()
const profile = user.profile

onMounted(async () => {
  try {
    const res = await getIntegralRecords()
    records.value = res.list
  } catch {
    records.value = []
  }
})

function formatDate(d: string): string {
  if (!d) return ''
  return d.slice(0, 16).replace('T', ' ')
}
</script>

<style scoped>
.integral-page { min-height: 100vh; background: #f8fafc; }
.header { padding: 60px 32px; background: linear-gradient(135deg, #2563eb, #1d4ed8); text-align: center; }
.total-label { font-size: 26px; color: rgba(255,255,255,0.8); }
.total-points { font-size: 72px; font-weight: 800; color: #fff; margin-top: 12px; }
.list-card { padding: 20px; }
.list-title { font-size: 30px; font-weight: 700; margin-bottom: 16px; }
.record-item { display: flex; justify-content: space-between; align-items: center; padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.record-reason { font-size: 28px; font-weight: 600; }
.record-time { font-size: 24px; color: #94a3b8; margin-top: 8px; }
.record-right { text-align: right; }
.change { font-size: 32px; font-weight: 700; color: #ef4444; }
.change.plus { color: #22c55e; }
.current { font-size: 22px; color: #94a3b8; margin-top: 6px; }
.empty { display: flex; flex-direction: column; align-items: center; padding: 120px 0; }
.empty-icon { font-size: 80px; }
.empty-text { font-size: 28px; color: #94a3b8; margin-top: 16px; }
</style>
