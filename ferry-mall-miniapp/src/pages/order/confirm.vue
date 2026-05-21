<template>
  <view class="confirm-page">
    <!-- 地址 -->
    <view class="address-card" @tap="goAddressList">
      <view v-if="address" class="address-info">
        <view class="addr-row">
          <text class="addr-name">{{ address.name }}</text>
          <text class="addr-mobile">{{ address.mobile }}</text>
        </view>
        <view class="addr-detail">
          {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
        </view>
      </view>
      <view v-else class="address-empty">
        <text>请选择收货地址</text>
        <text class="arrow">&gt;</text>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="goods-card">
      <view v-for="g in goodsList" :key="`${g.spuId}-${g.skuId ?? 0}`" class="goods-item">
        <image :src="g.coverUrl" class="goods-cover" mode="aspectFill" />
        <view class="goods-info">
          <view class="goods-name">{{ g.name }}</view>
          <view v-if="g.skuName" class="goods-sku">{{ g.skuName }}</view>
          <view class="goods-bottom">
            <text class="goods-price">¥{{ (g.priceCent / 100).toFixed(2) }}</text>
            <text class="goods-qty">x{{ g.quantity }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 优惠券 -->
    <view class="row-card" @tap="showCouponPicker = true">
      <text class="row-label">优惠券</text>
      <text class="row-value">{{ selectedCoupon ? selectedCoupon.name : '未使用' }}</text>
      <text class="arrow">&gt;</text>
    </view>

    <!-- 备注 -->
    <view class="row-card">
      <text class="row-label">订单备注</text>
      <input v-model="remark" class="remark-input" placeholder="请输入备注" />
    </view>

    <!-- 金额明细 -->
    <view class="amount-card">
      <view class="amount-row">
        <text>商品总额</text>
        <text>¥{{ (totalCent / 100).toFixed(2) }}</text>
      </view>
      <view class="amount-row">
        <text>运费</text>
        <text>免运费</text>
      </view>
      <view v-if="selectedCoupon" class="amount-row discount">
        <text>优惠券</text>
        <text>-¥{{ (selectedCoupon.discountCent / 100).toFixed(2) }}</text>
      </view>
      <view class="amount-row total">
        <text>实付金额</text>
        <text class="pay-amount">¥{{ (payAmount / 100).toFixed(2) }}</text>
      </view>
    </view>

    <!-- 底部结算 -->
    <view class="bottom-bar">
      <view class="pay-info">
        实付 <text class="pay-price">¥{{ (payAmount / 100).toFixed(2) }}</text>
      </view>
      <view class="submit-btn" @tap="onSubmit">提交订单</view>
    </view>

    <!-- 优惠券弹窗 -->
    <view v-if="showCouponPicker" class="picker-mask" @tap="showCouponPicker = false">
      <view class="picker-panel" @tap.stop>
        <view class="picker-title">选择优惠券</view>
        <view v-if="availableCoupons.length === 0" class="picker-empty">暂无可用优惠券</view>
        <view v-for="c in availableCoupons" :key="c.id" class="picker-item" @tap="selectCoupon(c)">
          <text>{{ c.name }}</text>
          <text v-if="selectedCoupon?.id === c.id" class="picker-check">&#x2713;</text>
        </view>
        <view class="picker-btn" @tap="showCouponPicker = false">确定</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { createOrder } from '@/api/order'
import { getAvailableCoupons, type CouponResp } from '@/api/coupon'
import { getAddressList, type AddressItem } from '@/api/address'
import { useCartStore } from '@/stores/cart'

interface ConfirmItem {
  spuId: number
  skuId?: number
  skuName?: string
  name: string
  coverUrl: string
  priceCent: number
  quantity: number
}

const goodsList = ref<ConfirmItem[]>([])
const address = ref<AddressItem | null>(null)
const remark = ref('')
const availableCoupons = ref<CouponResp[]>([])
const selectedCoupon = ref<CouponResp | null>(null)
const showCouponPicker = ref(false)
const cart = useCartStore()

const totalCent = computed(() => goodsList.value.reduce((s, g) => s + g.priceCent * g.quantity, 0))
const payAmount = computed(() => {
  let amt = totalCent.value
  if (selectedCoupon.value) amt -= selectedCoupon.value.discountCent
  return amt > 0 ? amt : 0
})

useLoad(async (query) => {
  try {
    const items = JSON.parse(decodeURIComponent(query.items || '[]')) as ConfirmItem[]
    goodsList.value = items
  } catch { goodsList.value = [] }

  try {
    const addrs = await getAddressList()
    address.value = addrs.find(a => a.isDefault === 1) || addrs[0] || null
  } catch { address.value = null }

  try {
    availableCoupons.value = await getAvailableCoupons()
  } catch { availableCoupons.value = [] }
})

function selectCoupon(c: CouponResp) {
  if (selectedCoupon.value?.id === c.id) {
    selectedCoupon.value = null
  } else {
    selectedCoupon.value = c
  }
}

function goAddressList() {
  Taro.navigateTo({ url: '/pages/address/list' })
}

// 供地址列表页回调
function setAddress(a: AddressItem) {
  address.value = a
}

defineExpose({ setAddress })

async function onSubmit() {
  if (!address.value) {
    Taro.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }
  if (goodsList.value.length === 0) {
    Taro.showToast({ title: '商品信息缺失', icon: 'none' })
    return
  }
  try {
    const items = goodsList.value.map(g => ({ spuId: g.spuId, skuId: g.skuId, quantity: g.quantity }))
    const order = await createOrder(
      items,
      address.value.name,
      address.value.mobile,
      `${address.value.province}${address.value.city}${address.value.district}${address.value.detail}`,
      remark.value
    )
    // 从购物车移除已结算商品
    goodsList.value.forEach(g => cart.remove(g.spuId, g.skuId))
    Taro.showToast({ title: '下单成功', icon: 'success' })
    Taro.navigateTo({ url: `/pages/order/list` })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '下单失败', icon: 'none' })
  }
}
</script>

