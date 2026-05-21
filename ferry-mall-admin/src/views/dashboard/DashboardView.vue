<template>
  <div v-loading="loading">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in statCards" :key="item.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-header">
            <div class="stat-icon" :style="{ background: item.iconBg }">
              <el-icon :size="24" color="#fff">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.formatValue }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <span class="trend" :class="item.trend >= 0 ? 'up' : 'down'">
              <el-icon>
                <ArrowUp v-if="item.trend >= 0" />
                <ArrowDown v-else />
              </el-icon>
              {{ Math.abs(item.trend) }}%
            </span>
            <span class="trend-label">较上周</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 / 待处理事项 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in pendingCards" :key="item.label">
        <el-card class="pending-card" shadow="hover" @click="goTo(item.path)">
          <div class="pending-content">
            <div class="pending-icon" :style="{ background: item.iconBg }">
              <el-icon :size="20" color="#fff">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="pending-info">
              <div class="pending-label">{{ item.label }}</div>
              <div class="pending-value">{{ item.value }}</div>
            </div>
            <el-icon class="pending-arrow"><ArrowRight /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势图 + 热销排行 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>近7日销售趋势</span>
              <el-radio-group v-model="chartType" size="small">
                <el-radio-button label="amount">销售额</el-radio-button>
                <el-radio-button label="count">订单量</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" class="chart-container" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="rank-card">
          <template #header>
            <div class="card-header">
              <span>热销商品 TOP10</span>
            </div>
          </template>
          <div class="rank-list">
            <div
              v-for="(item, index) in topProducts"
              :key="item.name"
              class="rank-item"
            >
              <div class="rank-index" :class="{ top: index < 3 }">{{ index + 1 }}</div>
              <div class="rank-name" :title="item.name">{{ item.name }}</div>
              <div class="rank-bar-wrap">
                <div class="rank-bar" :style="{ width: rankBarWidth(item.totalQuantity) + '%' }" />
              </div>
              <div class="rank-qty">{{ item.totalQuantity }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-row style="margin-top: 16px">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
              <el-link type="primary" @click="$router.push('/order/list')">查看更多</el-link>
            </div>
          </template>
          <el-table :data="recentOrders" size="small" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="160" />
            <el-table-column label="实付金额" width="120">
              <template #default="{ row }">
                ¥{{ (row.payAmountCent / 100).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="statusText" label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="orderStatusType(row.status)">{{ row.statusText }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="receiverName" label="收货人" width="120" />
            <el-table-column label="下单时间" width="160">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Document,
  Money,
  User,
  Goods,
  ShoppingCart,
  Service,
  CreditCard,
  Wallet,
  ArrowUp,
  ArrowDown,
  ArrowRight
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  overviewApi,
  topProductsApi,
  dailySalesApi,
  pendingCountApi,
  type Overview,
  type ProductRank,
  type DailySale,
  type PendingCount
} from '@/api/statistics'
import { orderPageApi, type OrderDetail } from '@/api/order'

const router = useRouter()
const loading = ref(false)

// 统计数据
const overview = ref<Overview>({
  orderCount: 0,
  salesAmountCent: 0,
  memberCount: 0,
  productCount: 0,
  merchantCount: 0
})
const pending = ref<PendingCount>({
  pendingShip: 0,
  pendingAftermarket: 0,
  pendingPayment: 0,
  pendingSettlement: 0
})
const topProducts = ref<ProductRank[]>([])
const dailySales = ref<DailySale[]>([])
const recentOrders = ref<OrderDetail[]>([])

// 图表
const chartRef = ref<HTMLDivElement | null>(null)
let chartInstance: echarts.ECharts | null = null
const chartType = ref<'amount' | 'count'>('amount')

const statCards = computed(() => [
  {
    label: '订单总数',
    value: overview.value.orderCount,
    formatValue: overview.value.orderCount.toLocaleString(),
    icon: 'Document',
    iconBg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    trend: 12.5
  },
  {
    label: '销售总额',
    value: overview.value.salesAmountCent,
    formatValue: '¥' + (overview.value.salesAmountCent / 100).toFixed(2),
    icon: 'Money',
    iconBg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    trend: 8.3
  },
  {
    label: '会员总数',
    value: overview.value.memberCount,
    formatValue: overview.value.memberCount.toLocaleString(),
    icon: 'User',
    iconBg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    trend: 5.2
  },
  {
    label: '商品总数',
    value: overview.value.productCount,
    formatValue: overview.value.productCount.toLocaleString(),
    icon: 'Goods',
    iconBg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    trend: -2.1
  }
])

const pendingCards = computed(() => [
  {
    label: '待发货订单',
    value: pending.value.pendingShip,
    icon: 'ShoppingCart',
    iconBg: '#ff6b6b',
    path: '/order/list'
  },
  {
    label: '待审核售后',
    value: pending.value.pendingAftermarket,
    icon: 'Service',
    iconBg: '#feca57',
    path: '/aftermarket/list'
  },
  {
    label: '待支付订单',
    value: pending.value.pendingPayment,
    icon: 'CreditCard',
    iconBg: '#48dbfb',
    path: '/order/list'
  },
  {
    label: '待结算账单',
    value: pending.value.pendingSettlement,
    icon: 'Wallet',
    iconBg: '#1dd1a1',
    path: '/settlement/bill'
  }
])

const maxRankQty = computed(() => {
  if (!topProducts.value.length) return 1
  return Math.max(...topProducts.value.map(p => p.totalQuantity))
})

function rankBarWidth(qty: number) {
  if (maxRankQty.value === 0) return 0
  return (qty / maxRankQty.value) * 100
}

function goTo(path: string) {
  router.push(path)
}

function orderStatusType(status: number): string {
  switch (status) {
    case 10: return 'warning'
    case 20: return 'primary'
    case 30: return 'success'
    case 40: return 'success'
    case 50: return 'info'
    case 60: return 'danger'
    case 70: return 'info'
    default: return 'info'
  }
}

function formatDate(dateStr: string | undefined): string {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChartOption()
  const resizeHandler = () => chartInstance?.resize()
  window.addEventListener('resize', resizeHandler)
}

function updateChartOption() {
  if (!chartInstance) return
  const isAmount = chartType.value === 'amount'
  const xData = dailySales.value.map(d => d.day.slice(5)) // MM-DD
  const yData = dailySales.value.map(d =>
    isAmount ? Number((d.amount_cent / 100).toFixed(2)) : d.order_count
  )

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const p = params[0]
        const val = isAmount ? '¥' + p.value.toFixed(2) : p.value + '单'
        return `${p.name}<br/>${p.marker} ${val}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLine: { lineStyle: { color: '#e4e7ed' } },
      axisLabel: { color: '#606266' },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { color: '#606266' }
    },
    series: [
      {
        type: 'bar',
        data: yData,
        barWidth: '40%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: isAmount ? '#667eea' : '#43e97b' },
            { offset: 1, color: isAmount ? '#764ba2' : '#38f9d7' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: isAmount ? '#5a6fd6' : '#3bd46e' },
              { offset: 1, color: isAmount ? '#6a4190' : '#32e0c8' }
            ])
          }
        }
      }
    ]
  }
  chartInstance.setOption(option, true)
}

watch(chartType, () => {
  updateChartOption()
})

watch(dailySales, () => {
  nextTick(() => updateChartOption())
})

async function fetchAll() {
  loading.value = true
  try {
    const [ov, pen, top, daily, orders] = await Promise.all([
      overviewApi().catch(e => { ElMessage.error('获取概览数据失败: ' + (e as Error).message); return null }),
      pendingCountApi().catch(e => { ElMessage.error('获取待处理数据失败: ' + (e as Error).message); return null }),
      topProductsApi(10).catch(e => { ElMessage.error('获取热销商品失败: ' + (e as Error).message); return [] }),
      dailySalesApi(7).catch(e => { ElMessage.error('获取销售趋势失败: ' + (e as Error).message); return [] }),
      orderPageApi().catch(e => { ElMessage.error('获取订单列表失败: ' + (e as Error).message); return { list: [], total: 0, pages: 0 } })
    ])
    if (ov) overview.value = ov
    if (pen) pending.value = pen
    topProducts.value = top
    dailySales.value = daily
    recentOrders.value = orders.list.slice(0, 5)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAll()
  nextTick(() => initChart())
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 16px;
  border-radius: 12px;
}
.stat-card :deep(.el-card__body) {
  padding: 20px;
}
.stat-header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
  min-width: 0;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.stat-footer {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}
.trend {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-weight: 500;
}
.trend.up {
  color: #67c23a;
}
.trend.down {
  color: #f56c6c;
}
.trend-label {
  color: #909399;
}

.pending-card {
  margin-bottom: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}
.pending-card:hover {
  transform: translateY(-2px);
}
.pending-card :deep(.el-card__body) {
  padding: 16px 20px;
}
.pending-content {
  display: flex;
  align-items: center;
  gap: 12px;
}
.pending-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.pending-info {
  flex: 1;
  min-width: 0;
}
.pending-label {
  font-size: 13px;
  color: #909399;
}
.pending-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.pending-arrow {
  color: #c0c4cc;
}

.chart-card {
  border-radius: 12px;
}
.chart-card :deep(.el-card__body) {
  padding: 10px 20px 20px;
}
.chart-container {
  width: 100%;
  height: 320px;
}

.rank-card {
  border-radius: 12px;
}
.rank-card :deep(.el-card__body) {
  padding: 10px 20px 20px;
}
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.rank-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}
.rank-index {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  background: #f0f2f5;
  color: #606266;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.rank-index.top {
  background: #fef0f0;
  color: #f56c6c;
}
.rank-name {
  flex: 1;
  min-width: 0;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rank-bar-wrap {
  width: 80px;
  height: 6px;
  background: #f0f2f5;
  border-radius: 3px;
  overflow: hidden;
  flex-shrink: 0;
}
.rank-bar {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: width 0.5s ease;
}
.rank-qty {
  width: 40px;
  text-align: right;
  color: #606266;
  font-weight: 500;
  flex-shrink: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}
</style>
