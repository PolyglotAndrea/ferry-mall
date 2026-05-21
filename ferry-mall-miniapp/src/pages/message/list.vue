<template>
  <view class="message-page">
    <view class="message-list">
      <view v-for="m in messages" :key="m.id" class="message-card" :class="{ unread: m.isRead === 0 }" @tap="markRead(m.id)">
        <view class="msg-header">
          <text class="msg-title">{{ m.title }}</text>
          <text v-if="m.isRead === 0" class="msg-dot"></text>
        </view>
        <view class="msg-content">{{ m.content }}</view>
        <view class="msg-time">{{ m.createdAt?.slice(0, 16).replace('T', ' ') }}</view>
      </view>
      <view v-if="messages.length === 0" class="empty">暂无消息</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@tarojs/taro'
import { ref } from 'vue'
import { getMessages, markMessageRead, type MessageItem } from '@/api/member'

const messages = ref<MessageItem[]>([])

async function fetch() {
  try { messages.value = await getMessages() } catch { messages.value = [] }
}
onShow(() => fetch())

async function markRead(id: number) {
  try {
    await markMessageRead(id)
    const item = messages.value.find(m => m.id === id)
    if (item) item.isRead = 1
  } catch {}
}
</script>

<style scoped>
.message-page { min-height: 100vh; background: #f8fafc; padding: 20px; }
.message-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.message-card.unread { border-left: 4px solid #2563eb; }
.msg-header { display: flex; justify-content: space-between; align-items: center; }
.msg-title { font-size: 28px; font-weight: 700; }
.msg-dot { width: 16px; height: 16px; border-radius: 50%; background: #ef4444; }
.msg-content { font-size: 26px; color: #475569; margin-top: 12px; line-height: 1.5; }
.msg-time { font-size: 22px; color: #94a3b8; margin-top: 12px; }
.empty { text-align: center; color: #94a3b8; padding: 200px 0; }
</style>
