package pw.pj.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.VO.FileStorageResult;
import pw.pj.common.config.StorageProperties;
import pw.pj.common.enums.StorageTypeEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 本地存储服务单元测试
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@ExtendWith(MockitoExtension.class)
class LocalStorageServiceImplTest {

    @TempDir
    Path tempDir;

    @Mock
    private StorageProperties storageProperties;

    @Mock
    private StorageProperties.LocalConfig localConfig;

    @InjectMocks
    private LocalStorageServiceImpl localStorageService;

    private MultipartFile testFile;

    @BeforeEach
    void setUp() throws IOException {
        // 初始化测试文件
        testFile = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 模拟配置
        when(storageProperties.getLocal()).thenReturn(localConfig);
        when(storageProperties.getMaxFileSize()).thenReturn(10 * 1024 * 1024L); // 10MB
        when(storageProperties.getAllowedTypes()).thenReturn(new String[] { "jpg", "png", "pdf", "txt" });

        // 模拟本地存储配置
        when(localConfig.getRootPath()).thenReturn(tempDir.toString());
        when(localConfig.getUrlPrefix()).thenReturn("/uploads");
        when(localConfig.getCreateDateDir()).thenReturn(true);

        // 手动初始化（模拟@PostConstruct）
        localStorageService.init();
    }

    @Test
    void testGetStorageType() {
        // When
        StorageTypeEnum result = localStorageService.getStorageType();

        // Then
        assertEquals(StorageTypeEnum.LOCAL, result);
    }

