<template>
  <div class="blog-detail-page">
    <div class="container">
      <h1 class="page-title">博客详情</h1>
      <div class="coming-soon">
        <p>博客详情功能正在开发中...</p>
        <router-link to="/blog" class="back-link">返回博客列表</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
// 博客详情页面 - 开发中
</script>

<style lang="scss" scoped>
.blog-detail-page {
  padding: var(--apple-space-2xl);
  
  .container {
    max-width: 800px;
    margin: 0 auto;
    text-align: center;
  }
  
  .page-title {
    font-size: var(--apple-text-3xl);
    color: var(--apple-text-primary);
    margin-bottom: var(--apple-space-xl);
  }
  
  .back-link {
    color: var(--apple-blue);
    text-decoration: none;
    margin-top: var(--apple-space-md);
    display: inline-block;
  }
}
</style> 