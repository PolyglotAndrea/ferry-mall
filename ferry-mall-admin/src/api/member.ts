import { request } from '@/utils/request'

export interface MemberItem {
  id: number
  nickname: string
  avatar: string
  mobile: string
  level: string
  points: number
  orderCount: number
  totalSpendCent: number
  status: number
  createdAt: string
}

export interface MemberDetail {
  id: number
  nickname: string
  avatar: string
  mobile: string
  level: string
  levelId: number
  points: number
  orderCount: number
  totalSpendCent: number
  status: number
  createdAt: string
}

export interface IntegralRecord {
  id: number
  memberId: number
  changeCount: number
  currentPoints: number
  reason: string
  type: number
  createdAt: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  pages: number
}

export const memberPageApi = (keyword?: string, pageNo?: number, pageSize?: number) =>
  request.get<unknown, PageResult<MemberItem>>('/admin-api/member/page', {
    params: { keyword, pageNo, pageSize }
  })

export const getMemberDetailApi = (id: number) =>
  request.get<unknown, MemberDetail>(`/admin-api/member/${id}`)

export const getMemberIntegralRecordsApi = (id: number, pageNo?: number, pageSize?: number) =>
  request.get<unknown, PageResult<IntegralRecord>>(`/admin-api/member/${id}/integral-records`, {
    params: { pageNo, pageSize }
  })
