package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import pw.pj.POJO.DO.TbArticle;
import pw.pj.POJO.DO.TbArticleTag;
import pw.pj.POJO.DO.TbCategory;
import pw.pj.POJO.DO.TbTag;
import pw.pj.POJO.VO.*;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.RedisUtils;
import pw.pj.common.utils.StringUtils;
import pw.pj.mapper.TbArticleMapper;
import pw.pj.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章管理服务实现类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbArticleServiceImpl extends ServiceImpl<TbArticleMapper, TbArticle> implements TbArticleService {

    @Autowired
    private TbCategoryService categoryService;

    @Autowired
    private TbTagService tagService;

    @Autowired
    private TbArticleTagService articleTagService;

    @Autowired
    private RedisUtils redisUtils;

    // ==================== 文章CRUD操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO createArticle(ArticleCreateVO articleCreateVO) {
        log.info("创建文章，标题：{}", articleCreateVO.getTitle());

        // 1. 验证分类是否存在
        if (articleCreateVO.getCategoryId() != null) {
            TbCategory category = categoryService.getById(articleCreateVO.getCategoryId());
            if (category == null || !SystemConstants.Category.STATUS_ENABLED.equals(category.getStatus())) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "分类不存在或已禁用");
            }
        }

        // 2. 创建文章对象
        TbArticle article = buildArticleFromCreate(articleCreateVO);

        // 3. 保存文章
        boolean saved = save(article);
        if (!saved) {
            throw new BusinessException(ResultEnum.SAVE_FAIL, "文章保存失败");
        }

        // 4. 处理文章标签关联
        if (!ObjectUtils.isEmpty(articleCreateVO.getTagIds())) {
            articleTagService.addTagsToArticle(article.getId(), articleCreateVO.getTagIds());
        }

        // 5. 清除相关缓存
        clearArticleCache();

        log.info("文章创建成功，文章ID：{}", article.getId());
        return convertToVO(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO updateArticle(Long articleId, ArticleUpdateVO articleUpdateVO) {
        log.info("更新文章，文章ID：{}", articleId);

        // 1. 验证文章是否存在
        TbArticle article = getById(articleId);
        if (article == null || SystemConstants.Article.STATUS_DELETED.equals(article.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "文章不存在");
        }

        // 2. 验证分类是否存在
        if (articleUpdateVO.getCategoryId() != null) {
            TbCategory category = categoryService.getById(articleUpdateVO.getCategoryId());
            if (category == null || !SystemConstants.Category.STATUS_ENABLED.equals(category.getStatus())) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "分类不存在或已禁用");
            }
        }

        // 3. 更新文章字段
        updateArticleFields(article, articleUpdateVO);

        // 4. 保存更新
        boolean updated = updateById(article);
        if (!updated) {
            throw new BusinessException(ResultEnum.UPDATE_FAIL, "文章更新失败");
        }

        // 5. 处理标签更新
        if (articleUpdateVO.getTagIds() != null) {
            // 先删除现有标签关联
            articleTagService.removeAllTagsFromArticle(articleId);
            // 再添加新的标签关联
            if (!ObjectUtils.isEmpty(articleUpdateVO.getTagIds())) {
                articleTagService.addTagsToArticle(articleId, articleUpdateVO.getTagIds());
            }
        }

        // 6. 清除相关缓存
        clearArticleCache();
        clearArticleDetailCache(articleId);

        log.info("文章更新成功，文章ID：{}", articleId);
        return convertToVO(article);
    }

    @Override
    public ArticleVO getArticleById(Long articleId) {
        if (articleId == null) {
            return null;
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Article.ARTICLE_DETAIL + articleId;
        ArticleVO cachedArticle = redisUtils.get(cacheKey, ArticleVO.class);
        if (cachedArticle != null) {
            return cachedArticle;
        }

        // 2. 从数据库查询
        TbArticle article = lambdaQuery()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .one();

        if (article == null) {
            return null;
        }

        // 3. 转换为VO
        ArticleVO articleVO = convertToVO(article);

        // 4. 缓存结果
        redisUtils.set(cacheKey, articleVO, SystemConstants.Cache.EXPIRE_HOUR);

        return articleVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteArticle(Long articleId) {
        log.info("删除文章，文章ID：{}", articleId);

        // 1. 验证文章是否存在
        TbArticle article = getById(articleId);
        if (article == null || SystemConstants.Article.STATUS_DELETED.equals(article.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "文章不存在");
        }

        // 2. 逻辑删除文章
        boolean deleted = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .set(TbArticle::getIsDelete, 1)
                .set(TbArticle::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 3. 删除文章标签关联
            articleTagService.removeAllTagsFromArticle(articleId);

            // 4. 清除相关缓存
            clearArticleCache();
            clearArticleDetailCache(articleId);

            log.info("文章删除成功，文章ID：{}", articleId);
        }

        return deleted;
    }

    // ==================== 文章查询操作 ====================

    @Override
    public PageResult<ArticleSimpleVO> getArticleList(PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .orderByDesc(TbArticle::getIsTop)
                .orderByDesc(TbArticle::getCreateTime);

        // 关键词搜索
        if (StringUtils.isNotBlank(pageQueryVO.getKeyword())) {
            queryWrapper.like(TbArticle::getTitle, pageQueryVO.getKeyword())
                    .or().like(TbArticle::getSummary, pageQueryVO.getKeyword());
        }

        Page<TbArticle> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbArticle> resultPage = page(page, queryWrapper);

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(resultPage.getRecords());
        return PageResult.of(articleVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    @Override
    public PageResult<ArticleSimpleVO> getArticlesByCategory(Long categoryId, PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbArticle::getCategoryId, categoryId)
                .eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .orderByDesc(TbArticle::getIsTop)
                .orderByDesc(TbArticle::getCreateTime);

        Page<TbArticle> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbArticle> resultPage = page(page, queryWrapper);

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(resultPage.getRecords());
        return PageResult.of(articleVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    @Override
    public PageResult<ArticleSimpleVO> getArticlesByTag(Long tagId, PageQueryVO pageQueryVO) {
        // 通过文章标签关联表查询
        List<Long> articleIds = articleTagService.getArticleIdsByTagId(tagId);
        if (ObjectUtils.isEmpty(articleIds)) {
            return PageResult.empty(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        }

        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TbArticle::getId, articleIds)
                .eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .orderByDesc(TbArticle::getIsTop)
                .orderByDesc(TbArticle::getCreateTime);

        Page<TbArticle> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbArticle> resultPage = page(page, queryWrapper);

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(resultPage.getRecords());
        return PageResult.of(articleVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    @Override
    public PageResult<ArticleSimpleVO> searchArticles(String keyword, PageQueryVO pageQueryVO) {
        if (StringUtils.isBlank(keyword)) {
            return PageResult.empty(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        }

        LambdaQueryWrapper<TbArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .and(wrapper -> wrapper.like(TbArticle::getTitle, keyword)
                        .or().like(TbArticle::getSummary, keyword)
                        .or().like(TbArticle::getContent, keyword))
                .orderByDesc(TbArticle::getViewCount)
                .orderByDesc(TbArticle::getCreateTime);

        Page<TbArticle> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbArticle> resultPage = page(page, queryWrapper);

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(resultPage.getRecords());
        return PageResult.of(articleVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    // ==================== 文章状态管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishArticle(Long articleId) {
        log.info("发布文章，文章ID：{}", articleId);

        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .set(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .set(TbArticle::getPublishedTime, new Date())
                .set(TbArticle::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearArticleCache();
            clearArticleDetailCache(articleId);
            log.info("文章发布成功，文章ID：{}", articleId);
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unpublishArticle(Long articleId) {
        log.info("取消发布文章，文章ID：{}", articleId);

        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .set(TbArticle::getStatus, SystemConstants.Article.STATUS_DRAFT)
                .set(TbArticle::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearArticleCache();
            clearArticleDetailCache(articleId);
            log.info("文章取消发布成功，文章ID：{}", articleId);
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean topArticle(Long articleId) {
        log.info("置顶文章，文章ID：{}", articleId);

        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .set(TbArticle::getIsTop, SystemConstants.Article.TOP_YES)
                .set(TbArticle::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearArticleCache();
            clearArticleDetailCache(articleId);
            log.info("文章置顶成功，文章ID：{}", articleId);
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean untopArticle(Long articleId) {
        log.info("取消置顶文章，文章ID：{}", articleId);

        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .set(TbArticle::getIsTop, SystemConstants.Article.TOP_NO)
                .set(TbArticle::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearArticleCache();
            clearArticleDetailCache(articleId);
            log.info("文章取消置顶成功，文章ID：{}", articleId);
        }

        return updated;
    }

    // ==================== 文章标签管理 ====================

    @Override
    public List<TagVO> getArticleTags(Long articleId) {
        return tagService.getTagsByArticleId(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addArticleTags(Long articleId, List<Long> tagIds) {
        return articleTagService.addTagsToArticle(articleId, tagIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            articleTagService.removeTagFromArticle(articleId, tagId);
        }
        return true;
    }

    // ==================== 文章统计和推荐 ====================

    @Override
    public List<ArticleSimpleVO> getHotArticles(Integer limit) {
        String cacheKey = RedisConstants.Article.HOT_ARTICLES + limit;
        List<ArticleSimpleVO> cachedArticles = redisUtils.get(cacheKey, List.class);
        if (cachedArticles != null) {
            return cachedArticles;
        }

        List<TbArticle> articles = lambdaQuery()
                .eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .orderByDesc(TbArticle::getViewCount)
                .orderByDesc(TbArticle::getLikeCount)
                .last("LIMIT " + limit)
                .list();

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(articles);
        redisUtils.set(cacheKey, articleVOList, SystemConstants.Cache.HOT_DATA_EXPIRE);

        return articleVOList;
    }

    @Override
    public List<ArticleSimpleVO> getRecentArticles(Integer limit) {
        String cacheKey = RedisConstants.Article.RECENT_ARTICLES + limit;
        List<ArticleSimpleVO> cachedArticles = redisUtils.get(cacheKey, List.class);
        if (cachedArticles != null) {
            return cachedArticles;
        }

        List<TbArticle> articles = lambdaQuery()
                .eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .orderByDesc(TbArticle::getPublishedTime)
                .orderByDesc(TbArticle::getCreateTime)
                .last("LIMIT " + limit)
                .list();

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(articles);
        redisUtils.set(cacheKey, articleVOList, SystemConstants.Cache.HOT_DATA_EXPIRE);

        return articleVOList;
    }

    @Override
    public List<ArticleSimpleVO> getRecommendArticles(Integer limit) {
        String cacheKey = RedisConstants.Article.RECOMMEND_ARTICLES + limit;
        List<ArticleSimpleVO> cachedArticles = redisUtils.get(cacheKey, List.class);
        if (cachedArticles != null) {
            return cachedArticles;
        }

        List<TbArticle> articles = lambdaQuery()
                .eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .eq(TbArticle::getIsRecommend, SystemConstants.Article.RECOMMEND_YES)
                .orderByDesc(TbArticle::getViewCount)
                .orderByDesc(TbArticle::getCreateTime)
                .last("LIMIT " + limit)
                .list();

        List<ArticleSimpleVO> articleVOList = convertToSimpleVOList(articles);
        redisUtils.set(cacheKey, articleVOList, SystemConstants.Cache.HOT_DATA_EXPIRE);

        return articleVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementArticleView(Long articleId) {
        // 增加浏览量（可以考虑防刷机制）
        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .setSql("view_count = view_count + 1")
                .update();

        if (updated) {
            // 清除文章详情缓存，确保获取最新浏览量
            clearArticleDetailCache(articleId);
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeArticle(Long articleId) {
        // 增加点赞数
        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .setSql("like_count = like_count + 1")
                .update();

        if (updated) {
            clearArticleDetailCache(articleId);
            clearArticleCache(); // 清除热门文章缓存
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeArticle(Long articleId) {
        // 减少点赞数
        boolean updated = lambdaUpdate()
                .eq(TbArticle::getId, articleId)
                .eq(TbArticle::getIsDelete, 0)
                .setSql("like_count = GREATEST(like_count - 1, 0)")
                .update();

        if (updated) {
            clearArticleDetailCache(articleId);
            clearArticleCache(); // 清除热门文章缓存
        }

        return updated;
    }

    // ==================== 数据转换方法 ====================

    @Override
    public ArticleVO convertToVO(TbArticle article) {
        if (article == null) {
            return null;
        }

        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);

        // 设置分类信息
        if (article.getCategoryId() != null) {
            TbCategory category = categoryService.getById(article.getCategoryId());
            if (category != null) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                articleVO.setCategory(categoryVO);
            }
        }

        // 设置标签信息
        List<TagVO> tags = getArticleTags(article.getId());
        articleVO.setTags(tags);

        return articleVO;
    }

    @Override
    public ArticleSimpleVO convertToSimpleVO(TbArticle article) {
        if (article == null) {
            return null;
        }

        ArticleSimpleVO articleSimpleVO = new ArticleSimpleVO();
        BeanUtils.copyProperties(article, articleSimpleVO);

        // 设置分类名称
        if (article.getCategoryId() != null) {
            TbCategory category = categoryService.getById(article.getCategoryId());
            if (category != null) {
                articleSimpleVO.setCategoryName(category.getName());
            }
        }

        return articleSimpleVO;
    }

    @Override
    public List<ArticleVO> convertToVOList(List<TbArticle> articles) {
        if (ObjectUtils.isEmpty(articles)) {
            return new ArrayList<>();
        }

        return articles.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleSimpleVO> convertToSimpleVOList(List<TbArticle> articles) {
        if (ObjectUtils.isEmpty(articles)) {
            return new ArrayList<>();
        }

        return articles.stream()
                .map(this::convertToSimpleVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从创建VO构建文章对象
     */
    private TbArticle buildArticleFromCreate(ArticleCreateVO createVO) {
        TbArticle article = new TbArticle();
        BeanUtils.copyProperties(createVO, article);

        // 设置默认值
        Date now = new Date();
        article.setCreateTime(now);
        article.setUpdateTime(now);
        article.setIsDelete(0);
        article.setStatus(createVO.getStatus() != null ? createVO.getStatus() : SystemConstants.Article.STATUS_DRAFT);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setIsTop(createVO.getIsTop() != null ? createVO.getIsTop() : SystemConstants.Article.TOP_NO);
        article.setIsRecommend(
                createVO.getIsRecommend() != null ? createVO.getIsRecommend() : SystemConstants.Article.RECOMMEND_NO);
        article.setAllowComment(createVO.getAllowComment() != null ? createVO.getAllowComment()
                : SystemConstants.Article.COMMENT_YES);

        if (SystemConstants.Article.STATUS_PUBLISHED.equals(article.getStatus())) {
            article.setCreateTime(now);
        }

        return article;
    }

    /**
     * 更新文章字段
     */
    private void updateArticleFields(TbArticle article, ArticleUpdateVO updateVO) {
        if (StringUtils.isNotBlank(updateVO.getTitle())) {
            article.setTitle(updateVO.getTitle());
        }
        if (StringUtils.isNotBlank(updateVO.getSummary())) {
            article.setSummary(updateVO.getSummary());
        }
        if (StringUtils.isNotBlank(updateVO.getContent())) {
            article.setContent(updateVO.getContent());
        }
        if (updateVO.getCategoryId() != null) {
            article.setCategoryId(updateVO.getCategoryId());
        }
        if (StringUtils.isNotBlank(updateVO.getCoverImage())) {
            article.setCoverImage(updateVO.getCoverImage());
        }
        if (updateVO.getStatus() != null) {
            article.setStatus(updateVO.getStatus());
        }
        if (updateVO.getIsTop() != null) {
            article.setIsTop(updateVO.getIsTop());
        }
        if (updateVO.getIsRecommend() != null) {
            article.setIsRecommend(updateVO.getIsRecommend());
        }
        if (updateVO.getAllowComment() != null) {
            article.setAllowComment(updateVO.getAllowComment());
        }

        article.setUpdateTime(new Date());
    }

    /**
     * 清除文章相关缓存
     */
    private void clearArticleCache() {
        redisUtils.deletePattern(RedisConstants.Article.HOT_ARTICLES + "*");
        redisUtils.deletePattern(RedisConstants.Article.RECENT_ARTICLES + "*");
        redisUtils.deletePattern(RedisConstants.Article.RECOMMEND_ARTICLES + "*");
    }

    /**
     * 清除文章详情缓存
     */
    private void clearArticleDetailCache(Long articleId) {
        String cacheKey = RedisConstants.Article.ARTICLE_DETAIL + articleId;
        redisUtils.delete(cacheKey);
    }
}
