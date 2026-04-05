<template>
  <div class="asset-borrow-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      :title="isEdit ? '编辑借用申请' : '资产借用'"
      left-arrow
      @click-left="goBack"
      right-text="提交"
      @click-right="submitApply"
    />

    <div class="form-container">
      <!-- 借用信息 -->
      <div class="section-title">借用信息</div>
      <van-cell-group inset>
        <van-field
          v-model="form.borrowType"
          label="借用类型"
          placeholder="请选择"
          required
          readonly
          @click="showTypePicker = true"
        />
        
        <van-field
          v-model="form.projectName"
          label="项目名称"
          placeholder="请输入项目名称（如适用）"
        />
        
        <van-field
          v-model="form.borrowDate"
          label="借用日期"
          placeholder="请选择"
          required
          readonly
          @click="showBorrowDatePicker = true"
        />
        
        <van-field
          v-model="form.returnDate"
          label="预计归还日期"
          placeholder="请选择"
          required
          readonly
          @click="showReturnDatePicker = true"
        />
        
        <van-field
          v-model="form.duration"
          label="借用时长"
          disabled
          :placeholder="calculateDuration"
        />
        
        <van-field
          v-model="form.reason"
          label="借用事由"
          type="textarea"
          rows="3"
          placeholder="请说明借用原因和用途"
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

      <!-- 借用人信息 -->
      <div class="section-title">借用人信息</div>
      <van-cell-group inset>
        <van-field
          v-model="form.borrowerName"
          label="借用人"
          placeholder="请选择借用人"
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
          v-model="form.contact"
          label="联系电话"
          placeholder="请输入联系电话"
          required
        />
        
        <van-field
          v-model="form.useLocation"
          label="使用地点"
          placeholder="如: 总部3楼会议室"
          required
        />
      </van-cell-group>

      <!-- 担保信息 -->
      <div class="section-title">担保信息（长期借用需要）</div>
      <van-cell-group inset>
        <van-field
          v-model="form.guarantorName"
          label="担保人"
          placeholder="请选择担保人（部门负责人）"
          readonly
          @click="showGuarantorPicker = true"
        />
        
        <van-cell title="借用期限说明" label="超过30天的借用需要部门负责人担保">
          <template #right-icon>
            <van-icon name="info-o" color="#1989fa" />
          </template>
        </van-cell>
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

      <!-- 借用须知 -->
      <div class="notice-section">
        <van-notice-bar
          left-icon="info-o"
          text="借用须知：1.借用资产需按时归还；2.损坏需照价赔偿；3.超期需提前申请续借"
        />
      </div>
    </div>

    <!-- 借用类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom">
      <van-picker
        :columns="typeColumns"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
        show-toolbar
        title="选择借用类型"
      />
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showBorrowDatePicker" position="bottom">
      <van-date-picker
        title="选择借用日期"
        :min-date="minDate"
        @confirm="onBorrowDateConfirm"
        @cancel="showBorrowDatePicker = false"
      />
    </van-popup>

    <van-popup v-model:show="showReturnDatePicker" position="bottom">
      <van-date-picker
        title="选择预计归还日期"
        :min-date="minReturnDate"
        @confirm="onReturnDateConfirm"
        @cancel="showReturnDatePicker = false"
      />
    </van-popup>

    <!-- 人员选择器 -->
    <van-popup v-model:show="showUserPicker" position="bottom" :style="{ height: '70%' }">
      <div class="user-picker">
        <div class="picker-header">
          <span>选择借用人</span>
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
              <van-icon v-if="form.borrowerId === user.id" name="success" color="#1989fa" />
            </template>
          </van-cell>
        </van-list>
      </div>
    </van-popup>

    <!-- 担保人选择器 -->
    <van-popup v-model:show="showGuarantorPicker" position="bottom" :style="{ height: '70%' }">
      <div class="user-picker">
        <div class="picker-header">
          <span>选择担保人</span>
          <van-icon name="cross" @click="showGuarantorPicker = false" />
        </div>
        <van-search
          v-model="guarantorSearchKey"
          placeholder="搜索姓名"
        />
        <van-list>
          <van-cell
            v-for="user in filteredGuarantors"
            :key="user.id"
            :title="user.name"
            :label="`${user.dept} | ${user.position}`"
            @click="selectGuarantor(user)"
          >
            <template #right-icon>
              <van-icon v-if="form.guarantorId === user.id" name="success" color="#1989fa" />
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
  borrowType: '',
  projectName: '',
  borrowDate: '',
  returnDate: '',
  duration: '',
  reason: '',
  borrowerId: '',
  borrowerName: '',
  department: '',
  contact: '',
  useLocation: '',
  guarantorId: '',
  guarantorName: '',
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
const showBorrowDatePicker = ref(false)
const showReturnDatePicker = ref(false)
const showUserPicker = ref(false)
const showGuarantorPicker = ref(false)
const showAssetSelector = ref(false)

