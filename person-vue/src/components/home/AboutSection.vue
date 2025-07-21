<template>
  <section id="about" class="about-section" ref="aboutRef">
    <div class="container">
      <h2 class="section-title">关于我</h2>
      <div class="about-content">
        <div class="about-grid">
          <!-- 个人介绍 -->
          <div class="about-intro">
            <div class="avatar-container" ref="avatarRef">
              <div class="avatar">
                <div class="avatar-ring"></div>
                <div class="avatar-placeholder">
                  <div class="avatar-initials">P</div>
                </div>
              </div>
            </div>
            <div class="intro-text">
              <h3>个人简介</h3>
              <p>{{ aboutText }}</p>
              <div class="contact-info">
                <div class="contact-item">
                  <el-icon><Monitor /></el-icon>
                  <span>全栈开发工程师</span>
                </div>
                <div class="contact-item">
                  <el-icon><Setting /></el-icon>
                  <span>5年开发经验</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 技能熟练度进度条 -->
          <div class="skills-progress-section">
            <h3 class="subsection-title">核心技能熟练度</h3>
            <div class="skills-progress-list">
              <div v-for="skill in coreSkills" :key="skill.name" class="skill-progress-item">
                <div class="skill-info">
                  <span class="skill-name">{{ skill.name }}</span>
                  <span class="skill-percentage">{{ skill.level }}%</span>
                </div>
                <div class="skill-bar">
                  <div 
                    class="skill-fill" 
                    :style="{ 
                      width: skill.level + '%', 
                      animationDelay: skill.delay,
                      '--target-width': skill.level + '%'
                    }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 职业时间轴 -->
          <div class="timeline-section">
            <h3 class="subsection-title">职业发展历程</h3>
            <div class="timeline">
              <div v-for="(event, index) in timeline" :key="index" class="timeline-item">
                <div class="timeline-marker">
                  <el-icon><component :is="event.icon" /></el-icon>
                </div>
                <div class="timeline-content">
                  <div class="timeline-date">{{ event.date }}</div>
                  <h4 class="timeline-title">{{ event.title }}</h4>
                  <p class="timeline-desc">{{ event.description }}</p>
                  <div class="timeline-tags">
                    <span v-for="tag in event.tags" :key="tag" class="timeline-tag">
                      {{ tag }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 统计数据卡片 -->
          <div class="stats-section">
            <h3 class="subsection-title">数据统计</h3>
            <div class="stats-grid">
              <div v-for="stat in enhancedStats" :key="stat.label" class="stat-card">
                <div class="stat-icon">
                  <el-icon><component :is="stat.icon" /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-number" :ref="setStatNumberRef">
                    {{ stat.value }}
                  </div>
                  <div class="stat-label">{{ stat.label }}</div>
                  <div class="stat-progress">
                    <div 
                      class="stat-progress-fill" 
                      :style="{ width: stat.progress + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { 
  Service, 
  Monitor, 
  Setting, 
  Document,
  Tools
} from '@element-plus/icons-vue'

gsap.registerPlugin(ScrollTrigger)

// Refs
const aboutRef = ref(null)
const avatarRef = ref(null)
const statNumbers = ref([])

// 数据
const aboutText = ref('我是一名充满激情的全栈开发者，专注于创造优雅而强大的数字体验。从前端的精美界面到后端的稳定架构，我致力于将创意转化为现实，用代码构建更美好的数字世界。')

// 核心技能熟练度数据
const coreSkills = ref([
  { name: 'Java开发', level: 95, delay: '0.1s' },
  { name: 'Spring生态', level: 90, delay: '0.2s' },
  { name: 'MySQL/Redis', level: 88, delay: '0.3s' },
  { name: 'Vue.js', level: 82, delay: '0.4s' },
  { name: 'Linux运维', level: 80, delay: '0.5s' }
])

// 时间轴数据
const timeline = ref([
  {
    date: '2023.09 - 2024.08',
    title: 'Linter-AI轻量级应用平台',
    description: '负责AI模型管理与数据处理平台的设计开发，实现核心接口3.3倍性能提升',
    tags: ['Spring Boot', 'Redis', 'Elasticsearch'],
    icon: markRaw(Service)
  },
  {
    date: '2022.07 - 2023.08',
    title: 'WebGIS校园安全系统',
    description: '开发车牌检测算法与监控界面，检测精度提升至93%，支持30FPS实时处理',
    tags: ['YOLOv5', 'OpenCV', 'Flask'],
    icon: markRaw(Monitor)
  },
  {
    date: '2022.06 - 2023.03',
    title: 'i创购电商平台',
    description: '构建电商核心业务系统，支付接口性能提升4倍，日均处理2500+订单',
    tags: ['Spring Boot', 'MySQL', 'Redis'],
    icon: markRaw(Setting)
  },
  {
    date: '2019 - 2022',
    title: '计算机专业学习',
    description: '系统学习计算机科学基础知识，掌握数据结构、算法、数据库等核心技能',
    tags: ['数据结构', '算法', '数据库'],
    icon: markRaw(Document)
  }
])

// 增强统计数据
const enhancedStats = ref([
  { 
    label: '项目经验', 
    value: '8+', 
    progress: 80,
    icon: markRaw(Service)
  },
  { 
    label: '技术栈', 
    value: '25+', 
    progress: 90,
    icon: markRaw(Tools)
  },
  { 
    label: '开发年限', 
    value: '5+', 
    progress: 75,
    icon: markRaw(Monitor)
  },
  { 
    label: '代码质量', 
    value: '95%', 
    progress: 95,
    icon: markRaw(Setting)
  }
])

