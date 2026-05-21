<template>
  <view class="home">
    <!-- 顶部搜索栏 -->
    <view class="header-bar">
      <view class="search-bar" @tap="goSearch">
        <text class="search-icon">&#x1F50D;</text>
        <text class="search-placeholder">搜索商品</text>
      </view>
      <view class="message-box" @tap="goMessage">
        <text class="message-icon">&#x1F4E8;</text>
        <text v-if="hasUnread" class="message-dot" />
      </view>
    </view>

    <!-- 轮播图 -->
    <swiper class="banner" :indicator-dots="true" :autoplay="true" :interval="3000" circular>
      <swiper-item v-for="b in banners" :key="b.id" @tap="goBanner(b.linkUrl)">
        <image :src="b.imageUrl" class="banner-img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <!-- 营销活动快捷入口 -->
    <view class="activity-bar">
      <view class="activity-item" @tap="goActivity('seckill')">
        <view class="activity-icon activity-seckill">
          <text>&#x23F0;</text>
        </view>
        <text class="activity-name">限时秒杀</text>
      </view>
      <view class="activity-item" @tap="goActivity('groupon')">
        <view class="activity-icon activity-groupon">
          <text>&#x1F465;</text>
        </view>
        <text class="activity-name">拼团购</text>
      </view>
      <view class="activity-item" @tap="goActivity('bargain')">
        <view class="activity-icon activity-bargain">
          <text>&#x1FA99;</text>
        </view>
        <text class="activity-name">砍价购</text>
      </view>
      <view class="activity-item" @tap="goActivity('coupon')">
        <view class="activity-icon activity-coupon">
          <text>&#x1F3AB;</text>
        </view>
        <text class="activity-name">领券中心</text>
      </view>
    </view>

    <!-- 分类入口 -->
    <view class="category-section">
      <scroll-view scroll-x class="category-scroll" v-if="categories.length > 10">
        <view class="category-row">
          <view v-for="c in categories" :key="c.id" class="cat-item" @tap="goCategory(c.id)">
            <image :src="c.icon || 'https://dummyimage.com/120x120/e5e7eb/666&text=C'" class="cat-icon" mode="aspectFill" />
            <text class="cat-name">{{ c.name }}</text>
          </view>
        </view>
      </scroll-view>
      <view v-else class="category-grid">
        <view v-for="c in categories" :key="c.id" class="cat-item" @tap="goCategory(c.id)">
          <image :src="c.icon || 'https://dummyimage.com/120x120/e5e7eb/666&text=C'" class="cat-icon" mode="aspectFill" />
          <text class="cat-name">{{ c.name }}</text>
        </view>
      </view>
    </view>

    <!-- 限时秒杀 -->
    <view class="section seckill-section" v-if="seckillProducts.length > 0">
      <view class="section-header">
        <view class="section-title-wrap">
          <text class="section-title">限时秒杀</text>
          <view class="countdown">
            <text class="countdown-text">距结束</text>
            <text class="countdown-num">{{ countdown.h }}</text>
            <text class="countdown-colon">:</text>
            <text class="countdown-num">{{ countdown.m }}</text>
            <text class="countdown-colon">:</text>
            <text class="countdown-num">{{ countdown.s }}</text>
          </view>
        </view>
        <text class="more" @tap="goActivity('seckill')">更多 &gt;</text>
      </view>
      <scroll-view scroll-x class="seckill-scroll">
        <view class="seckill-list">
          <view v-for="item in seckillProducts" :key="item.id" class="seckill-card" @tap="goSeckillDetail(item.id)">
            <image :src="item.coverUrl" class="seckill-cover" mode="aspectFill" />
            <view class="seckill-info">
              <view class="seckill-name">{{ item.name }}</view>
              <view class="seckill-price-row">
                <text class="seckill-price">¥{{ (item.seckillPriceCent / 100).toFixed(2) }}</text>
                <text class="seckill-market">¥{{ (item.marketPriceCent ?? 0 / 100).toFixed(2) }}</text>
              </view>
              <view class="seckill-progress">
                <view class="progress-bar">
                  <view class="progress-fill" :style="{ width: `${Math.min(((item.sold ?? 0) / ((item.stock ?? 1) + (item.sold ?? 0))) * 100, 100)}%` }" />
                </view>
                <text class="progress-text">已抢{{ Math.round(((item.sold ?? 0) / ((item.stock ?? 1) + (item.sold ?? 0))) * 100) }}%</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 优惠券 -->
    <view class="section coupon-section" v-if="coupons.length > 0">
      <view class="section-header">
        <text class="section-title">领券中心</text>
        <text class="more" @tap="goActivity('coupon')">更多 &gt;</text>
      </view>
      <scroll-view scroll-x class="coupon-scroll">
        <view class="coupon-list">
          <view v-for="c in coupons" :key="c.id" class="coupon-card" @tap="receiveCoupon(c.id)">
            <view class="coupon-left">
              <text class="coupon-value">¥{{ (c.discountCent / 100).toFixed(0) }}</text>
              <text class="coupon-threshold">满{{ (c.thresholdCent / 100).toFixed(0) }}可用</text>
            </view>
            <view class="coupon-right">
              <text class="coupon-name">{{ c.name }}</text>
              <text class="coupon-btn">立即领取</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 新品上架 -->
    <view class="section" v-if="newProducts.length > 0">
      <view class="section-header">
        <text class="section-title">新品上架</text>
        <text class="more" @tap="goCategory()">更多 &gt;</text>
      </view>
      <scroll-view scroll-x class="new-scroll">
        <view class="new-list">
          <view v-for="item in newProducts" :key="item.id" class="new-card" @tap="goDetail(item.id)">
            <image :src="item.coverUrl" class="new-cover" mode="aspectFill" />
            <view class="new-name">{{ item.name }}</view>
            <view class="new-price">¥{{ (item.priceCent / 100).toFixed(2) }}</view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 精选推荐 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">精选推荐</text>
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
      <view class="load-more">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
        <text v-else>上拉加载更多</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref, computed, onUnmounted } from 'vue'
