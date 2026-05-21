import { request } from '@/utils/request'

export interface PointsProduct {
  id: number
  name: string
  coverUrl: string
  points: number
  stock: number
  sort: number
  status: number
}

export interface PointsExchange {
  id: number
  productId: number
  productName: string
  points: number
  status: number
  createdAt: string
}

export const getPointsProducts = () =>
  request<PointsProduct[]>('/app-api/member/points-mall/products')

export const exchangePointsProduct = (productId: number) =>
  request<PointsExchange>(`/app-api/member/points-mall/exchange?productId=${productId}`, 'POST')

export const getPointsExchanges = () =>
  request<PointsExchange[]>('/app-api/member/points-mall/exchanges')
