import { request } from '@/utils/request'

export interface StoreResp { id: number; merchantId: number; name: string; logoUrl: string; description: string; status: number; score: number }

export interface LiveRoom {
  id: number
  name: string
  coverUrl: string
  anchorName: string
  streamUrl: string
  status: number
}

export const getStoreDetail = (id: number) => request<StoreResp>(`/app-api/store/${id}`)

export const getLiveRooms = () => request<LiveRoom[]>('/app-api/store/live/list')
