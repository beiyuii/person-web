<template>
  <section id="works" class="works-section" ref="worksRef">
    <div class="container">
      <h2 class="section-title">精选作品</h2>
      <div class="works-masonry" ref="masonryRef">
        <div 
          v-for="(work, index) in works" 
          :key="work.id"
          class="work-item"
          :class="`work-${index % 3 + 1}`"
          @click="openWorkDetail(work)"
        >
          <div class="work-image">
            <div class="work-placeholder">
              <div class="work-icon" :style="{ background: work.iconBg }">
                <el-icon size="48">
                  <component :is="work.icon" />
                </el-icon>
              </div>
            </div>
            <div class="work-overlay">
              <div class="work-period">{{ work.period }}</div>
              <h3>{{ work.title }}</h3>
              <p class="work-desc">{{ work.description }}</p>
              <div class="work-achievements">
                <div v-for="achievement in work.achievements.slice(0, 2)" :key="achievement" class="achievement-item">
                  • {{ achievement }}
                </div>
              </div>
              <div class="work-tags">
                <span v-for="tag in work.tags.slice(0, 3)" :key="tag" class="work-tag">
                  {{ tag }}
                </span>
                <span v-if="work.tags.length > 3" class="work-tag more">
                  +{{ work.tags.length - 3 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 作品详情模态框 -->
    <el-dialog 
      v-model="showWorkDetail" 
      :title="currentWork?.title"
      width="90%"
      :max-width="800"
      :before-close="closeWorkDetail"
      class="work-detail-dialog"
    >
      <div v-if="currentWork" class="work-detail-content">
        <div class="work-detail-header">
          <div class="work-detail-icon" :style="{ background: currentWork.iconBg }">
            <el-icon size="32">
              <component :is="currentWork.icon" />
            </el-icon>
          </div>
          <div class="work-detail-info">
            <h3>{{ currentWork.title }}</h3>
            <p class="work-period">{{ currentWork.period }}</p>
            <p class="work-description">{{ currentWork.description }}</p>
          </div>
        </div>
        
        <div class="work-detail-body">
          <div class="achievements-section">
            <h4>主要成就</h4>
            <div class="achievements-list">
              <div v-for="achievement in currentWork.achievements" :key="achievement" class="achievement-detail">
                <el-icon><Check /></el-icon>
                <span>{{ achievement }}</span>
              </div>
            </div>
          </div>
          
          <div class="tech-stack-section">
            <h4>技术栈</h4>
            <div class="tech-tags">
              <span v-for="tag in currentWork.tags" :key="tag" class="tech-tag">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </section>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { 
  Service, 
  Monitor, 
  Platform,
  Check
} from '@element-plus/icons-vue'

gsap.registerPlugin(ScrollTrigger)

// Refs
const worksRef = ref(null)
const masonryRef = ref(null)
const showWorkDetail = ref(false)
const currentWork = ref(null)

// 作品数据
const works = ref([
  {
    id: 1,
    title: 'Linter-AI轻量级应用平台',
    description: '基于Spring Boot的AI模型管理与数据处理平台，提供模型训练、数据预处理、知识分享等一体化服务',
    period: '2023.09 - 2024.08',
    image: null,
    icon: markRaw(Service),
    iconBg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    tags: ['Spring Boot', 'MyBatis Plus', 'Redis', 'Elasticsearch', 'Shiro + JWT'],
    achievements: [
      '核心接口响应优化至85ms（3.3×提升）',
      '缓存命中率达92%，支撑200+并发',
      'ES搜索性能提升12×，响应≤50ms',
      '日均处理150+训练任务，可用性99.6%+'
    ]
  },
  {
    id: 2,
    title: 'WebGIS校园安全系统',
    description: '车辆车牌检测算法与前端监控界面，实现高精度车牌识别与可视化展示',
    period: '2022.07 - 2023.08',
    image: null,
    icon: markRaw(Monitor),
    iconBg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    tags: ['YOLOv5', 'OpenCV', 'Flask', 'JSP', 'Bootstrap'],
    achievements: [
      '检测精度(mAP)提升至93%',
      '推理时延降至45ms（2.7×提升）',
      '支持30FPS视频流，峰值200TPS',
      '前端首屏加载优化至260ms'
    ]
  },
  {
    id: 3,
    title: 'i创购电商平台',
    description: '商品展示-购物车-支付业务场景，实现高并发订单处理与高效搜索匹配',
    period: '2022.06 - 2023.03',
    image: null,
    icon: markRaw(Platform),
    iconBg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    tags: ['Spring Boot', 'MyBatis Plus', 'Redis', 'Elasticsearch', 'Element UI'],
    achievements: [
      '支付接口响应降至90ms（4×提升）',
      '首屏加载优化至380ms',
      'ES搜索响应降至70ms（8.5×提升）',
      '日均2,500+笔订单，可用性99.7%+'
    ]
  }
])

// 打开作品详情
const openWorkDetail = (work) => {
  currentWork.value = work
  showWorkDetail.value = true
}

// 关闭作品详情
const closeWorkDetail = () => {
  showWorkDetail.value = false
  setTimeout(() => {
    currentWork.value = null
  }, 300)
}

// 作品卡片动画
const animateWorks = () => {
  const workItems = masonryRef.value?.querySelectorAll('.work-item')
  if (!workItems?.length) return

  workItems.forEach((item, index) => {
    gsap.fromTo(item, 
      {
        opacity: 0,
        y: 60,
        scale: 0.8
      },
      {
        opacity: 1,
        y: 0,
        scale: 1,
        duration: 0.8,
        delay: index * 0.15,
        ease: "power3.out",
        scrollTrigger: {
          trigger: item,
          start: "top 85%",
          toggleActions: "play none none reverse"
        }
      }
    )
  })
}

onMounted(() => {
  setTimeout(() => {
    animateWorks()
  }, 100)
})
</script>

<style scoped>
.works-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.works-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px),
    linear-gradient(180deg, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 60px 60px;
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

.works-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.work-item {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  will-change: transform;
}

.work-item:hover {
  transform: translateY(-15px) scale(1.05);
}

.work-image {
  height: 400px;
  border-radius: 20px;
  overflow: hidden;
  position: relative;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.work-item:hover .work-image {
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.15);
}

.work-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.work-placeholder::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.work-icon {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border: 3px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.work-item:hover .work-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25), 0 0 30px rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.work-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.work-icon .el-icon {
  color: white;
  z-index: 1;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.work-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1.5rem;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.85));
  backdrop-filter: blur(15px);
  color: white;
  transform: translateY(100%);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  opacity: 0;
}

