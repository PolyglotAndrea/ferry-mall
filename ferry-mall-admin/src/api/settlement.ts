import { request } from '@/utils/request'

export interface SettlementBill {
  id: number
  merchantId: number
  merchantName: string
  orderAmountCent: number
  commissionCent: number
  payableCent: number
  status: number
  statusText: string
}
export interface PageResult<T> { list: T[]; total: number; pages: number }
export const settlementBillPageApi = () => request.get<unknown, PageResult<SettlementBill>>('/admin-api/settlement/bill/page')
