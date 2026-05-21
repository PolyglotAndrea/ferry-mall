<template>
  <view class="trace-page">
    <!-- 顶部物流信息卡片 -->
    <view class="header-card">
      <view class="product-row">
        <image :src="productImage || defaultImage" class="product-img" mode="aspectFill" />
        <view class="product-info">
          <view class="company">{{ trace?.company || 'Ferry Express' }}</view>
          <view class="logistics-no-row">
            <text class="logistics-no">{{ trace?.logisticsNo || logisticsNo }}</text>
            <text class="copy-btn" @tap="copyNo">复制</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 物流轨迹时间线 -->
    <view class="timeline-card">
      <view class="timeline-title">物流轨迹</view>
      <view v-if="trace && trace.traces.length > 0" class="timeline">
        <view
          v-for="(item, idx) in parsedTraces"
          :key="idx"
          class="timeline-item"
          :class="{ first: idx === 0 }"
        >
          <view class="time-column">
            <view class="time-date">{{ item.time.split(' ')[0] }}</view>
            <view class="time-clock">{{ item.time.split(' ')[1] || '' }}</view>
          </view>
          <view class="timeline-marker">
            <view class="dot" :class="{ active: idx === 0 }"></view>
            <view v-if="idx < parsedTraces.length - 1" class="line"></view>
          </view>
          <view class="content">
            <view class="trace-location" v-if="item.location">{{ item.location }}</view>
            <view class="trace-text" :class="{ active: idx === 0 }">{{ item.description }}</view>
          </view>
        </view>
      </view>
      <view v-else class="empty">
        <text class="empty-icon">&#x1F4EF;</text>
        <view class="empty-text">暂无物流信息</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { getLogisticsTrace, type LogisticsTraceResp } from '@/api/logistics'

const trace = ref<LogisticsTraceResp>()
const logisticsNo = ref('')
const productImage = ref('')
const defaultImage = 'https://dummyimage.com/120x120/e5e7eb/666&text=商品'

interface ParsedTrace {
  time: string
  location: string
  description: string
}

// 解析后端返回的 trace 字符串为结构化数据
// 支持格式: "[时间] 地点 - 描述" 或 "时间 地点 - 描述" 或纯文本
const parsedTraces = computed<ParsedTrace[]>(() => {
  if (!trace.value?.traces.length) return []
  return trace.value.traces.map((item: string) => {
    // 尝试匹配 [时间] 地点 - 描述
    const bracketMatch = item.match(/^\[(.+?)\]\s*(.+?)\s*-\s*(.+)$/)
    if (bracketMatch) {
      return {
        time: bracketMatch[1].trim(),
        location: bracketMatch[2].trim(),
        description: bracketMatch[3].trim()
      }
    }
    // 尝试匹配 时间 地点 - 描述
    const simpleMatch = item.match(/^(.+?)\s+(.+?)\s*-\s*(.+)$/)
    if (simpleMatch) {
      return {
        time: simpleMatch[1].trim(),
        location: simpleMatch[2].trim(),
        description: simpleMatch[3].trim()
      }
    }
    // 兜底：整句作为描述，时间为空
    return {
      time: '',
      location: '',
      description: item
    }
  })
})

useLoad(async (query) => {
  const no = query.logisticsNo as string
  const img = query.productImage as string
  if (no) {
    logisticsNo.value = no
    if (img) productImage.value = img
    try {
      trace.value = await getLogisticsTrace(no)
    } catch {
      trace.value = undefined
    }
  }
})

function copyNo() {
  const no = trace.value?.logisticsNo || logisticsNo.value
  if (no) {
    Taro.setClipboardData({ data: no })
  }
}
</script>

<style scoped>
.trace-page { min-height: 100vh; background: #f8fafc; padding: 20px; padding-bottom: 40px; }

/* 顶部卡片 */
.header-card { padding: 24px; background: #fff; border-radius: 16px; margin-bottom: 20px; }
.product-row { display: flex; align-items: center; gap: 20px; }
.product-img { width: 120px; height: 120px; border-radius: 12px; background: #f1f5f9; flex-shrink: 0; }
.product-info { flex: 1; min-width: 0; }
.company { font-size: 30px; font-weight: 700; color: #1e293b; }
.logistics-no-row { display: flex; align-items: center; gap: 16px; margin-top: 12px; }
.logistics-no { font-size: 26px; color: #64748b; }
.copy-btn { padding: 6px 20px; border: 1px solid #2563eb; color: #2563eb; border-radius: 24px; font-size: 22px; }

/* 时间线卡片 */
.timeline-card { padding: 24px; background: #fff; border-radius: 16px; }
.timeline-title { font-size: 30px; font-weight: 700; margin-bottom: 24px; color: #1e293b; }

/* 时间线 */
.timeline { }
.timeline-item { display: flex; position: relative; padding-bottom: 32px; }
.timeline-item.first .trace-text { color: #2563eb; font-weight: 600; }
.timeline-item.first .trace-location { color: #2563eb; font-weight: 600; }

/* 时间列 */
.time-column { width: 120px; flex-shrink: 0; text-align: right; padding-right: 16px; }
.time-date { font-size: 24px; color: #64748b; }
.time-clock { font-size: 22px; color: #94a3b8; margin-top: 4px; }

/* 标记点 */
.timeline-marker { display: flex; flex-direction: column; align-items: center; position: relative; width: 24px; flex-shrink: 0; }
.dot { width: 16px; height: 16px; border-radius: 50%; background: #cbd5e1; margin-top: 6px; flex-shrink: 0; }
.dot.active { background: #2563eb; }
.line { position: absolute; left: 11px; top: 28px; width: 2px; height: calc(100% - 8px); background: #e2e8f0; }

/* 内容区 */
.content { margin-left: 16px; flex: 1; min-width: 0; }
.trace-location { font-size: 26px; color: #475569; margin-bottom: 4px; }
.trace-text { font-size: 26px; color: #475569; line-height: 1.5; }
.trace-text.active { color: #2563eb; }

/* 空状态 */
.empty { text-align: center; padding: 80px 0; }
.empty-icon { font-size: 64px; }
.empty-text { color: #94a3b8; font-size: 28px; margin-top: 16px; }
</style>
