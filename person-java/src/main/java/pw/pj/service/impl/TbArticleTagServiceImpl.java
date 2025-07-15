package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import pw.pj.POJO.DO.TbArticleTag;
import pw.pj.POJO.DO.TbTag;
import pw.pj.POJO.DO.TbArticle;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.RedisUtils;
import pw.pj.mapper.TbArticleTagMapper;
import pw.pj.service.TbArticleService;
import pw.pj.service.TbArticleTagService;
import pw.pj.service.TbTagService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章标签关联管理服务实现类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbArticleTagServiceImpl extends ServiceImpl<TbArticleTagMapper, TbArticleTag>
        implements TbArticleTagService {

    @Autowired
    @Lazy
    private TbArticleService articleService;

    @Autowired
    private TbTagService tagService;

    @Autowired
    private RedisUtils redisUtils;

    // ==================== 文章标签关联操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addTagToArticle(Long articleId, Long tagId) {
        log.info("为文章添加标签，文章ID：{}，标签ID：{}", articleId, tagId);

        // 1. 验证文章和标签是否存在
        validateArticleAndTag(articleId, tagId);

        // 2. 检查是否已经关联
        if (checkArticleHasTag(articleId, tagId)) {
            log.warn("文章{}已经关联了标签{}，跳过添加", articleId, tagId);
            return true;
        }

        // 3. 创建关联记录
        TbArticleTag articleTag = new TbArticleTag();
        articleTag.setArticleId(articleId);
        articleTag.setTagId(tagId);
        articleTag.setCreateTime(new Date());
        articleTag.setUpdateTime(new Date());
        articleTag.setIsDelete(0);

        boolean saved = save(articleTag);
        if (saved) {
            // 4. 更新标签的文章数量统计
            tagService.updateTagArticleCount(tagId);

            // 5. 清除相关缓存
            clearArticleTagCache(articleId);
            clearTagArticleCache(tagId);

            log.info("文章标签关联添加成功");
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addTagsToArticle(Long articleId, List<Long> tagIds) {
        if (ObjectUtils.isEmpty(tagIds)) {
            return true;
        }

        log.info("为文章批量添加标签，文章ID：{}，标签数量：{}", articleId, tagIds.size());

        // 1. 验证文章是否存在
        TbArticle article = articleService.getById(articleId);
        if (article == null || SystemConstants.Article.STATUS_DELETED.equals(article.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "文章不存在");
        }

        // 2. 验证标签是否存在
        List<TbTag> tags = tagService.lambdaQuery()
                .in(TbTag::getId, tagIds)
                .eq(TbTag::getIsDelete, 0)
                .list();

        if (tags.size() != tagIds.size()) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "部分标签不存在");
        }

        // 3. 查询已存在的关联
        List<Long> existingTagIds = getTagIdsByArticleId(articleId);
        List<Long> newTagIds = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(newTagIds)) {
            log.info("所有标签都已经关联，跳过添加");
            return true;
        }

        // 4. 批量创建关联记录
        List<TbArticleTag> articleTags = newTagIds.stream()
                .map(tagId -> {
                    TbArticleTag articleTag = new TbArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagId);
                    articleTag.setCreateTime(new Date());
                    articleTag.setUpdateTime(new Date());
                    articleTag.setIsDelete(0);
                    return articleTag;
                })
                .collect(Collectors.toList());

        boolean saved = saveBatch(articleTags);
        if (saved) {
            // 5. 批量更新标签的文章数量统计
            newTagIds.forEach(tagService::updateTagArticleCount);

            // 6. 清除相关缓存
            clearArticleTagCache(articleId);
            newTagIds.forEach(this::clearTagArticleCache);

            log.info("文章标签批量关联添加成功，新增关联数量：{}", newTagIds.size());
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeTagFromArticle(Long articleId, Long tagId) {
        log.info("移除文章标签，文章ID：{}，标签ID：{}", articleId, tagId);

        boolean removed = lambdaUpdate()
                .eq(TbArticleTag::getArticleId, articleId)
                .eq(TbArticleTag::getTagId, tagId)
                .eq(TbArticleTag::getIsDelete, 0)
                .set(TbArticleTag::getIsDelete, 1)
                .set(TbArticleTag::getUpdateTime, new Date())
                .update();

        if (removed) {
            // 更新标签的文章数量统计
            tagService.updateTagArticleCount(tagId);

            // 清除相关缓存
            clearArticleTagCache(articleId);
            clearTagArticleCache(tagId);

            log.info("文章标签关联移除成功");
        }

        return removed;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeAllTagsFromArticle(Long articleId) {
        log.info("移除文章的所有标签，文章ID：{}", articleId);

        // 获取当前关联的标签ID
        List<Long> tagIds = getTagIdsByArticleId(articleId);

        if (ObjectUtils.isEmpty(tagIds)) {
            log.info("文章没有关联任何标签，跳过移除");
            return true;
        }

        boolean removed = lambdaUpdate()
                .eq(TbArticleTag::getArticleId, articleId)
                .eq(TbArticleTag::getIsDelete, 0)
                .set(TbArticleTag::getIsDelete, 1)
                .set(TbArticleTag::getUpdateTime, new Date())
                .update();

        if (removed) {
            // 批量更新标签的文章数量统计
            tagIds.forEach(tagService::updateTagArticleCount);

            // 清除相关缓存
            clearArticleTagCache(articleId);
            tagIds.forEach(this::clearTagArticleCache);

            log.info("文章所有标签关联移除成功，数量：{}", tagIds.size());
        }

        return removed;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateArticleTags(Long articleId, List<Long> tagIds) {
        log.info("更新文章标签，文章ID：{}，新标签数量：{}", articleId,
                ObjectUtils.isEmpty(tagIds) ? 0 : tagIds.size());

        // 1. 移除现有的所有标签关联
        removeAllTagsFromArticle(articleId);

        // 2. 添加新的标签关联
        if (!ObjectUtils.isEmpty(tagIds)) {
            return addTagsToArticle(articleId, tagIds);
        }

        log.info("文章标签更新成功");
        return true;
    }

    // ==================== 查询操作 ====================

    @Override
    public List<Long> getTagIdsByArticleId(Long articleId) {
        if (articleId == null) {
            return new ArrayList<>();
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Tag.ARTICLE_TAGS + articleId;
        List<Long> cachedTagIds = redisUtils.get(cacheKey, List.class);
        if (cachedTagIds != null) {
            return cachedTagIds;
        }

        // 2. 从数据库查询
        List<TbArticleTag> articleTags = lambdaQuery()
                .eq(TbArticleTag::getArticleId, articleId)
                .eq(TbArticleTag::getIsDelete, 0)
                .list();

        List<Long> tagIds = articleTags.stream()
                .map(TbArticleTag::getTagId)
                .collect(Collectors.toList());

        // 3. 缓存结果
        redisUtils.set(cacheKey, tagIds, SystemConstants.Cache.EXPIRE_HOUR);

        return tagIds;
    }

    @Override
    public List<Long> getArticleIdsByTagId(Long tagId) {
        if (tagId == null) {
            return new ArrayList<>();
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Article.RELATED_ARTICLES + tagId;
        List<Long> cachedArticleIds = redisUtils.get(cacheKey, List.class);
        if (cachedArticleIds != null) {
            return cachedArticleIds;
        }

        // 2. 从数据库查询
        List<TbArticleTag> articleTags = lambdaQuery()
                .eq(TbArticleTag::getTagId, tagId)
                .eq(TbArticleTag::getIsDelete, 0)
                .list();

        List<Long> articleIds = articleTags.stream()
                .map(TbArticleTag::getArticleId)
                .collect(Collectors.toList());

        // 3. 缓存结果
        redisUtils.set(cacheKey, articleIds, SystemConstants.Cache.EXPIRE_HOUR);

        return articleIds;
    }

    @Override
    public List<Long> getArticleIdsByTagIds(List<Long> tagIds) {
        if (ObjectUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }

        if (tagIds.size() == 1) {
            return getArticleIdsByTagId(tagIds.get(0));
        }

        // 查询包含所有指定标签的文章
        Map<Long, Integer> articleTagCount = new HashMap<>();

        for (Long tagId : tagIds) {
            List<Long> articleIds = getArticleIdsByTagId(tagId);
            for (Long articleId : articleIds) {
                articleTagCount.put(articleId, articleTagCount.getOrDefault(articleId, 0) + 1);
            }
        }

        // 返回包含所有标签的文章ID
        return articleTagCount.entrySet().stream()
                .filter(entry -> entry.getValue() == tagIds.size())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean checkArticleHasTag(Long articleId, Long tagId) {
        if (articleId == null || tagId == null) {
            return false;
        }

        long count = lambdaQuery()
                .eq(TbArticleTag::getArticleId, articleId)
                .eq(TbArticleTag::getTagId, tagId)
                .eq(TbArticleTag::getIsDelete, 0)
                .count();

        return count > 0;
    }

    @Override
    public Integer getTagCountByArticleId(Long articleId) {
        if (articleId == null) {
            return 0;
        }

        long count = lambdaQuery()
                .eq(TbArticleTag::getArticleId, articleId)
                .eq(TbArticleTag::getIsDelete, 0)
                .count();

        return (int) count;
    }

    @Override
    public Integer getArticleCountByTagId(Long tagId) {
        if (tagId == null) {
            return 0;
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Tag.TAG_ARTICLE_COUNT + tagId;
        Integer cachedCount = redisUtils.get(cacheKey, Integer.class);
        if (cachedCount != null) {
            return cachedCount;
        }

        // 2. 从数据库查询
        long count = lambdaQuery()
                .eq(TbArticleTag::getTagId, tagId)
                .eq(TbArticleTag::getIsDelete, 0)
                .count();

        Integer articleCount = (int) count;

        // 3. 缓存结果
        redisUtils.set(cacheKey, articleCount, SystemConstants.Cache.EXPIRE_HOUR);

        return articleCount;
    }

    // ==================== 批量操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByArticleIds(List<Long> articleIds) {
        if (ObjectUtils.isEmpty(articleIds)) {
            return true;
        }

        log.info("批量删除文章的标签关联，文章数量：{}", articleIds.size());

        // 获取需要更新统计的标签ID
        Set<Long> affectedTagIds = new HashSet<>();
        for (Long articleId : articleIds) {
            List<Long> tagIds = getTagIdsByArticleId(articleId);
            affectedTagIds.addAll(tagIds);
        }

        boolean deleted = lambdaUpdate()
                .in(TbArticleTag::getArticleId, articleIds)
                .eq(TbArticleTag::getIsDelete, 0)
                .set(TbArticleTag::getIsDelete, 1)
                .set(TbArticleTag::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 批量更新标签的文章数量统计
            affectedTagIds.forEach(tagService::updateTagArticleCount);

            // 清除相关缓存
            articleIds.forEach(this::clearArticleTagCache);
            affectedTagIds.forEach(this::clearTagArticleCache);

            log.info("批量删除文章标签关联成功");
        }

        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByTagIds(List<Long> tagIds) {
        if (ObjectUtils.isEmpty(tagIds)) {
            return true;
        }

        log.info("批量删除标签的文章关联，标签数量：{}", tagIds.size());

        // 获取需要清除缓存的文章ID
        Set<Long> affectedArticleIds = new HashSet<>();
        for (Long tagId : tagIds) {
            List<Long> articleIds = getArticleIdsByTagId(tagId);
            affectedArticleIds.addAll(articleIds);
        }

        boolean deleted = lambdaUpdate()
                .in(TbArticleTag::getTagId, tagIds)
                .eq(TbArticleTag::getIsDelete, 0)
                .set(TbArticleTag::getIsDelete, 1)
                .set(TbArticleTag::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 清除相关缓存
            affectedArticleIds.forEach(this::clearArticleTagCache);
            tagIds.forEach(this::clearTagArticleCache);

            log.info("批量删除标签文章关联成功");
        }

        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanupInvalidRelations() {
        log.info("开始清理无效的文章标签关联数据");

        // 查找关联了已删除文章的记录
        List<TbArticleTag> invalidByArticle = lambdaQuery()
                .eq(TbArticleTag::getIsDelete, 0)
                .notExists("SELECT 1 FROM tb_article a WHERE a.id = tb_article_tag.article_id AND a.is_delete = 0")
                .list();

        // 查找关联了已删除标签的记录
        List<TbArticleTag> invalidByTag = lambdaQuery()
                .eq(TbArticleTag::getIsDelete, 0)
                .notExists("SELECT 1 FROM tb_tag t WHERE t.id = tb_article_tag.tag_id AND t.is_delete = 0")
                .list();

        Set<Long> invalidIds = new HashSet<>();
        invalidByArticle.forEach(relation -> invalidIds.add(relation.getId()));
        invalidByTag.forEach(relation -> invalidIds.add(relation.getId()));

        if (invalidIds.isEmpty()) {
            log.info("没有发现无效的关联数据");
            return 0;
        }

        // 批量删除无效记录
        boolean deleted = lambdaUpdate()
                .in(TbArticleTag::getId, invalidIds)
                .set(TbArticleTag::getIsDelete, 1)
                .set(TbArticleTag::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 清除相关缓存
            clearAllCache();
            log.info("清理无效关联数据完成，数量：{}", invalidIds.size());
            return invalidIds.size();
        }

        return 0;
    }

    // ==================== 数据迁移和同步 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncTagArticleCount() {
        log.info("开始同步标签文章数量统计");

        // 获取所有标签
        List<TbTag> allTags = tagService.lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .list();

        int syncCount = 0;
        for (TbTag tag : allTags) {
            Integer realCount = getArticleCountByTagId(tag.getId());
            if (!Objects.equals(tag.getArticleCount(), realCount)) {
                // 更新标签的文章数量
                tagService.lambdaUpdate()
                        .eq(TbTag::getId, tag.getId())
                        .set(TbTag::getArticleCount, realCount)
                        .set(TbTag::getUpdateTime, new Date())
                        .update();
                syncCount++;
            }
        }

        // 清除相关缓存
        clearAllCache();

        log.info("标签文章数量同步完成，更新的标签数量：{}", syncCount);
        return syncCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer fixDuplicateRelations() {
        log.info("开始修复重复的文章标签关联记录");

        // 查找重复的关联记录
        List<TbArticleTag> allRelations = lambdaQuery()
                .eq(TbArticleTag::getIsDelete, 0)
                .orderByAsc(TbArticleTag::getId)
                .list();

        Map<String, List<TbArticleTag>> groupedRelations = allRelations.stream()
                .collect(Collectors.groupingBy(relation -> relation.getArticleId() + "_" + relation.getTagId()));

        int fixedCount = 0;
        for (Map.Entry<String, List<TbArticleTag>> entry : groupedRelations.entrySet()) {
            List<TbArticleTag> relations = entry.getValue();
            if (relations.size() > 1) {
                // 保留第一个，删除其他重复的
                List<Long> duplicateIds = relations.stream()
                        .skip(1)
                        .map(TbArticleTag::getId)
                        .collect(Collectors.toList());

                lambdaUpdate()
                        .in(TbArticleTag::getId, duplicateIds)
                        .set(TbArticleTag::getIsDelete, 1)
                        .set(TbArticleTag::getUpdateTime, new Date())
                        .update();

                fixedCount += duplicateIds.size();
            }
        }

        if (fixedCount > 0) {
            // 清除相关缓存
            clearAllCache();
            log.info("修复重复关联记录完成，删除的重复记录数：{}", fixedCount);
        } else {
            log.info("没有发现重复的关联记录");
        }

        return fixedCount;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证文章和标签是否存在
     */
    private void validateArticleAndTag(Long articleId, Long tagId) {
        // 验证文章是否存在
        TbArticle article = articleService.getById(articleId);
        if (article == null || SystemConstants.Article.STATUS_DELETED.equals(article.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "文章不存在");
        }

        // 验证标签是否存在
        TbTag tag = tagService.getById(tagId);
        if (tag == null || SystemConstants.Tag.STATUS_DISABLED.equals(tag.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "标签不存在");
        }
    }

    /**
     * 清除文章标签缓存
     */
    private void clearArticleTagCache(Long articleId) {
        String cacheKey = RedisConstants.Tag.ARTICLE_TAGS + articleId;
        redisUtils.delete(cacheKey);
    }

    /**
     * 清除标签文章缓存
     */
    private void clearTagArticleCache(Long tagId) {
        String articleCacheKey = RedisConstants.Article.RELATED_ARTICLES + tagId;
        String countCacheKey = RedisConstants.Tag.TAG_ARTICLE_COUNT + tagId;
        redisUtils.delete(articleCacheKey);
        redisUtils.delete(countCacheKey);
    }

    /**
     * 清除所有相关缓存
     */
    private void clearAllCache() {
        redisUtils.deletePattern(RedisConstants.Tag.ARTICLE_TAGS + "*");
        redisUtils.deletePattern(RedisConstants.Article.RELATED_ARTICLES + "*");
        redisUtils.deletePattern(RedisConstants.Tag.TAG_ARTICLE_COUNT + "*");
    }
}
