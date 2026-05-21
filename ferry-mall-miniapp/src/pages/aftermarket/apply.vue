<template>
  <view class="apply-page">
    <!-- 提示 -->
    <view class="tip-card">
      <text class="tip-text">请选择售后类型并填写原因，提交后商家将尽快处理</text>
    </view>

    <!-- 订单信息 -->
    <view v-if="order" class="info-card">
      <view class="info-title">订单信息</view>
      <view class="info-row">
        <text class="info-label">订单编号</text>
        <text class="info-value">{{ order.orderNo }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">实付金额</text>
        <text class="info-value price">¥{{ (order.payAmountCent / 100).toFixed(2) }}</text>
      </view>
    </view>

    <!-- 售后表单 -->
    <view class="form-card">
      <!-- 售后类型 -->
      <view class="form-row">
        <text class="label required">售后类型</text>
        <picker mode="selector" :range="typeOptions" :value="typeIdx" @change="typeIdx = $event.detail.value" class="picker">
          <text :class="{ placeholder: typeIdx < 0 }">{{ typeIdx >= 0 ? typeOptions[typeIdx] : '请选择' }}</text>
        </picker>
        <text class="arrow">&gt;</text>
      </view>

      <!-- 售后原因 -->
      <view class="form-row">
        <text class="label required">售后原因</text>
        <picker mode="selector" :range="reasonOptions" :value="reasonIdx" @change="reasonIdx = $event.detail.value" class="picker">
          <text :class="{ placeholder: reasonIdx < 0 }">{{ reasonIdx >= 0 ? reasonOptions[reasonIdx] : '请选择' }}</text>
        </picker>
        <text class="arrow">&gt;</text>
      </view>

      <!-- 退款金额 -->
      <view class="form-row">
        <text class="label required">退款金额</text>
        <view class="amount-wrap">
          <text class="currency">¥</text>
          <input
            v-model="refundAmount"
            type="digit"
            placeholder="0.00"
            class="amount-input"
            @blur="onAmountBlur"
          />
        </view>
      </view>
      <view v-if="order" class="amount-tip">最多可退 ¥{{ (order.payAmountCent / 100).toFixed(2) }}</view>

      <!-- 问题描述 -->
      <view class="form-row block">
        <text class="label">问题描述</text>
      </view>
      <textarea
        v-model="description"
        class="desc-area"
        placeholder="请详细描述遇到的问题，便于商家快速处理（选填）"
        maxlength="300"
      />
      <view class="char-count">{{ description.length }}/300</view>

      <!-- 图片上传 -->
      <view class="form-row block">
        <text class="label">上传凭证</text>
        <text class="sub-label">（最多6张）</text>
      </view>
      <view class="image-list">
        <view v-for="(img, i) in images" :key="i" class="image-item">
          <image :src="img" class="upload-img" mode="aspectFill" @tap="previewImage(i)" />
          <text class="delete-icon" @tap.stop="removeImage(i)">&#x2715;</text>
        </view>
        <view v-if="images.length < 6" class="image-item add" @tap="chooseImage">
          <text class="add-icon">+</text>
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>

    <view class="submit-btn" @tap="onSubmit">提交售后申请</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { applyAftermarket, type AftermarketApplyReq } from '@/api/aftermarket'
import { getOrderDetail, type OrderResp } from '@/api/order'
import { uploadFile } from '@/utils/request'

const order = ref<OrderResp | null>(null)
const orderId = ref(0)

const typeOptions = ['仅退款', '退货退款', '换货']
const typeIdx = ref(-1)

const reasonOptions = ['七天无理由退货', '商品质量问题', '商品破损/少件', '与描述不符', '发错货', '其他原因']
const reasonIdx = ref(-1)

const refundAmount = ref('')
const description = ref('')
const images = ref<string[]>([])

const maxRefundAmount = computed(() => {
  if (!order.value) return 0
  return order.value.payAmountCent / 100
})

useLoad(async (query) => {
  orderId.value = Number(query.orderId || 0)
  const orderNo = (query.orderNo as string) || ''

  if (!orderId.value && !orderNo) {
    Taro.showToast({ title: '订单信息缺失', icon: 'none' })
    return
  }

  try {
    if (orderNo) {
      order.value = await getOrderDetail(orderNo)
      orderId.value = order.value.id
    } else if (orderId.value) {
      // 尝试通过 orderId 获取详情，若后端不支持则留空
      try {
        // 部分后端可能支持 id 查询，这里尝试拼接
        order.value = await getOrderDetail(String(orderId.value))
      } catch {
        order.value = null
      }
    }
  } catch (e: any) {
    Taro.showToast({ title: e.message || '加载订单失败', icon: 'none' })
  }
})

function onAmountBlur() {
  const val = parseFloat(refundAmount.value)
  if (isNaN(val) || val < 0) {
    refundAmount.value = ''
    return
  }
  const max = maxRefundAmount.value
  if (max > 0 && val > max) {
    refundAmount.value = max.toFixed(2)
    Taro.showToast({ title: `退款金额不能超过 ¥${max.toFixed(2)}`, icon: 'none' })
  } else {
    refundAmount.value = val.toFixed(2)
  }
}

function chooseImage() {
  const remain = 6 - images.value.length
  if (remain <= 0) return
  Taro.chooseImage({
    count: remain,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera']
  }).then(async (res) => {
    for (const path of res.tempFilePaths) {
      try {
        const url = await uploadFile(path)
        images.value.push(url)
      } catch (e: any) {
        Taro.showToast({ title: '上传失败', icon: 'none' })
      }
    }
  })
}

function removeImage(index: number) {
  images.value.splice(index, 1)
}

function previewImage(index: number) {
  Taro.previewImage({
    current: images.value[index],
    urls: images.value
  })
}

function buildReason(): string {
  const type = typeIdx.value >= 0 ? typeOptions[typeIdx.value] : ''
  const reason = reasonIdx.value >= 0 ? reasonOptions[reasonIdx.value] : ''
  const parts: string[] = []
  if (type) parts.push(`【${type}】`)
  if (reason) parts.push(reason)
  if (description.value) parts.push(`描述：${description.value}`)
  if (refundAmount.value) parts.push(`退款金额：¥${refundAmount.value}`)
  if (images.value.length) parts.push(`凭证：${images.value.join(',')}`)
  return parts.join(' | ')
}

async function onSubmit() {
  if (!orderId.value) {
    Taro.showToast({ title: '订单信息缺失', icon: 'none' })
    return
  }
  if (typeIdx.value < 0) {
    Taro.showToast({ title: '请选择售后类型', icon: 'none' })
    return
  }
  if (reasonIdx.value < 0) {
    Taro.showToast({ title: '请选择售后原因', icon: 'none' })
    return
  }
  const amount = parseFloat(refundAmount.value)
  if (isNaN(amount) || amount <= 0) {
    Taro.showToast({ title: '请填写退款金额', icon: 'none' })
    return
  }
  const max = maxRefundAmount.value
  if (max > 0 && amount > max + 0.001) {
    Taro.showToast({ title: `退款金额不能超过 ¥${max.toFixed(2)}`, icon: 'none' })
    return
  }

  const payload: AftermarketApplyReq = {
    orderId: orderId.value,
    reason: buildReason()
  }

  try {
    await applyAftermarket(payload)
    Taro.showToast({ title: '售后申请已提交', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '提交失败', icon: 'none' })
  }
}
</script>

<style scoped>
.apply-page { padding: 20px; padding-bottom: 40px; min-height: 100vh; background: #f8fafc; }

.tip-card { padding: 20px 24px; background: #fef3c7; border-radius: 12px; margin-bottom: 20px; }
.tip-text { font-size: 26px; color: #92400e; }

.info-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 20px; }
.info-title { font-size: 30px; font-weight: 700; margin-bottom: 16px; color: #1e293b; }
.info-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 26px; }
.info-label { color: #64748b; }
.info-value { color: #1e293b; }
.info-value.price { color: #ef4444; font-weight: 700; }

.form-card { padding: 0 24px; background: #fff; border-radius: 16px; margin-bottom: 20px; }
.form-row { display: flex; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; }
.form-row:last-child { border-bottom: 0; }
.form-row.block { display: block; padding-bottom: 12px; border-bottom: 0; }

.label { font-size: 28px; font-weight: 600; width: 180px; flex-shrink: 0; color: #1e293b; }
.label.required::before { content: '* '; color: #ef4444; }

.picker { flex: 1; font-size: 28px; color: #1e293b; text-align: right; }
.picker .placeholder { color: #94a3b8; }
.arrow { color: #94a3b8; font-size: 28px; margin-left: 8px; }

.amount-wrap { flex: 1; display: flex; align-items: center; justify-content: flex-end; }
.currency { font-size: 28px; color: #ef4444; margin-right: 4px; }
.amount-input { width: 160px; font-size: 28px; color: #ef4444; text-align: right; }
.amount-tip { font-size: 24px; color: #94a3b8; padding: 0 24px 16px; text-align: right; }

.desc-area { width: 100%; height: 200px; background: #f8fafc; border-radius: 12px; padding: 20px; font-size: 26px; color: #1e293b; }
.char-count { text-align: right; font-size: 22px; color: #94a3b8; padding: 8px 0 16px; }

.sub-label { font-size: 24px; color: #94a3b8; margin-left: 8px; font-weight: normal; }

.image-list { display: flex; flex-wrap: wrap; gap: 16px; padding-bottom: 24px; }
.image-item { position: relative; width: 160px; height: 160px; border-radius: 12px; overflow: hidden; background: #f1f5f9; }
.upload-img { width: 100%; height: 100%; }
.delete-icon { position: absolute; top: 4px; right: 4px; width: 36px; height: 36px; background: rgba(0,0,0,0.5); color: #fff; font-size: 20px; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
.image-item.add { display: flex; flex-direction: column; align-items: center; justify-content: center; border: 2px dashed #cbd5e1; background: #fff; }
.add-icon { font-size: 48px; color: #94a3b8; line-height: 1; }
.add-text { font-size: 22px; color: #94a3b8; margin-top: 4px; }

.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
