<template>
  <view class="detail-page" v-if="activity">
    <view class="info-card">
      <view class="name">{{ activity.name }}</view>
      <view class="price">拼团价 ¥{{ (activity.grouponPriceCent / 100).toFixed(2) }}</view>
      <view class="count">{{ activity.requireCount }}人成团</view>
    </view>
    <view class="btn-group">
      <view class="btn-primary" @tap="onJoin">立即参团</view>
    </view>
  </view>
  <view v-else class="empty">加载中...</view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { getGrouponDetail, joinGroupon, type GrouponActivity } from '@/api/marketing'

const activity = ref<GrouponActivity>()

useLoad(async (query) => {
  const id = Number(query.id || 0)
  if (id) {
    try { activity.value = await getGrouponDetail(id) } catch {}
  }
})

async function onJoin() {
  if (!activity.value) return
  try {
    await joinGroupon(activity.value.id)
    Taro.showToast({ title: '参团成功', icon: 'success' })
    const params = encodeURIComponent(JSON.stringify([{ spuId: activity.value.spuId, quantity: 1 }]))
    Taro.navigateTo({ url: `/pages/order/confirm?items=${params}` })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '参团失败', icon: 'none' })
  }
}
</script>

<style scoped>
.detail-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.info-card { padding: 40px 24px; background: linear-gradient(135deg, #2563eb, #1d4ed8); border-radius: 16px; text-align: center; color: #fff; }
.name { font-size: 36px; font-weight: 700; }
.price { font-size: 48px; font-weight: 800; margin-top: 16px; }
.count { font-size: 26px; margin-top: 12px; opacity: 0.9; }
.btn-group { margin-top: 40px; }
.btn-primary { text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>
