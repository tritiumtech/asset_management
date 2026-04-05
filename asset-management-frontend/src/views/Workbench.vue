<template>
  <div class="workbench-page">
    <!-- 业务操作 -->
    <div class="section">
      <div class="section-title">资产管理</div>
      <div class="grid-menu">
        <div class="menu-item" @click="goTo('/work/inbound')" v-if="hasRole('MGR_ASSET_ADMIN')">
          <div class="menu-icon inbound">
            <van-icon name="logistics" />
          </div>
          <div class="menu-text">入库质检</div>
        </div>
        <div class="menu-item" @click="goTo('/work/transfer')">
          <div class="menu-icon transfer">
            <van-icon name="exchange" />
          </div>
          <div class="menu-text">调拨管理</div>
        </div>
        <div class="menu-item" @click="goTo('/work/inventory')">
          <div class="menu-icon inventory">
            <van-icon name="scan" />
          </div>
          <div class="menu-text">盘点任务</div>
        </div>
      </div>
    </div>

    <!-- 资产处置 -->
    <div class="section">
      <div class="section-title">资产处置</div>
      <div class="grid-menu">
        <div class="menu-item" @click="goTo('/work/repair')">
          <div class="menu-icon repair">
            <van-icon name="setting-o" />
          </div>
          <div class="menu-text">报修申请</div>
        </div>
        <div class="menu-item" @click="goTo('/work/scrap')" v-if="hasAnyRole(['MGR_ASSET_ADMIN', 'MGR_FINANCE'])">
          <div class="menu-icon scrap">
            <van-icon name="delete-o" />
          </div>
          <div class="menu-text">报废审批</div>
        </div>
        <div class="menu-item" @click="goTo('/work/repair-tracking')" v-if="hasRole('MGR_ASSET_ADMIN')">
          <div class="menu-icon tracking">
            <van-icon name="eye-o" />
          </div>
          <div class="menu-text">维修跟踪</div>
        </div>
      </div>
    </div>

    <!-- 审批中心 -->
    <div class="section" v-if="pendingCount > 0">
      <div class="section-header">
        <div class="section-title">审批中心</div>
        <div class="pending-badge">{{ pendingCount }}个待办</div>
      </div>
      <van-cell-group inset>
        <van-cell title="调拨审批" :value="pendingTransfer + '个待审'" is-link @click="goTo('/approval/transfer')">
          <template #icon>
            <van-icon name="exchange" class="cell-icon" color="#1989fa" />
          </template>
        </van-cell>
        <van-cell title="报废审批" :value="pendingScrap + '个待审'" is-link @click="goTo('/approval/scrap')" v-if="hasAnyRole(['MGR_ASSET_ADMIN', 'MGR_FINANCE'])">
          <template #icon>
            <van-icon name="delete-o" class="cell-icon" color="#ee0a24" />
          </template>
        </van-cell>
        <van-cell title="领用审批" :value="pendingAssign + '个待审'" is-link @click="goTo('/approval/assign')">
          <template #icon>
            <van-icon name="bag-o" class="cell-icon" color="#07c160" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 快捷统计 -->
    <div class="section" v-if="hasRole('MGR_ASSET_ADMIN')">
      <div class="section-title">快捷统计</div>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总资产</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.inUse }}</div>
          <div class="stat-label">在用</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.inStock }}</div>
          <div class="stat-label">库存</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.repairing }}</div>
          <div class="stat-label">维修中</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

// 权限检查
const hasRole = (role) => {
  return userStore.roleCode === role
}

const hasAnyRole = (roles) => {
  return roles.includes(userStore.roleCode)
}

// 待办统计
const pendingTransfer = ref(2)
const pendingScrap = ref(1)
const pendingAssign = ref(3)
const pendingCount = computed(() => pendingTransfer.value + pendingScrap.value + pendingAssign.value)

// 资产统计
const stats = ref({
  total: 1250,
  inUse: 980,
  inStock: 150,
  repairing: 20
})

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.workbench-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20px;
}

.section {
  margin-bottom: 12px;
  background: #fff;
  padding: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pending-badge {
  font-size: 12px;
  color: #ee0a24;
  background: #ffeaea;
  padding: 2px 8px;
  border-radius: 10px;
}

.grid-menu {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 8px;
  background: #f8f8f8;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:active {
  background: #e8e8e8;
}

.menu-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  font-size: 24px;
  color: #fff;
}

.menu-icon.inbound { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.menu-icon.transfer { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.menu-icon.inventory { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.menu-icon.repair { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.menu-icon.scrap { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.menu-icon.tracking { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }

.menu-text {
  font-size: 14px;
  color: #333;
}

.cell-icon {
  font-size: 20px;
  margin-right: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: #f8f8f8;
  border-radius: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #1989fa;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

/* PC端适配 */
@media (min-width: 768px) {
  .workbench-page {
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .section {
    margin: 16px;
    border-radius: 12px;
  }
  
  .grid-menu {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .menu-item {
    padding: 24px;
  }
  
  .menu-icon {
    width: 56px;
    height: 56px;
    font-size: 28px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .stat-value {
    font-size: 28px;
  }
}
</style>