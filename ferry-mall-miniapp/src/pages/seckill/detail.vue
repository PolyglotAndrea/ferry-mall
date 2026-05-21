<template>
  <view class="detail-page" v-if="products.length > 0">
    <view class="section-title">秒杀商品</view>
    <view v-for="p in products" :key="p.id" class="product-card" @tap="goBuy(p)">
      <image class="cover" :src="p.coverUrl || 'https://dummyimage.com/200x200/e5e7eb/666&text=P'" mode="aspectFill" />
      <view class="info">
        <view class="name">商品 #{{ p.spuId }}</view>
        <view class="price-row">
          <text class="seckill-price">¥{{ (p.seckillPriceCent / 100).toFixed(2) }}</text>
          <text class="stock">库存 {{ p.stock }}</text>
        </view>
        <view class="progress-bar">
          <view class="progress-fill" :style="{ width: soldPercent(p) + '%' }"></view>
        </view>
        <view class="sold-text">已抢 {{ p.sold }} 件</view>
      </view>
    </view>
  </view>
  <view v-else class="empty">暂无秒杀商品</view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { getSeckillProducts, type SeckillProduct } from '@/api/marketing'

const products = ref<SeckillProduct[]>([])

useLoad(async (query) => {
  const id = Number(query.id || 0)
  if (id) {
    try { products.value = await getSeckillProducts(id) } catch { products.value = [] }
  }
})

function soldPercent(p: SeckillProduct): number {
  const total = p.stock + p.sold
  return total > 0 ? Math.round((p.sold / total) * 100) : 0
}

function goBuy(p: SeckillProduct) {
  const params = encodeURIComponent(JSON.stringify([{ spuId: p.spuId, quantity: 1 }]))
  Taro.navigateTo({ url: `/pages/order/confirm?items=${params}` })
}
</script>

<style scoped>
.detail-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.product-card { display: flex; gap: 16px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.cover { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.info { flex: 1; min-width: 0; }
.name { font-size: 28px; font-weight: 700; }
.price-row { display: flex; align-items: baseline; gap: 16px; margin-top: 12px; }
.seckill-price { font-size: 32px; color: #ef4444; font-weight: 700; }
.stock { font-size: 24px; color: #94a3b8; }
.progress-bar { height: 8px; background: #e2e8f0; border-radius: 4px; margin-top: 12px; overflow: hidden; }
.progress-fill { height: 100%; background: #ef4444; border-radius: 4px; }
.sold-text { font-size: 22px; color: #94a3b8; margin-top: 8px; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>
