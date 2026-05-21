<template>
  <view class="comment-page">
    <view class="summary">
      <view class="rating-big">{{ avgRating }}</view>
      <view class="rating-stars">{{ '★'.repeat(Math.round(avgRating)) }}</view>
      <view class="rating-count">共 {{ total }} 条评价</view>
    </view>
    <view class="comment-list">
      <view v-for="c in comments" :key="c.id" class="comment-card">
        <view class="comment-header">
          <image :src="c.memberAvatar || 'https://dummyimage.com/80x80/e5e7eb/666&text=U'" class="avatar" mode="aspectFill" />
          <view class="comment-meta">
            <view class="nickname">{{ c.memberNickname || '匿名用户' }}</view>
            <view class="stars">{{ '★'.repeat(c.rating) }}{{ '☆'.repeat(5 - c.rating) }}</view>
          </view>
          <view class="comment-time">{{ c.createdAt?.slice(0, 10) }}</view>
        </view>
        <view class="comment-content">{{ c.content }}</view>
      </view>
      <view v-if="comments.length === 0" class="empty">暂无评价</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import Taro, { useLoad } from '@tarojs/taro'
import { getProductComments, type ProductComment } from '@/api/product'

const spuId = ref(0)
const comments = ref<ProductComment[]>([])
const total = ref(0)

const avgRating = computed(() => {
  if (comments.value.length === 0) return 5
  const sum = comments.value.reduce((s, c) => s + c.rating, 0)
  return (sum / comments.value.length).toFixed(1)
})

useLoad((query) => { spuId.value = Number(query.spuId || 0) })

onMounted(async () => {
  if (!spuId.value) return
  try {
    const res = await getProductComments(spuId.value)
    comments.value = res.list
    total.value = res.total
  } catch { comments.value = [] }
})
</script>

<style scoped>
.comment-page { min-height: 100vh; background: #f8fafc; }
.summary { text-align: center; padding: 40px 0; background: #fff; margin-bottom: 16px; }
.rating-big { font-size: 64px; font-weight: 800; color: #f59e0b; }
.rating-stars { font-size: 32px; color: #f59e0b; margin-top: 8px; }
.rating-count { font-size: 26px; color: #94a3b8; margin-top: 12px; }
.comment-list { padding: 20px; }
.comment-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.comment-header { display: flex; align-items: center; gap: 16px; }
.avatar { width: 64px; height: 64px; border-radius: 50%; background: #f1f5f9; }
.comment-meta { flex: 1; }
.nickname { font-size: 28px; font-weight: 600; }
.stars { font-size: 24px; color: #f59e0b; margin-top: 4px; }
.comment-time { font-size: 22px; color: #94a3b8; }
.comment-content { font-size: 26px; color: #475569; margin-top: 16px; line-height: 1.5; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; font-size: 28px; }
</style>
