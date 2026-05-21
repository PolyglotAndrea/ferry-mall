import { request } from '@/utils/request'

export interface ProductSpu {
  id: number
  categoryId: number
  name: string
  subtitle: string
  coverUrl: string
  priceCent: number
  marketPriceCent: number
  stock: number
  sales: number
}

export interface PageResult<T> { list: T[]; total: number; pages: number }

export const productPageApi = () => request.get<unknown, PageResult<ProductSpu>>('/admin-api/product/spu/page')
