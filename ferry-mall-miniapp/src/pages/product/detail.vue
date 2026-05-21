<template>
  <view class="detail-page" v-if="product">
    <!-- 轮播图 -->
    <swiper class="gallery" :indicator-dots="true" circular>
      <swiper-item v-for="(img, idx) in gallery" :key="idx">
        <image :src="img" class="gallery-img" mode="aspectFill" @tap="preview(idx)" />
      </swiper-item>
    </swiper>

    <!-- 价格信息 -->
    <view class="info-card">
      <view class="price-row">
        <text class="price">¥{{ (currentPrice / 100).toFixed(2) }}</text>
        <text class="market">¥{{ (product.marketPriceCent / 100).toFixed(2) }}</text>
      </view>
      <view class="name">{{ product.name }}</view>
      <view class="subtitle">{{ product.subtitle }}</view>
      <view class="meta">
        <text>销量 {{ product.sales }}</text>
        <text>库存 {{ currentStock }}</text>
      </view>
    </view>

    <!-- 收藏/评价 -->
    <view class="info-card action-row">
      <view class="action-item" @tap="onToggleFavorite">
        <text class="action-icon">{{ isFav ? '&#x2764;&#xFE0F;' : '&#x1F90D;' }}</text>
        <text class="action-label">{{ isFav ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="action-item" @tap="goComment">
        <text class="action-icon">&#x1F4AC;</text>
        <text class="action-label">评价</text>
      </view>
    </view>

    <!-- SKU 选择 -->
    <view class="info-card sku-card" @tap="showSkuPicker = true">
      <text class="label">规格</text>
      <text class="value">{{ selectedSku ? selectedSku.name : '请选择规格' }}</text>
      <text class="arrow">&gt;</text>
    </view>

    <!-- 详情 -->
    <view class="info-card">
      <view class="section-title">商品详情</view>
      <rich-text :nodes="product.detailHtml || ''" />
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-icons">
        <view class="icon-btn" @tap="goCart">
          <text class="icon">&#x1F6D2;</text>
          <text class="label">购物车</text>
          <text v-if="cart.totalCount > 0" class="badge">{{ cart.totalCount }}</text>
        </view>
      </view>
      <view class="action-btns">
        <view class="btn-cart" @tap="onAddCart">加入购物车</view>
        <view class="btn-buy" @tap="onBuyNow">立即购买</view>
      </view>
    </view>

    <!-- SKU 弹窗 -->
    <view v-if="showSkuPicker" class="sku-mask" @tap="showSkuPicker = false">
      <view class="sku-panel" @tap.stop>
        <view class="sku-header">
          <image :src="product.coverUrl" class="sku-img" mode="aspectFill" />
          <view class="sku-info">
            <view class="sku-price">¥{{ (currentPrice / 100).toFixed(2) }}</view>
            <view class="sku-stock">库存 {{ currentStock }}</view>
          </view>
          <text class="sku-close" @tap="showSkuPicker = false">&#x2715;</text>
        </view>
        <view class="sku-options">
          <view v-for="s in product.skus" :key="s.id"
            class="sku-option" :class="{ active: selectedSku?.id === s.id }" @tap="selectSku(s)">
            {{ s.name }}
          </view>
        </view>
        <view class="sku-quantity">
          <text>数量</text>
          <view class="qty-btns">
            <text class="qty-btn" @tap="quantity > 1 && quantity--">-</text>
            <text class="qty-num">{{ quantity }}</text>
            <text class="qty-btn" @tap="quantity++">+</text>
          </view>
        </view>
        <view class="sku-confirm" @tap="confirmSku">确定</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { getProductDetail, getProductSkuList, type ProductSpu, type SkuDO } from '@/api/product'
import { addFavorite, removeFavorite, checkFavorite, addFootprint } from '@/api/member'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const product = ref<ProductSpu>()
const skus = ref<SkuDO[]>([])
const selectedSku = ref<SkuDO | null>(null)
const quantity = ref(1)
const showSkuPicker = ref(false)
const isFav = ref(false)
const cart = useCartStore()
const user = useUserStore()

const currentPrice = computed(() => selectedSku.value ? selectedSku.value.priceCent : product.value?.priceCent ?? 0)
const currentStock = computed(() => selectedSku.value ? selectedSku.value.stock : product.value?.stock ?? 0)
const gallery = computed(() => {
  if (product.value?.albumUrls?.length) return product.value.albumUrls
  return product.value?.coverUrl ? [product.value.coverUrl] : []
})

useLoad(async (query) => {
  const id = Number(query.id || 1)
  product.value = await getProductDetail(id)
  try {
    skus.value = await getProductSkuList(id)
    if (skus.value.length > 0) {
      product.value.skus = skus.value
      selectedSku.value = skus.value[0]
    }
  } catch { /* ignore */ }
  try {
    isFav.value = await checkFavorite(id)
  } catch { isFav.value = false }
  try {
    if (product.value) {
      await addFootprint(id)
    }
  } catch { /* ignore */ }
})

function selectSku(s: SkuDO) { selectedSku.value = s }
function preview(idx: number) {
  Taro.previewImage({ current: gallery.value[idx], urls: gallery.value })
}
function goCart() { Taro.switchTab({ url: '/pages/cart/index' }) }

async function checkLogin(): Promise<boolean> {
  if (!user.isLoggedIn) {
    const ok = await user.checkLogin()
    if (!ok) {
      Taro.showModal({ title: '提示', content: '请先登录', success: (res) => {
        if (res.confirm) user.wxLogin()
      }})
      return false
    }
  }
  return true
}

function onAddCart() {
  if (!product.value) return
  if (!selectedSku.value && product.value.skus && product.value.skus.length > 0) {
    showSkuPicker.value = true
    return
  }
  cart.add({
    spuId: product.value.id,
    skuId: selectedSku.value?.id,
    skuName: selectedSku.value?.name,
    name: product.value.name,
    coverUrl: selectedSku.value?.imageUrl || product.value.coverUrl,
    priceCent: currentPrice.value,
    quantity: quantity.value,
    checked: true
  })
  Taro.showToast({ title: '已加入购物车', icon: 'success' })
}

function onBuyNow() {
  if (!product.value) return
  if (!selectedSku.value && product.value.skus && product.value.skus.length > 0) {
    showSkuPicker.value = true
    return
  }
  const params = encodeURIComponent(JSON.stringify([{
    spuId: product.value.id,
    skuId: selectedSku.value?.id,
    quantity: quantity.value
  }]))
  Taro.navigateTo({ url: `/pages/order/confirm?items=${params}` })
}

function confirmSku() {
  showSkuPicker.value = false
}

async function onToggleFavorite() {
  if (!product.value) return
  try {
    if (isFav.value) {
      await removeFavorite(product.value.id)
      isFav.value = false
      Taro.showToast({ title: '已取消收藏', icon: 'success' })
    } else {
      await addFavorite(product.value.id)
      isFav.value = true
      Taro.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch (e: any) {
    Taro.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}

function goComment() {
  if (product.value) {
    Taro.navigateTo({ url: `/pages/product/comment?spuId=${product.value.id}` })
  }
}
</script>

<style scoped>
.gallery { height: 750px; }
.gallery-img { width: 100%; height: 100%; }
.info-card { padding: 24px; margin: 16px 20px; background: #fff; border-radius: 16px; }
.price-row { display: flex; align-items: baseline; gap: 16px; }
.price { font-size: 48px; color: #ef4444; font-weight: 800; }
.market { font-size: 26px; color: #94a3b8; text-decoration: line-through; }
.name { font-size: 32px; font-weight: 700; margin-top: 12px; }
.subtitle { font-size: 26px; color: #64748b; margin-top: 8px; }
.meta { display: flex; gap: 24px; font-size: 24px; color: #94a3b8; margin-top: 16px; }
.sku-card { display: flex; align-items: center; }
.sku-card .label { color: #64748b; font-size: 26px; width: 80px; }
.sku-card .value { flex: 1; font-size: 28px; }
.sku-card .arrow { color: #94a3b8; font-size: 28px; }
.action-row { display: flex; justify-content: space-around; padding: 20px 0; }
.action-item { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.action-icon { font-size: 36px; }
.action-label { font-size: 24px; color: #64748b; }
.section-title { font-size: 30px; font-weight: 700; margin-bottom: 16px; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; align-items: center; padding: 12px 20px; padding-bottom: calc(12px + env(safe-area-inset-bottom)); background: #fff; border-top: 1px solid #f1f5f9; }
.action-icons { display: flex; gap: 32px; padding: 0 24px; }
.icon-btn { display: flex; flex-direction: column; align-items: center; position: relative; }
.icon-btn .icon { font-size: 36px; }
.icon-btn .label { font-size: 20px; color: #64748b; }
.badge { position: absolute; top: -6px; right: -12px; background: #ef4444; color: #fff; font-size: 20px; min-width: 32px; height: 32px; border-radius: 16px; text-align: center; line-height: 32px; padding: 0 6px; }
.action-btns { flex: 1; display: flex; gap: 16px; }
.btn-cart { flex: 1; text-align: center; padding: 20px 0; background: #f59e0b; color: #fff; border-radius: 40px; font-size: 28px; font-weight: 600; }
.btn-buy { flex: 1; text-align: center; padding: 20px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 28px; font-weight: 600; }
.sku-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 100; display: flex; align-items: flex-end; }
.sku-panel { width: 100%; background: #fff; border-radius: 24px 24px 0 0; padding: 24px; padding-bottom: calc(24px + env(safe-area-inset-bottom)); }
.sku-header { display: flex; align-items: center; gap: 20px; margin-bottom: 24px; }
.sku-img { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; }
.sku-info { flex: 1; }
.sku-price { font-size: 36px; color: #ef4444; font-weight: 700; }
.sku-stock { font-size: 24px; color: #64748b; margin-top: 8px; }
.sku-close { font-size: 36px; color: #94a3b8; padding: 12px; }
.sku-options { display: flex; flex-wrap: wrap; gap: 16px; margin-bottom: 24px; }
.sku-option { padding: 12px 24px; background: #f1f5f9; border-radius: 8px; font-size: 26px; }
.sku-option.active { background: #2563eb; color: #fff; }
.sku-quantity { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.qty-btns { display: flex; align-items: center; gap: 16px; }
.qty-btn { width: 56px; height: 56px; background: #f1f5f9; border-radius: 8px; text-align: center; line-height: 56px; font-size: 32px; }
.qty-num { width: 60px; text-align: center; font-size: 30px; }
.sku-confirm { text-align: center; padding: 22px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
