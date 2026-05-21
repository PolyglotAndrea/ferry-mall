<template>
  <view class="live-page">
    <view class="section-title">直播推荐</view>
    <view class="live-grid">
      <view v-for="room in rooms" :key="room.id" class="live-card">
        <image :src="room.coverUrl || 'https://dummyimage.com/400x300/e5e7eb/666&text=Live'" class="live-cover" mode="aspectFill" />
        <view class="live-badge">直播中</view>
        <view class="live-info">
          <view class="live-name">{{ room.name }}</view>
          <view class="live-anchor">主播：{{ room.anchorName }}</view>
        </view>
      </view>
    </view>
    <view v-if="rooms.length === 0" class="empty">暂无直播</view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getLiveRooms, type LiveRoom } from '@/api/store'

const rooms = ref<LiveRoom[]>([])

onMounted(async () => {
  try { rooms.value = await getLiveRooms() } catch { rooms.value = [] }
})
</script>

<style scoped>
.live-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.live-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.live-card { position: relative; border-radius: 16px; overflow: hidden; background: #fff; }
.live-cover { width: 100%; height: 240px; }
.live-badge { position: absolute; top: 12px; left: 12px; padding: 4px 16px; background: #ef4444; color: #fff; border-radius: 12px; font-size: 22px; }
.live-info { padding: 16px; }
.live-name { font-size: 26px; font-weight: 700; }
.live-anchor { font-size: 22px; color: #94a3b8; margin-top: 6px; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>
