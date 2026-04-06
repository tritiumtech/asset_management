<template>
  <div class="assets-page">
    <!-- 统计数据区 -->
    <div class="stats-header">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总资产</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.inUse }}</div>
          <div class="stat-label">已领用</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.inTransfer }}</div>
          <div class="stat-label">调拨中</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.idle }}</div>
          <div class="stat-label">超期闲置</div>
        </div>
      </div>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
        v-model="searchText"
        placeholder="搜索资产名称、编号、SN码"
        @search="onSearch"
        @clear="onClear"
      />
    </div>
    
    <!-- 筛选标签 -->
    <div class="filter-tags">
      <van-dropdown-menu>
        <van-dropdown-item v-model="category" :options="categoryOptions" />
        <van-dropdown-item v-model="status" :options="statusOptions" />
      </van-dropdown-menu>
    </div>
    
    <!-- 资产列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-card
          v-for="asset in assets"
          :key="asset.id"
          :title="asset.assetName"
          :desc="asset.assetCode"
          :thumb="asset.assetPhotoUrl || defaultImage"
          @click="viewDetail(asset)"
        >
          <template #tags>
            <van-tag :type="getStatusType(asset.status)" class="status-tag">
              {{ asset.statusName }}
            </van-tag>
            <van-tag type="primary" plain class="category-tag">
              {{ asset.categoryName }}
            </van-tag>
          </template>
          
          <template #footer>
            <div class="asset-info">
              <div class="info-item">
                <van-icon name="location-o" />
                <span>{{ asset.locationDetail }}</span>
              </div>
              <div class="info-item" v-if="asset.currentUserName">
                <van-icon name="user-o" />
                <span>{{ asset.currentUserName }}</span>
              </div>
            </div>
          </template>
        </van-card>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchText = ref('')
const category = ref('')
const status = ref('')
const refreshing = ref(false)
const loading = ref(false)
const finished = ref(false)
const defaultImage = 'https://img.yzcdn.cn/vant/cat.jpeg'

// 统计数据
const stats = ref({
  total: 0,
  inUse: 0,
  inTransfer: 0,
  idle: 0
})

const categoryOptions = [
  { text: '全部类别', value: '' },
  { text: '主机', value: 'PC' },
  { text: '笔记本', value: 'NB' },
  { text: '显示器', value: 'DS' },
  { text: 'iPad', value: 'IP' },
  { text: '外设', value: 'PE' },
  { text: '网络设备', value: 'NT' },
  { text: '收银设备', value: 'PO' }
]

const statusOptions = [
  { text: '全部状态', value: '' },
  { text: '库存', value: 'IN_STOCK' },
  { text: '在用', value: 'IN_USE' },
  { text: '维修中', value: 'IN_REPAIR' },
  { text: '报废', value: 'SCRAPPED' }
]

const assets = ref([
  {
    id: 1,
    assetName: 'MacBook Pro 16寸',
    assetCode: 'NB-26-0001',
    categoryName: '笔记本',
    status: 'IN_USE',
    statusName: '在用',
    locationDetail: '总部-3F-IT部',
    currentUserName: '张三'
  },
  {
    id: 2,
    assetName: 'Dell显示器 U2723QE',
    assetCode: 'DS-26-0001',
    categoryName: '显示器',
    status: 'IN_STOCK',
    statusName: '库存',
    locationDetail: '总部-1F-仓库'
  }
])

const getStatusType = (status) => {
  const types = {
    'IN_STOCK': 'success',
    'IN_USE': 'primary',
    'IN_TRANSFER': 'warning',
    'IN_REPAIR': 'warning',
    'SCRAPPED': 'danger',
    'MISSING': 'danger'
  }
  return types[status] || 'default'
}

const viewDetail = (asset) => {
  router.push(`/asset/${asset.id}`)
}

const onSearch = () => {
  // TODO: 搜索资产
}

const onClear = () => {
  searchText.value = ''
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
    // 更新统计数据
    updateStats()
  }, 1000)
}

// 更新统计数据
const updateStats = () => {
  const total = assets.value.length
  const inUse = assets.value.filter(a => a.status === 'IN_USE').length
  const inTransfer = assets.value.filter(a => a.status === 'IN_TRANSFER').length
  // 超期闲置：6个月未领用（这里简化处理，实际应该根据assignDate计算）
  const idle = assets.value.filter(a => a.status === 'IN_STOCK').length
  
  stats.value = { total, inUse, inTransfer, idle }
}
</script>

<style scoped>
.assets-page {
  background: #f5f5f5;
  min-height: 100vh;
}

/* 统计数据区 */
.stats-header {
  background: #9C020E;
  padding: 20px;
  margin-top: 0;
  width: 100%;
  position: sticky;
  top: 0;
  z-index: 10;
  border: 1px solid rgba(255,255,255,0.1);
  box-sizing: border-box;
  height: 66px;
  display: flex;
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  color: #fff;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 10px;
  opacity: 0.9;
}

.search-bar {
  background: #fff;
}

.filter-tags {
  background: #fff;
  padding-bottom: 8px;
}

.status-tag {
  margin-right: 8px;
}

.category-tag {
  margin-right: 8px;
}

.asset-info {
  margin-top: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.info-item .van-icon {
  margin-right: 4px;
}

:deep(.van-card) {
  background: #fff;
  margin: 8px;
  border-radius: 8px;
}

:deep(.van-card__thumb) {
  width: 80px;
  height: 80px;
}

/* PC端适配 */
@media (min-width: 768px) {
  .assets-page {
    max-width: 100%;
    margin: 0;
  }
  
  .stats-header {
    margin-top: 0;
    width: 100%;
    height: 66px;
  }
  
  .stats-grid {
    max-width: 800px;
    margin: 0 auto;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  :deep(.van-card) {
    margin: 12px auto;
    max-width: 800px;
  }
}
</style>