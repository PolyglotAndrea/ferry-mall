<template>
  <div class="page-card">
    <h2>订单列表</h2>
    <el-table :data="orders" row-key="id">
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column label="实付金额"><template #default="{ row }">¥{{ (row.payAmountCent / 100).toFixed(2) }}</template></el-table-column>
      <el-table-column prop="statusText" label="状态" />
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { orderPageApi, type OrderItem } from '@/api/order'
const orders = ref<OrderItem[]>([])
onMounted(async () => { orders.value = (await orderPageApi()).list })
</script>