// 选择器数据
const typeColumns = [
  { text: '项目临时借用', value: '项目临时借用' },
  { text: '会议/活动借用', value: '会议/活动借用' },
  { text: '短期测试借用', value: '短期测试借用' },
  { text: '长期借用（>30天）', value: '长期借用' },
  { text: '其他', value: '其他' }
]

const minDate = new Date()
const minReturnDate = computed(() => {
  if (form.borrowDate) {
    const date = new Date(form.borrowDate)
    date.setDate(date.getDate() + 1)
    return date
  }
  return new Date()
})

// 计算借用时长
const calculateDuration = computed(() => {
  if (form.borrowDate && form.returnDate) {
    const start = new Date(form.borrowDate)
    const end = new Date(form.returnDate)
    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
    return days > 0 ? `${days} 天` : ''
  }
  return ''
})

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

// 担保人搜索
const guarantorSearchKey = ref('')
const guarantors = ref([
  { id: 101, name: '张三', dept: '技术部', position: '技术部经理' },
  { id: 102, name: '王经理', dept: '产品部', position: '产品总监' },
  { id: 103, name: '李总监', dept: '设计部', position: '设计总监' }
])

const filteredGuarantors = computed(() => {
  if (!guarantorSearchKey.value) return guarantors.value
  return guarantors.value.filter(u => 
    u.name.includes(guarantorSearchKey.value)
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

const goBack = () => {
  router.back()
}

const onTypeConfirm = ({ selectedOptions }) => {
  form.borrowType = selectedOptions[0].value
  showTypePicker.value = false
}

const onBorrowDateConfirm = ({ selectedValues }) => {
  form.borrowDate = selectedValues.join('-')
  showBorrowDatePicker.value = false
}

const onReturnDateConfirm = ({ selectedValues }) => {
  form.returnDate = selectedValues.join('-')
  showReturnDatePicker.value = false
}

const selectUser = (user) => {
  form.borrowerId = user.id
  form.borrowerName = user.name
  form.department = user.dept
  showUserPicker.value = false
}

const selectGuarantor = (user) => {
  form.guarantorId = user.id
  form.guarantorName = user.name
  showGuarantorPicker.value = false
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
  if (!form.borrowType) {
    showToast('请选择借用类型')
    return
  }
  if (!form.borrowDate || !form.returnDate) {
    showToast('请选择借用和归还日期')
    return
  }
  if (!form.reason) {
    showToast('请填写借用事由')
    return
  }
  if (selectedAssets.value.length === 0) {
    showToast('请至少选择一个资产')
    return
  }
  if (!form.borrowerName) {
    showToast('请选择借用人')
    return
  }
  if (!form.contact) {
    showToast('请输入联系电话')
    return
  }
  if (!form.useLocation) {
    showToast('请输入使用地点')
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
.asset-borrow-page {
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

.notice-section {
  padding: 16px;
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