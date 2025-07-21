import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import router from '@/router'

/**
 * 创建Axios实例
 * 配置基础URL、超时时间和默认头部
 */
const request = axios.create({
    baseURL: '/api', // 后端API基础路径
    timeout: 10000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    }
})

/**
 * 请求拦截器
 * 自动添加JWT Token到请求头
 */
request.interceptors.request.use(
    config => {
        const authStore = useAuthStore()
        const token = authStore.token

        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }

        // 请求开始时显示加载状态
        if (config.showLoading !== false) {
            // 可以在这里添加全局loading状态
        }

        return config
    },
    error => {
        console.error('请求配置错误:', error)
        return Promise.reject(error)
    }
)

/**
 * 响应拦截器
 * 统一处理响应结果和错误
 */
request.interceptors.response.use(
    response => {
        const { data } = response
        const { code, message } = data

        // 根据后端ApiResponse结构处理响应
        if (code === 200) {
            return data.data // 返回实际数据
        } else {
            // 业务错误处理
            ElMessage.error(message || '请求失败')
            return Promise.reject(new Error(message || '请求失败'))
        }
    },
    error => {
        const { response } = error

        if (response) {
            const { status, data } = response

            switch (status) {
                case 401:
                    // 未授权，清除token并跳转到登录页
                    const authStore = useAuthStore()
                    authStore.logout()
                    router.push('/auth/login')
                    ElMessage.error('登录已过期，请重新登录')
                    break
                case 403:
                    ElMessage.error('权限不足')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器内部错误')
                    break
                default:
                    ElMessage.error(data?.message || '请求失败')
            }
        } else {
            // 网络错误
            ElMessage.error('网络连接失败，请检查网络设置')
        }

        return Promise.reject(error)
    }
)

/**
 * 封装常用的HTTP方法
 */
export const http = {
    get: (url, params, config = {}) => {
        return request.get(url, { params, ...config })
    },

    post: (url, data, config = {}) => {
        return request.post(url, data, config)
    },

    put: (url, data, config = {}) => {
        return request.put(url, data, config)
    },

    delete: (url, config = {}) => {
        return request.delete(url, config)
    },

    upload: (url, formData, config = {}) => {
        return request.post(url, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            ...config
        })
    }
}

export default request 