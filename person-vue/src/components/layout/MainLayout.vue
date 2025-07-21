<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <header class="main-header apple-blur">
      <nav class="nav-container">
        <!-- 品牌Logo -->
        <router-link to="/" class="brand-logo">
          <div class="logo-icon">
            <div class="logo-symbol">P</div>
          </div>
          <span class="brand-name">PersonWeb</span>
        </router-link>

        <!-- 主导航菜单 -->
        <div class="nav-menu">
          <router-link to="/" class="nav-link" exact-active-class="active">
            首页
          </router-link>
          <router-link to="/blog" class="nav-link" active-class="active">
            博客
          </router-link>
          <router-link to="/about" class="nav-link" active-class="active">
            关于
          </router-link>
        </div>

        <!-- 用户操作区域 -->
        <div class="nav-actions">
          <!-- 未登录状态 -->
          <template v-if="!isAuthenticated">
            <router-link to="/auth/login" class="nav-link">
              登录
            </router-link>
            <AppleButton 
              type="primary" 
              size="small"
              @click="$router.push('/auth/register')"
            >
              注册
            </AppleButton>
          </template>

          <!-- 已登录状态 -->
          <template v-else>
            <!-- 创建按钮 -->
            <AppleButton 
              type="ghost" 
              size="small"
              prefix-icon="Plus"
              @click="$router.push('/blog/create')"
              v-if="hasPermission('blog:create')"
            >
              写文章
            </AppleButton>

            <!-- 用户菜单 -->
            <el-dropdown @command="handleUserCommand" trigger="click">
              <div class="user-avatar">
                <img :src="getUserAvatar" :alt="getUserNickname" />
                <span class="user-name">{{ getUserNickname }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人资料
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    账户设置
                  </el-dropdown-item>
                  <el-dropdown-item command="admin" v-if="isAdmin">
                    <el-icon><Tools /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><Close /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <!-- 移动端菜单按钮 -->
          <button class="mobile-menu-btn" @click="toggleMobileMenu">
            <el-icon><Menu /></el-icon>
          </button>
        </div>
      </nav>

      <!-- 移动端菜单 -->
      <div v-show="showMobileMenu" class="mobile-menu apple-blur">
        <div class="mobile-nav-links">
          <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">
            首页
          </router-link>
          <router-link to="/blog" class="mobile-nav-link" @click="closeMobileMenu">
            博客
          </router-link>
          <router-link to="/about" class="mobile-nav-link" @click="closeMobileMenu">
            关于
          </router-link>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <router-view v-slot="{ Component, route }">
        <transition :name="route.meta.transition || 'fade'" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="main-footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>PersonWeb</h3>
            <p>专注于创造优雅的数字体验</p>
          </div>
          <div class="footer-section">
            <h4>快速链接</h4>
            <router-link to="/" class="footer-link">首页</router-link>
            <router-link to="/blog" class="footer-link">博客</router-link>
            <router-link to="/about" class="footer-link">关于</router-link>
          </div>
          <div class="footer-section">
            <h4>联系我们</h4>
            <a href="mailto:hello@personweb.com" class="footer-link">hello@personweb.com</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 PersonWeb. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import AppleButton from '@/components/common/AppleButton.vue'
import { 
  User, 
  Setting, 
  Tools, 
  Close, 
  Menu, 
  Plus 
} from '@element-plus/icons-vue'

/**
 * 主布局组件
 * 提供统一的页面布局和导航功能
 */

const router = useRouter()
const { 
  isAuthenticated, 
  isAdmin, 
  hasPermission, 
  getUserAvatar, 
  getUserNickname, 
  logout 
} = useAuth()

// 移动端菜单状态
const showMobileMenu = ref(false)

// 切换移动端菜单
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

// 关闭移动端菜单
const closeMobileMenu = () => {
  showMobileMenu.value = false
}

// 处理用户菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/profile/settings')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      await logout()
      break
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

// 顶部导航栏
.main-header {
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  
  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 var(--apple-space-lg);
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .brand-logo {
    display: flex;
    align-items: center;
    gap: var(--apple-space-sm);
    text-decoration: none;
    color: var(--apple-text-primary);
    transition: transform var(--apple-transition-fast);
    
    &:hover {
      transform: scale(1.05);
    }
    
    .logo-icon {
      width: 32px;
      height: 32px;
      background: linear-gradient(135deg, var(--apple-blue), var(--apple-purple));
      border-radius: var(--apple-radius-sm);
      display: flex;
      align-items: center;
      justify-content: center;
      
      .logo-symbol {
        color: white;
        font-size: var(--apple-text-md);
        font-weight: 700;
      }
    }
    
    .brand-name {
      font-size: var(--apple-text-lg);
      font-weight: 600;
    }
  }
  
  .nav-menu {
    display: flex;
    align-items: center;
    gap: var(--apple-space-xl);
    
    .nav-link {
      color: var(--apple-text-secondary);
      text-decoration: none;
      font-weight: 500;
      transition: color var(--apple-transition-fast);
      position: relative;
      
      &:hover {
        color: var(--apple-blue);
      }
      
      &.active {
        color: var(--apple-blue);
        
        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 0;
          right: 0;
          height: 2px;
          background: var(--apple-blue);
          border-radius: 1px;
        }
      }
    }
  }
  
  .nav-actions {
    display: flex;
    align-items: center;
    gap: var(--apple-space-md);
    
    .nav-link {
      color: var(--apple-text-secondary);
      text-decoration: none;
      font-weight: 500;
      transition: color var(--apple-transition-fast);
      
      &:hover {
        color: var(--apple-blue);
      }
    }
    
    .user-avatar {
      display: flex;
      align-items: center;
      gap: var(--apple-space-sm);
      cursor: pointer;
      padding: var(--apple-space-xs) var(--apple-space-sm);
      border-radius: var(--apple-radius-lg);
      transition: background-color var(--apple-transition-fast);
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.05);
      }
      
      img {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        object-fit: cover;
      }
      
      .user-name {
        font-weight: 500;
        color: var(--apple-text-primary);
      }
    }
    
    .mobile-menu-btn {
      display: none;
      background: none;
      border: none;
      cursor: pointer;
      padding: var(--apple-space-xs);
      border-radius: var(--apple-radius-sm);
      transition: background-color var(--apple-transition-fast);
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.05);
      }
    }
  }
  
  // 移动端菜单
  .mobile-menu {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    
    .mobile-nav-links {
      max-width: 1200px;
      margin: 0 auto;
      padding: var(--apple-space-lg);
      display: flex;
      flex-direction: column;
      gap: var(--apple-space-md);
      
      .mobile-nav-link {
        color: var(--apple-text-primary);
        text-decoration: none;
        font-weight: 500;
        padding: var(--apple-space-sm) 0;
        transition: color var(--apple-transition-fast);
        
        &:hover {
          color: var(--apple-blue);
        }
      }
    }
  }
}

