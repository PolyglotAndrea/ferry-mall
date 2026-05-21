<template>
  <view class="level-page">
    <view class="header">
      <view class="current-level" v-if="currentLevel">
        <text class="level-name">{{ currentLevel.name }}</text>
        <text class="level-discount">享受 {{ currentLevel.discountPercent }}% 折扣</text>
      </view>
      <view class="current-level" v-else>
        <text class="level-name">普通会员</text>
        <text class="level-discount">升级可享更多权益</text>
      </view>
      <view class="points-info">当前积分 {{ profile?.points || 0 }}</view>
    </view>
    <view class="level-list">
      <view class="section-title">会员等级体系</view>
      <view v-for="level in levels" :key="level.id" class="level-card"
        :class="{ active: currentLevel?.id === level.id }"
      >
        <view class="level-top">
          <text class="name">{{ level.name }}</text>
          <text v-if="currentLevel?.id === level.id" class="badge">当前等级</text>
        </view>
        <view class="level-desc">
          最低积分 {{ level.minPoints }} · 购物享 {{ level.discountPercent }}% 折扣
        </view>
        <view class="progress-bar" v-if="profile">
          <view class="progress-fill" :style="{ width: progressWidth(level) + '%' }"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getMemberLevelList, getCurrentMemberLevel, type MemberLevel } from '@/api/member'
import { useUserStore } from '@/stores/user'

const levels = ref<MemberLevel[]>([])
const currentLevel = ref<MemberLevel | null>(null)
const user = useUserStore()
const profile = user.profile

onMounted(async () => {
  try { levels.value = await getMemberLevelList() } catch { levels.value = [] }
  try { currentLevel.value = await getCurrentMemberLevel() } catch { currentLevel.value = null }
})

function progressWidth(level: MemberLevel): number {
  const points = profile?.points || 0
  if (points >= level.minPoints) return 100
  const prev = levels.value.filter(l => l.minPoints < level.minPoints).pop()
  const prevMin = prev ? prev.minPoints : 0
  if (points <= prevMin) return 0
  return Math.round(((points - prevMin) / (level.minPoints - prevMin)) * 100)
}
</script>

<style scoped>
.level-page { min-height: 100vh; background: #f8fafc; }
.header { padding: 60px 32px; background: linear-gradient(135deg, #f59e0b, #d97706); text-align: center; }
.level-name { font-size: 48px; font-weight: 800; color: #fff; }
.level-discount { display: block; font-size: 26px; color: rgba(255,255,255,0.9); margin-top: 12px; }
.points-info { font-size: 28px; color: rgba(255,255,255,0.85); margin-top: 20px; }
.level-list { padding: 20px; }
.section-title { font-size: 30px; font-weight: 700; margin-bottom: 20px; }
.level-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; border: 2px solid transparent; }
.level-card.active { border-color: #f59e0b; background: #fffbeb; }
.level-top { display: flex; justify-content: space-between; align-items: center; }
.name { font-size: 30px; font-weight: 700; }
.badge { font-size: 22px; background: #f59e0b; color: #fff; padding: 4px 16px; border-radius: 12px; }
.level-desc { font-size: 24px; color: #64748b; margin-top: 12px; }
.progress-bar { height: 8px; background: #e2e8f0; border-radius: 4px; margin-top: 16px; overflow: hidden; }
.progress-fill { height: 100%; background: #f59e0b; border-radius: 4px; transition: width 0.3s; }
</style>
