package pw.pj.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储配置类
 * 提供存储配置验证和监控功能
 * 
 * <p>
 * 支持的存储策略：
 * </p>
 * <ul>
 * <li>storage.type=qiniu - 七牛云存储（生产环境推荐）</li>
 * <li>storage.type=local - 本地磁盘存储（开发环境推荐）</li>
 * </ul>
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
public class FileStorageConfig {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageConfig.class);

    private final StorageProperties storageProperties;

    /**
     * 构造注入存储配置属性
     * 
     * @param storageProperties 存储配置属性
     */
    @Autowired
    public FileStorageConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        logger.info("FileStorageConfig初始化完成，当前存储类型: {}", storageProperties.getType());
    }

    /**
     * 获取当前存储配置信息
     * 用于健康检查和监控
     * 
     * @return String 配置信息
     */
    public String getStorageConfigInfo() {
        StringBuilder info = new StringBuilder();
        info.append("当前存储配置: ").append(storageProperties.getType());
        info.append(", 最大文件大小: ").append(storageProperties.getMaxFileSize() / 1024 / 1024).append("MB");
        info.append(", 允许的文件类型: ").append(String.join(",", storageProperties.getAllowedTypes()));

        switch (storageProperties.getType().toLowerCase()) {
            case "qiniu":
                info.append(", 七牛云域名: ").append(storageProperties.getQiniu().getDomain());
                break;
            case "local":
                info.append(", 本地根目录: ").append(storageProperties.getLocal().getRootPath());
                break;
        }

        return info.toString();
    }

    /**
     * 检查当前存储配置是否有效
     * 
     * @return boolean true-配置有效，false-配置无效
     */
    public boolean isStorageConfigValid() {
        try {
            switch (storageProperties.getType().toLowerCase()) {
                case "qiniu":
                    validateQiniuConfig();
                    return true;
                case "local":
                    validateLocalConfig();
                    return true;
                default:
                    logger.error("不支持的存储类型: {}", storageProperties.getType());
                    return false;
            }
        } catch (Exception e) {
            logger.error("存储配置验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证七牛云配置
     */
    private void validateQiniuConfig() {
        StorageProperties.QiniuConfig qiniuConfig = storageProperties.getQiniu();

        if (qiniuConfig.getAccessKey() == null || qiniuConfig.getAccessKey().trim().isEmpty()) {
            throw new IllegalStateException("七牛云AccessKey未配置，请检查storage.qiniu.access-key配置项");
        }

        if (qiniuConfig.getSecretKey() == null || qiniuConfig.getSecretKey().trim().isEmpty()) {
            throw new IllegalStateException("七牛云SecretKey未配置，请检查storage.qiniu.secret-key配置项");
        }

        if (qiniuConfig.getBucket() == null || qiniuConfig.getBucket().trim().isEmpty()) {
            throw new IllegalStateException("七牛云Bucket未配置，请检查storage.qiniu.bucket配置项");
        }

        if (qiniuConfig.getDomain() == null || qiniuConfig.getDomain().trim().isEmpty()) {
            throw new IllegalStateException("七牛云Domain未配置，请检查storage.qiniu.domain配置项");
        }

        logger.info("七牛云配置验证通过: bucket={}, domain={}, region={}",
                qiniuConfig.getBucket(), qiniuConfig.getDomain(), qiniuConfig.getRegion());
    }

    /**
     * 验证本地存储配置
     */
    private void validateLocalConfig() {
        StorageProperties.LocalConfig localConfig = storageProperties.getLocal();

        if (localConfig.getRootPath() == null || localConfig.getRootPath().trim().isEmpty()) {
            throw new IllegalStateException("本地存储根目录未配置，请检查storage.local.root-path配置项");
        }

        if (localConfig.getUrlPrefix() == null || localConfig.getUrlPrefix().trim().isEmpty()) {
            logger.warn("本地存储URL前缀未配置，将使用默认值: /uploads");
            localConfig.setUrlPrefix("/uploads");
        }

        logger.info("本地存储配置验证通过: rootPath={}, urlPrefix={}, createDateDir={}",
                localConfig.getRootPath(), localConfig.getUrlPrefix(), localConfig.getCreateDateDir());
    }
}