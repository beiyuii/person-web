package pw.pj.service;

import pw.pj.POJO.DO.TbSystemConfig;
import pw.pj.POJO.VO.SystemConfigCreateVO;
import pw.pj.POJO.VO.SystemConfigUpdateVO;
import pw.pj.POJO.VO.SystemConfigVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 * 提供系统配置的增删改查、缓存管理、热更新等功能
 * 
 * @author PersonWeb开发团队
 * @description 针对表【tb_system_config(系统配置表)】的数据库操作Service
 * @createDate 2025-07-01 16:43:56
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbSystemConfigService extends IService<TbSystemConfig> {

    // ==================== 配置CRUD操作 ====================

    /**
     * 创建系统配置
     * 
     * @param createVO 配置创建信息
     * @return 创建结果
     */
    Boolean createConfig(SystemConfigCreateVO createVO);

    /**
     * 更新系统配置
     * 
     * @param configId 配置ID
     * @param updateVO 配置更新信息
     * @return 更新结果
     */
    Boolean updateConfig(Long configId, SystemConfigUpdateVO updateVO);

    /**
     * 删除系统配置
     * 
     * @param configId 配置ID
     * @return 删除结果
     */
    Boolean deleteConfig(Long configId);

    /**
     * 批量删除系统配置
     * 
     * @param configIds 配置ID列表
     * @return 删除结果
     */
    Boolean batchDeleteConfigs(List<Long> configIds);

    // ==================== 配置查询操作 ====================

    /**
     * 根据配置ID获取配置信息
     * 
     * @param configId 配置ID
     * @return 配置信息VO
     */
    SystemConfigVO getConfigById(Long configId);

    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置键获取配置值并转换为指定类型
     * 
     * @param configKey 配置键
     * @param clazz     目标类型
     * @param <T>       类型参数
     * @return 转换后的配置值
     */
    <T> T getConfigValue(String configKey, Class<T> clazz);

    /**
     * 根据配置键获取配置值并转换为指定类型（带默认值）
     * 
     * @param configKey    配置键
     * @param clazz        目标类型
     * @param defaultValue 默认值
     * @param <T>          类型参数
     * @return 转换后的配置值
     */
    <T> T getConfigValue(String configKey, Class<T> clazz, T defaultValue);

    /**
     * 根据配置分组获取配置列表
     * 
     * @param configGroup 配置分组
     * @return 配置列表
     */
    List<SystemConfigVO> getConfigsByGroup(String configGroup);

    /**
     * 获取所有配置（以Map形式返回）
     * 
     * @return 配置键值对Map
     */
    Map<String, String> getAllConfigs();

    /**
     * 根据配置分组获取配置Map
     * 
     * @param configGroup 配置分组
     * @return 配置键值对Map
     */
    Map<String, String> getConfigMapByGroup(String configGroup);

    // ==================== 配置分页查询 ====================

    /**
     * 分页查询系统配置
     * 
     * @param pageQueryVO 分页查询条件
     * @return 分页结果
     */
    PageResult<SystemConfigVO> getConfigList(PageQueryVO pageQueryVO);

    /**
     * 根据配置分组分页查询
     * 
     * @param configGroup 配置分组
     * @param pageQueryVO 分页查询条件
     * @return 分页结果
     */
    PageResult<SystemConfigVO> getConfigListByGroup(String configGroup, PageQueryVO pageQueryVO);

    // ==================== 配置缓存管理 ====================

    /**
     * 刷新配置缓存
     * 
     * @return 刷新结果
     */
    Boolean refreshConfigCache();

    /**
     * 刷新指定配置键的缓存
     * 
     * @param configKey 配置键
     * @return 刷新结果
     */
    Boolean refreshConfigCache(String configKey);

    /**
     * 刷新指定配置分组的缓存
     * 
     * @param configGroup 配置分组
     * @return 刷新结果
     */
    Boolean refreshConfigCacheByGroup(String configGroup);

    /**
     * 清除所有配置缓存
     * 
     * @return 清除结果
     */
    Boolean clearConfigCache();

    /**
     * 预热配置缓存
     * 
     * @return 预热结果
     */
    Boolean warmUpConfigCache();

    // ==================== 配置热更新 ====================

    /**
     * 更新配置值（支持热更新）
     * 
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 更新结果
     */
    Boolean updateConfigValue(String configKey, String configValue);

    /**
     * 批量更新配置值
     * 
     * @param configMap 配置键值对Map
     * @return 更新结果
     */
    Boolean batchUpdateConfigValues(Map<String, String> configMap);

    /**
     * 重新加载配置
     * 
     * @return 重新加载结果
     */
    Boolean reloadConfigs();

    // ==================== 配置验证和校验 ====================

    /**
     * 验证配置键是否存在
     * 
     * @param configKey 配置键
     * @return 是否存在
     */
    Boolean existsConfigKey(String configKey);

    /**
     * 验证配置键是否存在（排除指定ID）
     * 
     * @param configKey 配置键
     * @param excludeId 排除的配置ID
     * @return 是否存在
     */
    Boolean existsConfigKey(String configKey, Long excludeId);

    /**
     * 验证配置值格式
     * 
     * @param configType  配置类型
     * @param configValue 配置值
     * @return 验证结果
     */
    Boolean validateConfigValue(Integer configType, String configValue);

    // ==================== 配置分组管理 ====================

    /**
     * 获取所有配置分组
     * 
     * @return 配置分组列表
     */
    List<String> getAllConfigGroups();

    /**
     * 获取配置分组统计信息
     * 
     * @return 分组统计Map（分组名->配置数量）
     */
    Map<String, Long> getConfigGroupStatistics();

    /**
     * 创建新的配置分组
     * 
     * @param configGroup 配置分组名
     * @param description 分组描述
     * @return 创建结果
     */
    Boolean createConfigGroup(String configGroup, String description);

    // ==================== 配置导入导出 ====================

    /**
     * 导出配置数据
     * 
     * @param configGroup 配置分组（可选）
     * @return 配置数据列表
     */
    List<SystemConfigVO> exportConfigs(String configGroup);

    /**
     * 导入配置数据
     * 
     * @param configs   配置数据列表
     * @param overwrite 是否覆盖现有配置
     * @return 导入结果
     */
    Boolean importConfigs(List<SystemConfigCreateVO> configs, Boolean overwrite);

    // ==================== 配置状态管理 ====================

    /**
     * 启用/禁用配置
     * 
     * @param configId 配置ID
     * @param status   状态：0-禁用，1-启用
     * @return 操作结果
     */
    Boolean updateConfigStatus(Long configId, Integer status);

    /**
     * 批量更新配置状态
     * 
     * @param configIds 配置ID列表
     * @param status    状态
     * @return 是否成功
     */
    Boolean batchUpdateConfigStatus(List<Long> configIds, Integer status);

    // ==================== 数据转换方法 ====================

    /**
     * DO转VO
     * 
     * @param config 配置对象
     * @return VO对象
     */
    SystemConfigVO convertToVO(TbSystemConfig config);

    /**
     * DO列表转VO列表
     * 
     * @param configs 配置列表
     * @return VO列表
     */
    List<SystemConfigVO> convertToVOList(List<TbSystemConfig> configs);
}
