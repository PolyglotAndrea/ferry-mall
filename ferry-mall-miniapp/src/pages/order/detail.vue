<template>
  <view class="detail-page" v-if="order">
    <!-- 状态栏 -->
    <view class="status-bar" :style="{ background: statusColor }">
      <text class="status-icon">{{ statusIcon }}</text>
      <text class="status-text">{{ order.statusText }}</text>
      <text class="status-desc">{{ statusDesc }}</text>
    </view>

    <!-- 物流入口（已发货/已收货时展示） -->
    <view v-if="order.status >= 30" class="logistics-card" @tap="goLogistics">
      <view class="logistics-left">
        <text class="logistics-icon">&#x1F69A;</text>
        <view class="logistics-info">
          <view class="logistics-status">{{ logisticsStatusText }}</view>
          <view class="logistics-no">运单号：{{ logisticsNo }}</view>
        </view>
      </view>
      <text class="arrow">&gt;</text>
    </view>

    <!-- 收货地址 -->
    <view class="info-card address-card">
      <view class="address-header">
        <text class="address-label">&#x1F4CD; 收货地址</text>
      </view>
      <view class="address-body">
        <view class="addr-row">
          <text class="addr-name">{{ order.receiverName }}</text>
          <text class="addr-mobile">{{ maskMobile(order.receiverMobile) }}</text>
        </view>
        <view class="addr-detail">{{ order.receiverAddress }}</view>
      </view>
    </view>

    <!-- 商品信息 -->
    <view class="info-card goods-card">
      <view class="card-title">商品信息</view>
      <view v-for="item in order.items" :key="`${item.spuId}-${item.skuId ?? 0}`" class="goods-row">
        <image :src="item.productImage" class="goods-img" mode="aspectFill" />
        <view class="goods-info">
          <view class="goods-name">{{ item.productName }}</view>
          <view class="goods-sku" v-if="item.skuId">规格: {{ item.skuId }}</view>
          <view class="goods-bottom">
            <text class="goods-price">¥{{ (item.priceCent / 100).toFixed(2) }}</text>
            <text class="goods-qty">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 金额明细 -->
    <view class="info-card amount-card">
      <view class="amount-row">
        <text>商品总额</text>
        <text>¥{{ (order.totalAmountCent / 100).toFixed(2) }}</text>
      </view>
      <view class="amount-row">
        <text>运费</text>
        <text class="free">免运费</text>
      </view>
      <view class="amount-row discount" v-if="order.discountAmountCent > 0">
        <text>优惠金额</text>
        <text>-¥{{ (order.discountAmountCent / 100).toFixed(2) }}</text>
      </view>
      <view class="amount-divider"></view>
      <view class="amount-row total">
        <text>实付金额</text>
        <text class="pay">¥{{ (order.payAmountCent / 100).toFixed(2) }}</text>
      </view>
    </view>

    <!-- 订单信息 -->
    <view class="info-card meta-card">
      <view class="card-title">订单信息</view>
      <view class="meta-row">
        <text class="meta-label">订单编号</text>
        <text class="meta-value">{{ order.orderNo }}</text>
        <text class="meta-copy" @tap="copyOrderNo">复制</text>
      </view>
      <view class="meta-row">
        <text class="meta-label">创建时间</text>
        <text class="meta-value">{{ order.createdAt }}</text>
      </view>
      <view class="meta-row" v-if="order.payTime">
        <text class="meta-label">支付时间</text>
        <text class="meta-value">{{ order.payTime }}</text>
      </view>
      <view class="meta-row" v-if="order.deliveryTime">
        <text class="meta-label">发货时间</text>
        <text class="meta-value">{{ order.deliveryTime }}</text>
      </view>
      <view class="meta-row" v-if="order.receiveTime">
        <text class="meta-label">收货时间</text>
        <text class="meta-value">{{ order.receiveTime }}</text>
      </view>
      <view class="meta-row" v-if="order.cancelTime">
        <text class="meta-label">取消时间</text>
        <text class="meta-value">{{ order.cancelTime }}</text>
      </view>
      <view class="meta-row" v-if="order.cancelReason">
        <text class="meta-label">取消原因</text>
        <text class="meta-value">{{ order.cancelReason }}</text>
      </view>
      <view class="meta-row" v-if="order.remark">
        <text class="meta-label">订单备注</text>
        <text class="meta-value">{{ order.remark }}</text>
      </view>
    </view>

    <!-- 底部占位 -->
    <view class="bottom-placeholder"></view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <!-- 待付款(10) -->
      <template v-if="order.status === 10">
        <text class="btn-default" @tap="onCancel">取消订单</text>
        <text class="btn-primary" @tap="goPay">去支付</text>
      </template>

      <!-- 待发货(20) -->
      <template v-if="order.status === 20">
        <text class="btn-default" @tap="goAftermarket">申请售后</text>
      </template>

      <!-- 待收货(30) -->
      <template v-if="order.status === 30">
        <text class="btn-default" @tap="goLogistics">查看物流</text>
        <text class="btn-default" @tap="goAftermarket">申请售后</text>
        <text class="btn-primary" @tap="onReceive">确认收货</text>
      </template>

      <!-- 已完成(40) -->
      <template v-if="order.status === 40">
        <text class="btn-default" @tap="goAftermarket">申请售后</text>
        <text class="btn-primary" @tap="buyAgain">再次购买</text>
      </template>

      <!-- 已取消(50) -->
      <template v-if="order.status === 50">
        <text class="btn-default" @tap="onDelete">删除订单</text>
        <text class="btn-primary" @tap="buyAgain">再次购买</text>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { getOrderDetail, cancelOrder, confirmReceive, deleteOrder, type OrderResp } from '@/api/order'

