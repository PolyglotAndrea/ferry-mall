import { request } from '@/utils/request'

export interface AftermarketResp { id: number; orderId: number; reason: string; status: number; statusText: string }
export const applyAftermarket = (orderId: number, reason: string) => request<AftermarketResp>('/app-api/aftermarket/apply', 'POST', { orderId, reason })
