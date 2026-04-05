import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/assets',
    name: 'Assets',
    component: () => import('@/views/Assets.vue'),
    meta: { title: '资产列表' }
  },
  {
    path: '/asset/:id',
    name: 'AssetDetail',
    component: () => import('@/views/AssetDetail.vue'),
    meta: { title: '资产详情' }
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('@/views/Inventory.vue'),
    meta: { title: '盘点任务' }
  },
  {
    path: '/inventory/:id',
    name: 'InventoryDetail',
    component: () => import('@/views/InventoryDetail.vue'),
    meta: { title: '盘点详情' }
  },
  {
    path: '/inventory/:id/scan',
    name: 'InventoryScan',
    component: () => import('@/views/InventoryScan.vue'),
    meta: { title: '扫码盘点' }
  },
  {
    path: '/mine',
    name: 'Mine',
    component: () => import('@/views/Mine.vue'),
    meta: { title: '我的' }
  },
  {
    path: '/work',
    name: 'Workbench',
    component: () => import('@/views/Workbench.vue'),
    meta: { title: '工作台' }
  },
  {
    path: '/work/transfer',
    name: 'Transfer',
    component: () => import('@/views/Transfer.vue'),
    meta: { title: '调拨管理' }
  },
  {
    path: '/work/repair',
    name: 'Repair',
    component: () => import('@/views/Repair.vue'),
    meta: { title: '报修申请' }
  },
  {
    path: '/work/inbound',
    name: 'Inbound',
    component: () => import('@/views/Workbench.vue'),
    meta: { title: '入库质检' }
  },
  {
    path: '/work/scrap',
    name: 'Scrap',
    component: () => import('@/views/Workbench.vue'),
    meta: { title: '报废审批' }
  },
  {
    path: '/work/repair-tracking',
    name: 'RepairTracking',
    component: () => import('@/views/Workbench.vue'),
    meta: { title: '维修跟踪' }
  },
  {
    path: '/approval/:type',
    name: 'Approval',
    component: () => import('@/views/Approval.vue'),
    meta: { title: '审批中心' }
  },
  {
    path: '/my-assets',
    name: 'MyAssets',
    component: () => import('@/views/MyAssets.vue'),
    meta: { title: '我的资产' }
  },
  // 资产管理模块
  {
    path: '/category',
    name: 'Category',
    component: () => import('@/views/Category.vue'),
    meta: { title: '资产分类管理', admin: true }
  },
  {
    path: '/assets/add',
    name: 'AssetAdd',
    component: () => import('@/views/AssetAdd.vue'),
    meta: { title: '资产录入', admin: true }
  },
  {
    path: '/assets/batch-add',
    name: 'AssetBatchAdd',
    component: () => import('@/views/AssetBatchAdd.vue'),
    meta: { title: '资产批量录入', admin: true }
  },
  {
    path: '/assets/stock',
    name: 'AssetStock',
    component: () => import('@/views/AssetStock.vue'),
    meta: { title: '资产库存表', admin: true }
  },
  // 资产流转模块
  {
    path: '/work/receive',
    name: 'AssetReceive',
    component: () => import('@/views/AssetReceive.vue'),
    meta: { title: '资产领用' }
  },
  {
    path: '/work/borrow',
    name: 'AssetBorrow',
    component: () => import('@/views/AssetBorrow.vue'),
    meta: { title: '资产借用' }
  },
  {
    path: '/work/return',
    name: 'AssetReturn',
    component: () => import('@/views/AssetReturn.vue'),
    meta: { title: '资产归还' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  
  // 使用localStorage直接检查token，避免Pinia store在初始化前被调用
  const token = localStorage.getItem('token')
  const isLoggedIn = !!token
  
  // 未登录用户访问非公开页面，跳转到登录页
  if (!to.meta.public && !isLoggedIn) {
    next('/login')
    return
  }
  
  next()
})

export default router