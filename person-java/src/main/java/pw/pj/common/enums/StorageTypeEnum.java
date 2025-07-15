package pw.pj.common.enums;

/**
 * 存储类型枚举
 * 定义系统支持的文件存储策略类型
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum StorageTypeEnum {

    /**
     * 七牛云存储
     * 生产环境推荐，支持CDN加速
     */
    QINIU("qiniu", "七牛云存储", "https://www.qiniu.com/"),

    /**
     * 本地磁盘存储
     * 开发环境使用，小规模部署
     */
    LOCAL("local", "本地磁盘存储", "file://"),

    /**
     * 阿里云OSS存储
     * 未来扩展，企业级对象存储
     */
    ALIYUN_OSS("aliyun", "阿里云OSS存储", "https://www.aliyun.com/product/oss"),

    /**
     * 腾讯云COS存储
     * 未来扩展，企业级对象存储
     */
    TENCENT_COS("tencent", "腾讯云COS存储", "https://cloud.tencent.com/product/cos"),

    /**
     * 华为云OBS存储
     * 未来扩展，企业级对象存储
     */
    HUAWEI_OBS("huawei", "华为云OBS存储", "https://www.huaweicloud.com/product/obs.html");

    /**
     * 存储类型标识
     */
    private final String code;

    /**
     * 存储类型显示名称
     */
    private final String displayName;

    /**
     * 存储服务官网地址
     */
    private final String officialSite;

    /**
     * 构造方法
     * 
     * @param code         存储类型标识
     * @param displayName  显示名称
     * @param officialSite 官网地址
     */
    StorageTypeEnum(String code, String displayName, String officialSite) {
        this.code = code;
        this.displayName = displayName;
        this.officialSite = officialSite;
    }

    /**
     * 根据code获取存储类型
     * 
     * @param code 存储类型标识
     * @return StorageTypeEnum 匹配的存储类型，未找到返回null
     */
    public static StorageTypeEnum getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        for (StorageTypeEnum storageType : values()) {
            if (storageType.getCode().equalsIgnoreCase(code.trim())) {
                return storageType;
            }
        }
        return null;
    }

    /**
     * 判断是否为云存储类型
     * 
     * @return boolean true-云存储，false-本地存储
     */
    public boolean isCloudStorage() {
        return this != LOCAL;
    }

    /**
     * 判断是否为生产环境推荐的存储类型
     * 
     * @return boolean true-生产环境推荐，false-不推荐生产环境使用
     */
    public boolean isProductionRecommended() {
        return this == QINIU || this == ALIYUN_OSS || this == TENCENT_COS || this == HUAWEI_OBS;
    }

    /**
     * 获取存储类型的详细描述
     * 
     * @return String 详细描述
     */
    public String getDescription() {
        return String.format("%s (%s)", displayName, code);
    }

    // Getter methods

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOfficialSite() {
        return officialSite;
    }

    @Override
    public String toString() {
        return String.format("StorageTypeEnum{code='%s', displayName='%s'}", code, displayName);
    }
}