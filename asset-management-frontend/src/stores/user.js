import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const realName = computed(() => userInfo.value?.realName || '')
  const roleCode = computed(() => userInfo.value?.roleCode || '')

  // Actions
  function setUserInfo(data) {
    token.value = data.token || ''
    userInfo.value = data
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(data))
  }

  function clearUserInfo() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function logout() {
    clearUserInfo()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    realName,
    roleCode,
    setUserInfo,
    clearUserInfo,
    logout
  }
})
