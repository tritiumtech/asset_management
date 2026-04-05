<template>
  <div class="asset-receive-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      :title="isEdit ? '编辑领用申请' : '资产领用'"
      left-arrow
      @click-left="goBack"
      right-text="提交"
      @click-right="submitApply"
    />

    <div class="form-container">
      <!-- 领用类型 -->
      <div class="section-title">领用信息</div>
      <van-cell-group inset>
        <van-field
          v-model="form.receiveType"
          label="领用类型"
          placeholder="请选择"
          required
          readonly
          @click="showTypePicker = true"
        />
        
        <van-field
          v-if="form.receiveType === '新员工入职'"
          v-model="form.newEmployee"
          label="新员工姓名"
          placeholder="请输入新员工姓名"
          required
        />
        
        <van-field
          v-if="form.receiveType === '设备更新'"
          v-model="form.oldAsset"
          label="旧设备编号"
          placeholder="请输入旧设备资产编号"
        />
        
        <van-field
          v-model="form.receiveDate"
          label="期望领用日期"
          placeholder="请选择"
          readonly
          @click="showDatePicker = true"
        />
        
        <van-field
          v-model="form.reason"
          label="领用事由"
          type="textarea"
          rows="3"
          placeholder="请说明领用原因"
          required
        />
      </van-cell-group>

      <!-- 选择资产 -->
      <div class="section-title">
        选择资产
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
          从库存选择资产
        </van-button>
        
        <van-button 
          type="default" 
          block 
          round 
          icon="scan"
          @click="scanAsset"
        >
          扫码添加资产
        </van-button>
      </div>

      <!-- 已选资产列表 -->
      <div v-if="selectedAssets.length > 0" class="selected-assets">
        <van-swipe-cell v-for="(asset, index) in selectedAssets" :key="asset.id">
          <van-card
            class="asset-card"
            :title="asset.name"
            :desc="`${asset.category} | ${asset.assetNo}`"
          >
            <template #tags>
              <van-tag type="success">库存</van-tag>
            </template>
            <template #footer>
              <span class="asset-sn">SN: {{ asset.sn }}</span>
            </template>
          </van-card>
          
          <template #right>
            <van-button 
              square 
              text="删除" 
              type="danger" 
              class="delete-button"
              @click="removeAsset(index)"
            />
          </template>
        </van-swipe-cell>
      </div>

      <!-- 领用人信息 -->
      <div class="section-title">领用人信息</div>
      <van-cell-group inset>
        <van-field
          v-model="form.receiverName"
          label="领用人"
          placeholder="请选择领用人"
          required
          readonly
          @click="showUserPicker = true"
        />
        
        <van-field
          v-model="form.department"
          label="所属部门"
          placeholder="自动带出"
          disabled
        />
        
        <van-field
          v-model="form.position"
          label="职位"
          placeholder="自动带出"
          disabled
        />
        
        <van-field
          v-model="form.contact"
          label="联系电话"
          placeholder="请输入联系电话"
        />
        
        <van-field
          v-model="form.location"
          label="使用地点"
          placeholder="如: 总部3楼IT部"
        />
      </van-cell-group>

      <!-- 审批流程 -->
      <div class="section-title">审批流程</div>
      <van-cell-group inset>
        <van-cell title="部门负责人审批" :label="approver.deptManager" is-link>
          <template #right-icon>
            <van-tag type="primary">待审批</van-tag>
          </template>
        </van-cell>
        
        <van-cell title="资产管理员确认" :label="approver.assetAdmin" is-link>
          <template #right-icon>
            <van-tag>待确认</van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 附件 -->
      <div class="section-title">附件（可选）</div>
      <van-cell-group inset>
        <div class="upload-section">
          <van-uploader 
            v-model="form.attachments" 
            :max-count="5"
            upload-text="上传附件"
          />
        </div>
      </van-cell-group>
    </div>

    <!-- 领用类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom">
      <van-picker
        :columns="typeColumns"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
        show-toolbar
        title="选择领用类型"
      />
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        title="选择期望领用日期"
        :min-date="minDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 人员选择器 -->
    <van-popup v-model:show="showUserPicker" position="bottom" :style="{ height: '70%' }">
      <div class="user-picker">
        <div class="picker-header">
          <span>选择领用人</span>
          <van-icon name="cross" @click="showUserPicker = false" />
        </div>
        <van-search
          v-model="userSearchKey"
          placeholder="搜索姓名、工号"
        />
        <van-list>
          <van-cell
            v-for="user in filteredUsers"
            :key="user.id"
            :title="user.name"
            :label="`${user.dept} | ${user.position}`"
            @click="selectUser(user)"
          >
            <template #right-icon>
              <van-icon v-if="form.receiverId === user.id" name="success" color="#1989fa" />
            </template>
          </van-cell>
        </van-list>
      </div>
    </van-popup>

    <!-- 资产选择器 -->
    <van-popup v-model:show="showAssetSelector" position="bottom" :style="{ height: '80%' }">
      <div class="asset-picker">
        <div class="picker-header">
          <span>选择资产</span>
          <van-icon name="cross" @click="showAssetSelector = false" />
        </div>
        <van-search
          v-model="assetSearchKey"
          placeholder="搜索资产编号、名称、SN码"
        />
        <van-tabs v-model:active="pickerCategory">
          <van-tab title="全部"></van-tab>
          <van-tab title="笔记本"></van-tab>
          <van-tab title="显示器"></van-tab>
          <van-tab title="主机"></van-tab>
        </van-tabs>
        <van-list class="picker-list">
          <van-card
            v-for="asset in availableAssets"
            :key="asset.id"
            class="picker-asset-card"
            :title="asset.name"
            :desc="`${asset.assetNo} | SN:${asset.sn}`"
            @click="toggleSelectAsset(asset)"
          >
            <template #tags>
              <van-tag type="success">库存</van-tag>
              <van-tag v-if="isSelected(asset)" type="primary">已选</van-tag>
            </template>
          </van-card>
        </van-list>
        <div class="picker-footer">
          <van-button type="primary" block @click="confirmSelectAssets">
            确定选择 ({{ tempSelected.length }})
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.query.id)

