<template>
  <div class="register-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">创建账户</h1>
      <p class="page-subtitle">加入PersonWeb，开始您的创作之旅</p>
    </div>

    <!-- 注册表单 -->
    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
      @submit.prevent="handleRegister"
    >
      <!-- 用户名输入框 -->
      <el-form-item prop="username">
        <div class="form-field">
          <label class="field-label">
            用户名
            <span v-if="usernameStatus === 'checking'" class="status-text checking">
              <el-icon class="is-loading"><Loading /></el-icon>
              检查中...
            </span>
            <span v-else-if="usernameStatus === 'available'" class="status-text available">
              <el-icon><Select /></el-icon>
              可用
            </span>
            <span v-else-if="usernameStatus === 'unavailable'" class="status-text unavailable">
              <el-icon><Close /></el-icon>
              已被使用
            </span>
          </label>
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（3-20个字符）"
            size="large"
            class="apple-input"
            :prefix-icon="User"
            clearable
            @blur="checkUsername"
            @input="resetUsernameStatus"
          />
        </div>
      </el-form-item>

      <!-- 密码输入框 -->
      <el-form-item prop="password">
        <div class="form-field">
          <label class="field-label">
            密码
            <span class="password-strength" :class="passwordStrength.level">
              {{ passwordStrength.text }}
            </span>
          </label>
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-32个字符）"
            size="large"
            class="apple-input"
            :prefix-icon="Lock"
            show-password
            clearable
            @input="checkPasswordStrength"
          />
          <!-- 密码强度指示器 - 简化版 -->
          <div class="password-indicator" v-if="registerForm.password">
            <div class="strength-bar">
              <div 
                class="strength-fill"
                :class="passwordStrength.level"
                :style="{ width: passwordStrength.percent + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </el-form-item>

      <!-- 确认密码输入框 -->
      <el-form-item prop="confirmPassword">
        <div class="form-field">
          <label class="field-label">确认密码</label>
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            class="apple-input"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </div>
      </el-form-item>

      <!-- 服务条款 -->
      <el-form-item prop="agreeTerms">
        <div class="terms-container">
          <el-checkbox v-model="registerForm.agreeTerms" class="terms-checkbox">
            <span class="terms-text">
              我已阅读并同意
              <a href="#" class="terms-link">《用户协议》</a>
              和
              <a href="#" class="terms-link">《隐私政策》</a>
            </span>
          </el-checkbox>
        </div>
      </el-form-item>

      <!-- 注册按钮 -->
      <AppleButton
        type="primary"
        size="large"
        block
        :loading="isLoading"
        :disabled="!canSubmit"
        @click="handleRegister"
      >
        {{ isLoading ? '注册中...' : '立即注册' }}
      </AppleButton>
    </el-form>

    <!-- 登录链接 -->
    <div class="auth-switch">
      <p class="switch-text">
        已有账户？
        <router-link to="/auth/login" class="switch-link">
          立即登录
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { authApi } from '@/api'
import AppleButton from '@/components/common/AppleButton.vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Loading,
  Select,
  Close
} from '@element-plus/icons-vue'

/**
 * 注册页面组件
 * 提供用户注册功能
 */

const router = useRouter()
const { register } = useAuth()

// 表单引用
const registerFormRef = ref()

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  agreeTerms: false
})

