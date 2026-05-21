<template>
  <div class="page-card">
    <h2>订单管理</h2>

    <!-- 状态筛选标签 -->
    <el-tabs v-model="activeStatus" @tab-change="onStatusChange">
      <el-tab-pane label="全部" :name="0" />
      <el-tab-pane label="待付款" :name="10" />
      <el-tab-pane label="待发货" :name="20" />
      <el-tab-pane label="待收货" :name="30" />
      <el-tab-pane label="已完成" :name="40" />
      <el-tab-pane label="已取消" :name="50" />
    </el-tabs>

    <!-- 搜索栏 -->
    <el-form :inline="true" class="search-form">
      <el-form-item label="订单号">
        <el-input
          v-model="keyword"
          placeholder="请输入订单号"
          clearable
          style="width: 260px"
          @keyup.enter="onSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 订单表格 -->
    <el-table
      :data="orders"
      row-key="id"
      style="margin-top: 12px"
      v-loading="loading"
    >
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column label="买家信息" width="160">
        <template #default="{ row }">
          <div>{{ row.receiverName }}</div>
          <div style="color: #999; font-size: 12px">{{ row.receiverMobile }}</div>
        </template>
      </el-table-column>
      <el-table-column label="商品" min-width="200">
        <template #default="{ row }">
          <div v-for="item in row.items" :key="item.spuId" class="product-cell">
            <el-image
              :src="item.productImage"
              fit="cover"
              style="width: 48px; height: 48px; border-radius: 4px; flex-shrink: 0"
            />
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-meta">
                ¥{{ (item.priceCent / 100).toFixed(2) }} x {{ item.quantity }}
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="实付金额" width="120">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold">
            ¥{{ (row.payAmountCent / 100).toFixed(2) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row.orderNo)">查看详情</el-button>
          <el-button
            v-if="row.status === 20"
            size="small"
            type="primary"
            @click="openDeliver(row.orderNo)"
          >发货</el-button>
          <el-button
            v-if="row.status === 10"
            size="small"
            type="danger"
            @click="cancelOrder(row.orderNo)"
          >取消订单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageNo"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      style="margin-top: 20px; justify-content: flex-end"
      @change="fetchOrders"
    />

    <!-- 订单详情抽屉 -->
    <el-drawer v-model="detailVisible" title="订单详情" size="600px">
      <div v-if="currentOrder" class="detail-content">
        <!-- 订单信息 -->
        <div class="detail-section">
          <div class="section-title">订单信息</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="statusTagType(currentOrder.status)">{{ currentOrder.statusText }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentOrder.createdAt }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.payTime" label="支付时间">{{ currentOrder.payTime }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.deliveryTime" label="发货时间">{{ currentOrder.deliveryTime }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.receiveTime" label="完成时间">{{ currentOrder.receiveTime }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.cancelTime" label="取消时间">{{ currentOrder.cancelTime }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.cancelReason" label="取消原因">{{ currentOrder.cancelReason }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 收货地址 -->
        <div class="detail-section">
          <div class="section-title">收货信息</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ currentOrder.receiverMobile }}</el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 商品列表 -->
        <div class="detail-section">
          <div class="section-title">商品明细</div>
          <div
            v-for="item in currentOrder.items"
            :key="item.spuId"
            class="detail-product"
          >
            <el-image
              :src="item.productImage"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0"
            />
            <div class="detail-product-info">
              <div class="detail-product-name">{{ item.productName }}</div>
              <div class="detail-product-meta">
                单价: ¥{{ (item.priceCent / 100).toFixed(2) }} &nbsp;|&nbsp;
                数量: {{ item.quantity }} &nbsp;|&nbsp;
                小计: ¥{{ (item.totalCent / 100).toFixed(2) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 金额明细 -->
        <div class="detail-section">
          <div class="section-title">金额明细</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="商品总额">¥{{ (currentOrder.totalAmountCent / 100).toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="优惠金额">-¥{{ (currentOrder.discountAmountCent / 100).toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="实付金额">
              <span style="color: #f56c6c; font-weight: bold">¥{{ (currentOrder.payAmountCent / 100).toFixed(2) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 物流信息 -->
        <div v-if="currentOrder.status >= 30 && currentOrder.logisticsCompany" class="detail-section">
          <div class="section-title">物流信息</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="物流公司">{{ currentOrder.logisticsCompany }}</el-descriptions-item>
            <el-descriptions-item label="物流单号">{{ currentOrder.logisticsNo }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-drawer>

    <!-- 发货弹窗 -->
    <el-dialog v-model="deliverVisible" title="订单发货" width="460px">
      <el-form :model="deliverForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ deliverForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="物流公司" required>
          <el-input v-model="deliverForm.logisticsCompany" placeholder="请输入物流公司名称" />
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="deliverForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeliver">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  orderPageApi,
  getOrderDetailApi,
  deliverOrderApi,
  type OrderDetail
} from '@/api/order'
import { request } from '@/utils/request'

const orders = ref<OrderDetail[]>([])
const loading = ref(false)
const activeStatus = ref(0)
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 详情抽屉
const detailVisible = ref(false)
const currentOrder = ref<OrderDetail | null>(null)

// 发货弹窗
const deliverVisible = ref(false)
const deliverForm = ref({
  orderNo: '',
  logisticsCompany: '',
  logisticsNo: ''
})

function statusTagType(status: number): string {
  switch (status) {
    case 10: return 'warning'
    case 20: return 'primary'
    case 30: return 'success'
    case 40: return 'success'
    case 50: return 'info'
    default: return 'info'
  }
}

async function fetchOrders() {
  loading.value = true
  try {
    const status = activeStatus.value === 0 ? undefined : activeStatus.value
    const res = await orderPageApi(
      status,
      keyword.value || undefined,
      pageNo.value,
      pageSize.value
    )
    orders.value = res.list
    total.value = res.total
  } catch (e: any) {
    ElMessage.error(e.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

function onStatusChange() {
  pageNo.value = 1
  fetchOrders()
}

function onSearch() {
  pageNo.value = 1
  fetchOrders()
}

function onReset() {
  keyword.value = ''
  activeStatus.value = 0
  pageNo.value = 1
  fetchOrders()
}

async function showDetail(orderNo: string) {
  try {
    currentOrder.value = await getOrderDetailApi(orderNo)
    detailVisible.value = true
  } catch (e: any) {
    ElMessage.error(e.message || '获取订单详情失败')
  }
}

function openDeliver(orderNo: string) {
  deliverForm.value = {
    orderNo,
    logisticsCompany: '',
    logisticsNo: ''
  }
  deliverVisible.value = true
}

async function confirmDeliver() {
  if (!deliverForm.value.logisticsCompany.trim()) {
    ElMessage.warning('请输入物流公司')
    return
  }
  if (!deliverForm.value.logisticsNo.trim()) {
    ElMessage.warning('请输入物流单号')
    return
  }
  try {
    await deliverOrderApi(
      deliverForm.value.orderNo,
      deliverForm.value.logisticsCompany.trim(),
      deliverForm.value.logisticsNo.trim()
    )
    ElMessage.success('发货成功')
    deliverVisible.value = false
    fetchOrders()
  } catch (e: any) {
    ElMessage.error(e.message || '发货失败')
  }
}

async function cancelOrder(orderNo: string) {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await request.post(`/admin-api/order/${orderNo}/cancel`, null)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '取消订单失败')
    }
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.search-form {
  margin-top: 16px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
}

.product-cell + .product-cell {
  border-top: 1px solid #f0f0f0;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.detail-content {
  padding: 0 8px;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}

.detail-product {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 8px;
}

.detail-product-info {
  flex: 1;
  min-width: 0;
}

.detail-product-name {
  font-size: 14px;
  color: #333;
}

.detail-product-meta {
  font-size: 12px;
  color: #666;
  margin-top: 6px;
}
</style>
