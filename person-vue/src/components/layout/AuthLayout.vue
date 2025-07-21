<template>
  <div class="auth-layout">
    <!-- 背景装饰 -->
    <div class="auth-background">
      <div class="gradient-orb gradient-orb--1"></div>
      <div class="gradient-orb gradient-orb--2"></div>
      <div class="gradient-orb gradient-orb--3"></div>
    </div>

    <!-- 主要内容区域 -->
    <div class="auth-container">
      <!-- 品牌Logo区域 -->
      <div class="auth-header">
        <router-link to="/" class="brand-logo">
          <div class="logo-icon">
            <div class="logo-symbol">P</div>
          </div>
          <span class="brand-name">PersonWeb</span>
        </router-link>
      </div>

      <!-- 认证表单区域 -->
      <div class="auth-content">
        <div class="auth-card apple-card apple-blur">
          <router-view v-slot="{ Component, route }">
            <transition name="slide-fade" mode="out-in">
              <component :is="Component" :key="route.path" />
            </transition>
          </router-view>
        </div>
      </div>

      <!-- 页脚信息 -->
      <div class="auth-footer">
        <p class="footer-text">
          © 2024 PersonWeb. 专注于创造优雅的数字体验
        </p>
        <div class="footer-links">
          <router-link to="/about" class="footer-link">关于我们</router-link>
          <span class="divider">·</span>
          <a href="#" class="footer-link">隐私政策</a>
          <span class="divider">·</span>
          <a href="#" class="footer-link">服务条款</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from 'vue'

/**
 * 认证页面布局组件
 * 提供统一的认证页面布局和视觉效果
 */

onMounted(() => {
  // 页面加载时添加进入动画
  document.body.classList.add('auth-page')
})

onBeforeUnmount(() => {
  // 离开时清理样式
  document.body.classList.remove('auth-page')
})
</script>

<style lang="scss" scoped>
.auth-layout {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, var(--apple-bg-secondary) 0%, #E8F4FD 100%);
}

// 背景装饰
.auth-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  
  .gradient-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.6;
    animation: float 6s ease-in-out infinite;
    
    &--1 {
      width: 300px;
      height: 300px;
      background: linear-gradient(45deg, var(--apple-blue), var(--apple-purple));
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }
    
    &--2 {
      width: 200px;
      height: 200px;
      background: linear-gradient(45deg, var(--apple-green), var(--apple-teal));
      top: 60%;
      right: 15%;
      animation-delay: 2s;
    }
    
    &--3 {
      width: 150px;
      height: 150px;
      background: linear-gradient(45deg, var(--apple-orange), var(--apple-pink));
      bottom: 20%;
      left: 20%;
      animation-delay: 4s;
    }
  }
}

// 主容器
.auth-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: var(--apple-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--apple-space-xl);
}

// 品牌头部
.auth-header {
  text-align: center;
  
  .brand-logo {
    display: inline-flex;
    align-items: center;
    gap: var(--apple-space-md);
    text-decoration: none;
    color: var(--apple-text-primary);
    transition: transform var(--apple-transition-fast);
    
    &:hover {
      transform: scale(1.05);
    }
    
    .logo-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, var(--apple-blue), var(--apple-purple));
      border-radius: var(--apple-radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: var(--apple-shadow-md);
      
      .logo-symbol {
        color: white;
        font-size: var(--apple-text-xl);
        font-weight: 700;
        font-family: var(--apple-font-family);
      }
    }
    
    .brand-name {
      font-size: var(--apple-text-2xl);
      font-weight: 600;
      background: linear-gradient(135deg, var(--apple-blue), var(--apple-purple));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
}

// 认证内容区域
.auth-content {
  .auth-card {
    padding: var(--apple-space-2xl);
    border: 1px solid rgba(255, 255, 255, 0.3);
    transition: all var(--apple-transition-normal);
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: var(--apple-shadow-xl);
    }
  }
}

// 页脚
.auth-footer {
  text-align: center;
  
  .footer-text {
    color: var(--apple-text-tertiary);
    font-size: var(--apple-text-sm);
    margin-bottom: var(--apple-space-sm);
  }
  
  .footer-links {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--apple-space-sm);
    
    .footer-link {
      color: var(--apple-text-secondary);
      text-decoration: none;
      font-size: var(--apple-text-sm);
      transition: color var(--apple-transition-fast);
      
      &:hover {
        color: var(--apple-blue);
      }
    }
    
    .divider {
      color: var(--apple-text-quaternary);
    }
  }
}

// 过渡动画
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all var(--apple-transition-normal);
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

// 浮动动画
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .auth-container {
    max-width: 100%;
    padding: var(--apple-space-md);
  }
  
  .auth-content .auth-card {
    padding: var(--apple-space-lg);
  }
  
  .gradient-orb {
    &--1 {
      width: 200px;
      height: 200px;
    }
    
    &--2 {
      width: 150px;
      height: 150px;
    }
    
    &--3 {
      width: 100px;
      height: 100px;
    }
  }
}



// 全局样式
:global(.auth-page) {
  overflow: hidden;
}
</style> 