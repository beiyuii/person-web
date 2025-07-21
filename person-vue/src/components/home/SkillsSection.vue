<template>
  <section id="skills" class="skills-section" ref="skillsRef">
    <div class="container">
      <h2 class="section-title">技术栈</h2>
      <div class="skills-grid">
        <div 
          v-for="(skill, index) in skills" 
          :key="`skill-${skill.name}-${index}`"
          class="skill-card apple-card"
          :ref="(el) => setSkillCardRef(el, index)"
          @mouseenter="handleSkillHover(index, true)"
          @mouseleave="handleSkillHover(index, false)"
          :data-skill="skill.slug"
          :style="{ '--index': index }"
        >
          <div class="skill-icon">
            <el-icon size="32">
              <component :is="skill.icon" />
            </el-icon>
          </div>
          <h3>{{ skill.name }}</h3>
          <p class="skill-main-desc">{{ skill.description }}</p>
          
          <!-- 详细技能列表 -->
          <div class="skill-details">
            <div 
              v-for="detail in skill.details" 
              :key="detail" 
              class="skill-detail-item"
            >
              {{ detail }}
            </div>
          </div>
          
          <div class="skill-progress">
            <div class="progress-label">
              <span>熟练度</span>
              <span class="progress-value">{{ skill.level }}%</span>
            </div>
            <div class="progress-track">
              <div 
                class="progress-fill" 
                :style="{ width: skill.level + '%' }"
              ></div>
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
  DataLine, 
  Monitor, 
  Tools 
} from '@element-plus/icons-vue'

gsap.registerPlugin(ScrollTrigger)

// Refs
const skillsRef = ref(null)
const skillCards = ref([])

// 技能数据
const skills = ref([
  {
    name: '后端开发',
    slug: 'backend',
    description: 'Java • Spring Boot • MyBatis Plus',
    details: ['Java（精通）', 'Spring/Spring Boot（熟练）', 'MyBatis Plus（熟悉）', 'Shiro + JWT', 'RESTful API'],
    level: 95,
    icon: markRaw(Service)
  },
  {
    name: '数据处理',
    slug: 'database', 
    description: 'MySQL • Redis • Elasticsearch',
    details: ['MySQL（熟练）', 'Redis（熟悉）', 'Elasticsearch', '数据建模', '性能调优'],
    level: 88,
    icon: markRaw(DataLine)
  },
  {
    name: '前端技术',
    slug: 'frontend',
    description: 'Vue • JavaScript • Bootstrap',
    details: ['VUE全家桶', 'HTML/CSS/JavaScript', 'Element UI', 'Bootstrap', 'JSP'],
    level: 82,
    icon: markRaw(Monitor)
  },
  {
    name: '工具&协作',
    slug: 'tools',
    description: 'Git • Maven • Linux • Docker',
    details: ['Git/GitHub/GitLab', 'Maven', 'APIfox/Postman', 'Linux基础', 'JVM调优'],
    level: 85,
    icon: markRaw(Tools)
  }
])

// 设置技能卡片引用
const setSkillCardRef = (el, index) => {
  if (el) {
    if (!skillCards.value) skillCards.value = []
    skillCards.value[index] = el
  }
}

// 技能卡片悬停效果
const handleSkillHover = (index, isHover) => {
  const card = skillCards.value[index]
  if (!card) return
  
  if (isHover) {
    gsap.to(card, {
      y: -10,
      scale: 1.02,
      duration: 0.3,
      ease: "power2.out"
    })
  } else {
    gsap.to(card, {
      y: 0,
      scale: 1,
      duration: 0.3,
      ease: "power2.out"
    })
  }
}

// 技能卡片动画
const animateSkills = () => {
  if (!skillCards.value.length) return
  
  skillCards.value.forEach((card, index) => {
    if (card) {
      gsap.fromTo(card, 
        {
          opacity: 0,
          y: 50,
          scale: 0.9
        },
        {
          opacity: 1,
          y: 0,
          scale: 1,
          duration: 0.6,
          delay: index * 0.1,
          ease: "power3.out",
          scrollTrigger: {
            trigger: card,
            start: "top 80%",
            toggleActions: "play none none reverse"
          }
        }
      )
    }
  })
}

onMounted(() => {
  setTimeout(() => {
    animateSkills()
  }, 100)
})
</script>

<style scoped>
.skills-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #dbeafe 0%, #f1f5f9 100%);
  position: relative;
  overflow: hidden;
}

.skills-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(59, 130, 246, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(99, 102, 241, 0.1) 0%, transparent 50%);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.skill-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.skill-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.skill-card:hover {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(102, 126, 234, 0.3);
}

.skill-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  color: white;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.skill-card h3 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.skill-main-desc {
  color: #7f8c8d;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
  line-height: 1.5;
}

.skill-details {
  margin-bottom: 1.5rem;
}

.skill-detail-item {
  padding: 0.4rem 0;
  color: #34495e;
  font-size: 0.9rem;
  position: relative;
  padding-left: 1rem;
}

.skill-detail-item::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
}

.skill-progress {
  margin-top: auto;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #2c3e50;
  font-weight: 500;
}

.progress-value {
  color: #667eea;
  font-weight: 600;
}

.progress-track {
  height: 8px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  transition: width 1s ease-in-out;
  position: relative;
  overflow: hidden;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.4),
    transparent
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .skills-section {
    padding: 80px 0;
  }
  
  .section-title {
    font-size: 2.5rem;
    margin-bottom: 3rem;
  }
  
  .skills-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .skill-card {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .section-title {
    font-size: 2rem;
  }
  
  .skill-card {
    border-radius: 16px;
    padding: 1.25rem;
  }
  
  .skill-card h3 {
    font-size: 1.25rem;
  }
}
</style> 