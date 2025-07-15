package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import pw.pj.POJO.DO.TbSystemConfig;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.SystemConfigCreateVO;
import pw.pj.POJO.VO.SystemConfigUpdateVO;
import pw.pj.POJO.VO.SystemConfigVO;
import pw.pj.common.result.PageResult;
import pw.pj.common.utils.StringUtils;
import pw.pj.mapper.TbSystemConfigMapper;
import pw.pj.service.TbSystemConfigService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbSystemConfigServiceImpl extends ServiceImpl<TbSystemConfigMapper, TbSystemConfig>
        implements TbSystemConfigService {

    // ==================== 配置CRUD操作 ====================

    @Override
    public Boolean createConfig(SystemConfigCreateVO createVO) {
        log.info("创建系统配置，配置键：{}", createVO.getConfigKey());

        TbSystemConfig config = new TbSystemConfig();
        config.setConfigKey(createVO.getConfigKey());
        config.setConfigValue(createVO.getConfigValue());
        config.setConfigName(createVO.getConfigName());
        config.setConfigDesc(createVO.getConfigDescription());
        config.setConfigGroup(createVO.getCategory());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setIsDelete(0);
        config.setStatus(1);
        config.setSortOrder(createVO.getSortOrder() != null ? createVO.getSortOrder() : 0);

        return save(config);
    }

    @Override
    public Boolean updateConfig(Long configId, SystemConfigUpdateVO updateVO) {
        log.info("更新系统配置，配置ID：{}", configId);

        TbSystemConfig config = getById(configId);
        if (config == null) {
            return false;
        }

        if (updateVO.getConfigValue() != null) {
            config.setConfigValue(updateVO.getConfigValue());
        }
        if (updateVO.getConfigName() != null) {
            config.setConfigName(updateVO.getConfigName());
        }
        config.setUpdateTime(new Date());

        return updateById(config);
    }

    @Override
    public Boolean deleteConfig(Long configId) {
        return removeById(configId);
    }

    @Override
    public Boolean batchDeleteConfigs(List<Long> configIds) {
        return removeByIds(configIds);
    }

    // ==================== 配置查询操作 ====================

    @Override
    public SystemConfigVO getConfigById(Long configId) {
        TbSystemConfig config = getById(configId);
        return config != null ? convertToVO(config) : null;
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValueWithDefault(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        return getConfigValueWithDefault(configKey, defaultValue);
    }

    private String getConfigValueWithDefault(String configKey, String defaultValue) {
        if (StringUtils.isBlank(configKey)) {
            return defaultValue;
        }

        TbSystemConfig config = lambdaQuery()
                .eq(TbSystemConfig::getConfigKey, configKey)
                .eq(TbSystemConfig::getIsDelete, 0)
                .one();

        return config != null ? config.getConfigValue() : defaultValue;
    }

    @Override
    public <T> T getConfigValue(String configKey, Class<T> clazz) {
        return getConfigValue(configKey, clazz, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String configKey, Class<T> clazz, T defaultValue) {
        String value = getConfigValueWithDefault(configKey, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            if (clazz == String.class) {
                return (T) value;
            } else if (clazz == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (clazz == Boolean.class) {
                return (T) Boolean.valueOf(value);
            }
        } catch (Exception e) {
            log.error("配置值类型转换失败：{}", e.getMessage());
        }
        return defaultValue;
    }

    @Override
    public List<SystemConfigVO> getConfigsByGroup(String configGroup) {
        List<TbSystemConfig> configs = lambdaQuery()
                .eq(TbSystemConfig::getConfigGroup, configGroup)
                .eq(TbSystemConfig::getIsDelete, 0)
                .list();
        return convertToVOList(configs);
    }

    @Override
    public Map<String, String> getAllConfigs() {
        List<TbSystemConfig> configs = lambdaQuery()
                .eq(TbSystemConfig::getIsDelete, 0)
                .list();

        return configs.stream()
                .collect(Collectors.toMap(
                        TbSystemConfig::getConfigKey,
                        TbSystemConfig::getConfigValue,
                        (v1, v2) -> v2));
    }

    @Override
    public Map<String, String> getConfigMapByGroup(String configGroup) {
        List<SystemConfigVO> configs = getConfigsByGroup(configGroup);
        return configs.stream()
                .collect(Collectors.toMap(
                        SystemConfigVO::getConfigKey,
                        SystemConfigVO::getConfigValue,
                        (v1, v2) -> v2));
    }

    @Override
    public PageResult<SystemConfigVO> getConfigList(PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbSystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbSystemConfig::getIsDelete, 0);

        if (StringUtils.isNotBlank(pageQueryVO.getKeyword())) {
            queryWrapper.like(TbSystemConfig::getConfigKey, pageQueryVO.getKeyword())
                    .or().like(TbSystemConfig::getConfigName, pageQueryVO.getKeyword());
        }

        Page<TbSystemConfig> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbSystemConfig> resultPage = page(page, queryWrapper);

        List<SystemConfigVO> configVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(configVOList, resultPage.getTotal(), pageQueryVO.getPageNum(),
                pageQueryVO.getPageSize());
    }

    @Override
    public PageResult<SystemConfigVO> getConfigListByGroup(String configGroup, PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbSystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbSystemConfig::getConfigGroup, configGroup)
                .eq(TbSystemConfig::getIsDelete, 0);

        Page<TbSystemConfig> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbSystemConfig> resultPage = page(page, queryWrapper);

        List<SystemConfigVO> configVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(configVOList, resultPage.getTotal(), pageQueryVO.getPageNum(),
                pageQueryVO.getPageSize());
    }

    // ==================== 其他方法简化实现 ====================

    @Override
    public Boolean refreshConfigCache() {
        return true;
    }

    @Override
    public Boolean refreshConfigCache(String configKey) {
        return true;
    }

    @Override
    public Boolean refreshConfigCacheByGroup(String configGroup) {
        return true;
    }

    @Override
    public Boolean clearConfigCache() {
        return true;
    }

    @Override
    public Boolean warmUpConfigCache() {
        return true;
    }

    @Override
    public Boolean updateConfigValue(String configKey, String configValue) {
        TbSystemConfig config = lambdaQuery()
                .eq(TbSystemConfig::getConfigKey, configKey)
                .one();

        if (config == null) {
            return false;
        }

        config.setConfigValue(configValue);
        config.setUpdateTime(new Date());
        return updateById(config);
    }

    @Override
    public Boolean batchUpdateConfigValues(Map<String, String> configMap) {
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            updateConfigValue(entry.getKey(), entry.getValue());
        }
        return true;
    }

    @Override
    public Boolean reloadConfigs() {
        return true;
    }

    @Override
    public Boolean existsConfigKey(String configKey) {
        return existsConfigKey(configKey, null);
    }

    @Override
    public Boolean existsConfigKey(String configKey, Long excludeId) {
        LambdaQueryWrapper<TbSystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbSystemConfig::getConfigKey, configKey)
                .eq(TbSystemConfig::getIsDelete, 0);

        if (excludeId != null) {
            queryWrapper.ne(TbSystemConfig::getId, excludeId);
        }

        return count(queryWrapper) > 0;
    }

    @Override
    public Boolean validateConfigValue(Integer configType, String configValue) {
        return true;
    }

    @Override
    public List<String> getAllConfigGroups() {
        List<TbSystemConfig> configs = lambdaQuery()
                .select(TbSystemConfig::getConfigGroup)
                .eq(TbSystemConfig::getIsDelete, 0)
                .groupBy(TbSystemConfig::getConfigGroup)
                .list();

        return configs.stream()
                .map(TbSystemConfig::getConfigGroup)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getConfigGroupStatistics() {
        return new HashMap<>();
    }

    @Override
    public Boolean createConfigGroup(String configGroup, String description) {
        return true;
    }

    @Override
    public List<SystemConfigVO> exportConfigs(String configGroup) {
        return new ArrayList<>();
    }

    @Override
    public Boolean importConfigs(List<SystemConfigCreateVO> configs, Boolean overwrite) {
        return true;
    }

    @Override
    public Boolean updateConfigStatus(Long configId, Integer status) {
        return lambdaUpdate()
                .eq(TbSystemConfig::getId, configId)
                .set(TbSystemConfig::getStatus, status)
                .update();
    }

    @Override
    public Boolean batchUpdateConfigStatus(List<Long> configIds, Integer status) {
        return lambdaUpdate()
                .in(TbSystemConfig::getId, configIds)
                .set(TbSystemConfig::getStatus, status)
                .update();
    }

    @Override
    public SystemConfigVO convertToVO(TbSystemConfig config) {
        if (config == null) {
            return null;
        }

        SystemConfigVO configVO = new SystemConfigVO();
        BeanUtils.copyProperties(config, configVO);
        return configVO;
    }

    @Override
    public List<SystemConfigVO> convertToVOList(List<TbSystemConfig> configs) {
        if (ObjectUtils.isEmpty(configs)) {
            return new ArrayList<>();
        }

        return configs.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
