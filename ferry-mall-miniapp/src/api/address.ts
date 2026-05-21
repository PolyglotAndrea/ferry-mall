import { request } from '@/utils/request'

export interface AddressItem {
  id: number
  name: string
  mobile: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: number
}

export const getAddressList = () =>
  request<AddressItem[]>('/app-api/member/address/list')

export const addAddress = (data: Omit<AddressItem, 'id'>) =>
  request<AddressItem>('/app-api/member/address/create', 'POST', data)

export const updateAddress = (id: number, data: Omit<AddressItem, 'id'>) =>
  request<AddressItem>(`/app-api/member/address/${id}/update`, 'PUT', data)

export const deleteAddress = (id: number) =>
  request<boolean>(`/app-api/member/address/${id}/delete`, 'DELETE')

export const setDefaultAddress = (id: number) =>
  request<boolean>(`/app-api/member/address/${id}/default`, 'POST')
