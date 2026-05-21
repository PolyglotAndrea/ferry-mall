<template>
  <view class="order-page">
    <view class="search-bar">
      <input v-model="keyword" class="search-input" placeholder="搜索订单号" confirm-type="search" @confirm="onSearch" />
      <text class="search-btn" @tap="onSearch">搜索</text>
    </view>
    <view class="tabs">
      <view v-for="t in tabs" :key="t.status ?? -1"
        class="tab" :class="{ active: activeStatus === t.status }" @tap="activeStatus = t.status"
      >
        {{ t.label }}
      </view>
    </view>
    <scroll-view
      class="order-list"
      scroll-y
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="orders.length === 0 && !loading" class="empty">
        <text class="empty-icon">&#x1F4ED;</text>
        <text class="empty-text">暂无订单</text>
      </view>
      <view v-for="o in orders" :key="o.id" class="order-card">
        <view class="order-header">
          <text class="order-no">{{ o.orderNo }}</text>
          <text class="order-status" :style="{ color: statusColor(o.status) }">{{ o.statusText }}</text>
        </view>
        <view v-for="item in o.items" :key="`${item.spuId}-${item.skuId ?? 0}`" class="order-goods" @tap="goDetail(o.orderNo)">
          <image :src="item.productImage" class="goods-img" mode="aspectFill" />
          <view class="goods-info">
            <view class="goods-name">{{ item.productName }}</view>
            <view class="goods-price">¥{{ (item.priceCent / 100).toFixed(2) }} x{{ item.quantity }}</view>
          </view>
        </view>
        <view class="order-footer">
          <text>共{{ o.items.length }}件商品 实付 <text class="pay-amount">¥{{ (o.payAmountCent / 100).toFixed(2) }}</text></text>
        </view>
        <view class="order-actions">
          <!-- 待付款(10) -->
          <template v-if="o.status === 10">
            <text class="btn-default" @tap.stop="onCancel(o.orderNo)">取消订单</text>
            <text class="btn-primary" @tap.stop="goPay(o.orderNo)">去支付</text>
          </template>
          <!-- 待发货(20) -->
          <template v-if="o.status === 20">
            <text class="btn-default" @tap.stop="goDetail(o.orderNo)">查看详情</text>
          </template>
          <!-- 待收货(30) -->
          <template v-if="o.status === 30">
            <text class="btn-default" @tap.stop="goLogistics(o)">查看物流</text>
            <text class="btn-primary" @tap.stop="onReceive(o.orderNo)">确认收货</text>
          </template>
          <!-- 已完成(40) -->
          <template v-if="o.status === 40">
            <text class="btn-default" @tap.stop="goDetail(o.orderNo)">查看详情</text>
            <text class="btn-primary" @tap.stop="buyAgain(o)">再次购买</text>
          </template>
          <!-- 已取消(50) -->
          <template v-if="o.status === 50">
            <text class="btn-default" @tap.stop="onDelete(o.orderNo)">删除订单</text>
            <text class="btn-primary" @tap.stop="buyAgain(o)">再次购买</text>
          </template>
        </view>
      </view>
      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="!hasMore && orders.length > 0" class="no-more">没有更多了</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref, watch, onMounted } from 'vue'
import { getOrderPage, cancelOrder, confirmReceive, deleteOrder, type OrderResp } from '@/api/order'

const tabs = [
  { label: '全部', status: undefined },
  { label: '待付款', status: 10 },
  { label: '待发货', status: 20 },
  { label: '待收货', status: 30 },
  { label: '已完成', status: 40 },
]

const activeStatus = ref<number | undefined>(undefined)
const keyword = ref('')
const orders = ref<OrderResp[]>([])
const pageNo = ref(1)
const pageSize = 10
const loading = ref(false)
const hasMore = ref(true)
const refreshing = ref(false)

function statusColor(status: number): string {
  const map: Record<number, string> = {
    10: '#2563eb',
    20: '#f59e0b',
    30: '#8b5cf6',
    40: '#22c55e',
    50: '#94a3b8',
    60: '#ef4444',
    70: '#64748b',
  }
  return map[status] || '#2563eb'
}