// 主要内容区域
.main-content {
  flex: 1;
  min-height: calc(100vh - 64px);
}

// 页脚
.main-footer {
  background: var(--apple-bg-primary);
  border-top: 1px solid var(--apple-gray-5);
  margin-top: var(--apple-space-2xl);
  
  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: var(--apple-space-2xl) var(--apple-space-lg);
  }
  
  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: var(--apple-space-xl);
    margin-bottom: var(--apple-space-xl);
    
    .footer-section {
      h3, h4 {
        color: var(--apple-text-primary);
        margin-bottom: var(--apple-space-md);
        font-weight: 600;
      }
      
      p {
        color: var(--apple-text-secondary);
        line-height: 1.6;
      }
      
      .footer-link {
        display: block;
        color: var(--apple-text-secondary);
        text-decoration: none;
        margin-bottom: var(--apple-space-sm);
        transition: color var(--apple-transition-fast);
        
        &:hover {
          color: var(--apple-blue);
        }
      }
    }
  }
  
  .footer-bottom {
    padding-top: var(--apple-space-lg);
    border-top: 1px solid var(--apple-gray-5);
    text-align: center;
    
    p {
      color: var(--apple-text-tertiary);
      font-size: var(--apple-text-sm);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .main-header {
    .nav-menu {
      display: none;
    }
    
    .nav-actions .mobile-menu-btn {
      display: block;
    }
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: var(--apple-space-lg);
  }
}
</style> 