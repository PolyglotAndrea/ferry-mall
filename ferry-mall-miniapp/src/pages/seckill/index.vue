<template>
  <view class="seckill-page">
    <!-- 顶部 Banner -->
    <view class="header-banner">
      <view class="header-title">限时秒杀</view>
      <view class="header-subtitle">每日精选 限时抢购</view>
    </view>

    <!-- 活动标签栏 -->
    <scroll-view class="activity-tabs" scroll-x :scroll-into-view="`tab-${activeActivityId}`" scroll-with-animation>
      <view
        v-for="a in activities"
        :id="`tab-${a.id}`"
        :key="a.id"
        class="tab-item"
        :class="{ active: activeActivityId === a.id }"
        @tap="switchActivity(a.id)"
      >
        <view class="tab-time">{{ formatTimeShort(a.startTime) }}</view>
        <view class="tab-status">{{ getActivityStatusText(a) }}</view>
        <view v-if="activeActivityId === a.id" class="tab-indicator"></view>
      </view>
    </scroll-view>

    <!-- 倒计时 -->
    <view v-if="activeActivity" class="countdown-bar">
      <text class="countdown-label">{{ countdownLabel }}</text>
      <view class="countdown-box">
        <text class="countdown-num">{{ countdown.hours }}</text>
        <text class="countdown-sep">:</text>
        <text class="countdown-num">{{ countdown.minutes }}</text>
        <text class="countdown-sep">:</text>
        <text class="countdown-num">{{ countdown.seconds }}</text>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="product-list">
      <view v-for="p in products" :key="p.id" class="product-card" @tap="goProductDetail(p.spuId)">
        <image
          class="product-cover"
          :src="p.coverUrl || 'https://dummyimage.com/200x200/e5e7eb/666&text=商品'"
          mode="aspectFill"
        />
        <view class="product-info">
          <view class="product-name">{{ p.name || `商品 #${p.spuId}` }}</view>
          <view class="product-price-row">
            <text class="seckill-price">¥{{ (p.seckillPriceCent / 100).toFixed(2) }}</text>
            <text class="original-price">¥{{ ((p.marketPriceCent ?? p.seckillPriceCent * 2) / 100).toFixed(2) }}</text>
          </view>
          <view class="product-stock-row">
            <view class="progress-wrap">
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: soldPercent(p) + '%' }"></view>
              </view>
              <text class="progress-text">已抢 {{ p.sold }} 件</text>
            </view>
            <view class="btn-buy" :class="{ disabled: p.stock <= 0 }" @tap.stop="onBuy(p)">
              {{ p.stock <= 0 ? '已售罄' : '立即抢购' }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!loading && products.length === 0" class="empty">
      <text class="empty-icon">&#x23F0;</text>
      <text class="empty-text">暂无秒杀商品</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onMounted, ref, computed, onUnmounted } from 'vue'
import { getSeckillActivities, getSeckillProducts, type SeckillActivity, type SeckillProduct } from '@/api/marketing'

const activities = ref<SeckillActivity[]>([])
const products = ref<SeckillProduct[]>([])
const activeActivityId = ref<number>(0)
const loading = ref(false)
const countdownTimer = ref<number | null>(null)
const now = ref(Date.now())

const activeActivity = computed(() =>
  activities.value.find(a => a.id === activeActivityId.value)
)

const countdownLabel = computed(() => {
  const a = activeActivity.value
  if (!a) return ''
  const start = new Date(a.startTime).getTime()
  const end = new Date(a.endTime).getTime()
  const t = now.value
  if (t < start) return '距离开始'
  if (t <= end) return '距离结束'
  return '已结束'
})

const countdown = computed(() => {
  const a = activeActivity.value
  if (!a) return { hours: '00', minutes: '00', seconds: '00' }
  const start = new Date(a.startTime).getTime()
  const end = new Date(a.endTime).getTime()
  const t = now.value
  let diff = 0
  if (t < start) diff = start - t
  else if (t <= end) diff = end - t
  else return { hours: '00', minutes: '00', seconds: '00' }

  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return {
    hours: String(hours).padStart(2, '0'),
    minutes: String(minutes).padStart(2, '0'),
    seconds: String(seconds).padStart(2, '0'),
  }
})

function getActivityStatusText(a: SeckillActivity): string {
  const t = now.value
  const start = new Date(a.startTime).getTime()
  const end = new Date(a.endTime).getTime()
  if (t < start) return '即将开始'
  if (t <= end) return '抢购中'
  return '已结束'
}

function formatTimeShort(t: string): string {
  return t?.slice(11, 16) || ''
}