    @Test
    void testGetStorageInfo() {
        // When
        String result = localStorageService.getStorageInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("本地磁盘存储"));
        assertTrue(result.contains(tempDir.toString()));
        assertTrue(result.contains("/uploads"));
    }

    @Test
    void testUploadFile_Success() {
        // When
        FileStorageResult result = localStorageService.upload(testFile, "images");

        // Then
        assertTrue(result.getSuccess());
        assertNotNull(result.getFileKey());
        assertNotNull(result.getFileUrl());
        assertEquals("test.jpg", result.getOriginalFileName());
        assertEquals(testFile.getSize(), result.getFileSize());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals(StorageTypeEnum.LOCAL, result.getStorageType());
        assertTrue(result.getFileUrl().startsWith("/uploads"));

        // 验证文件确实被保存
        Path savedFile = tempDir.resolve(result.getFileKey());
        assertTrue(Files.exists(savedFile));
    }

    @Test
    void testUploadFile_WithNullFile_ShouldReturnFailure() {
        // When
        FileStorageResult result = localStorageService.upload(null, "test");

        // Then
        assertFalse(result.getSuccess());
        assertEquals("文件不能为空", result.getErrorMessage());
        assertEquals(StorageTypeEnum.LOCAL, result.getStorageType());
    }

    @Test
    void testUploadFile_WithEmptyFile_ShouldReturnFailure() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile("empty.jpg", new byte[0]);

        // When
        FileStorageResult result = localStorageService.upload(emptyFile, "test");

        // Then
        assertFalse(result.getSuccess());
        assertEquals("文件不能为空", result.getErrorMessage());
    }

    @Test
    void testUploadFile_WithUnsupportedFileType_ShouldReturnFailure() {
        // Given
        MultipartFile unsupportedFile = new MockMultipartFile(
                "test.exe",
                "test.exe",
                "application/exe",
                "test content".getBytes());

        // When
        FileStorageResult result = localStorageService.upload(unsupportedFile, "test");

        // Then
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("不支持的文件类型"));
    }

    @Test
    void testUploadFile_WithOversizedFile_ShouldReturnFailure() {
        // Given
        when(storageProperties.getMaxFileSize()).thenReturn(100L); // 100 bytes limit
        MultipartFile largeFile = new MockMultipartFile(
                "large.jpg",
                "large.jpg",
                "image/jpeg",
                new byte[200] // 200 bytes file
        );

        // When
        FileStorageResult result = localStorageService.upload(largeFile, "test");

        // Then
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("文件大小超过限制"));
    }

    @Test
    void testUploadFile_WithoutDateDir() {
        // Given
        when(localConfig.getCreateDateDir()).thenReturn(false);

        // When
        FileStorageResult result = localStorageService.upload(testFile, "images");

        // Then
        assertTrue(result.getSuccess());

        // 文件路径不应包含日期目录
        String fileKey = result.getFileKey();
        assertFalse(fileKey.matches(".*\\d{4}/\\d{2}/\\d{2}/.*"));
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // Given - 先上传一个文件
        FileStorageResult uploadResult = localStorageService.upload(testFile, "images");
        assertTrue(uploadResult.getSuccess());

        String fileKey = uploadResult.getFileKey();
        Path savedFile = tempDir.resolve(fileKey);
        assertTrue(Files.exists(savedFile));

        // When
        boolean deleteResult = localStorageService.delete(fileKey);

        // Then
        assertTrue(deleteResult);
        assertFalse(Files.exists(savedFile));
    }

    @Test
    void testDeleteFile_WithNonExistentFile_ShouldReturnFalse() {
        // When
        boolean result = localStorageService.delete("non-existent-file.jpg");

        // Then
        assertFalse(result);
    }

    @Test
    void testDeleteFile_WithEmptyFileKey_ShouldReturnFalse() {
        // When
        boolean result = localStorageService.delete("");

        // Then
        assertFalse(result);
    }

    @Test
    void testDeleteFile_WithNullFileKey_ShouldReturnFalse() {
        // When
        boolean result = localStorageService.delete(null);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetFileUrl() {
        // Given
        String fileKey = "images/2024/01/01/test.jpg";

        // When
        String result = localStorageService.getFileUrl(fileKey);

        // Then
        assertEquals("/uploads/" + fileKey, result);
    }

    @Test
    void testGetFileUrl_WithEmptyFileKey_ShouldReturnNull() {
        // When
        String result = localStorageService.getFileUrl("");

        // Then
        assertNull(result);
    }

    @Test
    void testGetFileUrl_WithNullFileKey_ShouldReturnNull() {
        // When
        String result = localStorageService.getFileUrl(null);

        // Then
        assertNull(result);
    }

    @Test
    void testExists_WithExistingFile() throws IOException {
        // Given - 先上传一个文件
        FileStorageResult uploadResult = localStorageService.upload(testFile, "images");
        assertTrue(uploadResult.getSuccess());

        // When
        boolean result = localStorageService.exists(uploadResult.getFileKey());

        // Then
        assertTrue(result);
    }

    @Test
    void testExists_WithNonExistentFile() {
        // When
        boolean result = localStorageService.exists("non-existent-file.jpg");

        // Then
        assertFalse(result);
    }

    @Test
    void testExists_WithEmptyFileKey() {
        // When
        boolean result = localStorageService.exists("");

        // Then
        assertFalse(result);
    }

    @Test
    void testExists_WithNullFileKey() {
        // When
        boolean result = localStorageService.exists(null);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetFilePath() {
        // Given
        String fileKey = "images/test.jpg";

        // When
        Path result = localStorageService.getFilePath(fileKey);

        // Then
        assertNotNull(result);
        assertEquals(tempDir.resolve(fileKey), result);
    }

    @Test
    void testGetFilePath_WithEmptyFileKey_ShouldReturnNull() {
        // When
        Path result = localStorageService.getFilePath("");

        // Then
        assertNull(result);
    }

    @Test
    void testGetRootPath() {
        // When
        Path result = localStorageService.getRootPath();

        // Then
        assertEquals(tempDir, result);
    }

    @Test
    void testGetStorageUsage() throws IOException {
        // Given - 上传几个文件
        localStorageService.upload(testFile, "images");

        MultipartFile textFile = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Hello World".getBytes());
        localStorageService.upload(textFile, "documents");

        // When
        String result = localStorageService.getStorageUsage();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("本地存储使用情况"));
        assertTrue(result.contains("文件数量: 2"));
        assertTrue(result.contains("总大小:"));
        assertTrue(result.contains("MB"));
    }

    @Test
    void testFileValidation_WithInvalidFileName() {
        // Given
        MultipartFile fileWithoutExtension = new MockMultipartFile(
                "test",
                "test", // 没有扩展名
                "application/octet-stream",
                "test content".getBytes());

        // When
        FileStorageResult result = localStorageService.upload(fileWithoutExtension, "test");

        // Then
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("文件名无效或缺少扩展名"));
    }

    @Test
    void testDirectoryCreation() throws IOException {
        // Given
        String path = "nested/directory/structure";

        // When
        FileStorageResult result = localStorageService.upload(testFile, path);

        // Then
        assertTrue(result.getSuccess());

        // 验证目录结构被创建
        Path expectedDir = tempDir.resolve(path);
        assertTrue(Files.exists(expectedDir));
        assertTrue(Files.isDirectory(expectedDir));
    }

    @Test
    void testUrlPrefixHandling_WithoutLeadingSlash() {
        // Given
        when(localConfig.getUrlPrefix()).thenReturn("uploads"); // 没有开头的 /

        // When
        String result = localStorageService.getFileUrl("test.jpg");

        // Then
        assertTrue(result.startsWith("/uploads/")); // 应该自动添加 /
    }

    @Test
    void testUrlPrefixHandling_WithTrailingSlash() {
        // Given
        when(localConfig.getUrlPrefix()).thenReturn("/uploads/"); // 有结尾的 /

        // When
        String result = localStorageService.getFileUrl("test.jpg");

        // Then
        assertEquals("/uploads/test.jpg", result); // 不应该有双斜杠
    }
}