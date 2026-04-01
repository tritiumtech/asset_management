<template>
  <div class="inventory-page">
    <van-nav-bar title="盘点任务" fixed placeholder />
    
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-cell-group v-for="task in tasks" :key="task.id" inset class="task-card">
          <van-cell :title="task.taskName" :label="task.batchNo">
            <template #value>
              <van-tag :type="getStatusType(task.status)">{{ task.statusName }}</van-tag>
            </template>
          </van-cell>
          
          <van-cell>
            <div class="task-stats">
              <div class="stat-item">
                <div class="stat-value">{{ task.totalCount }}</div>
                <div class="stat-label">应盘</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ task.checkedCount }}</div>
                <div class="stat-label">已盘</div>
              </div>
              <div class="stat-item">
                <div class="stat-value highlight">{{ getProgress(task) }}%</div>
                <div class="stat-label">进度</div>
              </div>
            </div>
          </van-cell>
          
          <van-cell v-if="task.status === 'IN_PROGRESS'">
            <van-button type="primary" block @click="startScan(task)">
              开始盘点
            </van-button>
          </van-cell>
        </van-cell-group>
      </van-list>
    </van-pull-refresh>
    
    <!-- 新建任务按钮 -->
    <van-floating-bubble 
      axis="xy" 
      icon="plus" 
      @click="showCreateDialog"
      v-if="isAdmin"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const refreshing = ref(false)
const loading = ref(false)
const finished = ref(false)
const isAdmin = ref(true)

const tasks = ref([
  {
    id: 1,
    taskName: '2026H1资产盘点',
    batchNo: '2026H1',
    status: 'IN_PROGRESS',
    statusName: '进行中',
    totalCount: 150,
    checkedCount: 45
  },
  {
    id: 2,
    taskName: '2025H2资产盘点',
    batchNo: '2025H2',
    status: 'COMPLETED',
    statusName: '已完成',
    totalCount: 145,
    checkedCount: 145
  }
])

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

const getProgress = (task) => {
  if (task.totalCount === 0) return 0
  return Math.round((task.checkedCount / task.totalCount) * 100)
}

const startScan = (task) => {
  router.push(`/inventory/${task.id}/scan`)
}

const showCreateDialog = () => {
  // TODO: 新建盘点任务
}

const onRefresh = () => {
  setTimeout(() => {
    refreshing.value = false
  }, 1000)
}

const onLoad = () => {
  setTimeout(() => {
    loading.value = false
    finished.value = true
  }, 1000)
}
</script>

<style scoped>
.inventory-page {
  padding: 8px;
}

.task-card {
  margin-bottom: 12px;
}

.task-stats {
  display: flex;
  justify-content: space-around;
  padding: 8px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-value.highlight {
  color: #1989fa;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>