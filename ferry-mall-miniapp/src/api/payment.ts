import { request } from '@/utils/request'

export interface PaymentPrepareResp {
  paymentNo: string
  channel: string
  mockPayload: string
}

export const preparePayment = (orderNo: string, channelCode?: string) =>
  request<PaymentPrepareResp>('/app-api/payment/prepare', 'POST', { orderNo, channelCode })
