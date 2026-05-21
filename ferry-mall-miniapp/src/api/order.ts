import { request } from '@/utils/request'

export interface OrderItemResp {
  spuId: number
  skuId: number | null
  productName: string
  productImage: string
  priceCent: number
  quantity: number
  totalCent: number
}

export interface OrderResp {
  id: number
  orderNo: string
  totalAmountCent: number
  discountAmountCent: number
  payAmountCent: number
  status: number
  statusText: string
  receiverName: string
  receiverMobile: string
  receiverAddress: string
  remark: string | null
  payTime: string | null
  deliveryTime: string | null
  receiveTime: string | null
  createdAt: string
  items: OrderItemResp[]
}

export interface PageResult<T> { list: T[]; total: number; pages: number }

export const createOrder = (items: { spuId: number; skuId?: number; quantity: number }[],
  receiverName: string, receiverMobile: string, receiverAddress: string, remark?: string) =>
  request<OrderResp>('/app-api/order/create', 'POST', {
    items,
    receiverName,
    receiverMobile,
    receiverAddress,
    remark
  })

export const getOrderPage = (status?: number, pageNo = 1, pageSize = 10, keyword?: string) => {
  const qs = new URLSearchParams()
  qs.append('pageNo', String(pageNo))
  qs.append('pageSize', String(pageSize))
  if (status !== undefined) qs.append('status', String(status))
  if (keyword) qs.append('keyword', keyword)
  return request<PageResult<OrderResp>>(`/app-api/order/page?${qs.toString()}`)
}

export const getOrderDetail = (orderNo: string) =>
  request<OrderResp>(`/app-api/order/${orderNo}`)

export const cancelOrder = (orderNo: string, reason?: string) =>
  request<OrderResp>(`/app-api/order/${orderNo}/cancel`, 'POST', { reason })

export const confirmReceive = (orderNo: string) =>
  request<OrderResp>(`/app-api/order/${orderNo}/receive`, 'POST')
