<template>
  <div class="forgot-password-page">
    <div class="page-header">
      <h1 class="page-title">重置密码</h1>
      <p class="page-subtitle">输入您的邮箱地址，我们将发送重置链接</p>
    </div>
    
    <div class="coming-soon">
      <p>密码重置功能正在开发中...</p>
      <router-link to="/auth/login" class="back-link">
        返回登录
      </router-link>
    </div>
  </div>
</template>

<script setup>
// 忘记密码页面组件 - 开发中
</script>

<style lang="scss" scoped>
.forgot-password-page {
  text-align: center;
  padding: var(--apple-space-2xl);
}

.page-header {
  margin-bottom: var(--apple-space-xl);
  
  .page-title {
    font-size: var(--apple-text-3xl);
    font-weight: 700;
    color: var(--apple-text-primary);
    margin-bottom: var(--apple-space-sm);
  }
  
  .page-subtitle {
    color: var(--apple-text-secondary);
  }
}

.coming-soon {
  .back-link {
    color: var(--apple-blue);
    text-decoration: none;
    margin-top: var(--apple-space-md);
    display: inline-block;
  }
}
</style> 