<template>
  <div class="page-card">
    <h2>运营概览</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in cards" :key="item.label">
        <el-statistic :title="item.label" :value="item.value" />
      </el-col>
    </el-row>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { overviewApi, type Overview } from '@/api/statistics'
const overview = ref<Overview>({ orderCount: 0, salesAmountCent: 0, memberCount: 0, productCount: 0 })
const cards = computed(() => [
  { label: '订单数', value: overview.value.orderCount },
  { label: '销售额(元)', value: overview.value.salesAmountCent / 100 },
  { label: '会员数', value: overview.value.memberCount },
  { label: '商品数', value: overview.value.productCount }
])
onMounted(async () => { overview.value = await overviewApi() })
</script>
