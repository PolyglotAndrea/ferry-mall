<template>
  <view class="footprint-page">
    <view v-if="footprints.length === 0" class="empty">暂无浏览足迹</view>
    <view v-else class="fp-list">
      <view v-for="item in footprints" :key="item.id" class="fp-item" @tap="goDetail(item.spuId)">
        <image :src="item.spuCover" class="fp-cover" mode="aspectFill" />
        <view class="fp-info">
          <view class="fp-name">{{ item.spuName }}</view>
          <view class="fp-bottom">
            <text class="fp-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
            <text class="fp-time">{{ item.createdAt?.slice(0, 10) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref } from 'vue'
import { getFootprints, type FootprintItem } from '@/api/member'

const footprints = ref<FootprintItem[]>([])

async function fetch() {
  try { footprints.value = await getFootprints() } catch { footprints.value = [] }
}
onShow(() => fetch())

function goDetail(id: number) { Taro.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
</script>

<style scoped>
.footprint-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; font-size: 28px; }
.fp-item { display: flex; gap: 20px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.fp-cover { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.fp-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.fp-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.fp-bottom { display: flex; justify-content: space-between; align-items: center; }
.fp-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.fp-time { font-size: 22px; color: #94a3b8; }
</style>
