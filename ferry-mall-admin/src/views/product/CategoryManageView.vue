<template>
  <div class="page">
    <h2>商品分类管理</h2>
    <el-button type="primary" @click="showDialog = true">新增分类</el-button>
    <el-table :data="categories" style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="parentId" label="父分类ID" width="120" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="父分类ID"><el-input v-model.number="form.parentId" type="number" /></el-form-item>
        <el-form-item label="排序"><el-input v-model.number="form.sort" type="number" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '@/utils/request'

interface Category { id: number; name: string; parentId: number; sort: number }
const categories = ref<Category[]>([])
const showDialog = ref(false)
const form = ref<Partial<Category>>({ name: '', parentId: 0, sort: 0 })

async function fetch() {
  categories.value = await request.get('/admin-api/product/category/tree')
}
onMounted(fetch)

function edit(row: Category) {
  form.value = { ...row }
  showDialog.value = true
}

async function save() {
  const f = form.value
  if (f.id) {
    await request.post(`/admin-api/product/category/${f.id}/update`, null, { params: f })
  } else {
    await request.post('/admin-api/product/category/create', null, { params: f })
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  form.value = { name: '', parentId: 0, sort: 0 }
  fetch()
}

async function del(id: number) {
  await ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
  await request.post(`/admin-api/product/category/${id}/delete`, null)
  ElMessage.success('删除成功')
  fetch()
}
</script>
