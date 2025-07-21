<template>
  <div class="not-found-page">
    <div class="container">
      <div class="error-content">
        <div class="error-code">404</div>
        <h1 class="error-title">页面未找到</h1>
        <p class="error-message">抱歉，您访问的页面不存在或已被移除</p>
        
        <div class="error-actions">
          <AppleButton type="primary" @click="$router.push('/')">
            返回首页
          </AppleButton>
          <AppleButton type="outline" @click="$router.go(-1)">
            返回上页
          </AppleButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import AppleButton from '@/components/common/AppleButton.vue'

// 404错误页面
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--apple-space-2xl);
  
  .container {
    text-align: center;
    max-width: 600px;
  }
  
  .error-content {
    .error-code {
      font-size: 120px;
      font-weight: 700;
      background: linear-gradient(135deg, var(--apple-blue), var(--apple-purple));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      line-height: 1;
      margin-bottom: var(--apple-space-lg);
    }
    
    .error-title {
      font-size: var(--apple-text-3xl);
      font-weight: 600;
      color: var(--apple-text-primary);
      margin-bottom: var(--apple-space-md);
    }
    
    .error-message {
      font-size: var(--apple-text-lg);
      color: var(--apple-text-secondary);
      margin-bottom: var(--apple-space-2xl);
      line-height: 1.6;
    }
    
    .error-actions {
      display: flex;
      gap: var(--apple-space-md);
      justify-content: center;
      flex-wrap: wrap;
    }
  }
}

@media (max-width: 768px) {
  .not-found-page {
    .error-content {
      .error-code {
        font-size: 80px;
      }
      
      .error-actions {
        flex-direction: column;
        align-items: center;
      }
    }
  }
}
</style> 