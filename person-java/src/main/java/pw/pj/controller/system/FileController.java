package pw.pj.controller.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.VO.FileVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.common.utils.IpUtils;
import pw.pj.service.TbFileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 文件管理控制器
 * 提供文件上传、下载、管理等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Api(tags = "文件管理")
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
public class FileController {

    @Autowired
    private TbFileService fileService;

    /**
     * 文件上传（单文件）
     * 
     * @param file     上传的文件
     * @param fileType 文件类型（image/document/video/other）
     * @param request  HTTP请求对象
     * @return 文件信息
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public ApiResponse<FileVO> uploadFile(
            @ApiParam(value = "上传文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "文件类型") @RequestParam(required = false, defaultValue = "other") String fileType,
            HttpServletRequest request) {
        log.info("文件上传: filename={}, size={}, type={}", file.getOriginalFilename(), file.getSize(), fileType);

        try {
            String clientIp = IpUtils.getClientIp(request);

            // 调用服务层上传文件
            FileVO fileVO = fileService.uploadFile(file, fileType, clientIp);

            log.info("文件上传成功: fileId={}, filename={}", fileVO.getId(), fileVO.getOriginalName());
            return ApiResponse.success("文件上传成功", fileVO);

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量文件上传
     * 
     * @param files    上传的文件数组
     * @param fileType 文件类型
     * @param request  HTTP请求对象
     * @return 文件列表
     */
    @ApiOperation("批量文件上传")
    @PostMapping("/upload/batch")
    public ApiResponse<List<FileVO>> uploadFiles(
            @ApiParam(value = "上传文件数组", required = true) @RequestParam("files") MultipartFile[] files,
            @ApiParam(value = "文件类型") @RequestParam(required = false, defaultValue = "other") String fileType,
            HttpServletRequest request) {
        log.info("批量文件上传: fileCount={}, type={}", files.length, fileType);

        try {
            String clientIp = IpUtils.getClientIp(request);

            // 调用服务层批量上传文件
            List<FileVO> fileVOList = fileService.uploadFiles(files, fileType, clientIp);

            log.info("批量文件上传成功: count={}", fileVOList.size());
            return ApiResponse.success("批量文件上传成功", fileVOList);

        } catch (Exception e) {
            log.error("批量文件上传失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 图片上传
     * 
     * @param file     上传的图片
     * @param category 图片分类
     * @param request  HTTP请求对象
     * @return 图片信息
     */
    @ApiOperation("图片上传")
    @PostMapping("/upload/image")
    public ApiResponse<FileVO> uploadImage(
            @ApiParam(value = "上传图片", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "图片分类") @RequestParam(required = false, defaultValue = "other") String category,
            HttpServletRequest request) {
        log.info("图片上传: filename={}, size={}, category={}", file.getOriginalFilename(), file.getSize(), category);

        try {
            String clientIp = IpUtils.getClientIp(request);

            // 调用服务层上传图片
            FileVO fileVO = fileService.uploadImage(file, category, clientIp);

            log.info("图片上传成功: fileId={}, filename={}", fileVO.getId(), fileVO.getOriginalName());
            return ApiResponse.success("图片上传成功", fileVO);

        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 文件下载
     * 
     * @param id       文件ID
     * @param response HTTP响应对象
     * @param request  HTTP请求对象
     */
    @ApiOperation("文件下载")
    @GetMapping("/download/{id}")
    public void downloadFile(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id,
            HttpServletResponse response,
            HttpServletRequest request) {
        log.info("文件下载: fileId={}", id);

        try {
            String clientIp = IpUtils.getClientIp(request);

            // 调用服务层下载文件
            fileService.downloadFile(id, response, clientIp);

            log.info("文件下载完成: fileId={}", id);

        } catch (Exception e) {
            log.error("文件下载失败: fileId={}, error={}", id, e.getMessage(), e);
            // 设置错误响应
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"success\":false,\"message\":\"文件下载失败\"}");
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 获取文件信息
     * 
     * @param id 文件ID
     * @return 文件信息
     */
    @ApiOperation("获取文件信息")
    @GetMapping("/{id}")
    public ApiResponse<FileVO> getFileInfo(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id) {
        log.info("获取文件信息: fileId={}", id);

        try {
            // 调用服务层获取文件信息
            FileVO fileVO = fileService.getFileById(id);

            if (fileVO != null) {
                log.info("获取文件信息成功: fileId={}, filename={}", id, fileVO.getOriginalName());
                return ApiResponse.success(fileVO);
            } else {
                return ApiResponse.error("文件不存在");
            }

        } catch (Exception e) {
            log.error("获取文件信息失败: fileId={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("获取文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 文件预览
     * 
     * @param id       文件ID
     * @param response HTTP响应对象
     */
    @ApiOperation("文件预览")
    @GetMapping("/preview/{id}")
    public void previewFile(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id,
            HttpServletResponse response) {
        log.info("文件预览: id={}", id);

        try {
            fileService.previewFile(id, response);
        } catch (Exception e) {
            log.error("文件预览错误", e);
            try {
                response.getWriter().write("文件预览失败");
            } catch (Exception ex) {
                log.error("写入预览错误响应失败", ex);
            }
        }
    }

    /**
     * 获取文件列表
     * 
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param fileType 文件类型
     * @param keyword  搜索关键词
     * @return 文件列表
     */
    @ApiOperation("获取文件列表")
    @GetMapping("/list")
    public ApiResponse<PageResult<FileVO>> getFileList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String keyword) {
        log.info("获取文件列表: pageNum={}, pageSize={}, fileType={}, keyword={}", pageNum, pageSize, fileType, keyword);

        try {
            // 调用服务层获取文件列表
            PageResult<FileVO> fileList = fileService.getFileList(pageNum, pageSize, fileType, keyword);

            log.info("获取文件列表成功: total={}, pageNum={}", fileList.getTotal(), pageNum);
            return ApiResponse.success(fileList);

        } catch (Exception e) {
            log.error("获取文件列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * 
     * @param id 文件ID
     * @return 删除结果
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFile(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id) {
        log.info("删除文件: fileId={}", id);

        try {
            // 调用服务层删除文件
            Boolean deleteResult = fileService.deleteFile(id);

            if (deleteResult) {
                log.info("文件删除成功: fileId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("文件删除失败");
            }

        } catch (Exception e) {
            log.error("文件删除失败: fileId={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除文件
     * 
     * @param ids 文件ID列表
     * @return 操作结果
     */
    @ApiOperation("批量删除文件")
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteFiles(
            @ApiParam(value = "文件ID列表", required = true) @RequestBody List<Long> ids) {
        log.info("批量删除文件: ids={}", ids);

        try {
            Boolean result = fileService.batchDeleteFiles(ids);

            if (result) {
                log.info("批量删除文件成功: count={}", ids.size());
                return ApiResponse.success();
            } else {
                return ApiResponse.error("批量删除失败");
            }

        } catch (Exception e) {
            log.error("批量删除文件失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件信息
     * 
     * @param id          文件ID
     * @param filename    新文件名
     * @param description 文件描述
     * @return 操作结果
     */
    @ApiOperation("更新文件信息")
    @PutMapping("/{id}")
    public ApiResponse<FileVO> updateFileInfo(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String description) {
        log.info("更新文件信息: id={}, filename={}, description={}", id, filename, description);

        try {
            FileVO fileVO = fileService.updateFileInfo(id, filename, description);
            return ApiResponse.success("文件信息更新成功", fileVO);

        } catch (Exception e) {
            log.error("更新文件信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 管理员分页查询文件
     * 
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param fileType 文件类型
     * @param keyword  搜索关键词
     * @param userId   用户ID
     * @return 文件分页列表
     */
    @ApiOperation("管理员分页查询文件")
    @GetMapping("/admin/page")
    public ApiResponse<PageResult<FileVO>> adminPageFiles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long userId) {
        log.info("管理员分页查询文件: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            PageResult<FileVO> files = fileService.adminPageFiles(pageNum, pageSize,
                    fileType, keyword, userId);

            return ApiResponse.success(files);

        } catch (Exception e) {
            log.error("管理员分页查询文件失败: {}", e.getMessage(), e);
            return ApiResponse.error("查询文件失败: " + e.getMessage());
        }
    }

    /**
     * 管理员删除文件
     * 
     * @param id     文件ID
     * @param reason 删除原因
     * @return 操作结果
     */
    @ApiOperation("管理员删除文件")
    @DeleteMapping("/admin/{id}")
    public ApiResponse<Void> adminDeleteFile(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id,
            @RequestParam(required = false) String reason) {
        log.info("管理员删除文件: id={}, reason={}", id, reason);

        try {
            Boolean result = fileService.adminDeleteFile(id, reason);

            if (result) {
                log.info("管理员删除文件成功: fileId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("删除文件失败");
            }

        } catch (Exception e) {
            log.error("管理员删除文件失败: {}", e.getMessage(), e);
            return ApiResponse.error("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件统计信息
     * 
     * @return 文件统计
     */
    @ApiOperation("获取文件统计信息")
    @GetMapping("/admin/statistics")
    public ApiResponse<Map<String, Object>> getFileStatistics() {
        log.info("获取文件统计信息");

        try {
            Map<String, Object> statistics = fileService.getFileStatistics();
            return ApiResponse.success(statistics);

        } catch (Exception e) {
            log.error("获取文件统计信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取文件统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 清理无效文件
     * 
     * @param days 清理天数
     * @return 操作结果
     */
    @ApiOperation("清理无效文件")
    @PostMapping("/admin/cleanup")
    public ApiResponse<Integer> cleanupInvalidFiles(
            @RequestParam(defaultValue = "30") Integer days) {
        log.info("清理无效文件: days={}", days);

        try {
            Integer cleanupCount = fileService.cleanupInvalidFiles(days);
            log.info("清理无效文件完成: count={}", cleanupCount);
            return ApiResponse.success(cleanupCount);

        } catch (Exception e) {
            log.error("清理无效文件失败: {}", e.getMessage(), e);
            return ApiResponse.error("清理无效文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取存储空间使用情况
     * 
     * @return 存储空间信息
     */
    @ApiOperation("获取存储空间使用情况")
    @GetMapping("/admin/storage")
    public ApiResponse<Map<String, Object>> getStorageInfo() {
        log.info("获取存储空间使用情况");

        try {
            Map<String, Object> storageInfo = fileService.getStorageInfo();
            return ApiResponse.success(storageInfo);

        } catch (Exception e) {
            log.error("获取存储空间使用情况失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取存储空间使用情况失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取文件列表
     * 
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 文件列表
     */
    @ApiOperation("根据用户ID获取文件列表")
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResult<FileVO>> getFilesByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable @NotNull Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("根据用户ID获取文件列表: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        try {
            PageResult<FileVO> fileList = fileService.getFilesByUserId(userId, pageNum, pageSize);
            return ApiResponse.success(fileList);

        } catch (Exception e) {
            log.error("根据用户ID获取文件列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据文件类型获取文件列表
     * 
     * @param fileType 文件类型
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 文件列表
     */
    @ApiOperation("根据文件类型获取文件列表")
    @GetMapping("/type/{fileType}")
    public ApiResponse<PageResult<FileVO>> getFilesByType(
            @ApiParam(value = "文件类型", required = true) @PathVariable @NotNull String fileType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("根据文件类型获取文件列表: fileType={}, pageNum={}, pageSize={}", fileType, pageNum, pageSize);

        try {
            PageResult<FileVO> fileList = fileService.getFilesByType(fileType, pageNum, pageSize);
            return ApiResponse.success(fileList);

        } catch (Exception e) {
            log.error("根据文件类型获取文件列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 恢复已删除的文件
     * 
     * @param id 文件ID
     * @return 恢复结果
     */
    @ApiOperation("恢复已删除的文件")
    @PutMapping("/{id}/restore")
    public ApiResponse<Void> restoreFile(
            @ApiParam(value = "文件ID", required = true) @PathVariable @NotNull Long id) {
        log.info("恢复已删除的文件: fileId={}", id);

        try {
            Boolean result = fileService.restoreFile(id);

            if (result) {
                log.info("文件恢复成功: fileId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("文件恢复失败");
            }

        } catch (Exception e) {
            log.error("文件恢复失败: {}", e.getMessage(), e);
            return ApiResponse.error("文件恢复失败: " + e.getMessage());
        }
    }
}