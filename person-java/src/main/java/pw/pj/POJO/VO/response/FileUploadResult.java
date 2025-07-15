package pw.pj.POJO.VO.response;

import lombok.Data;

/**
 * 文件上传结果
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class FileUploadResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 新文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 本地存储路径
     */
    private String localPath;

    /**
     * 七牛云URL
     */
    private String qiniuUrl;

    /**
     * 访问URL
     */
    private String accessUrl;

    /**
     * 创建成功结果
     */
    public static FileUploadResult success(String message) {
        FileUploadResult result = new FileUploadResult();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    /**
     * 创建失败结果
     */
    public static FileUploadResult error(String message) {
        FileUploadResult result = new FileUploadResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}