import { getBannerList, getCategoryList, getProductPage, type BannerDO, type CategoryDO, type ProductSpu } from '@/api/product'
import { getSeckillActivities, getSeckillProducts, type SeckillProduct } from '@/api/marketing'
import { getAvailableCoupons, receiveCoupon as apiReceiveCoupon, type CouponResp } from '@/api/coupon'

/* ==================== 数据状态 ==================== */
const banners = ref<BannerDO[]>([])
const categories = ref<CategoryDO[]>([])
const products = ref<ProductSpu[]>([])
const newProducts = ref<ProductSpu[]>([])
const seckillProducts = ref<SeckillProduct[]>([])
const coupons = ref<CouponResp[]>([])
const hasUnread = ref(false)

const loading = ref(false)
const noMore = ref(false)
const pageNo = ref(1)
const pageSize = 10

/* ==================== 倒计时 ==================== */
const countdown = ref({ h: '00', m: '00', s: '00' })
let countdownTimer: ReturnType<typeof setInterval> | null = null

function startCountdown(endTimeStr: string) {
  if (countdownTimer) clearInterval(countdownTimer)
  const update = () => {
    const diff = new Date(endTimeStr).getTime() - Date.now()
    if (diff <= 0) {
      countdown.value = { h: '00', m: '00', s: '00' }
      if (countdownTimer) clearInterval(countdownTimer)
      return
    }
    const h = Math.floor(diff / 3600000)
    const m = Math.floor((diff % 3600000) / 60000)
    const s = Math.floor((diff % 60000) / 1000)
    countdown.value = {
      h: String(h).padStart(2, '0'),
      m: String(m).padStart(2, '0'),
      s: String(s).padStart(2, '0'),
    }
  }
  update()
  countdownTimer = setInterval(update, 1000)
}

