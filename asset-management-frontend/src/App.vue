<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" />
      </keep-alive>
    </router-view>
    
    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" v-if="showTabbar" route>
      <van-tabbar-item to="/" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/assets" icon="apps-o">资产</van-tabbar-item>
      <van-tabbar-item to="/inventory" icon="scan">盘点</van-tabbar-item>
      <van-tabbar-item to="/mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref(0)

const showTabbar = computed(() => {
  const noTabbarRoutes = ['/login']
  return !noTabbarRoutes.includes(route.path)
})
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  padding-bottom: 50px;
}
</style>