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
import pw.pj.POJO.DO.TbArticle;
import pw.pj.POJO.DO.TbCategory;
import pw.pj.POJO.VO.CategoryVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.RedisUtils;
import pw.pj.common.utils.StringUtils;
import pw.pj.mapper.TbCategoryMapper;
import pw.pj.service.TbArticleService;
import pw.pj.service.TbCategoryService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类管理服务实现类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Autowired
    @Lazy
    private TbArticleService articleService;

    @Autowired
    private RedisUtils redisUtils;

    // ==================== 分类CRUD操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO createCategory(CategoryVO categoryVO) {
        log.info("创建分类，分类名称：{}", categoryVO.getName());

        // 1. 验证分类名称唯一性
        if (checkCategoryName(categoryVO.getName(), null)) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "分类名称已存在");
        }

        // 2. 验证父分类是否存在
        if (categoryVO.getParentId() != null && categoryVO.getParentId() > 0) {
            TbCategory parentCategory = getById(categoryVO.getParentId());
            if (parentCategory == null || !SystemConstants.Category.STATUS_ENABLED.equals(parentCategory.getStatus())) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "父分类不存在或已禁用");
            }
        }

        // 3. 创建分类对象
        TbCategory category = buildCategoryFromVO(categoryVO);

        // 4. 保存分类
        boolean saved = save(category);
        if (!saved) {
            throw new BusinessException(ResultEnum.SAVE_FAIL, "分类保存失败");
        }

        // 5. 清除相关缓存
        clearCategoryCache();

        log.info("分类创建成功，分类ID：{}", category.getId());
        return convertToVO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO updateCategory(Long categoryId, CategoryVO categoryVO) {
        log.info("更新分类，分类ID：{}", categoryId);

        // 1. 验证分类是否存在
        TbCategory category = getById(categoryId);
        if (category == null || SystemConstants.Category.STATUS_DISABLED.equals(category.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "分类不存在");
        }

        // 2. 验证分类名称唯一性（排除自己）
        if (StringUtils.isNotBlank(categoryVO.getName()) && !category.getName().equals(categoryVO.getName())) {
            if (checkCategoryName(categoryVO.getName(), categoryId)) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "分类名称已存在");
            }
        }

        // 3. 验证父分类
        if (categoryVO.getParentId() != null && categoryVO.getParentId() > 0) {
            if (categoryId.equals(categoryVO.getParentId())) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "不能将自己设为父分类");
            }
            TbCategory parentCategory = getById(categoryVO.getParentId());
            if (parentCategory == null || !SystemConstants.Category.STATUS_ENABLED.equals(parentCategory.getStatus())) {
                throw new BusinessException(ResultEnum.PARAM_ERROR, "父分类不存在或已禁用");
            }
        }

        // 4. 更新分类字段
        updateCategoryFields(category, categoryVO);

        // 5. 保存更新
        boolean updated = updateById(category);
        if (!updated) {
            throw new BusinessException(ResultEnum.UPDATE_FAIL, "分类更新失败");
        }

        // 6. 清除相关缓存
        clearCategoryCache();
        clearCategoryDetailCache(categoryId);

        log.info("分类更新成功，分类ID：{}", categoryId);
        return convertToVO(category);
    }

    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Category.CATEGORY_DETAIL + categoryId;
        CategoryVO cachedCategory = redisUtils.get(cacheKey, CategoryVO.class);
        if (cachedCategory != null) {
            return cachedCategory;
        }

        // 2. 从数据库查询
        TbCategory category = lambdaQuery()
                .eq(TbCategory::getId, categoryId)
                .eq(TbCategory::getIsDelete, 0)
                .one();

        if (category == null) {
            return null;
        }

        // 3. 转换为VO
        CategoryVO categoryVO = convertToVO(category);

        // 4. 缓存结果
        redisUtils.set(cacheKey, categoryVO, SystemConstants.Cache.EXPIRE_HOUR);

        return categoryVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Long categoryId) {
        log.info("删除分类，分类ID：{}", categoryId);

        // 1. 验证分类是否存在
        TbCategory category = getById(categoryId);
        if (category == null || SystemConstants.Category.STATUS_DISABLED.equals(category.getIsDelete())) {
            throw new BusinessException(ResultEnum.DATA_NOT_FOUND, "分类不存在");
        }

        // 2. 检查是否有子分类
        long childCount = lambdaQuery()
                .eq(TbCategory::getParentId, categoryId)
                .eq(TbCategory::getIsDelete, 0)
                .count();
        if (childCount > 0) {
            throw new BusinessException(ResultEnum.OPERATION_FORBIDDEN, "该分类下还有子分类，无法删除");
        }

        // 3. 检查是否有关联文章
        Integer articleCount = getCategoryArticleCount(categoryId);
        if (articleCount > 0) {
            throw new BusinessException(ResultEnum.OPERATION_FORBIDDEN, "该分类下还有文章，无法删除");
        }

        // 4. 逻辑删除分类
        boolean deleted = lambdaUpdate()
                .eq(TbCategory::getId, categoryId)
                .set(TbCategory::getIsDelete, 1)
                .set(TbCategory::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 5. 清除相关缓存
            clearCategoryCache();
            clearCategoryDetailCache(categoryId);

            log.info("分类删除成功，分类ID：{}", categoryId);
        }

        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategoriesBatch(List<Long> categoryIds) {
        log.info("批量删除分类，分类数量：{}", categoryIds.size());

        // 1. 验证所有分类是否可以删除
        for (Long categoryId : categoryIds) {
            // 检查子分类
            long childCount = lambdaQuery()
                    .eq(TbCategory::getParentId, categoryId)
                    .eq(TbCategory::getIsDelete, 0)
                    .count();
            if (childCount > 0) {
                throw new BusinessException(ResultEnum.OPERATION_FORBIDDEN, "分类ID " + categoryId + " 下还有子分类，无法删除");
            }

            // 检查关联文章
            Integer articleCount = getCategoryArticleCount(categoryId);
            if (articleCount > 0) {
                throw new BusinessException(ResultEnum.OPERATION_FORBIDDEN, "分类ID " + categoryId + " 下还有文章，无法删除");
            }
        }

        // 2. 批量逻辑删除
        boolean deleted = lambdaUpdate()
                .in(TbCategory::getId, categoryIds)
                .set(TbCategory::getIsDelete, 1)
                .set(TbCategory::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 3. 批量清除缓存
            clearCategoryCache();
            categoryIds.forEach(this::clearCategoryDetailCache);

            log.info("批量删除分类成功，分类数量：{}", categoryIds.size());
        }

        return deleted;
    }

    // ==================== 分类查询操作 ====================

    @Override
    public PageResult<CategoryVO> getCategoryList(PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbCategory::getIsDelete, 0)
                .orderByAsc(TbCategory::getSortOrder)
                .orderByDesc(TbCategory::getCreateTime);

        // 关键词搜索
        if (StringUtils.isNotBlank(pageQueryVO.getKeyword())) {
            queryWrapper.like(TbCategory::getName, pageQueryVO.getKeyword())
                    .or().like(TbCategory::getDescription, pageQueryVO.getKeyword());
        }

        Page<TbCategory> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbCategory> resultPage = page(page, queryWrapper);

        List<CategoryVO> categoryVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(categoryVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    @Override
    public List<CategoryVO> getAllCategories() {
        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Category.CATEGORY_LIST;
        List<CategoryVO> cachedCategories = redisUtils.get(cacheKey, List.class);
        if (cachedCategories != null) {
            return cachedCategories;
        }

        // 2. 从数据库查询
        List<TbCategory> categories = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .eq(TbCategory::getStatus, SystemConstants.Category.STATUS_ENABLED)
                .orderByAsc(TbCategory::getSortOrder)
                .orderByDesc(TbCategory::getCreateTime)
                .list();

        // 3. 转换为VO
        List<CategoryVO> categoryVOList = convertToVOList(categories);

        // 4. 缓存结果
        redisUtils.set(cacheKey, categoryVOList, SystemConstants.Cache.EXPIRE_HOUR);

        return categoryVOList;
    }

    @Override
    public List<CategoryVO> searchCategories(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }

        List<TbCategory> categories = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .eq(TbCategory::getStatus, SystemConstants.Category.STATUS_ENABLED)
                .and(wrapper -> wrapper.like(TbCategory::getName, keyword)
                        .or().like(TbCategory::getDescription, keyword))
                .orderByAsc(TbCategory::getSortOrder)
                .list();

        return convertToVOList(categories);
    }

    @Override
    public List<CategoryVO> getHotCategories(Integer limit) {
        String cacheKey = RedisConstants.Category.HOT_CATEGORIES;
        List<CategoryVO> cachedCategories = redisUtils.get(cacheKey, List.class);
        if (cachedCategories != null) {
            return cachedCategories.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        // 根据文章数量排序获取热门分类
        List<TbCategory> categories = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .eq(TbCategory::getStatus, SystemConstants.Category.STATUS_ENABLED)
                .orderByDesc(TbCategory::getArticleCount)
                .orderByAsc(TbCategory::getSortOrder)
                .last("LIMIT " + (limit * 2)) // 多查一些，以防有些分类文章数为0
                .list();

        List<CategoryVO> categoryVOList = convertToVOList(categories);

        // 过滤出有文章的分类
        List<CategoryVO> hotCategories = categoryVOList.stream()
                .filter(category -> category.getArticleCount() != null && category.getArticleCount() > 0)
                .limit(limit)
                .collect(Collectors.toList());

        // 缓存结果
        redisUtils.set(cacheKey, hotCategories, SystemConstants.Cache.HOT_DATA_EXPIRE);

        return hotCategories;
    }

    // ==================== 分类状态管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableCategory(Long categoryId) {
        log.info("启用分类，分类ID：{}", categoryId);

        boolean updated = lambdaUpdate()
                .eq(TbCategory::getId, categoryId)
                .eq(TbCategory::getIsDelete, 0)
                .set(TbCategory::getStatus, SystemConstants.Category.STATUS_ENABLED)
                .set(TbCategory::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearCategoryCache();
            clearCategoryDetailCache(categoryId);
            log.info("分类启用成功，分类ID：{}", categoryId);
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableCategory(Long categoryId) {
        log.info("禁用分类，分类ID：{}", categoryId);

        boolean updated = lambdaUpdate()
                .eq(TbCategory::getId, categoryId)
                .eq(TbCategory::getIsDelete, 0)
                .set(TbCategory::getStatus, SystemConstants.Category.STATUS_DISABLED)
                .set(TbCategory::getUpdateTime, new Date())
                .update();

        if (updated) {
            clearCategoryCache();
            clearCategoryDetailCache(categoryId);
            log.info("分类禁用成功，分类ID：{}", categoryId);
        }

        return updated;
    }

    // ==================== 分类统计和验证 ====================

    @Override
    public Integer getCategoryArticleCount(Long categoryId) {
        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.Category.CATEGORY_ARTICLE_COUNT + categoryId;
        Integer cachedCount = redisUtils.get(cacheKey, Integer.class);
        if (cachedCount != null) {
            return cachedCount;
        }

        // 2. 从数据库查询
        long count = articleService.lambdaQuery()
                .eq(TbArticle::getCategoryId, categoryId)
                .eq(TbArticle::getIsDelete, 0)
                .eq(TbArticle::getStatus, SystemConstants.Article.STATUS_PUBLISHED)
                .count();

        Integer articleCount = (int) count;

        // 3. 缓存结果
        redisUtils.set(cacheKey, articleCount, SystemConstants.Cache.EXPIRE_HOUR);

        return articleCount;
    }

    @Override
    public Boolean checkCategoryName(String name, Long excludeId) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        LambdaQueryWrapper<TbCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbCategory::getName, name.trim())
                .eq(TbCategory::getIsDelete, 0);

        if (excludeId != null) {
            queryWrapper.ne(TbCategory::getId, excludeId);
        }

        return count(queryWrapper) > 0;
    }

    @Override
    public Boolean checkCategoryPath(String path, Long excludeId) {
        if (StringUtils.isBlank(path)) {
            return false;
        }

        LambdaQueryWrapper<TbCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbCategory::getSlug, path.trim())
                .eq(TbCategory::getIsDelete, 0);

        if (excludeId != null) {
            queryWrapper.ne(TbCategory::getId, excludeId);
        }

        return count(queryWrapper) > 0;
    }

    @Override
    public Map<String, Object> getCategoryStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总分类数
        long totalCount = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .count();
        statistics.put("totalCount", totalCount);

        // 启用分类数
        long enabledCount = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .eq(TbCategory::getStatus, SystemConstants.Category.STATUS_ENABLED)
                .count();
        statistics.put("enabledCount", enabledCount);

        // 禁用分类数
        statistics.put("disabledCount", totalCount - enabledCount);

        // 顶级分类数
        long topLevelCount = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .eq(TbCategory::getLevel, 1)
                .count();
        statistics.put("topLevelCount", topLevelCount);

        // 有文章的分类数
        long categoriesWithArticles = lambdaQuery()
                .eq(TbCategory::getIsDelete, 0)
                .gt(TbCategory::getArticleCount, 0)
                .count();
        statistics.put("categoriesWithArticles", categoriesWithArticles);

        return statistics;
    }

    // ==================== 数据转换方法 ====================

    @Override
    public CategoryVO convertToVO(TbCategory category) {
        if (category == null) {
            return null;
        }

        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);

        // 字段映射调整
        categoryVO.setIconUrl(category.getIcon());

        // 状态映射：TbCategory(0-禁用，1-启用) -> CategoryVO(0-正常，1-禁用)
        if (category.getStatus() != null) {
            categoryVO.setStatus(SystemConstants.Category.STATUS_ENABLED.equals(category.getStatus()) ? 0 : 1);
        }

        // 设置文章数量
        Integer articleCount = getCategoryArticleCount(category.getId());
        categoryVO.setArticleCount(articleCount);

        // 设置父分类名称
        if (category.getParentId() != null && category.getParentId() > 0) {
            TbCategory parentCategory = getById(category.getParentId());
            if (parentCategory != null) {
                categoryVO.setParentName(parentCategory.getName());
            }
        }

        // 设置是否热门
        categoryVO.setIsHot(articleCount != null && articleCount >= 5); // 5篇以上文章算热门

        // 转换时间格式
        if (category.getCreateTime() != null) {
            categoryVO.setCreateTime(category.getCreateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        if (category.getUpdateTime() != null) {
            categoryVO.setUpdateTime(category.getUpdateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        return categoryVO;
    }

    @Override
    public List<CategoryVO> convertToVOList(List<TbCategory> categories) {
        if (ObjectUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }

        return categories.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从VO构建分类对象
     */
    private TbCategory buildCategoryFromVO(CategoryVO categoryVO) {
        TbCategory category = new TbCategory();
        BeanUtils.copyProperties(categoryVO, category);

        // 字段映射调整
        category.setIcon(categoryVO.getIconUrl());

        // 状态映射：CategoryVO(0-正常，1-禁用) -> TbCategory(0-禁用，1-启用)
        if (categoryVO.getStatus() != null) {
            category.setStatus(categoryVO.getStatus() == 0 ? SystemConstants.Category.STATUS_ENABLED
                    : SystemConstants.Category.STATUS_DISABLED);
        } else {
            category.setStatus(SystemConstants.Category.STATUS_ENABLED);
        }

        // 设置默认值
        Date now = new Date();
        category.setCreateTime(now);
        category.setUpdateTime(now);
        category.setIsDelete(0);
        category.setArticleCount(0);

        // 处理层级和路径
        if (categoryVO.getParentId() != null && categoryVO.getParentId() > 0) {
            TbCategory parentCategory = getById(categoryVO.getParentId());
            if (parentCategory != null) {
                category.setLevel(parentCategory.getLevel() + 1);
                category.setPath(parentCategory.getPath() + categoryVO.getParentId() + ",");
            }
        } else {
            category.setParentId(0L);
            category.setLevel(1);
            category.setPath("0,");
        }

        // 设置排序值
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        return category;
    }

    /**
     * 更新分类字段
     */
    private void updateCategoryFields(TbCategory category, CategoryVO categoryVO) {
        if (StringUtils.isNotBlank(categoryVO.getName())) {
            category.setName(categoryVO.getName());
        }
        if (StringUtils.isNotBlank(categoryVO.getDescription())) {
            category.setDescription(categoryVO.getDescription());
        }
        if (StringUtils.isNotBlank(categoryVO.getIconUrl())) {
            category.setIcon(categoryVO.getIconUrl());
        }
        if (StringUtils.isNotBlank(categoryVO.getCoverImage())) {
            category.setCoverImage(categoryVO.getCoverImage());
        }
        if (categoryVO.getSortOrder() != null) {
            category.setSortOrder(categoryVO.getSortOrder());
        }
        if (categoryVO.getStatus() != null) {
            category.setStatus(categoryVO.getStatus() == 0 ? SystemConstants.Category.STATUS_ENABLED
                    : SystemConstants.Category.STATUS_DISABLED);
        }

        // 处理父分类变更
        if (categoryVO.getParentId() != null && !categoryVO.getParentId().equals(category.getParentId())) {
            if (categoryVO.getParentId() > 0) {
                TbCategory parentCategory = getById(categoryVO.getParentId());
                if (parentCategory != null) {
                    category.setParentId(categoryVO.getParentId());
                    category.setLevel(parentCategory.getLevel() + 1);
                    category.setPath(parentCategory.getPath() + categoryVO.getParentId() + ",");
                }
            } else {
                category.setParentId(0L);
                category.setLevel(1);
                category.setPath("0,");
            }
        }

        category.setUpdateTime(new Date());
    }

    /**
     * 清除分类相关缓存
     */
    private void clearCategoryCache() {
        redisUtils.delete(RedisConstants.Category.CATEGORY_LIST);
        redisUtils.delete(RedisConstants.Category.HOT_CATEGORIES);
        redisUtils.delete(RedisConstants.Category.CATEGORY_TREE);
        redisUtils.deletePattern(RedisConstants.Category.CATEGORY_ARTICLE_COUNT + "*");
    }

    /**
     * 清除分类详情缓存
     */
    private void clearCategoryDetailCache(Long categoryId) {
        String cacheKey = RedisConstants.Category.CATEGORY_DETAIL + categoryId;
        redisUtils.delete(cacheKey);
    }
}
