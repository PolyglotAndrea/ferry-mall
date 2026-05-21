<template>
  <div class="page-card">
    <h2>店铺管理</h2>
    <el-table :data="stores" row-key="id">
      <el-table-column label="店铺">
        <template #default="{ row }">
          <div class="store-cell"><img :src="row.logoUrl" /><div><strong>{{ row.name }}</strong><p>{{ row.description }}</p></div></div>
        </template>
      </el-table-column>
      <el-table-column prop="merchantId" label="商家ID" width="100" />
      <el-table-column prop="score" label="评分" width="100" />
      <el-table-column label="状态" width="120"><template #default="{ row }"><el-tag type="success" v-if="row.status === 1">营业中</el-tag></template></el-table-column>
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { storePageApi, type Store } from '@/api/store'
const stores = ref<Store[]>([])
onMounted(async () => { stores.value = (await storePageApi()).list })
</script>
<style scoped>.store-cell{display:flex;gap:12px;align-items:center}.store-cell img{width:42px;height:42px;border-radius:8px}.store-cell p{margin:4px 0 0;color:#64748b}</style>
