<template>
  <div class="login-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">欢迎回来</h1>
      <p class="page-subtitle">登录您的PersonWeb账户</p>
    </div>

    <!-- 登录表单 -->
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      @submit.prevent="handleLogin"
    >
      <!-- 用户名输入框 -->
      <el-form-item prop="username">
        <div class="form-field">
          <label class="field-label">用户名或邮箱</label>
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名或邮箱"
            size="large"
            class="apple-input"
            :prefix-icon="User"
            clearable
            @keyup.enter="handleLogin"
          />
        </div>
      </el-form-item>

      <!-- 密码输入框 -->
      <el-form-item prop="password">
        <div class="form-field">
          <label class="field-label">密码</label>
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            class="apple-input"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </div>
      </el-form-item>

      <!-- 验证码 -->
      <el-form-item prop="captcha" v-if="showCaptcha">
        <div class="form-field">
          <label class="field-label">验证码</label>
          <div class="captcha-container">
            <el-input
              v-model="loginForm.captcha"
              placeholder="请输入验证码"
              size="large"
              class="apple-input captcha-input"
              :prefix-icon="View"
              clearable
              @keyup.enter="handleLogin"
            />
            <div class="captcha-image" @click="refreshCaptcha">
              <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" />
              <div v-else class="captcha-placeholder">
                <el-icon><Refresh /></el-icon>
                <span>点击获取</span>
              </div>
            </div>
          </div>
        </div>
      </el-form-item>

      <!-- 记住我和忘记密码 -->
      <div class="form-options">
        <el-checkbox v-model="loginForm.rememberMe" class="remember-checkbox">
          记住我
        </el-checkbox>
        <router-link to="/auth/forgot-password" class="forgot-link">
          忘记密码？
        </router-link>
      </div>

      <!-- 登录按钮 -->
      <AppleButton
        type="primary"
        size="large"
        block
        :loading="isLoading"
        @click="handleLogin"
      >
        {{ isLoading ? '登录中...' : '登录' }}
      </AppleButton>
    </el-form>

    <!-- 分隔线 -->
    <div class="divider">
      <span class="divider-text">或</span>
    </div>

    <!-- 第三方登录 -->
    <div class="social-login">
      <AppleButton
        type="outline"
        size="large"
        block
        class="social-btn"
        @click="handleSocialLogin('github')"
      >
        <template #prefix>
          <div class="social-icon github-icon"></div>
        </template>
        使用 GitHub 登录
      </AppleButton>
    </div>

    <!-- 注册链接 -->
    <div class="auth-switch">
      <p class="switch-text">
        还没有账户？
        <router-link to="/auth/register" class="switch-link">
          立即注册
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { authApi } from '@/api'
import AppleButton from '@/components/common/AppleButton.vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  View,
  Refresh
} from '@element-plus/icons-vue'

/**
 * 登录页面组件
 * 提供用户名密码登录和第三方登录功能
 */

const router = useRouter()
const { login } = useAuth()

// 表单引用
const loginFormRef = ref()

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaUuid: '',
  rememberMe: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码为4位字符', trigger: 'blur' }
  ]
}

// 状态变量
const isLoading = ref(false)
const showCaptcha = ref(false)
const captchaUrl = ref('')
const loginAttempts = ref(0)

/**
 * 获取验证码
 */
const getCaptcha = async () => {
  try {
    const response = await authApi.getCaptcha()
    if (response.code === 200) {
      captchaUrl.value = response.data.captchaImage
      loginForm.captchaUuid = response.data.captchaUuid
      showCaptcha.value = true
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage.error('获取验证码失败')
  }
}

/**
 * 刷新验证码
 */
const refreshCaptcha = () => {
  getCaptcha()
  loginForm.captcha = ''
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    // 检查是否需要验证码
    if (showCaptcha.value && !loginForm.captcha) {
      ElMessage.warning('请输入验证码')
      return
    }
    
    isLoading.value = true
    
    // 调用登录接口
    await login(loginForm)
    
    ElMessage.success('登录成功！')
    
    // 重置登录尝试次数
    loginAttempts.value = 0
    
  } catch (error) {
    console.error('登录失败:', error)
    
    // 增加登录尝试次数
    loginAttempts.value++
    
    // 第2次失败后显示验证码
    if (loginAttempts.value >= 2) {
      if (!showCaptcha.value) {
        getCaptcha()
      } else {
        refreshCaptcha()
      }
    }
    
    // 显示错误信息
    const errorMessage = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
    ElMessage.error(errorMessage)
    
  } finally {
    isLoading.value = false
  }
}

/**
 * 处理第三方登录
 */
