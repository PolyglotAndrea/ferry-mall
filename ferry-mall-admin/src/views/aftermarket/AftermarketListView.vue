<template>
  <div class="page-card">
    <h2>售后管理</h2>
    <el-table :data="records" row-key="id">
      <el-table-column prop="id" label="售后单号" width="120" />
      <el-table-column prop="orderId" label="订单ID" width="120" />
      <el-table-column prop="reason" label="申请原因" />
      <el-table-column prop="statusText" label="状态" width="120" />
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { aftermarketPageApi, type AftermarketRecord } from '@/api/aftermarket'
const records = ref<AftermarketRecord[]>([])
onMounted(async () => { records.value = (await aftermarketPageApi()).list })
</script>
