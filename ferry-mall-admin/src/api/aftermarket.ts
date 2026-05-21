import { request } from '@/utils/request'

export interface AftermarketRecord {
  id: number
  orderId: number
  reason: string
  status: number
  statusText: string
}
export interface PageResult<T> { list: T[]; total: number; pages: number }
export const aftermarketPageApi = () => request.get<unknown, PageResult<AftermarketRecord>>('/admin-api/aftermarket/page')
