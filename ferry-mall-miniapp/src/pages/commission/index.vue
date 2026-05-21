<template>
  <view class="commission-page">
    <view class="header">
      <view class="amount">¥{{ (promoter?.availableCommissionCent || 0) / 100 }}</view>
      <view class="label">可提现佣金</view>
      <view class="sub" v-if="promoter">累计佣金 ¥{{ (promoter.totalCommissionCent || 0) / 100 }}</view>
    </view>
    <view class="menu-card">
      <view class="menu-item" @tap="goWithdraw">
        <text>佣金提现</text>
        <text class="arrow">&gt;</text>
      </view>
      <view class="menu-item" @tap="goRecords">
        <text>佣金明细</text>
        <text class="arrow">&gt;</text>
      </view>
      <view class="menu-item" @tap="goTeam">
        <text>我的团队</text>
        <text class="arrow">&gt;</text>
      </view>
    </view>
    <view v-if="!promoter" class="apply-btn" @tap="onApply">申请成为推广员</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref } from 'vue'
import { getCommissionInfo, applyCommission, type CommissionUser } from '@/api/commission'

const promoter = ref<CommissionUser>()

async function fetch() {
  try { promoter.value = await getCommissionInfo() } catch { promoter.value = undefined }
}
onShow(() => fetch())

async function onApply() {
  try {
    promoter.value = await applyCommission()
    Taro.showToast({ title: '申请成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e.message || '申请失败', icon: 'none' })
  }
}

function goWithdraw() { Taro.navigateTo({ url: '/pages/commission/withdraw' }) }
function goRecords() { Taro.navigateTo({ url: '/pages/commission/records' }) }
function goTeam() { Taro.navigateTo({ url: '/pages/commission/team' }) }
</script>

<style scoped>
.commission-page { min-height: 100vh; background: #f8fafc; }
.header { padding: 60px 32px; background: linear-gradient(135deg, #7c3aed, #6d28d9); text-align: center; color: #fff; }
.amount { font-size: 64px; font-weight: 800; }
.label { font-size: 26px; opacity: 0.85; margin-top: 12px; }
.sub { font-size: 24px; opacity: 0.7; margin-top: 12px; }
.menu-card { margin: 20px; background: #fff; border-radius: 16px; padding: 0 24px; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; font-size: 28px; }
.menu-item:last-child { border-bottom: 0; }
.arrow { color: #94a3b8; font-size: 28px; }
.apply-btn { margin: 40px 20px; text-align: center; padding: 24px 0; background: #7c3aed; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
