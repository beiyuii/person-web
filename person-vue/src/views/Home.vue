<template>
  <div class="home-page">
    <!-- Hero Section -->
    <HeroSection />
    
    <!-- Skills Section -->
    <SkillsSection />
    
    <!-- Works Section -->
    <ProjectsSection />
    
    <!-- About Section -->
    <AboutSection />
    
    <!-- Footer Section -->
    <FooterSection />
  </div>
</template>

<script setup>
import HeroSection from '@/components/home/HeroSection.vue'
import SkillsSection from '@/components/home/SkillsSection.vue'
import ProjectsSection from '@/components/home/ProjectsSection.vue'
import AboutSection from '@/components/home/AboutSection.vue'
import FooterSection from '@/components/home/FooterSection.vue'
</script>

<style scoped>
.home-page {
  overflow-x: hidden;
}

/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 确保滚动流畅 */
html {
  scroll-behavior: smooth;
}

/* 优化字体渲染 */
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.6;
}

/* 隐藏滚动条但保持可滚动 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
}

/* 通用容器样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 通用标题样式 */
.section-title {
  text-align: center;
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 4rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 响应式标题 */
@media (max-width: 768px) {
  .section-title {
    font-size: 2.5rem;
    margin-bottom: 3rem;
  }
}

@media (max-width: 480px) {
  .section-title {
    font-size: 2rem;
    margin-bottom: 2rem;
  }
}
</style>
