<template>
  <view class="groupon-page">
    <!-- 顶部 Banner -->
    <view class="header-banner">
      <view class="header-title">拼团活动</view>
      <view class="header-subtitle">好友一起拼 价格更优惠</view>
    </view>

    <!-- 拼团商品列表 -->
    <view class="product-list">
      <view v-for="a in activities" :key="a.id" class="product-card" @tap="goDetail(a.id)">
        <image
          class="product-cover"
          :src="a.coverUrl || 'https://dummyimage.com/200x200/e5e7eb/666&text=拼团'"
          mode="aspectFill"
        />
        <view class="product-info">
          <view class="product-name">{{ a.name }}</view>
          <view class="product-price-row">
            <text class="groupon-price">¥{{ (a.grouponPriceCent / 100).toFixed(2) }}</text>
            <text class="original-price">¥{{ ((a.originalPriceCent ?? a.grouponPriceCent * 2) / 100).toFixed(2) }}</text>
          </view>
          <view class="product-meta">
            <view class="group-info">
              <text class="group-count">{{ a.requireCount }}人成团</text>
              <text class="group-save">省¥{{ ((a.originalPriceCent ?? a.grouponPriceCent * 2) - a.grouponPriceCent) / 100 }}</text>
            </view>
            <view class="btn-group-action" @tap.stop="onJoin(a)">
              {{ hasJoined(a.id) ? '已参团' : '去开团' }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!loading && activities.length === 0" class="empty">
      <text class="empty-icon">&#x1F465;</text>
      <text class="empty-text">暂无拼团活动</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getGrouponActivities, type GrouponActivity } from '@/api/marketing'

const activities = ref<GrouponActivity[]>([])
const loading = ref(false)
const joinedIds = ref<Set<number>>(new Set())

function hasJoined(id: number): boolean {
  return joinedIds.value.has(id)
}

async function loadActivities() {
  loading.value = true
  try {
    activities.value = await getGrouponActivities()
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载拼团活动失败', icon: 'none' })
    activities.value = []
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/groupon/detail?id=${id}` })
}

function onJoin(a: GrouponActivity) {
  if (hasJoined(a.id)) {
    Taro.showToast({ title: '您已参与该拼团', icon: 'none' })
    return
  }
  // 跳转到拼团详情页进行参团
  Taro.navigateTo({ url: `/pages/groupon/detail?id=${a.id}` })
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.groupon-page { min-height: 100vh; background: #f8fafc; padding-bottom: 40px; }

/* Header */
.header-banner {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
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
.product-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-price-row { display: flex; align-items: baseline; gap: 12px; margin-top: 8px; }
.groupon-price { font-size: 36px; color: #ef4444; font-weight: 800; }
.original-price { font-size: 24px; color: #94a3b8; text-decoration: line-through; }
.product-meta { display: flex; align-items: center; justify-content: space-between; margin-top: 12px; }
.group-info { display: flex; align-items: center; gap: 12px; }
.group-count {
  padding: 4px 12px;
  background: #dbeafe;
  color: #2563eb;
  border-radius: 8px;
  font-size: 22px;
  font-weight: 600;
}
.group-save { font-size: 22px; color: #ef4444; font-weight: 600; }
.btn-group-action {
  padding: 12px 28px;
  background: #ef4444;
  color: #fff;
  border-radius: 28px;
  font-size: 26px;
  font-weight: 600;
  flex-shrink: 0;
}

/* Empty */
.empty { text-align: center; padding: 120px 0; }
.empty-icon { font-size: 80px; }
.empty-text { display: block; font-size: 28px; color: #94a3b8; margin-top: 16px; }
</style>
