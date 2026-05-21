import { request } from '@/utils/request'
import type { PageResult } from '@/api/product'

export interface MemberProfile { id: number; nickname: string; avatarUrl: string; points: number }
export interface MemberLoginResp { accessToken: string; memberId: number }
export interface IntegralRecord {
  id: number
  changeCount: number
  currentPoints: number
  reason: string
  type: number
  createdAt: string
}
export interface MemberLevel {
  id: number
  name: string
  minPoints: number
  discountPercent: number
  status: number
}
export interface FavoriteItem {
  id: number
  spuId: number
  spuName: string
  spuCover: string
  priceCent: number
  createdAt: string
}
export interface FootprintItem {
  id: number
  spuId: number
  spuName: string
  spuCover: string
  priceCent: number
  createdAt: string
}
export interface MessageItem {
  id: number
  title: string
  content: string
  type: number
  isRead: number
  createdAt: string
}

export const memberLogin = (code: string) =>
  request<MemberLoginResp>('/app-api/member/auth/login', 'POST', { code })

export const getMemberProfile = () =>
  request<MemberProfile>('/app-api/member/profile')

export const memberSign = () =>
  request<number>('/app-api/member/sign', 'POST')

export const getIntegralRecords = (pageNo = 1, pageSize = 20) =>
  request<PageResult<IntegralRecord>>(`/app-api/member/integral/records?pageNo=${pageNo}&pageSize=${pageSize}`)

export const getMemberLevelList = () =>
  request<MemberLevel[]>('/app-api/member/level/list')

export const getCurrentMemberLevel = () =>
  request<MemberLevel>('/app-api/member/level/current')

export const getFavorites = () =>
  request<FavoriteItem[]>('/app-api/member/favorite/list')

export const addFavorite = (spuId: number) =>
  request<FavoriteItem>(`/app-api/member/favorite/add?spuId=${spuId}`, 'POST')

export const removeFavorite = (spuId: number) =>
  request<boolean>(`/app-api/member/favorite/${spuId}`, 'DELETE')

export const checkFavorite = (spuId: number) =>
  request<boolean>(`/app-api/member/favorite/exists?spuId=${spuId}`)

export const getFootprints = () =>
  request<FootprintItem[]>('/app-api/member/footprint/list')

export const addFootprint = (spuId: number) =>
  request<FootprintItem>(`/app-api/member/footprint/add?spuId=${spuId}`, 'POST')

export const getMessages = () =>
  request<MessageItem[]>('/app-api/member/message/list')

export const markMessageRead = (id: number) =>
  request<boolean>(`/app-api/member/message/${id}/read`, 'POST')

export const getUnreadMessageCount = () =>
  request<number>('/app-api/member/message/unread-count')

export const sendBindCode = (mobile: string) =>
  request<boolean>(`/app-api/member/bind/send-code?mobile=${mobile}`, 'POST')

export const bindPhone = (mobile: string, code: string) =>
  request<boolean>('/app-api/member/bind/phone', 'POST', { mobile, code })
