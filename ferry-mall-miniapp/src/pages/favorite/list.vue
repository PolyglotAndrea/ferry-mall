<template>
  <view class="favorite-page">
    <view v-if="favorites.length === 0" class="empty">
      <text>暂无收藏商品</text>
      <text class="go-btn" @tap="goShop">去逛逛</text>
    </view>
    <view v-else class="fav-list">
      <view v-for="item in favorites" :key="item.id" class="fav-item">
        <image :src="item.spuCover" class="fav-cover" mode="aspectFill" @tap="goDetail(item.spuId)" />
        <view class="fav-info">
          <view class="fav-name" @tap="goDetail(item.spuId)">{{ item.spuName }}</view>
          <view class="fav-bottom">
            <text class="fav-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
            <text class="fav-del" @tap="onRemove(item.spuId)">取消收藏</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref } from 'vue'
import { getFavorites, removeFavorite, type FavoriteItem } from '@/api/member'

const favorites = ref<FavoriteItem[]>([])

async function fetch() {
  try { favorites.value = await getFavorites() } catch { favorites.value = [] }
}
onShow(() => fetch())

function goDetail(id: number) { Taro.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
function goShop() { Taro.switchTab({ url: '/pages/index/index' }) }

async function onRemove(spuId: number) {
  try {
    await removeFavorite(spuId)
    Taro.showToast({ title: '已取消收藏', icon: 'success' })
    fetch()
  } catch (e: any) {
    Taro.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}
</script>

<style scoped>
.favorite-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.empty { display: flex; flex-direction: column; align-items: center; padding-top: 200px; }
.empty text { font-size: 28px; color: #94a3b8; }
.go-btn { margin-top: 24px; padding: 14px 48px; background: #2563eb; color: #fff; border-radius: 32px; }
.fav-item { display: flex; gap: 20px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.fav-cover { width: 180px; height: 180px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.fav-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.fav-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.fav-bottom { display: flex; justify-content: space-between; align-items: center; }
.fav-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.fav-del { font-size: 24px; color: #94a3b8; }
</style>
