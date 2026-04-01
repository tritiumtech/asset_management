<template>
  <div class="detail-page">
    <van-nav-bar 
      title="资产详情" 
      left-text="返回" 
      left-arrow 
      @click-left="goBack"
      fixed
      placeholder
    />
    
    <van-skeleton title avatar :row="10" :loading="loading">
      <!-- 资产图片 -->
      <div class="asset-image">
        <van-image
          width="100%"
          height="200"
          fit="cover"
          :src="asset.assetPhotoUrl || defaultImage"
        />
      </div>
      
      <!-- 基本信息 -->
      <van-cell-group inset class="info-group">
        <van-cell title="资产名称" :label="asset.assetName" />
        <van-cell title="资产编号">
          <template #value>
            <span class="code-text">{{ asset.assetCode }}</span>
            <van-icon name="bar-chart-o" class="copy-icon" @click="copyCode" />
          </template>
        </van-cell>
        
        <van-cell title="SN码" :value="asset.serialNumber" />
        <van-cell title="资产类别">
          <template #value>
            <van-tag type="primary">{{ asset.categoryName }}</van-tag>
          </template>
        </van-cell>
        
        <van-cell title="当前状态">
          <template #value>
            <van-tag :type="getStatusType(asset.status)">{{ asset.statusName }}</van-tag>
          </template>
        </van-cell>
      </van-cell-group>
      
      <!-- 使用信息 -->
      <van-cell-group inset class="info-group" title="使用信息">
        <van-cell title="当前责任人" :value="asset.currentUserName || '无'" />
        <van-cell title="所属部门" :value="asset.department" />
        <van-cell title="存放位置" :value="asset.locationDetail" />
        <van-cell title="领用日期" :value="asset.assignDate" />
      </van-cell-group>
      
      <!-- 采购信息 -->
      <van-cell-group inset class="info-group" title="采购信息">
        <van-cell title="品牌型号" :value="asset.brand + ' ' + asset.model" />
        <van-cell title="购置日期" :value="asset.purchaseDate" />
        <van-cell title="购置金额" :value="'¥' + asset.purchaseAmount" />
        <van-cell title="保修期至" :value="asset.warrantyDate" />
        <van-cell title="供应商" :value="asset.supplier" />
      </van-cell-group>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button type="primary" block @click="showAssignDialog">资产领用</van-button>
        <van-button type="warning" plain block @click="showTransferDialog" style="margin-top: 12px;">资产调拨</van-button>
        <van-button type="danger" plain block @click="showReturnDialog" style="margin-top: 12px;">资产归还</van-button>
      </div>
    </van-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const defaultImage = 'https://img.yzcdn.cn/vant/cat.jpeg'

const asset = ref({
  id: 1,
  assetName: 'MacBook Pro 16寸',
  assetCode: 'NB-26-0001',
  serialNumber: 'C02ZW1YJMD6T',
  category: 'NB',
  categoryName: '笔记本',
  status: 'IN_STOCK',
  statusName: '库存',
  brand: 'Apple',
  model: 'MacBook Pro 16" M3 Max',
  purchaseDate: '2026-01-15',
  purchaseAmount: '29999.00',
  warrantyDate: '2029-01-15',
  supplier: 'Apple官方',
  locationDetail: '总部-1F-仓库',
  currentUserName: '',
  department: 'IT部'
})

const getStatusType = (status) => {
  const types = {
    'IN_STOCK': 'success',
    'IN_USE': 'primary',
    'IN_REPAIR': 'warning',
    'SCRAPPED': 'danger'
  }
  return types[status] || 'default'
}

const goBack = () => {
  router.back()
}

const copyCode = () => {
  // TODO: 复制资产编号
  showToast('已复制')
}

const showAssignDialog = () => {
  // TODO: 领用弹窗
}

const showTransferDialog = () => {
  // TODO: 调拨弹窗
}

const showReturnDialog = () => {
  // TODO: 归还弹窗
}

onMounted(() => {
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<style scoped>
.detail-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20px;
}

.asset-image {
  background: #fff;
}

.info-group {
  margin-top: 12px;
}

.code-text {
  font-family: monospace;
  font-weight: bold;
  margin-right: 8px;
}

.copy-icon {
  color: #1989fa;
  font-size: 18px;
}

.action-buttons {
  padding: 20px 16px;
}

:deep(.van-cell__title) {
  flex: none;
  width: 100px;
}

:deep(.van-cell__value) {
  text-align: left;
  flex: 1;
}
</style>