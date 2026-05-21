import { defineStore } from 'pinia'
import { loginApi, profileApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({ token: localStorage.getItem('ferry-admin-token') || '', nickname: '' }),
  actions: {
    async login(username: string, password: string) {
      const data = await loginApi({ username, password })
      this.token = data.accessToken
      this.nickname = data.nickname
      localStorage.setItem('ferry-admin-token', data.accessToken)
    },
    async loadProfile() {
      const data = await profileApi()
      this.nickname = data.nickname
    }
  }
})