<style scoped>
.confirm-page { padding: 20px; padding-bottom: 140px; min-height: 100vh; background: #f8fafc; }
.address-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.address-info { }
.addr-row { display: flex; align-items: center; gap: 20px; }
.addr-name { font-size: 30px; font-weight: 700; }
.addr-mobile { font-size: 28px; color: #475569; }
.addr-detail { font-size: 26px; color: #64748b; margin-top: 12px; }
.address-empty { display: flex; justify-content: space-between; align-items: center; font-size: 28px; color: #94a3b8; }
.goods-card { padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.goods-item { display: flex; gap: 16px; padding: 16px 0; border-bottom: 1px solid #f1f5f9; }
.goods-item:last-child { border-bottom: 0; }
.goods-cover { width: 140px; height: 140px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.goods-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.goods-name { font-size: 26px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.goods-sku { font-size: 22px; color: #94a3b8; background: #f1f5f9; display: inline-block; padding: 4px 12px; border-radius: 6px; }
.goods-bottom { display: flex; justify-content: space-between; align-items: center; }
.goods-price { font-size: 28px; color: #ef4444; font-weight: 700; }
.goods-qty { font-size: 24px; color: #64748b; }
.row-card { display: flex; align-items: center; padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.row-label { font-size: 28px; font-weight: 600; width: 160px; flex-shrink: 0; }
.row-value { flex: 1; font-size: 26px; color: #64748b; text-align: right; }
.remark-input { flex: 1; font-size: 26px; margin-left: 16px; }
.arrow { color: #94a3b8; font-size: 28px; margin-left: 8px; }
.amount-card { padding: 24px; background: #fff; border-radius: 16px; }
.amount-row { display: flex; justify-content: space-between; padding: 12px 0; font-size: 26px; color: #475569; }
.amount-row.discount { color: #ef4444; }
.amount-row.total { border-top: 1px solid #f1f5f9; margin-top: 12px; padding-top: 20px; font-size: 28px; font-weight: 700; }
.pay-amount { color: #ef4444; font-size: 36px; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; align-items: center; padding: 16px 20px; padding-bottom: calc(16px + env(safe-area-inset-bottom)); background: #fff; border-top: 1px solid #f1f5f9; }
.pay-info { flex: 1; font-size: 26px; }
.pay-price { font-size: 36px; color: #ef4444; font-weight: 700; }
.submit-btn { padding: 18px 48px; background: #ef4444; color: #fff; border-radius: 36px; font-size: 28px; font-weight: 600; }
.picker-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 100; display: flex; align-items: flex-end; }
.picker-panel { width: 100%; background: #fff; border-radius: 24px 24px 0 0; padding: 24px; padding-bottom: calc(24px + env(safe-area-inset-bottom)); }
.picker-title { font-size: 32px; font-weight: 700; text-align: center; margin-bottom: 24px; }
.picker-empty { text-align: center; color: #94a3b8; padding: 40px 0; }
.picker-item { display: flex; justify-content: space-between; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; font-size: 28px; }
.picker-check { color: #2563eb; font-weight: 700; }
.picker-btn { text-align: center; padding: 20px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; margin-top: 24px; }
</style>
