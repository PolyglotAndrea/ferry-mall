<template>
  <view class="detail-page" v-if="order">
    <view class="status-bar">
      <text class="status-text">{{ order.statusText }}</text>
    </view>
    <view class="info-card">
      <view class="info-title">收货信息</view>
      <view>{{ order.receiverName }} {{ order.receiverMobile }}</view>
      <view class="info-muted">{{ order.receiverAddress }}</view>
      <view v-if="order.remark" class="info-muted">备注：{{ order.remark }}</view>
    </view>
    <view class="info-card">
      <view class="info-title">商品信息</view>
      <view v-for="item in order.items" :key="item.spuId" class="goods-row">
        <image :src="item.productImage" class="goods-img" mode="aspectFill" />
        <view class="goods-info">
          <view class="goods-name">{{ item.productName }}</view>
          <view class="goods-price">¥{{ (item.priceCent / 100).toFixed(2) }} x{{ item.quantity }}</view>
        </view>
      </view>
    </view>
    <view class="info-card">
      <view class="amount-row"><text>商品总额</text><text>¥{{ (order.totalAmountCent / 100).toFixed(2) }}</text></view>
      <view class="amount-row"><text>优惠金额</text><text>-¥{{ (order.discountAmountCent / 100).toFixed(2) }}</text></view>
      <view class="amount-row total"><text>实付金额</text><text class="pay">¥{{ (order.payAmountCent / 100).toFixed(2) }}</text></view>
    </view>
    <view class="info-card">
      <view class="info-muted">订单编号：{{ order.orderNo }}</view>
      <view class="info-muted">下单时间：{{ order.createdAt }}</view>
      <view v-if="order.payTime" class="info-muted">支付时间：{{ order.payTime }}</view>
      <view v-if="order.deliveryTime" class="info-muted">发货时间：{{ order.deliveryTime }}</view>
      <view v-if="order.receiveTime" class="info-muted">收货时间：{{ order.receiveTime }}</view>
    </view>
    <view class="bottom-actions">
      <text v-if="order.status === 0" class="btn-primary" @tap="goPay">去支付</text>
      <text v-if="order.status === 0" class="btn-default" @tap="onCancel">取消订单</text>
      <text v-if="order.status === 2" class="btn-primary" @tap="onReceive">确认收货</text>
      <text v-if="order.status === 2" class="btn-default" @tap="goLogistics">查看物流</text>
      <text v-if="order.status >= 1" class="btn-default" @tap="goAftermarket">申请售后</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { getOrderDetail, cancelOrder, confirmReceive, type OrderResp } from '@/api/order'

const order = ref<OrderResp>()

useLoad(async (query) => {
  if (query.orderNo) {
    order.value = await getOrderDetail(query.orderNo as string)
  }
})

function goPay() {
  if (order.value) Taro.navigateTo({ url: `/pages/payment/pay?orderNo=${order.value.orderNo}` })
}
async function onCancel() {
  if (!order.value) return
  const res = await Taro.showModal({ title: '提示', content: '确定取消该订单吗？' })
  if (!res.confirm) return
  try {
    order.value = await cancelOrder(order.value.orderNo)
    Taro.showToast({ title: '已取消', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '取消失败', icon: 'none' })
  }
}
function goLogistics() {
  if (order.value) Taro.navigateTo({ url: `/pages/logistics/trace?logisticsNo=FE${order.value.orderNo}` })
}
function goAftermarket() {
  if (order.value) Taro.navigateTo({ url: `/pages/aftermarket/apply?orderNo=${order.value.orderNo}&orderId=${order.value.id}` })
}
async function onReceive() {
  if (!order.value) return
  const res = await Taro.showModal({ title: '提示', content: '确认已收到商品？' })
  if (!res.confirm) return
  try {
    order.value = await confirmReceive(order.value.orderNo)
    Taro.showToast({ title: '确认成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}
</script>

<style scoped>
.detail-page { padding: 20px; padding-bottom: 140px; min-height: 100vh; background: #f8fafc; }
.status-bar { padding: 40px 24px; background: linear-gradient(135deg, #2563eb, #1d4ed8); border-radius: 16px; margin-bottom: 16px; }
.status-text { font-size: 36px; font-weight: 700; color: #fff; }
.info-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.info-title { font-size: 30px; font-weight: 700; margin-bottom: 16px; }
.info-muted { font-size: 26px; color: #64748b; margin-top: 8px; }
.goods-row { display: flex; gap: 16px; padding: 16px 0; border-bottom: 1px solid #f1f5f9; }
.goods-img { width: 140px; height: 140px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.goods-info { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.goods-name { font-size: 26px; font-weight: 700; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.goods-price { font-size: 24px; color: #64748b; }
.amount-row { display: flex; justify-content: space-between; padding: 10px 0; font-size: 26px; color: #475569; }
.amount-row.total { border-top: 1px solid #f1f5f9; margin-top: 12px; padding-top: 16px; font-weight: 700; }
.amount-row .pay { color: #ef4444; font-size: 32px; }
.bottom-actions { position: fixed; bottom: 0; left: 0; right: 0; display: flex; justify-content: flex-end; gap: 16px; padding: 16px 20px; padding-bottom: calc(16px + env(safe-area-inset-bottom)); background: #fff; border-top: 1px solid #f1f5f9; }
.btn-default { padding: 12px 28px; border: 1px solid #e2e8f0; border-radius: 28px; font-size: 26px; color: #475569; }
.btn-primary { padding: 12px 28px; background: #ef4444; color: #fff; border-radius: 28px; font-size: 26px; }
</style>
