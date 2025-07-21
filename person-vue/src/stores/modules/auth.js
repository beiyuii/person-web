import { defineStore } from 'pinia'
import { authApi } from '@/api'
import { ElMessage } from 'element-plus'

/**
 * 用户认证状态管理
 * 处理登录、登出、用户信息等状态
 */
export const useAuthStore = defineStore('auth', {
    state: () => ({
        // 用户信息
        user: null,

        // 访问令牌
        token: localStorage.getItem('token') || null,

        // 刷新令牌
        refreshToken: localStorage.getItem('refreshToken') || null,

        // 登录状态
        isLoggedIn: false,

        // 权限列表
        permissions: [],

        // 用户角色
        roles: []
    }),

    getters: {
        /**
         * 获取用户昵称
         */
        nickname: (state) => state.user?.nickname || state.user?.username || '游客',

        /**
         * 获取用户头像
         */
        avatar: (state) => state.user?.avatar || '/default-avatar.png',

        /**
         * 检查是否已登录
         */
        isAuthenticated: (state) => !!state.token && !!state.user,

        /**
         * 检查是否有指定权限
         */
        hasPermission: (state) => (permission) => {
            return state.permissions.includes(permission)
        },

        /**
         * 检查是否有指定角色
         */
        hasRole: (state) => (role) => {
            return state.roles.includes(role)
        },

        /**
         * 获取认证头信息
         */
        authHeaders: (state) => {
            return state.token ? { Authorization: `Bearer ${state.token}` } : {}
        }
    },

    actions: {
        /**
         * 用户登录
         * @param {Object} loginData - 登录表单数据
         */
        async login(loginData) {
            try {
                const response = await authApi.login(loginData)

                if (response.code === 200) {
                    const { user, token, refreshToken } = response.data

                    // 更新状态
                    this.user = user
                    this.token = token
                    this.refreshToken = refreshToken
                    this.permissions = user.permissions || []
                    this.roles = user.roles || []
                    this.isLoggedIn = true

                    // 保存到本地存储
                    this.saveTokens()

                    return response
                } else {
                    throw new Error(response.message || '登录失败')
                }
            } catch (error) {
                this.clearAuth()
                throw error
            }
        },

        /**
         * 用户注册
         * @param {Object} registerData - 注册表单数据
         */
        async register(registerData) {
            try {
                const response = await authApi.register(registerData)

                if (response.code === 200) {
                    return response
                } else {
                    throw new Error(response.message || '注册失败')
                }
            } catch (error) {
                throw error
            }
        },

        /**
         * 用户登出
         */
        async logout() {
            try {
                // 调用后端登出接口
                if (this.token) {
                    await authApi.logout()
                }
            } catch (error) {
                console.warn('登出接口调用失败:', error)
            } finally {
                // 清除本地状态
                this.clearAuth()
            }
        },

        /**
         * 刷新访问令牌
         */
        async refreshAccessToken() {
            try {
                if (!this.refreshToken) {
                    throw new Error('无刷新令牌')
                }

                const response = await authApi.refreshToken(this.refreshToken)

                if (response.code === 200) {
                    const { token, refreshToken } = response.data

                    // 更新令牌
                    this.token = token
                    if (refreshToken) {
                        this.refreshToken = refreshToken
                    }

                    // 保存到本地存储
                    this.saveTokens()

                    return token
                } else {
                    throw new Error(response.message || '刷新令牌失败')
                }
            } catch (error) {
                // 刷新失败，清除认证状态
                this.clearAuth()
                throw error
            }
        },

        /**
         * 验证Token有效性并获取用户信息
         */
        async validateToken() {
            try {
                if (!this.token) {
                    return false
                }

                const response = await authApi.getCurrentUser()

                if (response.code === 200) {
                    const user = response.data

                    // 更新用户信息
                    this.user = user
                    this.permissions = user.permissions || []
                    this.roles = user.roles || []
                    this.isLoggedIn = true

                    return true
                } else {
                    throw new Error(response.message || 'Token验证失败')
                }
            } catch (error) {
                this.clearAuth()
                return false
            }
        },

        /**
         * 更新用户信息
         * @param {Object} userInfo - 用户信息
         */
        updateUserInfo(userInfo) {
            this.user = { ...this.user, ...userInfo }
            this.saveTokens()
        },

        /**
         * 修改密码
         * @param {Object} passwordData - 密码数据
         */
        async changePassword(passwordData) {
            try {
                const response = await authApi.changePassword(passwordData)

                if (response.code === 200) {
                    ElMessage.success('密码修改成功')
                    return response
                } else {
                    throw new Error(response.message || '密码修改失败')
                }
            } catch (error) {
                throw error
            }
        },

        /**
         * 保存令牌到本地存储
         */
        saveTokens() {
            if (this.token) {
                localStorage.setItem('token', this.token)
            }
            if (this.refreshToken) {
                localStorage.setItem('refreshToken', this.refreshToken)
            }
            if (this.user) {
                localStorage.setItem('user', JSON.stringify(this.user))
            }
        },

        /**
         * 清除认证状态
         */
        clearAuth() {
            this.user = null
            this.token = null
            this.refreshToken = null
            this.isLoggedIn = false
            this.permissions = []
            this.roles = []

            // 清除本地存储
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('user')
        },

        /**
         * 初始化认证状态
         * 从本地存储恢复状态
         */
        initializeAuth() {
            const token = localStorage.getItem('token')
            const refreshToken = localStorage.getItem('refreshToken')
            const user = localStorage.getItem('user')

            if (token && user) {
                this.token = token
                this.refreshToken = refreshToken
                this.user = JSON.parse(user)
                this.isLoggedIn = true

                // 验证token有效性
                this.validateToken().catch(() => {
                    this.clearAuth()
                })
            }
        }
    }
}) 