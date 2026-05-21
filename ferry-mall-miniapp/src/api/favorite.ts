import { request } from '@/utils/request'

export interface FavoriteItem {
  id: number
  spuId: number
  spuName: string
  spuCover: string
  priceCent: number
  createdAt: string
}

export const getFavorites = () =>
  request<FavoriteItem[]>('/app-api/member/favorite/list')

export const addFavorite = (spuId: number) =>
  request<FavoriteItem>(`/app-api/member/favorite/add?spuId=${spuId}`, 'POST')

export const removeFavorite = (spuId: number) =>
  request<boolean>(`/app-api/member/favorite/${spuId}`, 'DELETE')

export const checkFavorite = (spuId: number) =>
  request<boolean>(`/app-api/member/favorite/exists?spuId=${spuId}`)
