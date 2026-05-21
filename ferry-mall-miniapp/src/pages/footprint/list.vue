<template>
  <view class="footprint-page">
    <view v-if="groupedFootprints.length === 0" class="empty">
      <text class="empty-icon">&#x1F463;</text>
      <text class="empty-text">暂无浏览足迹</text>
      <text class="go-btn" @tap="goShop">去逛逛</text>
    </view>
    <view v-else class="fp-list">
      <view v-for="group in groupedFootprints" :key="group.date" class="fp-group">
        <view class="fp-date">{{ group.date }}</view>
        <view v-for="item in group.items" :key="item.id" class="fp-item" @tap="goDetail(item.spuId)">
          <image :src="item.spuCover" class="fp-cover" mode="aspectFill" />
          <view class="fp-info">
            <view class="fp-name">{{ item.spuName }}</view>
            <view class="fp-bottom">
              <text class="fp-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
              <text class="fp-time">{{ formatTime(item.createdAt) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { getFootprints, type FootprintItem } from '@/api/footprint'

interface FootprintGroup {
  date: string
  items: FootprintItem[]
}

const footprints = ref<FootprintItem[]>([])

const groupedFootprints = computed(() => {
  const map = new Map<string, FootprintItem[]>()
  for (const item of footprints.value) {
    const date = item.createdAt?.slice(0, 10) || '未知日期'
    if (!map.has(date)) map.set(date, [])
    map.get(date)!.push(item)
  }
  const result: FootprintGroup[] = []
  for (const [date, items] of map) {
    result.push({ date, items })
  }
  return result.sort((a, b) => b.date.localeCompare(a.date))
})

async function fetch() {
  try {
    footprints.value = await getFootprints()
  } catch {
    footprints.value = []
  }
}
onShow(() => fetch())

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

function goShop() {
  Taro.switchTab({ url: '/pages/index/index' })
}

function formatTime(d: string): string {
  if (!d) return ''
  return d.slice(11, 16)
}
</script>

<style scoped>
.footprint-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.empty { display: flex; flex-direction: column; align-items: center; padding-top: 200px; }
.empty-icon { font-size: 100px; }
.empty-text { font-size: 28px; color: #94a3b8; margin-top: 16px; }
.go-btn { margin-top: 24px; padding: 14px 48px; background: #2563eb; color: #fff; border-radius: 32px; font-size: 28px; }
.fp-group { margin-bottom: 24px; }
.fp-date { font-size: 26px; color: #64748b; font-weight: 600; margin-bottom: 12px; padding-left: 8px; }
.fp-item { display: flex; gap: 20px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.fp-cover { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.fp-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.fp-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.fp-bottom { display: flex; justify-content: space-between; align-items: center; }
.fp-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.fp-time { font-size: 22px; color: #94a3b8; }
</style>