// 设置统计数字引用
const setStatNumberRef = (el) => {
  if (el) {
    if (!statNumbers.value) statNumbers.value = []
    statNumbers.value.push(el)
  }
}

// 头像动画
const animateAvatar = () => {
  if (!avatarRef.value) return
  
  gsap.fromTo(avatarRef.value, 
    {
      scale: 0.8,
      opacity: 0,
      rotation: -10
    },
    {
      scale: 1,
      opacity: 1,
      rotation: 0,
      duration: 1,
      ease: "back.out(1.7)",
      scrollTrigger: {
        trigger: avatarRef.value,
        start: "top 80%",
        toggleActions: "play none none reverse"
      }
    }
  )
}

// 关于我区域动画
const animateAbout = () => {
  const elements = aboutRef.value?.querySelectorAll('.about-intro, .skills-progress-section, .timeline-section, .stats-section')
  if (!elements?.length) return

  elements.forEach((element, index) => {
    gsap.fromTo(element, 
      {
        opacity: 0,
        y: 50
      },
      {
        opacity: 1,
        y: 0,
        duration: 0.8,
        delay: index * 0.2,
        ease: "power3.out",
        scrollTrigger: {
          trigger: element,
          start: "top 80%",
          toggleActions: "play none none reverse"
        }
      }
    )
  })
}

onMounted(() => {
  setTimeout(() => {
    animateAvatar()
    animateAbout()
  }, 100)
})
</script>

<style scoped>
.about-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;
}

.about-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(255,255,255,0.2) 2px, transparent 2px),
    radial-gradient(circle at 80% 20%, rgba(255,255,255,0.2) 2px, transparent 2px),
    radial-gradient(circle at 40% 40%, rgba(255,255,255,0.15) 1px, transparent 1px);
  background-size: 100px 100px, 120px 120px, 60px 60px;
  animation: float-particles 20s linear infinite;
  pointer-events: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.section-title {
  text-align: center;
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 4rem;
  color: white;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.about-grid {
  display: grid;
  gap: 3rem;
  max-width: 1000px;
  margin: 0 auto;
}

.about-intro {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2.5rem;
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 2rem;
  align-items: center;
}

.avatar-container {
  display: flex;
  justify-content: center;
}

.avatar {
  position: relative;
  width: 150px;
  height: 150px;
}

.avatar-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  animation: rotate 3s linear infinite;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  border: 4px solid white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.avatar-initials {
  color: white;
  font-size: 3rem;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.intro-text h3 {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: white;
}

.intro-text p {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.contact-info {
  display: flex;
  gap: 2rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.subsection-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: white;
  text-align: center;
  position: relative;
}

.subsection-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.3));
  border-radius: 2px;
}

/* 技能进度条样式 */
.skills-progress-section {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2.5rem;
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.skills-progress-list {
  display: grid;
  gap: 1.5rem;
}

.skill-progress-item {
  padding: 1rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  border-left: 4px solid rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.skill-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.skill-name {
  font-weight: 600;
  color: white;
}

.skill-percentage {
  font-weight: 700;
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
}

.skill-bar {
  height: 12px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
}

.skill-fill {
  height: 100%;
  background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 50%, #cbd5e1 100%);
  border-radius: 6px;
  width: 0%;
  transition: width 2s ease-in-out;
  position: relative;
  animation: fillProgress 2s ease-in-out forwards;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.3);
}

@keyframes fillProgress {
  from { width: 0%; }
  to { width: var(--target-width); }
}

.skill-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.5),
    transparent
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes float-particles {
  0% { transform: translateX(0) translateY(0); }
  33% { transform: translateX(30px) translateY(-30px); }
  66% { transform: translateX(-20px) translateY(20px); }
  100% { transform: translateX(0) translateY(0); }
}

/* 时间轴样式 */
.timeline-section {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2.5rem;
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.timeline {
  position: relative;
  padding-left: 2rem;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 1rem;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.2));
}

.timeline-item {
  position: relative;
  margin-bottom: 2rem;
}

.timeline-marker {
  position: absolute;
  left: -2rem;
  top: 0.5rem;
  width: 3rem;
  height: 3rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.1));
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(10px);
}

.timeline-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.timeline-date {
  color: #667eea;
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.timeline-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #2c3e50;
}

.timeline-desc {
  color: #34495e;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.timeline-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.timeline-tag {
  background: #667eea;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
}

/* 统计卡片样式 */
.stats-section {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2.5rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 16px;
  text-align: center;
  border-left: 4px solid #667eea;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  color: white;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #7f8c8d;
  font-weight: 500;
  margin-bottom: 1rem;
}

.stat-progress {
  height: 6px;
  background: rgba(102, 126, 234, 0.15);
  border-radius: 3px;
  overflow: hidden;
}

.stat-progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 2s ease-in-out;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .about-section {
    padding: 80px 0;
  }
  
  .section-title {
    font-size: 2.5rem;
    margin-bottom: 3rem;
  }
  
  .about-intro {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 1.5rem;
  }
  
  .contact-info {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
  
  .timeline {
    padding-left: 1.5rem;
  }
  
  .timeline-marker {
    left: -1.5rem;
  }
}

@media (max-width: 480px) {
  .section-title {
    font-size: 2rem;
  }
  
  .about-intro,
  .skills-progress-section,
  .timeline-section,
  .stats-section {
    padding: 1.5rem;
    border-radius: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .avatar {
    width: 120px;
    height: 120px;
  }
}
</style> 