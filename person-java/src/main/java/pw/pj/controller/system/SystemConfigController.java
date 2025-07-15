package pw.pj.controller.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pw.pj.POJO.VO.SystemConfigCreateVO;
import pw.pj.POJO.VO.SystemConfigUpdateVO;
import pw.pj.POJO.VO.SystemConfigVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.service.TbSystemConfigService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器
 * 提供系统配置的增删改查、缓存管理等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Api(tags = "系统配置")
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Validated
public class SystemConfigController {

    @Autowired
    private TbSystemConfigService systemConfigService;

    /**
     * 创建系统配置
     * 
     * @param createVO 配置创建参数
     * @return 配置信息
     */
    @ApiOperation("创建系统配置")
    @PostMapping
    public ApiResponse<SystemConfigVO> createConfig(
            @Valid @RequestBody SystemConfigCreateVO createVO) {
        log.info("创建系统配置: {}", createVO);

        try {
            // 调用服务层创建配置
            Boolean result = systemConfigService.createConfig(createVO);

            if (result) {
                // 创建成功后，获取配置详情返回
                SystemConfigVO configVO = systemConfigService.getConfigById(null); // 需要获取新创建的ID
                log.info("系统配置创建成功: configKey={}", createVO.getConfigKey());
                return ApiResponse.success("配置创建成功", configVO);
            } else {
                return ApiResponse.error("配置创建失败");
            }

        } catch (Exception e) {
            log.error("创建系统配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("配置创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统配置
     * 
     * @param id       配置ID
     * @param updateVO 配置更新参数
     * @return 更新后的配置信息
     */
    @ApiOperation("更新系统配置")
    @PutMapping("/{id}")
    public ApiResponse<SystemConfigVO> updateConfig(
            @ApiParam(value = "配置ID", required = true) @PathVariable @NotNull Long id,
            @Valid @RequestBody SystemConfigUpdateVO updateVO) {
        log.info("更新系统配置: configId={}, updateVO={}", id, updateVO);

        try {
            // 调用服务层更新配置
            Boolean result = systemConfigService.updateConfig(id, updateVO);

            if (result) {
                // 更新成功后，获取最新配置详情返回
                SystemConfigVO configVO = systemConfigService.getConfigById(id);
                log.info("系统配置更新成功: configId={}", id);
                return ApiResponse.success("配置更新成功", configVO);
            } else {
                return ApiResponse.error("配置不存在或更新失败");
            }

        } catch (Exception e) {
            log.error("更新系统配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("配置更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统配置详情
     * 
     * @param id 配置ID
     * @return 配置详情
     */
    @ApiOperation("获取系统配置详情")
    @GetMapping("/{id}")
    public ApiResponse<SystemConfigVO> getConfig(
            @ApiParam(value = "配置ID", required = true) @PathVariable @NotNull Long id) {
        log.info("获取系统配置详情: configId={}", id);

        try {
            // 调用服务层获取配置详情
            SystemConfigVO configVO = systemConfigService.getConfigById(id);

            if (configVO != null) {
                log.info("获取系统配置详情成功: configId={}, configKey={}", id, configVO.getConfigKey());
                return ApiResponse.success(configVO);
            } else {
                return ApiResponse.error("配置不存在");
            }

        } catch (Exception e) {
            log.error("获取系统配置详情失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取配置详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    @ApiOperation("根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public ApiResponse<String> getConfigValue(
            @ApiParam(value = "配置键", required = true) @PathVariable @NotNull String configKey) {
        log.info("根据配置键获取配置值: configKey={}", configKey);

        try {
            // 调用服务层获取配置值
            String configValue = systemConfigService.getConfigValue(configKey);

            if (configValue != null) {
                log.info("获取配置值成功: configKey={}", configKey);
                return ApiResponse.success(configValue);
            } else {
                return ApiResponse.error("配置不存在");
            }

        } catch (Exception e) {
            log.error("获取配置值失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取配置值失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置键更新配置值
     * 
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 更新结果
     */
    @ApiOperation("根据配置键更新配置值")
    @PutMapping("/value/{configKey}")
    public ApiResponse<Void> updateConfigValue(
            @ApiParam(value = "配置键", required = true) @PathVariable @NotNull String configKey,
            @ApiParam(value = "配置值", required = true) @RequestParam @NotNull String configValue) {
        log.info("根据配置键更新配置值: configKey={}, configValue={}", configKey, configValue);

        try {
            // 调用服务层更新配置值
            Boolean result = systemConfigService.updateConfigValue(configKey, configValue);

            if (result) {
                log.info("配置值更新成功: configKey={}", configKey);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("配置不存在或更新失败");
            }

        } catch (Exception e) {
            log.error("更新配置值失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新配置值失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询系统配置
     * 
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键词
     * @param category 配置分类
     * @return 配置列表
     */
    @ApiOperation("分页查询系统配置")
    @GetMapping("/page")
    public ApiResponse<PageResult<SystemConfigVO>> pageConfigs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        log.info("分页查询系统配置: pageNum={}, pageSize={}, keyword={}, category={}", pageNum, pageSize, keyword, category);

        try {
            // 构建查询参数
            PageQueryVO pageQueryVO = new PageQueryVO();
            pageQueryVO.setPageNum(pageNum);
            pageQueryVO.setPageSize(pageSize);
            pageQueryVO.setKeyword(keyword);

            PageResult<SystemConfigVO> configList;

            // 根据是否有分类选择不同的查询方法
            if (category != null && !category.trim().isEmpty()) {
                configList = systemConfigService.getConfigListByGroup(category, pageQueryVO);
            } else {
                configList = systemConfigService.getConfigList(pageQueryVO);
            }

            log.info("分页查询系统配置成功: total={}", configList.getTotal());
            return ApiResponse.success(configList);

        } catch (Exception e) {
            log.error("分页查询系统配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有配置分类
     * 
     * @return 配置分类列表
     */
    @ApiOperation("获取所有配置分类")
    @GetMapping("/categories")
    public ApiResponse<List<String>> getConfigCategories() {
        log.info("获取所有配置分类");

        try {
            // 调用服务层获取配置分类
            List<String> categories = systemConfigService.getAllConfigGroups();

            log.info("获取配置分类成功: count={}", categories.size());
            return ApiResponse.success(categories);

        } catch (Exception e) {
            log.error("获取配置分类失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取配置分类失败: " + e.getMessage());
        }
    }

    /**
     * 根据分类获取配置列表
     * 
     * @param category 配置分类
     * @return 配置列表
     */
    @ApiOperation("根据分类获取配置列表")
    @GetMapping("/category/{category}")
    public ApiResponse<List<SystemConfigVO>> getConfigsByCategory(
            @ApiParam(value = "配置分类", required = true) @PathVariable @NotNull String category) {
        log.info("根据分类获取配置列表: category={}", category);

        try {
            // 调用服务层根据分类获取配置
            List<SystemConfigVO> configs = systemConfigService.getConfigsByGroup(category);

            log.info("根据分类获取配置列表成功: category={}, count={}", category, configs.size());
            return ApiResponse.success(configs);

        } catch (Exception e) {
            log.error("根据分类获取配置列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取配置列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新配置
     * 
     * @param configMap 配置键值对
     * @return 更新结果
     */
    @ApiOperation("批量更新配置")
    @PostMapping("/batch")
    public ApiResponse<Void> batchUpdateConfigs(
            @ApiParam(value = "配置键值对", required = true) @RequestBody @NotNull Map<String, String> configMap) {
        log.info("批量更新配置: configCount={}", configMap.size());

        try {
            // 调用服务层批量更新配置
            Boolean result = systemConfigService.batchUpdateConfigValues(configMap);

            if (result) {
                log.info("批量更新配置成功: count={}", configMap.size());
                return ApiResponse.success();
            } else {
                return ApiResponse.error("批量更新失败");
            }

        } catch (Exception e) {
            log.error("批量更新配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除系统配置
     * 
     * @param id 配置ID
     * @return 删除结果
     */
    @ApiOperation("删除系统配置")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteConfig(
            @ApiParam(value = "配置ID", required = true) @PathVariable @NotNull Long id) {
        log.info("删除系统配置: configId={}", id);

        try {
            // 调用服务层删除配置
            Boolean result = systemConfigService.deleteConfig(id);

            if (result) {
                log.info("系统配置删除成功: configId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("配置不存在或删除失败");
            }

        } catch (Exception e) {
            log.error("删除系统配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("删除配置失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除系统配置
     * 
     * @param ids 配置ID列表
     * @return 删除结果
     */
    @ApiOperation("批量删除系统配置")
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteConfigs(
            @ApiParam(value = "配置ID列表", required = true) @RequestBody @NotNull List<Long> ids) {
        log.info("批量删除系统配置: configIds={}", ids);

        try {
            // 调用服务层批量删除配置
            Boolean result = systemConfigService.batchDeleteConfigs(ids);

            if (result) {
                log.info("批量删除系统配置成功: count={}", ids.size());
                return ApiResponse.success();
            } else {
                return ApiResponse.error("批量删除失败");
            }

        } catch (Exception e) {
            log.error("批量删除系统配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有配置（以Map形式返回）
     * 
     * @return 配置键值对Map
     */
    @ApiOperation("获取所有配置")
    @GetMapping("/all")
    public ApiResponse<Map<String, String>> getAllConfigs() {
        log.info("获取所有配置");

        try {
            // 调用服务层获取所有配置
            Map<String, String> allConfigs = systemConfigService.getAllConfigs();

            log.info("获取所有配置成功: count={}", allConfigs.size());
            return ApiResponse.success(allConfigs);

        } catch (Exception e) {
            log.error("获取所有配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取所有配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置分组获取配置Map
     * 
     * @param configGroup 配置分组
     * @return 配置键值对Map
     */
    @ApiOperation("根据配置分组获取配置Map")
    @GetMapping("/group/{configGroup}/map")
    public ApiResponse<Map<String, String>> getConfigMapByGroup(
            @ApiParam(value = "配置分组", required = true) @PathVariable @NotNull String configGroup) {
        log.info("根据配置分组获取配置Map: configGroup={}", configGroup);

        try {
            // 调用服务层根据分组获取配置Map
            Map<String, String> configMap = systemConfigService.getConfigMapByGroup(configGroup);

            log.info("根据配置分组获取配置Map成功: configGroup={}, count={}", configGroup, configMap.size());
            return ApiResponse.success(configMap);

        } catch (Exception e) {
            log.error("根据配置分组获取配置Map失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取配置Map失败: " + e.getMessage());
        }
    }

    /**
     * 获取网站基本信息配置
     * 
     * @return 网站基本信息
     */
    @ApiOperation("获取网站基本信息配置")
    @GetMapping("/website/info")
    public ApiResponse<Map<String, String>> getWebsiteInfo() {
        log.info("获取网站基本信息配置");

        try {
            // 调用服务层获取网站配置分组
            Map<String, String> websiteInfo = systemConfigService.getConfigMapByGroup("website");

            log.info("获取网站基本信息成功: count={}", websiteInfo.size());
            return ApiResponse.success(websiteInfo);

        } catch (Exception e) {
            log.error("获取网站基本信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取网站基本信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新网站基本信息配置
     * 
     * @param websiteInfo 网站基本信息
     * @return 更新结果
     */
    @ApiOperation("更新网站基本信息配置")
    @PutMapping("/website/info")
    public ApiResponse<Void> updateWebsiteInfo(
            @ApiParam(value = "网站基本信息", required = true) @RequestBody @NotNull Map<String, String> websiteInfo) {
        log.info("更新网站基本信息配置: {}", websiteInfo);

        try {
            // 调用服务层批量更新配置值
            Boolean result = systemConfigService.batchUpdateConfigValues(websiteInfo);

            if (result) {
                log.info("网站基本信息更新成功");
                return ApiResponse.success();
            } else {
                return ApiResponse.error("网站信息更新失败");
            }

        } catch (Exception e) {
            log.error("更新网站基本信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新网站信息失败: " + e.getMessage());
        }
    }

    /**
     * 刷新配置缓存
     * 
     * @return 刷新结果
     */
    @ApiOperation("刷新配置缓存")
    @PostMapping("/cache/refresh")
    public ApiResponse<Void> refreshConfigCache() {
        log.info("刷新配置缓存");

        try {
            // 调用服务层刷新缓存
            Boolean result = systemConfigService.refreshConfigCache();

            if (result) {
                log.info("配置缓存刷新成功");
                return ApiResponse.success();
            } else {
                return ApiResponse.error("缓存刷新失败");
            }

        } catch (Exception e) {
            log.error("刷新配置缓存失败: {}", e.getMessage(), e);
            return ApiResponse.error("缓存刷新失败: " + e.getMessage());
        }
    }

    /**
     * 清理配置缓存
     * 
     * @return 清理结果
     */
    @ApiOperation("清理配置缓存")
    @PostMapping("/cache/clear")
    public ApiResponse<Void> clearConfigCache() {
        log.info("清理配置缓存");

        try {
            // 调用服务层清理缓存
            Boolean result = systemConfigService.clearConfigCache();

            if (result) {
                log.info("配置缓存清理成功");
                return ApiResponse.success();
            } else {
                return ApiResponse.error("缓存清理失败");
            }

        } catch (Exception e) {
            log.error("清理配置缓存失败: {}", e.getMessage(), e);
            return ApiResponse.error("缓存清理失败: " + e.getMessage());
        }
    }

    /**
     * 更新配置状态
     * 
     * @param id     配置ID
     * @param status 状态：0-禁用，1-启用
     * @return 更新结果
     */
    @ApiOperation("更新配置状态")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateConfigStatus(
            @ApiParam(value = "配置ID", required = true) @PathVariable @NotNull Long id,
            @ApiParam(value = "状态", required = true) @RequestParam @NotNull Integer status) {
        log.info("更新配置状态: configId={}, status={}", id, status);

        try {
            // 调用服务层更新配置状态
            Boolean result = systemConfigService.updateConfigStatus(id, status);

            if (result) {
                log.info("配置状态更新成功: configId={}, status={}", id, status);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("配置状态更新失败");
            }

        } catch (Exception e) {
            log.error("更新配置状态失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新配置状态失败: " + e.getMessage());
        }
    }

    /**
     * 验证配置键是否存在
     * 
     * @param configKey 配置键
     * @return 验证结果
     */
    @ApiOperation("验证配置键是否存在")
    @GetMapping("/validate/{configKey}")
    public ApiResponse<Boolean> validateConfigKey(
            @ApiParam(value = "配置键", required = true) @PathVariable @NotNull String configKey) {
        log.info("验证配置键是否存在: configKey={}", configKey);

        try {
            // 调用服务层验证配置键
            Boolean exists = systemConfigService.existsConfigKey(configKey);

            log.info("配置键验证完成: configKey={}, exists={}", configKey, exists);
            return ApiResponse.success(exists);

        } catch (Exception e) {
            log.error("验证配置键失败: {}", e.getMessage(), e);
            return ApiResponse.error("验证配置键失败: " + e.getMessage());
        }
    }

    /**
     * 重新加载配置
     * 
     * @return 重新加载结果
     */
    @ApiOperation("重新加载配置")
    @PostMapping("/reload")
    public ApiResponse<Void> reloadConfigs() {
        log.info("重新加载配置");

        try {
            // 调用服务层重新加载配置
            Boolean result = systemConfigService.reloadConfigs();

            if (result) {
                log.info("配置重新加载成功");
                return ApiResponse.success();
            } else {
                return ApiResponse.error("配置重新加载失败");
            }

        } catch (Exception e) {
            log.error("重新加载配置失败: {}", e.getMessage(), e);
            return ApiResponse.error("重新加载配置失败: " + e.getMessage());
        }
    }
}