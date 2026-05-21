import { request } from '@/utils/request'

export interface Store {
  id: number
  merchantId: number
  name: string
  logoUrl: string
  description: string
  status: number
  score: number
}
export interface PageResult<T> { list: T[]; total: number; pages: number }
export const storePageApi = () => request.get<unknown, PageResult<Store>>('/admin-api/store/page')
