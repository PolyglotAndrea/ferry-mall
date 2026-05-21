<template>
  <view class="records-page">
    <view v-for="r in records" :key="r.id" class="record-item">
      <view class="record-top">
        <text class="record-type">{{ r.type === 1 ? '推广佣金' : '团队奖励' }}</text>
        <text class="record-amount">+¥{{ (r.commissionCent / 100).toFixed(2) }}</text>
      </view>
      <view class="record-order">订单：{{ r.orderNo || '-' }}</view>
      <view class="record-time">{{ r.createdAt?.slice(0, 16).replace('T', ' ') }}</view>
    </view>
    <view v-if="records.length === 0" class="empty">暂无佣金记录</view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getCommissionRecords, type CommissionRecord } from '@/api/commission'

const records = ref<CommissionRecord[]>([])

onMounted(async () => {
  try {
    const res = await getCommissionRecords()
    records.value = res.list
  } catch { records.value = [] }
})
</script>

<style scoped>
.records-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.record-item { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.record-top { display: flex; justify-content: space-between; align-items: center; }
.record-type { font-size: 28px; font-weight: 600; }
.record-amount { font-size: 30px; color: #22c55e; font-weight: 700; }
.record-order { font-size: 24px; color: #64748b; margin-top: 8px; }
.record-time { font-size: 22px; color: #94a3b8; margin-top: 6px; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>