const order = ref<OrderResp>()

const statusColor = computed(() => {
  const map: Record<number, string> = {
    10: '#2563eb',   // 待付款 - 蓝
    20: '#f59e0b',   // 待发货 - 橙
    30: '#8b5cf6',   // 待收货 - 紫
    40: '#22c55e',   // 已完成 - 绿
    50: '#94a3b8',   // 已取消 - 灰
    60: '#ef4444',   // 退款中 - 红
    70: '#64748b',   // 已退款 - 深灰
  }
  return map[order.value?.status ?? 10] || '#2563eb'
})

const statusIcon = computed(() => {
  const map: Record<number, string> = {
    10: '&#x23F3;',
    20: '&#x1F4E6;',
    30: '&#x1F69A;',
    40: '&#x2713;',
    50: '&#x2715;',
    60: '&#x21A9;',
    70: '&#x2713;',
  }
  return map[order.value?.status ?? 10] || '&#x23F3;'
})

const statusDesc = computed(() => {
  const map: Record<number, string> = {
    10: '请在30分钟内完成支付，超时订单将自动取消',
    20: '商家正在备货中，请耐心等待',
    30: '商品运输中，请注意查收',
    40: '交易已完成，感谢您的购买',
    50: '订单已取消',
    60: '退款处理中',
    70: '退款已完成',
  }
  return map[order.value?.status ?? 10] || ''
})

const logisticsNo = computed(() => {
  if (!order.value) return ''
  return `FE${order.value.orderNo}`
})

const logisticsStatusText = computed(() => {
  if (!order.value) return ''
  if (order.value.status === 40) return '已签收'
  if (order.value.status === 30) return '运输中'
  return '已发货'
})

useLoad(async (query) => {
  if (query.orderNo) {
    try {
      order.value = await getOrderDetail(query.orderNo as string)
    } catch (e: any) {
      Taro.showToast({ title: e.message || '加载失败', icon: 'none' })
    }
  }
})

function maskMobile(mobile: string): string {
  if (!mobile || mobile.length !== 11) return mobile
  return mobile.slice(0, 3) + '****' + mobile.slice(7)
}

function copyOrderNo() {
  if (!order.value) return
  Taro.setClipboardData({
    data: order.value.orderNo,
    success: () => Taro.showToast({ title: '已复制', icon: 'success' })
  })
}

