<template>
  <view class="bargain-page">
    <!-- 顶部 Banner -->
    <view class="header-banner">
      <view class="header-title">好友砍价</view>
      <view class="header-subtitle">邀请好友帮砍 0元拿好物</view>
    </view>

    <!-- 砍价商品列表 -->
    <view class="product-list">
      <view v-for="a in activities" :key="a.id" class="product-card">
        <image
          class="product-cover"
          :src="a.coverUrl || 'https://dummyimage.com/200x200/e5e7eb/666&text=砍价'"
          mode="aspectFill"
          @tap="goDetail(a.id)"
        />
        <view class="product-info">
          <view class="product-name" @tap="goDetail(a.id)">{{ a.name }}</view>
          <view class="product-price-row">
            <text class="floor-price">底价 ¥{{ (a.floorPriceCent / 100).toFixed(2) }}</text>
            <text class="original-price">¥{{ (a.originalPriceCent / 100).toFixed(2) }}</text>
          </view>

          <!-- 进度条 -->
          <view class="bargain-progress">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: bargainPercent(a) + '%' }"></view>
            </view>
            <view class="progress-labels">
              <text class="label-left">已砍 ¥{{ getCutAmount(a).toFixed(2) }}</text>
              <text class="label-right">还差 ¥{{ getRemainAmount(a).toFixed(2) }}</text>
            </view>
          </view>

          <view class="product-actions">
            <view class="btn-bargain" @tap="onStartBargain(a)">发起砍价</view>
            <view class="btn-help" @tap="goDetail(a.id)">帮砍一刀</view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!loading && activities.length === 0" class="empty">
      <text class="empty-icon">&#x1FA99;</text>
      <text class="empty-text">暂无砍价活动</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getBargainActivities, startBargain, type BargainActivity } from '@/api/marketing'

const activities = ref<BargainActivity[]>([])
const loading = ref(false)

function bargainPercent(a: BargainActivity): number {
  if (a.originalPriceCent <= a.floorPriceCent) return 100
  const cut = a.originalPriceCent - a.floorPriceCent
  const total = a.originalPriceCent - a.floorPriceCent
  // 模拟已砍金额（实际应从记录中获取）
  const mockCut = Math.floor(cut * 0.3)
  return Math.min(Math.round((mockCut / total) * 100), 100)
}

function getCutAmount(a: BargainActivity): number {
  const total = a.originalPriceCent - a.floorPriceCent
  return Math.floor(total * 0.3) / 100
}

function getRemainAmount(a: BargainActivity): number {
  const total = a.originalPriceCent - a.floorPriceCent
  const cut = Math.floor(total * 0.3)
  return (total - cut) / 100
}

async function loadActivities() {
  loading.value = true
  try {
    activities.value = await getBargainActivities()
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载砍价活动失败', icon: 'none' })
    activities.value = []
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/bargain/detail?id=${id}` })
}

async function onStartBargain(a: BargainActivity) {
  try {
    const record = await startBargain(a.id)
    Taro.showToast({ title: '砍价发起成功', icon: 'success' })
    // 跳转到砍价详情页
    setTimeout(() => {
      Taro.navigateTo({ url: `/pages/bargain/detail?id=${a.id}` })
    }, 500)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '发起砍价失败', icon: 'none' })
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.bargain-page { min-height: 100vh; background: #f8fafc; padding-bottom: 40px; }

/* Header */
.header-banner {
  background: linear-gradient(135deg, #10b981, #059669);
  padding: 40px 24px 60px;
  text-align: center;
  color: #fff;
}
.header-title { font-size: 44px; font-weight: 800; }
.header-subtitle { font-size: 26px; opacity: 0.9; margin-top: 8px; }

/* Product List */
.product-list { padding: 16px 20px; margin-top: -30px; }
.product-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.product-cover { width: 200px; height: 200px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.product-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-price-row { display: flex; align-items: baseline; gap: 12px; margin-top: 8px; }
.floor-price { font-size: 30px; color: #ef4444; font-weight: 800; }
.original-price { font-size: 22px; color: #94a3b8; text-decoration: line-through; }

/* Progress */
.bargain-progress { margin-top: 12px; }
.progress-bar { height: 10px; background: #e2e8f0; border-radius: 5px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #10b981, #34d399); border-radius: 5px; }
.progress-labels { display: flex; justify-content: space-between; margin-top: 6px; }
.label-left { font-size: 20px; color: #10b981; font-weight: 600; }
.label-right { font-size: 20px; color: #94a3b8; }

/* Actions */
.product-actions { display: flex; gap: 12px; margin-top: 12px; }
.btn-bargain {
  flex: 1;
  text-align: center;
  padding: 14px 0;
  background: #ef4444;
  color: #fff;
  border-radius: 28px;
  font-size: 26px;
  font-weight: 600;
}
.btn-help {
  flex: 1;
  text-align: center;
  padding: 14px 0;
  background: #10b981;
  color: #fff;
  border-radius: 28px;
  font-size: 26px;
  font-weight: 600;
}

/* Empty */
.empty { text-align: center; padding: 120px 0; }
.empty-icon { font-size: 80px; }
.empty-text { display: block; font-size: 28px; color: #94a3b8; margin-top: 16px; }
</style>
