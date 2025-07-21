<template>
  <div id="app">
    <!-- 路由视图 -->
    <router-view v-slot="{ Component, route }">
      <transition
        :name="route.meta.transition || 'fade'"
        mode="out-in"
        appear
      >
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/modules/auth'

const authStore = useAuthStore()

// 应用初始化
onMounted(() => {
  // 初始化认证状态
  authStore.initializeAuth()
})
</script>

<style lang="scss">
// 导入苹果风格样式
@use '@/assets/styles/apple-ui.scss';

#app {
  min-height: 100vh;
  width: 100%;
}

// 路由过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--apple-transition-normal);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-active,
.slide-leave-active {
  transition: transform var(--apple-transition-normal);
}

.slide-enter-from {
  transform: translateX(100%);
}

.slide-leave-to {
  transform: translateX(-100%);
}
</style>