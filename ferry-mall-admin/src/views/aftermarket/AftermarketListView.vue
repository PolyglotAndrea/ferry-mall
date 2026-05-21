<template>
  <div class="page-card">
    <h2>售后管理</h2>
    <el-table :data="records" row-key="id" style="margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderId" label="订单ID" width="120" />
      <el-table-column prop="reason" label="申请原因" show-overflow-tooltip />
      <el-table-column prop="statusText" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 10 || row.status === 20"
            size="small"
            type="success"
            @click="approve(row.id)"
          >审核通过</el-button>
          <el-button
            v-if="row.status === 10 || row.status === 20"
            size="small"
            type="danger"
            @click="reject(row.id)"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '@/utils/request'

interface AftermarketRecord {
  id: number
  orderId: number
  reason: string
  status: number
  statusText: string
  createdAt: string
}

interface PageResult<T> {
  list: T[]
  total: number
  pages: number
}

const records = ref<AftermarketRecord[]>([])
const loading = ref(false)

function statusTagType(status: number): string {
  switch (status) {
    case 10: return 'warning'
    case 20: return 'primary'
    case 30: return 'success'
    case 40: return 'danger'
    default: return 'info'
  }
}

async function fetch() {
  loading.value = true
  try {
    const res = await request.get<unknown, PageResult<AftermarketRecord>>('/admin-api/aftermarket/page')
    records.value = res.list
  } finally {
    loading.value = false
  }
}

async function approve(id: number) {
  try {
    await ElMessageBox.confirm('确定审核通过该售后申请吗？', '提示', { type: 'warning' })
    await request.post(`/admin-api/aftermarket/${id}/approve`, null)
    ElMessage.success('审核通过')
    fetch()
  } catch (e) {
    // user cancelled
  }
}

async function reject(id: number) {
  try {
    await ElMessageBox.confirm('确定拒绝该售后申请吗？', '提示', { type: 'warning' })
    await request.post(`/admin-api/aftermarket/${id}/reject`, null)
    ElMessage.success('已拒绝')
    fetch()
  } catch (e) {
    // user cancelled
  }
}

onMounted(fetch)
</script>
