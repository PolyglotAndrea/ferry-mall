import { request } from '@/utils/request'
import type { PageResult } from '@/api/order'

export type MessageType = 1 | 2 | 3

export interface MessageItem {
  id: number
  title: string
  content: string
  type: MessageType
  isRead: number
  createdAt: string
}

export const getMessages = (pageNo = 1, pageSize = 20) =>
  request<PageResult<MessageItem>>(`/app-api/member/message/list?pageNo=${pageNo}&pageSize=${pageSize}`)

export const markMessageRead = (id: number) =>
  request<void>(`/app-api/member/message/${id}/read`, 'POST')

export const getUnreadMessageCount = () =>
  request<number>('/app-api/member/message/unread-count')
