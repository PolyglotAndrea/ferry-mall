<template>
  <view class="detail-page" v-if="activity">
    <view class="info-card">
      <view class="name">{{ activity.name }}</view>
      <view class="price-row">
        <text class="original">原价 ¥{{ (activity.originalPriceCent / 100).toFixed(2) }}</text>
        <text class="floor">底价 ¥{{ (activity.floorPriceCent / 100).toFixed(2) }}</text>
      </view>
    </view>
    <view v-if="record" class="record-card">
      <view class="record-title">当前已砍到</view>
      <view class="record-price">¥{{ (record.currentPriceCent / 100).toFixed(2) }}</view>
      <view class="btn-primary" @tap="onHelp">帮砍一刀</view>
    </view>
    <view v-else class="btn-group">
      <view class="btn-primary" @tap="onStart">发起砍价</view>
    </view>
  </view>
  <view v-else class="empty">加载中...</view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { getBargainActivities, startBargain, helpBargain, getBargainRecord, type BargainActivity, type BargainRecord } from '@/api/marketing'

const activity = ref<BargainActivity>()
const record = ref<BargainRecord>()
const recordId = ref(0)

useLoad(async (query) => {
  const id = Number(query.id || 0)
  if (id) {
    try {
      const acts = await getBargainActivities()
      activity.value = acts.find(a => a.id === id)
    } catch {}
  }
})

async function onStart() {
  if (!activity.value) return
  try {
    const r = await startBargain(activity.value.id)
    record.value = r
    recordId.value = r.id
    Taro.showToast({ title: '砍价发起成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '发起失败', icon: 'none' })
  }
}

async function onHelp() {
  if (!recordId.value) return
  try {
    const r = await helpBargain(recordId.value)
    record.value = r
    Taro.showToast({ title: '砍价成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '砍价失败', icon: 'none' })
  }
}
</script>

<style scoped>
.detail-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.info-card { padding: 40px 24px; background: linear-gradient(135deg, #10b981, #059669); border-radius: 16px; text-align: center; color: #fff; }
.name { font-size: 36px; font-weight: 700; }
.price-row { margin-top: 20px; }
.original { font-size: 26px; opacity: 0.8; text-decoration: line-through; }
.floor { font-size: 40px; font-weight: 800; margin-left: 20px; }
.record-card { padding: 40px; background: #fff; border-radius: 16px; margin-top: 24px; text-align: center; }
.record-title { font-size: 28px; color: #64748b; }
.record-price { font-size: 56px; font-weight: 800; color: #10b981; margin: 20px 0; }
.btn-group { margin-top: 40px; }
.btn-primary { text-align: center; padding: 24px 0; background: #10b981; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>
