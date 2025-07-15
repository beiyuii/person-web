package pw.pj.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.VO.FileStorageResult;
import pw.pj.common.config.StorageProperties;
import pw.pj.common.enums.StorageTypeEnum;
import pw.pj.common.exception.StorageException;
import pw.pj.service.FileStorageService;
import pw.pj.common.utils.StringUtils;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 七牛云存储服务实现
 * 生产环境推荐使用，提供高性能、高可用的云存储服务
 * 
 * <p>
 * 功能特性：
 * </p>
 * <ul>
 * <li>文件上传到七牛云</li>
 * <li>支持多种存储区域</li>
 * <li>HTTPS/HTTP访问支持</li>
 * <li>文件删除操作</li>
 * <li>CDN加速访问</li>
 * <li>自动重试机制</li>
 * </ul>
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "qiniu")
public class QiniuStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(QiniuStorageServiceImpl.class);

    /**
     * 上传重试次数
     */
    private static final int UPLOAD_RETRY_COUNT = 3;

    /**
     * 文件名日期格式
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final StorageProperties storageProperties;

    /**
     * 七牛云认证对象
     */
    private Auth auth;

    /**
     * 上传管理器
     */
    private UploadManager uploadManager;

    /**
     * 空间管理器
     */
    private BucketManager bucketManager;

    /**
     * 构造注入存储配置
     * 
     * @param storageProperties 存储配置属性
     */
    @Autowired
    public QiniuStorageServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    /**
     * 初始化七牛云客户端
     */
    @PostConstruct
    public void init() {
        try {
            StorageProperties.QiniuConfig qiniuConfig = storageProperties.getQiniu();

            // 初始化认证信息
            this.auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());

            // 根据配置选择存储区域
            Region region = getRegionByName(qiniuConfig.getRegion());
            Configuration cfg = new Configuration(region);
            cfg.useHttpsDomains = qiniuConfig.getUseHttps();

            // 初始化上传管理器和空间管理器
            this.uploadManager = new UploadManager(cfg);
            this.bucketManager = new BucketManager(auth, cfg);

            logger.info("七牛云存储服务初始化成功，区域: {}, 使用HTTPS: {}",
                    qiniuConfig.getRegion(), qiniuConfig.getUseHttps());

        } catch (Exception e) {
            logger.error("七牛云存储服务初始化失败", e);
            throw new StorageException("七牛云存储服务初始化失败: " + e.getMessage(), e);
        }
    }

    @Override
    public FileStorageResult upload(MultipartFile file, String path) {
        if (file == null || file.isEmpty()) {
            return FileStorageResult.failure("文件不能为空", StorageTypeEnum.QINIU);
        }

        // 验证文件类型和大小
        String validationError = validateFile(file);
        if (validationError != null) {
            return FileStorageResult.failure(validationError, StorageTypeEnum.QINIU);
        }

        String fileKey = null;
        try {
            // 生成文件Key
            fileKey = generateFileKey(file.getOriginalFilename(), path);

            // 计算文件MD5
            String md5Hash = calculateMD5(file.getBytes());

            // 执行上传（带重试）
            DefaultPutRet putRet = uploadWithRetry(file.getBytes(), fileKey);

            // 生成访问URL
            String fileUrl = generateFileUrl(fileKey);

            // 构建成功结果
            FileStorageResult result = FileStorageResult.success(
                    fileKey, fileUrl, file.getOriginalFilename(),
                    file.getSize(), file.getContentType(), StorageTypeEnum.QINIU);
            result.setMd5Hash(md5Hash);
            result.setStoragePath(path);

            logger.info("文件上传成功: fileKey={}, size={}, contentType={}",
                    fileKey, file.getSize(), file.getContentType());

            return result;

        } catch (Exception e) {
            logger.error("文件上传失败: fileKey={}, error={}", fileKey, e.getMessage(), e);
            throw StorageException.uploadError("文件上传失败: " + e.getMessage(), StorageTypeEnum.QINIU, e);
        }
    }

    @Override
    public boolean delete(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            logger.warn("删除文件失败：文件Key为空");
            return false;
        }

        try {
            String bucket = storageProperties.getQiniu().getBucket();
            Response response = bucketManager.delete(bucket, fileKey);

            if (response.isOK()) {
                logger.info("文件删除成功: fileKey={}", fileKey);
                return true;
            } else {
                logger.warn("文件删除失败: fileKey={}, statusCode={}, body={}",
                        fileKey, response.statusCode, response.bodyString());
                return false;
            }

        } catch (QiniuException e) {
            logger.error("删除文件异常: fileKey={}, error={}", fileKey, e.getMessage(), e);
            throw StorageException.deleteError("删除文件失败: " + e.getMessage(), StorageTypeEnum.QINIU, fileKey, e);
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return null;
        }

        try {
            return generateFileUrl(fileKey);
        } catch (Exception e) {
            logger.error("获取文件URL失败: fileKey={}, error={}", fileKey, e.getMessage(), e);
            throw StorageException.getUrlError("获取文件URL失败: " + e.getMessage(), StorageTypeEnum.QINIU, fileKey, e);
        }
    }

    @Override
    public boolean exists(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return false;
        }

        try {
            String bucket = storageProperties.getQiniu().getBucket();
            bucketManager.stat(bucket, fileKey);
            return true;
        } catch (QiniuException e) {
            if (e.response.statusCode == 612) {
                // 文件不存在
                return false;
            }
            logger.error("检查文件存在性异常: fileKey={}, error={}", fileKey, e.getMessage(), e);
            throw new StorageException("检查文件存在性失败: " + e.getMessage(), e);
        }
    }

    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.QINIU;
    }

    @Override
    public String getStorageInfo() {
        StorageProperties.QiniuConfig config = storageProperties.getQiniu();
        return String.format("七牛云存储 [Bucket: %s, Domain: %s, Region: %s, HTTPS: %s]",
                config.getBucket(), config.getDomain(), config.getRegion(), config.getUseHttps());
    }

    /**
     * 验证文件类型和大小
     * 
     * @param file 文件对象
     * @return String 错误信息，null表示验证通过
     */
    private String validateFile(MultipartFile file) {
        // 检查文件大小
        if (file.getSize() > storageProperties.getMaxFileSize()) {
            return String.format("文件大小超过限制，最大允许 %d MB",
                    storageProperties.getMaxFileSize() / 1024 / 1024);
        }

        // 检查文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            return "文件名无效或缺少扩展名";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String[] allowedTypes = storageProperties.getAllowedTypes();

        if (allowedTypes != null && allowedTypes.length > 0) {
            boolean typeAllowed = Arrays.stream(allowedTypes)
                    .anyMatch(type -> type.equalsIgnoreCase(extension));
            if (!typeAllowed) {
                return String.format("不支持的文件类型: %s，允许的类型: %s",
                        extension, String.join(", ", allowedTypes));
            }
        }

        return null;
    }

    /**
     * 生成文件Key
     * 
     * @param originalFileName 原始文件名
     * @param path             存储路径
     * @return String 文件Key
     */
    private String generateFileKey(String originalFileName, String path) {
        // 获取文件扩展名
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 生成日期路径
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + extension;

        // 组合完整路径
        StringBuilder keyBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(path)) {
            keyBuilder.append(path.endsWith("/") ? path : path + "/");
        }
        keyBuilder.append(datePath).append("/").append(fileName);

        return keyBuilder.toString();
    }

    /**
     * 带重试的文件上传
     * 
     * @param data    文件数据
     * @param fileKey 文件Key
     * @return DefaultPutRet 上传结果
     * @throws Exception 上传异常
     */
    private DefaultPutRet uploadWithRetry(byte[] data, String fileKey) throws Exception {
        String bucket = storageProperties.getQiniu().getBucket();
        String upToken = auth.uploadToken(bucket);

        Exception lastException = null;
        for (int i = 0; i < UPLOAD_RETRY_COUNT; i++) {
            try {
                Response response = uploadManager.put(data, fileKey, upToken);
                return response.jsonToObject(DefaultPutRet.class);
            } catch (Exception e) {
                lastException = e;
                if (i < UPLOAD_RETRY_COUNT - 1) {
                    logger.warn("文件上传失败，正在重试 ({}/{}): {}", i + 1, UPLOAD_RETRY_COUNT, e.getMessage());
                    Thread.sleep(1000 * (i + 1)); // 递增延迟
                }
            }
        }

        throw new QiniuException(lastException, "上传重试失败");
    }

    /**
     * 生成文件访问URL
     * 
     * @param fileKey 文件Key
     * @return String 访问URL
     */
    private String generateFileUrl(String fileKey) {
        StorageProperties.QiniuConfig config = storageProperties.getQiniu();
        String protocol = config.getUseHttps() ? "https" : "http";
        String domain = config.getDomain();

        // 确保域名不包含协议前缀
        if (domain.startsWith("http://") || domain.startsWith("https://")) {
            domain = domain.substring(domain.indexOf("://") + 3);
        }

        return String.format("%s://%s/%s", protocol, domain, fileKey);
    }

    /**
     * 根据名称获取七牛云存储区域
     * 
     * @param regionName 区域名称
     * @return Region 存储区域
     */
    private Region getRegionByName(String regionName) {
        if (regionName == null) {
            return Region.region2(); // 华南默认
        }

        switch (regionName) {
            case "华东":
                return Region.region0();
            case "华北":
                return Region.region1();
            case "华南":
                return Region.region2();
            case "北美":
                return Region.regionNa0();
            case "东南亚":
                return Region.regionAs0();
            default:
                logger.warn("未识别的存储区域: {}，使用默认华南区域", regionName);
                return Region.region2();
        }
    }

    /**
     * 计算文件MD5值
     * 
     * @param data 文件数据
     * @return String MD5值
     */
    private String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            logger.warn("计算文件MD5失败: {}", e.getMessage());
            return null;
        }
    }
}