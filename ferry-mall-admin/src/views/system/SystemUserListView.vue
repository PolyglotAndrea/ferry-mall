<template>
  <div class="page-card">
    <h2>系统用户</h2>
    <el-row :gutter="12" style="margin-bottom: 16px">
      <el-col :span="6">
        <el-input v-model="keyword" placeholder="搜索用户名/昵称" clearable @keyup.enter="fetch" />
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="fetch">搜索</el-button>
        <el-button @click="keyword = ''; fetch()">重置</el-button>
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-button type="primary" @click="openDialog()">新增用户</el-button>
      </el-col>
    </el-row>
    <el-table :data="users" row-key="id" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          <el-button size="small" @click="resetPwd(row)">重置密码</el-button>
          <el-popconfirm title="确定删除?" @confirm="remove(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNo" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetch" style="margin-top: 16px" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="密码" :required="!isEdit">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  sysUserPageApi, createSysUserApi, updateSysUserApi, deleteSysUserApi,
  toggleSysUserStatusApi, resetSysUserPasswordApi, type SysUser
} from '@/api/system'

const users = ref<SysUser[]>([])
const loading = ref(false)
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const form = ref({ username: '', nickname: '', password: '' })

async function fetch() {
  loading.value = true
  try {
    const res = await sysUserPageApi(keyword.value || undefined, pageNo.value, pageSize.value)
    users.value = res.list
    total.value = res.total
  } finally { loading.value = false }
}

function openDialog(row?: SysUser) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = { username: row.username, nickname: row.nickname, password: '' }
  } else {
    isEdit.value = false
    editId.value = null
    form.value = { username: '', nickname: '', password: '' }
  }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.username || !form.value.nickname) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!isEdit.value && !form.value.password) {
    ElMessage.warning('请输入密码')
    return
  }
  try {
    if (isEdit.value && editId.value !== null) {
      await updateSysUserApi(editId.value, {
        nickname: form.value.nickname,
        password: form.value.password || undefined
      })
      ElMessage.success('更新成功')
    } else {
      await createSysUserApi({
        username: form.value.username,
        password: form.value.password,
        nickname: form.value.nickname
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '操作失败') }
}

async function toggleStatus(row: SysUser) {
  try {
    await toggleSysUserStatusApi(row.id)
    ElMessage.success('状态更新成功')
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '操作失败') }
}

async function resetPwd(row: SysUser) {
  try {
    await ElMessageBox.confirm('确定重置该用户密码为 123456 吗？', '提示')
    await resetSysUserPasswordApi(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch { /* ignore */ }
}

async function remove(id: number) {
  try {
    await deleteSysUserApi(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '删除失败') }
}

onMounted(fetch)
</script>
