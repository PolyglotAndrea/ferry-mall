<template>
  <div class="page-card">
    <h2>商品列表</h2>
    <el-table :data="products" row-key="id">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="subtitle" label="卖点" />
      <el-table-column label="价格"><template #default="{ row }">¥{{ (row.priceCent / 100).toFixed(2) }}</template></el-table-column>
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="sales" label="销量" />
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { productPageApi, type ProductSpu } from '@/api/product'
const products = ref<ProductSpu[]>([])
onMounted(async () => { products.value = (await productPageApi()).list })
</script>
