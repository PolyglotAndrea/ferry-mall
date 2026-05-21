import axios, { type AxiosRequestConfig } from 'axios'

export interface CommonResult<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

const client = axios.create({ baseURL: '', timeout: 10_000 })

function unwrap<T>(body: CommonResult<T>): T {
  if (body.code !== 200) throw new Error(body.message)
  return body.data
}

export const request = {
  async get<T = unknown, R = T>(url: string, config?: AxiosRequestConfig): Promise<R> {
    const response = await client.get<CommonResult<R>>(url, config)
    return unwrap(response.data)
  },
  async post<T = unknown, R = T>(url: string, data?: T, config?: AxiosRequestConfig): Promise<R> {
    const response = await client.post<CommonResult<R>>(url, data, config)
    return unwrap(response.data)
  },
  async put<T = unknown, R = T>(url: string, data?: T, config?: AxiosRequestConfig): Promise<R> {
    const response = await client.put<CommonResult<R>>(url, data, config)
    return unwrap(response.data)
  },
  async delete<T = unknown, R = T>(url: string, config?: AxiosRequestConfig): Promise<R> {
    const response = await client.delete<CommonResult<R>>(url, config)
    return unwrap(response.data)
  }
}
