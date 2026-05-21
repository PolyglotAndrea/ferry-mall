import { request } from '@/utils/request'

export interface LoginReq { username: string; password: string }
export interface LoginResp { accessToken: string; nickname: string }
export interface ProfileResp { id: number; username: string; nickname: string; permissions: string[] }

export const loginApi = (data: LoginReq) => request.post<unknown, LoginResp>('/admin-api/system/auth/login', data)
export const profileApi = () => request.get<unknown, ProfileResp>('/admin-api/system/auth/profile')
