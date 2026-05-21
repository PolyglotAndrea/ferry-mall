import Taro from '@tarojs/taro'

const baseUrl = 'http://localhost:48080'
const TOKEN_KEY = 'ferry_access_token'

export interface CommonResult<T> { code: number; message: string; data: T; timestamp: number }

export function getToken(): string | null {
  return Taro.getStorageSync(TOKEN_KEY)
}

export function setToken(token: string) {
  Taro.setStorageSync(TOKEN_KEY, token)
}

export function removeToken() {
  Taro.removeStorageSync(TOKEN_KEY)
}

export async function request<T>(url: string, method: keyof Taro.request.Method = 'GET', data?: unknown): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const response = await Taro.request<CommonResult<T>>({
    url: baseUrl + url,
    method,
    data,
    header: headers
  })

  if (response.statusCode === 401) {
    removeToken()
    Taro.showToast({ title: '登录已过期', icon: 'none' })
    throw new Error('登录已过期')
  }

  if (response.data.code !== 200) {
    Taro.showToast({ title: response.data.message || '请求失败', icon: 'none' })
    throw new Error(response.data.message)
  }

  return response.data.data
}

export async function uploadFile(filePath: string): Promise<string> {
  const token = getToken()
  const resp = await Taro.uploadFile({
    url: baseUrl + '/admin-api/system/file/upload',
    filePath,
    name: 'file',
    header: token ? { Authorization: `Bearer ${token}` } : {}
  })
  const data = JSON.parse(resp.data) as CommonResult<{ url: string }>
  if (data.code !== 200) throw new Error(data.message)
  return data.data.url
}
