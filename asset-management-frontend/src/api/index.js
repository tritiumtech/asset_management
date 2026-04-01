import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 资产相关API
export const assetApi = {
  // 获取资产列表
  listAssets: (params) => apiClient.post('/assets/list', params),
  
  // 获取资产详情
  getAssetById: (id) => apiClient.get(`/assets/${id}`),
  
  // 根据编号查询资产
  getAssetByCode: (code) => apiClient.get(`/assets/code/${code}`),
  
  // 创建资产
  createAsset: (data) => apiClient.post('/assets', data),
  
  // 更新资产
  updateAsset: (id, data) => apiClient.put(`/assets/${id}`, data),
  
  // 删除资产
  deleteAsset: (id) => apiClient.delete(`/assets/${id}`),
  
  // 资产领用
  assignAsset: (id, data) => apiClient.post(`/assets/${id}/assign`, data),
  
  // 资产归还
  returnAsset: (id, description) => 
    apiClient.post(`/assets/${id}/return`, null, { params: { description } }),
  
  // 资产调拨
  transferAsset: (id, data) => apiClient.post(`/assets/${id}/transfer`, data)
}

// 盘点相关API
export const inventoryApi = {
  // 获取盘点任务列表
  listTasks: (pageNum = 1, pageSize = 20) => 
    apiClient.get('/inventory/tasks', { params: { pageNum, pageSize } }),
  
  // 获取盘点任务详情
  getTaskById: (id) => apiClient.get(`/inventory/tasks/${id}`),
  
  // 创建盘点任务
  createTask: (data) => apiClient.post('/inventory/tasks', data),
  
  // 开始盘点任务
  startTask: (id) => apiClient.post(`/inventory/tasks/${id}/start`),
  
  // 完成盘点任务
  completeTask: (id) => apiClient.post(`/inventory/tasks/${id}/complete`),
  
  // 获取盘点明细列表
  listItems: (taskId, pageNum = 1, pageSize = 20) => 
    apiClient.get(`/inventory/tasks/${taskId}/items`, { params: { pageNum, pageSize } }),
  
  // 扫码盘点
  scanAsset: (data) => apiClient.post('/inventory/scan', data),
  
  // 核实盘点项
  verifyItem: (data) => apiClient.post('/inventory/verify', data),
  
  // 获取盘点统计
  getTaskStatistics: (taskId) => apiClient.get(`/inventory/tasks/${taskId}/statistics`)
}

export default apiClient