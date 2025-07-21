import { createPinia } from 'pinia'

// 创建pinia实例
const pinia = createPinia()

export default pinia

// 导出所有store模块
export { useAuthStore } from './modules/auth' 