<template>
  <footer class="footer-section">
    <div class="container">
      <div class="footer-content">
        <!-- 联系信息 -->
        <div class="footer-contact">
          <h3>联系方式</h3>
          <div class="contact-list">
            <div class="contact-item">
              <el-icon><Monitor /></el-icon>
              <span>技术博客</span>
            </div>
            <div class="contact-item">
              <el-icon><Document /></el-icon>
              <span>技术文档</span>
            </div>
            <div class="contact-item">
              <el-icon><Setting /></el-icon>
              <span>开源项目</span>
            </div>
          </div>
        </div>

        <!-- 技术栈 -->
        <div class="footer-tech">
          <h3>技术栈</h3>
          <div class="tech-list">
            <span class="tech-tag">Vue3</span>
            <span class="tech-tag">Spring Boot</span>
            <span class="tech-tag">Redis</span>
            <span class="tech-tag">MySQL</span>
            <span class="tech-tag">Elasticsearch</span>
            <span class="tech-tag">Docker</span>
          </div>
        </div>

        <!-- 快速导航 -->
        <div class="footer-nav">
          <h3>快速导航</h3>
          <div class="nav-list">
            <a href="#" @click="scrollToSection('skills')">技术栈</a>
            <a href="#" @click="scrollToSection('works')">精选作品</a>
            <a href="#" @click="$router.push('/auth/login')">登录体验</a>
            <a href="#" @click="$router.push('/blog')">技术博客</a>
          </div>
        </div>

        <!-- 友情链接 -->
        <div class="footer-links-section">
          <h3>友情链接</h3>
          <div class="friendly-links">
            <a v-for="link in friendlyLinks" 
               :key="link.name" 
               :href="link.url" 
               target="_blank" 
               rel="noopener noreferrer"
               class="friendly-link"
               :title="link.description">
              <el-icon><component :is="link.icon" /></el-icon>
              <span>{{ link.name }}</span>
            </a>
          </div>
        </div>
      </div>

      <!-- 分割线 -->
      <div class="footer-divider"></div>

      <!-- 版权信息 -->
      <div class="footer-bottom">
        <div class="footer-copyright">
          <p>&copy; {{ currentYear }} Person Web. 基于Vue3+Spring Boot构建</p>
          <p class="footer-slogan">用代码创造美好，让技术改变世界</p>
        </div>
        <div class="footer-links">
          <a href="#">隐私政策</a>
          <a href="#">服务条款</a>
          <a href="#">关于我们</a>
        </div>
      </div>

      <!-- 回到顶部按钮 -->
      <div class="back-to-top" @click="scrollToTop" ref="backToTopRef">
        <el-icon><View /></el-icon>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { 
  View, 
  Document, 
  Setting, 
  Monitor,
  Platform,
  Service,
  Connection
} from '@element-plus/icons-vue'

const router = useRouter()

// Refs
const backToTopRef = ref(null)

// 页脚数据
const currentYear = new Date().getFullYear()

const friendlyLinks = ref([
  {
    name: 'GitHub',
    url: 'https://github.com',
    description: '全球最大的代码托管平台',
    icon: markRaw(Platform)
  },
  {
    name: 'Vue.js',
    url: 'https://vuejs.org',
    description: '渐进式JavaScript框架',
    icon: markRaw(Setting)
  },
  {
    name: 'Spring Boot',
    url: 'https://spring.io/projects/spring-boot',
    description: 'Java企业级开发框架',
    icon: markRaw(Service)
  },
  {
    name: 'Element Plus',
    url: 'https://element-plus.org',
    description: 'Vue3组件库',
    icon: markRaw(Connection)
  }
])

// 滚动到指定区域
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ 
      behavior: 'smooth',
      block: 'start'
    })
  }
}

// 回到顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 监听滚动显示回到顶部按钮
const handleScroll = () => {
  if (!backToTopRef.value) return
  
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const isVisible = scrollTop > 300
  
  backToTopRef.value.style.opacity = isVisible ? '1' : '0'
  backToTopRef.value.style.visibility = isVisible ? 'visible' : 'hidden'
  backToTopRef.value.style.transform = `translateY(${isVisible ? '0' : '20px'})`
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  
  // 清理监听器
  return () => {
    window.removeEventListener('scroll', handleScroll)
  }
})
</script>

<style scoped>
.footer-section {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  padding: 4rem 0 2rem;
  position: relative;
  overflow: hidden;
}

.footer-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 3rem;
  margin-bottom: 3rem;
}

.footer-content h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: white;
  position: relative;
}

.footer-content h3::after {
  content: '';
  position: absolute;
  bottom: -0.5rem;
  left: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 联系信息样式 */
.contact-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: rgba(255, 255, 255, 0.8);
  transition: color 0.3s ease;
  cursor: pointer;
}

.contact-item:hover {
  color: #667eea;
}

.contact-item .el-icon {
  color: #667eea;
}

/* 技术栈样式 */
.tech-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.tech-tag {
  background: rgba(102, 126, 234, 0.2);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  border: 1px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  cursor: pointer;
}

.tech-tag:hover {
  background: rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

/* 快速导航样式 */
.nav-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.nav-list a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  padding-left: 1rem;
}

.nav-list a::before {
  content: '→';
  position: absolute;
  left: 0;
  color: #667eea;
  transition: transform 0.3s ease;
}

.nav-list a:hover {
  color: #667eea;
  padding-left: 1.5rem;
}

.nav-list a:hover::before {
  transform: translateX(0.25rem);
}

/* 友情链接样式 */
.friendly-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.friendly-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  padding: 0.75rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.friendly-link:hover {
  color: white;
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateY(-2px);
}

.friendly-link .el-icon {
  color: #667eea;
  flex-shrink: 0;
}

.friendly-link span {
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 分割线 */
.footer-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  margin: 2rem 0;
}

/* 版权信息 */
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  padding-top: 1rem;
}

.footer-copyright p {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.footer-slogan {
  font-style: italic;
  color: rgba(255, 255, 255, 0.6) !important;
  font-size: 0.85rem !important;
}

.footer-links {
  display: flex;
  gap: 1.5rem;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #667eea;
}

/* 回到顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  opacity: 0;
  visibility: hidden;
  transform: translateY(20px);
  z-index: 1000;
}

.back-to-top:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.back-to-top .el-icon {
  transform: rotate(-90deg);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .footer-section {
    padding: 3rem 0 1.5rem;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
    text-align: center;
  }
  
  .footer-content h3::after {
    left: 50%;
    transform: translateX(-50%);
  }
  
  .footer-bottom {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
  
  .footer-links {
    justify-content: center;
  }
  
  .back-to-top {
    bottom: 1rem;
    right: 1rem;
    width: 45px;
    height: 45px;
  }
}

@media (max-width: 480px) {
  .footer-content {
    padding: 0 1rem;
  }
  
  .tech-list {
    justify-content: center;
  }
  
  .friendly-links {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .footer-links {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style> 