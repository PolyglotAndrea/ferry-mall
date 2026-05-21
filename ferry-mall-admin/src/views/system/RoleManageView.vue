<template>
  <div class="page-card">
    <h2>角色管理</h2>
    <el-row :gutter="12" style="margin-bottom: 16px">
      <el-col :span="24" style="text-align: right">
        <el-button type="primary" @click="openDialog()">新增角色</el-button>
      </el-col>
    </el-row>
    <el-table :data="roles" row-key="id" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="角色名称" />
      <el-table-column prop="code" label="权限字符" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" @click="openMenuDialog(row)">权限</el-button>
          <el-popconfirm title="确定删除?" @confirm="remove(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNo" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetch" style="margin-top: 16px" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称" required><el-input v-model="form.name" placeholder="请输入角色名称" /></el-form-item>
        <el-form-item label="权限字符" required><el-input v-model="form.code" placeholder="如: admin, common" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" title="分配权限" width="480px">
      <el-tree ref="menuTreeRef" :data="menuTree" show-checkbox node-key="id" :props="{ label: 'name', children: 'children' }" />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMenuAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  rolePageApi, createRoleApi, updateRoleApi, deleteRoleApi,
  assignRoleMenusApi, menuTreeApi, type Role, type MenuNode
} from '@/api/system'

const roles = ref<Role[]>([])
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const form = ref({ name: '', code: '' })

const menuDialogVisible = ref(false)
const menuRoleId = ref<number | null>(null)
const menuTree = ref<MenuNode[]>([])
const menuTreeRef = ref<any>(null)

async function fetch() {
  loading.value = true
  try {
    const res = await rolePageApi(pageNo.value, pageSize.value)
    roles.value = res.list
    total.value = res.total
  } finally { loading.value = false }
}

function openDialog(row?: Role) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = { name: row.name, code: row.code }
  } else {
    isEdit.value = false
    editId.value = null
    form.value = { name: '', code: '' }
  }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.name || !form.value.code) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (isEdit.value && editId.value !== null) {
      await updateRoleApi(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createRoleApi(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '操作失败') }
}

async function remove(id: number) {
  try {
    await deleteRoleApi(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '删除失败') }
}

async function openMenuDialog(row: Role) {
  menuRoleId.value = row.id
  try {
    const tree = await menuTreeApi()
    menuTree.value = tree
    menuDialogVisible.value = true
  } catch (e: any) { ElMessage.error(e.message || '加载菜单失败') }
}

async function submitMenuAssign() {
  if (!menuRoleId.value || !menuTreeRef.value) return
  const checked = menuTreeRef.value.getCheckedKeys() as number[]
  const half = menuTreeRef.value.getHalfCheckedKeys() as number[]
  try {
    await assignRoleMenusApi(menuRoleId.value, [...checked, ...half])
    ElMessage.success('权限分配成功')
    menuDialogVisible.value = false
  } catch (e: any) { ElMessage.error(e.message || '分配失败') }
}

onMounted(fetch)
</script>
