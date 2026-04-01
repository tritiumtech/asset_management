<template>
  <div class="my-assets-page">
    <van-nav-bar 
      title="我的资产" 
      left-text="返回" 
      left-arrow 
      @click-left="goBack"
      fixed
      placeholder
    />
    
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-empty v-if="assets.length === 0" description="暂无资产" />
        
        <van-card
          v-for="asset in assets"
          :key="asset.id"
          :title="asset.assetName"
          :desc="asset.assetCode"
          :thumb="asset.assetPhotoUrl || defaultImage"
          @click="viewDetail(asset)"
        >
          <template #tags>
            <van-tag type="primary">{{ asset.categoryName }}</van-tag>
          </template>
          
          <template #footer>
            <div class="asset-info">
              <span>领用日期: {{ asset.assignDate }}</span>
            </div>
            <div class="action-row">
              <van-button size="small" type="primary" @click.stop="applyReturn(asset)">归还</van-button>
              <van-button size="small" type="warning" plain @click.stop="applyRepair(asset)">报修</van-button>
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
const refreshing = ref(false)
const loading = ref(false)
const finished = ref(false)
const defaultImage = 'https://img.yzcdn.cn/vant/cat.jpeg'

const assets = ref([
  {
    id: 1,
    assetName: 'MacBook Pro 16寸',
    assetCode: 'NB-26-0001',
    categoryName: '笔记本',
    assignDate: '2026-01-20'
  },
  {
    id: 2,
    assetName: 'Dell显示器 U2723QE',
    assetCode: 'DS-26-0005',
    categoryName: '显示器',
    assignDate: '2026-01-20'
  }
])

const goBack = () => {
  router.back()
}

const viewDetail = (asset) => {
  router.push(`/asset/${asset.id}`)
}

const applyReturn = (asset) => {
  // TODO: 申请归还
}

const applyRepair = (asset) => {
  // TODO: 申请报修
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
.my-assets-page {
  background: #f5f5f5;
  min-height: 100vh;
}

.asset-info {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.action-row {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

:deep(.van-card) {
  background: #fff;
  margin: 8px;
  border-radius: 8px;
}
</style>