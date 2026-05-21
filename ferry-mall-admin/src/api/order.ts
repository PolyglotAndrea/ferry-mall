import { request } from '@/utils/request'

export interface OrderItem {
  spuId: number
  skuId: number
  productName: string
  productImage: string
  priceCent: number
  quantity: number
  totalCent: number
}

export interface OrderDetail {
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
  remark: string
  logisticsCompany: string
  logisticsNo: string
  payTime: string
  deliveryTime: string
  receiveTime: string
  cancelTime: string
  cancelReason: string
  createdAt: string
  items: OrderItem[]
}

export interface PageResult<T> {
  list: T[]
  total: number
  pages: number
}

export const orderPageApi = (
  status?: number,
  keyword?: string,
  pageNo?: number,
  pageSize?: number
) =>
  request.get<unknown, PageResult<OrderDetail>>('/admin-api/order/page', {
    params: { status, keyword, pageNo, pageSize }
  })

export const getOrderDetailApi = (orderNo: string) =>
  request.get<unknown, OrderDetail>(`/admin-api/order/${orderNo}`)

export const deliverOrderApi = (
  orderNo: string,
  logisticsCompany: string,
  logisticsNo: string
) =>
  request.post<unknown, OrderDetail>(`/admin-api/order/${orderNo}/deliver`, {
    logisticsCompany,
    logisticsNo
  })