function soldPercent(p: SeckillProduct): number {
  const total = p.stock + p.sold
  return total > 0 ? Math.round((p.sold / total) * 100) : 0
}

async function loadActivities() {
  try {
    activities.value = await getSeckillActivities()
    if (activities.value.length > 0) {
      // 默认选中第一个进行中的活动，如果没有则选第一个
      const ongoing = activities.value.find(a => {
        const t = Date.now()
        const start = new Date(a.startTime).getTime()
        const end = new Date(a.endTime).getTime()
        return t >= start && t <= end
      })
      activeActivityId.value = ongoing ? ongoing.id : activities.value[0].id
      await loadProducts(activeActivityId.value)
    }
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载活动失败', icon: 'none' })
  }
}

async function loadProducts(activityId: number) {
  loading.value = true
  try {
    products.value = await getSeckillProducts(activityId)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载商品失败', icon: 'none' })
    products.value = []
  } finally {
    loading.value = false
  }
}

async function switchActivity(id: number) {
  if (activeActivityId.value === id) return
  activeActivityId.value = id
  await loadProducts(id)
}

function goProductDetail(spuId: number) {
  if (spuId) {
    Taro.navigateTo({ url: `/pages/product/detail?id=${spuId}` })
  }
}

function onBuy(p: SeckillProduct) {
  if (p.stock <= 0) {
    Taro.showToast({ title: '商品已售罄', icon: 'none' })
    return
  }
  const params = encodeURIComponent(JSON.stringify([{ spuId: p.spuId, quantity: 1 }]))
  Taro.navigateTo({ url: `/pages/order/confirm?items=${params}` })
}

function startCountdown() {
  countdownTimer.value = setInterval(() => {
    now.value = Date.now()
  }, 1000) as unknown as number
}

onMounted(() => {
  loadActivities()
  startCountdown()
})

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style scoped>
.seckill-page { min-height: 100vh; background: #f8fafc; padding-bottom: 40px; }

/* Header */
.header-banner {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  padding: 40px 24px 60px;
  text-align: center;
  color: #fff;
}
.header-title { font-size: 44px; font-weight: 800; }
.header-subtitle { font-size: 26px; opacity: 0.9; margin-top: 8px; }

/* Tabs */
.activity-tabs {
  white-space: nowrap;
  background: #fff;
  padding: 16px 0;
  margin-top: -30px;
  margin-left: 20px;
  margin-right: 20px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.tab-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 28px;
  position: relative;
  min-width: 120px;
}
.tab-item.active .tab-time { color: #ef4444; font-weight: 700; }
.tab-item.active .tab-status { color: #ef4444; font-weight: 600; }
.tab-time { font-size: 30px; color: #475569; font-weight: 600; }
.tab-status { font-size: 22px; color: #94a3b8; margin-top: 4px; }
.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 4px;
  background: #ef4444;
  border-radius: 2px;
}

/* Countdown */
.countdown-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 20px;
  margin: 16px 20px 0;
  background: #fff;
  border-radius: 16px;
}
.countdown-label { font-size: 26px; color: #64748b; }
.countdown-box { display: flex; align-items: center; gap: 6px; }
.countdown-num {
  display: inline-block;
  min-width: 44px;
  padding: 6px 8px;
  background: #ef4444;
  color: #fff;
  font-size: 26px;
  font-weight: 700;
  text-align: center;
  border-radius: 8px;
}
.countdown-sep { font-size: 26px; color: #ef4444; font-weight: 700; }

/* Product List */
.product-list { padding: 16px 20px; }
.product-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.product-cover { width: 180px; height: 180px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.product-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-price-row { display: flex; align-items: baseline; gap: 12px; margin-top: 8px; }
.seckill-price { font-size: 36px; color: #ef4444; font-weight: 800; }
.original-price { font-size: 24px; color: #94a3b8; text-decoration: line-through; }
.product-stock-row { display: flex; align-items: center; justify-content: space-between; margin-top: 12px; }
.progress-wrap { flex: 1; }
.progress-bar { height: 10px; background: #e2e8f0; border-radius: 5px; overflow: hidden; width: 180px; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #ef4444, #f87171); border-radius: 5px; }
.progress-text { font-size: 20px; color: #94a3b8; margin-top: 6px; }
.btn-buy {
  padding: 12px 24px;
  background: #ef4444;
  color: #fff;
  border-radius: 28px;
  font-size: 26px;
  font-weight: 600;
  flex-shrink: 0;
}
.btn-buy.disabled { background: #cbd5e1; }

/* Empty */
.empty { text-align: center; padding: 120px 0; }
.empty-icon { font-size: 80px; }
.empty-text { display: block; font-size: 28px; color: #94a3b8; margin-top: 16px; }
</style>
