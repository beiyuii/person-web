import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'
import { ElMessage } from 'element-plus'

/**
 * 路由配置
 * 采用懒加载方式优化性能
 */
const routes = [
    // 首页
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: {
            title: '首页',
            requiresAuth: false
        }
    },

    // 认证相关路由
    {
        path: '/auth',
        name: 'Auth',
        component: () => import('@/components/layout/AuthLayout.vue'),
        redirect: '/auth/login',
        meta: {
            title: '用户认证',
            requiresAuth: false,
            hideForAuth: true // 已登录用户隐藏
        },
        children: [
            {
                path: 'login',
                name: 'Login',
                component: () => import('@/views/auth/Login.vue'),
                meta: {
                    title: '用户登录',
                    requiresAuth: false
                }
            },
            {
                path: 'register',
                name: 'Register',
                component: () => import('@/views/auth/Register.vue'),
                meta: {
                    title: '用户注册',
                    requiresAuth: false
                }
            },
            {
                path: 'forgot-password',
                name: 'ForgotPassword',
                component: () => import('@/views/auth/ForgotPassword.vue'),
                meta: {
                    title: '忘记密码',
                    requiresAuth: false
                }
            }
        ]
    },

    // 博客相关路由
    {
        path: '/blog',
        name: 'Blog',
        component: () => import('@/components/layout/MainLayout.vue'),
        children: [
            {
                path: '',
                name: 'BlogList',
                component: () => import('@/views/blog/BlogList.vue'),
                meta: {
                    title: '博客列表',
                    requiresAuth: false
                }
            },
            {
                path: ':id',
                name: 'BlogDetail',
                component: () => import('@/views/blog/BlogDetail.vue'),
                meta: {
                    title: '博客详情',
                    requiresAuth: false
                }
            },
            {
                path: 'create',
                name: 'BlogCreate',
                component: () => import('@/views/blog/BlogCreate.vue'),
                meta: {
                    title: '创建博客',
                    requiresAuth: true,
                    permissions: ['blog:create']
                }
            },
            {
                path: ':id/edit',
                name: 'BlogEdit',
                component: () => import('@/views/blog/BlogEdit.vue'),
                meta: {
                    title: '编辑博客',
                    requiresAuth: true,
                    permissions: ['blog:edit']
                }
            }
        ]
    },

    // 用户中心路由
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/components/layout/MainLayout.vue'),
        meta: {
            title: '个人中心',
            requiresAuth: true
        },
        children: [
            {
                path: '',
                name: 'UserProfile',
                component: () => import('@/views/profile/UserProfile.vue'),
                meta: {
                    title: '个人资料',
                    requiresAuth: true
                }
            },
            {
                path: 'settings',
                name: 'UserSettings',
                component: () => import('@/views/profile/UserSettings.vue'),
                meta: {
                    title: '账户设置',
                    requiresAuth: true
                }
            }
        ]
    },

    // 管理后台路由
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/components/layout/AdminLayout.vue'),
        meta: {
            title: '管理后台',
            requiresAuth: true,
            roles: ['admin']
        },
        children: [
            {
                path: '',
                name: 'Dashboard',
                component: () => import('@/views/admin/Dashboard.vue'),
                meta: {
                    title: '控制台',
                    requiresAuth: true,
                    roles: ['admin']
                }
            },
            {
                path: 'articles',
                name: 'ArticleManage',
                component: () => import('@/views/admin/ArticleManage.vue'),
                meta: {
                    title: '文章管理',
                    requiresAuth: true,
                    permissions: ['article:manage']
                }
            },
            {
                path: 'comments',
                name: 'CommentManage',
                component: () => import('@/views/admin/CommentManage.vue'),
                meta: {
                    title: '评论管理',
                    requiresAuth: true,
                    permissions: ['comment:manage']
                }
            },
            {
                path: 'files',
                name: 'FileManage',
                component: () => import('@/views/admin/FileManage.vue'),
                meta: {
                    title: '文件管理',
                    requiresAuth: true,
                    permissions: ['file:manage']
                }
            },
            {
                path: 'system',
                name: 'SystemConfig',
                component: () => import('@/views/admin/SystemConfig.vue'),
                meta: {
                    title: '系统配置',
                    requiresAuth: true,
                    roles: ['admin']
                }
            }
        ]
    },

    // 关于页面
    {
        path: '/about',
        name: 'About',
        component: () => import('@/views/About.vue'),
        meta: {
            title: '关于我们',
            requiresAuth: false
        }
    },

    // 404 页面
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/error/NotFound.vue'),
        meta: {
            title: '页面未找到',
            requiresAuth: false
        }
    }
]

/**
 * 创建路由器实例
 */
const router = createRouter({
    history: createWebHistory(),
    routes,
    // 滚动行为
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return { top: 0 }
        }
    }
})

/**
 * 全局前置守卫
 * 处理权限验证和页面标题
 */
router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore()

    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - PersonWeb`
    }

    // 检查是否需要认证
    if (to.meta.requiresAuth) {
        // 未登录，跳转到登录页
        if (!authStore.isAuthenticated) {
            ElMessage.warning('请先登录')
            next({
                name: 'Login',
                query: { redirect: to.fullPath }
            })
            return
        }

        // 检查角色权限
        if (to.meta.roles && to.meta.roles.length > 0) {
            const hasRole = to.meta.roles.some(role => authStore.hasRole(role))
            if (!hasRole) {
                ElMessage.error('权限不足')
                next({ name: 'Home' })
                return
            }
        }

        // 检查操作权限
        if (to.meta.permissions && to.meta.permissions.length > 0) {
            const hasPermission = to.meta.permissions.some(permission =>
                authStore.hasPermission(permission)
            )
            if (!hasPermission) {
                ElMessage.error('权限不足')
                next({ name: 'Home' })
                return
            }
        }
    }

    // 已登录用户访问认证页面，重定向到首页
    if (to.meta.hideForAuth && authStore.isAuthenticated) {
        next({ name: 'Home' })
        return
    }

    next()
})

/**
 * 全局后置守卫
 * 处理加载状态等
 */
router.afterEach((to, from) => {
    // 可以在这里处理页面加载完成后的逻辑
    // 比如关闭全局loading状态
})

export default router 