package pw.pj.common.exception;

import pw.pj.common.enums.StorageTypeEnum;

/**
 * 存储异常类
 * 统一封装文件存储操作中的异常，便于异常处理和调试
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 存储类型
     */
    private StorageTypeEnum storageType;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 文件Key（如果有）
     */
    private String fileKey;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 默认构造方法
     * 
     * @param message 异常信息
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * 带异常原因的构造方法
     * 
     * @param message 异常信息
     * @param cause   异常原因
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 完整构造方法
     * 
     * @param message     异常信息
     * @param storageType 存储类型
     * @param operation   操作类型
     * @param fileKey     文件Key
     * @param cause       异常原因
     */
    public StorageException(String message, StorageTypeEnum storageType, String operation, String fileKey,
            Throwable cause) {
        super(message, cause);
        this.storageType = storageType;
        this.operation = operation;
        this.fileKey = fileKey;
    }

    /**
     * 带错误代码的构造方法
     * 
     * @param message     异常信息
     * @param errorCode   错误代码
     * @param storageType 存储类型
     */
    public StorageException(String message, String errorCode, StorageTypeEnum storageType) {
        super(message);
        this.errorCode = errorCode;
        this.storageType = storageType;
    }

    // 静态工厂方法

    /**
     * 创建上传异常
     * 
     * @param message     异常信息
     * @param storageType 存储类型
     * @param cause       异常原因
     * @return StorageException
     */
    public static StorageException uploadError(String message, StorageTypeEnum storageType, Throwable cause) {
        return new StorageException(message, storageType, "UPLOAD", null, cause);
    }

    /**
     * 创建删除异常
     * 
     * @param message     异常信息
     * @param storageType 存储类型
     * @param fileKey     文件Key
     * @param cause       异常原因
     * @return StorageException
     */
    public static StorageException deleteError(String message, StorageTypeEnum storageType, String fileKey,
            Throwable cause) {
        return new StorageException(message, storageType, "DELETE", fileKey, cause);
    }

    /**
     * 创建获取URL异常
     * 
     * @param message     异常信息
     * @param storageType 存储类型
     * @param fileKey     文件Key
     * @param cause       异常原因
     * @return StorageException
     */
    public static StorageException getUrlError(String message, StorageTypeEnum storageType, String fileKey,
            Throwable cause) {
        return new StorageException(message, storageType, "GET_URL", fileKey, cause);
    }

    /**
     * 创建配置异常
     * 
     * @param message     异常信息
     * @param storageType 存储类型
     * @return StorageException
     */
    public static StorageException configError(String message, StorageTypeEnum storageType) {
        return new StorageException(message, "CONFIG_ERROR", storageType);
    }

    /**
     * 创建文件不存在异常
     * 
     * @param fileKey     文件Key
     * @param storageType 存储类型
     * @return StorageException
     */
    public static StorageException fileNotFound(String fileKey, StorageTypeEnum storageType) {
        String message = String.format("文件不存在: %s (存储类型: %s)", fileKey, storageType.getDisplayName());
        return new StorageException(message, "FILE_NOT_FOUND", storageType);
    }

    /**
     * 创建文件格式不支持异常
     * 
     * @param fileName    文件名
     * @param storageType 存储类型
     * @return StorageException
     */
    public static StorageException unsupportedFileType(String fileName, StorageTypeEnum storageType) {
        String message = String.format("不支持的文件格式: %s (存储类型: %s)", fileName, storageType.getDisplayName());
        return new StorageException(message, "UNSUPPORTED_FILE_TYPE", storageType);
    }

    /**
     * 创建文件大小超限异常
     * 
     * @param fileName    文件名
     * @param fileSize    文件大小
     * @param maxSize     最大允许大小
     * @param storageType 存储类型
     * @return StorageException
     */
    public static StorageException fileSizeExceeded(String fileName, long fileSize, long maxSize,
            StorageTypeEnum storageType) {
        String message = String.format("文件大小超限: %s (大小: %d, 最大允许: %d, 存储类型: %s)",
                fileName, fileSize, maxSize, storageType.getDisplayName());
        return new StorageException(message, "FILE_SIZE_EXCEEDED", storageType);
    }

    /**
     * 获取详细的错误信息
     * 
     * @return String 详细错误信息
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("存储异常: ").append(getMessage());

        if (storageType != null) {
            sb.append(" [存储类型: ").append(storageType.getDisplayName()).append("]");
        }

        if (operation != null) {
            sb.append(" [操作: ").append(operation).append("]");
        }

        if (fileKey != null) {
            sb.append(" [文件: ").append(fileKey).append("]");
        }

        if (errorCode != null) {
            sb.append(" [错误代码: ").append(errorCode).append("]");
        }

        return sb.toString();
    }

    // Getter and Setter methods

    public StorageTypeEnum getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageTypeEnum storageType) {
        this.storageType = storageType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return getDetailedMessage();
    }
}