<template>
  <div class="approval-page">
    <van-nav-bar :title="title" left-arrow @click-left="goBack" />
    
    <div class="content">
      <van-empty v-if="approvalList.length === 0" description="暂无待办审批" />
      
      <van-cell-group v-else inset>
        <van-cell 
          v-for="item in approvalList" 
          :key="item.id"
          clickable
          @click="handleApproval(item)"
        >
          <template #title>
            <div class="approval-title">{{ item.title }}</div>
          </template>
          <template #label>
            <div class="approval-info">
              <div>申请人: {{ item.applicant }}</div>
              <div>申请时间: {{ item.createTime }}</div>
            </div>
          </template>
          <template #value>
            <van-tag :type="item.statusType">{{ item.status }}</van-tag>
          </template>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showDialog, showToast } from 'vant'

const router = useRouter()
const route = useRoute()

const type = computed(() => route.params.type || 'transfer')

const title = computed(() => {
  const titles = {
    transfer: '调拨审批',
    scrap: '报废审批',
    assign: '领用审批'
  }
  return titles[type.value] || '审批中心'
})

const approvalList = ref([
  {
    id: 1,
    title: 'MacBook Pro 调拨申请',
    applicant: '张三',
    createTime: '2026-04-01 10:30',
    status: '待审批',
    statusType: 'warning',
    type: 'transfer'
  },
  {
    id: 2,
    title: 'iPhone 15 Pro 领用申请',
    applicant: '李四',
    createTime: '2026-04-01 09:15',
    status: '待审批',
    statusType: 'warning',
    type: 'assign'
  },
  {
    id: 3,
    title: '旧显示器报废申请',
    applicant: '王经理',
    createTime: '2026-03-31 16:45',
    status: '待审批',
    statusType: 'warning',
    type: 'scrap'
  }
])

const handleApproval = (item) => {
  showDialog({
    title: '审批操作',
    message: `审批: ${item.title}`,
    showCancelButton: true,
    confirmButtonText: '同意',
    cancelButtonText: '拒绝'
  }).then(() => {
    showToast('已同意')
  }).catch(() => {
    showToast('已拒绝')
  })
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.approval-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  padding: 12px;
}

.approval-title {
  font-weight: bold;
  color: #333;
}

.approval-info {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>