<template>
  <view class="address-page">
    <view v-if="addresses.length === 0" class="empty">
      <text class="empty-icon">&#x1F3E0;</text>
      <text class="empty-text">暂无收货地址</text>
      <text class="add-btn" @tap="goEdit()">添加地址</text>
    </view>
    <view v-else class="address-list">
      <view v-for="a in addresses" :key="a.id" class="address-card" @tap="selectAddress(a)">
        <view class="addr-top">
          <text class="addr-name">{{ a.name }}</text>
          <text class="addr-mobile">{{ a.mobile }}</text>
          <text v-if="a.isDefault === 1" class="addr-default">默认</text>
        </view>
        <view class="addr-detail">{{ a.province }}{{ a.city }}{{ a.district }}{{ a.detail }}</view>
        <view class="addr-actions">
          <view class="action-left">
            <text v-if="a.isDefault !== 1" class="action" @tap.stop="onSetDefault(a.id)">
              <text class="radio" />&nbsp;设为默认
            </text>
            <text v-else class="action active">
              <text class="radio checked">&#x2713;</text>&nbsp;默认地址
            </text>
          </view>
          <view class="action-right">
            <text class="action" @tap.stop="goEdit(a)">&#x270F; 编辑</text>
            <text class="action danger" @tap.stop="onDelete(a.id)">&#x1F5D1; 删除</text>
          </view>
        </view>
      </view>
      <view class="add-new" @tap="goEdit()">+ 添加新地址</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow } from '@tarojs/taro'
import { ref } from 'vue'
import { getAddressList, deleteAddress, setDefaultAddress, type AddressItem } from '@/api/address'

const addresses = ref<AddressItem[]>([])

async function fetch() {
  try {
    addresses.value = await getAddressList()
  } catch {
    addresses.value = []
  }
}
onShow(() => fetch())

function goEdit(a?: AddressItem) {
  const url = a ? `/pages/address/edit?id=${a.id}` : '/pages/address/edit'
  Taro.navigateTo({ url })
}

async function onDelete(id: number) {
  const res = await Taro.showModal({ title: '提示', content: '确定删除该地址吗？' })
  if (!res.confirm) return
  try {
    await deleteAddress(id)
    Taro.showToast({ title: '已删除', icon: 'success' })
    fetch()
  } catch (e: any) {
    Taro.showToast({ title: e.message || '删除失败', icon: 'none' })
  }
}

async function onSetDefault(id: number) {
  try {
    await setDefaultAddress(id)
    Taro.showToast({ title: '已设为默认', icon: 'success' })
    fetch()
  } catch (e: any) {
    Taro.showToast({ title: e.message || '设置失败', icon: 'none' })
  }
}

function selectAddress(a: AddressItem) {
  const pages = Taro.getCurrentPages()
  const prev = pages[pages.length - 2]
  if (prev && prev.route?.includes('order/confirm')) {
    // @ts-ignore
    prev.$vm?.setAddress?.(a)
    Taro.navigateBack()
  }
}
</script>

<style scoped>
.address-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.empty { display: flex; flex-direction: column; align-items: center; padding-top: 200px; }
.empty-icon { font-size: 100px; }
.empty-text { font-size: 28px; color: #94a3b8; margin-top: 16px; }
.add-btn { margin-top: 24px; padding: 14px 48px; background: #2563eb; color: #fff; border-radius: 32px; font-size: 28px; }
.address-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.addr-top { display: flex; align-items: center; gap: 16px; margin-bottom: 12px; }
.addr-name { font-size: 30px; font-weight: 700; }
.addr-mobile { font-size: 26px; color: #475569; }
.addr-default { font-size: 20px; background: #dbeafe; color: #2563eb; padding: 2px 12px; border-radius: 8px; }
.addr-detail { font-size: 26px; color: #64748b; line-height: 1.5; }
.addr-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #f1f5f9; }
.action-left { display: flex; align-items: center; }
.action-right { display: flex; gap: 24px; }
.action { font-size: 26px; color: #475569; display: flex; align-items: center; }
.action.active { color: #2563eb; }
.radio { width: 32px; height: 32px; border-radius: 50%; border: 2px solid #cbd5e1; display: inline-flex; align-items: center; justify-content: center; font-size: 20px; color: #fff; }
.radio.checked { background: #2563eb; border-color: #2563eb; }
.action.danger { color: #ef4444; }
.add-new { text-align: center; padding: 24px; background: #fff; border-radius: 16px; font-size: 28px; color: #2563eb; border: 2px dashed #cbd5e1; }
</style>