.work-item:hover .work-overlay {
  transform: translateY(0);
  opacity: 1;
}

.work-period {
  font-size: 0.85rem;
  opacity: 0.8;
  margin-bottom: 0.5rem;
}

.work-overlay h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: white;
}

.work-desc {
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 1rem;
  opacity: 0.9;
}

.work-achievements {
  margin-bottom: 1rem;
}

.achievement-item {
  font-size: 0.85rem;
  opacity: 0.8;
  margin-bottom: 0.25rem;
}

.work-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.work-tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.work-tag.more {
  background: rgba(255, 255, 255, 0.1);
}

/* 模态框样式 */
:deep(.work-detail-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.work-detail-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem 2rem;
}

:deep(.work-detail-dialog .el-dialog__title) {
  color: white;
  font-weight: 600;
}

:deep(.work-detail-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

.work-detail-content {
  padding: 2rem;
}

.work-detail-header {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  align-items: flex-start;
}

.work-detail-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.work-detail-icon .el-icon {
  color: white;
}

.work-detail-info h3 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.work-detail-info .work-period {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.work-detail-info .work-description {
  color: #34495e;
  line-height: 1.6;
}

.work-detail-body h4 {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.achievements-section {
  margin-bottom: 2rem;
}

.achievements-list {
  display: grid;
  gap: 0.75rem;
}

.achievement-detail {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.achievement-detail:hover {
  background: #e8f4fd;
  transform: translateX(5px);
}

.achievement-detail .el-icon {
  color: #667eea;
  flex-shrink: 0;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.tech-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
}

.tech-tag:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .works-section {
    padding: 80px 0;
  }
  
  .section-title {
    font-size: 2.5rem;
    margin-bottom: 3rem;
  }
  
  .works-masonry {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .work-item {
    max-width: none;
  }
  
  .work-detail-header {
    flex-direction: column;
    text-align: center;
  }
  
  .work-detail-icon {
    margin: 0 auto;
  }
}

@media (max-width: 480px) {
  .section-title {
    font-size: 2rem;
  }
  
  .work-image {
    height: 300px;
  }
  
  .work-detail-content {
    padding: 1rem;
  }
  
  .tech-tags {
    gap: 0.5rem;
  }
  
  .tech-tag {
    padding: 0.4rem 0.8rem;
    font-size: 0.8rem;
  }
}
</style> 