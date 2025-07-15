package pw.pj.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.common.result.ApiResponse;
import pw.pj.service.QiniuFileStorageService;
import pw.pj.POJO.VO.response.FileUploadResult;

/**
 * 文件上传控制器
 * 处理文件上传、下载、删除等操作
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@Api(tags = "文件管理接口")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private QiniuFileStorageService qiniuFileStorageService;

    /**
     * 上传文件
     * 
     * @param file   上传的文件
     * @param module 模块名称（可选，默认为 "default"）
     * @return 上传结果
     */
    @PostMapping("/upload")
    @ApiOperation(value = "上传文件", notes = "上传文件到七牛云存储")
    public ApiResponse<FileUploadResult> uploadFile(
            @ApiParam(value = "上传的文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "模块名称", example = "avatar") @RequestParam(value = "module", defaultValue = "default") String module) {

        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return ApiResponse.error("请选择要上传的文件");
            }

            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ApiResponse.error("文件大小不能超过10MB");
            }

            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponse.error("文件名不能为空");
            }

            String extension = getFileExtension(originalFilename);
            if (!isAllowedFileType(extension)) {
                return ApiResponse.error("不支持的文件类型：" + extension);
            }

            // 上传文件
            FileUploadResult result = qiniuFileStorageService.uploadFile(file, module);

            if (result.isSuccess()) {
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(result.getMessage());
            }

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    @GetMapping("/url/{fileId}")
    @ApiOperation(value = "获取文件访问URL", notes = "根据文件ID获取文件访问URL")
    public ApiResponse<String> getFileUrl(
            @ApiParam(value = "文件ID", required = true) @PathVariable String fileId) {

        try {
            String fileUrl = qiniuFileStorageService.getFileUrl(fileId);
            if (fileUrl != null) {
                return ApiResponse.success(fileUrl);
            } else {
                return ApiResponse.error("文件不存在");
            }
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return ApiResponse.error("获取文件URL失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{fileId}")
    @ApiOperation(value = "删除文件", notes = "根据文件ID删除文件")
    public ApiResponse<String> deleteFile(
            @ApiParam(value = "文件ID", required = true) @PathVariable String fileId) {

        try {
            boolean success = qiniuFileStorageService.deleteFile(fileId);
            if (success) {
                return ApiResponse.success("文件删除成功");
            } else {
                return ApiResponse.error("文件删除失败");
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return ApiResponse.error("删除文件失败：" + e.getMessage());
        }
    }

    /**
     * 上传头像
     * 
     * @param file 头像文件
     * @return 上传结果
     */
    @PostMapping("/upload/avatar")
    @ApiOperation(value = "上传头像", notes = "上传用户头像")
    public ApiResponse<FileUploadResult> uploadAvatar(
            @ApiParam(value = "头像文件", required = true) @RequestParam("file") MultipartFile file) {

        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return ApiResponse.error("请选择要上传的头像");
            }

            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return ApiResponse.error("头像文件大小不能超过2MB");
            }

            // 验证文件类型（只允许图片）
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponse.error("文件名不能为空");
            }

            String extension = getFileExtension(originalFilename);
            if (!isImageFile(extension)) {
                return ApiResponse.error("头像只支持图片格式：jpg、jpeg、png、gif、webp");
            }

            // 上传头像
            FileUploadResult result = qiniuFileStorageService.uploadFile(file, "avatar");

            if (result.isSuccess()) {
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(result.getMessage());
            }

        } catch (Exception e) {
            log.error("头像上传失败", e);
            return ApiResponse.error("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 验证是否为允许的文件类型
     */
    private boolean isAllowedFileType(String extension) {
        String[] allowedTypes = { "jpg", "jpeg", "png", "gif", "bmp", "webp", "pdf", "doc", "docx", "xls", "xlsx",
                "ppt", "pptx", "txt", "zip", "rar" };
        for (String type : allowedTypes) {
            if (type.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证是否为图片文件
     */
    private boolean isImageFile(String extension) {
        String[] imageTypes = { "jpg", "jpeg", "png", "gif", "bmp", "webp" };
        for (String type : imageTypes) {
            if (type.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}