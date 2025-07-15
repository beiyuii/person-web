package pw.pj.controller.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pw.pj.POJO.VO.*;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.service.TbTagService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

/**
 * 标签管理控制器
 * 负责处理标签的增删改查、分页查询等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@RestController
@RequestMapping("/api/tags")
@Validated
@Slf4j
public class TagController {

    @Autowired
    private TbTagService tagService;

    /**
     * 创建标签
     * 
     * @param tagVO 标签创建请求参数
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<TagVO> createTag(@Valid @RequestBody TagVO tagVO) {
        try {
            log.info("创建标签请求：标签名称={}", tagVO.getName());

            TagVO createdTag = tagService.createTag(tagVO);
            return ApiResponse.success(createdTag);

        } catch (Exception e) {
            log.error("创建标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("创建标签失败");
        }
    }

    /**
     * 获取标签详情
     * 
     * @param id 标签ID
     * @return 标签详情
     */
    @GetMapping("/{id}")
    public ApiResponse<TagVO> getTagById(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("获取标签详情请求：标签ID={}", id);

            TagVO tagVO = tagService.getTagById(id);
            if (tagVO == null) {
                return ApiResponse.error("标签不存在");
            }
            return ApiResponse.success(tagVO);

        } catch (Exception e) {
            log.error("获取标签详情异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取标签详情失败");
        }
    }

    /**
     * 更新标签
     * 
     * @param id    标签ID
     * @param tagVO 标签更新请求参数
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResponse<TagVO> updateTag(@PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody TagVO tagVO) {
        try {
            log.info("更新标签请求：标签ID={}, 标签名称={}", id, tagVO.getName());

            TagVO updatedTag = tagService.updateTag(id, tagVO);
            return ApiResponse.success(updatedTag);

        } catch (Exception e) {
            log.error("更新标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("更新标签失败");
        }
    }

    /**
     * 删除标签
     * 
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("删除标签请求：标签ID={}", id);

            Boolean deleted = tagService.deleteTag(id);
            if (deleted) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("删除标签失败");
            }

        } catch (Exception e) {
            log.error("删除标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("删除标签失败");
        }
    }

    /**
     * 分页查询标签列表
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<TagVO>> getTagList(@Valid PageQueryVO pageQueryVO) {
        try {
            log.info("分页查询标签列表请求：页码={}, 每页大小={}", pageQueryVO.getPageNum(), pageQueryVO.getPageSize());

            PageResult<TagVO> pageResult = tagService.getTagList(pageQueryVO);
            return ApiResponse.success(pageResult);

        } catch (Exception e) {
            log.error("分页查询标签列表异常：{}", e.getMessage(), e);
            return ApiResponse.error("查询标签列表失败");
        }
    }

    /**
     * 获取所有标签列表
     * 
     * @return 所有标签列表
     */
    @GetMapping("/all")
    public ApiResponse<List<TagVO>> getAllTags() {
        try {
            log.info("获取所有标签列表请求");

            List<TagVO> tags = tagService.getAllTags();
            return ApiResponse.success(tags);

        } catch (Exception e) {
            log.error("获取所有标签列表异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取标签列表失败");
        }
    }

    /**
     * 搜索标签
     * 
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ApiResponse<List<TagVO>> searchTags(@RequestParam("keyword") String keyword) {
        try {
            log.info("搜索标签请求：关键词={}", keyword);

            // 验证搜索关键词
            if (keyword == null || keyword.trim().isEmpty()) {
                return ApiResponse.error("搜索关键词不能为空");
            }

            List<TagVO> tags = tagService.searchTags(keyword);
            return ApiResponse.success(tags);

        } catch (Exception e) {
            log.error("搜索标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("搜索标签失败");
        }
    }

    /**
     * 获取标签文章数量
     * 
     * @param id 标签ID
     * @return 文章数量
     */
    @GetMapping("/{id}/articles/count")
    public ApiResponse<Integer> getTagArticleCount(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("获取标签文章数量请求：标签ID={}", id);

            Integer articleCount = tagService.getTagArticleCount(id);
            return ApiResponse.success(articleCount);

        } catch (Exception e) {
            log.error("获取标签文章数量异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取标签文章数量失败");
        }
    }

    /**
     * 批量删除标签
     * 
     * @param tagIds 标签ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteTags(@RequestBody @Valid List<Long> tagIds) {
        try {
            log.info("批量删除标签请求：标签数量={}", tagIds.size());

            // 验证标签ID列表
            if (tagIds == null || tagIds.isEmpty()) {
                return ApiResponse.error("标签ID列表不能为空");
            }

            Boolean deleted = tagService.deleteTagsBatch(tagIds);
            if (deleted) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("批量删除标签失败");
            }

        } catch (Exception e) {
            log.error("批量删除标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("批量删除标签失败");
        }
    }

    /**
     * 获取热门标签
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    @GetMapping("/hot")
    public ApiResponse<List<TagVO>> getHotTags(
            @RequestParam(value = "limit", defaultValue = "10") @Positive int limit) {
        try {
            log.info("获取热门标签请求：数量限制={}", limit);

            List<TagVO> hotTags = tagService.getHotTags(limit);
            return ApiResponse.success(hotTags);

        } catch (Exception e) {
            log.error("获取热门标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取热门标签失败");
        }
    }

    /**
     * 获取标签云数据
     * 
     * @param maxTags 最大标签数量
     * @return 标签云数据
     */
    @GetMapping("/cloud")
    public ApiResponse<List<TagVO>> getTagCloud(
            @RequestParam(value = "maxTags", defaultValue = "50") @Positive int maxTags) {
        try {
            log.info("获取标签云数据请求：最大标签数量={}", maxTags);

            List<TagVO> tagCloud = tagService.getTagCloud(maxTags);
            return ApiResponse.success(tagCloud);

        } catch (Exception e) {
            log.error("获取标签云数据异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取标签云数据失败");
        }
    }

    /**
     * 启用标签
     * 
     * @param id 标签ID
     * @return 启用结果
     */
    @PutMapping("/{id}/enable")
    public ApiResponse<Void> enableTag(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("启用标签请求：标签ID={}", id);

            Boolean enabled = tagService.enableTag(id);
            if (enabled) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("启用标签失败");
            }

        } catch (Exception e) {
            log.error("启用标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("启用标签失败");
        }
    }

    /**
     * 禁用标签
     * 
     * @param id 标签ID
     * @return 禁用结果
     */
    @PutMapping("/{id}/disable")
    public ApiResponse<Void> disableTag(@PathVariable @NotNull @Positive Long id) {
        try {
            log.info("禁用标签请求：标签ID={}", id);

            Boolean disabled = tagService.disableTag(id);
            if (disabled) {
                return ApiResponse.success();
            } else {
                return ApiResponse.error("禁用标签失败");
            }

        } catch (Exception e) {
            log.error("禁用标签异常：{}", e.getMessage(), e);
            return ApiResponse.error("禁用标签失败");
        }
    }

    /**
     * 检查标签名称是否存在
     * 
     * @param name      标签名称
     * @param excludeId 排除的标签ID（更新时使用）
     * @return 检查结果
     */
    @GetMapping("/check-name")
    public ApiResponse<Boolean> checkTagName(@RequestParam("name") String name,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        try {
            log.info("检查标签名称请求：名称={}, 排除ID={}", name, excludeId);

            // 验证标签名称
            if (name == null || name.trim().isEmpty()) {
                return ApiResponse.error("标签名称不能为空");
            }

            Boolean exists = tagService.checkTagName(name, excludeId);
            return ApiResponse.success(exists);

        } catch (Exception e) {
            log.error("检查标签名称异常：{}", e.getMessage(), e);
            return ApiResponse.error("检查标签名称失败");
        }
    }

    /**
     * 获取标签统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getTagStatistics() {
        try {
            log.info("获取标签统计信息请求");

            Map<String, Object> statistics = tagService.getTagStatistics();
            return ApiResponse.success(statistics);

        } catch (Exception e) {
            log.error("获取标签统计信息异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取标签统计信息失败");
        }
    }
}