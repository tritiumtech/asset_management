<template>
  <div class="asset-return-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      title="资产归还"
      left-arrow
      @click-left="goBack"
      right-text="提交"
      @click-right="submitReturn"
    />

    <div class="form-container">
      <!-- 选择归还资产 -->
      <div class="section-title">
        选择归还资产
        <span class="subtitle">已选择 {{ selectedAssets.length }} 个</span>
      </div>
      
      <div class="asset-select-section">
        <van-button 
          type="primary" 
          block 
          round 
          icon="plus"
          @click="showAssetSelector = true"
          style="margin-bottom: 12px;"
        >
          选择名下资产
        </van-button>
        
        <van-button 
          type="default" 
          block 
          round 
          icon="scan"
          @click="scanAsset"
        >
          扫码归还
        </van-button>
      </div>

      <!-- 已选资产列表 -->
      <div v-if="selectedAssets.length > 0" class="selected-assets">
        <van-cell-group inset>
          <van-cell 
            v-for="(asset, index) in selectedAssets" 
            :key="asset.id"
            :title="asset.name"
            :label="`编号: ${asset.assetNo} | SN: ${asset.sn}`"
            center
          >
            <template #right-icon>
              <van-icon name="delete-o" color="#ee0a24" @click="removeAsset(index)" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 归还信息 -->
      <div class="section-title">归还信息</div>
      <van-cell-group inset>
        <van-field
          v-model="form.returnDate"
          label="归还日期"
          placeholder="请选择"
          required
          readonly
          @click="showDatePicker = true"
        />
        
        <van-field
          v-model="form.returnType"
          label="归还类型"
          placeholder="请选择"
          required
          readonly
          @click="showTypePicker = true"
        />
        
        <van-field
          v-model="form.location"
          label="归还位置"
          placeholder="请选择归还地点"
          required
          readonly
          @click="showLocationPicker = true"
        />
        
        <van-field
          v-model="form.condition"
          label="资产状态"
          placeholder="请选择资产状态"
          required
          readonly
          @click="showConditionPicker = true"
        />
        
        <van-field
          v-if="form.condition === '损坏'"
          v-model="form.damageDesc"
          label="损坏说明"
          type="textarea"
          rows="3"
          placeholder="请描述损坏情况"
          required
        />
        
        <van-field
          v-model="form.remark"
          label="备注"
          type="textarea"
          rows="2"
          placeholder="其他说明（可选）"
        />
      </van-cell-group>

      <!-- 资产照片 -->
      <div class="section-title">资产照片</div>
      <van-cell-group inset>
        <div class="upload-section">
          <div class="upload-label">归还时资产照片</div>
          <van-uploader 
            v-model="form.photos" 
            :max-count="5"
            upload-text="上传照片"
          />
        </div>
      </van-cell-group>

      <!-- 确认信息 -->
      <div class="section-title">确认信息</div>
      <van-cell-group inset>
        <van-cell title="归还人" :value="currentUser.name" />
        <van-cell title="所属部门" :value="currentUser.dept" />
        <van-cell title="资产管理员验收" :value="approver.assetAdmin" />
      </van-cell-group>

      <!-- 归还须知 -->
      <div class="notice-section">
        <van-notice-bar
          left-icon="info-o"
          text="归还须知：资产归还后将由资产管理员验收，如有损坏需按公司规定赔偿"
        />
      </div>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <van-checkbox v-model="form.confirmed" style="margin-bottom: 12px;">
          我已确认资产完好，同意归还
        </van-checkbox>
        <van-button type="primary" block round @click="submitReturn">提交归还申请</van-button>
      </div>
    </div>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        title="选择归还日期"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 归还类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom">
      <van-picker
        :columns="typeColumns"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
        show-toolbar
        title="选择归还类型"
      />
    </van-popup>

    <!-- 位置选择器 -->
    <van-popup v-model:show="showLocationPicker" position="bottom">
      <van-picker
        :columns="locationColumns"
        @confirm="onLocationConfirm"
        @cancel="showLocationPicker = false"
        show-toolbar
        title="选择归还位置"
      />
    </van-popup>

    <!-- 资产状态选择器 -->
    <van-popup v-model:show="showConditionPicker" position="bottom">
      <van-picker
        :columns="conditionColumns"
        @confirm="onConditionConfirm"
        @cancel="showConditionPicker = false"
        show-toolbar
        title="选择资产状态"
      />
    </van-popup>

    <!-- 资产选择器 -->
    <van-popup v-model:show="showAssetSelector" position="bottom" :style="{ height: '80%' }">
      <div class="asset-picker">
        <div class="picker-header">
          <span>选择要归还的资产</span>
          <van-icon name="cross" @click="showAssetSelector = false" />
        </div>
        <van-search
          v-model="assetSearchKey"
          placeholder="搜索资产编号、名称"
        />
        <van-list class="picker-list">
          <van-cell
            v-for="asset in myAssets"
            :key="asset.id"
            :title="asset.name"
            :label="`${asset.assetNo} | ${asset.category}`"
            @click="selectAsset(asset)"
          >
            <template #right-icon>
              <van-icon v-if="isSelected(asset)" name="success" color="#1989fa" />
            </template>
          </van-cell>
        </van-list>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'

