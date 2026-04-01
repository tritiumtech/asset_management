<template>
  <div class="scan-page">
    <!-- 顶部导航 -->
    <van-nav-bar 
      title="扫码盘点" 
      left-text="返回" 
      left-arrow 
      @click-left="goBack"
      fixed
      placeholder
    />
    
    <!-- 盘点进度 -->
    <div class="progress-section">
      <div class="progress-header">
        <span class="task-name">{{ taskName }}</span>
        <span class="progress-text">{{ checkedCount }}/{{ totalCount }}</span>
      </div>
      <van-progress 
        :percentage="progressPercent" 
        :stroke-width="12"
        color="linear-gradient(to right, #be99ff, #7232dd)"
      />
    </div>
    
    <!-- 扫码区域 -->
    <div class="scan-section">
      <div class="scan-box" @click="startScan">
        <van-icon name="scan" class="scan-icon" />
        <div class="scan-text">点击扫码</div>
        <div class="scan-subtext">支持条码和二维码</div>
      </div>
      
      <!-- 手动输入 -->
      <div class="manual-input">
        <van-field
          v-model="manualCode"
          placeholder="手动输入资产编号"
          center
          clearable
        >
          <template #button>
            <van-button size="small" type="primary" @click="handleManualInput">确认</van-button>
          </template>
        </van-field>
      </div>
    </div>
    
    <!-- 最近盘点记录 -->
    <div class="recent-records">
      <div class="section-header">
        <span>最近盘点</span>
        <van-button size="small" plain type="primary" @click="showAllRecords">查看全部</van-button>
      </div>
      
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-swipe-cell v-for="record in records" :key="record.id">
            <van-card
              :title="record.assetName"
              :desc="record.assetCode"
            >
              <template #tags>
                <van-tag :type="getResultType(record.result)" class="result-tag">
                  {{ record.resultName }}
                </van-tag>
              </template>
              
              <template #footer>
                <div class="record-info">
                  <span>{{ record.checkedLocation }}</span>
                  <span>{{ formatTime(record.actualDate) }}</span>
                </div>
              </template>
            </van-card>
            
            <template #right>
              <van-button 
                square 
                text="修改" 
                type="primary" 
                class="swipe-btn"
                @click="editRecord(record)"
              />
            </template>
          </van-swipe-cell>
        </van-list>
      </van-pull-refresh>
    </div>
    
    <!-- 盘点结果选择弹窗 -->
    <van-popup v-model:show="showResultPicker" position="bottom">
      <van-picker
        title="选择盘点结果"
        :columns="resultColumns"
        @confirm="onResultConfirm"
        @cancel="showResultPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const taskId = route.params.id

const taskName = ref('2026H1盘点')
const totalCount = ref(100)
const checkedCount = ref(0)
const manualCode = ref('')
const refreshing = ref(false)
const loading = ref(false)
const finished = ref(false)
const showResultPicker = ref(false)
const currentAsset = ref(null)

const records = ref([])

const progressPercent = computed(() => {
  if (totalCount.value === 0) return 0
  return Math.round((checkedCount.value / totalCount.value) * 100)
})

const resultColumns = [
  { text: '正常', value: 'NORMAL' },
  { text: '位置变更', value: 'LOCATION_CHANGE' },
  { text: '责任人变更', value: 'USER_CHANGE' },
  { text: '盘亏', value: 'MISSING' },
  { text: '盘盈', value: 'SURPLUS' }
]

const getResultType = (result) => {
  const types = {
    'NORMAL': 'success',
    'LOCATION_CHANGE': 'warning',
    'USER_CHANGE': 'warning',
    'MISSING': 'danger',
    'SURPLUS': 'primary'
  }
  return types[result] || 'default'
}

const formatTime = (time) => {
  return time ? dayjs(time).format('HH:mm') : ''
}

const goBack = () => {
  router.back()
}

// 开始扫码
const startScan = () => {
  // 模拟扫码成功
  const mockCode = 'NB-26-' + String(Math.floor(Math.random() * 1000)).padStart(4, '0')
  handleScanResult(mockCode)
}

// 处理扫码结果
const handleScanResult = (code) => {
  currentAsset.value = { assetCode: code }
  showResultPicker.value = true
}

// 手动输入
const handleManualInput = () => {
  if (!manualCode.value.trim()) {
    return
  }
  handleScanResult(manualCode.value.trim())
  manualCode.value = ''
}

// 确认盘点结果
const onResultConfirm = ({ selectedOptions }) => {
  const result = selectedOptions[0].value
  
  // 模拟提交盘点结果
  const record = {
    id: Date.now(),
    assetName: '笔记本电脑',
    assetCode: currentAsset.value.assetCode,
    result: result,
    resultName: selectedOptions[0].text,
    checkedLocation: '总部-3F-IT部',
    actualDate: new Date()
  }
  
  records.value.unshift(record)
  checkedCount.value++
  showResultPicker.value = false
  
  // 震动反馈（如果支持）
  if (navigator.vibrate) {
    navigator.vibrate(100)
  }
}

// 编辑记录
const editRecord = (record) => {
  currentAsset.value = record
  showResultPicker.value = true
}

// 查看全部
const showAllRecords = () => {
  router.push(`/inventory/${taskId}`)
}

// 下拉刷新
const onRefresh = () => {
  setTimeout(() => {
    refreshing.value = false
  }, 1000)
}

// 加载更多
const onLoad = () => {
  setTimeout(() => {
    loading.value = false
    finished.value = true
  }, 1000)
}

onMounted(() => {
  // TODO: 加载盘点任务信息
})
</script>

<style scoped>
.scan-page {
  background: #f5f5f5;
  min-height: 100vh;
}

.progress-section {
  background: #fff;
  padding: 12px 16px;
  margin-bottom: 8px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.task-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.progress-text {
  font-size: 14px;
  color: #666;
}

.scan-section {
  background: #fff;
  padding: 24px 16px;
  margin-bottom: 8px;
}

.scan-box {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  color: #fff;
  margin-bottom: 16px;
}

.scan-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.scan-text {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 4px;
}

.scan-subtext {
  font-size: 12px;
  opacity: 0.8;
}

.manual-input {
  margin-top: 8px;
}

.recent-records {
  background: #fff;
  padding: 12px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px 12px;
  font-size: 14px;
  color: #666;
}

.result-tag {
  margin-top: 4px;
}

.record-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.swipe-btn {
  height: 100%;
}

:deep(.van-card) {
  background: #fff;
  margin: 0;
  padding: 12px 16px;
}

:deep(.van-card__title) {
  font-size: 15px;
  font-weight: 500;
}

:deep(.van-card__desc) {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>