async function fetchOrders(reset = false) {
  if (loading.value) return
  loading.value = true
  try {
    if (reset) { pageNo.value = 1; orders.value = []; hasMore.value = true }
    const res = await getOrderPage(activeStatus.value, pageNo.value, pageSize, keyword.value || undefined)
    orders.value.push(...res.list)
    if (res.list.length < pageSize) hasMore.value = false
    else pageNo.value++
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onSearch() {
  fetchOrders(true)
}

function onRefresh() {
  refreshing.value = true
  fetchOrders(true)
}

function loadMore() {
  if (!hasMore.value || loading.value) return
  fetchOrders()
}

watch(activeStatus, () => fetchOrders(true))
onMounted(() => fetchOrders(true))

function goDetail(orderNo: string) {
  Taro.navigateTo({ url: `/pages/order/detail?orderNo=${orderNo}` })
}
function goPay(orderNo: string) {
  Taro.navigateTo({ url: `/pages/payment/pay?orderNo=${orderNo}` })
}
function goLogistics(o: OrderResp) {
  const firstImage = o.items[0]?.productImage || ''
  Taro.navigateTo({
    url: `/pages/logistics/trace?logisticsNo=${encodeURIComponent('FE' + o.orderNo)}&productImage=${encodeURIComponent(firstImage)}`
  })
}
async function onCancel(orderNo: string) {
  const res = await Taro.showModal({ title: '提示', content: '确定取消该订单吗？' })
  if (!res.confirm) return
  try {
    await cancelOrder(orderNo)
    Taro.showToast({ title: '已取消', icon: 'success' })
    fetchOrders(true)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '取消失败', icon: 'none' })
  }
}
async function onReceive(orderNo: string) {
  const res = await Taro.showModal({ title: '提示', content: '确认已收到商品？' })
  if (!res.confirm) return
  try {
    await confirmReceive(orderNo)
    Taro.showToast({ title: '确认成功', icon: 'success' })
    fetchOrders(true)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}
async function onDelete(orderNo: string) {
  const res = await Taro.showModal({ title: '提示', content: '确定删除该订单吗？删除后不可恢复' })
  if (!res.confirm) return
  try {
    await deleteOrder(orderNo)
    Taro.showToast({ title: '已删除', icon: 'success' })
    fetchOrders(true)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '删除失败', icon: 'none' })
  }
}
function buyAgain(o: OrderResp) {
  if (!o.items.length) return
  const items = o.items.map(i => ({
    spuId: Number(i.spuId),
    skuId: i.skuId ? Number(i.skuId) : undefined,
    name: i.productName,
    coverUrl: i.productImage,
    priceCent: i.priceCent,
    quantity: i.quantity
  }))
  Taro.navigateTo({
    url: `/pages/order/confirm?items=${encodeURIComponent(JSON.stringify(items))}`
  })
}
</script>

<style scoped>
.order-page { height: 100vh; display: flex; flex-direction: column; }
.search-bar { display: flex; align-items: center; gap: 16px; padding: 16px 20px; background: #fff; border-bottom: 1px solid #f1f5f9; }
.search-input { flex: 1; height: 64px; background: #f1f5f9; border-radius: 32px; padding: 0 28px; font-size: 28px; }
.search-btn { color: #2563eb; font-size: 28px; font-weight: 600; }
.tabs { display: flex; background: #fff; border-bottom: 1px solid #f1f5f9; }
.tab { flex: 1; text-align: center; padding: 24px 0; font-size: 28px; color: #64748b; }
.tab.active { color: #2563eb; font-weight: 700; border-bottom: 4px solid #2563eb; }
.order-list { flex: 1; padding: 16px 20px; background: #f8fafc; }
.empty { text-align: center; padding: 120px 0; }
.empty-icon { font-size: 80px; display: block; margin-bottom: 16px; }
.empty-text { font-size: 28px; color: #94a3b8; }
.order-card { padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.order-no { font-size: 24px; color: #94a3b8; }
.order-status { font-size: 26px; font-weight: 600; }
.order-goods { display: flex; gap: 16px; padding: 16px 0; border-bottom: 1px solid #f1f5f9; }
.goods-img { width: 140px; height: 140px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.goods-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.goods-name { font-size: 26px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.goods-price { font-size: 24px; color: #64748b; }
.order-footer { text-align: right; padding: 16px 0; font-size: 26px; color: #475569; }
.pay-amount { color: #ef4444; font-weight: 700; }
.order-actions { display: flex; justify-content: flex-end; gap: 16px; }
.btn-default { padding: 10px 24px; border: 1px solid #e2e8f0; border-radius: 28px; font-size: 24px; color: #475569; }
.btn-primary { padding: 10px 24px; background: #ef4444; color: #fff; border-radius: 28px; font-size: 24px; }
.loading { text-align: center; color: #94a3b8; padding: 24px 0; }
.no-more { text-align: center; color: #94a3b8; padding: 24px 0; font-size: 24px; }
</style>
