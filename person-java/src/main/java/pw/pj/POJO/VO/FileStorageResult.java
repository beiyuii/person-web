package pw.pj.POJO.VO;

import pw.pj.common.enums.StorageTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件存储结果VO
 * 封装文件上传后的完整信息，用于前后端数据传输
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public class FileStorageResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识Key
     */
    private String fileKey;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    private String contentType;

    /**
     * 存储类型
     */
    private StorageTypeEnum storageType;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 文件存储路径
     */
    private String storagePath;

    /**
     * 是否上传成功
     */
    private Boolean success;

    /**
     * 错误信息（上传失败时）
     */
    private String errorMessage;

    /**
     * 文件MD5值（用于去重和校验）
     */
    private String md5Hash;

    // 构造方法

    /**
     * 默认构造方法
     */
    public FileStorageResult() {
        this.uploadTime = LocalDateTime.now();
        this.success = false;
    }

    /**
     * 成功结果构造方法
     * 
     * @param fileKey          文件Key
     * @param fileUrl          文件URL
     * @param originalFileName 原始文件名
     * @param fileSize         文件大小
     * @param contentType      文件类型
     * @param storageType      存储类型
     */
    public FileStorageResult(String fileKey, String fileUrl, String originalFileName,
            Long fileSize, String contentType, StorageTypeEnum storageType) {
        this();
        this.fileKey = fileKey;
        this.fileUrl = fileUrl;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storageType = storageType;
        this.success = true;
    }

    /**
     * 失败结果构造方法
     * 
     * @param errorMessage 错误信息
     * @param storageType  存储类型
     */
    public FileStorageResult(String errorMessage, StorageTypeEnum storageType) {
        this();
        this.errorMessage = errorMessage;
        this.storageType = storageType;
        this.success = false;
    }

    // 静态工厂方法

    /**
     * 创建成功结果
     * 
     * @param fileKey          文件Key
     * @param fileUrl          文件URL
     * @param originalFileName 原始文件名
     * @param fileSize         文件大小
     * @param contentType      文件类型
     * @param storageType      存储类型
     * @return FileStorageResult
     */
    public static FileStorageResult success(String fileKey, String fileUrl, String originalFileName,
            Long fileSize, String contentType, StorageTypeEnum storageType) {
        return new FileStorageResult(fileKey, fileUrl, originalFileName, fileSize, contentType, storageType);
    }

    /**
     * 创建失败结果
     * 
     * @param errorMessage 错误信息
     * @param storageType  存储类型
     * @return FileStorageResult
     */
    public static FileStorageResult failure(String errorMessage, StorageTypeEnum storageType) {
        return new FileStorageResult(errorMessage, storageType);
    }

    // Getter and Setter methods

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public StorageTypeEnum getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageTypeEnum storageType) {
        this.storageType = storageType;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    @Override
    public String toString() {
        return "FileStorageResult{" +
                "fileKey='" + fileKey + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", fileSize=" + fileSize +
                ", contentType='" + contentType + '\'' +
                ", storageType=" + storageType +
                ", uploadTime=" + uploadTime +
                ", storagePath='" + storagePath + '\'' +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", md5Hash='" + md5Hash + '\'' +
                '}';
    }
}