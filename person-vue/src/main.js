import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/assets/styles/apple-ui.scss'
import { useAuthStore } from '@/stores/modules/auth'

const app = createApp(App)
const pinia = createPinia()

// 注册插件
app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 初始化认证状态
const authStore = useAuthStore()
authStore.initializeAuth()

app.mount('#app')