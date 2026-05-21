import { request } from '@/utils/request'
import type { PageResult } from '@/api/order'

export interface IntegralRecord {
  id: number
  changeCount: number
  currentPoints: number
  reason: string
  type: number
  createdAt: string
}

export const getIntegralRecords = (pageNo = 1, pageSize = 20) =>
  request<PageResult<IntegralRecord>>(`/app-api/member/integral/records?pageNo=${pageNo}&pageSize=${pageSize}`)
