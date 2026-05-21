<template>
  <view class="apply-page">
    <view class="tip-card">
      <text class="tip-text">填写以下信息申请成为商家，审核通过后即可开店</text>
    </view>
    <view class="form-card">
      <view class="form-row">
        <text class="label">商家名称</text>
        <input v-model="form.name" placeholder="请输入商家名称" />
      </view>
      <view class="form-row">
        <text class="label">联系人</text>
        <input v-model="form.contactName" placeholder="请输入联系人姓名" />
      </view>
      <view class="form-row">
        <text class="label">联系电话</text>
        <input v-model="form.contactMobile" placeholder="请输入手机号" type="number" maxlength="11" />
      </view>
      <view class="form-row">
        <text class="label">营业执照</text>
        <input v-model="form.licenseNo" placeholder="选填" />
      </view>
    </view>
    <view class="submit-btn" @tap="onSubmit">提交入驻申请</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { reactive } from 'vue'
import { applyMerchant } from '@/api/merchant'

const form = reactive({ name: '', contactName: '', contactMobile: '', licenseNo: '' })

async function onSubmit() {
  if (!form.name || !form.contactName || !form.contactMobile) {
    Taro.showToast({ title: '请填写必填项', icon: 'none' })
    return
  }
  try {
    await applyMerchant(form)
    Taro.showToast({ title: '申请已提交，请等待审核', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '提交失败', icon: 'none' })
  }
}
</script>

<style scoped>
.apply-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.tip-card { padding: 20px 24px; background: #dbeafe; border-radius: 12px; margin-bottom: 20px; }
.tip-text { font-size: 26px; color: #1e40af; }
.form-card { padding: 0 24px; background: #fff; border-radius: 16px; }
.form-row { display: flex; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; }
.form-row:last-child { border-bottom: 0; }
.label { font-size: 28px; font-weight: 600; width: 160px; flex-shrink: 0; }
input { flex: 1; font-size: 28px; }
.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #2563eb; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
