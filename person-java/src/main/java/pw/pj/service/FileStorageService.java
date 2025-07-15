package pw.pj.service;

import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.VO.FileStorageResult;
import pw.pj.common.enums.StorageTypeEnum;

/**
 * 文件存储服务抽象接口
 * 定义统一的文件存储操作规范，支持多种存储策略
 * 
 * <p>
 * 该接口基于策略模式设计，支持：
 * </p>
 * <ul>
 * <li>七牛云存储 - 生产环境推荐</li>
 * <li>本地磁盘存储 - 开发环境使用</li>
 * <li>阿里云OSS - 未来扩展</li>
 * </ul>
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface FileStorageService {

    /**
     * 上传文件到存储服务
     * 
     * @param file 要上传的文件对象，不能为null
     * @param path 存储路径，如：avatar/、article/、temp/ 等
     * @return FileStorageResult 上传结果，包含文件URL、Key等信息
     * @throws StorageException 当上传失败时抛出存储异常
     */
    FileStorageResult upload(MultipartFile file, String path);

    /**
     * 根据文件Key删除文件
     * 
     * @param fileKey 文件唯一标识，不能为空
     * @return boolean true-删除成功，false-删除失败
     * @throws StorageException 当删除操作异常时抛出存储异常
     */
    boolean delete(String fileKey);

    /**
     * 获取文件的访问URL
     * 
     * @param fileKey 文件唯一标识，不能为空
     * @return String 文件的可访问URL，如果文件不存在返回null
     * @throws StorageException 当获取URL操作异常时抛出存储异常
     */
    String getFileUrl(String fileKey);

    /**
     * 检查文件是否存在
     * 
     * @param fileKey 文件唯一标识，不能为空
     * @return boolean true-文件存在，false-文件不存在
     * @throws StorageException 当检查操作异常时抛出存储异常
     */
    boolean exists(String fileKey);

    /**
     * 获取当前存储服务的类型
     * 
     * @return StorageTypeEnum 存储类型枚举
     */
    StorageTypeEnum getStorageType();

    /**
     * 获取存储服务的配置信息
     * 用于监控和调试
     * 
     * @return String 存储服务配置的简要描述
     */
    String getStorageInfo();
}