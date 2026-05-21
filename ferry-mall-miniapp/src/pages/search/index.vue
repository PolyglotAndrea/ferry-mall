<template>
  <view class="search-page">
    <!-- 顶部搜索栏 -->
    <view class="search-header">
      <view class="search-bar">
        <text class="search-icon">&#x1F50D;</text>
        <input
          v-model="keyword"
          class="search-input"
          placeholder="搜索商品"
          confirm-type="search"
          @confirm="onSearch"
        />
        <text v-if="keyword" class="clear-icon" @tap="keyword = ''">&#x2715;</text>
      </view>
      <text class="search-btn" @tap="onSearch">搜索</text>
    </view>

    <!-- 搜索前：历史 + 热门 -->
    <view v-if="!showResult" class="search-suggest">
      <!-- 历史搜索 -->
      <view v-if="historyList.length > 0" class="section">
        <view class="section-header">
          <text class="section-title">历史搜索</text>
          <text class="section-action" @tap="clearHistory">&#x1F5D1; 清除</text>
        </view>
        <view class="tag-list">
          <text
            v-for="(tag, idx) in historyList"
            :key="idx"
            class="tag"
            @tap="onTapTag(tag)"
          >{{ tag }}</text>
        </view>
      </view>

      <!-- 热门搜索 -->
      <view class="section">
        <view class="section-header">
          <text class="section-title">热门搜索</text>
        </view>
        <view class="tag-list">
          <text
            v-for="tag in hotTags"
            :key="tag"
            class="tag hot"
            @tap="onTapTag(tag)"
          >{{ tag }}</text>
        </view>
      </view>
    </view>

    <!-- 搜索结果 -->
    <view v-else class="result-area">
      <!-- 排序栏 -->
      <view class="sort-bar">
        <view
          v-for="s in sortOptions"
          :key="s.key"
          class="sort-item"
          :class="{ active: sortKey === s.key }"
          @tap="onSort(s.key)"
        >
          <text>{{ s.label }}</text>
          <text v-if="s.key === 'price'" class="sort-arrow">
            {{ sortKey === 'price' && sortDir === 'asc' ? '&#x2191;' : '&#x2193;' }}
          </text>
        </view>
      </view>

      <!-- 商品网格 -->
      <view v-if="productList.length > 0" class="product-grid">
        <view
          v-for="item in productList"
          :key="item.id"
          class="product-card"
          @tap="goDetail(item.id)"
        >
          <image :src="item.coverUrl" class="product-cover" mode="aspectFill" />
          <view class="product-info">
            <view class="product-name">{{ item.name }}</view>
            <view class="product-desc">{{ item.subtitle }}</view>
            <view class="product-bottom">
              <text class="product-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
              <text class="product-market">¥{{ (item.marketPriceCent / 100).toFixed(2) }}</text>
            </view>
            <view class="product-sales">销量 {{ item.sales }}</view>
          </view>
        </view>
      </view>

      <!-- 空结果 -->
      <view v-else-if="!loading" class="empty">
        <text class="empty-icon">&#x1F50D;</text>
        <text class="empty-text">未找到相关商品</text>
        <text class="empty-tip">换个关键词试试吧</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading">加载中...</view>
      <view v-else-if="noMore && productList.length > 0" class="no-more">没有更多了</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref, onMounted } from 'vue'
import { getProductPage, type ProductSpu } from '@/api/product'

const HISTORY_KEY = 'ferry_search_history'
const PAGE_SIZE = 10

const keyword = ref('')
const showResult = ref(false)
const loading = ref(false)
const noMore = ref(false)
const pageNo = ref(1)
const productList = ref<ProductSpu[]>([])
const historyList = ref<string[]>([])
const hotTags = ref<string[]>(['手表', '咖啡', '背包', '耳机', '键盘', '运动鞋', '手机壳'])

type SortKey = 'default' | 'sales' | 'price'
const sortKey = ref<SortKey>('default')
const sortDir = ref<'asc' | 'desc'>('desc')

const sortOptions: { key: SortKey; label: string }[] = [
  { key: 'default', label: '综合' },
  { key: 'sales', label: '销量' },
  { key: 'price', label: '价格' },
]

function getSortParam(): string | undefined {
  if (sortKey.value === 'default') return undefined
  if (sortKey.value === 'sales') return 'sales_desc'
  if (sortKey.value === 'price') return sortDir.value === 'asc' ? 'price_asc' : 'price_desc'
  return undefined
}

function loadHistory() {
  try {
    const raw = Taro.getStorageSync(HISTORY_KEY)
    if (raw) historyList.value = JSON.parse(raw)
  } catch {
    historyList.value = []
  }
}

