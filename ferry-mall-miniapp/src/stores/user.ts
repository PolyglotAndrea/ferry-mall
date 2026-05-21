import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Taro from '@tarojs/taro'
import { getMemberProfile, memberLogin, memberSign } from '@/api/member'
import { setToken, getToken, removeToken } from '@/utils/request'
import type { MemberProfile } from '@/api/member'

export const useUserStore = defineStore('user', () => {
  const profile = ref<MemberProfile | null>(null)
  const isLoggedIn = computed(() => !!profile.value)

  async function wxLogin(): Promise<boolean> {
    try {
      const { code } = await Taro.login({} as any)
      const resp = await memberLogin(code)
      setToken(resp.accessToken)
      await fetchProfile()
      return true
    } catch (e: any) {
      Taro.showToast({ title: e.message || '登录失败', icon: 'none' })
      return false
    }
  }

  async function fetchProfile() {
    try {
      profile.value = await getMemberProfile()
    } catch {
      profile.value = null
    }
  }

  async function checkLogin(): Promise<boolean> {
    const token = getToken()
    if (!token) return false
    await fetchProfile()
    return isLoggedIn.value
  }

  async function sign() {
    try {
      const points = await memberSign()
      if (profile.value) profile.value.points += points
      Taro.showToast({ title: `签到成功 +${points}积分`, icon: 'success' })
    } catch (e: any) {
      Taro.showToast({ title: e.message || '签到失败', icon: 'none' })
    }
  }

  function logout() {
    removeToken()
    profile.value = null
  }

  return { profile, isLoggedIn, wxLogin, fetchProfile, checkLogin, sign, logout }
})
