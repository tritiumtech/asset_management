<template>
  <div class="asset-batch-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      title="资产批量录入"
      left-arrow
      @click-left="goBack"
    />

    <div class="content">
      <!-- 步骤条 -->
      <van-steps :active="activeStep">
        <van-step>下载模板</van-step>
        <van-step>填写数据</van-step>
        <van-step>上传导入</van-step>
        <van-step>确认导入</van-step>
      </van-steps>

      <!-- 步骤1: 下载模板 -->
      <div v-if="activeStep === 0" class="step-content">
        <div class="step-card">
          <van-icon name="description" class="step-icon" />
          <h3 class="step-title">下载导入模板</h3>
          <p class="step-desc">
            请下载资产导入模板，按照模板格式填写资产信息。
            模板包含必填字段说明和示例数据。
          </p>
          
          <van-cell-group inset class="template-list">
            <van-cell 
              title="IT设备导入模板" 
              label="适用于电脑、显示器、网络设备等"
              is-link
              @click="downloadTemplate('it')"
            >
              <template #icon>
                <van-icon name="description" class="template-icon" color="#1989fa" />
              </template>
            </van-cell>
            
            <van-cell 
              title="收银设备导入模板" 
              label="适用于POS机、扫码枪、钱箱等"
              is-link
              @click="downloadTemplate('pos')"
            >
              <template #icon>
                <van-icon name="description" class="template-icon" color="#07c160" />
              </template>
            </van-cell>
            
            <van-cell 
              title="通用物料导入模板" 
              label="适用于耗材、配件等消耗性物料"
              is-link
              @click="downloadTemplate('material')"
            >
              <template #icon>
                <van-icon name="description" class="template-icon" color="#ff976a" />
              </template>
            </van-cell>
          </van-cell-group>

          <div class="template-tips">
            <div class="tips-title">模板字段说明</div>
            <van-collapse v-model="activeCollapse">
              <van-collapse-item title="必填字段" name="1">
                <ul class="field-list">
                  <li><strong>资产名称</strong> - 资产的显示名称</li>
                  <li><strong>资产分类</strong> - 从预设分类中选择</li>
                  <li><strong>SN码</strong> - 设备的唯一序列号</li>
                  <li><strong>购置日期</strong> - 格式: YYYY-MM-DD</li>
                  <li><strong>存放位置</strong> - 总部/门店名称</li>
                </ul>
              </van-collapse-item>
              
              <van-collapse-item title="选填字段" name="2">
                <ul class="field-list">
                  <li><strong>品牌</strong> - 设备品牌</li>
                  <li><strong>型号</strong> - 具体型号</li>
                  <li><strong>购置金额</strong> - 单位:元</li>
                  <li><strong>供应商</strong> - 采购供应商</li>
                  <li><strong>保修期</strong> - 格式: YYYY-MM-DD</li>
                  <li><strong>备注</strong> - 其他说明</li>
                </ul>
              </van-collapse-item>
            </van-collapse>
          </div>

          <van-button type="primary" block round @click="activeStep = 1" style="margin-top: 24px;">
            已下载模板，下一步
          </van-button>
        </div>
      </div>

      <!-- 步骤2: 填写数据 -->
      <div v-if="activeStep === 1" class="step-content">
        <div class="step-card">
          <van-icon name="edit" class="step-icon" />
          <h3 class="step-title">填写资产数据</h3>
          
          <div class="data-tips">
            <van-notice-bar
              left-icon="info-o"
              text="请确保数据准确，SN码不能重复，购置日期格式为YYYY-MM-DD"
            />
          </div>

          <div class="example-section">
            <div class="example-title">数据示例</div>
            <div class="example-table">
              <table>
                <thead>
                  <tr>
                    <th>资产名称</th>
                    <th>分类</th>
                    <th>SN码</th>
                    <th>购置日期</th>
                    <th>位置</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>MacBook Pro 16寸</td>
                    <td>笔记本类</td>
                    <td>C02ABC123456</td>
                    <td>2026-03-15</td>
                    <td>总部</td>
                  </tr>
                  <tr>
                    <td>Dell显示器 U2723QE</td>
                    <td>显示类</td>
                    <td>CN123456789</td>
                    <td>2026-03-15</td>
                    <td>深圳店</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="step-actions">
            <van-button @click="activeStep = 0">上一步</van-button>
            <van-button type="primary" @click="activeStep = 2">下一步</van-button>
          </div>
        </div>
      </div>

      <!-- 步骤3: 上传导入 -->
      <div v-if="activeStep === 2" class="step-content">
        <div class="step-card">
          <van-icon name="upload" class="step-icon" />
          <h3 class="step-title">上传资产数据</h3>
          
          <div class="upload-area">
            <van-uploader
              v-model="fileList"
              accept=".xlsx,.xls,.csv"
              :max-count="1"
              :after-read="afterRead"
              :before-read="beforeRead"
            >
              <div class="upload-placeholder">
                <van-icon name="upload" class="upload-icon" />
                <div class="upload-text">点击上传Excel文件</div>
                <div class="upload-hint">支持 .xlsx, .xls, .csv 格式</div>
              </div>
            </van-uploader>
          </div>

          <div v-if="uploadStatus === 'uploading'" class="upload-progress">
            <van-progress :percentage="uploadProgress" />
            <div class="progress-text">正在上传...{{ uploadProgress }}%</div>
          </div>

          <div v-if="uploadStatus === 'success'" class="upload-success">
            <van-icon name="success" color="#07c160" size="48" />
            <div class="success-text">上传成功！</div>
            <div class="file-info">
              <div>文件名: {{ uploadedFile.name }}</div>
              <div>文件大小: {{ uploadedFile.size }}</div>
              <div>数据行数: {{ uploadedFile.rows }} 条</div>
            </div>
          </div>

          <div class="step-actions">
            <van-button @click="activeStep = 1">上一步</van-button>
            <van-button 
              type="primary" 
              @click="parseFile"
              :disabled="uploadStatus !== 'success'"
            >
              解析数据
            </van-button>
          </div>
        </div>
      </div>

      <!-- 步骤4: 确认导入 -->
      <div v-if="activeStep === 3" class="step-content">
        <div class="step-card">
          <van-icon name="records" class="step-icon" />
          
          <div class="parse-result" v-if="parseResult">
            <h3 class="step-title">数据解析结果</h3>
            
            <div class="result-stats">
              <div class="stat-item">
                <div class="stat-number total">{{ parseResult.total }}</div>
                <div class="stat-label">总记录</div>
              </div>
              <div class="stat-item">
                <div class="stat-number valid">{{ parseResult.valid }}</div>
                <div class="stat-label">有效</div>
              </div>
              <div class="stat-item">
                <div class="stat-number invalid">{{ parseResult.invalid }}</div>
                <div class="stat-label">异常</div>
              </div>
            </div>

            <div v-if="parseResult.errors.length > 0" class="error-list">
              <div class="error-title">异常记录</div>
              <van-cell-group inset>
                <van-cell
                  v-for="(error, index) in parseResult.errors"
                  :key="index"
                  :title="`第${error.row}行`"
                  :label="error.message"
                >
                  <template #right-icon>
                    <van-tag type="danger">错误</van-tag>
                  </template>
                </van-cell>
              </van-cell-group>
            </div>

            <div v-if="parseResult.preview.length > 0" class="preview-list">
              <div class="preview-title">数据预览（前5条）</div>
              <van-cell-group inset>
                <van-cell
                  v-for="(item, index) in parseResult.preview"
                  :key="index"
                  :title="item.name"
                  :label="`${item.category} | ${item.sn} | ${item.location}`"
                >
                  <template #right-icon>
                    <van-tag type="success">有效</van-tag>
                  </template>
                </van-cell>
              </van-cell-group>
            </div>
          </div>

          <div class="step-actions">
            <van-button @click="activeStep = 2">重新上传</van-button>
            <van-button 
              type="primary" 
              @click="confirmImport"
              :disabled="parseResult && parseResult.valid === 0"
            >
              确认导入
            </van-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'