function goPay() {
  if (order.value) {
    Taro.navigateTo({ url: `/pages/payment/pay?orderNo=${order.value.orderNo}` })
  }
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
  if (!order.value) return
  const firstImage = order.value.items[0]?.productImage || ''
  Taro.navigateTo({
    url: `/pages/logistics/trace?logisticsNo=${encodeURIComponent(logisticsNo.value)}&productImage=${encodeURIComponent(firstImage)}`
  })
}

function goAftermarket() {
  if (order.value) {
    Taro.navigateTo({ url: `/pages/aftermarket/apply?orderNo=${order.value.orderNo}&orderId=${order.value.id}` })
  }
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

async function onDelete() {
  if (!order.value) return
  const res = await Taro.showModal({ title: '提示', content: '确定删除该订单吗？删除后不可恢复' })
  if (!res.confirm) return
  try {
    await deleteOrder(order.value.orderNo)
    Taro.showToast({ title: '已删除', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '删除失败', icon: 'none' })
  }
}

function buyAgain() {
  if (!order.value || !order.value.items.length) return
  const items = order.value.items.map(i => ({
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
.detail-page {
  padding: 20px;
  padding-bottom: 160px;
  min-height: 100vh;
  background: #f8fafc;
}

.status-bar {
  padding: 40px 24px;
  border-radius: 16px;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.status-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.status-text {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
}

.status-desc {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 8px;
}

/* 物流入口卡片 */
.logistics-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  margin-bottom: 16px;
}

.logistics-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logistics-icon {
  font-size: 40px;
}

.logistics-status {
  font-size: 28px;
  font-weight: 700;
  color: #2563eb;
}

.logistics-no {
  font-size: 24px;
  color: #64748b;
  margin-top: 6px;
}

.arrow {
  color: #94a3b8;
  font-size: 28px;
}

.info-card {
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #1e293b;
}

.address-header {
  margin-bottom: 16px;
}

.address-label {
  font-size: 28px;
  font-weight: 600;
  color: #1e293b;
}

.addr-row {
  display: flex;
  align-items: center;
  gap: 20px;
}

.addr-name {
  font-size: 30px;
  font-weight: 700;
  color: #1e293b;
}

.addr-mobile {
  font-size: 26px;
  color: #64748b;
}

.addr-detail {
  font-size: 26px;
  color: #475569;
  margin-top: 12px;
  line-height: 1.5;
}

.goods-row {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f1f5f9;
}

.goods-row:last-child {
  border-bottom: none;
}

.goods-img {
  width: 140px;
  height: 140px;
  border-radius: 12px;
  background: #f1f5f9;
  flex-shrink: 0;
}

.goods-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 26px;
  font-weight: 700;
  line-height: 1.3;
  color: #1e293b;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-sku {
  font-size: 22px;
  color: #94a3b8;
  background: #f1f5f9;
  display: inline-block;
  padding: 4px 12px;
  border-radius: 6px;
  margin-top: 8px;
  align-self: flex-start;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.goods-price {
  font-size: 28px;
  color: #ef4444;
  font-weight: 700;
}

.goods-qty {
  font-size: 24px;
  color: #64748b;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 26px;
  color: #475569;
}

.amount-row.discount {
  color: #ef4444;
}

.amount-row .free {
  color: #22c55e;
}

.amount-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 12px 0;
}

.amount-row.total {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
}

.amount-row .pay {
  color: #ef4444;
  font-size: 32px;
}

.meta-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  font-size: 26px;
}

.meta-label {
  color: #94a3b8;
  width: 140px;
  flex-shrink: 0;
}

.meta-value {
  color: #475569;
  flex: 1;
  word-break: break-all;
}

.meta-copy {
  color: #2563eb;
  font-size: 24px;
  margin-left: 12px;
  padding: 4px 12px;
  border: 1px solid #2563eb;
  border-radius: 8px;
}

.bottom-placeholder {
  height: 120px;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 16px 20px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1px solid #f1f5f9;
}

.btn-default {
  padding: 12px 28px;
  border: 1px solid #e2e8f0;
  border-radius: 28px;
  font-size: 26px;
  color: #475569;
  background: #fff;
}

.btn-primary {
  padding: 12px 28px;
  background: #ef4444;
  color: #fff;
  border-radius: 28px;
  font-size: 26px;
}
</style>
