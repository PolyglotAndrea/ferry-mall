import { request } from '@/utils/request'

export interface Coupon { id: number; name: string; discountCent: number; thresholdCent: number; status: number }
export interface PageResult<T> { list: T[]; total: number; pages: number }

export const couponPageApi = () => request.get<unknown, PageResult<Coupon>>('/admin-api/marketing/coupon/page')
