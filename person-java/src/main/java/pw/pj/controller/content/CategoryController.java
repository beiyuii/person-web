package pw.pj.controller.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pw.pj.POJO.VO.*;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.service.TbCategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

/**
 * 分类管理控制器
 * 负责处理分类的增删改查、分页查询等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@RestController
@RequestMapping("/api/categories")
@Validated
@Slf4j
public class CategoryController {

    @Autowired
    private TbCategoryService categoryService;

    /**
     * 创建分类
     * 
     * @param categoryVO 分类创建请求参数
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<CategoryVO> createCategory(@Valid @RequestBody CategoryVO categoryVO) {
        try {
            log.info("创建分类请求：分类名称={}, 父分类ID={}", categoryVO.getName(), categoryVO.getParentId());

            CategoryVO createdCategory = categoryService.createCategory(categoryVO);
            return ApiResponse.success(createdCategory);

        } catch (Exception e) {
            log.error("创建分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("创建分类失败");
        }
    }

    /**
     * 获取分类详情
     * 
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryVO> getCategoryById(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("获取分类详情请求：分类ID={}", id);

            CategoryVO categoryVO = categoryService.getCategoryById(id);
            if (categoryVO == null) {
                return ApiResponse.error("分类不存在");
            }
            return ApiResponse.success(categoryVO);

        } catch (Exception e) {
            log.error("获取分类详情异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取分类详情失败");
        }
    }

    /**
     * 更新分类
     * 
     * @param id         分类ID
     * @param categoryVO 分类更新请求参数
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResponse<CategoryVO> updateCategory(@PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody CategoryVO categoryVO) {
        try {
            log.info("更新分类请求：分类ID={}, 分类名称={}", id, categoryVO.getName());

            CategoryVO updatedCategory = categoryService.updateCategory(id, categoryVO);
            return ApiResponse.success(updatedCategory);

        } catch (Exception e) {
            log.error("更新分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("更新分类失败");
        }
    }

    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("删除分类请求：分类ID={}", id);

            Boolean deleted = categoryService.deleteCategory(id);
            if (deleted) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("删除分类失败");
            }

        } catch (Exception e) {
            log.error("删除分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("删除分类失败");
        }
    }

    /**
     * 分页查询分类列表
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<CategoryVO>> getCategoryList(@Valid PageQueryVO pageQueryVO) {
        try {
            log.info("分页查询分类列表请求：页码={}, 每页大小={}", pageQueryVO.getPageNum(), pageQueryVO.getPageSize());

            PageResult<CategoryVO> pageResult = categoryService.getCategoryList(pageQueryVO);
            return ApiResponse.success(pageResult);

        } catch (Exception e) {
            log.error("分页查询分类列表异常：{}", e.getMessage(), e);
            return ApiResponse.error("查询分类列表失败");
        }
    }

    /**
     * 获取所有分类列表
     * 
     * @return 所有分类列表
     */
    @GetMapping("/all")
    public ApiResponse<List<CategoryVO>> getAllCategories() {
        try {
            log.info("获取所有分类列表请求");

            List<CategoryVO> categories = categoryService.getAllCategories();
            return ApiResponse.success(categories);

        } catch (Exception e) {
            log.error("获取所有分类列表异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取分类列表失败");
        }
    }

    /**
     * 搜索分类
     * 
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ApiResponse<List<CategoryVO>> searchCategories(@RequestParam("keyword") String keyword) {
        try {
            log.info("搜索分类请求：关键词={}", keyword);

            // 验证搜索关键词
            if (keyword == null || keyword.trim().isEmpty()) {
                return ApiResponse.error("搜索关键词不能为空");
            }

            List<CategoryVO> categories = categoryService.searchCategories(keyword);
            return ApiResponse.success(categories);

        } catch (Exception e) {
            log.error("搜索分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("搜索分类失败");
        }
    }

    /**
     * 获取分类文章数量
     * 
     * @param id 分类ID
     * @return 文章数量
     */
    @GetMapping("/{id}/articles/count")
    public ApiResponse<Integer> getCategoryArticleCount(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("获取分类文章数量请求：分类ID={}", id);

            Integer articleCount = categoryService.getCategoryArticleCount(id);
            return ApiResponse.success(articleCount);

        } catch (Exception e) {
            log.error("获取分类文章数量异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取分类文章数量失败");
        }
    }

    /**
     * 批量删除分类
     * 
     * @param categoryIds 分类ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteCategories(@RequestBody @Valid List<Long> categoryIds) {
        try {
            log.info("批量删除分类请求：分类数量={}", categoryIds.size());

            // 验证分类ID列表
            if (categoryIds == null || categoryIds.isEmpty()) {
                return ApiResponse.error("分类ID列表不能为空");
            }

            Boolean deleted = categoryService.deleteCategoriesBatch(categoryIds);
            if (deleted) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("批量删除分类失败");
            }

        } catch (Exception e) {
            log.error("批量删除分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("批量删除分类失败");
        }
    }

    /**
     * 获取热门分类
     * 
     * @param limit 数量限制
     * @return 热门分类列表
     */
    @GetMapping("/hot")
    public ApiResponse<List<CategoryVO>> getHotCategories(
            @RequestParam(value = "limit", defaultValue = "10") @Positive int limit) {
        try {
            log.info("获取热门分类请求：数量限制={}", limit);

            List<CategoryVO> hotCategories = categoryService.getHotCategories(limit);
            return ApiResponse.success(hotCategories);

        } catch (Exception e) {
            log.error("获取热门分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取热门分类失败");
        }
    }

    /**
     * 启用分类
     * 
     * @param id 分类ID
     * @return 启用结果
     */
    @PutMapping("/{id}/enable")
    public ApiResponse<Void> enableCategory(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("启用分类请求：分类ID={}", id);

            Boolean enabled = categoryService.enableCategory(id);
            if (enabled) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("启用分类失败");
            }

        } catch (Exception e) {
            log.error("启用分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("启用分类失败");
        }
    }

    /**
     * 禁用分类
     * 
     * @param id 分类ID
     * @return 禁用结果
     */
    @PutMapping("/{id}/disable")
    public ApiResponse<Void> disableCategory(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("禁用分类请求：分类ID={}", id);

            Boolean disabled = categoryService.disableCategory(id);
            if (disabled) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("禁用分类失败");
            }

        } catch (Exception e) {
            log.error("禁用分类异常：{}", e.getMessage(), e);
            return ApiResponse.error("禁用分类失败");
        }
    }

    /**
     * 检查分类名称是否存在
     * 
     * @param name      分类名称
     * @param excludeId 排除的分类ID（更新时使用）
     * @return 检查结果
     */
    @GetMapping("/check-name")
    public ApiResponse<Boolean> checkCategoryName(@RequestParam("name") String name,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        try {
            log.info("检查分类名称请求：名称={}, 排除ID={}", name, excludeId);

            // 验证分类名称
            if (name == null || name.trim().isEmpty()) {
                return ApiResponse.error("分类名称不能为空");
            }

            Boolean exists = categoryService.checkCategoryName(name, excludeId);
            return ApiResponse.success(exists);

        } catch (Exception e) {
            log.error("检查分类名称异常：{}", e.getMessage(), e);
            return ApiResponse.error("检查分类名称失败");
        }
    }

    /**
     * 检查分类路径是否存在
     * 
     * @param path      分类路径
     * @param excludeId 排除的分类ID（更新时使用）
     * @return 检查结果
     */
    @GetMapping("/check-path")
    public ApiResponse<Boolean> checkCategoryPath(@RequestParam("path") String path,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        try {
            log.info("检查分类路径请求：路径={}, 排除ID={}", path, excludeId);

            // 验证分类路径
            if (path == null || path.trim().isEmpty()) {
                return ApiResponse.error("分类路径不能为空");
            }

            Boolean exists = categoryService.checkCategoryPath(path, excludeId);
            return ApiResponse.success(exists);

        } catch (Exception e) {
            log.error("检查分类路径异常：{}", e.getMessage(), e);
            return ApiResponse.error("检查分类路径失败");
        }
    }

    /**
     * 获取分类统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getCategoryStatistics() {
        try {
            log.info("获取分类统计信息请求");

            Map<String, Object> statistics = categoryService.getCategoryStatistics();
            return ApiResponse.success(statistics);

        } catch (Exception e) {
            log.error("获取分类统计信息异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取分类统计信息失败");
        }
    }
}