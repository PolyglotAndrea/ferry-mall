import { request } from '@/utils/request'

export interface ProductSpu {
  id: number
  categoryId: number
  storeId: number
  name: string
  subtitle: string
  coverUrl: string
  priceCent: number
  marketPriceCent: number
  stock: number
  sales: number
  status: number
}

export interface PageResult<T> { list: T[]; total: number; pages: number }

export interface ProductCreateReq {
  categoryId: number
  name: string
  subtitle?: string
  coverUrl?: string
  priceCent: number
  marketPriceCent?: number
  stock: number
}

export interface ProductUpdateReq {
  categoryId: number
  name: string
  subtitle?: string
  coverUrl?: string
  priceCent: number
  marketPriceCent?: number
  stock: number
  status: number
}

export const productPageApi = (keyword?: string, categoryId?: number, pageNo = 1, pageSize = 10) =>
  request.get<unknown, PageResult<ProductSpu>>('/admin-api/product/spu/page', {
    params: { keyword, categoryId, pageNo, pageSize }
  })

export const createProductApi = (req: ProductCreateReq) =>
  request.post<unknown, ProductSpu>('/admin-api/product/spu/create', req)

export const updateProductApi = (id: number, req: ProductUpdateReq) =>
  request.put<unknown, ProductSpu>(`/admin-api/product/spu/${id}/update`, req)

export const deleteProductApi = (id: number) =>
  request.delete<unknown, boolean>(`/admin-api/product/spu/${id}`)

export const toggleProductStatusApi = (id: number) =>
  request.put<unknown, ProductSpu>(`/admin-api/product/spu/${id}/toggle-status`)
