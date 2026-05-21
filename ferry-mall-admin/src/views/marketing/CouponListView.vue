<template>
  <div class="page-card">
    <h2>优惠券管理</h2>
    <el-table :data="coupons" row-key="id">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="券名" />
      <el-table-column label="优惠">
        <template #default="{ row }">
          <span v-if="row.discountCent > 0">减 ¥{{ (row.discountCent / 100).toFixed(2) }}</span>
          <span v-else>折扣</span>
        </template>
      </el-table-column>
      <el-table-column label="门槛">
        <template #default="{ row }">满 ¥{{ (row.thresholdCent / 100).toFixed(2) }} 可用</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '进行中' : '已结束' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { couponPageApi, type Coupon } from '@/api/marketing'
const coupons = ref<Coupon[]>([])
onMounted(async () => { coupons.value = (await couponPageApi()).list })
</script>
