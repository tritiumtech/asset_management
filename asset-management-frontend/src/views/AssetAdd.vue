<template>
  <div class="asset-add-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      title="资产录入"
      left-arrow
      @click-left="goBack"
      right-text="保存"
      @click-right="saveAsset"
    />

    <div class="form-container">
      <!-- 基本信息 -->
      <div class="section-title">基本信息</div>
      
      <van-cell-group inset>
        <van-field
          v-model="form.assetNo"
          label="资产编号"
          placeholder="系统自动生成"
          disabled
        />
        
        <van-field
          v-model="form.name"
          label="资产名称"
          placeholder="请输入资产名称"
          required
        />
        
        <van-field
          v-model="form.categoryName"
          label="资产分类"
          placeholder="请选择分类"
          required
          readonly
          @click="showCategoryPicker = true"
        />
        
        <van-field
          v-model="form.subCategoryName"
          label="二级分类"
          placeholder="请选择二级分类"
          readonly
          @click="showSubCategoryPicker = true"
        />
        
        <van-field
          v-model="form.brand"
          label="品牌"
          placeholder="如: Apple、Dell"
        />
        
        <van-field
          v-model="form.model"
          label="型号"
          placeholder="如: MacBook Pro 16寸"
        />
        
        <van-field
          v-model="form.sn"
          label="SN码"
          placeholder="请输入设备序列号"
          required
        />
      </van-cell-group>

      <!-- 采购信息 -->
      <div class="section-title">采购信息</div>
      
      <van-cell-group inset>
        <van-field
          v-model="form.purchaseDate"
          label="购置日期"
          placeholder="请选择日期"
          readonly
          @click="showDatePicker = true"
        />
        
        <van-field
          v-model="form.purchasePrice"
          label="购置金额"
          placeholder="请输入金额（元）"
          type="number"
        />
        
        <van-field
          v-model="form.supplier"
          label="供应商"
          placeholder="请输入供应商名称"
        />
        
        <van-field
          v-model="form.warrantyDate"
          label="保修期至"
          placeholder="请选择日期"
          readonly
          @click="showWarrantyPicker = true"
        />
      </van-cell-group>

      <!-- 存放信息 -->
      <div class="section-title">存放信息</div>
      
      <van-cell-group inset>
        <van-field
          v-model="form.locationType"
          label="存放类型"
          placeholder="请选择"
          readonly
          @click="showLocationTypePicker = true"
        />
        
        <van-field
          v-model="form.locationDetail"
          label="存放位置"
          placeholder="如: 总部3楼IT部"
        />
        
        <van-field
          v-model="form.status"
          label="资产状态"
          placeholder="请选择"
          readonly
          @click="showStatusPicker = true"
        />
      </van-cell-group>

      <!-- 资产照片 -->
      <div class="section-title">资产照片</div>
      
      <van-cell-group inset>
        <div class="upload-section">
          <div class="upload-label">设备正面照</div>
          <van-uploader 
            v-model="form.photos.front" 
            :max-count="1"
            upload-text="上传照片"
          />
        </div>
        
        <div class="upload-section">
          <div class="upload-label">SN码照片</div>
          <van-uploader 
            v-model="form.photos.sn" 
            :max-count="1"
            upload-text="上传照片"
          />
        </div>
        
        <div class="upload-section">
          <div class="upload-label">购置凭证</div>
          <van-uploader 
            v-model="form.photos.invoice" 
            :max-count="3"
            upload-text="上传发票/收据"
          />
        </div>
      </van-cell-group>

      <!-- 备注 -->
      <div class="section-title">备注</div>
      
      <van-cell-group inset>
        <van-field
          v-model="form.remark"
          type="textarea"
          rows="3"
          placeholder="请输入备注信息"
        />
      </van-cell-group>

      <!-- 底部按钮 -->
      <div class="bottom-actions">
        <van-button type="primary" block round @click="saveAsset">保存</van-button>
        <van-button block round @click="saveAndAdd" style="margin-top: 12px;">保存并继续添加</van-button>
      </div>
    </div>

    <!-- 分类选择器 -->
    <van-popup v-model:show="showCategoryPicker" position="bottom">
      <van-picker
        :columns="categoryColumns"
        @confirm="onCategoryConfirm"
        @cancel="showCategoryPicker = false"
        show-toolbar
        title="选择资产分类"
      />
    </van-popup>

    <!-- 二级分类选择器 -->
    <van-popup v-model:show="showSubCategoryPicker" position="bottom">
      <van-picker
        :columns="subCategoryColumns"
        @confirm="onSubCategoryConfirm"
        @cancel="showSubCategoryPicker = false"
        show-toolbar
        title="选择二级分类"
      />
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="purchaseDateValue"
        title="选择购置日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 保修日期选择器 -->
    <van-popup v-model:show="showWarrantyPicker" position="bottom">
      <van-date-picker
        v-model="warrantyDateValue"
        title="选择保修到期日"
        :min-date="minDate"
        :max-date="maxWarrantyDate"
        @confirm="onWarrantyConfirm"
        @cancel="showWarrantyPicker = false"
      />
    </van-popup>

    <!-- 存放类型选择器 -->
    <van-popup v-model:show="showLocationTypePicker" position="bottom">
      <van-picker
        :columns="locationTypeColumns"
        @confirm="onLocationTypeConfirm"
        @cancel="showLocationTypePicker = false"
        show-toolbar
        title="选择存放类型"
      />
    </van-popup>

    <!-- 状态选择器 -->
    <van-popup v-model:show="showStatusPicker" position="bottom">
      <van-picker
        :columns="statusColumns"
        @confirm="onStatusConfirm"
        @cancel="showStatusPicker = false"
        show-toolbar
        title="选择资产状态"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'

