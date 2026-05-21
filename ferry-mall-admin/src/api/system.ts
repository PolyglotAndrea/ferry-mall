import { request } from '@/utils/request'

export interface SysUser { id: number; username: string; nickname: string; status: number; createdAt: string }
export interface PageResult<T> { list: T[]; total: number; pages: number }

export const sysUserPageApi = (keyword?: string, pageNo = 1, pageSize = 10) =>
  request.get<unknown, PageResult<SysUser>>('/admin-api/system/user/page', {
    params: { keyword, pageNo, pageSize }
  })

export const createSysUserApi = (data: { username: string; password: string; nickname: string; deptId?: number }) =>
  request.post<unknown, SysUser>('/admin-api/system/user/create', data)

export const updateSysUserApi = (id: number, data: { nickname: string; password?: string; deptId?: number }) =>
  request.put<unknown, SysUser>(`/admin-api/system/user/${id}/update`, data)

export const deleteSysUserApi = (id: number) =>
  request.delete<unknown, boolean>(`/admin-api/system/user/${id}`)

export const toggleSysUserStatusApi = (id: number) =>
  request.put<unknown, boolean>(`/admin-api/system/user/${id}/toggle-status`)

export const resetSysUserPasswordApi = (id: number) =>
  request.post<unknown, boolean>(`/admin-api/system/user/${id}/reset-password`)

export const assignUserRolesApi = (id: number, roleIds: number[]) =>
  request.post<unknown, boolean>(`/admin-api/system/user/${id}/assign-roles`, roleIds)

// Role
export interface Role {
  id: number
  name: string
  code: string
  dataScope: number
  status: number
}

export const rolePageApi = (pageNo = 1, pageSize = 10) =>
  request.get<unknown, PageResult<Role>>('/admin-api/system/role/page', {
    params: { pageNo, pageSize }
  })

export const createRoleApi = (data: { name: string; code: string; dataScope?: number }) =>
  request.post<unknown, Role>('/admin-api/system/role/create', data)

export const updateRoleApi = (id: number, data: { name: string; code: string; dataScope?: number }) =>
  request.put<unknown, Role>(`/admin-api/system/role/${id}/update`, data)

export const deleteRoleApi = (id: number) =>
  request.delete<unknown, boolean>(`/admin-api/system/role/${id}`)

export const assignRoleMenusApi = (id: number, menuIds: number[]) =>
  request.post<unknown, boolean>(`/admin-api/system/role/${id}/assign-menus`, menuIds)

// Menu
export interface MenuNode {
  id: number
  name: string
  permission: string
  type: number
  parentId: number
  sort: number
  path: string
  component: string
  icon: string
  status: number
  children: MenuNode[]
}

export const menuTreeApi = () =>
  request.get<unknown, MenuNode[]>('/admin-api/system/menu/tree')

export const menuListApi = () =>
  request.get<unknown, MenuNode[]>('/admin-api/system/menu/list')

export const createMenuApi = (data: {
  name: string; permission?: string; type?: number; parentId?: number; sort?: number; path?: string; component?: string; icon?: string
}) => request.post<unknown, MenuNode>('/admin-api/system/menu/create', data)

export const updateMenuApi = (id: number, data: {
  name: string; permission?: string; type?: number; parentId?: number; sort?: number; path?: string; component?: string; icon?: string
}) => request.put<unknown, MenuNode>(`/admin-api/system/menu/${id}/update`, data)

export const deleteMenuApi = (id: number) =>
  request.delete<unknown, boolean>(`/admin-api/system/menu/${id}`)

// Operate Log
export interface OperateLog {
  id: number
  userId: number
  module: string
  name: string
  type: number
  requestMethod: string
  requestUrl: string
  requestParams: string
  responseBody: string
  userIp: string
  duration: number
  result: number
  createdAt: string
}

export const operateLogPageApi = (
  module?: string,
  name?: string,
  result?: number,
  startTime?: string,
  endTime?: string,
  pageNo = 1,
  pageSize = 10
) =>
  request.get<unknown, PageResult<OperateLog>>('/admin-api/system/operate-log/page', {
    params: { module, name, result, startTime, endTime, pageNo, pageSize }
  })

export const cleanOperateLogApi = (before: string) =>
  request.post<unknown, number>('/admin-api/system/operate-log/clean', null, {
    params: { before }
  })

// File
export interface SysFile {
  id: number
  name: string
  path: string
  url: string
  contentType: string
  size: number
  createdAt: string
}

export interface FileResp {
  name: string
  url: string
  size: number
  contentType: string
}

export const filePageApi = (pageNo = 1, pageSize = 10) =>
  request.get<unknown, PageResult<SysFile>>('/admin-api/system/file/page', {
    params: { pageNo, pageSize }
  })

export const uploadFileApi = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<unknown, FileResp>('/admin-api/system/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const deleteFileApi = (id: number) =>
  request.delete<unknown, boolean>(`/admin-api/system/file/${id}`)
