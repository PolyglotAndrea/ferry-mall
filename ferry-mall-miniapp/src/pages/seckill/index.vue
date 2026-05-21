<template>
  <view class="seckill-page">
    <view class="section-title">限时秒杀</view>
    <view v-for="a in activities" :key="a.id" class="activity-card" @tap="goDetail(a.id)">
      <view class="act-name">{{ a.name }}</view>
      <view class="act-time">{{ formatTime(a.startTime) }} - {{ formatTime(a.endTime) }}</view>
      <view class="act-status" :class="{ active: isActive(a) }">{{ isActive(a) ? '进行中' : '即将开始' }}</view>
    </view>
    <view v-if="activities.length === 0" class="empty">暂无秒杀活动</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getSeckillActivities, type SeckillActivity } from '@/api/marketing'

const activities = ref<SeckillActivity[]>([])

onMounted(async () => {
  try { activities.value = await getSeckillActivities() } catch { activities.value = [] }
})

function isActive(a: SeckillActivity): boolean {
  const now = new Date().getTime()
  const start = new Date(a.startTime).getTime()
  const end = new Date(a.endTime).getTime()
  return now >= start && now <= end
}

function formatTime(t: string): string {
  return t?.slice(0, 16).replace('T', ' ') || ''
}

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/seckill/detail?id=${id}` })
}
</script>

<style scoped>
.seckill-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.activity-card { padding: 24px; background: linear-gradient(135deg, #fef3c7, #fde68a); border-radius: 16px; margin-bottom: 16px; }
.act-name { font-size: 30px; font-weight: 700; }
.act-time { font-size: 24px; color: #92400e; margin-top: 8px; }
.act-status { display: inline-block; margin-top: 12px; padding: 4px 16px; background: #d97706; color: #fff; border-radius: 12px; font-size: 22px; }
.act-status.active { background: #ef4444; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; }
</style>
