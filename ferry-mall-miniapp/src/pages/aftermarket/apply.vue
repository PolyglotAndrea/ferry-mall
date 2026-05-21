<template>
  <view class="apply-page">
    <view class="tip-card">
      <text class="tip-text">请选择售后原因，提交后商家将尽快处理</text>
    </view>
    <view class="form-card">
      <view class="form-row">
        <text class="label">订单号</text>
        <text class="value">{{ orderNo }}</text>
      </view>
      <view class="form-row">
        <text class="label">售后原因</text>
        <picker mode="selector" :range="reasons" :value="reasonIdx" @change="reasonIdx = $event.detail.value"
          class="picker">
          <text>{{ reasons[reasonIdx] }}</text>
        </picker>
      </view>
      <view class="form-row">
        <text class="label">详细说明</text>
      </view>
      <textarea v-model="detailReason" class="reason-area" placeholder="请详细描述问题，便于商家快速处理" maxlength="200" />
      <view class="char-count">{{ detailReason.length }}/200</view>
    </view>
    <view class="submit-btn" @tap="onSubmit">提交售后申请</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { applyAftermarket } from '@/api/aftermarket'

const orderNo = ref('')
const orderId = ref(0)
const reasons = ['七天无理由退货', '商品质量问题', '商品破损/少件', '与描述不符', '其他原因']
const reasonIdx = ref(0)
const detailReason = ref('')

useLoad((query) => {
  orderNo.value = (query.orderNo as string) || ''
  orderId.value = Number(query.orderId || 0)
})

async function onSubmit() {
  if (!orderId.value) {
    Taro.showToast({ title: '订单信息缺失', icon: 'none' })
    return
  }
  const reason = `${reasons[reasonIdx.value]}${detailReason.value ? '：' + detailReason.value : ''}`
  try {
    await applyAftermarket(orderId.value, reason)
    Taro.showToast({ title: '售后申请已提交', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '提交失败', icon: 'none' })
  }
}
</script>

<style scoped>
.apply-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.tip-card { padding: 20px 24px; background: #fef3c7; border-radius: 12px; margin-bottom: 20px; }
.tip-text { font-size: 26px; color: #92400e; }
.form-card { padding: 24px; background: #fff; border-radius: 16px; }
.form-row { display: flex; align-items: center; padding: 20px 0; border-bottom: 1px solid #f1f5f9; }
.form-row .label { font-size: 28px; font-weight: 600; width: 160px; flex-shrink: 0; }
.form-row .value { flex: 1; font-size: 26px; color: #475569; }
.picker { flex: 1; font-size: 26px; color: #2563eb; }
.reason-area { width: 100%; height: 200px; background: #f8fafc; border-radius: 12px; padding: 20px; margin-top: 16px; font-size: 26px; }
.char-count { text-align: right; font-size: 22px; color: #94a3b8; margin-top: 8px; }
.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
