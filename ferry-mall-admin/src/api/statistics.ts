import { request } from '@/utils/request'

export interface Overview { orderCount: number; salesAmountCent: number; memberCount: number; productCount: number }
export const overviewApi = () => request.get<unknown, Overview>('/admin-api/statistics/overview')
