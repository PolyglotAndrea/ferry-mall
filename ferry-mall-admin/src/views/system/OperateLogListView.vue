<template>
  <div class="page-card">
    <h2>操作日志</h2>

    <!-- 搜索栏 -->
    <el-form :inline="true" class="search-form">
      <el-form-item label="模块">
        <el-input v-model="searchModule" placeholder="请输入模块名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="操作">
        <el-input v-model="searchName" placeholder="请输入操作名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchResult" placeholder="全部" clearable style="width: 120px">
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 360px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 日志表格 -->
    <el-table :data="logs" row-key="id" style="margin-top: 12px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="createdAt" label="时间" width="170" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="module" label="模块" width="140" />
      <el-table-column prop="name" label="操作" width="140" />
      <el-table-column prop="requestMethod" label="请求方法" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="methodTagType(row.requestMethod)">{{ row.requestMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="requestUrl" label="请求URL" min-width="200" show-overflow-tooltip />
      <el-table-column prop="userIp" label="IP" width="130" />
      <el-table-column prop="duration" label="耗时(ms)" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.duration > 1000 ? '#f56c6c' : row.duration > 500 ? '#e6a23c' : '#67c23a' }">
            {{ row.duration }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="状态" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="row.result === 1 ? 'success' : 'danger'">
            {{ row.result === 1 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
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
      @change="fetchLogs"
    />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="1" border v-if="currentLog">
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ currentLog.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentLog.userId }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ currentLog.module }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ currentLog.name }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">
          <el-tag size="small" :type="methodTagType(currentLog.requestMethod)">{{ currentLog.requestMethod }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ currentLog.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre style="white-space: pre-wrap; word-break: break-all; margin: 0; font-size: 12px; background: #f5f7fa; padding: 8px; border-radius: 4px;">{{ currentLog.requestParams || '-' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果">
          <pre style="white-space: pre-wrap; word-break: break-all; margin: 0; font-size: 12px; background: #f5f7fa; padding: 8px; border-radius: 4px;">{{ currentLog.responseBody || '-' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="IP">{{ currentLog.userIp }}</el-descriptions-item>
        <el-descriptions-item label="耗时">
          <span :style="{ color: currentLog.duration > 1000 ? '#f56c6c' : currentLog.duration > 500 ? '#e6a23c' : '#67c23a' }">
            {{ currentLog.duration }} ms
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="small" :type="currentLog.result === 1 ? 'success' : 'danger'">
            {{ currentLog.result === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { operateLogPageApi, type OperateLog } from '@/api/system'

const logs = ref<OperateLog[]>([])
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchModule = ref('')
const searchName = ref('')
const searchResult = ref<number | undefined>(undefined)
const dateRange = ref<string[]>([])

const detailVisible = ref(false)
const currentLog = ref<OperateLog | null>(null)

function methodTagType(method: string): string {
  switch (method) {
    case 'GET': return 'success'
    case 'POST': return 'primary'
    case 'PUT': return 'warning'
    case 'DELETE': return 'danger'
    default: return 'info'
  }
}

async function fetchLogs() {
  loading.value = true
  try {
    const res = await operateLogPageApi(
      searchModule.value || undefined,
      searchName.value || undefined,
      searchResult.value,
      dateRange.value?.[0],
      dateRange.value?.[1],
      pageNo.value,
      pageSize.value
    )
    logs.value = res.list
    total.value = res.total
  } catch (e: any) {
    ElMessage.error(e.message || '获取操作日志失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNo.value = 1
  fetchLogs()
}

function onReset() {
  searchModule.value = ''
  searchName.value = ''
  searchResult.value = undefined
  dateRange.value = []
  pageNo.value = 1
  fetchLogs()
}

function showDetail(row: OperateLog) {
  currentLog.value = row
  detailVisible.value = true
}

onMounted(fetchLogs)
</script>

<style scoped>
.search-form {
  margin-top: 16px;
}
</style>
