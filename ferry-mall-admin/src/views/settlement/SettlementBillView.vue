<template>
  <div class="page-card">
    <h2>商家结算账单</h2>
    <el-table :data="bills" row-key="id">
      <el-table-column prop="merchantName" label="商家" />
      <el-table-column label="订单金额"><template #default="{ row }">¥{{ money(row.orderAmountCent) }}</template></el-table-column>
      <el-table-column label="平台佣金"><template #default="{ row }">¥{{ money(row.commissionCent) }}</template></el-table-column>
      <el-table-column label="应结金额"><template #default="{ row }"><strong>¥{{ money(row.payableCent) }}</strong></template></el-table-column>
      <el-table-column prop="statusText" label="状态" />
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { settlementBillPageApi, type SettlementBill } from '@/api/settlement'
const bills = ref<SettlementBill[]>([])
const money = (cent: number) => (cent / 100).toFixed(2)
onMounted(async () => { bills.value = (await settlementBillPageApi()).list })
</script>
