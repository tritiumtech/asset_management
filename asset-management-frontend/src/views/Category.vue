<template>
  <div class="category-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      title="资产分类管理"
      left-arrow
      @click-left="goBack"
      right-text="新增"
      @click-right="showAddDialog"
    />

    <!-- 分类列表 -->
    <div class="category-list">
      <van-collapse v-model="activeNames">
        <van-collapse-item 
          v-for="category in categories" 
          :key="category.code"
          :title="category.name" 
          :name="category.code"
        >
          <template #title>
            <div class="category-title">
              <span class="category-name">{{ category.name }}</span>
              <van-tag type="primary" class="category-code">{{ category.code }}</van-tag>
              <span class="category-count">{{ category.count }}个资产</span>
            </div>
          </template>
          
          <div class="subcategory-list">
            <van-cell
              v-for="sub in category.children"
              :key="sub.code"
              :title="sub.name"
              :label="'编码: ' + sub.code"
            >
              <template #right-icon>
                <div class="sub-actions">
                  <span class="sub-count">{{ sub.count }}个</span>
                  <van-icon name="edit" class="action-icon" @click="editSubcategory(sub)" />
                  <van-icon name="delete-o" class="action-icon delete" @click="deleteSubcategory(sub)" />
                </div>
              </template>
            </van-cell>
            
            <van-cell 
              title="+ 添加子分类" 
              class="add-sub-cell"
              @click="addSubcategory(category)"
            />
          </div>
        </van-collapse-item>
      </van-collapse>
    </div>

    <!-- 新增/编辑分类弹窗 -->
    <van-dialog
      v-model:show="dialogVisible"
      :title="dialogTitle"
      show-cancel-button
      @confirm="saveCategory"
    >
      <van-form class="dialog-form">
        <van-field
          v-model="form.name"
          label="分类名称"
          placeholder="请输入分类名称"
          :rules="[{ required: true, message: '请输入分类名称' }]"
        />
        <van-field
          v-model="form.code"
          label="分类编码"
          placeholder="如: NB"
          :rules="[{ required: true, message: '请输入分类编码' }]"
          :disabled="isEdit"
        />
        <van-field
          v-if="form.parentCode"
          v-model="form.parentName"
          label="上级分类"
          disabled
        />
        
        <van-field
          v-model="form.description"
          label="分类说明"
          type="textarea"
          rows="2"
          placeholder="请输入分类说明"
        />
        
        <van-field
          v-model="form.attributes"
          label="管理属性"
          type="textarea"
          rows="2"
          placeholder="如: SN码、配置、位置等，用逗号分隔"
        />
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()

// 当前展开的分类
const activeNames = ref(['PC', 'NB'])

// 分类数据
const categories = ref([
  {
    code: 'PC',
    name: '主机类',
    count: 156,
    children: [
      { code: 'PC-01', name: '台式主机', count: 89, parentCode: 'PC' },
      { code: 'PC-02', name: '服务器', count: 12, parentCode: 'PC' },
      { code: 'PC-03', name: '工作站', count: 55, parentCode: 'PC' }
    ]
  },
  {
    code: 'NB',
    name: '笔记本类',
    count: 234,
    children: [
      { code: 'NB-01', name: 'MacBook', count: 89, parentCode: 'NB' },
      { code: 'NB-02', name: 'ThinkPad', count: 98, parentCode: 'NB' },
      { code: 'NB-03', name: '其他笔记本', count: 47, parentCode: 'NB' }
    ]
  },
  {
    code: 'DS',
    name: '显示类',
    count: 312,
    children: [
      { code: 'DS-01', name: '24寸显示器', count: 156, parentCode: 'DS' },
      { code: 'DS-02', name: '27寸显示器', count: 89, parentCode: 'DS' },
      { code: 'DS-03', name: '4K显示器', count: 45, parentCode: 'DS' },
      { code: 'DS-04', name: '投影仪', count: 22, parentCode: 'DS' }
    ]
  },
  {
    code: 'PE',
    name: '外设类',
    count: 445,
    children: [
      { code: 'PE-01', name: '键盘鼠标', count: 234, parentCode: 'PE' },
      { code: 'PE-02', name: '耳机耳麦', count: 123, parentCode: 'PE' },
      { code: 'PE-03', name: '转接线/配件', count: 88, parentCode: 'PE' }
    ]
  },
  {
    code: 'NT',
    name: '网络类',
    count: 67,
    children: [
      { code: 'NT-01', name: '路由器', count: 23, parentCode: 'NT' },
      { code: 'NT-02', name: '交换机', count: 34, parentCode: 'NT' },
      { code: 'NT-03', name: 'AP设备', count: 10, parentCode: 'NT' }
    ]
  },
  {
    code: 'PO',
    name: '收银类',
    count: 156,
    children: [
      { code: 'PO-01', name: 'POS机', count: 89, parentCode: 'PO' },
      { code: 'PO-02', name: '扫码枪', count: 45, parentCode: 'PO' },
      { code: 'PO-03', name: '钱箱', count: 22, parentCode: 'PO' }
    ]
  }
])

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const isEdit = ref(false)

// 表单数据
const form = reactive({
  name: '',
  code: '',
  parentCode: '',
  parentName: '',
  description: '',
  attributes: ''
})

const goBack = () => {
  router.back()
}

const showAddDialog = () => {
  dialogTitle.value = '新增一级分类'
  isEdit.value = false
  form.name = ''
  form.code = ''
  form.parentCode = ''
  form.parentName = ''
  form.description = ''
  form.attributes = ''
  dialogVisible.value = true
}

const addSubcategory = (category) => {
  dialogTitle.value = `添加${category.name}子分类`
  isEdit.value = false
  form.name = ''
  form.code = ''
  form.parentCode = category.code
  form.parentName = category.name
  form.description = ''
  form.attributes = ''
  dialogVisible.value = true
}

const editSubcategory = (sub) => {
  dialogTitle.value = '编辑子分类'
  isEdit.value = true
  form.name = sub.name
  form.code = sub.code
  form.parentCode = sub.parentCode
  const parent = categories.value.find(c => c.code === sub.parentCode)
  form.parentName = parent ? parent.name : ''
  form.description = ''
  form.attributes = ''
  dialogVisible.value = true
}

const deleteSubcategory = async (sub) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除"${sub.name}"分类吗？该分类下有${sub.count}个资产，删除后这些资产将变为未分类状态。`
    })
    showToast('删除成功')
  } catch {
    // 取消删除
  }
}

const saveCategory = () => {
  if (!form.name || !form.code) {
    showToast('请填写完整信息')
    return
  }
  showToast(isEdit.value ? '修改成功' : '添加成功')
  dialogVisible.value = false
}
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.category-list {
  padding: 12px;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-name {
  font-size: 15px;
  font-weight: 500;
}

.category-code {
  font-size: 11px;
}

.category-count {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

.subcategory-list {
  padding: 0 12px;
}

.sub-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sub-count {
  font-size: 12px;
  color: #999;
}

.action-icon {
  font-size: 18px;
  color: #1989fa;
  padding: 4px;
}

.action-icon.delete {
  color: #ee0a24;
}

.add-sub-cell {
  color: #1989fa;
  font-weight: 500;
}

.dialog-form {
  padding: 16px;
}
</style>