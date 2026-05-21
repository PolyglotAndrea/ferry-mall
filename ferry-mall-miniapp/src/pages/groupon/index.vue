<template>
  <view class="groupon-page">
    <view class="section-title">拼团活动</view>
    <view v-for="a in activities" :key="a.id" class="activity-card" @tap="goDetail(a.id)">
      <view class="act-name">{{ a.name }}</view>
      <view class="act-price">拼团价 ¥{{ (a.grouponPriceCent / 100).toFixed(2) }}</view>
      <view class="act-count">{{ a.requireCount }}人成团</view>
    </view>
    <view v-if="activities.length === 0" class="empty">暂无拼团活动</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getGrouponActivities, type GrouponActivity } from '@/api/marketing'

const activities = ref<GrouponActivity[]>([])

onMounted(async () => {
  try { activities.value = await getGrouponActivities() } catch { activities.value = [] }
})

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/groupon/detail?id=${id}` })
}
</script>

<style scoped>
.groupon-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.activity-card { padding: 24px; background: linear-gradient(135deg, #dbeafe, #bfdbfe); border-radius: 16px; margin-bottom: 16px; }
.act-name { font-size: 30px; font-weight: 700; }
.act-price { font-size: 28px; color: #2563eb; font-weight: 700; margin-top: 8px; }
.act-count { font-size: 24px; color: #1e40af; margin-top: 8px; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; }
</style>