function saveHistory(word: string) {
  const w = word.trim()
  if (!w) return
  let list = historyList.value.filter(item => item !== w)
  list.unshift(w)
  if (list.length > 20) list = list.slice(0, 20)
  historyList.value = list
  try {
    Taro.setStorageSync(HISTORY_KEY, JSON.stringify(list))
  } catch { /* ignore */ }
}

function clearHistory() {
  Taro.showModal({
    title: '提示',
    content: '确定清除所有历史搜索？',
    success: (res) => {
      if (res.confirm) {
        historyList.value = []
        try { Taro.removeStorageSync(HISTORY_KEY) } catch { /* ignore */ }
      }
    }
  })
}

function onTapTag(tag: string) {
  keyword.value = tag
  onSearch()
}

async function onSearch() {
  const w = keyword.value.trim()
  if (!w) {
    Taro.showToast({ title: '请输入搜索关键词', icon: 'none' })
    return
  }
  saveHistory(w)
  showResult.value = true
  pageNo.value = 1
  noMore.value = false
  productList.value = []
  await fetchList()
}

async function fetchList() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await getProductPage({
      keyword: keyword.value.trim(),
      pageNo: pageNo.value,
      pageSize: PAGE_SIZE,
      sort: getSortParam(),
    })
    const list = res.list || []
    if (pageNo.value === 1) {
      productList.value = list
    } else {
      productList.value.push(...list)
    }
    if (list.length < PAGE_SIZE) noMore.value = true
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function onSort(key: SortKey) {
  if (sortKey.value === key) {
    if (key === 'price') {
      sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
    }
    // default / sales 不切换方向，只保持当前
  } else {
    sortKey.value = key
    if (key === 'price') sortDir.value = 'asc'
  }
  pageNo.value = 1
  noMore.value = false
  productList.value = []
  fetchList()
}

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

// 触底加载
Taro.useReachBottom?.(() => {
  if (showResult.value && !loading.value && !noMore.value) {
    pageNo.value++
    fetchList()
  }
})

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.search-page { min-height: 100vh; background: #f8fafc; }

/* 顶部搜索栏 */
.search-header { display: flex; align-items: center; gap: 16px; padding: 16px 20px; background: #fff; }
.search-bar { flex: 1; display: flex; align-items: center; gap: 12px; padding: 0 20px; background: #f1f5f9; border-radius: 36px; height: 72px; }
.search-icon { font-size: 28px; color: #94a3b8; }
.search-input { flex: 1; font-size: 28px; color: #1e293b; height: 100%; }
.clear-icon { font-size: 24px; color: #94a3b8; padding: 8px; }
.search-btn { color: #2563eb; font-size: 28px; font-weight: 600; }

/* 历史/热门 */
.search-suggest { padding: 20px; }
.section { margin-bottom: 32px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.section-title { font-size: 30px; font-weight: 700; color: #1e293b; }
.section-action { font-size: 24px; color: #94a3b8; }
.tag-list { display: flex; flex-wrap: wrap; gap: 16px; }
.tag { background: #fff; color: #475569; padding: 12px 24px; border-radius: 32px; font-size: 26px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.tag.hot { color: #ef4444; }

/* 排序栏 */
.sort-bar { display: flex; align-items: center; padding: 0 20px; background: #fff; border-bottom: 1px solid #f1f5f9; }
.sort-item { flex: 1; text-align: center; padding: 20px 0; font-size: 28px; color: #64748b; }
.sort-item.active { color: #2563eb; font-weight: 700; }
.sort-arrow { font-size: 22px; margin-left: 4px; }

/* 商品网格 */
.result-area { padding: 20px; }
.product-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.product-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.product-cover { width: 100%; height: 340px; background: #f1f5f9; }
.product-info { padding: 16px; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-desc { font-size: 22px; color: #94a3b8; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.product-bottom { display: flex; align-items: baseline; gap: 12px; margin-top: 12px; }
.product-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.product-market { font-size: 22px; color: #94a3b8; text-decoration: line-through; }
.product-sales { font-size: 22px; color: #94a3b8; margin-top: 8px; }

/* 空结果 */
.empty { display: flex; flex-direction: column; align-items: center; padding: 120px 0; }
.empty-icon { font-size: 80px; margin-bottom: 24px; }
.empty-text { font-size: 30px; color: #64748b; margin-bottom: 12px; }
.empty-tip { font-size: 26px; color: #94a3b8; }

/* 加载 */
.loading { text-align: center; padding: 24px 0; color: #94a3b8; font-size: 26px; }
.no-more { text-align: center; padding: 24px 0; color: #94a3b8; font-size: 24px; }
</style>
