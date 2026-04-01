<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="资产管理系统" fixed placeholder />
    
    <!-- 统计卡片 -->
    <div class="stats-section">
      <van-grid :column-num="2" :gutter="10">
        <van-grid-item>
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalAssets }}</div>
            <div class="stat-label">资产总数</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-card warning">
            <div class="stat-value">{{ stats.inUseAssets }}</div>
            <div class="stat-label">在用资产</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-card success">
            <div class="stat-value">{{ stats.inStockAssets }}</div>
            <div class="stat-label">库存资产</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-card danger">
            <div class="stat-value">{{ stats.inRepairAssets }}</div>
            <div class="stat-label">维修中</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>
    
    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="section-title">快捷功能</div>
      <van-cell-group inset>
        <van-cell title="扫码查资产" is-link @click="scanAsset">
          <template #icon>
            <van-icon name="scan" class="action-icon" color="#1989fa" />
          </template>
        </van-cell>
        
        <van-cell title="资产盘点" to="/inventory" is-link>
          <template #icon>
            <van-icon name="records" class="action-icon" color="#07c160" />
          </template>
        </van-cell>
        
        <van-cell title="我的资产" to="/my-assets" is-link>
          <template #icon>
            <van-icon name="bag-o" class="action-icon" color="#ff976a" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>
    
    <!-- 最近盘点任务 -->
    <div class="recent-tasks" v-if="recentTasks.length > 0">
      <div class="section-title">最近盘点</div>
      <van-cell-group inset>
        <van-cell 
          v-for="task in recentTasks" 
          :key="task.id"
          :title="task.taskName"
          :label="task.batchNo"
          is-link
          :to="`/inventory/${task.id}`"
        >
          <template #value>
            <van-tag :type="getStatusType(task.status)">{{ task.statusName }}</van-tag>
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const stats = ref({
  totalAssets: 0,
  inUseAssets: 0,
  inStockAssets: 0,
  inRepairAssets: 0
})

const recentTasks = ref([])

const getStatusType = (status) => {
  const types = {
    'PLANNED': 'default',
    'IN_PROGRESS': 'primary',
    'FIRST_VERIFY': 'warning',
    'SECOND_VERIFY': 'warning',
    'COMPLETED': 'success'
  }
  return types[status] || 'default'
}

const scanAsset = () => {
  // TODO: 调用原生扫码或上传图片识别
  router.push('/assets')
}

onMounted(() => {
  // TODO: 加载统计数据和最近任务
})
</script>

<style scoped>
.stats-section {
  padding: 12px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  color: #fff;
}

.stat-card.warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.success {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.danger {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
}

.quick-actions,
.recent-tasks {
  margin-top: 16px;
}

.section-title {
  font-size: 14px;
  color: #666;
  padding: 0 16px 8px;
}

.action-icon {
  font-size: 20px;
  margin-right: 8px;
}
</style>