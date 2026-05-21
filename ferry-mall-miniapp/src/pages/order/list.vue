<template>
  <view class="order-page">
    <view class="tabs">
      <view v-for="t in tabs" :key="t.status ?? -1"
        class="tab" :class="{ active: activeStatus === t.status }" @tap="activeStatus = t.status"
      >
        {{ t.label }}
      </view>
    </view>
    <scroll-view class="order-list" scroll-y @scrolltolower="loadMore">
      <view v-if="orders.length === 0" class="empty">暂无订单</view>
      <view v-for="o in orders" :key="o.id" class="order-card">
        <view class="order-header">
          <text class="order-no">{{ o.orderNo }}</text>
          <text class="order-status">{{ o.statusText }}</text>
        </view>
        <view v-for="item in o.items" :key="item.spuId" class="order-goods" @tap="goDetail(o.orderNo)">
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
          <text v-if="o.status === 0" class="btn-primary" @tap="goPay(o.orderNo)">去支付</text>
          <text v-if="o.status === 0" class="btn-default" @tap="onCancel(o.orderNo)">取消订单</text>
          <text v-if="o.status === 2" class="btn-primary" @tap="onReceive(o.orderNo)">确认收货</text>
          <text class="btn-default" @tap="goDetail(o.orderNo)">查看详情</text>
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
import { getOrderPage, cancelOrder, confirmReceive, type OrderResp } from '@/api/order'

const tabs = [
  { label: '全部', status: undefined },
  { label: '待付款', status: 0 },
  { label: '待发货', status: 1 },
  { label: '待收货', status: 2 },
  { label: '已完成', status: 3 },
]

const activeStatus = ref<number | undefined>(undefined)
const orders = ref<OrderResp[]>([])
const pageNo = ref(1)
const pageSize = 10
const loading = ref(false)
const hasMore = ref(true)

async function fetchOrders(reset = false) {
  if (loading.value) return
  loading.value = true
  try {
    if (reset) { pageNo.value = 1; orders.value = []; hasMore.value = true }
    const res = await getOrderPage(activeStatus.value, pageNo.value, pageSize)
    orders.value.push(...res.list)
    if (res.list.length < pageSize) hasMore.value = false
    else pageNo.value++
  } finally {
    loading.value = false
  }
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
</script>

<style scoped>
.order-page { height: 100vh; display: flex; flex-direction: column; }
.tabs { display: flex; background: #fff; border-bottom: 1px solid #f1f5f9; }
.tab { flex: 1; text-align: center; padding: 24px 0; font-size: 28px; color: #64748b; }
.tab.active { color: #2563eb; font-weight: 700; border-bottom: 4px solid #2563eb; }
.order-list { flex: 1; padding: 16px 20px; background: #f8fafc; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; font-size: 28px; }
.order-card { padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.order-no { font-size: 24px; color: #94a3b8; }
.order-status { font-size: 26px; color: #2563eb; font-weight: 600; }
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
