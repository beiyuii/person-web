package pw.pj.service;

import pw.pj.POJO.DO.TbArticle;
import pw.pj.POJO.VO.*;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 文章管理服务接口
 * 提供文章的完整业务功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbArticleService extends IService<TbArticle> {

    // ==================== 文章CRUD操作 ====================

    /**
     * 创建文章
     * 
     * @param articleCreateVO 文章创建请求
     * @return 文章详情
     */
    ArticleVO createArticle(ArticleCreateVO articleCreateVO);

    /**
     * 更新文章
     * 
     * @param articleId       文章ID
     * @param articleUpdateVO 文章更新请求
     * @return 文章详情
     */
    ArticleVO updateArticle(Long articleId, ArticleUpdateVO articleUpdateVO);

    /**
     * 根据ID获取文章详情
     * 
     * @param articleId 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Long articleId);

    /**
     * 删除文章（逻辑删除）
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean deleteArticle(Long articleId);

    // ==================== 文章查询操作 ====================

    /**
     * 分页查询文章列表
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<ArticleSimpleVO> getArticleList(PageQueryVO pageQueryVO);

    /**
     * 根据分类ID查询文章列表
     * 
     * @param categoryId  分类ID
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<ArticleSimpleVO> getArticlesByCategory(Long categoryId, PageQueryVO pageQueryVO);

    /**
     * 根据标签ID查询文章列表
     * 
     * @param tagId       标签ID
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<ArticleSimpleVO> getArticlesByTag(Long tagId, PageQueryVO pageQueryVO);

    /**
     * 搜索文章
     * 
     * @param keyword     搜索关键词
     * @param pageQueryVO 分页查询参数
     * @return 搜索结果
     */
    PageResult<ArticleSimpleVO> searchArticles(String keyword, PageQueryVO pageQueryVO);

    // ==================== 文章状态管理 ====================

    /**
     * 发布文章
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean publishArticle(Long articleId);

    /**
     * 取消发布文章
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean unpublishArticle(Long articleId);

    /**
     * 置顶文章
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean topArticle(Long articleId);

    /**
     * 取消置顶文章
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean untopArticle(Long articleId);

    // ==================== 文章标签管理 ====================

    /**
     * 获取文章的标签列表
     * 
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<TagVO> getArticleTags(Long articleId);

    /**
     * 为文章添加标签
     * 
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     * @return 是否成功
     */
    Boolean addArticleTags(Long articleId, List<Long> tagIds);

    /**
     * 移除文章标签
     * 
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     * @return 是否成功
     */
    Boolean removeArticleTags(Long articleId, List<Long> tagIds);

    // ==================== 文章统计和推荐 ====================

    /**
     * 获取热门文章列表
     * 
     * @param limit 数量限制
     * @return 热门文章列表
     */
    List<ArticleSimpleVO> getHotArticles(Integer limit);

    /**
     * 获取最新文章列表
     * 
     * @param limit 数量限制
     * @return 最新文章列表
     */
    List<ArticleSimpleVO> getRecentArticles(Integer limit);

    /**
     * 获取推荐文章列表
     * 
     * @param limit 数量限制
     * @return 推荐文章列表
     */
    List<ArticleSimpleVO> getRecommendArticles(Integer limit);

    /**
     * 增加文章浏览量
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean incrementArticleView(Long articleId);

    /**
     * 点赞文章
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean likeArticle(Long articleId);

    /**
     * 取消点赞文章
     * 
     * @param articleId 文章ID
     * @return 是否成功
     */
    Boolean unlikeArticle(Long articleId);

    // ==================== 数据转换方法 ====================

    /**
     * DO转VO
     * 
     * @param article 文章实体
     * @return 文章VO
     */
    ArticleVO convertToVO(TbArticle article);

    /**
     * DO转简单VO
     * 
     * @param article 文章实体
     * @return 文章简单VO
     */
    ArticleSimpleVO convertToSimpleVO(TbArticle article);

    /**
     * DO列表转VO列表
     * 
     * @param articles 文章实体列表
     * @return 文章VO列表
     */
    List<ArticleVO> convertToVOList(List<TbArticle> articles);

    /**
     * DO列表转简单VO列表
     * 
     * @param articles 文章实体列表
     * @return 文章简单VO列表
     */
    List<ArticleSimpleVO> convertToSimpleVOList(List<TbArticle> articles);
}