/* ==================== 数据加载 ==================== */
async function loadAll() {
  // banner
  try {
    banners.value = await getBannerList()
  } catch { banners.value = [] }

  // category
  try {
    categories.value = await getCategoryList(0)
  } catch { categories.value = [] }

  // 新品（取第1页，按时间倒序）
  try {
    const res = await getProductPage({ pageNo: 1, pageSize: 10, sort: 'createTime_desc' })
    newProducts.value = res.list
  } catch { newProducts.value = [] }

  // 秒杀活动 & 商品
  try {
    const activities = await getSeckillActivities()
    if (activities.length > 0) {
      const active = activities.find(a => a.status === 1) || activities[0]
      startCountdown(active.endTime)
      const list = await getSeckillProducts(active.id)
      seckillProducts.value = list.slice(0, 6)
    } else {
      seckillProducts.value = []
    }
  } catch { seckillProducts.value = [] }

  // 优惠券
  try {
    coupons.value = await getAvailableCoupons()
  } catch { coupons.value = [] }

  // 推荐商品（第1页）
  pageNo.value = 1
  noMore.value = false
  await loadProducts()
}

async function loadProducts() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await getProductPage({ pageNo: pageNo.value, pageSize })
    if (pageNo.value === 1) {
      products.value = res.list
    } else {
      products.value.push(...res.list)
    }
    if (res.list.length < pageSize || pageNo.value >= res.pages) {
      noMore.value = true
    }
    pageNo.value++
  } catch (e) {
    Taro.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/* ==================== 生命周期 ==================== */
onMounted(() => {
  loadAll()
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})

/* ==================== 页面事件 ==================== */
function onPullDownRefresh() {
  loadAll().finally(() => {
    Taro.stopPullDownRefresh()
  })
}

function onReachBottom() {
  loadProducts()
}

/* ==================== 跳转 ==================== */
function goSearch() { Taro.navigateTo({ url: '/pages/search/index' }) }
function goMessage() { Taro.navigateTo({ url: '/pages/message/index' }) }
function goCategory(id?: number) {
  if (id) {
    Taro.navigateTo({ url: `/pages/category/index?id=${id}` })
  } else {
    Taro.switchTab({ url: '/pages/category/index' })
  }
}
function goDetail(id: number) { Taro.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
function goBanner(url: string) {
  if (url) Taro.navigateTo({ url })
}
function goActivity(type: string) {
  const map: Record<string, string> = {
    seckill: '/pages/marketing/seckill/index',
    groupon: '/pages/marketing/groupon/index',
    bargain: '/pages/marketing/bargain/index',
    coupon: '/pages/coupon/index',
  }
  const url = map[type]
  if (url) Taro.navigateTo({ url })
}
function goSeckillDetail(id: number) {
  Taro.navigateTo({ url: `/pages/marketing/seckill/detail?id=${id}` })
}

/* ==================== 交互 ==================== */
async function receiveCoupon(id: number) {
  try {
    await apiReceiveCoupon(id)
    Taro.showToast({ title: '领取成功', icon: 'success' })
  } catch {
    Taro.showToast({ title: '领取失败', icon: 'none' })
  }
}
</script>

<style scoped>
.home { padding-bottom: 40px; background: #f8fafc; min-height: 100vh; }

/* 顶部搜索栏 */
.header-bar { display: flex; align-items: center; gap: 16px; padding: 16px 20px; }
.search-bar { flex: 1; display: flex; align-items: center; gap: 12px; padding: 12px 20px; background: #f1f5f9; border-radius: 16px; }
.search-icon { font-size: 28px; }
.search-placeholder { color: #94a3b8; font-size: 28px; }
.message-box { position: relative; width: 64px; height: 64px; display: flex; align-items: center; justify-content: center; }
.message-icon { font-size: 36px; }
.message-dot { position: absolute; top: 4px; right: 4px; width: 16px; height: 16px; background: #ef4444; border-radius: 50%; border: 2px solid #fff; }

/* 轮播图 */
.banner { height: 320px; margin: 0 20px; border-radius: 16px; overflow: hidden; }
.banner-img { width: 100%; height: 100%; }

/* 营销活动 */
.activity-bar { display: flex; justify-content: space-around; padding: 24px 20px; margin: 16px 20px 0; background: #fff; border-radius: 16px; }
.activity-item { display: flex; flex-direction: column; align-items: center; }
.activity-icon { width: 88px; height: 88px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 40px; }
.activity-seckill { background: #fef2f2; }
.activity-groupon { background: #eff6ff; }
.activity-bargain { background: #f0fdf4; }
.activity-coupon { background: #fffbeb; }
.activity-name { font-size: 24px; color: #475569; margin-top: 8px; }

/* 分类 */
.category-section { margin: 16px 20px 0; padding: 24px 20px; background: #fff; border-radius: 16px; }
.category-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.category-scroll { white-space: nowrap; }
.category-row { display: flex; gap: 24px; }
.cat-item { display: flex; flex-direction: column; align-items: center; flex-shrink: 0; width: 120px; }
.cat-icon { width: 96px; height: 96px; border-radius: 50%; background: #f1f5f9; }
.cat-name { font-size: 24px; color: #475569; margin-top: 8px; }

/* 通用区块 */
.section { margin: 16px 20px 0; padding: 24px 20px; background: #fff; border-radius: 16px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.section-title { font-size: 32px; font-weight: 700; }
.more { font-size: 26px; color: #64748b; }

/* 秒杀 */
.seckill-section { padding-bottom: 20px; }
.section-title-wrap { display: flex; align-items: center; gap: 16px; }
.countdown { display: flex; align-items: center; gap: 4px; }
.countdown-text { font-size: 22px; color: #64748b; margin-right: 4px; }
.countdown-num { display: inline-flex; align-items: center; justify-content: center; min-width: 36px; height: 36px; background: #ef4444; color: #fff; font-size: 22px; border-radius: 6px; padding: 0 4px; }
.countdown-colon { color: #ef4444; font-size: 22px; font-weight: 700; }
.seckill-scroll { white-space: nowrap; }
.seckill-list { display: flex; gap: 16px; }
.seckill-card { flex-shrink: 0; width: 220px; }
.seckill-cover { width: 220px; height: 220px; border-radius: 12px; background: #f1f5f9; }
.seckill-info { margin-top: 12px; }
.seckill-name { font-size: 26px; font-weight: 600; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.seckill-price-row { display: flex; align-items: baseline; gap: 8px; margin-top: 8px; }
.seckill-price { font-size: 28px; color: #ef4444; font-weight: 700; }
.seckill-market { font-size: 22px; color: #94a3b8; text-decoration: line-through; }
.seckill-progress { margin-top: 8px; display: flex; align-items: center; gap: 8px; }
.progress-bar { flex: 1; height: 12px; background: #f1f5f9; border-radius: 6px; overflow: hidden; }
.progress-fill { height: 100%; background: #ef4444; border-radius: 6px; }
.progress-text { font-size: 20px; color: #94a3b8; }

/* 优惠券 */
.coupon-section { padding-bottom: 20px; }
.coupon-scroll { white-space: nowrap; }
.coupon-list { display: flex; gap: 16px; }
.coupon-card { flex-shrink: 0; display: flex; width: 320px; height: 140px; background: #fffbeb; border-radius: 12px; overflow: hidden; border: 1px solid #fef3c7; }
.coupon-left { width: 120px; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #fef3c7; }
.coupon-value { font-size: 36px; color: #d97706; font-weight: 700; }
.coupon-threshold { font-size: 20px; color: #b45309; margin-top: 4px; }
.coupon-right { flex: 1; display: flex; flex-direction: column; justify-content: center; padding: 0 20px; }
.coupon-name { font-size: 26px; color: #92400e; font-weight: 600; }
.coupon-btn { margin-top: 12px; align-self: flex-start; padding: 6px 16px; background: #d97706; color: #fff; font-size: 22px; border-radius: 20px; }

/* 新品 */
.new-scroll { white-space: nowrap; }
.new-list { display: flex; gap: 16px; }
.new-card { flex-shrink: 0; width: 200px; }
.new-cover { width: 200px; height: 200px; border-radius: 12px; background: #f1f5f9; }
.new-name { font-size: 26px; margin-top: 12px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.new-price { font-size: 28px; color: #ef4444; font-weight: 700; margin-top: 8px; }

/* 推荐商品 */
.product-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.product-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.product-cover { width: 100%; height: 340px; background: #f1f5f9; }
.product-info { padding: 16px; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-desc { font-size: 22px; color: #94a3b8; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.product-bottom { display: flex; align-items: baseline; gap: 12px; margin-top: 12px; }
.product-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.product-market { font-size: 22px; color: #94a3b8; text-decoration: line-through; }

/* 加载更多 */
.load-more { text-align: center; padding: 24px 0; font-size: 24px; color: #94a3b8; }
</style>
