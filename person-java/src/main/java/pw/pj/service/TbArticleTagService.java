package pw.pj.service;

import pw.pj.POJO.DO.TbArticleTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 文章标签关联管理服务接口
 * 针对表【tb_article_tag(文章标签关联表)】的数据库操作Service
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbArticleTagService extends IService<TbArticleTag> {

    // ==================== 文章标签关联操作 ====================

    /**
     * 为文章添加标签
     * 
     * @param articleId 文章ID
     * @param tagId     标签ID
     * @return 是否添加成功
     */
    Boolean addTagToArticle(Long articleId, Long tagId);

    /**
     * 为文章批量添加标签
     * 
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     * @return 是否添加成功
     */
    Boolean addTagsToArticle(Long articleId, List<Long> tagIds);

    /**
     * 移除文章的标签
     * 
     * @param articleId 文章ID
     * @param tagId     标签ID
     * @return 是否移除成功
     */
    Boolean removeTagFromArticle(Long articleId, Long tagId);

    /**
     * 移除文章的所有标签
     * 
     * @param articleId 文章ID
     * @return 是否移除成功
     */
    Boolean removeAllTagsFromArticle(Long articleId);

    /**
     * 更新文章的标签（先清空再添加）
     * 
     * @param articleId 文章ID
     * @param tagIds    新的标签ID列表
     * @return 是否更新成功
     */
    Boolean updateArticleTags(Long articleId, List<Long> tagIds);

    // ==================== 查询操作 ====================

    /**
     * 获取文章的所有标签ID
     * 
     * @param articleId 文章ID
     * @return 标签ID列表
     */
    List<Long> getTagIdsByArticleId(Long articleId);

    /**
     * 获取标签关联的所有文章ID
     * 
     * @param tagId 标签ID
     * @return 文章ID列表
     */
    List<Long> getArticleIdsByTagId(Long tagId);

    /**
     * 获取多个标签共同关联的文章ID
     * 
     * @param tagIds 标签ID列表
     * @return 文章ID列表
     */
    List<Long> getArticleIdsByTagIds(List<Long> tagIds);

    /**
     * 检查文章是否已经关联了指定标签
     * 
     * @param articleId 文章ID
     * @param tagId     标签ID
     * @return 是否已关联
     */
    Boolean checkArticleHasTag(Long articleId, Long tagId);

    /**
     * 获取文章的标签数量
     * 
     * @param articleId 文章ID
     * @return 标签数量
     */
    Integer getTagCountByArticleId(Long articleId);

    /**
     * 获取标签关联的文章数量
     * 
     * @param tagId 标签ID
     * @return 文章数量
     */
    Integer getArticleCountByTagId(Long tagId);

    // ==================== 批量操作 ====================

    /**
     * 批量删除文章的标签关联
     * 
     * @param articleIds 文章ID列表
     * @return 是否删除成功
     */
    Boolean deleteByArticleIds(List<Long> articleIds);

    /**
     * 批量删除标签的文章关联
     * 
     * @param tagIds 标签ID列表
     * @return 是否删除成功
     */
    Boolean deleteByTagIds(List<Long> tagIds);

    /**
     * 清理已删除文章或标签的关联数据
     * 
     * @return 清理的记录数
     */
    Integer cleanupInvalidRelations();

    // ==================== 数据迁移和同步 ====================

    /**
     * 同步文章标签统计数据
     * 重新计算所有标签的文章数量
     * 
     * @return 同步的标签数量
     */
    Integer syncTagArticleCount();

    /**
     * 修复标签关联数据
     * 移除重复的关联记录
     * 
     * @return 修复的记录数
     */
    Integer fixDuplicateRelations();

}
