package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import pw.pj.POJO.DO.TbArticleTag;
import pw.pj.POJO.DO.TbTag;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.TagVO;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.RedisUtils;
import pw.pj.common.utils.StringUtils;
import pw.pj.mapper.TbTagMapper;
import pw.pj.service.TbArticleTagService;
import pw.pj.service.TbTagService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签管理服务实现类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbTagServiceImpl extends ServiceImpl<TbTagMapper, TbTag> implements TbTagService {

    @Autowired
    @Lazy
    private TbArticleTagService articleTagService;

    @Autowired
    private RedisUtils redisUtils;

    // ==================== 标签CRUD操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVO createTag(TagVO tagVO) {
        log.info("创建标签，标签名称：{}", tagVO.getName());

        // 1. 验证标签名称唯一性
        if (checkTagName(tagVO.getName(), null)) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "标签名称已存在");
        }

        // 2. 验证标签别名唯一性（如果提供了的话）
        if (StringUtils.isNotBlank(tagVO.getName())) {
            String slug = generateSlug(tagVO.getName());
            if (checkTagSlug(slug, null)) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "标签别名已存在");
            }
        }

        // 3. 创建标签对象
        TbTag tag = buildTagFromVO(tagVO);

        // 4. 保存标签
        boolean saved = save(tag);
        if (!saved) {
            throw new BusinessException(ResultEnum.SAVE_FAIL, "标签保存失败");
        }

        // 5. 清除相关缓存
        clearTagCache();

        log.info("标签创建成功，标签ID：{}", tag.getId());
        return convertToVO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVO updateTag(Long tagId, TagVO tagVO) {
        log.info("更新标签，标签ID：{}", tagId);

        // 1. 验证标签是否存在
        TbTag tag = getById(tagId);
        if (tag == null || SystemConstants.Tag.STATUS_DISABLED.equals(tag.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "标签不存在");
        }

        // 2. 验证标签名称唯一性（排除自己）
        if (StringUtils.isNotBlank(tagVO.getName()) && !tag.getName().equals(tagVO.getName())) {
            if (checkTagName(tagVO.getName(), tagId)) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "标签名称已存在");
            }
        }

        // 3. 更新标签字段
        updateTagFields(tag, tagVO);

        // 4. 保存更新
        boolean updated = updateById(tag);
        if (!updated) {
            throw new BusinessException(ResultEnum.UPDATE_FAIL, "标签更新失败");
        }

        // 5. 清除相关缓存
        clearTagCache();
        clearTagDetailCache(tagId);

        log.info("标签更新成功，标签ID：{}", tagId);
        return convertToVO(tag);
    }

    @Override
    public TagVO getTagById(Long tagId) {
        if (tagId == null) {
            return null;
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Tag.TAG_DETAIL + tagId;
        TagVO cachedTag = redisUtils.get(cacheKey, TagVO.class);
        if (cachedTag != null) {
            return cachedTag;
        }

        // 2. 从数据库查询
        TbTag tag = lambdaQuery()
                .eq(TbTag::getId, tagId)
                .eq(TbTag::getIsDelete, 0)
                .one();

        if (tag == null) {
            return null;
        }

        // 3. 转换为VO
        TagVO tagVO = convertToVO(tag);

        // 4. 缓存结果
        redisUtils.set(cacheKey, tagVO, SystemConstants.Cache.EXPIRE_HOUR);

        return tagVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTag(Long tagId) {
        log.info("删除标签，标签ID：{}", tagId);

        // 1. 验证标签是否存在
        TbTag tag = getById(tagId);
        if (tag == null || SystemConstants.Tag.STATUS_DISABLED.equals(tag.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "标签不存在");
        }

        // 2. 检查是否有关联文章
        Integer articleCount = getTagArticleCount(tagId);
        if (articleCount > 0) {
            throw new BusinessException(ResultEnum.OPERATION_FORBIDDEN, "该标签下还有文章，无法删除");
        }

        // 3. 逻辑删除标签
        boolean deleted = lambdaUpdate()
                .eq(TbTag::getId, tagId)
                .set(TbTag::getIsDelete, 1)
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 4. 清除相关缓存
            clearTagCache();
            clearTagDetailCache(tagId);

            log.info("标签删除成功，标签ID：{}", tagId);
        }

        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTagsBatch(List<Long> tagIds) {
        log.info("批量删除标签，标签数量：{}", tagIds.size());

        // 1. 验证所有标签是否可以删除
        for (Long tagId : tagIds) {
            Integer articleCount = getTagArticleCount(tagId);
            if (articleCount > 0) {
                throw new BusinessException(ResultEnum.OPERATION_FORBIDDEN, "标签ID " + tagId + " 下还有文章，无法删除");
            }
        }

        // 2. 批量逻辑删除
        boolean deleted = lambdaUpdate()
                .in(TbTag::getId, tagIds)
                .set(TbTag::getIsDelete, 1)
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 3. 批量清除缓存
            clearTagCache();
            tagIds.forEach(this::clearTagDetailCache);

            log.info("批量删除标签成功，标签数量：{}", tagIds.size());
        }

        return deleted;
    }

    // ==================== 标签查询操作 ====================

    @Override
    public PageResult<TagVO> getTagList(PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbTag::getIsDelete, 0)
                .orderByDesc(TbTag::getArticleCount)
                .orderByAsc(TbTag::getSortOrder)
                .orderByDesc(TbTag::getCreateTime);

        // 关键词搜索
        if (StringUtils.isNotBlank(pageQueryVO.getKeyword())) {
            queryWrapper.like(TbTag::getName, pageQueryVO.getKeyword())
                    .or().like(TbTag::getDescription, pageQueryVO.getKeyword());
        }

        Page<TbTag> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbTag> resultPage = page(page, queryWrapper);

        List<TagVO> tagVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(tagVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    @Override
    public List<TagVO> getAllTags() {
        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Tag.TAG_LIST;
        List<TagVO> cachedTags = redisUtils.get(cacheKey, List.class);
        if (cachedTags != null) {
            return cachedTags;
        }

        // 2. 从数据库查询
        List<TbTag> tags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .orderByDesc(TbTag::getArticleCount)
                .orderByAsc(TbTag::getSortOrder)
                .list();

        // 3. 转换为VO
        List<TagVO> tagVOList = convertToVOList(tags);

        // 4. 缓存结果
        redisUtils.set(cacheKey, tagVOList, SystemConstants.Cache.EXPIRE_HOUR);

        return tagVOList;
    }

    @Override
    public List<TagVO> searchTags(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }

        List<TbTag> tags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .and(wrapper -> wrapper.like(TbTag::getName, keyword)
                        .or().like(TbTag::getDescription, keyword))
                .orderByDesc(TbTag::getArticleCount)
                .orderByAsc(TbTag::getSortOrder)
                .list();

        return convertToVOList(tags);
    }

    @Override
    public List<TagVO> getHotTags(Integer limit) {
        String cacheKey = RedisConstants.Tag.HOT_TAGS;
        List<TagVO> cachedTags = redisUtils.get(cacheKey, List.class);
        if (cachedTags != null) {
            return cachedTags.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        // 根据文章数量和点击次数计算热度
        List<TbTag> tags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .gt(TbTag::getArticleCount, 0)
                .orderByDesc(TbTag::getArticleCount)
                .orderByDesc(TbTag::getClickCount)
                .last("LIMIT " + (limit * 2))
                .list();

        List<TagVO> tagVOList = convertToVOList(tags);

        // 标记为热门标签
        tagVOList.forEach(tag -> tag.setIsHot(true));

        // 缓存结果
        redisUtils.set(cacheKey, tagVOList, SystemConstants.Cache.HOT_DATA_EXPIRE);

        return tagVOList.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getRecommendTags(Integer limit) {
        // 基于多个指标计算推荐度：文章数量、最近活跃度、点击次数
        List<TbTag> tags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .orderByDesc(TbTag::getArticleCount)
                .orderByDesc(TbTag::getClickCount)
                .orderByDesc(TbTag::getUpdateTime)
                .last("LIMIT " + limit)
                .list();

        List<TagVO> tagVOList = convertToVOList(tags);
        tagVOList.forEach(tag -> tag.setIsRecommend(true));

        return tagVOList;
    }

    @Override
    public List<TagVO> getTagsByType(Integer tagType, Integer limit) {
        List<TbTag> tags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .orderByDesc(TbTag::getArticleCount)
                .orderByAsc(TbTag::getSortOrder)
                .last("LIMIT " + limit)
                .list();

        List<TagVO> tagVOList = convertToVOList(tags);
        // 过滤特定类型的标签（这里简化处理，可以根据实际需求扩展）
        return tagVOList.stream()
                .filter(tag -> tagType == null || Objects.equals(tag.getTagType(), tagType))
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getTagCloud(Integer maxTags) {
        String cacheKey = RedisConstants.Tag.TAG_CLOUD;
        List<TagVO> cachedTags = redisUtils.get(cacheKey, List.class);
        if (cachedTags != null) {
            return cachedTags.stream()
                    .limit(maxTags)
                    .collect(Collectors.toList());
        }

        // 获取有文章关联的标签，按文章数量和点击次数权重排序
        List<TbTag> tags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .gt(TbTag::getArticleCount, 0)
                .orderByDesc(TbTag::getArticleCount)
                .orderByDesc(TbTag::getClickCount)
                .last("LIMIT " + maxTags)
                .list();

        List<TagVO> tagVOList = convertToVOList(tags);

        // 为标签云计算权重级别
        if (!tagVOList.isEmpty()) {
            int maxArticleCount = tagVOList.get(0).getUseCount();
            int minArticleCount = tagVOList.get(tagVOList.size() - 1).getUseCount();

            tagVOList.forEach(tag -> {
                // 计算权重级别（1-5级）
                int level = calculateTagWeight(tag.getUseCount(), maxArticleCount, minArticleCount);
                // 可以通过扩展TagVO添加weight字段来存储
            });
        }

        // 缓存结果
        redisUtils.set(cacheKey, tagVOList, SystemConstants.Cache.EXPIRE_HOUR);

        return tagVOList;
    }

    // ==================== 标签状态管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableTag(Long tagId) {
        log.info("启用标签，标签ID：{}", tagId);

        boolean updated = lambdaUpdate()
                .eq(TbTag::getId, tagId)
                .eq(TbTag::getIsDelete, 0)
                .set(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearTagCache();
            clearTagDetailCache(tagId);
            log.info("标签启用成功，标签ID：{}", tagId);
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableTag(Long tagId) {
        log.info("禁用标签，标签ID：{}", tagId);

        boolean updated = lambdaUpdate()
                .eq(TbTag::getId, tagId)
                .eq(TbTag::getIsDelete, 0)
                .set(TbTag::getStatus, SystemConstants.Tag.STATUS_DISABLED)
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearTagCache();
            clearTagDetailCache(tagId);
            log.info("标签禁用成功，标签ID：{}", tagId);
        }

        return updated;
    }

    // ==================== 标签关联操作 ====================

    @Override
    public List<TagVO> getTagsByArticleId(Long articleId) {
        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Tag.ARTICLE_TAGS + articleId;
        List<TagVO> cachedTags = redisUtils.get(cacheKey, List.class);
        if (cachedTags != null) {
            return cachedTags;
        }

        // 2. 通过关联表查询文章的标签
        List<TbArticleTag> articleTags = articleTagService.lambdaQuery()
                .eq(TbArticleTag::getArticleId, articleId)
                .eq(TbArticleTag::getIsDelete, 0)
                .list();

        if (ObjectUtils.isEmpty(articleTags)) {
            return new ArrayList<>();
        }

        List<Long> tagIds = articleTags.stream()
                .map(TbArticleTag::getTagId)
                .collect(Collectors.toList());

        // 3. 查询标签详情
        List<TbTag> tags = lambdaQuery()
                .in(TbTag::getId, tagIds)
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .orderByAsc(TbTag::getSortOrder)
                .list();

        List<TagVO> tagVOList = convertToVOList(tags);

        // 4. 缓存结果
        redisUtils.set(cacheKey, tagVOList, SystemConstants.Cache.EXPIRE_HOUR);

        return tagVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateTagArticleCount(Long tagId) {
        // 统计标签关联的文章数量
        long count = articleTagService.lambdaQuery()
                .eq(TbArticleTag::getTagId, tagId)
                .eq(TbArticleTag::getIsDelete, 0)
                .count();

        // 更新标签的文章数量
        boolean updated = lambdaUpdate()
                .eq(TbTag::getId, tagId)
                .set(TbTag::getArticleCount, (int) count)
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 清除相关缓存
            clearTagCache();
            clearTagDetailCache(tagId);
            clearTagArticleCountCache(tagId);
        }

        return (int) count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementTagClickCount(Long tagId) {
        boolean updated = lambdaUpdate()
                .eq(TbTag::getId, tagId)
                .eq(TbTag::getIsDelete, 0)
                .setSql("click_count = click_count + 1")
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 清除热门标签缓存
            redisUtils.delete(RedisConstants.Tag.HOT_TAGS);
            clearTagDetailCache(tagId);
        }

        return updated;
    }

    // ==================== 标签统计和验证 ====================

    @Override
    public Integer getTagArticleCount(Long tagId) {
        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Tag.TAG_ARTICLE_COUNT + tagId;
        Integer cachedCount = redisUtils.get(cacheKey, Integer.class);
        if (cachedCount != null) {
            return cachedCount;
        }

        // 2. 从数据库查询
        long count = articleTagService.lambdaQuery()
                .eq(TbArticleTag::getTagId, tagId)
                .eq(TbArticleTag::getIsDelete, 0)
                .count();

        Integer articleCount = (int) count;

        // 3. 缓存结果
        redisUtils.set(cacheKey, articleCount, SystemConstants.Cache.EXPIRE_HOUR);

        return articleCount;
    }

    @Override
    public Boolean checkTagName(String name, Long excludeId) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        LambdaQueryWrapper<TbTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbTag::getName, name.trim())
                .eq(TbTag::getIsDelete, 0);

        if (excludeId != null) {
            queryWrapper.ne(TbTag::getId, excludeId);
        }

        return count(queryWrapper) > 0;
    }

    @Override
    public Boolean checkTagSlug(String slug, Long excludeId) {
        if (StringUtils.isBlank(slug)) {
            return false;
        }

        LambdaQueryWrapper<TbTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbTag::getSlug, slug.trim())
                .eq(TbTag::getIsDelete, 0);

        if (excludeId != null) {
            queryWrapper.ne(TbTag::getId, excludeId);
        }

        return count(queryWrapper) > 0;
    }

    @Override
    public Map<String, Object> getTagStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总标签数
        long totalCount = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .count();
        statistics.put("totalCount", totalCount);

        // 启用标签数
        long enabledCount = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getStatus, SystemConstants.Tag.STATUS_ENABLED)
                .count();
        statistics.put("enabledCount", enabledCount);

        // 禁用标签数
        statistics.put("disabledCount", totalCount - enabledCount);

        // 有文章的标签数
        long tagsWithArticles = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .gt(TbTag::getArticleCount, 0)
                .count();
        statistics.put("tagsWithArticles", tagsWithArticles);

        // 热门标签数（文章数>=5）
        long hotTagsCount = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .ge(TbTag::getArticleCount, 5)
                .count();
        statistics.put("hotTagsCount", hotTagsCount);

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanupUnusedTags() {
        log.info("开始清理无关联文章的标签");

        // 查找文章数量为0的标签
        List<TbTag> unusedTags = lambdaQuery()
                .eq(TbTag::getIsDelete, 0)
                .eq(TbTag::getArticleCount, 0)
                .list();

        if (ObjectUtils.isEmpty(unusedTags)) {
            log.info("没有需要清理的标签");
            return 0;
        }

        // 批量删除
        List<Long> tagIds = unusedTags.stream()
                .map(TbTag::getId)
                .collect(Collectors.toList());

        boolean deleted = lambdaUpdate()
                .in(TbTag::getId, tagIds)
                .set(TbTag::getIsDelete, 1)
                .set(TbTag::getUpdateTime, new Date())
                .update();

        if (deleted) {
            clearTagCache();
            tagIds.forEach(this::clearTagDetailCache);
            log.info("清理无关联文章的标签完成，数量：{}", tagIds.size());
            return tagIds.size();
        }

        return 0;
    }

    // ==================== 数据转换方法 ====================

    @Override
    public TagVO convertToVO(TbTag tag) {
        if (tag == null) {
            return null;
        }

        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tag, tagVO);

        // 字段映射调整
        tagVO.setUseCount(tag.getArticleCount());

        // 状态映射：TbTag(0-禁用，1-启用) -> TagVO(0-正常，1-禁用)
        if (tag.getStatus() != null) {
            tagVO.setStatus(SystemConstants.Tag.STATUS_ENABLED.equals(tag.getStatus()) ? 0 : 1);
        }

        // 设置标签类型（可以根据标签名称或其他逻辑判断）
        tagVO.setTagType(inferTagType(tag.getName()));

        // 设置是否热门（文章数>=5）
        tagVO.setIsHot(tag.getArticleCount() != null && tag.getArticleCount() >= 5);

        // 设置是否推荐（文章数>=3且最近有更新）
        tagVO.setIsRecommend(tag.getArticleCount() != null && tag.getArticleCount() >= 3);

        // 转换时间格式
        if (tag.getCreateTime() != null) {
            tagVO.setCreateTime(tag.getCreateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        if (tag.getUpdateTime() != null) {
            tagVO.setUpdateTime(tag.getUpdateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        return tagVO;
    }

    @Override
    public List<TagVO> convertToVOList(List<TbTag> tags) {
        if (ObjectUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }

        return tags.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从VO构建标签对象
     */
    private TbTag buildTagFromVO(TagVO tagVO) {
        TbTag tag = new TbTag();
        BeanUtils.copyProperties(tagVO, tag);

        // 生成slug
        if (StringUtils.isBlank(tag.getSlug())) {
            tag.setSlug(generateSlug(tagVO.getName()));
        }

        // 状态映射：TagVO(0-正常，1-禁用) -> TbTag(0-禁用，1-启用)
        if (tagVO.getStatus() != null) {
            tag.setStatus(tagVO.getStatus() == 0 ? SystemConstants.Tag.STATUS_ENABLED
                    : SystemConstants.Tag.STATUS_DISABLED);
        } else {
            tag.setStatus(SystemConstants.Tag.STATUS_ENABLED);
        }

        // 设置默认值
        Date now = new Date();
        tag.setCreateTime(now);
        tag.setUpdateTime(now);
        tag.setIsDelete(0);
        tag.setArticleCount(0);
        tag.setClickCount(0);

        if (tag.getSortOrder() == null) {
            tag.setSortOrder(0);
        }

        return tag;
    }

    /**
     * 更新标签字段
     */
    private void updateTagFields(TbTag tag, TagVO tagVO) {
        if (StringUtils.isNotBlank(tagVO.getName())) {
            tag.setName(tagVO.getName());
            tag.setSlug(generateSlug(tagVO.getName()));
        }
        if (StringUtils.isNotBlank(tagVO.getDescription())) {
            tag.setDescription(tagVO.getDescription());
        }
        if (StringUtils.isNotBlank(tagVO.getColor())) {
            tag.setColor(tagVO.getColor());
        }
        if (tagVO.getSortOrder() != null) {
            tag.setSortOrder(tagVO.getSortOrder());
        }
        if (tagVO.getStatus() != null) {
            tag.setStatus(tagVO.getStatus() == 0 ? SystemConstants.Tag.STATUS_ENABLED
                    : SystemConstants.Tag.STATUS_DISABLED);
        }

        tag.setUpdateTime(new Date());
    }

    /**
     * 生成标签别名
     */
    private String generateSlug(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        return name.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * 推断标签类型
     */
    private Integer inferTagType(String tagName) {
        if (StringUtils.isBlank(tagName)) {
            return 0; // 普通标签
        }

        String name = tagName.toLowerCase();
        // 技术标签关键词
        String[] techKeywords = { "java", "spring", "mysql", "redis", "vue", "javascript", "python", "docker",
                "kubernetes" };
        for (String keyword : techKeywords) {
            if (name.contains(keyword)) {
                return 1; // 技术标签
            }
        }

        return 0; // 普通标签
    }

    /**
     * 计算标签权重级别
     */
    private int calculateTagWeight(Integer articleCount, int maxCount, int minCount) {
        if (articleCount == null || maxCount == minCount) {
            return 1;
        }

        double ratio = (double) (articleCount - minCount) / (maxCount - minCount);
        return Math.max(1, Math.min(5, (int) Math.ceil(ratio * 5)));
    }

    /**
     * 清除标签相关缓存
     */
    private void clearTagCache() {
        redisUtils.delete(RedisConstants.Tag.TAG_LIST);
        redisUtils.delete(RedisConstants.Tag.HOT_TAGS);
        redisUtils.delete(RedisConstants.Tag.TAG_CLOUD);
        redisUtils.deletePattern(RedisConstants.Tag.TAG_ARTICLE_COUNT + "*");
        redisUtils.deletePattern(RedisConstants.Tag.ARTICLE_TAGS + "*");
    }

    /**
     * 清除标签详情缓存
     */
    private void clearTagDetailCache(Long tagId) {
        String cacheKey = RedisConstants.Tag.TAG_DETAIL + tagId;
        redisUtils.delete(cacheKey);
    }

    /**
     * 清除标签文章数量缓存
     */
    private void clearTagArticleCountCache(Long tagId) {
        String cacheKey = RedisConstants.Tag.TAG_ARTICLE_COUNT + tagId;
        redisUtils.delete(cacheKey);
    }
}
