import { request } from '@/utils/request'

export interface OrderItem { id: number; orderNo: string; payAmountCent: number; status: number; statusText: string }
export interface PageResult<T> { list: T[]; total: number; pages: number }

export const orderPageApi = () => request.get<unknown, PageResult<OrderItem>>('/admin-api/order/page')
