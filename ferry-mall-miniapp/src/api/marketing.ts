import { request } from '@/utils/request'
import type { PageResult } from '@/api/product'

export interface SeckillActivity {
  id: number
  name: string
  startTime: string
  endTime: string
  status: number
}

export interface SeckillProduct {
  id: number
  activityId: number
  spuId: number
  seckillPriceCent: number
  stock: number
  sold: number
  status: number
}

export interface GrouponActivity {
  id: number
  name: string
  spuId: number
  grouponPriceCent: number
  requireCount: number
  startTime: string
  endTime: string
  status: number
}

export interface BargainActivity {
  id: number
  name: string
  spuId: number
  originalPriceCent: number
  floorPriceCent: number
  startTime: string
  endTime: string
  status: number
}

export interface BargainRecord {
  id: number
  activityId: number
  memberId: number
  currentPriceCent: number
  status: number
  createdAt: string
}

export const getSeckillActivities = () =>
  request<SeckillActivity[]>('/app-api/marketing/seckill/activities')

export const getSeckillProducts = (activityId: number) =>
  request<SeckillProduct[]>(`/app-api/marketing/seckill/${activityId}/products`)

export const getSeckillProductDetail = (productId: number) =>
  request<SeckillProduct>(`/app-api/marketing/seckill/product/${productId}`)

export const getGrouponActivities = () =>
  request<GrouponActivity[]>('/app-api/marketing/groupon/activities')

export const getGrouponDetail = (activityId: number) =>
  request<GrouponActivity>(`/app-api/marketing/groupon/${activityId}`)

export const joinGroupon = (activityId: number) =>
  request<boolean>(`/app-api/marketing/groupon/${activityId}/join`, 'POST')

export const getBargainActivities = () =>
  request<BargainActivity[]>('/app-api/marketing/bargain/activities')

export const startBargain = (activityId: number) =>
  request<BargainRecord>(`/app-api/marketing/bargain/${activityId}/start`, 'POST')

export const helpBargain = (recordId: number) =>
  request<BargainRecord>(`/app-api/marketing/bargain/${recordId}/help`, 'POST')

export const getBargainRecord = (recordId: number) =>
  request<BargainRecord>(`/app-api/marketing/bargain/record/${recordId}`)