const router = useRouter()

// 当前用户
const currentUser = ref({
  name: '张三',
  dept: '技术部'
})

// 审批人
const approver = ref({
  assetAdmin: '李四（资产管理员）'
})

// 表单数据
const form = reactive({
  returnDate: '',
  returnType: '',
  location: '',
  condition: '',
  damageDesc: '',
  remark: '',
  photos: [],
  confirmed: false
})

// 已选资产
const selectedAssets = ref([])

// 弹窗控制
const showDatePicker = ref(false)
const showTypePicker = ref(false)
const showLocationPicker = ref(false)
const showConditionPicker = ref(false)
const showAssetSelector = ref(false)

// 选择器数据
const maxDate = new Date()

const typeColumns = [
  { text: '正常归还', value: '正常归还' },
  { text: '离职归还', value: '离职归还' },
  { text: '调岗归还', value: '调岗归还' },
  { text: '借用到期归还', value: '借用到期归还' },
  { text: '设备更新归还', value: '设备更新归还' }
]

const locationColumns = [
  { text: '总部IT部', value: '总部IT部' },
  { text: '总部仓库', value: '总部仓库' },
  { text: '深圳店', value: '深圳店' },
  { text: '北京店', value: '北京店' },
  { text: '上海店', value: '上海店' }
]

const conditionColumns = [
  { text: '完好', value: '完好' },
  { text: '轻微磨损', value: '轻微磨损' },
  { text: '损坏', value: '损坏' },
  { text: '丢失配件', value: '丢失配件' }
]

// 我的资产列表
const assetSearchKey = ref('')
const myAssets = ref([
  { id: 1, name: 'MacBook Pro 16寸', assetNo: 'NB-26-0001', sn: 'C02ABC123456', category: '笔记本类' },
  { id: 2, name: 'Dell显示器 U2723QE', assetNo: 'DS-26-0005', sn: 'CN123456789', category: '显示类' },
  { id: 3, name: 'Keychron K3键盘', assetNo: 'PE-26-0012', sn: 'KEY20260012', category: '外设类' }
])

const isSelected = (asset) => {
  return selectedAssets.value.some(a => a.id === asset.id)
}

const goBack = () => {
  router.back()
}

const onDateConfirm = ({ selectedValues }) => {
  form.returnDate = selectedValues.join('-')
  showDatePicker.value = false
}

const onTypeConfirm = ({ selectedOptions }) => {
  form.returnType = selectedOptions[0].value
  showTypePicker.value = false
}

const onLocationConfirm = ({ selectedOptions }) => {
  form.location = selectedOptions[0].value
  showLocationPicker.value = false
}

const onConditionConfirm = ({ selectedOptions }) => {
  form.condition = selectedOptions[0].value
  showConditionPicker.value = false
}

const selectAsset = (asset) => {
  if (isSelected(asset)) {
    selectedAssets.value = selectedAssets.value.filter(a => a.id !== asset.id)
  } else {
    selectedAssets.value.push(asset)
  }
}

const removeAsset = (index) => {
  selectedAssets.value.splice(index, 1)
}

const scanAsset = () => {
  showToast('扫码功能开发中...')
}

const submitReturn = async () => {
  // 表单验证
  if (selectedAssets.value.length === 0) {
    showToast('请至少选择一个要归还的资产')
    return
  }
  if (!form.returnDate) {
    showToast('请选择归还日期')
    return
  }
  if (!form.returnType) {
    showToast('请选择归还类型')
    return
  }
  if (!form.location) {
    showToast('请选择归还位置')
    return
  }
  if (!form.condition) {
    showToast('请选择资产状态')
    return
  }
  if (form.condition === '损坏' && !form.damageDesc) {
    showToast('请描述损坏情况')
    return
  }
  if (!form.confirmed) {
    showToast('请确认归还信息')
    return
  }
  
  showLoadingToast({
    message: '提交中...',
    forbidClick: true
  })
  
  // TODO: 调用API提交归还申请
  setTimeout(() => {
    closeToast()
    showToast('归还申请提交成功')
    router.push('/work')
  }, 1500)
}
</script>

<style scoped>
.asset-return-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 24px;
}

.form-container {
  padding: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  padding: 16px 16px 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subtitle {
  font-size: 12px;
  color: #1989fa;
  font-weight: normal;
}

.asset-select-section {
  padding: 0 16px;
}

.selected-assets {
  padding: 0 16px;
  margin-top: 12px;
}

.upload-section {
  padding: 12px 16px;
}

.upload-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.notice-section {
  padding: 16px;
}

.submit-section {
  padding: 24px 16px;
}

/* 资产选择器 */
.asset-picker {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  font-size: 16px;
  font-weight: 500;
  border-bottom: 1px solid #ebedf0;
}

.picker-list {
  flex: 1;
  overflow-y: auto;
}
</style>