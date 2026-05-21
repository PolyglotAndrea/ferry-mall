<template>
  <view class="search-page">
    <view class="search-bar">
      <input v-model="keyword" class="search-input" placeholder="搜索商品" confirm-type="search" @confirm="onSearch" />
      <text class="search-btn" @tap="onSearch">搜索</text>
    </view>
    <view v-if="results.length > 0" class="results">
      <view v-for="item in results" :key="item.id" class="product" @tap="goDetail(item.id)">
        <image :src="item.coverUrl" class="cover" mode="aspectFill" />
        <view class="info">
          <view class="name">{{ item.name }}</view>
          <view class="desc">{{ item.subtitle }}</view>
          <view class="price">¥{{ (item.priceCent / 100).toFixed(2) }}</view>
        </view>
      </view>
    </view>
    <view v-else-if="searched" class="empty">未找到相关商品</view>
    <view v-else class="hot">
      <view class="section-title">热门搜索</view>
      <view class="tags">
        <text v-for="tag in hotTags" :key="tag" class="tag" @tap="keyword = tag; onSearch()">{{ tag }}</text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref } from 'vue'
import { getProductPage, type ProductSpu } from '@/api/product'
const keyword = ref('')
const results = ref<ProductSpu[]>([])
const searched = ref(false)
const hotTags = ['手表', '咖啡', '背包', '耳机', '键盘']

async function onSearch() {
  if (!keyword.value.trim()) return
  searched.value = true
  const all = (await getProductPage()).list
  results.value = all.filter(p => p.name.includes(keyword.value) || p.subtitle.includes(keyword.value))
}
function goDetail(id: number) { Taro.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
</script>
<style>
.search-page { padding: 20px; }
.search-bar { display: flex; align-items: center; gap: 16px; }
.search-input { flex: 1; height: 72px; background: #f1f5f9; border-radius: 36px; padding: 0 28px; font-size: 28px; }
.search-btn { color: #2563eb; font-size: 28px; font-weight: 600; }
.results { margin-top: 24px; }
.product { display: flex; padding: 18px 0; border-bottom: 1px solid #eef2f7; }
.cover { width: 160px; height: 160px; border-radius: 16px; background: #f1f5f9; }
.info { flex: 1; margin-left: 20px; }
.name { font-size: 30px; font-weight: 700; }
.desc { font-size: 24px; color: #64748b; margin: 12px 0; }
.price { font-size: 32px; color: #ef4444; font-weight: 700; }
.empty { text-align: center; color: #94a3b8; margin-top: 120px; font-size: 28px; }
.hot { margin-top: 32px; }
.section-title { font-size: 32px; font-weight: 700; margin-bottom: 20px; }
.tags { display: flex; flex-wrap: wrap; gap: 16px; }
.tag { background: #f1f5f9; color: #475569; padding: 12px 24px; border-radius: 32px; font-size: 26px; }
</style>
