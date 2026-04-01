<template>
  <div class="mine-page">
    <!-- 用户信息 -->
    <div class="user-header">
      <van-image
        round
        width="80"
        height="80"
        src="https://img.yzcdn.cn/vant/cat.jpeg"
        class="avatar"
      />
      <div class="user-info">
        <div class="user-name">张三</div>
        <div class="user-dept">IT部 - 资产管理员</div>
      </div>
    </div>
    
    <!-- 我的资产统计 -->
    <van-grid :column-num="3" :border="false">
      <van-grid-item>
        <div class="grid-value">3</div>
        <div class="grid-label">我的资产</div>
      </van-grid-item>
      <van-grid-item>
        <div class="grid-value">1</div>
        <div class="grid-label">待审批</div>
      </van-grid-item>
      <van-grid-item>
        <div class="grid-value">0</div>
        <div class="grid-label">待归还</div>
      </van-grid-item>
    </van-grid>
    
    <!-- 功能菜单 -->
    <van-cell-group inset class="menu-group">
      <van-cell title="我的资产" is-link to="/my-assets">
        <template #icon>
          <van-icon name="bag-o" class="menu-icon" color="#1989fa" />
        </template>
      </van-cell>
      
      <van-cell title="领用申请" is-link>
        <template #icon>
          <van-icon name="add-o" class="menu-icon" color="#07c160" />
        </template>
      </van-cell>
      
      <van-cell title="归还申请" is-link>
        <template #icon>
          <van-icon name="replay" class="menu-icon" color="#ff976a" />
        </template>
      </van-cell>
      
      <van-cell title="报修申请" is-link>
        <template #icon>
          <van-icon name="warning-o" class="menu-icon" color="#ee0a24" />
        </template>
      </van-cell>
    </van-cell-group>
    
    <van-cell-group inset class="menu-group">
      <van-cell title="设置" is-link>
        <template #icon>
          <van-icon name="setting-o" class="menu-icon" />
        </template>
      </van-cell>
      
      <van-cell title="关于" is-link>
        <template #icon>
          <van-icon name="info-o" class="menu-icon" />
        </template>
      </van-cell>
    </van-cell-group>
    
    <div class="logout-btn">
      <van-button block plain type="danger" @click="logout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.mine-page {
  background: #f5f5f5;
  min-height: 100vh;
}

.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  display: flex;
  align-items: center;
  color: #fff;
}

.avatar {
  border: 3px solid rgba(255,255,255,0.3);
}

.user-info {
  margin-left: 16px;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-dept {
  font-size: 14px;
  opacity: 0.9;
}

.grid-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.grid-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.menu-group {
  margin-top: 12px;
}

.menu-icon {
  font-size: 20px;
  margin-right: 8px;
}

.logout-btn {
  padding: 20px 16px;
}
</style>