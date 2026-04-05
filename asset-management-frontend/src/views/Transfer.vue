<template>
  <div class="transfer-page">
    <van-nav-bar title="调拨管理" left-arrow @click-left="goBack" />
    
    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="调拨申请">
        <div class="tab-content">
          <van-empty description="暂无调拨申请功能" />
        </div>
      </van-tab>
      <van-tab title="调拨记录">
        <div class="tab-content">
          <van-cell-group inset>
            <van-cell 
              v-for="item in transferList" 
              :key="item.id"
              :title="item.assetName"
              :label="`${item.fromUser} → ${item.toUser}`"
              :value="item.status"
            />
          </van-cell-group>
          <van-empty v-if="transferList.length === 0" description="暂无调拨记录" />
        </div>
      </van-tab>
    </van-tabs>
    
    <div class="footer-btn">
      <van-button block type="primary" @click="showApply = true">发起调拨申请</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref(0)
const showApply = ref(false)

const transferList = ref([
  { id: 1, assetName: 'MacBook Pro 14寸', fromUser: '张三', toUser: '李四', status: '审批中' },
  { id: 2, assetName: 'Dell显示器 U2723QE', fromUser: '王经理', toUser: '赵经理', status: '已完成' }
])

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.transfer-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.tab-content {
  padding: 12px;
}

.footer-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.05);
}
</style>