<template>
  <div class="repair-page">
    <van-nav-bar title="报修申请" left-arrow @click-left="goBack" />
    
    <div class="content">
      <van-form @submit="onSubmit">
        <van-cell-group inset title="选择资产">
          <van-field
            v-model="form.assetName"
            name="asset"
            label="报修资产"
            placeholder="请选择需要报修的资产"
            readonly
            @click="showAssetPicker = true"
            :rules="[{ required: true, message: '请选择资产' }]"
          />
        </van-cell-group>
        
        <van-cell-group inset title="故障描述">
          <van-field
            v-model="form.description"
            name="description"
            label="故障现象"
            type="textarea"
            rows="3"
            placeholder="请描述设备故障情况"
            :rules="[{ required: true, message: '请描述故障' }]"
          />
          <van-field
            v-model="form.urgency"
            name="urgency"
            label="紧急程度"
            readonly
            @click="showUrgencyPicker = true"
          />
        </van-cell-group>
        
        <van-cell-group inset title="联系方式">
          <van-field
            v-model="form.contactPhone"
            name="contactPhone"
            label="联系电话"
            placeholder="请输入联系电话"
          />
          <van-field
            v-model="form.contactLocation"
            name="contactLocation"
            label="所在位置"
            placeholder="请输入设备所在位置"
          />
        </van-cell-group>
        
        <div class="submit-btn">
          <van-button block type="primary" native-type="submit">提交报修申请</van-button>
        </div>
      </van-form>
      
      <!-- 历史报修记录 -->
      <div class="section-title">历史报修记录</div>
      <van-cell-group inset>
        <van-cell 
          v-for="item in repairHistory" 
          :key="item.id"
          :title="item.assetName"
          :label="item.createTime"
          :value="item.status"
        />
      </van-cell-group>
      <van-empty v-if="repairHistory.length === 0" description="暂无报修记录" />
    </div>
    
    <!-- 资产选择弹窗 -->
    <van-popup v-model:show="showAssetPicker" position="bottom">
      <van-picker
        :columns="assetColumns"
        @confirm="onAssetConfirm"
        @cancel="showAssetPicker = false"
      />
    </van-popup>
    
    <!-- 紧急程度选择 -->
    <van-popup v-model:show="showUrgencyPicker" position="bottom">
      <van-picker
        :columns="['一般', '紧急', '特急']"
        @confirm="onUrgencyConfirm"
        @cancel="showUrgencyPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

const form = ref({
  assetName: '',
  assetId: '',
  description: '',
  urgency: '一般',
  contactPhone: '',
  contactLocation: ''
})

const showAssetPicker = ref(false)
const showUrgencyPicker = ref(false)

const assetColumns = ref([
  { text: 'MacBook Pro 14寸', value: '1' },
  { text: 'Dell显示器 U2723QE', value: '2' },
  { text: 'iPhone 15 Pro', value: '3' }
])

const repairHistory = ref([
  { id: 1, assetName: 'MacBook Pro 14寸', status: '维修中', createTime: '2026-03-15' },
  { id: 2, assetName: 'Dell显示器', status: '已完成', createTime: '2026-02-20' }
])

const onAssetConfirm = ({ selectedOptions }) => {
  form.value.assetName = selectedOptions[0].text
  form.value.assetId = selectedOptions[0].value
  showAssetPicker.value = false
}

const onUrgencyConfirm = (value) => {
  form.value.urgency = value
  showUrgencyPicker.value = false
}

const onSubmit = () => {
  showToast('报修申请已提交')
  router.back()
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.repair-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  padding: 12px;
}

.submit-btn {
  margin: 20px 16px;
}

.section-title {
  font-size: 14px;
  color: #999;
  margin: 20px 16px 12px;
}
</style>