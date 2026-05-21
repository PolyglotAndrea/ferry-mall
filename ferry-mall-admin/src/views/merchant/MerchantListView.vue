<template>
  <div class="page-card">
    <div class="toolbar">
      <h2>商家入驻审核</h2>
      <el-tag type="success">B2B2C 平台商家管理</el-tag>
    </div>
    <el-table :data="merchants" row-key="id">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商家名称" />
      <el-table-column prop="contactName" label="联系人" />
      <el-table-column prop="contactMobile" label="联系电话" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button v-if="row.status === 10" size="small" type="primary" @click="approve(row.id)">通过</el-button>
          <el-tag v-else type="success">已通过</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { approveMerchantApi, merchantPageApi, type Merchant } from '@/api/merchant'
const merchants = ref<Merchant[]>([])
async function load() { merchants.value = (await merchantPageApi()).list }
async function approve(id: number) { await approveMerchantApi(id); ElMessage.success('审核通过'); await load() }
onMounted(load)
</script>
<style scoped>.toolbar{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px}</style>
