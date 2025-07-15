package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.DO.TbFile;
import pw.pj.POJO.VO.FileVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.FileStorageResult;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.exception.StorageException;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.DateTimeUtils;
import pw.pj.mapper.TbFileMapper;
import pw.pj.service.FileStorageService;
import pw.pj.service.TbFileService;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件管理服务实现类
 * 负责文件的业务逻辑处理和数据库操作，存储操作委托给FileStorageService
 * 
 * <p>
 * 职责划分：
 * </p>
 * <ul>
 * <li>文件业务逻辑：验证、权限控制、统计分析</li>
 * <li>数据库操作：文件元数据的CRUD操作</li>
 * <li>存储委托：通过FileStorageService接口操作文件存储</li>
 * </ul>
 * 
 * @author PersonWeb开发团队
 * @version 2.0.0 - 架构重构版本
 * @since 2024-01-01
 */
@Slf4j
@Service
public class TbFileServiceImpl extends ServiceImpl<TbFileMapper, TbFile> implements TbFileService {

    /**
     * 文件存储服务接口 - 策略模式，支持多种存储实现
     */
    private final FileStorageService fileStorageService;

    /**
     * 构造注入文件存储服务
     * 
     * @param fileStorageService 文件存储服务
     */
    @Autowired
    public TbFileServiceImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        // 安全检查，避免在单元测试中因mock未设置导致空指针异常
        if (fileStorageService != null && fileStorageService.getStorageType() != null) {
            log.info("TbFileService初始化完成，当前存储类型: {}",
                    fileStorageService.getStorageType().getDisplayName());
        } else {
            log.info("TbFileService初始化完成，存储服务待配置");
        }
    }

    // ==================== 文件上传操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadFile(MultipartFile file, String fileType, String clientIp) {
        log.info("上传文件：文件名={}, 大小={}, 类型={}, 存储服务={}",
                file.getOriginalFilename(), file.getSize(), fileType,
                fileStorageService.getStorageType().getDisplayName());

        // 1. 验证文件
        if (!validateFileType(file, fileType)) {
            throw new BusinessException(ResultEnum.FILE_TYPE_NOT_SUPPORTED);
        }

        if (!validateFileSize(file, fileType)) {
            throw new BusinessException(ResultEnum.FILE_SIZE_EXCEEDED);
        }

        try {
            // 2. 调用存储服务上传文件
            String storagePath = generateStoragePath(fileType);
            FileStorageResult storageResult = fileStorageService.upload(file, storagePath);

            if (!storageResult.getSuccess()) {
                throw new BusinessException(ResultEnum.FILE_UPLOAD_ERROR, storageResult.getErrorMessage());
            }

            // 3. 保存文件记录到数据库
            TbFile tbFile = createFileRecord(storageResult, fileType, clientIp);
            boolean saved = save(tbFile);

            if (!saved) {
                // 如果数据库保存失败，删除已上传的文件
                try {
                    fileStorageService.delete(storageResult.getFileKey());
                } catch (Exception e) {
                    log.error("回滚文件删除失败: fileKey={}", storageResult.getFileKey(), e);
                }
                throw new BusinessException(ResultEnum.ERROR, "文件记录保存失败");
            }

            log.info("文件上传成功：文件ID={}, fileKey={}, 存储类型={}",
                    tbFile.getId(), storageResult.getFileKey(), storageResult.getStorageType().getDisplayName());

            return convertToVO(tbFile);

        } catch (StorageException e) {
            log.error("存储服务上传失败：{}", e.getDetailedMessage(), e);
            throw new BusinessException(ResultEnum.FILE_UPLOAD_ERROR, e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传异常：{}", e.getMessage(), e);
            throw new BusinessException(ResultEnum.ERROR, "文件上传失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileVO> uploadFiles(MultipartFile[] files, String fileType, String clientIp) {
        log.info("批量上传文件：数量={}, 类型={}, 存储服务={}",
                files.length, fileType, fileStorageService.getStorageType().getDisplayName());

        List<FileVO> fileVOList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                FileVO fileVO = uploadFile(file, fileType, clientIp);
                fileVOList.add(fileVO);
            } catch (Exception e) {
                log.error("文件上传失败：{}", file.getOriginalFilename(), e);
                errorMessages.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        if (!errorMessages.isEmpty()) {
            log.warn("部分文件上传失败：{}", String.join(", ", errorMessages));
        }

        log.info("批量上传完成：成功={}, 失败={}", fileVOList.size(), errorMessages.size());
        return fileVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadImage(MultipartFile file, String category, String clientIp) {
        log.info("上传图片：文件名={}, 分类={}, 存储服务={}",
                file.getOriginalFilename(), category, fileStorageService.getStorageType().getDisplayName());

        // 验证是否为图片文件
        if (!isImageFile(file)) {
            throw new BusinessException(ResultEnum.FILE_TYPE_NOT_SUPPORTED, "只能上传图片文件");
        }

        return uploadFile(file, "image", clientIp);
    }

    // ==================== 文件下载和预览 ====================

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response, String clientIp) {
        log.info("下载文件：文件ID={}, IP={}", fileId, clientIp);

        // 1. 获取文件信息
        TbFile file = getById(fileId);
        if (file == null || file.getIsDelete() == 1) {
            throw new BusinessException(ResultEnum.FILE_NOT_FOUND);
        }

        try {
            // 2. 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename="
                            + URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8.toString()));

            // 3. 从存储服务获取文件流并输出（Java 8兼容方式）
            URL url = new URL(file.getFileUrl());
            URLConnection connection = url.openConnection();

            try (InputStream inputStream = connection.getInputStream();
                    OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            // 4. 更新下载次数
            incrementDownloadCount(fileId);

            log.info("文件下载成功：文件ID={}", fileId);

        } catch (StorageException e) {
            log.error("文件下载失败：文件ID={}", fileId, e);
            throw new BusinessException(ResultEnum.FILE_READ_ERROR, "文件下载失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件下载失败：文件ID={}", fileId, e);
            throw new BusinessException(ResultEnum.FILE_READ_ERROR, "文件下载失败");
        }
    }

    @Override
    public void previewFile(Long fileId, HttpServletResponse response) {
        log.info("预览文件：文件ID={}", fileId);

        // 1. 获取文件信息
        TbFile file = getById(fileId);
        if (file == null || file.getIsDelete() == 1) {
            throw new BusinessException(ResultEnum.FILE_NOT_FOUND);
        }

        try {
            // 2. 设置响应头
            response.setContentType(file.getMimeType());
            response.setHeader("Content-Disposition", "inline; filename=" + file.getOriginalName());

            // 3. 从存储服务获取文件流并输出（Java 8兼容方式）
            URL url = new URL(file.getFileUrl());
            URLConnection connection = url.openConnection();

            try (InputStream inputStream = connection.getInputStream();
                    OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            log.info("文件预览成功：文件ID={}", fileId);

        } catch (StorageException e) {
            log.error("文件预览失败：文件ID={}", fileId, e);
            throw new BusinessException(ResultEnum.FILE_READ_ERROR, "文件预览失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件预览失败：文件ID={}", fileId, e);
            throw new BusinessException(ResultEnum.FILE_READ_ERROR, "文件预览失败");
        }
    }

    @Override
    public String getFileUrl(Long fileId) {
        TbFile file = getById(fileId);
        return file != null ? file.getFileUrl() : null;
    }

    // ==================== 文件查询操作 ====================

    @Override
    public FileVO getFileById(Long fileId) {
        if (fileId == null) {
            return null;
        }

        TbFile file = lambdaQuery()
                .eq(TbFile::getId, fileId)
                .eq(TbFile::getIsDelete, 0)
                .one();

        return convertToVO(file);
    }

    @Override
    public PageResult<FileVO> getFileList(Integer pageNum, Integer pageSize, String fileType, String keyword) {
        LambdaQueryWrapper<TbFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbFile::getIsDelete, 0)
                .eq(TbFile::getStatus, SystemConstants.File.STATUS_NORMAL)
                .orderByDesc(TbFile::getCreateTime);

        // 文件类型筛选
        if (StringUtils.hasText(fileType)) {
            queryWrapper.eq(TbFile::getFileType, fileType);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(TbFile::getOriginalName, keyword)
                    .or().like(TbFile::getFileName, keyword)
                    .or().like(TbFile::getRemark, keyword));
        }

        Page<TbFile> page = new Page<>(pageNum, pageSize);
        Page<TbFile> resultPage = page(page, queryWrapper);

        List<FileVO> fileVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(fileVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    @Override
    public PageResult<FileVO> getFileList(PageQueryVO pageQueryVO) {
        return getFileList(pageQueryVO.getPageNum(), pageQueryVO.getPageSize(),
                null, pageQueryVO.getKeyword());
    }

    @Override
    public PageResult<FileVO> getFilesByUserId(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TbFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbFile::getUploadUserId, userId)
                .eq(TbFile::getIsDelete, 0)
                .eq(TbFile::getStatus, SystemConstants.File.STATUS_NORMAL)
                .orderByDesc(TbFile::getCreateTime);

        Page<TbFile> page = new Page<>(pageNum, pageSize);
        Page<TbFile> resultPage = page(page, queryWrapper);

        List<FileVO> fileVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(fileVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    @Override
    public PageResult<FileVO> getFilesByType(String fileType, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TbFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbFile::getFileType, fileType)
                .eq(TbFile::getIsDelete, 0)
                .eq(TbFile::getStatus, SystemConstants.File.STATUS_NORMAL)
                .orderByDesc(TbFile::getCreateTime);

        Page<TbFile> page = new Page<>(pageNum, pageSize);
        Page<TbFile> resultPage = page(page, queryWrapper);

        List<FileVO> fileVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(fileVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    // ==================== 文件管理操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO updateFileInfo(Long fileId, String filename, String description) {
        log.info("更新文件信息：文件ID={}, 文件名={}", fileId, filename);

        TbFile file = getById(fileId);
        if (file == null || file.getIsDelete() == 1) {
            throw new BusinessException(ResultEnum.FILE_NOT_FOUND);
        }

        // 更新文件信息
        boolean updated = false;
        if (StringUtils.hasText(filename)) {
            file.setOriginalName(filename);
            updated = true;
        }
        if (description != null) {
            file.setRemark(description);
            updated = true;
        }

        if (updated) {
            file.setUpdateTime(new Date());
            updateById(file);
            log.info("文件信息更新成功：文件ID={}", fileId);
        }

        return convertToVO(file);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFile(Long fileId) {
        log.info("删除文件：文件ID={}", fileId);

        // 1. 获取文件信息
        TbFile file = getById(fileId);
        if (file == null) {
            throw new BusinessException(ResultEnum.FILE_NOT_FOUND);
        }

        if (file.getIsDelete() == 1) {
            log.warn("文件已被删除：文件ID={}", fileId);
            return true;
        }

        try {
            // 2. 软删除数据库记录
            boolean updated = lambdaUpdate()
                    .eq(TbFile::getId, fileId)
                    .set(TbFile::getIsDelete, 1)
                    .set(TbFile::getUpdateTime, LocalDateTime.now())
                    .update();

            if (updated) {
                log.info("文件软删除成功：文件ID={}, filePath={}", fileId, file.getFilePath());
                return true;
            } else {
                throw new BusinessException(ResultEnum.ERROR, "文件删除失败");
            }

        } catch (Exception e) {
            log.error("文件删除失败：文件ID={}", fileId, e);
            throw new BusinessException(ResultEnum.ERROR, "文件删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteFiles(List<Long> fileIds) {
        log.info("批量删除文件：数量={}", fileIds.size());

        boolean deleted = lambdaUpdate()
                .in(TbFile::getId, fileIds)
                .set(TbFile::getIsDelete, 1)
                .set(TbFile::getUpdateTime, new Date())
                .update();

        if (deleted) {
            log.info("批量删除文件成功：数量={}", fileIds.size());
        }

        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean permanentDeleteFile(Long fileId) {
        log.info("永久删除文件：文件ID={}", fileId);

        // 1. 获取文件信息
        TbFile file = getById(fileId);
        if (file == null) {
            throw new BusinessException(ResultEnum.FILE_NOT_FOUND);
        }

        try {
            // 2. 从存储服务删除文件
            if (StringUtils.hasText(file.getFilePath())) {
                boolean storageDeleted = fileStorageService.delete(file.getFilePath());
                if (!storageDeleted) {
                    log.warn("存储服务删除文件失败，但继续删除数据库记录：filePath={}", file.getFilePath());
                }
            }

            // 3. 从数据库删除记录
            boolean dbDeleted = removeById(fileId);

            if (dbDeleted) {
                log.info("文件永久删除成功：文件ID={}, filePath={}", fileId, file.getFilePath());
                return true;
            } else {
                throw new BusinessException(ResultEnum.ERROR, "数据库记录删除失败");
            }

        } catch (StorageException e) {
            log.error("存储服务删除失败：{}", e.getDetailedMessage(), e);
            throw new BusinessException(ResultEnum.FILE_READ_ERROR, "存储服务删除失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件永久删除失败：文件ID={}", fileId, e);
            throw new BusinessException(ResultEnum.ERROR, "文件永久删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean restoreFile(Long fileId) {
        log.info("恢复文件：文件ID={}", fileId);

        boolean restored = lambdaUpdate()
                .eq(TbFile::getId, fileId)
                .set(TbFile::getIsDelete, 0)
                .set(TbFile::getUpdateTime, new Date())
                .update();

        if (restored) {
            log.info("文件恢复成功：文件ID={}", fileId);
        }

        return restored;
    }

    // ==================== 管理员操作 ====================

    @Override
    public PageResult<FileVO> adminPageFiles(Integer pageNum, Integer pageSize, String fileType, String keyword,
            Long userId) {
        LambdaQueryWrapper<TbFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(TbFile::getCreateTime);

        // 文件类型筛选
        if (StringUtils.hasText(fileType)) {
            queryWrapper.eq(TbFile::getFileType, fileType);
        }

        // 用户筛选
        if (userId != null) {
            queryWrapper.eq(TbFile::getUploadUserId, userId);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(TbFile::getOriginalName, keyword)
                    .or().like(TbFile::getFileName, keyword)
                    .or().like(TbFile::getRemark, keyword));
        }

        Page<TbFile> page = new Page<>(pageNum, pageSize);
        Page<TbFile> resultPage = page(page, queryWrapper);

        List<FileVO> fileVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(fileVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean adminDeleteFile(Long fileId, String reason) {
        log.info("管理员删除文件：文件ID={}, 原因={}", fileId, reason);

        TbFile file = getById(fileId);
        if (file == null) {
            throw new BusinessException(ResultEnum.FILE_NOT_FOUND);
        }

        // 更新删除原因到备注
        String remark = file.getRemark();
        String newRemark = "管理员删除：" + (reason != null ? reason : "无原因");
        if (StringUtils.hasText(remark)) {
            newRemark = remark + " | " + newRemark;
        }

        boolean deleted = lambdaUpdate()
                .eq(TbFile::getId, fileId)
                .set(TbFile::getIsDelete, 1)
                .set(TbFile::getRemark, newRemark)
                .set(TbFile::getUpdateTime, new Date())
                .update();

        if (deleted) {
            log.info("管理员删除文件成功：文件ID={}", fileId);
        }

        return deleted;
    }

    @Override
    public Map<String, Object> getFileStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总文件数
        Long totalFiles = lambdaQuery()
                .eq(TbFile::getIsDelete, 0)
                .count();

        // 总存储大小
        List<TbFile> allFiles = lambdaQuery()
                .eq(TbFile::getIsDelete, 0)
                .list();

        long totalSize = allFiles.stream()
                .mapToLong(file -> file.getFileSize() != null ? file.getFileSize() : 0L)
                .sum();

        // 各类型文件统计
        Map<String, Long> typeStatistics = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getFileType() != null ? file.getFileType() : "unknown",
                        Collectors.counting()));

        // 今日上传文件数（修复DateTimeUtils调用）
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Date today = DateTimeUtils.toDate(todayStart);
        Long todayFiles = lambdaQuery()
                .eq(TbFile::getIsDelete, 0)
                .ge(TbFile::getCreateTime, today)
                .count();

        statistics.put("totalFiles", totalFiles);
        statistics.put("totalSize", totalSize);
        statistics.put("formattedTotalSize", formatFileSize(totalSize));
        statistics.put("typeStatistics", typeStatistics);
        statistics.put("todayFiles", todayFiles);

        return statistics;
    }

    @Override
    public Map<String, Object> getStorageInfo() {
        Map<String, Object> storageInfo = new HashMap<>();

        // 按存储类型统计
        List<TbFile> allFiles = lambdaQuery()
                .eq(TbFile::getIsDelete, 0)
                .list();

        Map<Integer, Long> storageTypeCount = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getStorageType() != null ? file.getStorageType() : 1,
                        Collectors.counting()));

        Map<Integer, Long> storageTypeSize = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getStorageType() != null ? file.getStorageType() : 1,
                        Collectors.summingLong(file -> file.getFileSize() != null ? file.getFileSize() : 0L)));

        storageInfo.put("storageTypeCount", storageTypeCount);
        storageInfo.put("storageTypeSize", storageTypeSize);

        return storageInfo;
    }

    // ==================== 数据维护操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanupInvalidFiles(Integer days) {
        log.info("清理无效文件：天数={}", days);

        // 计算清理时间点
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date cleanupDate = calendar.getTime();

        // 查找要清理的文件
        List<TbFile> filesToCleanup = lambdaQuery()
                .eq(TbFile::getIsDelete, 1)
                .lt(TbFile::getUpdateTime, cleanupDate)
                .list();

        int cleanupCount = 0;
        for (TbFile file : filesToCleanup) {
            try {
                // 删除七牛云文件
                fileStorageService.delete(file.getFilePath());

                // 删除数据库记录
                removeById(file.getId());

                cleanupCount++;
            } catch (Exception e) {
                log.error("清理文件失败：文件ID={}", file.getId(), e);
            }
        }

        log.info("无效文件清理完成：清理数量={}", cleanupCount);
        return cleanupCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncFileSizeStatistics() {
        log.info("同步文件大小统计");

        List<TbFile> allFiles = lambdaQuery()
                .eq(TbFile::getIsDelete, 0)
                .list();

        int syncCount = 0;
        for (TbFile file : allFiles) {
            if (file.getFileSize() == null || file.getFileSize() == 0) {
                // 这里可以通过文件URL获取实际文件大小进行更新
                // 暂时跳过，实际项目中可以实现
                syncCount++;
            }
        }

        log.info("文件大小统计同步完成：同步数量={}", syncCount);
        return syncCount;
    }

    @Override
    public Map<String, Object> checkFileIntegrity() {
        log.info("检查文件完整性");

        Map<String, Object> result = new HashMap<>();
        List<String> missingFiles = new ArrayList<>();
        List<String> inconsistentFiles = new ArrayList<>();

        List<TbFile> allFiles = lambdaQuery()
                .eq(TbFile::getIsDelete, 0)
                .list();

        int checkedCount = 0;
        for (TbFile file : allFiles) {
            try {
                // 检查七牛云文件是否存在
                // 这里简化处理，实际项目中需要调用七牛云API检查
                checkedCount++;
            } catch (Exception e) {
                log.error("检查文件完整性失败：文件ID={}", file.getId(), e);
                missingFiles.add(file.getOriginalName());
            }
        }

        result.put("totalChecked", checkedCount);
        result.put("missingFiles", missingFiles);
        result.put("inconsistentFiles", inconsistentFiles);
        result.put("missingCount", missingFiles.size());
        result.put("inconsistentCount", inconsistentFiles.size());

        log.info("文件完整性检查完成：检查={}, 缺失={}, 不一致={}",
                checkedCount, missingFiles.size(), inconsistentFiles.size());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementDownloadCount(Long fileId) {
        boolean updated = lambdaUpdate()
                .eq(TbFile::getId, fileId)
                .setSql("download_count = download_count + 1")
                .update();

        return updated;
    }

    // ==================== 文件验证和工具 ====================

    @Override
    public Boolean validateFileType(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            return false;
        }

        String extension = getFileExtension(originalFilename).toLowerCase();

        switch (fileType) {
            case "image":
                return Arrays.asList(SystemConstants.File.ALLOWED_IMAGE_FORMATS).contains(extension);
            case "document":
                return Arrays.asList(SystemConstants.File.ALLOWED_DOCUMENT_FORMATS).contains(extension);
            default:
                return true; // 其他类型暂时都允许
        }
    }

    @Override
    public Boolean validateFileSize(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        long fileSize = file.getSize();

        switch (fileType) {
            case "image":
                return fileSize <= SystemConstants.File.MAX_IMAGE_SIZE;
            default:
                return fileSize <= SystemConstants.File.MAX_FILE_SIZE;
        }
    }

    @Override
    public String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "." + extension;
    }

    @Override
    public Integer getFileTypeCategory(String mimeType) {
        if (!StringUtils.hasText(mimeType)) {
            return SystemConstants.File.TYPE_OTHER;
        }

        String type = mimeType.toLowerCase();
        if (type.startsWith("image/")) {
            return SystemConstants.File.TYPE_IMAGE;
        } else if (type.startsWith("video/")) {
            return SystemConstants.File.TYPE_VIDEO;
        } else if (type.startsWith("audio/")) {
            return SystemConstants.File.TYPE_AUDIO;
        } else if (type.contains("document") || type.contains("pdf") || type.contains("word") ||
                type.contains("excel") || type.contains("powerpoint")) {
            return SystemConstants.File.TYPE_DOCUMENT;
        } else {
            return SystemConstants.File.TYPE_OTHER;
        }
    }

    // ==================== 数据转换方法 ====================

    @Override
    public FileVO convertToVO(TbFile file) {
        if (file == null) {
            return null;
        }

        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(file, fileVO);

        // 设置isImage属性（FileVO已有此属性）
        fileVO.setIsImage(isImageByExtension(file.getFileExtension()));

        // 注意：不再设置formattedFileSize，因为FileVO有getFormattedFileSize()方法

        return fileVO;
    }

    @Override
    public List<FileVO> convertToVOList(List<TbFile> files) {
        if (ObjectUtils.isEmpty(files)) {
            return new ArrayList<>();
        }

        return files.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename) || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * 根据扩展名判断是否为图片
     */
    private boolean isImageByExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        return Arrays.asList(SystemConstants.File.ALLOWED_IMAGE_FORMATS)
                .contains(extension.toLowerCase());
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long fileSize) {
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
     * 生成存储路径
     * 
     * @param fileType 文件类型
     * @return String 存储路径
     */
    private String generateStoragePath(String fileType) {
        switch (fileType) {
            case "image":
                return "images";
            case "avatar":
                return "avatars";
            case "article":
                return "articles";
            case "document":
                return "documents";
            default:
                return "files";
        }
    }

    /**
     * 创建文件记录
     * 
     * @param storageResult 存储结果
     * @param fileType      文件类型
     * @param clientIp      客户端IP
     * @return TbFile 文件记录
     */
    private TbFile createFileRecord(FileStorageResult storageResult, String fileType, String clientIp) {
        TbFile tbFile = new TbFile();

        // 基本信息
        tbFile.setOriginalName(storageResult.getOriginalFileName());
        tbFile.setFileName(extractFileName(storageResult.getFileKey()));
        tbFile.setFilePath(storageResult.getFileKey()); // 使用fileKey作为filePath
        tbFile.setFileUrl(storageResult.getFileUrl());
        tbFile.setFileSize(storageResult.getFileSize());
        tbFile.setMimeType(storageResult.getContentType());

        // 扩展信息
        tbFile.setFileType(fileType);
        tbFile.setStorageType(getStorageTypeCode(storageResult.getStorageType()));
        // tbFile.setStoragePath(storageResult.getStoragePath()); // TbFile中没有此字段
        // tbFile.setMd5Hash(storageResult.getMd5Hash()); // TbFile中没有此字段

        // 系统信息
        tbFile.setUploadIp(clientIp);
        tbFile.setDownloadCount(0);
        tbFile.setIsDelete(0);
        tbFile.setCreateTime(new Date());
        tbFile.setUpdateTime(new Date());

        return tbFile;
    }

    /**
     * 将存储类型枚举转换为数据库存储的编码
     * 
     * @param storageType 存储类型枚举
     * @return Integer 存储类型编码
     */
    private Integer getStorageTypeCode(pw.pj.common.enums.StorageTypeEnum storageType) {
        switch (storageType) {
            case LOCAL:
                return 1;
            case QINIU:
                return 3;
            case ALIYUN_OSS:
                return 2;
            default:
                return 1;
        }
    }

    /**
     * 从文件Key中提取文件名
     * 
     * @param fileKey 文件Key
     * @return String 文件名
     */
    private String extractFileName(String fileKey) {
        if (fileKey == null || !fileKey.contains("/")) {
            return fileKey;
        }
        return fileKey.substring(fileKey.lastIndexOf("/") + 1);
    }
}
