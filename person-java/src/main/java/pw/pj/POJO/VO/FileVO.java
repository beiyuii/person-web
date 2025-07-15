package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息响应VO
 * 用于前端显示文件信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class FileVO {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件原始名称
     */
    private String originalName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    /**
     * 文件状态：0-正常，1-已删除
     */
    private Integer status;

    /**
     * 文件分类：1-图片，2-文档，3-视频，4-音频，5-其他
     */
    private Integer category;

    /**
     * 存储位置：1-本地，2-阿里云OSS，3-腾讯云COS，4-七牛云
     */
    private Integer storageType;

    /**
     * 存储配置ID
     */
    private Long storageId;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 上传者信息
     */
    private UserVO uploader;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 引用次数
     */
    private Integer referenceCount;

    /**
     * 是否为图片文件
     */
    private Boolean isImage;

    /**
     * 图片宽度（仅图片文件）
     */
    private Integer imageWidth;

    /**
     * 图片高度（仅图片文件）
     */
    private Integer imageHeight;

    /**
     * 缩略图URL（仅图片文件）
     */
    private String thumbnailUrl;

    /**
     * 获取文件状态文本描述
     * 
     * @return 状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "正常";
            case 1:
                return "已删除";
            default:
                return "未知";
        }
    }

    /**
     * 获取文件分类文本描述
     * 
     * @return 分类文本
     */
    public String getCategoryText() {
        if (category == null) {
            return "其他";
        }
        switch (category) {
            case 1:
                return "图片";
            case 2:
                return "文档";
            case 3:
                return "视频";
            case 4:
                return "音频";
            case 5:
            default:
                return "其他";
        }
    }

    /**
     * 获取存储类型文本描述
     * 
     * @return 存储类型文本
     */
    public String getStorageTypeText() {
        if (storageType == null) {
            return "本地";
        }
        switch (storageType) {
            case 1:
                return "本地";
            case 2:
                return "阿里云OSS";
            case 3:
                return "腾讯云COS";
            case 4:
                return "七牛云";
            default:
                return "未知";
        }
    }

    /**
     * 判断文件是否正常状态
     * 
     * @return 是否正常
     */
    public boolean isNormal() {
        return status != null && status == 0;
    }

    /**
     * 判断文件是否已删除
     * 
     * @return 是否已删除
     */
    public boolean isDeleted() {
        return status != null && status == 1;
    }

    /**
     * 判断是否为图片文件
     * 
     * @return 是否为图片文件
     */
    public boolean isImageFile() {
        return Boolean.TRUE.equals(isImage) ||
                (category != null && category == 1) ||
                isImageExtension();
    }

    /**
     * 判断是否为文档文件
     * 
     * @return 是否为文档文件
     */
    public boolean isDocumentFile() {
        return category != null && category == 2;
    }

    /**
     * 判断是否为视频文件
     * 
     * @return 是否为视频文件
     */
    public boolean isVideoFile() {
        return category != null && category == 3;
    }

    /**
     * 判断是否为音频文件
     * 
     * @return 是否为音频文件
     */
    public boolean isAudioFile() {
        return category != null && category == 4;
    }

    /**
     * 判断是否为本地存储
     * 
     * @return 是否为本地存储
     */
    public boolean isLocalStorage() {
        return storageType == null || storageType == 1;
    }

    /**
     * 判断是否为云存储
     * 
     * @return 是否为云存储
     */
    public boolean isCloudStorage() {
        return storageType != null && storageType > 1;
    }

    /**
     * 根据文件扩展名判断是否为图片
     * 
     * @return 是否为图片扩展名
     */
    private boolean isImageExtension() {
        if (fileExtension == null) {
            return false;
        }

        String ext = fileExtension.toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") ||
                ext.equals("gif") || ext.equals("bmp") || ext.equals("webp") ||
                ext.equals("svg") || ext.equals("ico");
    }

    /**
     * 获取格式化的文件大小
     * 
     * @return 格式化的文件大小
     */
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) {
            return "0 B";
        }

        String[] units = { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = 0;
        double size = fileSize.doubleValue();

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }

    /**
     * 获取文件图标CSS类名
     * 
     * @return 图标CSS类名
     */
    public String getFileIconClass() {
        if (isImageFile()) {
            return "file-icon-image";
        } else if (isDocumentFile()) {
            return "file-icon-document";
        } else if (isVideoFile()) {
            return "file-icon-video";
        } else if (isAudioFile()) {
            return "file-icon-audio";
        } else {
            return "file-icon-other";
        }
    }

    /**
     * 获取文件预览URL
     * 
     * @return 预览URL
     */
    public String getPreviewUrl() {
        if (isImageFile()) {
            return thumbnailUrl != null ? thumbnailUrl : fileUrl;
        } else if (isDocumentFile()) {
            return "/preview/document?id=" + id;
        } else {
            return fileUrl;
        }
    }

    /**
     * 获取文件下载URL
     * 
     * @return 下载URL
     */
    public String getDownloadUrl() {
        return "/download/" + id;
    }

    /**
     * 判断文件是否可以在线预览
     * 
     * @return 是否可以在线预览
     */
    public boolean isPreviewable() {
        if (isImageFile()) {
            return true;
        }

        if (fileExtension != null) {
            String ext = fileExtension.toLowerCase();
            return ext.equals("pdf") || ext.equals("txt") || ext.equals("md") ||
                    ext.equals("doc") || ext.equals("docx") || ext.equals("xls") ||
                    ext.equals("xlsx") || ext.equals("ppt") || ext.equals("pptx");
        }

        return false;
    }

    /**
     * 获取上传者昵称
     * 
     * @return 上传者昵称
     */
    public String getUploaderName() {
        if (uploader != null) {
            return uploader.getNickname() != null ? uploader.getNickname() : uploader.getUsername();
        }
        return "未知";
    }

    /**
     * 获取文件尺寸信息（仅图片文件）
     * 
     * @return 尺寸信息
     */
    public String getImageDimensions() {
        if (!isImageFile() || imageWidth == null || imageHeight == null) {
            return "";
        }
        return imageWidth + " × " + imageHeight;
    }

    /**
     * 获取文件的完整显示名称
     * 
     * @return 完整显示名称
     */
    public String getDisplayName() {
        if (originalName != null && !originalName.trim().isEmpty()) {
            return originalName;
        }
        return fileName;
    }

    /**
     * 判断文件是否经常被引用
     * 
     * @return 是否经常被引用
     */
    public boolean isPopularFile() {
        return referenceCount != null && referenceCount > 10;
    }

    /**
     * 计算文件热度（基于下载次数和引用次数）
     * 
     * @return 热度值
     */
    public double getHotScore() {
        int downloads = downloadCount != null ? downloadCount : 0;
        int references = referenceCount != null ? referenceCount : 0;

        // 权重设置：下载次数(1) + 引用次数(3)
        return downloads + references * 3;
    }
}