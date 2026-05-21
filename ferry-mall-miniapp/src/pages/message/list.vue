<template>
  <view class="message-page">
    <!-- 消息类型标签 -->
    <view class="type-tabs">
      <view
        v-for="t in typeTabs"
        :key="t.type ?? -1"
        class="type-tab"
        :class="{ active: activeType === t.type }"
        @tap="activeType = t.type"
      >
        {{ t.label }}
        <text v-if="t.type !== undefined && typeUnreadCount[t.type] > 0" class="tab-badge">{{ typeUnreadCount[t.type] }}</text>
      </view>
    </view>

    <!-- 消息列表 -->
    <scroll-view class="message-list" scroll-y @scrolltolower="loadMore">
      <view v-for="m in filteredMessages" :key="m.id" class="message-card" :class="{ unread: m.isRead === 0 }" @tap="onTapMessage(m)">
        <view class="msg-header">
          <view class="msg-type-tag" :class="typeClass(m.type)">{{ typeLabel(m.type) }}</view>
          <text class="msg-title">{{ m.title }}</text>
          <text v-if="m.isRead === 0" class="msg-dot"></text>
        </view>
        <view class="msg-content">{{ m.content }}</view>
        <view class="msg-time">{{ formatTime(m.createdAt) }}</view>
      </view>

      <view v-if="loading" class="loading">加载中...</view>
      <view v-if="!hasMore && filteredMessages.length > 0" class="no-more">没有更多了</view>
      <view v-if="filteredMessages.length === 0 && !loading" class="empty">
        <text class="empty-icon">&#x1F4ED;</text>
        <view class="empty-text">暂无消息</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import Taro, { onShow, onPullDownRefresh } from '@tarojs/taro'
import { ref, computed, watch } from 'vue'
import { getMessages, markMessageRead, getUnreadMessageCount, type MessageItem, type MessageType } from '@/api/message'

const typeTabs = [
  { label: '全部', type: undefined },
  { label: '订单', type: 1 },
  { label: '活动', type: 2 },
  { label: '系统', type: 3 },
] as const

const activeType = ref<MessageType | undefined>(undefined)
const messages = ref<MessageItem[]>([])
const pageNo = ref(1)
const pageSize = 20
const loading = ref(false)
const hasMore = ref(true)
const unreadCount = ref(0)

const filteredMessages = computed(() => {
  if (activeType.value === undefined) return messages.value
  return messages.value.filter(m => m.type === activeType.value)
})

const typeUnreadCount = computed(() => {
  const counts: Record<number, number> = { 1: 0, 2: 0, 3: 0 }
  messages.value.forEach(m => {
    if (m.isRead === 0 && m.type in counts) {
      counts[m.type]++
    }
  })
  return counts
})

function typeLabel(type: MessageType): string {
  switch (type) {
    case 1: return '订单'
    case 2: return '活动'
    case 3: return '系统'
    default: return '其他'
  }
}

function typeClass(type: MessageType): string {
  switch (type) {
    case 1: return 'type-order'
    case 2: return 'type-promo'
    case 3: return 'type-system'
    default: return 'type-system'
  }
}

function formatTime(createdAt?: string): string {
  if (!createdAt) return ''
  return createdAt.slice(0, 16).replace('T', ' ')
}

async function fetchMessages(reset = false) {
  if (loading.value) return
  loading.value = true
  try {
    if (reset) {
      pageNo.value = 1
      messages.value = []
      hasMore.value = true
    }
    const res = await getMessages(pageNo.value, pageSize)
    const list = res.list ?? []
    messages.value.push(...list)
    if (list.length < pageSize) hasMore.value = false
    else pageNo.value++
  } finally {
    loading.value = false
    if (reset) Taro.stopPullDownRefresh()
  }
}

async function fetchUnreadCount() {
  try {
    unreadCount.value = await getUnreadMessageCount()
  } catch {
    unreadCount.value = 0
  }
}

function loadMore() {
  if (!hasMore.value || loading.value) return
  fetchMessages()
}

async function onTapMessage(m: MessageItem) {
  if (m.isRead === 0) {
    try {
      await markMessageRead(m.id)
      m.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch {
      // ignore
    }
  }
}

watch(activeType, () => {
  // 切换标签时不需要重新请求，因为后端返回全部，前端过滤
})

onShow(() => {
  fetchMessages(true)
  fetchUnreadCount()
})

onPullDownRefresh(() => {
  fetchMessages(true)
  fetchUnreadCount()
})
</script>

<style scoped>
.message-page { height: 100vh; display: flex; flex-direction: column; background: #f8fafc; }

/* 类型标签 */
.type-tabs { display: flex; background: #fff; border-bottom: 1px solid #f1f5f9; padding: 0 20px; }
.type-tab { flex: 1; text-align: center; padding: 24px 0; font-size: 28px; color: #64748b; position: relative; }
.type-tab.active { color: #2563eb; font-weight: 700; border-bottom: 4px solid #2563eb; }
.tab-badge { position: absolute; top: 12px; right: 8px; min-width: 32px; height: 32px; line-height: 32px; padding: 0 8px; background: #ef4444; color: #fff; border-radius: 16px; font-size: 20px; font-weight: 700; }

/* 消息列表 */
.message-list { flex: 1; padding: 16px 20px; overflow-y: auto; }
.message-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; position: relative; }
.message-card.unread { border-left: 4px solid #2563eb; }

.msg-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.msg-type-tag { padding: 4px 12px; border-radius: 8px; font-size: 20px; font-weight: 600; flex-shrink: 0; }
.type-order { background: #dbeafe; color: #2563eb; }
.type-promo { background: #fef3c7; color: #d97706; }
.type-system { background: #f1f5f9; color: #475569; }
.msg-title { font-size: 28px; font-weight: 700; flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.msg-dot { width: 16px; height: 16px; border-radius: 50%; background: #ef4444; flex-shrink: 0; }

.msg-content { font-size: 26px; color: #475569; line-height: 1.5; }
.msg-time { font-size: 22px; color: #94a3b8; margin-top: 12px; }

.loading { text-align: center; color: #94a3b8; padding: 24px 0; }
.no-more { text-align: center; color: #94a3b8; padding: 24px 0; font-size: 24px; }
.empty { text-align: center; padding: 160px 0; }
.empty-icon { font-size: 64px; }
.empty-text { color: #94a3b8; font-size: 28px; margin-top: 16px; }
</style>
