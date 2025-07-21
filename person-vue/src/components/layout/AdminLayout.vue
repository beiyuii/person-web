<template>
  <div class="admin-layout">
    <header class="admin-header">
      <h1>PersonWeb 管理后台</h1>
      <router-link to="/" class="back-to-site">返回网站</router-link>
    </header>
    
    <div class="admin-container">
      <aside class="admin-sidebar">
        <nav class="admin-nav">
          <router-link to="/admin" class="nav-item">控制台</router-link>
          <router-link to="/admin/articles" class="nav-item">文章管理</router-link>
          <router-link to="/admin/comments" class="nav-item">评论管理</router-link>
          <router-link to="/admin/files" class="nav-item">文件管理</router-link>
          <router-link to="/admin/system" class="nav-item">系统配置</router-link>
        </nav>
      </aside>
      
      <main class="admin-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
// 管理后台布局组件
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: var(--apple-bg-primary);
  border-bottom: 1px solid var(--apple-gray-5);
  padding: var(--apple-space-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  h1 {
    font-size: var(--apple-text-xl);
    font-weight: 600;
    color: var(--apple-text-primary);
  }
  
  .back-to-site {
    color: var(--apple-blue);
    text-decoration: none;
  }
}

.admin-container {
  flex: 1;
  display: flex;
}

.admin-sidebar {
  width: 240px;
  background: var(--apple-bg-primary);
  border-right: 1px solid var(--apple-gray-5);
  padding: var(--apple-space-lg);
  
  .admin-nav {
    display: flex;
    flex-direction: column;
    gap: var(--apple-space-sm);
    
    .nav-item {
      padding: var(--apple-space-sm) var(--apple-space-md);
      border-radius: var(--apple-radius-sm);
      color: var(--apple-text-secondary);
      text-decoration: none;
      transition: all var(--apple-transition-fast);
      
      &:hover {
        background: var(--apple-gray-6);
        color: var(--apple-text-primary);
      }
      
      &.router-link-active {
        background: var(--apple-blue);
        color: white;
      }
    }
  }
}

.admin-main {
  flex: 1;
  padding: var(--apple-space-lg);
}
</style> 