// 表单数据
const form = reactive({
  receiveType: '',
  newEmployee: '',
  oldAsset: '',
  receiveDate: '',
  reason: '',
  receiverId: '',
  receiverName: '',
  department: '',
  position: '',
  contact: '',
  location: '',
  attachments: []
})

// 审批人
const approver = ref({
  deptManager: '张三（技术部经理）',
  assetAdmin: '李四（资产管理员）'
})

// 已选资产
const selectedAssets = ref([])

// 弹窗控制
const showTypePicker = ref(false)
const showDatePicker = ref(false)
const showUserPicker = ref(false)
const showAssetSelector = ref(false)

// 选择器数据
const typeColumns = [
  { text: '新员工入职', value: '新员工入职' },
  { text: '设备更新', value: '设备更新' },
  { text: '岗位调整', value: '岗位调整' },
  { text: '临时借用转正式', value: '临时借用转正式' },
  { text: '其他', value: '其他' }
]

const minDate = new Date()

// 人员搜索
const userSearchKey = ref('')
const users = ref([
  { id: 1, name: '张三', dept: '技术部', position: '高级工程师' },
  { id: 2, name: '李四', dept: '产品部', position: '产品经理' },
  { id: 3, name: '王五', dept: '设计部', position: 'UI设计师' },
  { id: 4, name: '赵六', dept: '运营部', position: '运营专员' }
])

const filteredUsers = computed(() => {
  if (!userSearchKey.value) return users.value
  return users.value.filter(u => 
    u.name.includes(userSearchKey.value) || 
    u.dept.includes(userSearchKey.value)
  )
})

// 资产选择器
const assetSearchKey = ref('')
const pickerCategory = ref(0)
const tempSelected = ref([])

const availableAssets = ref([
  { id: 1, name: 'MacBook Pro 16寸', assetNo: 'NB-26-0045', sn: 'C02XYZ123456', category: '笔记本类' },
  { id: 2, name: 'ThinkPad X1 Carbon', assetNo: 'NB-26-0046', sn: 'PF1ABC789012', category: '笔记本类' },
  { id: 3, name: 'Dell显示器 U2723QE', assetNo: 'DS-26-0123', sn: 'CN123456789', category: '显示类' },
  { id: 4, name: 'Keychron K3键盘', assetNo: 'PE-26-0234', sn: 'KEY20260001', category: '外设类' }
])

const isSelected = (asset) => {
  return tempSelected.value.some(a => a.id === asset.id)
}

// 初始化 - 如果有assetId参数，自动添加该资产
if (route.query.assetId) {
  const asset = availableAssets.value.find(a => a.id === parseInt(route.query.assetId))
  if (asset) {
    selectedAssets.value.push(asset)
  }
}

const goBack = () => {
  router.back()
}

const onTypeConfirm = ({ selectedOptions }) => {
  form.receiveType = selectedOptions[0].value
  showTypePicker.value = false
}

const onDateConfirm = ({ selectedValues }) => {
  form.receiveDate = selectedValues.join('-')
  showDatePicker.value = false
}

const selectUser = (user) => {
  form.receiverId = user.id
  form.receiverName = user.name
  form.department = user.dept
  form.position = user.position
  showUserPicker.value = false
}

const toggleSelectAsset = (asset) => {
  const index = tempSelected.value.findIndex(a => a.id === asset.id)
  if (index > -1) {
    tempSelected.value.splice(index, 1)
  } else {
    tempSelected.value.push(asset)
  }
}

const confirmSelectAssets = () => {
  selectedAssets.value.push(...tempSelected.value)
  tempSelected.value = []
  showAssetSelector.value = false
}

const removeAsset = (index) => {
  selectedAssets.value.splice(index, 1)
}

const scanAsset = () => {
  showToast('扫码功能开发中...')
}

const submitApply = async () => {
  // 表单验证
  if (!form.receiveType) {
    showToast('请选择领用类型')
    return
  }
  if (!form.reason) {
    showToast('请填写领用事由')
    return
  }
  if (selectedAssets.value.length === 0) {
    showToast('请至少选择一个资产')
    return
  }
  if (!form.receiverName) {
    showToast('请选择领用人')
    return
  }
  
  showLoadingToast({
    message: '提交中...',
    forbidClick: true
  })
  
  // TODO: 调用API提交申请
  setTimeout(() => {
    closeToast()
    showToast('提交成功')
    router.push('/work')
  }, 1500)
}
</script>

<style scoped>
.asset-receive-page {
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

.asset-card {
  margin-bottom: 8px;
  background: #fff;
}

.asset-sn {
  font-size: 12px;
  color: #969799;
}

.delete-button {
  height: 100%;
}

.upload-section {
  padding: 12px 16px;
}

/* 人员选择器 */
.user-picker,
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
  padding: 8px;
}

.picker-asset-card {
  margin-bottom: 8px;
  background: #fff;
}

.picker-footer {
  padding: 16px;
  border-top: 1px solid #ebedf0;
}
</style>