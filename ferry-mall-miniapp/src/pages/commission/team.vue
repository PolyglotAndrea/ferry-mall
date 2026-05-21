<template>
  <view class="team-page">
    <view v-for="m in team" :key="m.id" class="member-item">
      <text class="member-id">会员 #{{ m.memberId }}</text>
      <text class="member-amount">累计 ¥{{ (m.totalCommissionCent / 100).toFixed(2) }}</text>
    </view>
    <view v-if="team.length === 0" class="empty">暂无团队成员</view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getTeamList, type TeamMember } from '@/api/commission'

const team = ref<TeamMember[]>([])

onMounted(async () => {
  try { team.value = await getTeamList() } catch { team.value = [] }
})
</script>

<style scoped>
.team-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.member-item { display: flex; justify-content: space-between; align-items: center; padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.member-id { font-size: 28px; }
.member-amount { font-size: 26px; color: #7c3aed; font-weight: 600; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>