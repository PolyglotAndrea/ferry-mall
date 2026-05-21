<template>
  <view class="edit-page">
    <view class="form-card">
      <view class="form-row">
        <text class="label">收货人</text>
        <input v-model="form.name" placeholder="请输入收货人姓名" />
      </view>
      <view class="form-row">
        <text class="label">手机号</text>
        <input v-model="form.mobile" placeholder="请输入手机号" type="number" maxlength="11" />
      </view>
      <view class="form-row">
        <text class="label">省</text>
        <input v-model="form.province" placeholder="请输入省份" />
      </view>
      <view class="form-row">
        <text class="label">市</text>
        <input v-model="form.city" placeholder="请输入城市" />
      </view>
      <view class="form-row">
        <text class="label">区</text>
        <input v-model="form.district" placeholder="请输入区县" />
      </view>
      <view class="form-row">
        <text class="label">详细地址</text>
        <textarea v-model="form.detail" placeholder="请输入详细地址" auto-height />
      </view>
      <view class="form-row">
        <text class="label">设为默认</text>
        <switch :checked="form.isDefault === 1" @change="form.isDefault = $event.detail.value ? 1 : 0" />
      </view>
    </view>
    <view class="submit-btn" @tap="onSubmit">保存</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { addAddress, updateAddress, getAddressList, type AddressItem } from '@/api/address'

const form = ref({ name: '', mobile: '', province: '', city: '', district: '', detail: '', isDefault: 0 })
const editId = ref<number>(0)

useLoad(async (query) => {
  if (query.id) {
    editId.value = Number(query.id)
    try {
      const list = await getAddressList()
      const item = list.find(a => a.id === editId.value)
      if (item) {
        form.value = { name: item.name, mobile: item.mobile, province: item.province, city: item.city, district: item.district, detail: item.detail, isDefault: item.isDefault }
      }
    } catch { /* ignore */ }
  }
})

async function onSubmit() {
  const { name, mobile, province, city, district, detail } = form.value
  if (!name || !mobile || !province || !city || !district || !detail) {
    Taro.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  try {
    if (editId.value) {
      await updateAddress(editId.value, form.value)
    } else {
      await addAddress(form.value)
    }
    Taro.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '保存失败', icon: 'none' })
  }
}
</script>

<style scoped>
.edit-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.form-card { background: #fff; border-radius: 16px; padding: 0 24px; }
.form-row { display: flex; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; }
.form-row:last-child { border-bottom: 0; }
.label { font-size: 28px; font-weight: 600; width: 160px; flex-shrink: 0; }
input, textarea { flex: 1; font-size: 28px; }
.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