// 验证器函数
const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度在 3 到 20 个字符'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else if (usernameStatus.value === 'unavailable') {
    callback(new Error('用户名已被使用'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 32) {
    callback(new Error('密码长度在 6 到 32 个字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [{ validator: validateUsername, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  agreeTerms: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请同意用户协议和隐私政策'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
}

// 状态变量
const isLoading = ref(false)
const usernameStatus = ref('')

// 密码强度检查
const passwordChecks = reactive({
  length: false,
  hasUpper: false,
  hasLower: false,
  hasNumber: false,
  hasSpecial: false
})

// 计算密码强度
const passwordStrength = computed(() => {
  const password = registerForm.password
  if (!password) {
    return { level: '', text: '', percent: 0 }
  }

  let score = 0
  const checks = {
    length: password.length >= 6,
    hasUpper: /[A-Z]/.test(password),
    hasLower: /[a-z]/.test(password),
    hasNumber: /\d/.test(password),
    hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(password)
  }

  Object.assign(passwordChecks, checks)
  score = Object.values(checks).filter(Boolean).length

  if (score <= 1) {
    return { level: 'weak', text: '弱', percent: 20 }
  } else if (score <= 3) {
    return { level: 'medium', text: '中', percent: 60 }
  } else {
    return { level: 'strong', text: '强', percent: 100 }
  }
})

// 检查是否可以提交
const canSubmit = computed(() => {
  return registerForm.username &&
         registerForm.password &&
         registerForm.confirmPassword &&
         registerForm.agreeTerms &&
         usernameStatus.value !== 'unavailable'
})



/**
 * 检查用户名可用性
 */
const checkUsername = async () => {
  const username = registerForm.username?.trim()
  if (!username || username.length < 3) {
    usernameStatus.value = ''
    return
  }

  try {
    usernameStatus.value = 'checking'
    const response = await authApi.checkUsername(username)
    if (response.code === 200) {
      usernameStatus.value = response.data.available ? 'available' : 'unavailable'
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
    usernameStatus.value = ''
  }
}



/**
 * 重置用户名状态
 */
const resetUsernameStatus = () => {
  if (usernameStatus.value) {
    usernameStatus.value = ''
  }
}



/**
 * 检查密码强度
 */
const checkPasswordStrength = () => {
  // 触发计算属性更新
  passwordStrength.value
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    // 表单验证
    await registerFormRef.value.validate()

    isLoading.value = true

    // 调用注册接口
    await register({
      username: registerForm.username.trim(),
      password: registerForm.password
    })

    ElMessage.success('注册成功！请登录')

    // 跳转到登录页面
    router.push('/auth/login')

  } catch (error) {
    console.error('注册失败:', error)

    // 显示错误信息
    const errorMessage = error.response?.data?.message || error.message || '注册失败，请重试'
    ElMessage.error(errorMessage)

  } finally {
    isLoading.value = false
  }
}

// 组件挂载时的初始化
onMounted(() => {
  // 页面初始化完成
})
</script>

<style lang="scss" scoped>
.register-page {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  padding: var(--apple-space-md);
  max-height: 100vh;
  overflow-y: auto;
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

// 注册表单
.register-form {
  margin-bottom: var(--apple-space-lg);
  width: 100%;
  overflow-x: hidden;

  :deep(.el-form-item) {
    margin-bottom: var(--apple-space-md);
    width: 100%;
    overflow-x: hidden;
  }

  .form-field {
    .field-label {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: var(--apple-text-sm);
      font-weight: 500;
      color: var(--apple-text-primary);
      margin-bottom: var(--apple-space-xs);

      .status-text {
        display: flex;
        align-items: center;
        gap: var(--apple-space-xs);
        font-size: var(--apple-text-xs);
        font-weight: 400;

        &.checking {
          color: var(--apple-text-secondary);
        }

        &.available {
          color: var(--apple-green);
        }

        &.unavailable {
          color: var(--apple-red);
        }
      }

      .password-strength {
        font-size: var(--apple-text-xs);
        font-weight: 400;

        &.weak {
          color: var(--apple-red);
        }

        &.medium {
          color: var(--apple-orange);
        }

        &.strong {
          color: var(--apple-green);
        }
      }
    }
  }



  // 密码强度指示器 - 简化版
  .password-indicator {
    margin-top: var(--apple-space-xs);

    .strength-bar {
      height: 3px;
      background: var(--apple-bg-secondary);
      border-radius: 2px;
      overflow: hidden;

      .strength-fill {
        height: 100%;
        transition: all 0.3s ease;

        &.weak {
          background: var(--apple-red);
        }

        &.medium {
          background: var(--apple-orange);
        }

        &.strong {
          background: var(--apple-green);
        }
      }
    }
  }

  // 服务条款
  .terms-container {
    width: 100%;
    
    .terms-checkbox {
      width: 100%;
      font-size: var(--apple-text-sm);
      color: var(--apple-text-secondary);
      display: flex;
      align-items: flex-start;
      line-height: 1.5;

      .terms-text {
        flex: 1;
        word-wrap: break-word;
        word-break: break-word;
        white-space: normal;
        line-height: 1.5;
      }

      .terms-link {
        color: var(--apple-blue);
        text-decoration: none;
        white-space: nowrap;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}

// 认证切换
.auth-switch {
  text-align: center;
  padding-bottom: var(--apple-space-lg);

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

// 复选框样式
:deep(.terms-checkbox) {
  width: 100% !important;
  
  .el-checkbox__input {
    flex-shrink: 0;
    margin-right: var(--apple-space-xs);
    
    &.is-checked .el-checkbox__inner {
      background-color: var(--apple-blue);
      border-color: var(--apple-blue);
    }
  }

  .el-checkbox__label {
    color: var(--apple-text-secondary);
    flex: 1;
    word-wrap: break-word;
    word-break: break-word;
    white-space: normal;
    line-height: 1.5;
    margin-left: 0;
  }
}

// 响应式设计
@media (max-width: 480px) {
  .register-page {
    max-width: 100%;
    padding: var(--apple-space-sm);
  }

  .page-header {
    margin-bottom: var(--apple-space-md);
    
    .page-title {
      font-size: var(--apple-text-xl);
    }
    
    .page-subtitle {
      font-size: var(--apple-text-sm);
    }
  }

  .terms-container {
    .terms-checkbox {
      font-size: var(--apple-text-xs);
      
      .terms-text {
        line-height: 1.4;
      }
      
      .terms-link {
        word-break: break-all;
        white-space: normal;
      }
    }
  }
}

// 全局防止水平滚动
* {
  box-sizing: border-box;
}

:deep(*) {
  max-width: 100%;
  word-wrap: break-word;
}
</style> 