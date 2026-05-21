<template>
  <view class="cart-page">
    <view v-if="cart.items.length === 0" class="empty">
      <text class="empty-icon">&#x1F6D2;</text>
      <text class="empty-text">购物车是空的</text>
      <text class="empty-btn" @tap="goShop">去逛逛</text>
    </view>
    <view v-else>
      <view class="cart-list">
        <view v-for="item in cart.items" :key="item.id" class="cart-item">
          <text class="check" :class="{ checked: item.selected === 1 }" @tap="cart.toggle(item.id)">
            {{ item.selected === 1 ? '&#x2713;' : '' }}
          </text>
          <image :src="productMap[item.spuId]?.coverUrl || ''" class="item-cover" mode="aspectFill" />
          <view class="item-info">
            <view class="item-name">{{ productMap[item.spuId]?.name || '商品加载中...' }}</view>
            <view v-if="item.skuId && skuMap[item.skuId]" class="item-sku">{{ skuMap[item.skuId].name }}</view>
            <view class="item-bottom">
              <text class="item-price">¥{{ ((productMap[item.spuId]?.priceCent || 0) / 100).toFixed(2) }}</text>
              <view class="qty-btns">
                <text class="qty-btn" @tap="cart.updateQuantity(item.id, item.quantity - 1)">-</text>
                <text class="qty-num">{{ item.quantity }}</text>
                <text class="qty-btn" @tap="cart.updateQuantity(item.id, item.quantity + 1)">+</text>
              </view>
            </view>
          </view>
          <text class="del" @tap="onDelete(item.id)">&#x2715;</text>
        </view>
      </view>
      <view class="cart-footer">
        <view class="all-check" @tap="cart.toggleAll">
          <text class="check-box" :class="{ checked: cart.isAllChecked }">{{ cart.isAllChecked ? '&#x2713;' : '' }}</text>
          <text>全选</text>
        </view>
        <view class="total">
          合计 <text class="total-price">¥{{ (totalCent / 100).toFixed(2) }}</text>
        </view>
        <view class="settle-btn" :class="{ disabled: cart.checkedCount === 0 }" @tap="onSettle">
          结算({{ cart.checkedCount }})
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref, computed, onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'
import { getProductDetail, getProductSkuList } from '@/api/product'
import type { ProductSpu, SkuDO } from '@/api/product'

const cart = useCartStore()
const productMap = ref<Record<number, ProductSpu>>({})
const skuMap = ref<Record<number, SkuDO>>({})

const totalCent = computed(() => {
  return cart.items
    .filter(i => i.selected === 1)
    .reduce((s, i) => {
      const price = productMap.value[i.spuId]?.priceCent || 0
      return s + price * i.quantity
    }, 0)
})

async function fetchProductInfo() {
  const spuIds = [...new Set(cart.items.map(i => i.spuId))]
  const skuIds = cart.items.map(i => i.skuId).filter(Boolean) as number[]

  for (const spuId of spuIds) {
    if (!productMap.value[spuId]) {
      try {
        const product = await getProductDetail(spuId)
        productMap.value[spuId] = product
      } catch {
        // ignore
      }
    }
  }

  for (const skuId of skuIds) {
    if (!skuMap.value[skuId]) {
      try {
        const item = cart.items.find(i => i.skuId === skuId)
        if (item) {
          const skus = await getProductSkuList(item.spuId)
          for (const sku of skus) {
            skuMap.value[sku.id] = sku
          }
        }
      } catch {
        // ignore
      }
    }
  }
}

function onDelete(cartId: number) {
  Taro.showModal({
    title: '提示',
    content: '确定删除该商品？',
    success: (res) => {
      if (res.confirm) {
        cart.remove(cartId)
      }
    }
  })
}

function goShop() { Taro.switchTab({ url: '/pages/index/index' }) }

function onSettle() {
  const items = cart.items.filter(i => i.selected === 1)
  if (items.length === 0) {
    Taro.showToast({ title: '请选择商品', icon: 'none' })
    return
  }
  const params = encodeURIComponent(JSON.stringify(items.map(i => ({
    spuId: i.spuId,
    skuId: i.skuId,
    quantity: i.quantity
  }))))
  Taro.navigateTo({ url: `/pages/order/confirm?items=${params}` })
}

onMounted(() => {
  cart.fetchCart().then(() => fetchProductInfo())
})

Taro.useDidShow(() => {
  cart.fetchCart().then(() => fetchProductInfo())
})
</script>

<style scoped>
.cart-page { min-height: 100vh; background: #f8fafc; padding-bottom: 140px; }
.empty { display: flex; flex-direction: column; align-items: center; padding-top: 200px; }
.empty-icon { font-size: 120px; }
.empty-text { font-size: 28px; color: #94a3b8; margin-top: 20px; }
.empty-btn { margin-top: 24px; padding: 14px 48px; background: #2563eb; color: #fff; border-radius: 32px; font-size: 28px; }
.cart-list { padding: 16px 20px; }
.cart-item { display: flex; align-items: center; gap: 16px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.check { width: 40px; height: 40px; border-radius: 50%; border: 2px solid #cbd5e1; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; flex-shrink: 0; }
.check.checked { background: #2563eb; border-color: #2563eb; }
.item-cover { width: 160px; height: 160px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 28px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.item-sku { font-size: 22px; color: #94a3b8; margin-top: 6px; background: #f1f5f9; display: inline-block; padding: 4px 12px; border-radius: 6px; }
.item-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.item-price { font-size: 30px; color: #ef4444; font-weight: 700; }
.qty-btns { display: flex; align-items: center; gap: 12px; }
.qty-btn { width: 48px; height: 48px; background: #f1f5f9; border-radius: 8px; text-align: center; line-height: 48px; font-size: 28px; }
.qty-num { width: 48px; text-align: center; font-size: 26px; }
.del { font-size: 28px; color: #94a3b8; padding: 12px; }
.cart-footer { position: fixed; bottom: 0; left: 0; right: 0; display: flex; align-items: center; padding: 16px 20px; padding-bottom: calc(16px + env(safe-area-inset-bottom)); background: #fff; border-top: 1px solid #f1f5f9; }
.all-check { display: flex; align-items: center; gap: 12px; font-size: 26px; }
.check-box { width: 40px; height: 40px; border-radius: 50%; border: 2px solid #cbd5e1; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; }
.check-box.checked { background: #2563eb; border-color: #2563eb; }
.total { flex: 1; text-align: right; padding-right: 20px; font-size: 26px; }
.total-price { font-size: 32px; color: #ef4444; font-weight: 700; }
.settle-btn { padding: 16px 40px; background: #ef4444; color: #fff; border-radius: 36px; font-size: 28px; font-weight: 600; }
.settle-btn.disabled { background: #cbd5e1; }
</style>
