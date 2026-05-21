import { request } from '@/utils/request'

export interface SysUser { id: number; username: string; nickname: string; status: number }
export interface PageResult<T> { list: T[]; total: number; pages: number }

export const sysUserPageApi = () => request.get<unknown, PageResult<SysUser>>('/admin-api/system/user/page')
