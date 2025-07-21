import { http } from '../request'

/**
 * 用户认证相关API
 * 对应后端 /api/auth 控制器
 */
export const authApi = {
    /**
     * 用户登录
     * @param {Object} loginData - 登录数据
     * @param {string} loginData.username - 用户名
     * @param {string} loginData.password - 密码
     * @param {string} loginData.captcha - 验证码
     * @param {string} loginData.captchaUuid - 验证码UUID
     * @param {boolean} loginData.rememberMe - 记住我
     * @returns {Promise} 登录结果
     */
    login: (loginData) => {
        return http.post('/auth/login', {
            username: loginData.username,
            password: loginData.password,
            captcha: loginData.captcha,
            captchaUuid: loginData.captchaUuid,
            rememberMe: loginData.rememberMe || false
        })
    },

    /**
     * 用户注册
     * @param {Object} registerData - 注册数据
     * @param {string} registerData.username - 用户名
     * @param {string} registerData.password - 密码
     * @param {string} registerData.email - 邮箱
     * @param {string} registerData.nickname - 昵称
     * @param {string} registerData.captcha - 验证码
     * @param {string} registerData.captchaUuid - 验证码UUID
     * @returns {Promise} 注册结果
     */
    register: (registerData) => {
        return http.post('/auth/register', registerData)
    },

    /**
     * 用户登出
     * @returns {Promise} 登出结果
     */
    logout: () => {
        return http.post('/auth/logout')
    },

    /**
     * 获取当前用户信息
     * @returns {Promise} 用户信息
     */
    getCurrentUser: () => {
        return http.get('/auth/me')
    },

    /**
     * 刷新Token
     * @param {string} refreshToken - 刷新令牌
     * @returns {Promise} 新的访问令牌
     */
    refreshToken: (refreshToken) => {
        return http.post('/auth/refresh', { refreshToken })
    },

    /**
     * 获取验证码
     * @returns {Promise} 验证码图片和UUID
     */
    getCaptcha: () => {
        return http.get('/auth/captcha')
    },

    /**
     * 检查用户名是否可用
     * @param {string} username - 用户名
     * @returns {Promise} 检查结果
     */
    checkUsername: (username) => {
        return http.get('/auth/check-username', { username })
    },

    /**
     * 检查邮箱是否可用
     * @param {string} email - 邮箱
     * @returns {Promise} 检查结果
     */
    checkEmail: (email) => {
        return http.get('/auth/check-email', { email })
    },

    /**
     * 验证Token有效性
     * @returns {Promise} 验证结果
     */
    validateToken: () => {
        return http.get('/auth/validate')
    },

    /**
     * 发送重置密码邮件
     * @param {string} email - 邮箱地址
     * @returns {Promise} 发送结果
     */
    sendResetPasswordEmail: (email) => {
        return http.post('/auth/forgot-password', { email })
    },

    /**
     * 重置密码
     * @param {Object} resetData - 重置密码数据
     * @param {string} resetData.token - 重置令牌
     * @param {string} resetData.password - 新密码
     * @returns {Promise} 重置结果
     */
    resetPassword: (resetData) => {
        return http.post('/auth/reset-password', resetData)
    },

    /**
     * 修改密码
     * @param {Object} changeData - 修改密码数据
     * @param {string} changeData.oldPassword - 旧密码
     * @param {string} changeData.newPassword - 新密码
     * @returns {Promise} 修改结果
     */
    changePassword: (changeData) => {
        return http.put('/auth/change-password', changeData)
    },

    /**
     * 发送邮箱验证码
     * @param {string} email - 邮箱地址
     * @returns {Promise} 发送结果
     */
    sendEmailVerification: (email) => {
        return http.post('/auth/send-verification', { email })
    },

    /**
     * 验证邮箱验证码
     * @param {Object} verifyData - 验证数据
     * @param {string} verifyData.email - 邮箱
     * @param {string} verifyData.code - 验证码
     * @returns {Promise} 验证结果
     */
    verifyEmail: (verifyData) => {
        return http.post('/auth/verify-email', verifyData)
    }
} 