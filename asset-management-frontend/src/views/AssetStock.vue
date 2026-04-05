<template>
  <div class="asset-stock-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      title="资产库存表"
      left-arrow
      @click-left="goBack"
      right-text="筛选"
      @click-right="showFilter = true"
    />

    <!-- 统计卡片 -->
    <div class="stats-section">
      <van-grid :column-num="3" :border="false">
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">库存总数</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value amount">¥{{ stats.amount }}</div>
            <div class="stat-label">库存总值</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="stat-item">
            <div class="stat-value">{{ stats.categories }}</div>
            <div class="stat-label">分类数量</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>

    <!-- 分类筛选标签 -->
    <div class="category-tabs">
      <van-tabs v-model:active="activeCategory" @change="onCategoryChange">
        <van-tab title="全部" name=""></van-tab>
        <van-tab title="主机类" name="PC"></van-tab>
        <van-tab title="笔记本" name="NB"></van-tab>
        <van-tab title="显示类" name="DS"></van-tab>
        <van-tab title="外设类" name="PE"></van-tab>
        <van-tab title="网络类" name="NT"></van-tab>
        <van-tab title="收银类" name="PO"></van-tab>
      </van-tabs>
    </div>

    <!-- 库存列表 -->
    <div class="stock-list">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-card
            v-for="item in stockList"
            :key="item.id"
            class="stock-card"
            @click="viewDetail(item)"
          >
            <template #title>
              <div class="card-title">
                <span class="asset-name">{{ item.name }}</span>
                <van-tag :type="getStatusType(item.status)">{{ item.status }}</van-tag>
              </div>
            </template>
            
            <template #desc>
              <div class="card-info">
                <div class="info-row">
                  <span class="info-label">编号:</span>
                  <span class="info-value">{{ item.assetNo }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">分类:</span>
                  <span class="info-value">{{ item.category }} / {{ item.subCategory }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">SN码:</span>
                  <span class="info-value">{{ item.sn }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">位置:</span>
                  <span class="info-value">{{ item.location }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">购置:</span>
                  <span class="info-value">{{ item.purchaseDate }} | ¥{{ item.price }}</span>
                </div>
              </div>
            </template>
            
            <template #footer>
              <div class="card-actions">
                <van-button size="small" type="primary" @click.stop="receiveAsset(item)">领用</van-button>
                <van-button size="small" type="default" @click.stop="editAsset(item)">编辑</van-button>
                <van-button size="small" type="default" @click.stop="printLabel(item)">打印标签</van-button>
              </div>
            </template>
          </van-card>
        </van-list>
      </van-pull-refresh>
    </div>

    <!-- 筛选弹窗 -->
    <van-popup v-model:show="showFilter" position="right" :style="{ width: '80%', height: '100%' }">
      <div class="filter-panel">
        <div class="filter-header">
          <span>筛选条件</span>
          <van-icon name="cross" @click="showFilter = false" />
        </div>
        
        <div class="filter-content">
          <div class="filter-section">
            <div class="filter-title">资产状态</div>
            <div class="filter-options">
              <van-checkbox-group v-model="filterForm.status">
                <van-checkbox name="库存">库存</van-checkbox>
                <van-checkbox name="在用">在用</van-checkbox>
                <van-checkbox name="维修中">维修中</van-checkbox>
                <van-checkbox name="调拨中">调拨中</van-checkbox>
              </van-checkbox-group>
            </div>
          </div>
          
          <div class="filter-section">
            <div class="filter-title">存放位置</div>
            <van-field
              v-model="filterForm.location"
              placeholder="请输入位置关键词"
            />
          </div>
          
          <div class="filter-section">
            <div class="filter-title">购置日期</div>
            <div class="date-range">
              <van-field
                v-model="filterForm.startDate"
                placeholder="开始日期"
                readonly
                @click="showStartDate = true"
              />
              <span class="date-separator">至</span>
              <van-field
                v-model="filterForm.endDate"
                placeholder="结束日期"
                readonly
                @click="showEndDate = true"
              />
            </div>
          </div>
          
          <div class="filter-section">
            <div class="filter-title">价格区间</div>
            <div class="price-range">
              <van-field
                v-model="filterForm.minPrice"
                type="number"
                placeholder="最低价"
              />
              <span class="price-separator">-</span>
              <van-field
                v-model="filterForm.maxPrice"
                type="number"
                placeholder="最高价"
              />
            </div>
          </div>
        </div>
        
        <div class="filter-footer">
          <van-button block @click="resetFilter">重置</van-button>
          <van-button type="primary" block @click="applyFilter">确定</van-button>
        </div>
      </div>
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showStartDate" position="bottom">
      <van-date-picker
        title="选择开始日期"
        @confirm="onStartDateConfirm"
        @cancel="showStartDate = false"
      />
    </van-popup>
    
    <van-popup v-model:show="showEndDate" position="bottom">
      <van-date-picker
        title="选择结束日期"
        @confirm="onEndDateConfirm"
        @cancel="showEndDate = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'

const router = useRouter()

// 统计数据
const stats = ref({
  total: 156,
  amount: '234,567',
  categories: 6
})

// 当前选中的分类
const activeCategory = ref('')

// 列表数据
const stockList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 筛选
const showFilter = ref(false)
const filterForm = reactive({
  status: ['库存'],
  location: '',
  startDate: '',
  endDate: '',
  minPrice: '',
  maxPrice: ''
})
const showStartDate = ref(false)
const showEndDate = ref(false)

// 模拟数据
const mockData = [
  {
    id: 1,
    assetNo: 'NB-26-0045',
    name: 'MacBook Pro 16寸 M3 Pro',
    category: '笔记本类',
    subCategory: 'MacBook',
    sn: 'C02XYZ123456',
    location: '总部仓库',
    status: '库存',
    purchaseDate: '2026-03-15',
    price: '18999'
  },
  {
    id: 2,
    assetNo: 'NB-26-0046',
    name: 'ThinkPad X1 Carbon Gen 12',
    category: '笔记本类',
    subCategory: 'ThinkPad',
    sn: 'PF1ABC789012',
    location: '总部仓库',
    status: '库存',
    purchaseDate: '2026-03-15',
    price: '12999'
  },
  {
    id: 3,
    assetNo: 'DS-26-0123',
    name: 'Dell显示器 U2723QE',
    category: '显示类',
    subCategory: '27寸显示器',
    sn: 'CN123456789',
    location: '深圳店仓库',
    status: '库存',
    purchaseDate: '2026-03-10',
    price: '3999'
  },
  {
    id: 4,
    assetNo: 'PE-26-0234',
    name: 'Keychron K3机械键盘',
    category: '外设类',
    subCategory: '键盘鼠标',
    sn: 'KEY20260001',
    location: '总部仓库',
    status: '库存',
    purchaseDate: '2026-03-05',
    price: '568'
  },
  {
    id: 5,
    assetNo: 'PO-26-0056',
    name: 'Sunmi V2 POS机',
    category: '收银类',
    subCategory: 'POS机',
    sn: 'POS20260001',
    location: '上海店仓库',
    status: '库存',
    purchaseDate: '2026-03-01',
    price: '1299'
  }
]

const getStatusType = (status) => {
  const map = {
    '库存': 'success',
    '在用': 'primary',
    '维修中': 'warning',
    '调拨中': 'default',
    '报废': 'danger'
  }
  return map[status] || 'default'
}

const goBack = () => {
  router.back()
}

const onCategoryChange = () => {
  stockList.value = []
  finished.value = false
  onLoad()
}

const onLoad = () => {
  loading.value = true
  
  // 模拟加载数据
  setTimeout(() => {
    const newData = mockData.filter(item => {
      if (activeCategory.value && !item.assetNo.startsWith(activeCategory.value)) {
        return false
      }
      return true
    })
    
    stockList.value.push(...newData)
    loading.value = false
    finished.value = true
  }, 500)
}

const onRefresh = () => {
  stockList.value = []
  finished.value = false
  onLoad()
  refreshing.value = false
}

const viewDetail = (item) => {
  router.push(`/asset/${item.id}`)
}

const receiveAsset = (item) => {
  router.push({
    path: '/work/receive',
    query: { assetId: item.id }
  })
}

const editAsset = (item) => {
  router.push(`/assets/edit/${item.id}`)
}

const printLabel = (item) => {
  showToast('正在生成标签...')
  // TODO: 调用打印标签API
}

const onStartDateConfirm = ({ selectedValues }) => {
  filterForm.startDate = selectedValues.join('-')
  showStartDate.value = false
}

const onEndDateConfirm = ({ selectedValues }) => {
  filterForm.endDate = selectedValues.join('-')
  showEndDate.value = false
}

const resetFilter = () => {
  filterForm.status = ['库存']
  filterForm.location = ''
  filterForm.startDate = ''
  filterForm.endDate = ''
  filterForm.minPrice = ''
  filterForm.maxPrice = ''
}

const applyFilter = () => {
  showFilter.value = false
  stockList.value = []
  finished.value = false
  onLoad()
  showToast('筛选已应用')
}

// 初始加载
onLoad()
</script>

<style scoped>
.asset-stock-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.stats-section {
  padding: 16px;
  background: #fff;
  margin-bottom: 8px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #323233;
}

.stat-value.amount {
  color: #1989fa;
}

.stat-label {
  font-size: 12px;
  color: #969799;
  margin-top: 4px;
}

.category-tabs {
  background: #fff;
}

.stock-list {
  padding: 8px;
}

.stock-card {
  margin-bottom: 8px;
  background: #fff;
  border-radius: 8px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.asset-name {
  font-size: 15px;
  font-weight: 500;
}

.card-info {
  margin-top: 8px;
}

.info-row {
  display: flex;
  margin-bottom: 4px;
  font-size: 13px;
}

.info-label {
  color: #969799;
  width: 50px;
  flex-shrink: 0;
}

.info-value {
  color: #323233;
  flex: 1;
}

.card-actions {
  display: flex;
  gap: 8px;
}

/* 筛选面板 */
.filter-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  font-size: 16px;
  font-weight: 500;
  border-bottom: 1px solid #ebedf0;
}

.filter-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-range,
.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-separator,
.price-separator {
  color: #969799;
}

.filter-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #ebedf0;
}
</style>