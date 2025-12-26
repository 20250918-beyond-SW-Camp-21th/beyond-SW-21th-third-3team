import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/userApi'

export const useUserStore = defineStore('user', () => {
  // 상태
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const isAuthenticated = computed(() => !!user.value)
  const userName = computed(() => user.value?.name || user.value?.login || '사용자')
  const userAvatar = computed(() => user.value?.avatar_url || '')
  const userEmail = computed(() => user.value?.email || '')

  // Actions
  const fetchProfile = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await userApi.getProfile()
      user.value = response.data.data
      return user.value
    } catch (err) {
      error.value = err.message || '프로필을 불러오는데 실패했습니다.'
      throw err
    } finally {
      loading.value = false
    }
  }

  const checkLoginSuccess = async () => {
    try {
      const response = await userApi.checkLoginSuccess()
      return response.data
    } catch (err) {
      throw err
    }
  }

  const logout = async () => {
    try {
      await userApi.logout()
    } catch (err) {
      console.error('로그아웃 실패:', err)
    } finally {
      user.value = null
    }
    // 로그아웃 후 로그인 페이지로 리다이렉트
    window.location.href = '/'
  }

  const setUser = (userData) => {
    user.value = userData
  }

  const clearUser = () => {
    user.value = null
    error.value = null
  }

  return {
    // 상태
    user,
    loading,
    error,
    // Getters
    isAuthenticated,
    userName,
    userAvatar,
    userEmail,
    // Actions
    fetchProfile,
    checkLoginSuccess,
    logout,
    setUser,
    clearUser
  }
})
