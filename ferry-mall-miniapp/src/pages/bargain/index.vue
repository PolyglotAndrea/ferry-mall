<template>
  <view class="bargain-page">
    <view class="section-title">好友砍价</view>
    <view v-for="a in activities" :key="a.id" class="activity-card" @tap="goDetail(a.id)">
      <view class="act-name">{{ a.name }}</view>
      <view class="act-price">
        <text class="original">¥{{ (a.originalPriceCent / 100).toFixed(2) }}</text>
        <text class="floor">底价 ¥{{ (a.floorPriceCent / 100).toFixed(2) }}</text>
      </view>
    </view>
    <view v-if="activities.length === 0" class="empty">暂无砍价活动</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getBargainActivities, type BargainActivity } from '@/api/marketing'

const activities = ref<BargainActivity[]>([])

onMounted(async () => {
  try { activities.value = await getBargainActivities() } catch { activities.value = [] }
})

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/bargain/detail?id=${id}` })
}
</script>

<style scoped>
.bargain-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.activity-card { padding: 24px; background: linear-gradient(135deg, #d1fae5, #a7f3d0); border-radius: 16px; margin-bottom: 16px; }
.act-name { font-size: 30px; font-weight: 700; }
.act-price { margin-top: 12px; }
.original { font-size: 24px; color: #94a3b8; text-decoration: line-through; }
.floor { font-size: 28px; color: #059669; font-weight: 700; margin-left: 16px; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; }
</style>
