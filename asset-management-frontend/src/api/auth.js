import axios from 'axios'
import { useUserStore } from '../stores/user'

// 生产环境使用相对路径，开发环境使用localhost
const isDev = import.meta.env.DEV
const baseURL = isDev ? 'http://localhost:8081/api' : '/api'

const api = axios.create({
  baseURL: baseURL,
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      // token过期，清除登录状态并跳转到登录页
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 认证相关API
export const login = (data) => api.post('/auth/login', data)
export const logout = () => api.post('/auth/logout')

// 资产相关API
export const getAssetList = (params) => api.post('/assets/list', params)
export const getAssetDetail = (id) => api.get(`/assets/${id}`)

// 盘点相关API
export const getInventoryList = () => api.get('/inventory/list')
export const getInventoryDetail = (id) => api.get(`/inventory/${id}`)

export default api
