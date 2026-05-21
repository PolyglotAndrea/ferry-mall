<template>
  <div class="page">
    <h2>商品评价管理</h2>
    <el-table :data="comments" style="margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="spuId" label="商品ID" width="90" />
      <el-table-column prop="memberNickname" label="用户" width="120" />
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="图片" width="200">
        <template #default="{ row }">
          <div v-if="row.images" class="comment-images">
            <el-image
              v-for="(url, idx) in row.images.split(',')"
              :key="idx"
              :src="url.trim()"
              :preview-src-list="row.images.split(',').map((u: string) => u.trim())"
              fit="cover"
              class="comment-thumb"
              :preview-teleported="true"
            />
          </div>
          <span v-else class="no-images">无</span>
        </template>
      </el-table-column>
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

interface Comment {
  id: number
  spuId: number
  memberNickname: string
  rating: number
  content: string
  images: string
  status: number
  createdAt: string
}

const comments = ref<Comment[]>([])
const loading = ref(false)

async function fetch() {
  loading.value = true
  try {
    const res = await request.get<any, { list: any[] }>('/admin-api/product/comment/page')
    comments.value = res.list
  } finally {
    loading.value = false
  }
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

<style scoped>
.comment-images {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
.comment-thumb {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  cursor: pointer;
}
.no-images {
  color: #999;
  font-size: 12px;
}
</style>
