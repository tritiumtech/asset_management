<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>资产管理系统</h1>
        <p>Asset Management System</p>
      </div>
      
      <van-form @submit="onSubmit" class="login-form">
        <van-cell-group inset>
          <van-field
            v-model="form.username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请填写用户名' }]"
          >
            <template #left-icon>
              <van-icon name="user-o" />
            </template>
          </van-field>
          
          <van-field
            v-model="form.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
          >
            <template #left-icon>
              <van-icon name="lock" />
            </template>
          </van-field>
        </van-cell-group>
        
        <div class="login-actions">
          <van-button 
            round 
            block 
            type="primary" 
            native-type="submit"
            :loading="loading"
            size="large"
          >
            登录
          </van-button>
        </div>
      </van-form>
      
      <div class="login-tips">
        <p>测试账户：</p>
        <p>admin / admin123</p>
        <p>test_employee / test123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login } from '../api/auth'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const onSubmit = async (values) => {
  loading.value = true
  try {
    const res = await login(values)
    if (res.code === 200) {
      // 保存token
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      
      showToast({
        type: 'success',
        message: '登录成功'
      })
      
      // 跳转到首页
      router.push('/')
    } else {
      showToast({
        type: 'fail',
        message: res.message || '登录失败'
      })
    }
  } catch (error) {
    showToast({
      type: 'fail',
      message: '网络错误，请稍后重试'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 16px;
  padding: 40px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-bottom: 30px;
}

.login-actions {
  margin-top: 30px;
  padding: 0 16px;
}

.login-tips {
  text-align: center;
  font-size: 12px;
  color: #999;
}

.login-tips p {
  margin: 4px 0;
}
</style>
