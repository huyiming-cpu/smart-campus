import { defineStore } from 'pinia'

// 安全解析 JSON，防止脏数据报错
function safeParse(str, fallback = {}) {
  try { return JSON.parse(str) || fallback }
  catch { return fallback }
}

export const useUserStore = defineStore('user', {
  state() {
    return {
      token: localStorage.getItem('token') || '',
      userInfo: safeParse(localStorage.getItem('userInfo')),
      role: localStorage.getItem('role') || ''
    }
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(info, role) {
      this.userInfo = info
      this.role = role
      localStorage.setItem('userInfo', JSON.stringify(info))
      localStorage.setItem('role', role)
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      this.role = ''
      localStorage.clear()
    }
  }
})
