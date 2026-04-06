<template>
  <div class="mine-page">
    <!-- 用户信息 -->
    <div class="user-header">
      <van-image
        round
        width="40"
        height="40"
        src="https://img.yzcdn.cn/vant/cat.jpeg"
        class="avatar"
      />
      <div class="user-info">
        <div class="user-name">{{ userStore.realName || '未登录' }}</div>
        <div class="user-dept">{{ userDept }}</div>
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
      <van-button block plain type="danger" @click="handleLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog } from 'vant'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const userDept = computed(() => {
  const roleMap = {
    'MGR_SYSTEM': '系统管理员',
    'MGR_ASSET_ADMIN': '资产管理员',
    'MGR_TRANSFER': '调拨员',
    'MGR_INVENTORY': '盘点员',
    'MGR_FINANCE': '财务专员',
    'USER_STORE_MANAGER': '店长',
    'USER_DEPT_MANAGER': '部门经理',
    'USER_EMPLOYEE': '员工',
    'SUPPLIER_SELLER': '供应商-出售方',
    'SUPPLIER_RENTER': '供应商-出租方',
    'SUPPLIER_MAINTAINER': '供应商-维修方'
  }
  return userStore.roleName || roleMap[userStore.roleCode] || '未知角色'
})

const handleLogout = () => {
  showConfirmDialog({
    title: '确认退出',
    message: '确定要退出登录吗？'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {
    // 取消退出
  })
}
</script>

<style scoped>
.mine-page {
  background: #f5f5f5;
  min-height: 100vh;
  border: 1px solid rgba(255,255,255,0.1);
  box-sizing: border-box;
}

.user-header {
  background: #9C020E;
  padding: 20px;
  display: flex;
  align-items: center;
  color: #fff;
  height: 66px;
  box-sizing: border-box;
}

.avatar {
  border: 3px solid rgba(255,255,255,0.3);
}

.user-info {
  margin-left: 16px;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-dept {
  font-size: 12px;
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

/* PC端适配 */
@media (min-width: 768px) {
  .mine-page {
    max-width: 100%;
    margin: 0;
  }
  
  .user-header {
    padding: 20px;
    height: 66px;
  }
  
  .user-name {
    font-size: 18px;
  }
  
  .menu-group {
    margin: 16px;
    border-radius: 12px;
    overflow: hidden;
  }
}
</style>