import { request } from '@/utils/request'

export interface MerchantApplyReq { name: string; contactName: string; contactMobile: string; licenseNo?: string }
export interface MerchantResp { id: number; name: string; contactName: string; contactMobile: string; status: number; statusText: string }
export const applyMerchant = (data: MerchantApplyReq) => request<MerchantResp>('/app-api/merchant/apply', 'POST', data)
