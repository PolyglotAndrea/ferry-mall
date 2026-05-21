import { request } from '@/utils/request'

export interface Merchant {
  id: number
  name: string
  contactName: string
  contactMobile: string
  status: number
  statusText: string
}
export interface PageResult<T> { list: T[]; total: number; pages: number }
export const merchantPageApi = () => request.get<unknown, PageResult<Merchant>>('/admin-api/merchant/page')
export const approveMerchantApi = (id: number) => request.post<unknown, Merchant>(`/admin-api/merchant/${id}/approve`)
