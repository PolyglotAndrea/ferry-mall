import { request } from '@/utils/request'

export interface AftermarketResp {
  id: number
  orderId: number
  reason: string
  status: number
  statusText: string
}

export interface AftermarketApplyReq {
  orderId: number
  reason: string
}

export const applyAftermarket = (data: AftermarketApplyReq) =>
  request<AftermarketResp>('/app-api/aftermarket/apply', 'POST', data)
