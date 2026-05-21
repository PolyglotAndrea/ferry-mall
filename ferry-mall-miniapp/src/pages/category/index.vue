<template>
  <view class="category-page">
    <!-- 左侧一级分类 -->
    <scroll-view class="left" scroll-y :scroll-top="leftScrollTop">
      <view
        v-for="c in categories"
        :key="c.id"
        class="cat-item"
        :class="{ active: activeId === c.id }"
        @tap="selectCategory(c.id)"
      >
        {{ c.name }}
      </view>
    </scroll-view>

    <!-- 右侧内容 -->
    <scroll-view class="right" scroll-y :scroll-top="rightScrollTop" @scrolltolower="onLoadMore">
      <!-- 二级分类 -->
      <view v-if="subCategories.length > 0" class="sub-section">
        <view class="sub-grid">
          <view
            v-for="s in subCategories"
            :key="s.id"
            class="sub-item"
            @tap="selectSubCategory(s.id)"
          >
            <image
              :src="s.icon || 'https://dummyimage.com/160x160/e5e7eb/666&text=S'"
              class="sub-icon"
              mode="aspectFill"
            />
            <text class="sub-name">{{ s.name }}</text>
          </view>
        </view>
      </view>

      <!-- 排序栏 -->
      <view v-if="products.length > 0 || loading" class="sort-bar">
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
      <view v-if="products.length > 0" class="product-grid">
        <view
          v-for="p in products"
          :key="p.id"
          class="product-card"
          @tap="goDetail(p.id)"
        >
          <image :src="p.coverUrl" class="product-cover" mode="aspectFill" />
          <view class="product-info">
            <view class="product-name">{{ p.name }}</view>
            <view class="product-desc">{{ p.subtitle }}</view>
            <view class="product-bottom">
              <text class="product-price">¥{{ (p.priceCent / 100).toFixed(2) }}</text>
              <text class="product-market">¥{{ (p.marketPriceCent / 100).toFixed(2) }}</text>
            </view>
            <view class="product-sales">销量 {{ p.sales }}</view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty">
        <text class="empty-icon">&#x1F4E6;</text>
        <text class="empty-text">该分类暂无商品</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading">加载中...</view>
      <view v-else-if="noMore && products.length > 0" class="no-more">没有更多了</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref, onMounted } from 'vue'
import { getCategoryList, getProductPage, type CategoryDO, type ProductSpu } from '@/api/product'

const PAGE_SIZE = 10

const categories = ref<CategoryDO[]>([])
const activeId = ref<number>(0)
const subCategories = ref<CategoryDO[]>([])
const products = ref<ProductSpu[]>([])
const loading = ref(false)
const noMore = ref(false)
const pageNo = ref(1)
const isLeafCategory = ref(false)
const leftScrollTop = ref(0)
const rightScrollTop = ref(0)

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

async function selectCategory(id: number) {
  activeId.value = id
  rightScrollTop.value = 0
  sortKey.value = 'default'
  sortDir.value = 'desc'
  pageNo.value = 1
  noMore.value = false
  products.value = []
  loading.value = true

  try {
    const subs = await getCategoryList(id)
    subCategories.value = subs
    if (subs.length === 0) {
      isLeafCategory.value = true
      const res = await getProductPage({
        categoryId: id,
        pageNo: 1,
        pageSize: PAGE_SIZE,
        sort: getSortParam(),
      })
      products.value = res.list || []
      if ((res.list || []).length < PAGE_SIZE) noMore.value = true
    } else {
      isLeafCategory.value = false
      // 默认加载第一个二级分类的商品
      await loadSubProducts(subs[0].id)
    }
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function selectSubCategory(id: number) {
  rightScrollTop.value = 0
  sortKey.value = 'default'
  sortDir.value = 'desc'
  pageNo.value = 1
  noMore.value = false
  products.value = []
  await loadSubProducts(id)
}

async function loadSubProducts(categoryId: number) {
  loading.value = true
  try {
    const res = await getProductPage({
      categoryId,
      pageNo: pageNo.value,
      pageSize: PAGE_SIZE,
      sort: getSortParam(),
    })
    const list = res.list || []
    if (pageNo.value === 1) {
      products.value = list
    } else {
      products.value.push(...list)
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
  } else {
    sortKey.value = key
    if (key === 'price') sortDir.value = 'asc'
  }
  pageNo.value = 1
  noMore.value = false
  products.value = []
  // 重新加载当前选中的叶子分类商品
  const currentLeafId = isLeafCategory.value ? activeId.value : subCategories.value[0]?.id
  if (currentLeafId) loadSubProducts(currentLeafId)
}

async function onLoadMore() {
  if (loading.value || noMore.value) return
  pageNo.value++
  const currentLeafId = isLeafCategory.value ? activeId.value : subCategories.value[0]?.id
  if (currentLeafId) await loadSubProducts(currentLeafId)
}

onMounted(async () => {
  try {
    categories.value = await getCategoryList(0)
    if (categories.value.length > 0) {
      await selectCategory(categories.value[0].id)
    }
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载分类失败', icon: 'none' })
  }
})

function goDetail(id: number) {
  Taro.navigateTo({ url: `/pages/product/detail?id=${id}` })
}
</script>

<style scoped>
.category-page { display: flex; height: 100vh; background: #f8fafc; }

/* 左侧分类 */
.left { width: 200px; background: #f8fafc; flex-shrink: 0; height: 100%; }
.cat-item { padding: 28px 16px; font-size: 26px; color: #64748b; text-align: center; border-left: 4px solid transparent; }
.cat-item.active { background: #fff; color: #2563eb; border-left-color: #2563eb; font-weight: 700; }

/* 右侧内容 */
.right { flex: 1; height: 100%; background: #fff; }
.sub-section { padding: 20px; }
.sub-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.sub-item { display: flex; flex-direction: column; align-items: center; }
.sub-icon { width: 120px; height: 120px; border-radius: 16px; background: #f1f5f9; }
.sub-name { font-size: 24px; color: #475569; margin-top: 8px; }

/* 排序栏 */
.sort-bar { display: flex; align-items: center; padding: 0 20px; border-bottom: 1px solid #f1f5f9; }
.sort-item { flex: 1; text-align: center; padding: 20px 0; font-size: 28px; color: #64748b; }
.sort-item.active { color: #2563eb; font-weight: 700; }
.sort-arrow { font-size: 22px; margin-left: 4px; }

/* 商品网格 */
.product-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; padding: 20px; }
.product-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.product-cover { width: 100%; height: 340px; background: #f1f5f9; }
.product-info { padding: 16px; }
.product-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-desc { font-size: 22px; color: #94a3b8; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.product-bottom { display: flex; align-items: baseline; gap: 12px; margin-top: 12px; }
.product-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.product-market { font-size: 22px; color: #94a3b8; text-decoration: line-through; }
.product-sales { font-size: 22px; color: #94a3b8; margin-top: 8px; }

/* 空状态 */
.empty { display: flex; flex-direction: column; align-items: center; padding: 120px 0; }
.empty-icon { font-size: 80px; margin-bottom: 24px; }
.empty-text { font-size: 30px; color: #64748b; }

/* 加载 */
.loading { text-align: center; padding: 24px 0; color: #94a3b8; font-size: 26px; }
.no-more { text-align: center; padding: 24px 0; color: #94a3b8; font-size: 24px; }
</style>
