import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'

/**
 * 认证相关的组合式函数
 * 提供登录、登出、权限检查等功能
 */
export function useAuth() {
    const router = useRouter()
    const authStore = useAuthStore()

    // 响应式的认证状态
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const user = computed(() => authStore.user)
    const token = computed(() => authStore.token)
    const permissions = computed(() => authStore.permissions)
    const roles = computed(() => authStore.roles)

    /**
     * 用户登录
     * @param {Object} loginData - 登录数据
     */
    const login = async (loginData) => {
        try {
            await authStore.login(loginData)

            // 登录成功后的重定向逻辑
            const redirect = router.currentRoute.value.query.redirect || '/'
            router.push(redirect)
        } catch (error) {
            throw error
        }
    }

    /**
     * 用户登出
     */
    const logout = async () => {
        try {
            await authStore.logout()

            // 登出后跳转到首页
            router.push('/')
        } catch (error) {
            console.error('登出失败:', error)
        }
    }

    /**
     * 用户注册
     * @param {Object} registerData - 注册数据
     */
    const register = async (registerData) => {
        try {
            await authStore.register(registerData)

            // 注册成功后跳转到登录页
            router.push('/auth/login')
        } catch (error) {
            throw error
        }
    }

    /**
     * 检查是否有指定权限
     * @param {string|Array} permission - 权限或权限数组
     * @returns {boolean} 是否有权限
     */
    const hasPermission = (permission) => {
        if (!isAuthenticated.value) return false

        if (Array.isArray(permission)) {
            return permission.some(p => authStore.hasPermission(p))
        }

        return authStore.hasPermission(permission)
    }

    /**
     * 检查是否有指定角色
     * @param {string|Array} role - 角色或角色数组
     * @returns {boolean} 是否有角色
     */
    const hasRole = (role) => {
        if (!isAuthenticated.value) return false

        if (Array.isArray(role)) {
            return role.some(r => authStore.hasRole(r))
        }

        return authStore.hasRole(role)
    }

    /**
     * 检查是否为管理员
     * @returns {boolean} 是否为管理员
     */
    const isAdmin = computed(() => hasRole('admin'))

    /**
     * 获取用户头像URL
     * @returns {string} 头像URL
     */
    const getUserAvatar = computed(() => {
        return user.value?.avatar || '/default-avatar.png'
    })

    /**
     * 获取用户昵称
     * @returns {string} 用户昵称
     */
    const getUserNickname = computed(() => {
        return user.value?.nickname || user.value?.username || '游客'
    })

    /**
     * 刷新用户信息
     */
    const refreshUserInfo = async () => {
        try {
            await authStore.validateToken()
        } catch (error) {
            console.error('刷新用户信息失败:', error)
        }
    }

    /**
     * 修改密码
     * @param {Object} passwordData - 密码数据
     */
    const changePassword = async (passwordData) => {
        try {
            await authStore.changePassword(passwordData)
        } catch (error) {
            throw error
        }
    }

    /**
     * 更新用户信息
     * @param {Object} userInfo - 用户信息
     */
    const updateUserInfo = (userInfo) => {
        authStore.updateUserInfo(userInfo)
    }

    /**
     * 检查路由权限
     * @param {Object} route - 路由对象
     * @returns {boolean} 是否有访问权限
     */
    const canAccessRoute = (route) => {
        const { meta } = route

        // 不需要认证的路由
        if (!meta.requiresAuth) {
            return true
        }

        // 需要认证但未登录
        if (!isAuthenticated.value) {
            return false
        }

        // 检查角色权限
        if (meta.roles && meta.roles.length > 0) {
            const hasRequiredRole = meta.roles.some(role => hasRole(role))
            if (!hasRequiredRole) {
                return false
            }
        }

        // 检查操作权限
        if (meta.permissions && meta.permissions.length > 0) {
            const hasRequiredPermission = meta.permissions.some(permission =>
                hasPermission(permission)
            )
            if (!hasRequiredPermission) {
                return false
            }
        }

        return true
    }

    /**
     * 初始化认证状态
     */
    const initializeAuth = () => {
        authStore.initializeAuth()
    }

    return {
        // 状态
        isAuthenticated,
        user,
        token,
        permissions,
        roles,
        isAdmin,
        getUserAvatar,
        getUserNickname,

        // 方法
        login,
        logout,
        register,
        hasPermission,
        hasRole,
        refreshUserInfo,
        changePassword,
        updateUserInfo,
        canAccessRoute,
        initializeAuth
    }
} 