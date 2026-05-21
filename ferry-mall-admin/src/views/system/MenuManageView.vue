<template>
  <div class="page-card">
    <h2>菜单管理</h2>
    <el-row :gutter="12" style="margin-bottom: 16px">
      <el-col :span="24" style="text-align: right">
        <el-button type="primary" @click="openDialog()">新增菜单</el-button>
      </el-col>
    </el-row>
    <el-table :data="menuList" row-key="id" default-expand-all v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="菜单名称" />
      <el-table-column prop="permission" label="权限标识" />
      <el-table-column prop="path" label="路由路径" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除?" @confirm="remove(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select v-model="form.parentId" :data="menuOptions" :props="{ label: 'name', value: 'id', children: 'children' }" check-strictly clearable placeholder="顶级菜单" />
        </el-form-item>
        <el-form-item label="菜单名称" required><el-input v-model="form.name" placeholder="请输入菜单名称" /></el-form-item>
        <el-form-item label="权限标识"><el-input v-model="form.permission" placeholder="如: system:user:page" /></el-form-item>
        <el-form-item label="路由路径"><el-input v-model="form.path" placeholder="如: /system/user" /></el-form-item>
        <el-form-item label="组件路径"><el-input v-model="form.component" placeholder="如: system/user/index" /></el-form-item>
        <el-form-item label="菜单图标"><el-input v-model="form.icon" placeholder="如: User" /></el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">目录</el-radio>
            <el-radio :label="2">菜单</el-radio>
            <el-radio :label="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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
import { ElMessage } from 'element-plus'
import { menuListApi, createMenuApi, updateMenuApi, deleteMenuApi, type MenuNode } from '@/api/system'

const menuList = ref<MenuNode[]>([])
const menuOptions = ref<MenuNode[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const form = ref({
  name: '', permission: '', path: '', component: '', icon: '', type: 1, parentId: 0, sort: 0
})

async function fetch() {
  loading.value = true
  try {
    const res = await menuListApi()
    menuList.value = res
    menuOptions.value = [{ id: 0, name: '顶级菜单', children: res, permission: '', type: 1, parentId: 0, sort: 0, path: '', component: '', icon: '', status: 1 }]
  } finally { loading.value = false }
}

function openDialog(row?: MenuNode) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = {
      name: row.name,
      permission: row.permission || '',
      path: row.path || '',
      component: row.component || '',
      icon: row.icon || '',
      type: row.type || 1,
      parentId: row.parentId || 0,
      sort: row.sort || 0
    }
  } else {
    isEdit.value = false
    editId.value = null
    form.value = { name: '', permission: '', path: '', component: '', icon: '', type: 1, parentId: 0, sort: 0 }
  }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.name) {
    ElMessage.warning('请输入菜单名称')
    return
  }
  const data = { ...form.value, parentId: form.value.parentId || 0 }
  try {
    if (isEdit.value && editId.value !== null) {
      await updateMenuApi(editId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createMenuApi(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '操作失败') }
}

async function remove(id: number) {
  try {
    await deleteMenuApi(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e: any) { ElMessage.error(e.message || '删除失败') }
}

onMounted(fetch)
</script>
