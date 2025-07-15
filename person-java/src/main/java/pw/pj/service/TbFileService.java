package pw.pj.service;

import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.DO.TbFile;
import pw.pj.POJO.VO.FileVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 文件管理服务接口
 * 提供文件的完整业务功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbFileService extends IService<TbFile> {

    // ==================== 文件上传操作 ====================

    /**
     * 上传文件
     * 
     * @param file     上传的文件
     * @param fileType 文件类型
     * @param clientIp 客户端IP
     * @return 文件信息
     */
    FileVO uploadFile(MultipartFile file, String fileType, String clientIp);

    /**
     * 批量上传文件
     * 
     * @param files    上传的文件数组
     * @param fileType 文件类型
     * @param clientIp 客户端IP
     * @return 文件信息列表
     */
    List<FileVO> uploadFiles(MultipartFile[] files, String fileType, String clientIp);

    /**
     * 上传图片（专用接口）
     * 
     * @param file     上传的图片文件
     * @param category 图片分类
     * @param clientIp 客户端IP
     * @return 图片信息
     */
    FileVO uploadImage(MultipartFile file, String category, String clientIp);

    // ==================== 文件下载和预览 ====================

    /**
     * 下载文件
     * 
     * @param fileId   文件ID
     * @param response HTTP响应对象
     * @param clientIp 客户端IP
     */
    void downloadFile(Long fileId, HttpServletResponse response, String clientIp);

    /**
     * 预览文件
     * 
     * @param fileId   文件ID
     * @param response HTTP响应对象
     */
    void previewFile(Long fileId, HttpServletResponse response);

    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @return 访问URL
     */
    String getFileUrl(Long fileId);

    // ==================== 文件查询操作 ====================

    /**
     * 根据ID获取文件信息
     * 
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileVO getFileById(Long fileId);

    /**
     * 分页查询文件列表
     * 
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param fileType 文件类型
     * @param keyword  搜索关键词
     * @return 文件分页列表
     */
    PageResult<FileVO> getFileList(Integer pageNum, Integer pageSize, String fileType, String keyword);

    /**
     * 分页查询文件列表（使用PageQueryVO）
     * 
     * @param pageQueryVO 分页查询参数
     * @return 文件分页列表
     */
    PageResult<FileVO> getFileList(PageQueryVO pageQueryVO);

    /**
     * 根据用户ID查询文件列表
     * 
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 文件分页列表
     */
    PageResult<FileVO> getFilesByUserId(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 根据文件类型查询文件列表
     * 
     * @param fileType 文件类型
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 文件分页列表
     */
    PageResult<FileVO> getFilesByType(String fileType, Integer pageNum, Integer pageSize);

    // ==================== 文件管理操作 ====================

    /**
     * 更新文件信息
     * 
     * @param fileId      文件ID
     * @param filename    新文件名
     * @param description 文件描述
     * @return 文件信息
     */
    FileVO updateFileInfo(Long fileId, String filename, String description);

    /**
     * 删除文件（逻辑删除）
     * 
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     * 
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    Boolean batchDeleteFiles(List<Long> fileIds);

    /**
     * 永久删除文件（物理删除）
     * 
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean permanentDeleteFile(Long fileId);

    /**
     * 恢复已删除的文件
     * 
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean restoreFile(Long fileId);

    // ==================== 管理员操作 ====================

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
    PageResult<FileVO> adminPageFiles(Integer pageNum, Integer pageSize, String fileType, String keyword, Long userId);

    /**
     * 管理员删除文件
     * 
     * @param fileId 文件ID
     * @param reason 删除原因
     * @return 是否成功
     */
    Boolean adminDeleteFile(Long fileId, String reason);

    /**
     * 获取文件统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getFileStatistics();

    /**
     * 获取存储空间信息
     * 
     * @return 存储空间信息
     */
    Map<String, Object> getStorageInfo();

    // ==================== 数据维护操作 ====================

    /**
     * 清理无效文件
     * 清理已删除超过指定天数的文件记录
     * 
     * @param days 天数
     * @return 清理的文件数量
     */
    Integer cleanupInvalidFiles(Integer days);

    /**
     * 同步文件大小统计
     * 重新计算用户和系统的文件大小统计
     * 
     * @return 同步的文件数量
     */
    Integer syncFileSizeStatistics();

    /**
     * 检查文件完整性
     * 检查数据库记录与实际存储文件的一致性
     * 
     * @return 检查结果
     */
    Map<String, Object> checkFileIntegrity();

    /**
     * 更新文件下载次数
     * 
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean incrementDownloadCount(Long fileId);

    // ==================== 文件验证和工具 ====================

    /**
     * 验证文件类型是否允许
     * 
     * @param file     文件
     * @param fileType 文件类型
     * @return 是否允许
     */
    Boolean validateFileType(MultipartFile file, String fileType);

    /**
     * 验证文件大小是否超限
     * 
     * @param file     文件
     * @param fileType 文件类型
     * @return 是否超限
     */
    Boolean validateFileSize(MultipartFile file, String fileType);

    /**
     * 生成唯一文件名
     * 
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    String generateUniqueFileName(String originalFilename);

    /**
     * 获取文件类型分类
     * 
     * @param mimeType MIME类型
     * @return 文件类型分类
     */
    Integer getFileTypeCategory(String mimeType);

    // ==================== 数据转换方法 ====================

    /**
     * 将TbFile转换为FileVO
     * 
     * @param file 文件实体
     * @return 文件VO
     */
    FileVO convertToVO(TbFile file);

    /**
     * 将TbFile列表转换为FileVO列表
     * 
     * @param files 文件实体列表
     * @return 文件VO列表
     */
    List<FileVO> convertToVOList(List<TbFile> files);
}
