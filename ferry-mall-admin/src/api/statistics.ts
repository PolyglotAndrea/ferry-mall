import { request } from '@/utils/request'

export interface Overview {
  orderCount: number
  salesAmountCent: number
  memberCount: number
  productCount: number
  merchantCount: number
}

export interface ProductRank {
  name: string
  totalQuantity: number
  totalAmountCent: number
}

export interface DailySale {
  day: string
  order_count: number
  amount_cent: number
}

export interface PendingCount {
  pendingShip: number
  pendingAftermarket: number
  pendingPayment: number
  pendingSettlement: number
}

export const overviewApi = () => request.get<unknown, Overview>('/admin-api/statistics/overview')

export const topProductsApi = (limit = 10) =>
  request.get<unknown, ProductRank[]>('/admin-api/statistics/top-products', { params: { limit } })

export const dailySalesApi = (days = 7) =>
  request.get<unknown, DailySale[]>('/admin-api/statistics/daily-sales', { params: { days } })

export const pendingCountApi = () =>
  request.get<unknown, PendingCount>('/admin-api/statistics/pending-count')
