import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

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
    path: '/my-assets',
    name: 'MyAssets',
    component: () => import('@/views/MyAssets.vue'),
    meta: { title: '我的资产' }
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
  
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  
  // 未登录用户访问非公开页面，跳转到登录页
  if (!to.meta.public && !isLoggedIn) {
    next('/login')
    return
  }
  
  next()
})

export default router