import { request } from '@/utils/request'

export interface BannerDO {
  id: number
  title: string
  imageUrl: string
  linkUrl: string
  sort: number
}

export interface CategoryDO {
  id: number
  name: string
  icon: string
  parentId: number
  sort: number
}

export interface SkuDO {
  id: number
  spuId: number
  name: string
  priceCent: number
  marketPriceCent: number
  stock: number
  imageUrl: string
  properties: Record<string, string>
}

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
  detailHtml?: string
  albumUrls?: string[]
  skus?: SkuDO[]
}

export interface ProductComment {
  id: number
  memberNickname: string
  memberAvatar: string
  content: string
  rating: number
  images?: string
  createdAt: string
}

export interface PageResult<T> { list: T[]; total: number; pages: number }

export const getBannerList = () =>
  request<BannerDO[]>('/app-api/product/banner/list')

export const getCategoryList = (parentId = 0) =>
  request<CategoryDO[]>(`/app-api/product/category/list?parentId=${parentId}`)

export const getProductPage = (params?: { keyword?: string; categoryId?: number; pageNo?: number; pageSize?: number; sort?: string }) => {
  const qs = new URLSearchParams()
  if (params?.keyword) qs.append('keyword', params.keyword)
  if (params?.categoryId) qs.append('categoryId', String(params.categoryId))
  if (params?.sort) qs.append('sort', params.sort)
  qs.append('pageNo', String(params?.pageNo ?? 1))
  qs.append('pageSize', String(params?.pageSize ?? 10))
  return request<PageResult<ProductSpu>>(`/app-api/product/spu/page?${qs.toString()}`)
}

export const getProductDetail = (id: number) =>
  request<ProductSpu>(`/app-api/product/spu/${id}`)

export const getProductSkuList = (spuId: number) =>
  request<SkuDO[]>(`/app-api/product/spu/${spuId}/sku`)

export const getProductComments = (spuId: number, pageNo = 1, pageSize = 10) =>
  request<PageResult<ProductComment>>(`/app-api/product/comment/page?spuId=${spuId}&pageNo=${pageNo}&pageSize=${pageSize}`)

export const createProductComment = (spuId: number, content: string, rating = 5) =>
  request<ProductComment>('/app-api/product/comment/create', 'POST', { spuId, content, rating })
