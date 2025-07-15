package pw.pj.config;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 七牛云配置类
 * 根据存储空间 person-hzt 的配置信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Component
@ConfigurationProperties(prefix = "qiniu")
@Data
public class QiniuConfig {

    /**
     * 七牛云Access Key
     */
    private String accessKey;

    /**
     * 七牛云Secret Key
     */
    private String secretKey;

    /**
     * 存储空间名称
     */
    private String bucketName;

    /**
     * 存储区域
     */
    private String region;

    /**
     * CDN加速域名
     */
    private String domain;

    /**
     * S3协议访问域名
     */
    private String s3Domain;

    /**
     * 是否启用HTTPS
     */
    private Boolean useHttps;

    /**
     * 创建七牛云认证对象
     * 
     * @return Auth 认证对象
     */
    @Bean
    public Auth qiniuAuth() {
        return Auth.create(accessKey, secretKey);
    }

    /**
     * 创建七牛云配置对象
     * 根据华南-广东区域配置
     * 
     * @return Configuration 配置对象
     */
    @Bean
    public Configuration qiniuConfiguration() {
        Configuration cfg = new Configuration();

        // 根据存储区域设置Zone
        switch (region.toLowerCase()) {
            case "huadong":
                cfg.zone = Zone.zone0();
                break;
            case "huabei":
                cfg.zone = Zone.zone1();
                break;
            case "huanan":
                cfg.zone = Zone.zone2(); // 华南-广东
                break;
            case "beimei":
                cfg.zone = Zone.zoneNa0();
                break;
            case "dongnanya":
                cfg.zone = Zone.zoneAs0();
                break;
            default:
                cfg.zone = Zone.zone2(); // 默认华南
        }

        // 设置是否使用HTTPS
        cfg.useHttpsDomains = useHttps != null ? useHttps : true;

        return cfg;
    }

    /**
     * 创建七牛云上传管理器
     * 
     * @return UploadManager 上传管理器
     */
    @Bean
    public UploadManager uploadManager() {
        return new UploadManager(qiniuConfiguration());
    }

    /**
     * 创建七牛云存储管理器
     * 
     * @return BucketManager 存储管理器
     */
    @Bean
    public BucketManager bucketManager() {
        return new BucketManager(qiniuAuth(), qiniuConfiguration());
    }

    /**
     * 获取上传Token
     * 
     * @return 上传Token
     */
    public String getUploadToken() {
        return qiniuAuth().uploadToken(bucketName);
    }

    /**
     * 获取上传Token（带过期时间）
     * 
     * @param expires 过期时间（秒）
     * @return 上传Token
     */
    public String getUploadToken(long expires) {
        return qiniuAuth().uploadToken(bucketName, null, expires, null);
    }

    /**
     * 获取完整的文件访问URL
     * 
     * @param fileName 文件名
     * @return 完整URL
     */
    public String getFileUrl(String fileName) {
        if (domain != null && !domain.isEmpty()) {
            String protocol = useHttps ? "https://" : "http://";
            return protocol + domain + "/" + fileName;
        }
        return null;
    }

    /**
     * 获取S3协议访问URL
     * 
     * @param fileName 文件名
     * @return S3 URL
     */
    public String getS3Url(String fileName) {
        if (s3Domain != null && !s3Domain.isEmpty()) {
            String protocol = useHttps ? "https://" : "http://";
            return protocol + s3Domain + "/" + fileName;
        }
        return null;
    }
}