const router = useRouter()

const activeStep = ref(0)
const activeCollapse = ref(['1'])
const fileList = ref([])
const uploadStatus = ref('')
const uploadProgress = ref(0)
const uploadedFile = reactive({
  name: '',
  size: '',
  rows: 0
})
const parseResult = ref(null)

const goBack = () => {
  router.back()
}

const downloadTemplate = (type) => {
  showToast(`正在下载${type === 'it' ? 'IT设备' : type === 'pos' ? '收银设备' : '通用物料'}模板...`)
  // TODO: 实际下载逻辑
}

const beforeRead = (file) => {
  const validTypes = ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 
                      'application/vnd.ms-excel', 
                      'text/csv']
  const validExtensions = ['.xlsx', '.xls', '.csv']
  
  const isValidType = validTypes.includes(file.type) || validExtensions.some(ext => file.name.endsWith(ext))
  
  if (!isValidType) {
    showToast('请上传Excel或CSV文件')
    return false
  }
  return true
}

const afterRead = (file) => {
  uploadStatus.value = 'uploading'
  uploadProgress.value = 0
  
  // 模拟上传进度
  const interval = setInterval(() => {
    uploadProgress.value += 10
    if (uploadProgress.value >= 100) {
      clearInterval(interval)
      uploadStatus.value = 'success'
      uploadedFile.name = file.file.name
      uploadedFile.size = (file.file.size / 1024).toFixed(2) + ' KB'
      uploadedFile.rows = Math.floor(Math.random() * 100) + 10
    }
  }, 200)
}

