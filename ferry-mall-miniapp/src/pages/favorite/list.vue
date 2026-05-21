<template>
  <view class="favorite-page">
    <view v-if="favorites.length === 0" class="empty">
      <text class="empty-icon">&#x1F90D;</text>
      <text class="empty-text">暂无收藏商品</text>
      <text class="go-btn" @tap="goShop">去逛逛</text>
    </view>
    <view v-else class="fav-list">
      <view
        v-for="item in favorites"
        :key="item.id"
        class="fav-item"
        @touchstart="touchStart"
        @touchend="touchEnd($event, item.spuId)"
      >
        <view class="fav-main" :style="{ transform: `translateX(${slideMap[item.spuId] || 0}rpx)` }">
          <image :src="item.spuCover" class="fav-cover" mode="aspectFill" @tap="goDetail(item.spuId)" />
          <view class="fav-info" @tap="goDetail(item.spuId)">
            <view class="fav-name">{{ item.spuName }}</view>
            <view class="fav-bottom">
              <text class="fav-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
              <text class="fav-time">{{ item.createdAt?.slice(0, 10) }}</text>
            </view>
          </view>
        </view>
        <view class="fav-delete" @tap="onRemove(item.spuId)">
          <text>取消</text>
          <text>收藏</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref, reactive } from 'vue'
import { getFavorites, removeFavorite, type FavoriteItem } from '@/api/favorite'

const favorites = ref<FavoriteItem[]>([])
const slideMap = reactive<Record<number, number>>({})
let touchStartX = 0

async function fetch() {
  try {
    favorites.value = await getFavorites()
  } catch {
    favorites.value = []
  }
}
onShow(() => {
  fetch()
  Object.keys(slideMap).forEach(k => { slideMap[Number(k)] = 0 })
})

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

function goShop() {
  Taro.switchTab({ url: '/pages/index/index' })
}

function touchStart(e: TouchEvent) {
  touchStartX = e.changedTouches[0].clientX
}

function touchEnd(e: TouchEvent, spuId: number) {
  const diff = touchStartX - e.changedTouches[0].clientX
  if (diff > 50) {
    Object.keys(slideMap).forEach(k => { slideMap[Number(k)] = 0 })
    slideMap[spuId] = -160
  } else if (diff < -50) {
    slideMap[spuId] = 0
  }
}

async function onRemove(spuId: number) {
  try {
    await removeFavorite(spuId)
    slideMap[spuId] = 0
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
.empty-icon { font-size: 100px; }
.empty-text { font-size: 28px; color: #94a3b8; margin-top: 16px; }
.go-btn { margin-top: 24px; padding: 14px 48px; background: #2563eb; color: #fff; border-radius: 32px; font-size: 28px; }
.fav-item { position: relative; overflow: hidden; border-radius: 16px; margin-bottom: 16px; }
.fav-main { display: flex; gap: 20px; padding: 20px; background: #fff; border-radius: 16px; transition: transform 0.2s ease; position: relative; z-index: 1; }
.fav-cover { width: 180px; height: 180px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.fav-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.fav-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.fav-bottom { display: flex; justify-content: space-between; align-items: center; }
.fav-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.fav-time { font-size: 22px; color: #94a3b8; }
.fav-delete { position: absolute; right: 0; top: 0; bottom: 0; width: 160rpx; background: #ef4444; color: #fff; display: flex; flex-direction: column; align-items: center; justify-content: center; font-size: 26px; font-weight: 600; z-index: 0; }
</style>
