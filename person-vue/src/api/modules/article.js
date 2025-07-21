import { http } from '../request'

/**
 * 文章管理相关API
 * 对应后端 /api/articles 控制器
 */
export const articleApi = {
    /**
     * 获取文章列表（分页）
     * @param {Object} params - 查询参数
     * @param {number} params.pageNum - 页码
     * @param {number} params.pageSize - 每页大小
     * @param {string} params.keyword - 搜索关键词
     * @param {number} params.categoryId - 分类ID
     * @param {string} params.status - 文章状态
     * @returns {Promise} 文章列表
     */
    getArticleList: (params) => {
        return http.get('/articles/page', params)
    },

    /**
     * 获取文章详情
     * @param {number} id - 文章ID
     * @returns {Promise} 文章详情
     */
    getArticleById: (id) => {
        return http.get(`/articles/${id}`)
    },

    /**
     * 创建文章
     * @param {Object} articleData - 文章数据
     * @param {string} articleData.title - 标题
     * @param {string} articleData.content - 内容
     * @param {string} articleData.summary - 摘要
     * @param {number} articleData.categoryId - 分类ID
     * @param {Array} articleData.tagIds - 标签ID数组
     * @returns {Promise} 创建结果
     */
    createArticle: (articleData) => {
        return http.post('/articles', articleData)
    },

    /**
     * 更新文章
     * @param {number} id - 文章ID
     * @param {Object} articleData - 文章数据
     * @returns {Promise} 更新结果
     */
    updateArticle: (id, articleData) => {
        return http.put(`/articles/${id}`, articleData)
    },

    /**
     * 删除文章
     * @param {number} id - 文章ID
     * @returns {Promise} 删除结果
     */
    deleteArticle: (id) => {
        return http.delete(`/articles/${id}`)
    },

    /**
     * 发布文章
     * @param {number} id - 文章ID
     * @returns {Promise} 发布结果
     */
    publishArticle: (id) => {
        return http.put(`/articles/${id}/publish`)
    },

    /**
     * 取消发布文章
     * @param {number} id - 文章ID
     * @returns {Promise} 取消发布结果
     */
    unpublishArticle: (id) => {
        return http.put(`/articles/${id}/unpublish`)
    },

    /**
     * 点赞文章
     * @param {number} id - 文章ID
     * @returns {Promise} 点赞结果
     */
    likeArticle: (id) => {
        return http.post(`/articles/${id}/like`)
    },

    /**
     * 取消点赞文章
     * @param {number} id - 文章ID
     * @returns {Promise} 取消点赞结果
     */
    unlikeArticle: (id) => {
        return http.delete(`/articles/${id}/like`)
    },

    /**
     * 收藏文章
     * @param {number} id - 文章ID
     * @returns {Promise} 收藏结果
     */
    favoriteArticle: (id) => {
        return http.post(`/articles/${id}/favorite`)
    },

    /**
     * 取消收藏文章
     * @param {number} id - 文章ID
     * @returns {Promise} 取消收藏结果
     */
    unfavoriteArticle: (id) => {
        return http.delete(`/articles/${id}/favorite`)
    },

    /**
     * 获取热门文章
     * @param {number} limit - 限制数量
     * @returns {Promise} 热门文章列表
     */
    getPopularArticles: (limit = 10) => {
        return http.get('/articles/popular', { limit })
    },

    /**
     * 获取推荐文章
     * @param {number} articleId - 当前文章ID
     * @param {number} limit - 限制数量
     * @returns {Promise} 推荐文章列表
     */
    getRecommendedArticles: (articleId, limit = 5) => {
        return http.get('/articles/recommended', { articleId, limit })
    },

    /**
     * 搜索文章
     * @param {Object} params - 搜索参数
     * @param {string} params.keyword - 搜索关键词
     * @param {number} params.pageNum - 页码
     * @param {number} params.pageSize - 每页大小
     * @returns {Promise} 搜索结果
     */
    searchArticles: (params) => {
        return http.get('/articles/search', params)
    }
} 