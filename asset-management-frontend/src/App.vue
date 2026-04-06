<template>
  <div class="app-wrapper">
    <!-- PC端侧边栏 -->
    <div class="pc-sidebar" v-if="showSidebar">
      <div class="sidebar-header">
        <div class="logo">📦 资产管理系统</div>
      </div>
      <div class="sidebar-menu">
        <router-link to="/" class="menu-item" :class="{ active: route.path === '/' }">
          <van-icon name="home-o" />
          <span>首页</span>
        </router-link>
        <router-link to="/assets" class="menu-item" :class="{ active: route.path === '/assets' }">
          <van-icon name="apps-o" />
          <span>资产</span>
        </router-link>
        <router-link to="/work" class="menu-item" :class="{ active: route.path === '/work' }">
          <van-icon name="setting-o" />
          <span>工作台</span>
        </router-link>
        <router-link to="/mine" class="menu-item" :class="{ active: route.path === '/mine' }">
          <van-icon name="user-o" />
          <span>我的</span>
        </router-link>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-content" :class="{ 'pc-mode': showSidebar }">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
    
    <!-- 移动端底部导航 -->
    <van-tabbar v-model="activeTab" v-if="showTabbar && !isPC" route>
      <van-tabbar-item to="/" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/assets" icon="apps-o">资产</van-tabbar-item>
      <van-tabbar-item to="/work" icon="setting-o">工作台</van-tabbar-item>
      <van-tabbar-item to="/mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref(0)
const isPC = ref(false)

// 不需要显示导航的路由
const noNavRoutes = ['/login']

const showTabbar = computed(() => {
  return !noNavRoutes.includes(route.path)
})

const showSidebar = computed(() => {
  return isPC.value && !noNavRoutes.includes(route.path)
})

// 检测是否为PC端
const checkDevice = () => {
  isPC.value = window.innerWidth >= 768
}

onMounted(() => {
  checkDevice()
  window.addEventListener('resize', checkDevice)
})
</script>

<style scoped>
.app-wrapper {
  min-height: 100vh;
  display: flex;
}

/* PC端侧边栏 */
.pc-sidebar {
  width: 200px;
  background: #9C020E;
  color: #fff;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo {
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  padding: 10px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  transition: all 0.3s;
}

.menu-item:hover,
.menu-item.active {
  background: rgba(255,255,255,0.1);
  color: #fff;
}

.menu-item .van-icon {
  margin-right: 10px;
  font-size: 20px;
}

/* 主内容区 */
.main-content {
  flex: 1;
  min-height: 100vh;
  padding-bottom: 50px;
}

.main-content.pc-mode {
  margin-left: 200px;
  padding-bottom: 0;
  max-width: calc(100% - 200px);
}

/* 移动端 */
@media (max-width: 767px) {
  .pc-sidebar {
    display: none;
  }
  
  .main-content.pc-mode {
    margin-left: 0;
    max-width: 100%;
  }
}
</style>