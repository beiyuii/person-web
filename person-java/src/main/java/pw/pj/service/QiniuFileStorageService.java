package pw.pj.service;

import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.DO.TbFile;
import pw.pj.POJO.VO.response.FileUploadResult;
import pw.pj.config.QiniuConfig;

import java.util.Date;
import java.util.UUID;

/**
 * 七牛云文件存储服务
 * 基于七牛云存储的文件上传服务
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Service
@Slf4j
public class QiniuFileStorageService {

    @Autowired
    private QiniuConfig qiniuConfig;

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private TbFileService tbFileService;

    /**
     * 上传文件到七牛云
     * 
     * @param file   上传的文件
     * @param module 模块名称（如：avatar, article, attachment）
     * @return 文件上传结果
     */
    public FileUploadResult uploadFile(MultipartFile file, String module) {
        FileUploadResult result = new FileUploadResult();

        if (file == null || file.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("文件为空");
            return result;
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = generateFileName(originalFilename);
            String filePath = module + "/" + fileName;

            // 上传到七牛云
            String qiniuUrl = uploadToQiniu(file, filePath);

            // 创建文件记录对象
            TbFile tbFile = new TbFile();
            tbFile.setOriginalName(originalFilename);
            tbFile.setFileName(fileName);
            tbFile.setFilePath(filePath);
            tbFile.setFileUrl(qiniuUrl);
            tbFile.setFileType(getFileExtension(originalFilename));
            tbFile.setFileExtension(getFileExtension(originalFilename));
            tbFile.setFileSize(file.getSize());
            tbFile.setMimeType(file.getContentType());
            tbFile.setStorageType(3); // 七牛云存储
            tbFile.setBucketName(qiniuConfig.getBucketName());
            tbFile.setIsPublic(1);
            tbFile.setDownloadCount(0);
            tbFile.setStatus(1);
            tbFile.setCreateTime(new Date());
            tbFile.setUpdateTime(new Date());
            tbFile.setIsDelete(0);

            // 保存文件记录到数据库
            tbFileService.save(tbFile);

            // 设置返回结果
            result.setSuccess(true);
            result.setMessage("文件上传成功");
            result.setFileId(tbFile.getId().toString());
            result.setOriginalName(originalFilename);
            result.setFileName(fileName);
            result.setFilePath(filePath);
            result.setFileSize(file.getSize());
            result.setQiniuUrl(qiniuUrl);
            result.setAccessUrl(qiniuUrl);

            log.info("文件上传成功: {} -> {}", originalFilename, qiniuUrl);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            result.setSuccess(false);
            result.setMessage("文件上传失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 是否成功
     */
    public boolean deleteFile(String fileId) {
        try {
            TbFile tbFile = tbFileService.getById(fileId);
            if (tbFile == null) {
                return false;
            }

            // 删除七牛云文件
            bucketManager.delete(qiniuConfig.getBucketName(), tbFile.getFilePath());

            // 删除数据库记录
            tbFileService.removeById(fileId);

            log.info("文件删除成功: {}", tbFile.getFilePath());
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", fileId, e);
            return false;
        }
    }

    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @return 访问URL
     */
    public String getFileUrl(String fileId) {
        TbFile tbFile = tbFileService.getById(fileId);
        if (tbFile == null) {
            return null;
        }
        return tbFile.getFileUrl();
    }

    /**
     * 根据文件路径获取访问URL
     * 
     * @param filePath 文件路径
     * @return 访问URL
     */
    public String getFileUrlByPath(String filePath) {
        return qiniuConfig.getFileUrl(filePath);
    }

    /**
     * 七牛云上传
     * 
     * @param file     文件
     * @param filePath 文件路径
     * @return 七牛云URL
     */
    private String uploadToQiniu(MultipartFile file, String filePath) throws Exception {
        String upToken = qiniuConfig.getUploadToken();

        Response response = uploadManager.put(
                file.getInputStream(),
                filePath,
                upToken,
                null,
                null);

        if (response.isOK()) {
            return qiniuConfig.getFileUrl(filePath);
        } else {
            throw new RuntimeException("七牛云上传失败: " + response.bodyString());
        }
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "." + extension;
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
}