const parseFile = () => {
  showLoadingToast({
    message: '正在解析...',
    forbidClick: true
  })
  
  // 模拟解析结果
  setTimeout(() => {
    closeToast()
    parseResult.value = {
      total: uploadedFile.rows,
      valid: uploadedFile.rows - 3,
      invalid: 3,
      errors: [
        { row: 5, message: 'SN码重复: ABC123456' },
        { row: 12, message: '购置日期格式错误: 2026/3/15' },
        { row: 18, message: '资产分类不存在: 未知分类' }
      ],
      preview: [
        { name: 'MacBook Pro 16寸', category: '笔记本类', sn: 'C02ABC123456', location: '总部' },
        { name: 'Dell显示器 U2723QE', category: '显示类', sn: 'CN123456789', location: '深圳店' },
        { name: 'ThinkPad X1 Carbon', category: '笔记本类', sn: 'PF1ABC789012', location: '北京店' },
        { name: '机械键盘 Keychron K3', category: '外设类', sn: 'KEY20260001', location: '总部' },
        { name: 'POS机 Sunmi V2', category: '收银类', sn: 'POS20260001', location: '上海店' }
      ]
    }
    activeStep.value = 3
  }, 1500)
}

const confirmImport = async () => {
  showLoadingToast({
    message: '正在导入...',
    forbidClick: true
  })
  
  // 模拟导入
  setTimeout(() => {
    closeToast()
    showToast(`成功导入 ${parseResult.value.valid} 条资产记录`)
    router.push('/assets')
  }, 2000)
}
</script>

<style scoped>
.asset-batch-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  padding: 12px;
}

.step-content {
  margin-top: 16px;
}

.step-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px 16px;
  text-align: center;
}

.step-icon {
  font-size: 48px;
  color: #1989fa;
  margin-bottom: 16px;
}

.step-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 12px;
}

.step-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 24px;
}

.template-list {
  text-align: left;
  margin: 16px 0;
}

.template-icon {
  font-size: 24px;
  margin-right: 8px;
}

.template-tips {
  text-align: left;
  margin-top: 16px;
}

.tips-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  padding: 0 16px;
}

.field-list {
  padding-left: 16px;
  font-size: 13px;
  line-height: 1.8;
  color: #666;
}

.field-list li {
  margin-bottom: 4px;
}

.data-tips {
  margin: 16px 0;
}

.example-section {
  margin: 24px 0;
  text-align: left;
}

.example-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
  padding: 0 16px;
}

.example-table {
  overflow-x: auto;
  padding: 0 16px;
}

.example-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.example-table th,
.example-table td {
  border: 1px solid #ebedf0;
  padding: 8px;
  text-align: left;
}

.example-table th {
  background: #f7f8fa;
  font-weight: 500;
}

.upload-area {
  margin: 24px 0;
}

.upload-placeholder {
  padding: 32px;
  border: 2px dashed #dcdee0;
  border-radius: 8px;
}

.upload-icon {
  font-size: 48px;
  color: #dcdee0;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 16px;
  color: #323233;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 12px;
  color: #969799;
}

.upload-progress {
  margin: 16px 0;
}

.progress-text {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.upload-success {
  margin: 24px 0;
}

.success-text {
  font-size: 16px;
  color: #07c160;
  margin: 12px 0;
}

.file-info {
  font-size: 13px;
  color: #666;
  line-height: 1.8;
}

.step-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 24px;
}

.parse-result {
  text-align: left;
}

.result-stats {
  display: flex;
  justify-content: space-around;
  margin: 24px 0;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
}

.stat-number.total { color: #323233; }
.stat-number.valid { color: #07c160; }
.stat-number.invalid { color: #ee0a24; }

.error-list,
.preview-list {
  margin-top: 16px;
}

.error-title,
.preview-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
  padding: 0 16px;
}
</style>