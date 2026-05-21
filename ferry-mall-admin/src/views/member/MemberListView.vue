<template>
  <div class="page-card">
    <div class="toolbar">
      <h2>会员列表</h2>
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索昵称 / 手机号"
          clearable
          style="width: 260px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <el-table :data="members" row-key="id" v-loading="loading" style="margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar" v-if="row.avatar">
            <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
          </el-avatar>
          <el-avatar :size="40" v-else>{{ row.nickname?.charAt(0) || '?' }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="mobile" label="手机号" width="140" />
      <el-table-column prop="level" label="会员等级" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.level" size="small">{{ row.level }}</el-tag>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分" width="100" />
      <el-table-column prop="orderCount" label="订单数" width="100" />
      <el-table-column label="消费总额" width="120">
        <template #default="{ row }">
          ¥{{ (row.totalSpendCent / 100).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDetail(row.id)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pageNo"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog v-model="detailVisible" title="会员详情" width="640px" destroy-on-close>
      <div v-if="detail" class="detail-content">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-row">
            <div class="detail-item">
              <span class="label">头像</span>
              <el-avatar :size="48" :src="detail.avatar" v-if="detail.avatar">
                <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
              </el-avatar>
              <el-avatar :size="48" v-else>{{ detail.nickname?.charAt(0) || '?' }}</el-avatar>
            </div>
            <div class="detail-item">
              <span class="label">昵称</span>
              <span class="value">{{ detail.nickname }}</span>
            </div>
            <div class="detail-item">
              <span class="label">手机号</span>
              <span class="value">{{ detail.mobile || '-' }}</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-item">
              <span class="label">会员等级</span>
              <el-tag v-if="detail.level" size="small">{{ detail.level }}</el-tag>
              <span v-else class="text-muted">-</span>
            </div>
            <div class="detail-item">
              <span class="label">当前积分</span>
              <span class="value">{{ detail.points }}</span>
            </div>
            <div class="detail-item">
              <span class="label">状态</span>
              <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
                {{ detail.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>统计信息</h4>
          <div class="detail-row">
            <div class="detail-item">
              <span class="label">订单数</span>
              <span class="value">{{ detail.orderCount }}</span>
            </div>
            <div class="detail-item">
              <span class="label">消费总额</span>
              <span class="value">¥{{ (detail.totalSpendCent / 100).toFixed(2) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">注册时间</span>
              <span class="value">{{ detail.createdAt }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>最近积分变动</h4>
          <el-table :data="integralRecords" size="small" v-loading="integralLoading">
            <el-table-column prop="changeCount" label="变动" width="100">
              <template #default="{ row }">
                <span :class="row.changeCount >= 0 ? 'text-success' : 'text-danger'">
                  {{ row.changeCount >= 0 ? '+' : '' }}{{ row.changeCount }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="currentPoints" label="变动后积分" width="110" />
            <el-table-column prop="reason" label="原因" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="时间" width="160" />
          </el-table>
          <div class="pagination-wrapper" v-if="integralTotal > integralPageSize">
            <el-pagination
              v-model:current-page="integralPageNo"
              v-model:page-size="integralPageSize"
              :page-sizes="[5, 10]"
              :total="integralTotal"
              small
              layout="total, prev, pager, next"
              @current-change="handleIntegralPageChange"
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  memberPageApi,
  getMemberDetailApi,
  getMemberIntegralRecordsApi,
  type MemberItem,
  type MemberDetail,
  type IntegralRecord,
} from '@/api/member'

const members = ref<MemberItem[]>([])
const loading = ref(false)
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const detail = ref<MemberDetail | null>(null)
const currentMemberId = ref(0)

const integralRecords = ref<IntegralRecord[]>([])
const integralLoading = ref(false)
const integralPageNo = ref(1)
const integralPageSize = ref(5)
const integralTotal = ref(0)

async function fetchMembers() {
  loading.value = true
  try {
    const res = await memberPageApi(keyword.value || undefined, pageNo.value, pageSize.value)
    members.value = res.list
    total.value = res.total
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '加载失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNo.value = 1
  fetchMembers()
}

function handleSizeChange(val: number) {
  pageSize.value = val
  pageNo.value = 1
  fetchMembers()
}

function handlePageChange(val: number) {
  pageNo.value = val
  fetchMembers()
}

async function openDetail(id: number) {
  currentMemberId.value = id
  detailVisible.value = true
  try {
    detail.value = await getMemberDetailApi(id)
    integralPageNo.value = 1
    await fetchIntegralRecords()
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '加载详情失败'
    ElMessage.error(msg)
  }
}

async function fetchIntegralRecords() {
  if (!currentMemberId.value) return
  integralLoading.value = true
  try {
    const res = await getMemberIntegralRecordsApi(
      currentMemberId.value,
      integralPageNo.value,
      integralPageSize.value
    )
    integralRecords.value = res.list
    integralTotal.value = res.total
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '加载积分记录失败'
    ElMessage.error(msg)
  } finally {
    integralLoading.value = false
  }
}

function handleIntegralPageChange(val: number) {
  integralPageNo.value = val
  fetchIntegralRecords()
}

onMounted(fetchMembers)
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.search-bar {
  display: flex;
  gap: 8px;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.detail-content {
  padding: 0 8px;
}
.detail-section {
  margin-bottom: 24px;
}
.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 15px;
  color: #1f2937;
  border-left: 3px solid #409eff;
  padding-left: 10px;
}
.detail-row {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.detail-item .label {
  color: #6b7280;
  font-size: 13px;
}
.detail-item .value {
  color: #1f2937;
  font-size: 14px;
  font-weight: 500;
}
.text-muted {
  color: #9ca3af;
  font-size: 13px;
}
.text-success {
  color: #67c23a;
  font-weight: 500;
}
.text-danger {
  color: #f56c6c;
  font-weight: 500;
}
</style>
