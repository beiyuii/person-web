<template>
  <section ref="heroRef" class="hero-section">
    <div class="particles-container" ref="particlesRef"></div>
    <div class="hero-content">
      <h1 class="hero-title" ref="titleRef">
        <span class="gradient-text">{{ displayText }}</span>
        <span class="cursor">|</span>
      </h1>
      <p class="hero-subtitle" ref="subtitleRef">
        全栈开发者 · 创意设计师 · 技术探索者
      </p>
      <div class="hero-buttons" ref="buttonsRef">
        <AppleButton 
          type="primary" 
          size="large"
          @click="scrollToSection('works')"
        >
          <template #prefix>
            <el-icon><View /></el-icon>
          </template>
          查看作品
        </AppleButton>
        <AppleButton 
          type="outline" 
          size="large"
          @click="$router.push('/auth/login')"
        >
          开始体验
        </AppleButton>
      </div>
    </div>
    <div class="scroll-indicator" ref="scrollRef">
      <div class="scroll-line"></div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { View } from '@element-plus/icons-vue'
import AppleButton from '@/components/common/AppleButton.vue'

const router = useRouter()

// 响应式引用
const heroRef = ref(null)
const particlesRef = ref(null)
const titleRef = ref(null)
const subtitleRef = ref(null)
const buttonsRef = ref(null)
const scrollRef = ref(null)

// 打字机效果
const displayText = ref('')
const fullText = 'Hello, I\'m Developer'

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

// 打字机动画
const startTypewriter = () => {
  let i = 0
  const speed = 100
  
  const type = () => {
    if (i < fullText.length) {
      displayText.value += fullText.charAt(i)
      i++
      setTimeout(type, speed)
    }
  }
  
  type()
}

// 粒子动画
const createParticles = () => {
  if (!particlesRef.value) return
  
  for (let i = 0; i < 50; i++) {
    const particle = document.createElement('div')
    particle.className = 'particle'
    particle.style.left = Math.random() * 100 + '%'
    particle.style.top = Math.random() * 100 + '%'
    particle.style.animationDelay = Math.random() * 20 + 's'
    particle.style.animationDuration = (Math.random() * 10 + 10) + 's'
    particlesRef.value.appendChild(particle)
  }
}

// Hero区入场动画
const playHeroAnimation = () => {
  const tl = gsap.timeline()
  
  tl.from(titleRef.value, {
    y: 50,
    opacity: 0,
    duration: 1,
    ease: "power3.out"
  })
  .from(subtitleRef.value, {
    y: 30,
    opacity: 0,
    duration: 0.8,
    ease: "power3.out"
  }, "-=0.5")
  .from(buttonsRef.value, {
    y: 30,
    opacity: 0,
    duration: 0.8,
    ease: "power3.out"
  }, "-=0.3")
  .from(scrollRef.value, {
    opacity: 0,
    duration: 0.5,
    ease: "power2.out"
  }, "-=0.2")
}

onMounted(() => {
  nextTick(() => {
    createParticles()
    startTypewriter()
    setTimeout(() => {
      playHeroAnimation()
    }, 500)
  })
})
</script>

<style scoped>
.hero-section {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: float linear infinite;
}

@keyframes float {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1);
    opacity: 0;
  }
}

.hero-content {
  text-align: center;
  z-index: 10;
  max-width: 800px;
  padding: 0 20px;
}

.hero-title {
  font-size: 4.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  line-height: 1.1;
}

.gradient-text {
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.cursor {
  color: rgba(255, 255, 255, 0.8);
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 3rem;
  font-weight: 300;
  letter-spacing: 2px;
}

.hero-buttons {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
}

.scroll-indicator {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  color: rgba(255, 255, 255, 0.7);
  animation: bounce 2s infinite;
}

.scroll-line {
  width: 1px;
  height: 30px;
  background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.7));
  margin-top: 10px;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  40% {
    transform: translateX(-50%) translateY(-10px);
  }
  60% {
    transform: translateX(-50%) translateY(-5px);
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 3rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
    letter-spacing: 1px;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-content {
    padding: 0 15px;
  }
}
</style> 