import { request } from '@/utils/request'

export interface CartItem {
  id: number
  spuId: number
  skuId?: number
  quantity: number
  selected: number
  createdAt: string
}

export const getCartList = () => request<CartItem[]>('/app-api/member/cart/list')

export const addCartItem = (spuId: number, skuId?: number, quantity = 1) =>
  request<CartItem>(`/app-api/member/cart/add?spuId=${spuId}${skuId ? '&skuId=' + skuId : ''}&quantity=${quantity}`, 'POST')

export const updateCartQuantity = (cartId: number, quantity: number) =>
  request<CartItem>(`/app-api/member/cart/${cartId}/quantity?quantity=${quantity}`, 'PUT')

export const removeCartItem = (cartId: number) =>
  request<boolean>(`/app-api/member/cart/${cartId}`, 'DELETE')

export const toggleCartSelect = (cartId: number, selected: number) =>
  request<boolean>(`/app-api/member/cart/${cartId}/select?selected=${selected}`, 'POST')

export const clearCart = () => request<boolean>('/app-api/member/cart/clear', 'POST')

export const selectAllCart = (selected: number) => request<boolean>(`/app-api/member/cart/select-all?selected=${selected}`, 'POST')

export const getCartCount = () => request<number>('/app-api/member/cart/count')
