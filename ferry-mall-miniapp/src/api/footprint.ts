import { request } from '@/utils/request'

export interface FootprintItem {
  id: number
  spuId: number
  spuName: string
  spuCover: string
  priceCent: number
  createdAt: string
}

export const getFootprints = () =>
  request<FootprintItem[]>('/app-api/member/footprint/list')

export const addFootprint = (spuId: number) =>
  request<FootprintItem>(`/app-api/member/footprint/add?spuId=${spuId}`, 'POST')
