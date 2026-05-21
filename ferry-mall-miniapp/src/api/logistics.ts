import { request } from '@/utils/request'

export interface LogisticsTraceResp { logisticsNo: string; company: string; traces: string[] }
export const getLogisticsTrace = (logisticsNo: string) => request<LogisticsTraceResp>(`/app-api/logistics/trace?logisticsNo=${logisticsNo}`)
