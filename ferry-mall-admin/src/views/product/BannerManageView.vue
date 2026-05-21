<template>
  <div class="page">
    <h2>Banner 管理</h2>
    <el-button type="primary" @click="showDialog = true">新增 Banner</el-button>
    <el-table :data="banners" style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" style="width: 80px; height: 40px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="linkUrl" label="链接" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑 Banner' : '新增 Banner'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="form.imageUrl" /></el-form-item>
        <el-form-item label="链接URL"><el-input v-model="form.linkUrl" /></el-form-item>
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

interface Banner { id: number; title: string; imageUrl: string; linkUrl: string; sort: number }
const banners = ref<Banner[]>([])
const showDialog = ref(false)
const form = ref<Partial<Banner>>({ title: '', imageUrl: '', linkUrl: '', sort: 0 })

async function fetch() {
  banners.value = await request.get('/admin-api/product/banner/list')
}
onMounted(fetch)

function edit(row: Banner) {
  form.value = { ...row }
  showDialog.value = true
}

async function save() {
  const f = form.value
  if (f.id) {
    await request.post(`/admin-api/product/banner/${f.id}/update`, null, { params: f })
  } else {
    await request.post('/admin-api/product/banner/create', null, { params: f })
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  form.value = { title: '', imageUrl: '', linkUrl: '', sort: 0 }
  fetch()
}

async function del(id: number) {
  await ElMessageBox.confirm('确定删除该 Banner 吗？', '提示', { type: 'warning' })
  await request.post(`/admin-api/product/banner/${id}/delete`, null)
  ElMessage.success('删除成功')
  fetch()
}
</script>
