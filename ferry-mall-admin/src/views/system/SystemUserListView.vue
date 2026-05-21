<template>
  <div class="page-card">
    <h2>系统用户</h2>
    <el-table :data="users" row-key="id">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { sysUserPageApi, type SysUser } from '@/api/system'
const users = ref<SysUser[]>([])
onMounted(async () => { users.value = (await sysUserPageApi()).list })
</script>