const handleSocialLogin = (provider) => {
  ElMessage.info(`${provider} 登录功能开发中...`)
  // TODO: 实现第三方登录
  // window.location.href = `/api/auth/oauth/${provider}`
}

// 组件挂载时的初始化
onMounted(() => {
  // 预加载验证码（可根据业务需求决定是否启用）
  // getCaptcha()
  
  // 监听回车键登录
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !isLoading.value) {
      handleLogin()
    }
  })
})
</script>

<style lang="scss" scoped>
.login-page {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  overflow-x: hidden;
  box-sizing: border-box;
}

// 页面标题
.page-header {
  text-align: center;
  margin-bottom: var(--apple-space-2xl);
  
  .page-title {
    font-size: var(--apple-text-3xl);
    font-weight: 700;
    color: var(--apple-text-primary);
    margin-bottom: var(--apple-space-sm);
    line-height: 1.2;
  }
  
  .page-subtitle {
    font-size: var(--apple-text-md);
    color: var(--apple-text-secondary);
    line-height: 1.5;
  }
}

// 登录表单
.login-form {
  margin-bottom: var(--apple-space-xl);
  
  .form-field {
    .field-label {
      display: block;
      font-size: var(--apple-text-sm);
      font-weight: 500;
      color: var(--apple-text-primary);
      margin-bottom: var(--apple-space-xs);
    }
  }
  
  // 验证码容器
  .captcha-container {
    display: flex;
    gap: var(--apple-space-sm);
    align-items: flex-end;
    
    .captcha-input {
      flex: 1;
    }
    
    .captcha-image {
      width: 100px;
      height: 40px;
      border: 1px solid var(--apple-border-primary);
      border-radius: var(--apple-radius-sm);
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--apple-bg-primary);
      transition: all 0.3s ease;
      
      &:hover {
        border-color: var(--apple-blue);
        transform: scale(1.02);
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: inherit;
      }
      
      .captcha-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2px;
        color: var(--apple-text-tertiary);
        font-size: var(--apple-text-xs);
        
        .el-icon {
          font-size: 16px;
        }
      }
    }
  }
  
  // 表单选项
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--apple-space-lg);
    
    .remember-checkbox {
      font-size: var(--apple-text-sm);
      color: var(--apple-text-secondary);
      
      :deep(.el-checkbox__label) {
        color: var(--apple-text-secondary);
      }
    }
    
    .forgot-link {
      font-size: var(--apple-text-sm);
      color: var(--apple-blue);
      text-decoration: none;
      transition: color 0.3s ease;
      
      &:hover {
        color: var(--apple-purple);
      }
    }
  }
}

// 分隔线
.divider {
  position: relative;
  text-align: center;
  margin: var(--apple-space-xl) 0;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background: var(--apple-border-primary);
  }
  
  .divider-text {
    background: var(--apple-bg-primary);
    padding: 0 var(--apple-space-md);
    font-size: var(--apple-text-sm);
    color: var(--apple-text-tertiary);
  }
}

// 第三方登录
.social-login {
  margin-bottom: var(--apple-space-xl);
  
  .social-btn {
    .social-icon {
      width: 20px;
      height: 20px;
      border-radius: 50%;
      
      &.github-icon {
        background: #24292e;
        mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath d='M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z'/%3E%3C/svg%3E") no-repeat;
        mask-size: contain;
      }
    }
  }
}

// 认证切换
.auth-switch {
  text-align: center;
  
  .switch-text {
    font-size: var(--apple-text-sm);
    color: var(--apple-text-secondary);
    
    .switch-link {
      color: var(--apple-blue);
      text-decoration: none;
      font-weight: 500;
      transition: color 0.3s ease;
      
      &:hover {
        color: var(--apple-purple);
      }
    }
  }
}

// 自定义输入框样式
:deep(.apple-input) {
  .el-input__wrapper {
    border-radius: var(--apple-radius-md);
    border: 1px solid var(--apple-border-primary);
    transition: all 0.3s ease;
    box-shadow: var(--apple-shadow-sm);
    
    &:hover {
      border-color: var(--apple-border-hover);
      box-shadow: var(--apple-shadow-md);
    }
    
    &.is-focus {
      border-color: var(--apple-blue);
      box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
    }
  }
  
  .el-input__inner {
    font-family: var(--apple-font-family);
    font-size: var(--apple-text-md);
  }
}

// 记住我复选框样式
:deep(.remember-checkbox) {
  .el-checkbox__input.is-checked .el-checkbox__inner {
    background-color: var(--apple-blue);
    border-color: var(--apple-blue);
  }
}

// 响应式设计
@media (max-width: 480px) {
  .login-page {
    max-width: 100%;
  }
  
  .captcha-container {
    .captcha-image {
      width: 80px;
      height: 36px;
    }
  }
}
</style> 