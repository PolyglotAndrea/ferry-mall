<template>
  <div class="page">
    <h2>砍价活动管理</h2>
    <el-button type="primary" @click="showDialog = true">新增砍价活动</el-button>
    <el-table :data="activities" style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="spuId" label="商品ID" width="80" />
      <el-table-column prop="originalPriceCent" label="原价(分)" width="100" />
      <el-table-column prop="floorPriceCent" label="底价(分)" width="100" />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑砍价活动' : '新增砍价活动'" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="商品ID"><el-input v-model.number="form.spuId" type="number" /></el-form-item>
        <el-form-item label="原价(分)"><el-input v-model.number="form.originalPriceCent" type="number" /></el-form-item>
        <el-form-item label="底价(分)"><el-input v-model.number="form.floorPriceCent" type="number" /></el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
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

interface BargainActivity {
  id: number
  name: string
  spuId: number
  originalPriceCent: number
  floorPriceCent: number
  startTime: string
  endTime: string
  status: number
}

const activities = ref<BargainActivity[]>([])
const showDialog = ref(false)
const form = ref<Partial<BargainActivity>>({ name: '', spuId: undefined, originalPriceCent: undefined, floorPriceCent: undefined, startTime: '', endTime: '', status: 1 })

async function fetch() {
  activities.value = await request.get('/admin-api/marketing/bargain/list')
}
onMounted(fetch)

function edit(row: BargainActivity) {
  form.value = { ...row }
  showDialog.value = true
}

async function save() {
  const f = form.value
  if (f.id) {
    await request.put(`/admin-api/marketing/bargain/${f.id}/update`, f)
  } else {
    await request.post('/admin-api/marketing/bargain/create', f)
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  form.value = { name: '', spuId: undefined, originalPriceCent: undefined, floorPriceCent: undefined, startTime: '', endTime: '', status: 1 }
  fetch()
}

async function del(id: number) {
  await ElMessageBox.confirm('确定删除该砍价活动吗？', '提示', { type: 'warning' })
  await request.delete(`/admin-api/marketing/bargain/${id}`)
  ElMessage.success('删除成功')
  fetch()
}
</script>