const router = useRouter()

// 表单数据
const form = reactive({
  assetNo: '',
  name: '',
  category: '',
  categoryName: '',
  subCategory: '',
  subCategoryName: '',
  brand: '',
  model: '',
  sn: '',
  purchaseDate: '',
  purchasePrice: '',
  supplier: '',
  warrantyDate: '',
  locationType: '',
  locationTypeName: '',
  locationDetail: '',
  status: '库存',
  photos: {
    front: [],
    sn: [],
    invoice: []
  },
  remark: ''
})

// 自动生成资产编号
const generateAssetNo = () => {
  const prefix = form.category || 'ASSET'
  const year = new Date().getFullYear().toString().slice(-2)
  const random = Math.floor(Math.random() * 9000) + 1000
  form.assetNo = `${prefix}-${year}-${random}`
}

// 分类数据
const categoryColumns = [
  { text: '主机类', value: 'PC' },
  { text: '笔记本类', value: 'NB' },
  { text: '显示类', value: 'DS' },
  { text: '外设类', value: 'PE' },
  { text: '网络类', value: 'NT' },
  { text: '收银类', value: 'PO' }
]

const subCategoryMap = {
  'PC': [
    { text: '台式主机', value: 'PC-01' },
    { text: '服务器', value: 'PC-02' },
    { text: '工作站', value: 'PC-03' }
  ],
  'NB': [
    { text: 'MacBook', value: 'NB-01' },
    { text: 'ThinkPad', value: 'NB-02' },
    { text: '其他笔记本', value: 'NB-03' }
  ],
  'DS': [
    { text: '24寸显示器', value: 'DS-01' },
    { text: '27寸显示器', value: 'DS-02' },
    { text: '4K显示器', value: 'DS-03' },
    { text: '投影仪', value: 'DS-04' }
  ],
  'PE': [
    { text: '键盘鼠标', value: 'PE-01' },
    { text: '耳机耳麦', value: 'PE-02' },
    { text: '转接线/配件', value: 'PE-03' }
  ],
  'NT': [
    { text: '路由器', value: 'NT-01' },
    { text: '交换机', value: 'NT-02' },
    { text: 'AP设备', value: 'NT-03' }
  ],
  'PO': [
    { text: 'POS机', value: 'PO-01' },
    { text: '扫码枪', value: 'PO-02' },
    { text: '钱箱', value: 'PO-03' }
  ]
}

