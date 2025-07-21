import { http } from '../request'

/**
 * 分类管理相关API
 * 对应后端 /api/categories 控制器
 */
export const categoryApi = {
    /**
     * 获取所有分类
     * @returns {Promise} 分类列表
     */
    getAllCategories: () => {
        return http.get('/categories/all')
    },

    /**
     * 获取分类列表（分页）
     * @param {Object} params - 查询参数
     * @param {number} params.pageNum - 页码
     * @param {number} params.pageSize - 每页大小
     * @param {string} params.keyword - 搜索关键词
     * @returns {Promise} 分类列表
     */
    getCategoryList: (params) => {
        return http.get('/categories/page', params)
    },

    /**
     * 根据ID获取分类
     * @param {number} id - 分类ID
     * @returns {Promise} 分类详情
     */
    getCategoryById: (id) => {
        return http.get(`/categories/${id}`)
    },

    /**
     * 创建分类
     * @param {Object} categoryData - 分类数据
     * @param {string} categoryData.name - 分类名称
     * @param {string} categoryData.description - 分类描述
     * @param {string} categoryData.color - 分类颜色
     * @returns {Promise} 创建结果
     */
    createCategory: (categoryData) => {
        return http.post('/categories', categoryData)
    },

    /**
     * 更新分类
     * @param {number} id - 分类ID
     * @param {Object} categoryData - 分类数据
     * @returns {Promise} 更新结果
     */
    updateCategory: (id, categoryData) => {
        return http.put(`/categories/${id}`, categoryData)
    },

    /**
     * 删除分类
     * @param {number} id - 分类ID
     * @returns {Promise} 删除结果
     */
    deleteCategory: (id) => {
        return http.delete(`/categories/${id}`)
    },

    /**
     * 获取分类统计信息
     * @returns {Promise} 分类统计
     */
    getCategoryStats: () => {
        return http.get('/categories/stats')
    }
} 