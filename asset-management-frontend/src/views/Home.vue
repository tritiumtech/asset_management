<template>
  <div class="home-page">
    <!-- 搜索栏 -->
    <div class="search-section">
      <van-search
        v-model="searchKey"
        placeholder="搜索资产编号、名称、位置"
        @search="onSearch"
      />
    </div>
    
    <!-- 数据卡片（管理员可见） -->
    <div class="stats-section" v-if="isAdmin">
      <div class="stats-header">
        <span class="stats-title">资产概览</span>
        <van-tag type="primary" @click="goToAssets">查看更多</van-tag>
      </div>
      <van-grid :column-num="4" :border="false">
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总数</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value in-use">{{ stats.inUse }}</div>
            <div class="stat-label">在用</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value in-stock">{{ stats.inStock }}</div>
            <div class="stat-label">库存</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value repairing">{{ stats.repairing }}</div>
            <div class="stat-label">维修</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>

    <!-- 快捷入口（根据角色动态显示） -->
    <div class="quick-section">
      <div class="section-title">快捷操作</div>
      <div class="quick-grid">
        <div class="quick-item" @click="scanAsset">
          <div class="quick-icon scan">
            <van-icon name="scan" />
          </div>
          <div class="quick-text">扫码盘点</div>
        </div>
        
        <div class="quick-item" @click="goTo('/work/inbound')" v-if="isAdmin">
          <div class="quick-icon inbound">
            <van-icon name="logistics" />
          </div>
          <div class="quick-text">入库质检</div>
        </div>
        
        <div class="quick-item" @click="goTo('/work/transfer')">
          <div class="quick-icon transfer">
            <van-icon name="exchange" />
          </div>
          <div class="quick-text">调拨申请</div>
        </div>
        
        <div class="quick-item" @click="goTo('/work/repair')">
          <div class="quick-icon repair">
            <van-icon name="setting-o" />
          </div>
          <div class="quick-text">报修申请</div>
        </div>
        
        <div class="quick-item" @click="goTo('/inventory')" v-if="canInventory">
          <div class="quick-icon inventory">
            <van-icon name="records" />
          </div>
          <div class="quick-text">盘点任务</div>
        </div>
        
        <div class="quick-item" @click="goTo('/assets')">
          <div class="quick-icon assets">
            <van-icon name="bag-o" />
          </div>
          <div class="quick-text">资产查询</div>
        </div>
      </div>
    </div>
    
    <!-- 待办提醒 -->
    <div class="todo-section" v-if="todoCount > 0">
      <div class="section-header">
        <span class="section-title">待办提醒</span>
        <span class="todo-count">{{ todoCount }}项</span>
      </div>
      <van-cell-group inset>
        <van-cell v-if="pendingInventory > 0" title="资产盘点" :value="pendingInventory + '个待盘点'" is-link @click="goTo('/inventory')">
          <template #icon>
            <van-icon name="records" class="cell-icon" color="#1989fa" />
          </template>
        </van-cell>
        <van-cell v-if="pendingApproval > 0" title="审批事项" :value="pendingApproval + '个待审批'" is-link @click="goTo('/work')">
          <template #icon>
            <van-icon name="todo-list-o" class="cell-icon" color="#ee0a24" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 最近动态 -->
    <div class="recent-section">
      <div class="section-title">最近动态</div>
      <van-cell-group inset>
        <van-cell title="张三领用了 NB-26-0001" label="MacBook Pro 16寸" />
        <van-cell title="李四归还了 DS-26-0005" label="Dell显示器 U2723QE" />
        <van-cell title="系统创建了盘点任务" label="2026年上半年盘点" />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const searchKey = ref('')

// 权限检查
const isAdmin = computed(() => {
  return ['MGR_SYSTEM', 'MGR_ASSET_ADMIN'].includes(userStore.roleCode)
})

const canInventory = computed(() => {
  return ['MGR_SYSTEM', 'MGR_ASSET_ADMIN', 'MGR_INVENTORY'].includes(userStore.roleCode)
})

// 统计数据
const stats = ref({
  total: 1250,
  inUse: 980,
  inStock: 150,
  repairing: 20
})

// 待办统计
const pendingInventory = ref(3)
const pendingApproval = ref(2)
const todoCount = computed(() => pendingInventory.value + pendingApproval.value)

const onSearch = () => {
  router.push(`/assets?search=${searchKey.value}`)
}

const goTo = (path) => {
  router.push(path)
}

const goToAssets = () => {
  router.push('/assets')
}

const scanAsset = () => {
  // TODO: 调用扫码功能
  router.push('/inventory/scan')
}
</script>

<style scoped>
.home-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20px;
}

.search-section {
  padding: 8px 12px;
  background: #fff;
}

.stats-section {
  margin: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stats-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-value.in-use { color: #1989fa; }
.stat-value.in-stock { color: #07c160; }
.stat-value.repairing { color: #ff976a; }

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.quick-section {
  margin: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
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

.todo-count {
  font-size: 12px;
  color: #ee0a24;
  background: #ffeaea;
  padding: 2px 8px;
  border-radius: 10px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 8px;
}

.quick-icon.scan { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.quick-icon.inbound { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.quick-icon.transfer { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.quick-icon.repair { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.quick-icon.inventory { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.quick-icon.assets { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }

.quick-text {
  font-size: 12px;
  color: #333;
}

.todo-section,
.recent-section {
  margin: 12px;
}

.cell-icon {
  font-size: 20px;
  margin-right: 8px;
}
</style>