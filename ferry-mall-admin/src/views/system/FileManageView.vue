<template>
  <div class="page-card">
    <h2>文件管理</h2>

    <!-- 上传区域 -->
    <el-upload
      drag
      action="#"
      :auto-upload="false"
      :on-change="handleFileChange"
      :show-file-list="false"
      accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.zip,.rar"
      style="margin-top: 16px"
    >
      <el-icon class="el-icon--upload" :size="50"><UploadFilled /></el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或 <em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          支持图片、文档等格式，单个文件不超过 50MB
        </div>
      </template>
    </el-upload>

    <!-- 文件表格 -->
    <el-table :data="files" row-key="id" style="margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="预览" width="100">
        <template #default="{ row }">
          <el-image
            v-if="isImage(row.contentType)"
            :src="row.url"
            fit="cover"
            style="width: 48px; height: 48px; border-radius: 4px; cursor: pointer"
            preview-teleported
            :preview-src-list="[row.url]"
          />
          <div v-else class="file-icon">
            <el-icon :size="28"><Document /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="文件名" min-width="200" show-overflow-tooltip />
      <el-table-column prop="contentType" label="类型" width="160" />
      <el-table-column prop="size" label="大小" width="120">
        <template #default="{ row }">
          {{ formatSize(row.size) }}
        </template>
      </el-table-column>
      <el-table-column prop="url" label="URL" min-width="260" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="上传时间" width="170" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="copyUrl(row.url)">复制链接</el-button>
          <el-button size="small" type="danger" @click="onDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageNo"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      style="margin-top: 20px; justify-content: flex-end"
      @change="fetchFiles"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Document } from '@element-plus/icons-vue'
import { filePageApi, uploadFileApi, deleteFileApi, type SysFile } from '@/api/system'

const files = ref<SysFile[]>([])
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

function isImage(contentType: string): boolean {
  return contentType?.startsWith('image/') ?? false
}

function formatSize(size: number): string {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

async function fetchFiles() {
  loading.value = true
  try {
    const res = await filePageApi(pageNo.value, pageSize.value)
    files.value = res.list
    total.value = res.total
  } catch (e: any) {
    ElMessage.error(e.message || '获取文件列表失败')
  } finally {
    loading.value = false
  }
}

async function handleFileChange(uploadFile: any) {
  const raw = uploadFile.raw as File
  if (!raw) return

  const maxSize = 50 * 1024 * 1024
  if (raw.size > maxSize) {
    ElMessage.warning('文件大小不能超过 50MB')
    return
  }

  loading.value = true
  try {
    await uploadFileApi(raw)
    ElMessage.success('上传成功')
    fetchFiles()
  } catch (e: any) {
    ElMessage.error(e.message || '上传失败')
  } finally {
    loading.value = false
  }
}

function copyUrl(url: string) {
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

async function onDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该文件吗？', '提示', { type: 'warning' })
    await deleteFileApi(id)
    ElMessage.success('删除成功')
    fetchFiles()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

onMounted(fetchFiles)
</script>

<style scoped>
.file-icon {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>
