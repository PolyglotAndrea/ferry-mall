<template>
  <view class="points-page">
    <view class="header">
      <view class="label">我的积分</view>
      <view class="amount">{{ profile?.points || 0 }}</view>
    </view>
    <view class="product-list">
      <view v-for="p in products" :key="p.id" class="product-card">
        <image :src="p.coverUrl || 'https://dummyimage.com/200x200/e5e7eb/666&text=P'" class="cover" mode="aspectFill" />
        <view class="info">
          <view class="name">{{ p.name }}</view>
          <view class="bottom">
            <text class="points">{{ p.points }} 积分</text>
            <text class="stock">库存 {{ p.stock }}</text>
          </view>
          <view class="exchange-btn" @tap="onExchange(p)">立即兑换</view>
        </view>
      </view>
      <view v-if="products.length === 0" class="empty">暂无积分商品</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref } from 'vue'
import { getPointsProducts, exchangePointsProduct, type PointsProduct } from '@/api/points'
import { useUserStore } from '@/stores/user'

const products = ref<PointsProduct[]>([])
const user = useUserStore()
const profile = user.profile

async function fetch() {
  try { products.value = await getPointsProducts() } catch { products.value = [] }
}
onShow(() => fetch())

async function onExchange(p: PointsProduct) {
  try {
    await exchangePointsProduct(p.id)
    Taro.showToast({ title: '兑换成功', icon: 'success' })
    if (profile) profile.points -= p.points
    fetch()
  } catch (e: any) {
    Taro.showToast({ title: e.message || '兑换失败', icon: 'none' })
  }
}
</script>

<style scoped>
.points-page { min-height: 100vh; background: #f8fafc; }
.header { padding: 60px 32px; background: linear-gradient(135deg, #f59e0b, #d97706); text-align: center; color: #fff; }
.label { font-size: 26px; opacity: 0.85; }
.amount { font-size: 72px; font-weight: 800; margin-top: 12px; }
.product-list { padding: 20px; }
.product-card { display: flex; gap: 16px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.cover { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.name { font-size: 28px; font-weight: 700; }
.bottom { display: flex; gap: 16px; margin-top: 8px; }
.points { font-size: 28px; color: #f59e0b; font-weight: 700; }
.stock { font-size: 24px; color: #94a3b8; }
.exchange-btn { display: inline-block; padding: 10px 32px; background: #f59e0b; color: #fff; border-radius: 24px; font-size: 24px; text-align: center; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; }
</style>
