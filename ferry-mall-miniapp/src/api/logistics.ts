import { request } from '@/utils/request'

export interface LogisticsTraceItem {
  time: string
  location: string
  description: string
}

export interface LogisticsTraceResp {
  logisticsNo: string
  company: string
  traces: string[]
  productImage?: string
}

export const getLogisticsTrace = (logisticsNo: string) =>
  request<LogisticsTraceResp>(`/app-api/logistics/trace?logisticsNo=${encodeURIComponent(logisticsNo)}`)
