package pw.pj.service.impl;

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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 本地磁盘存储服务实现
 * 适用于开发环境和小规模部署，支持本地文件系统存储
 * 
 * <p>
 * 功能特性：
 * </p>
 * <ul>
 * <li>本地文件保存</li>
 * <li>自动创建目录结构</li>
 * <li>支持日期目录分类</li>
 * <li>文件删除操作</li>
 * <li>静态资源访问URL生成</li>
 * <li>文件存在性检查</li>
 * <li>支持文件去重（MD5）</li>
 * </ul>
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageServiceImpl.class);

    /**
     * 文件名日期格式
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final StorageProperties storageProperties;

    /**
     * 根目录路径
     */
    private Path rootPath;

    /**
     * 构造注入存储配置
     * 
     * @param storageProperties 存储配置属性
     */
    @Autowired
    public LocalStorageServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    /**
     * 初始化本地存储目录
     */
    @PostConstruct
    public void init() {
        try {
            String rootPathStr = storageProperties.getLocal().getRootPath();
            this.rootPath = Paths.get(rootPathStr).toAbsolutePath();

            // 创建根目录
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
                logger.info("创建本地存储根目录: {}", rootPath);
            }

            // 检查目录权限
            if (!Files.isWritable(rootPath)) {
                throw new StorageException("本地存储根目录不可写: " + rootPath);
            }

            logger.info("本地存储服务初始化成功，根目录: {}", rootPath);

        } catch (IOException e) {
            logger.error("本地存储服务初始化失败", e);
            throw new StorageException("本地存储服务初始化失败: " + e.getMessage(), e);
        }
    }

    @Override
    public FileStorageResult upload(MultipartFile file, String path) {
        if (file == null || file.isEmpty()) {
            return FileStorageResult.failure("文件不能为空", StorageTypeEnum.LOCAL);
        }

        // 验证文件类型和大小
        String validationError = validateFile(file);
        if (validationError != null) {
            return FileStorageResult.failure(validationError, StorageTypeEnum.LOCAL);
        }

        String fileKey = null;
        try {
            // 生成文件Key和路径
            fileKey = generateFileKey(file.getOriginalFilename(), path);
            Path targetPath = rootPath.resolve(fileKey);

            // 创建目录
            Path parentDir = targetPath.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // 计算文件MD5
            String md5Hash = calculateMD5(file.getBytes());

            // 保存文件
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 生成访问URL
            String fileUrl = generateFileUrl(fileKey);

            // 构建成功结果
            FileStorageResult result = FileStorageResult.success(
                    fileKey, fileUrl, file.getOriginalFilename(),
                    file.getSize(), file.getContentType(), StorageTypeEnum.LOCAL);
            result.setMd5Hash(md5Hash);
            result.setStoragePath(path);

            logger.info("文件保存成功: fileKey={}, size={}, targetPath={}",
                    fileKey, file.getSize(), targetPath);

            return result;

        } catch (Exception e) {
            logger.error("文件保存失败: fileKey={}, error={}", fileKey, e.getMessage(), e);
            throw StorageException.uploadError("文件保存失败: " + e.getMessage(), StorageTypeEnum.LOCAL, e);
        }
    }

    @Override
    public boolean delete(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            logger.warn("删除文件失败：文件Key为空");
            return false;
        }

        try {
            Path targetPath = rootPath.resolve(fileKey);

            if (!Files.exists(targetPath)) {
                logger.warn("删除文件失败：文件不存在 fileKey={}", fileKey);
                return false;
            }

            Files.delete(targetPath);

            // 尝试删除空的父目录
            cleanupEmptyDirectories(targetPath.getParent());

            logger.info("文件删除成功: fileKey={}", fileKey);
            return true;

        } catch (Exception e) {
            logger.error("删除文件异常: fileKey={}, error={}", fileKey, e.getMessage(), e);
            throw StorageException.deleteError("删除文件失败: " + e.getMessage(), StorageTypeEnum.LOCAL, fileKey, e);
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
            throw StorageException.getUrlError("获取文件URL失败: " + e.getMessage(), StorageTypeEnum.LOCAL, fileKey, e);
        }
    }

    @Override
    public boolean exists(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return false;
        }

        try {
            Path targetPath = rootPath.resolve(fileKey);
            return Files.exists(targetPath) && Files.isRegularFile(targetPath);
        } catch (Exception e) {
            logger.error("检查文件存在性异常: fileKey={}, error={}", fileKey, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.LOCAL;
    }

    @Override
    public String getStorageInfo() {
        StorageProperties.LocalConfig config = storageProperties.getLocal();
        return String.format("本地磁盘存储 [根目录: %s, URL前缀: %s, 日期目录: %s]",
                rootPath, config.getUrlPrefix(), config.getCreateDateDir());
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

        StringBuilder keyBuilder = new StringBuilder();

        // 添加路径前缀
        if (StringUtils.isNotEmpty(path)) {
            keyBuilder.append(path.endsWith("/") ? path : path + "/");
        }

        // 添加日期路径（如果启用）
        if (storageProperties.getLocal().getCreateDateDir()) {
            String datePath = LocalDateTime.now().format(DATE_FORMATTER);
            keyBuilder.append(datePath).append("/");
        }

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + extension;
        keyBuilder.append(fileName);

        return keyBuilder.toString();
    }

    /**
     * 生成文件访问URL
     * 
     * @param fileKey 文件Key
     * @return String 访问URL
     */
    private String generateFileUrl(String fileKey) {
        String urlPrefix = storageProperties.getLocal().getUrlPrefix();

        // 确保URL前缀以/开头
        if (!urlPrefix.startsWith("/")) {
            urlPrefix = "/" + urlPrefix;
        }

        // 确保URL前缀不以/结尾
        if (urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        }

        return urlPrefix + "/" + fileKey;
    }

    /**
     * 清理空目录
     * 
     * @param dir 目录路径
     */
    private void cleanupEmptyDirectories(Path dir) {
        if (dir == null || dir.equals(rootPath) || !Files.exists(dir)) {
            return;
        }

        try {
            // 检查目录是否为空
            if (Files.isDirectory(dir) && isDirectoryEmpty(dir)) {
                Files.delete(dir);
                logger.debug("删除空目录: {}", dir);

                // 递归删除父目录（如果也为空）
                cleanupEmptyDirectories(dir.getParent());
            }
        } catch (IOException e) {
            logger.debug("清理空目录失败: {}", e.getMessage());
        }
    }

    /**
     * 检查目录是否为空
     * 
     * @param dir 目录路径
     * @return boolean true-目录为空，false-目录不为空
     */
    private boolean isDirectoryEmpty(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            return !stream.findFirst().isPresent();
        } catch (IOException e) {
            return false;
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

    /**
     * 获取文件的实际磁盘路径
     * 
     * @param fileKey 文件Key
     * @return Path 文件路径
     */
    public Path getFilePath(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return null;
        }
        return rootPath.resolve(fileKey);
    }

    /**
     * 获取存储根目录
     * 
     * @return Path 根目录路径
     */
    public Path getRootPath() {
        return rootPath;
    }

    /**
     * 获取目录使用情况统计
     * 
     * @return String 使用情况描述
     */
    public String getStorageUsage() {
        try {
            long totalSize = Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();

            long fileCount = Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .count();

            return String.format("本地存储使用情况 - 文件数量: %d, 总大小: %.2f MB",
                    fileCount, totalSize / 1024.0 / 1024.0);
        } catch (IOException e) {
            logger.error("获取存储使用情况失败", e);
            return "获取存储使用情况失败: " + e.getMessage();
        }
    }
}