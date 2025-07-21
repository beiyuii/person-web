import { http } from '../request'

/**
 * 标签管理相关API
 * 对应后端 /api/tags 控制器
 */
export const tagApi = {
    /**
     * 获取所有标签
     * @returns {Promise} 标签列表
     */
    getAllTags: () => {
        return http.get('/tags/all')
    },

    /**
     * 获取标签云
     * @returns {Promise} 标签云数据
     */
    getTagCloud: () => {
        return http.get('/tags/cloud')
    },

    /**
     * 获取标签列表（分页）
     * @param {Object} params - 查询参数
     * @param {number} params.pageNum - 页码
     * @param {number} params.pageSize - 每页大小
     * @param {string} params.keyword - 搜索关键词
     * @returns {Promise} 标签列表
     */
    getTagList: (params) => {
        return http.get('/tags/page', params)
    },

    /**
     * 根据ID获取标签
     * @param {number} id - 标签ID
     * @returns {Promise} 标签详情
     */
    getTagById: (id) => {
        return http.get(`/tags/${id}`)
    },

    /**
     * 创建标签
     * @param {Object} tagData - 标签数据
     * @param {string} tagData.name - 标签名称
     * @param {string} tagData.color - 标签颜色
     * @returns {Promise} 创建结果
     */
    createTag: (tagData) => {
        return http.post('/tags', tagData)
    },

    /**
     * 更新标签
     * @param {number} id - 标签ID
     * @param {Object} tagData - 标签数据
     * @returns {Promise} 更新结果
     */
    updateTag: (id, tagData) => {
        return http.put(`/tags/${id}`, tagData)
    },

    /**
     * 删除标签
     * @param {number} id - 标签ID
     * @returns {Promise} 删除结果
     */
    deleteTag: (id) => {
        return http.delete(`/tags/${id}`)
    },

    /**
     * 获取热门标签
     * @param {number} limit - 限制数量
     * @returns {Promise} 热门标签列表
     */
    getPopularTags: (limit = 20) => {
        return http.get('/tags/popular', { limit })
    },

    /**
     * 搜索标签
     * @param {string} keyword - 搜索关键词
     * @returns {Promise} 标签列表
     */
    searchTags: (keyword) => {
        return http.get('/tags/search', { keyword })
    }
} 