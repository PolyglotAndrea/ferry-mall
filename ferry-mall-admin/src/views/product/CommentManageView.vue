<template>
  <div class="page">
    <h2>商品评价管理</h2>
    <el-table :data="comments" style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="spuId" label="商品ID" width="90" />
      <el-table-column prop="memberNickname" label="用户" width="120" />
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="160" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status !== 1" size="small" type="success" @click="audit(row.id)">审核</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '@/utils/request'

interface Comment { id: number; spuId: number; memberNickname: string; rating: number; content: string; status: number; createdAt: string }
const comments = ref<Comment[]>([])

async function fetch() {
  const res = await request.get('/admin-api/product/comment/page')
  comments.value = res.list
}
onMounted(fetch)

async function audit(id: number) {
  await request.post(`/admin-api/product/comment/${id}/audit`, null)
  ElMessage.success('审核通过')
  fetch()
}

async function del(id: number) {
  await ElMessageBox.confirm('确定删除该评价吗？', '提示', { type: 'warning' })
  await request.post(`/admin-api/product/comment/${id}/delete`, null)
  ElMessage.success('删除成功')
  fetch()
}
</script>