const subCategoryColumns = computed(() => {
  return subCategoryMap[form.category] || []
})

const locationTypeColumns = [
  { text: '总部', value: 'HQ' },
  { text: '门店', value: 'STORE' },
  { text: '仓库', value: 'WAREHOUSE' }
]

const statusColumns = [
  { text: '库存', value: '库存' },
  { text: '在用', value: '在用' },
  { text: '维修中', value: '维修中' }
]

// 弹窗显示控制
const showCategoryPicker = ref(false)
const showSubCategoryPicker = ref(false)
const showDatePicker = ref(false)
const showWarrantyPicker = ref(false)
const showLocationTypePicker = ref(false)
const showStatusPicker = ref(false)

// 日期选择器值
const purchaseDateValue = ref(['2026', '04', '03'])
const warrantyDateValue = ref(['2027', '04', '03'])
const minDate = new Date(2020, 0, 1)
const maxDate = new Date()
const maxWarrantyDate = new Date(2030, 11, 31)

const goBack = () => {
  router.back()
}

const onCategoryConfirm = ({ selectedOptions }) => {
  form.category = selectedOptions[0].value
  form.categoryName = selectedOptions[0].text
  form.subCategory = ''
  form.subCategoryName = ''
  generateAssetNo()
  showCategoryPicker.value = false
}

const onSubCategoryConfirm = ({ selectedOptions }) => {
  form.subCategory = selectedOptions[0].value
  form.subCategoryName = selectedOptions[0].text
  showSubCategoryPicker.value = false
}

const onDateConfirm = ({ selectedValues }) => {
  form.purchaseDate = selectedValues.join('-')
  showDatePicker.value = false
}

const onWarrantyConfirm = ({ selectedValues }) => {
  form.warrantyDate = selectedValues.join('-')
  showWarrantyPicker.value = false
}

const onLocationTypeConfirm = ({ selectedOptions }) => {
  form.locationType = selectedOptions[0].value
  form.locationTypeName = selectedOptions[0].text
  showLocationTypePicker.value = false
}

const onStatusConfirm = ({ selectedOptions }) => {
  form.status = selectedOptions[0].value
  showStatusPicker.value = false
}

const validateForm = () => {
  if (!form.name) {
    showToast('请输入资产名称')
    return false
  }
  if (!form.category) {
    showToast('请选择资产分类')
    return false
  }
  if (!form.sn) {
    showToast('请输入SN码')
    return false
  }
  return true
}

const saveAsset = async () => {
  if (!validateForm()) return
  
  showLoadingToast({
    message: '保存中...',
    forbidClick: true
  })
  
  // TODO: 调用API保存资产
  setTimeout(() => {
    closeToast()
    showToast('保存成功')
    router.back()
  }, 1000)
}

const saveAndAdd = async () => {
  if (!validateForm()) return
  
  showLoadingToast({
    message: '保存中...',
    forbidClick: true
  })
  
  // TODO: 调用API保存资产
  setTimeout(() => {
    closeToast()
    showToast('保存成功')
    // 重置表单
    form.name = ''
    form.brand = ''
    form.model = ''
    form.sn = ''
    form.photos = { front: [], sn: [], invoice: [] }
    form.remark = ''
    generateAssetNo()
  }, 1000)
}

// 初始化生成资产编号
generateAssetNo()
</script>

<style scoped>
.asset-add-page {
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
}

.upload-section {
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.upload-section:last-child {
  border-bottom: none;
}

.upload-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.bottom-actions {
  padding: 24px 16px;
}
</style>