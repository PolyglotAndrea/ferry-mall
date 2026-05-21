import { request } from '@/utils/request'
import type { PageResult } from '@/api/product'

export interface CommissionUser {
  id: number
  memberId: number
  parentId: number
  totalCommissionCent: number
  availableCommissionCent: number
  status: number
  createdAt: string
}

export interface CommissionRecord {
  id: number
  orderNo: string
  commissionCent: number
  type: number
  status: number
  createdAt: string
}

export interface TeamMember {
  id: number
  memberId: number
  totalCommissionCent: number
  createdAt: string
}

export const applyCommission = () =>
  request<CommissionUser>('/app-api/member/commission/apply', 'POST')

export const getCommissionInfo = () =>
  request<CommissionUser>('/app-api/member/commission/info')

export const getCommissionRecords = (pageNo = 1, pageSize = 20) =>
  request<PageResult<CommissionRecord>>(`/app-api/member/commission/records?pageNo=${pageNo}&pageSize=${pageSize}`)

export const getTeamList = () =>
  request<TeamMember[]>('/app-api/member/commission/team')

export interface CommissionOverview {
  totalCent: number
  availableCent: number
  teamSize: number
}

export const getCommissionOverview = () =>
  request<CommissionOverview>('/app-api/member/commission/overview')

export const withdrawCommission = (amountCent: number) =>
  request<void>(`/app-api/member/commission/withdraw?amountCent=${amountCent}`, 'POST')

export const bindParent = (parentId: number) =>
  request<void>(`/app-api/member/commission/bind-parent?parentId=${parentId}`, 'POST')
