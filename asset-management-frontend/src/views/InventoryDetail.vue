<template>
  <div class="inventory-detail-page">
    <van-nav-bar 
      title="盘点详情" 
      left-text="返回" 
      left-arrow 
      @click-left="goBack"
      fixed
      placeholder
    />
    
    <div class="task-header" v-if="task">
      <h2>{{ task.taskName }}</h2>
      <van-tag :type="getStatusType(task.status)">{{ task.statusName }}</van-tag>
      <div class="progress-section">
        <van-progress 
          :percentage="progressPercent" 
          :stroke-width="10"
          color="linear-gradient(to right, #be99ff, #7232dd)"
        />
        <div class="progress-stats">
          <span>总: {{ task.totalCount }}</span>
          <span>已盘: {{ task.checkedCount }}</span>
          <span>进度: {{ progressPercent }}%</span>
        </div>
      </div>
    </div>
    
    <van-cell-group inset class="stats-group" v-if="task">
      <van-cell title="正常" :value="task.normalCount || 0" />
      <van-cell title="盘亏" :value="task.missingCount || 0" />
      <van-cell title="盘盈" :value="task.surplusCount || 0" />
      <van-cell title="位置变更" :value="task.locationChangeCount || 0" />
      <van-cell title="责任人变更" :value="task.userChangeCount || 0" />
    </van-cell-group>
    
    <div class="action-buttons">
      <van-button type="primary" block @click="startScan">开始盘点</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { inventoryApi } from '@/api'

const route = useRoute()
const router = useRouter()
const taskId = route.params.id
const task = ref(null)

const progressPercent = computed(() => {
  if (!task.value || task.value.totalCount === 0) return 0
  return Math.round((task.value.checkedCount / task.value.totalCount) * 100)
})

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

const loadTask = async () => {
  try {
    const res = await inventoryApi.getTaskById(taskId)
    if (res.code === 200) {
      task.value = res.data
    }
  } catch (error) {
    console.error('加载盘点任务失败:', error)
  }
}

const goBack = () => {
  router.back()
}

const startScan = () => {
  router.push(`/inventory/${taskId}/scan`)
}

onMounted(() => {
  loadTask()
})
</script>

<style scoped>
.inventory-detail-page {
  padding: 16px;
  background: #f5f5f5;
  min-height: 100vh;
}

.task-header {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
}

.task-header h2 {
  margin: 0 0 8px 0;
  font-size: 18px;
}

.progress-section {
  margin-top: 12px;
}

.progress-stats {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.stats-group {
  margin-bottom: 12px;
}

.action-buttons {
  padding: 16px 0;
}
</style>