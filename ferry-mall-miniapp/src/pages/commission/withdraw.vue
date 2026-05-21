<template>
  <view class="withdraw-page">
    <view class="header">
      <view class="label">可提现金额</view>
      <view class="amount">¥{{ (promoter?.availableCommissionCent || 0) / 100 }}</view>
    </view>
    <view class="form-card">
      <view class="form-row">
        <text class="label">提现金额</text>
        <input v-model="amount" type="digit" placeholder="请输入提现金额" />
      </view>
    </view>
    <view class="submit-btn" @tap="onSubmit">申请提现</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { ref } from 'vue'
import { getCommissionInfo, type CommissionUser } from '@/api/commission'

const promoter = ref<CommissionUser>()
const amount = ref('')

async function fetch() {
  try { promoter.value = await getCommissionInfo() } catch {}
}
fetch()

function onSubmit() {
  const amt = parseFloat(amount.value)
  if (!amt || amt <= 0) {
    Taro.showToast({ title: '请输入有效金额', icon: 'none' })
    return
  }
  Taro.showToast({ title: '提现申请已提交', icon: 'success' })
  amount.value = ''
}
</script>

<style scoped>
.withdraw-page { min-height: 100vh; background: #f8fafc; }
.header { padding: 60px 32px; background: linear-gradient(135deg, #7c3aed, #6d28d9); text-align: center; color: #fff; }
.label { font-size: 26px; opacity: 0.85; }
.amount { font-size: 64px; font-weight: 800; margin-top: 12px; }
.form-card { margin: 20px; background: #fff; border-radius: 16px; padding: 0 24px; }
.form-row { display: flex; align-items: center; padding: 24px 0; }
.form-row .label { font-size: 28px; font-weight: 600; width: 160px; flex-shrink: 0; }
input { flex: 1; font-size: 28px; }
.submit-btn { margin: 40px 20px; text-align: center; padding: 24px 0; background: #7c3aed; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
