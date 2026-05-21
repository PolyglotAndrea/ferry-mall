<template>
  <view class="home">
    <!-- 搜索栏 -->
    <view class="search-bar" @tap="goSearch">
      <text class="search-icon">&#x1F50D;</text>
      <text class="search-placeholder">搜索商品</text>
    </view>

    <!-- 轮播图 -->
    <swiper class="banner" :indicator-dots="true" :autoplay="true" :interval="3000" circular>
      <swiper-item v-for="b in banners" :key="b.id" @tap="goBanner(b.linkUrl)">
        <image :src="b.imageUrl" class="banner-img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <!-- 分类入口 -->
    <view class="category-grid">
      <view v-for="c in categories" :key="c.id" class="cat-item" @tap="goCategory(c.id)">
        <image :src="c.icon || 'https://dummyimage.com/120x120/e5e7eb/666&text=C'" class="cat-icon" mode="aspectFill" />
        <text class="cat-name">{{ c.name }}</text>
      </view>
    </view>

    <!-- 推荐商品 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">精选推荐</text>
        <text class="more" @tap="goCategory()">更多 &gt;</text>
      </view>
      <view class="product-grid">
        <view v-for="item in products" :key="item.id" class="product-card" @tap="goDetail(item.id)">
          <image :src="item.coverUrl" class="product-cover" mode="aspectFill" />
          <view class="product-info">
            <view class="product-name">{{ item.name }}</view>
            <view class="product-desc">{{ item.subtitle }}</view>
            <view class="product-bottom">
              <text class="product-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
              <text class="product-market">¥{{ (item.marketPriceCent / 100).toFixed(2) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref } from 'vue'
import { getBannerList, getCategoryList, getProductPage, type BannerDO, type CategoryDO, type ProductSpu } from '@/api/product'

const banners = ref<BannerDO[]>([])
const categories = ref<CategoryDO[]>([])
const products = ref<ProductSpu[]>([])

onMounted(async () => {
  try {
    banners.value = await getBannerList()
  } catch { banners.value = [] }
  try {
    categories.value = await getCategoryList(0)
  } catch { categories.value = [] }
  try {
    products.value = (await getProductPage({ pageSize: 10 })).list
  } catch { products.value = [] }
})

function goSearch() { Taro.navigateTo({ url: '/pages/search/index' }) }
function goCategory(id?: number) {
  if (id) Taro.switchTab({ url: '/pages/category/index' })
  else Taro.switchTab({ url: '/pages/category/index' })
}
function goDetail(id: number) { Taro.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
function goBanner(url: string) {
  if (url) Taro.navigateTo({ url })
}
</script>

<style scoped>
.home { padding-bottom: 40px; }
.search-bar { display: flex; align-items: center; gap: 12px; padding: 16px 24px; margin: 16px 20px; background: #f1f5f9; border-radius: 16px; }
.search-icon { font-size: 28px; }
.search-placeholder { color: #94a3b8; font-size: 28px; }
.banner { height: 320px; margin: 0 20px; border-radius: 16px; overflow: hidden; }
.banner-img { width: 100%; height: 100%; }
.category-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; padding: 24px 20px; }
.cat-item { display: flex; flex-direction: column; align-items: center; }
.cat-icon { width: 96px; height: 96px; border-radius: 50%; background: #f1f5f9; }
.cat-name { font-size: 24px; color: #475569; margin-top: 8px; }
.section { padding: 0 20px; margin-top: 16px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.section-title { font-size: 32px; font-weight: 700; }
.more { font-size: 26px; color: #64748b; }
.product-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.product-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.product-cover { width: 100%; height: 340px; background: #f1f5f9; }
.product-info { padding: 16px; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-desc { font-size: 22px; color: #94a3b8; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.product-bottom { display: flex; align-items: baseline; gap: 12px; margin-top: 12px; }
.product-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.product-market { font-size: 22px; color: #94a3b8; text-decoration: line-through; }
</style>
