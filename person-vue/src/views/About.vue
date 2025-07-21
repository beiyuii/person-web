<template>
  <div class="about-page">
    <div class="container">
      <div class="hero-section">
        <h1 class="page-title">关于PersonWeb</h1>
        <p class="page-subtitle">专注于创造优雅的数字体验</p>
      </div>
      
      <div class="content-section">
        <div class="feature-grid">
          <div class="feature-card apple-card">
            <h3>简洁设计</h3>
            <p>遵循苹果设计理念，追求简洁优雅的用户体验</p>
          </div>
          
          <div class="feature-card apple-card">
            <h3>现代技术</h3>
            <p>采用Vue3、Spring Boot等现代技术栈构建</p>
          </div>
          
          <div class="feature-card apple-card">
            <h3>开源精神</h3>
            <p>拥抱开源社区，分享知识与经验</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 关于页面
</script>

<style lang="scss" scoped>
.about-page {
  padding: var(--apple-space-2xl);
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .hero-section {
    text-align: center;
    margin-bottom: var(--apple-space-2xl);
    
    .page-title {
      font-size: var(--apple-text-4xl);
      font-weight: 700;
      color: var(--apple-text-primary);
      margin-bottom: var(--apple-space-md);
    }
    
    .page-subtitle {
      font-size: var(--apple-text-lg);
      color: var(--apple-text-secondary);
    }
  }
  
  .content-section {
    .feature-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: var(--apple-space-xl);
      
      .feature-card {
        text-align: center;
        padding: var(--apple-space-xl);
        
        h3 {
          font-size: var(--apple-text-xl);
          font-weight: 600;
          color: var(--apple-text-primary);
          margin-bottom: var(--apple-space-md);
        }
        
        p {
          color: var(--apple-text-secondary);
          line-height: 1.6;
        }
      }
    }
  }
}
</style> 