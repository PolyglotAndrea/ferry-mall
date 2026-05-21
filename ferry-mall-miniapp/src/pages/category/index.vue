<template>
  <view class="category-page">
    <view class="left">
      <view
        v-for="c in categories"
        :key="c.id"
        class="cat-item"
        :class="{ active: activeId === c.id }"
        @tap="selectCategory(c.id)"
      >
        {{ c.name }}
      </view>
    </view>
    <scroll-view class="right" scroll-y :scroll-top="0">
      <view v-if="subCategories.length > 0" class="sub-grid">
        <view v-for="s in subCategories" :key="s.id" class="sub-item" @tap="selectCategory(s.id, true)">
          <image :src="s.icon || 'https://dummyimage.com/160x160/e5e7eb/666&text=S'" class="sub-icon" mode="aspectFill" />
          <text class="sub-name">{{ s.name }}</text>
        </view>
      </view>
      <view v-if="products.length > 0" class="product-list">
        <view v-for="p in products" :key="p.id" class="product-item" @tap="goDetail(p.id)">
          <image :src="p.coverUrl" class="product-cover" mode="aspectFill" />
          <view class="product-info">
            <view class="product-name">{{ p.name }}</view>
            <view class="product-price">¥{{ (p.priceCent / 100).toFixed(2) }}</view>
          </view>
        </view>
      </view>
      <view v-if="subCategories.length === 0 && products.length === 0" class="empty">
        暂无商品
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref, computed, onMounted } from 'vue'
import { getCategoryList, getProductPage, type CategoryDO, type ProductSpu } from '@/api/product'

const categories = ref<CategoryDO[]>([])
const activeId = ref<number>(0)
const subCategories = ref<CategoryDO[]>([])
const products = ref<ProductSpu[]>([])

const activeParent = computed(() => categories.value.find(c => c.id === activeId.value))

async function selectCategory(id: number, leaf = false) {
  activeId.value = id
  if (leaf) {
    subCategories.value = []
    const res = await getProductPage({ categoryId: id, pageSize: 50 })
    products.value = res.list
  } else {
    subCategories.value = await getCategoryList(id)
    if (subCategories.value.length === 0) {
      const res = await getProductPage({ categoryId: id, pageSize: 50 })
      products.value = res.list
    } else {
      products.value = []
    }
  }
}

onMounted(async () => {
  categories.value = await getCategoryList(0)
  if (categories.value.length > 0) {
    selectCategory(categories.value[0].id)
  }
})

function goDetail(id: number) { Taro.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
</script>

<style scoped>
.category-page { display: flex; height: 100vh; }
.left { width: 200px; background: #f8fafc; flex-shrink: 0; }
.cat-item { padding: 28px 16px; font-size: 26px; color: #64748b; text-align: center; border-left: 4px solid transparent; }
.cat-item.active { background: #fff; color: #2563eb; border-left-color: #2563eb; font-weight: 700; }
.right { flex: 1; padding: 20px; background: #fff; }
.sub-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 24px; }
.sub-item { display: flex; flex-direction: column; align-items: center; }
.sub-icon { width: 120px; height: 120px; border-radius: 16px; background: #f1f5f9; }
.sub-name { font-size: 24px; color: #475569; margin-top: 8px; }
.product-list { }
.product-item { display: flex; padding: 16px 0; border-bottom: 1px solid #f1f5f9; }
.product-cover { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.product-info { flex: 1; margin-left: 16px; display: flex; flex-direction: column; justify-content: space-between; }
.product-name { font-size: 28px; font-weight: 700; }
.product-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.empty { text-align: center; color: #94a3b8; padding: 80px 0; }
</style>
