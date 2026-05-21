<template>
  <view class="trace-page">
    <view v-if="trace" class="info-card">
      <view class="company-row">
        <text class="company">{{ trace.company }}</text>
        <text class="copy" @tap="copyNo">复制单号</text>
      </view>
      <view class="logistics-no">{{ trace.logisticsNo }}</view>
    </view>
    <view v-if="trace && trace.traces.length > 0" class="timeline">
      <view v-for="(item, idx) in trace.traces" :key="idx" class="timeline-item"
        :class="{ first: idx === 0 }">
        <view class="dot" :class="{ active: idx === 0 }"></view>
        <view class="line" v-if="idx < trace.traces.length - 1"></view>
        <view class="content">
          <view class="trace-text" :class="{ active: idx === 0 }">{{ item }}</view>
        </view>
      </view>
    </view>
    <view v-else class="empty">暂无物流信息</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref } from 'vue'
import { getLogisticsTrace, type LogisticsTraceResp } from '@/api/logistics'

const trace = ref<LogisticsTraceResp>()

useLoad(async (query) => {
  const no = query.logisticsNo as string
  if (no) {
    try { trace.value = await getLogisticsTrace(no) } catch { trace.value = undefined }
  }
})

function copyNo() {
  if (trace.value) {
    Taro.setClipboardData({ data: trace.value.logisticsNo })
  }
}
</script>

<style scoped>
.trace-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.info-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 20px; }
.company-row { display: flex; justify-content: space-between; align-items: center; }
.company { font-size: 30px; font-weight: 700; }
.copy { font-size: 24px; color: #2563eb; }
.logistics-no { font-size: 26px; color: #64748b; margin-top: 12px; }
.timeline { padding: 24px; background: #fff; border-radius: 16px; }
.timeline-item { display: flex; position: relative; padding-bottom: 32px; }
.timeline-item.first .trace-text { color: #2563eb; font-weight: 600; }
.dot { width: 16px; height: 16px; border-radius: 50%; background: #cbd5e1; margin-top: 6px; flex-shrink: 0; }
.dot.active { background: #2563eb; }
.line { position: absolute; left: 7px; top: 28px; width: 2px; height: calc(100% - 8px); background: #e2e8f0; }
.content { margin-left: 20px; flex: 1; }
.trace-text { font-size: 26px; color: #475569; line-height: 1.5; }
.empty { text-align: center; color: #94a3b8; padding: 120px 0; font-size: 28px; }
</style>
