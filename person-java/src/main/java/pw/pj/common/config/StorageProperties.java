package pw.pj.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 存储配置属性类
 * 从application.yml中读取文件存储相关配置
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /**
     * 存储类型，支持：qiniu、local、aliyun、tencent、huawei
     */
    private String type = "local";

    /**
     * 允许上传的文件类型
     */
    private String[] allowedTypes = { "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "txt" };

    /**
     * 最大文件大小（字节）
     */
    private Long maxFileSize = 10 * 1024 * 1024L; // 10MB

    /**
     * 七牛云配置
     */
    private QiniuConfig qiniu = new QiniuConfig();

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * 阿里云OSS配置
     */
    private AliyunConfig aliyun = new AliyunConfig();

    /**
     * 七牛云存储配置
     */
    public static class QiniuConfig {
        /**
         * AccessKey
         */
        private String accessKey;

        /**
         * SecretKey
         */
        private String secretKey;

        /**
         * 存储空间名称
         */
        private String bucket;

        /**
         * CDN域名
         */
        private String domain;

        /**
         * 存储区域
         */
        private String region = "华南";

        /**
         * 是否使用HTTPS
         */
        private Boolean useHttps = true;

        /**
         * URL过期时间（秒）
         */
        private Long urlExpireSeconds = 3600L;

        // Getter and Setter methods

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Boolean getUseHttps() {
            return useHttps;
        }

        public void setUseHttps(Boolean useHttps) {
            this.useHttps = useHttps;
        }

        public Long getUrlExpireSeconds() {
            return urlExpireSeconds;
        }

        public void setUrlExpireSeconds(Long urlExpireSeconds) {
            this.urlExpireSeconds = urlExpireSeconds;
        }
    }

    /**
     * 本地存储配置
     */
    public static class LocalConfig {
        /**
         * 文件存储根目录
         */
        private String rootPath = "./uploads";

        /**
         * 访问URL前缀
         */
        private String urlPrefix = "/uploads";

        /**
         * 是否创建日期目录
         */
        private Boolean createDateDir = true;

        // Getter and Setter methods

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public String getUrlPrefix() {
            return urlPrefix;
        }

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = urlPrefix;
        }

        public Boolean getCreateDateDir() {
            return createDateDir;
        }

        public void setCreateDateDir(Boolean createDateDir) {
            this.createDateDir = createDateDir;
        }
    }

    /**
     * 阿里云OSS配置
     */
    public static class AliyunConfig {
        /**
         * AccessKey ID
         */
        private String accessKeyId;

        /**
         * AccessKey Secret
         */
        private String accessKeySecret;

        /**
         * 存储空间名称
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String endpoint;

        /**
         * 自定义域名
         */
        private String customDomain;

        // Getter and Setter methods

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getCustomDomain() {
            return customDomain;
        }

        public void setCustomDomain(String customDomain) {
            this.customDomain = customDomain;
        }
    }

    // 主配置类的 Getter and Setter methods

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String[] allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public QiniuConfig getQiniu() {
        return qiniu;
    }

    public void setQiniu(QiniuConfig qiniu) {
        this.qiniu = qiniu;
    }

    public LocalConfig getLocal() {
        return local;
    }

    public void setLocal(LocalConfig local) {
        this.local = local;
    }

    public AliyunConfig getAliyun() {
        return aliyun;
    }

    public void setAliyun(AliyunConfig aliyun) {
        this.aliyun = aliyun;
    }

    /**
     * 验证配置是否完整
     * 
     * @return boolean true-配置完整，false-配置不完整
     */
    public boolean isValid() {
        if (type == null || type.trim().isEmpty()) {
            return false;
        }

        switch (type.toLowerCase()) {
            case "qiniu":
                return qiniu.getAccessKey() != null && !qiniu.getAccessKey().trim().isEmpty()
                        && qiniu.getSecretKey() != null && !qiniu.getSecretKey().trim().isEmpty()
                        && qiniu.getBucket() != null && !qiniu.getBucket().trim().isEmpty()
                        && qiniu.getDomain() != null && !qiniu.getDomain().trim().isEmpty();
            case "local":
                return local.getRootPath() != null && !local.getRootPath().trim().isEmpty();
            case "aliyun":
                return aliyun.getAccessKeyId() != null && !aliyun.getAccessKeyId().trim().isEmpty()
                        && aliyun.getAccessKeySecret() != null && !aliyun.getAccessKeySecret().trim().isEmpty()
                        && aliyun.getBucketName() != null && !aliyun.getBucketName().trim().isEmpty()
                        && aliyun.getEndpoint() != null && !aliyun.getEndpoint().trim().isEmpty();
            default:
                return false;
        }
    }

    /**
     * 获取配置摘要信息
     * 
     * @return String 配置摘要
     */
    public String getSummary() {
        return String.format("StorageProperties{type='%s', maxFileSize=%d, allowedTypes=%d}",
                type, maxFileSize, allowedTypes != null ? allowedTypes.length : 0);
    }
}