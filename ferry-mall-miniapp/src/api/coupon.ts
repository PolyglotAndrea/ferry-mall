import { request } from '@/utils/request'

export interface CouponResp {
  id: number
  name: string
  discountCent: number
  thresholdCent: number
}

export interface MemberCoupon {
  id: number
  couponId: number
  couponName: string
  discountCent: number
  thresholdCent: number
  status: number
  expireTime: string
}

export const getAvailableCoupons = () =>
  request<CouponResp[]>('/app-api/marketing/coupon/available')

export const receiveCoupon = (id: number) =>
  request<boolean>(`/app-api/marketing/coupon/${id}/receive`, 'POST')

export const getMyCoupons = () =>
  request<MemberCoupon[]>('/app-api/marketing/member-coupon/list')
