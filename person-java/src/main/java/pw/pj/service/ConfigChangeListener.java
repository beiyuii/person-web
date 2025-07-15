package pw.pj.service;

/**
 * 配置变更监听器接口
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@FunctionalInterface
public interface ConfigChangeListener {
    /**
     * 配置变更回调
     * 
     * @param configKey 配置键
     * @param oldValue  旧值
     * @param newValue  新值
     */
    void onConfigChange(String configKey, String oldValue, String newValue);
}