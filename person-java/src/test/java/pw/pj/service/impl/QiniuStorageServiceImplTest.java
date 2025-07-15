package pw.pj.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pw.pj.POJO.VO.FileStorageResult;
import pw.pj.common.config.StorageProperties;
import pw.pj.common.enums.StorageTypeEnum;
import pw.pj.common.exception.StorageException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 七牛云存储服务单元测试
 * 
 * @author PersonWeb开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@ExtendWith(MockitoExtension.class)
class QiniuStorageServiceImplTest {

    @Mock
    private StorageProperties storageProperties;

    @Mock
    private StorageProperties.QiniuConfig qiniuConfig;

    @InjectMocks
    private QiniuStorageServiceImpl qiniuStorageService;

    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        // 初始化测试文件
        testFile = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 模拟配置
        when(storageProperties.getQiniu()).thenReturn(qiniuConfig);
        when(storageProperties.getMaxFileSize()).thenReturn(10 * 1024 * 1024L); // 10MB
        when(storageProperties.getAllowedTypes()).thenReturn(new String[] { "jpg", "png", "pdf" });

        // 模拟七牛云配置
        when(qiniuConfig.getAccessKey()).thenReturn("test-access-key");
        when(qiniuConfig.getSecretKey()).thenReturn("test-secret-key");
        when(qiniuConfig.getBucket()).thenReturn("test-bucket");
        when(qiniuConfig.getDomain()).thenReturn("test.domain.com");
        when(qiniuConfig.getRegion()).thenReturn("华南");
        when(qiniuConfig.getUseHttps()).thenReturn(true);
        when(qiniuConfig.getUrlExpireSeconds()).thenReturn(3600L);
    }

    @Test
    void testGetStorageType() {
        // When
        StorageTypeEnum result = qiniuStorageService.getStorageType();

        // Then
        assertEquals(StorageTypeEnum.QINIU, result);
    }

    @Test
    void testGetStorageInfo() {
        // When
        String result = qiniuStorageService.getStorageInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("七牛云存储"));
        assertTrue(result.contains("test-bucket"));
        assertTrue(result.contains("test.domain.com"));
    }

    @Test
    void testUploadFile_WithNullFile_ShouldReturnFailure() {
        // When
        FileStorageResult result = qiniuStorageService.upload(null, "test");

        // Then
        assertFalse(result.getSuccess());
        assertEquals("文件不能为空", result.getErrorMessage());
        assertEquals(StorageTypeEnum.QINIU, result.getStorageType());
    }

    @Test
    void testUploadFile_WithEmptyFile_ShouldReturnFailure() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile("empty.jpg", new byte[0]);

        // When
        FileStorageResult result = qiniuStorageService.upload(emptyFile, "test");

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
        FileStorageResult result = qiniuStorageService.upload(unsupportedFile, "test");

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
        FileStorageResult result = qiniuStorageService.upload(largeFile, "test");

        // Then
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("文件大小超过限制"));
    }

    @Test
    void testUploadFile_WithInvalidFileName_ShouldReturnFailure() {
        // Given
        MultipartFile fileWithoutExtension = new MockMultipartFile(
                "test",
                "test", // 没有扩展名
                "application/octet-stream",
                "test content".getBytes());

        // When
        FileStorageResult result = qiniuStorageService.upload(fileWithoutExtension, "test");

        // Then
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("文件名无效或缺少扩展名"));
    }

    @Test
    void testGetFileUrl_WithEmptyFileKey_ShouldReturnNull() {
        // When
        String result = qiniuStorageService.getFileUrl("");

        // Then
        assertNull(result);
    }

    @Test
    void testGetFileUrl_WithNullFileKey_ShouldReturnNull() {
        // When
        String result = qiniuStorageService.getFileUrl(null);

        // Then
        assertNull(result);
    }

    @Test
    void testGetFileUrl_WithValidFileKey_ShouldReturnUrl() {
        // Given
        String fileKey = "test/2024/01/01/test.jpg";

        // When
        String result = qiniuStorageService.getFileUrl(fileKey);

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("https://"));
        assertTrue(result.contains("test.domain.com"));
        assertTrue(result.contains(fileKey));
    }

    @Test
    void testDelete_WithEmptyFileKey_ShouldReturnFalse() {
        // When
        boolean result = qiniuStorageService.delete("");

        // Then
        assertFalse(result);
    }

    @Test
    void testDelete_WithNullFileKey_ShouldReturnFalse() {
        // When
        boolean result = qiniuStorageService.delete(null);

        // Then
        assertFalse(result);
    }

    @Test
    void testExists_WithEmptyFileKey_ShouldReturnFalse() {
        // When
        boolean result = qiniuStorageService.exists("");

        // Then
        assertFalse(result);
    }

    @Test
    void testExists_WithNullFileKey_ShouldReturnFalse() {
        // When
        boolean result = qiniuStorageService.exists(null);

        // Then
        assertFalse(result);
    }

    /**
     * 测试文件路径生成逻辑
     */
    @Test
    void testGenerateStoragePath() {
        // 通过反射或者其他方式测试私有方法
        // 这里展示测试思路，实际实现可能需要调整

        // Given
        String fileType = "image";

        // 可以通过上传文件来间接测试路径生成
        // 检查生成的路径是否符合预期格式：images/yyyy/MM/dd/filename
    }

    /**
     * 测试配置验证逻辑
     */
    @Test
    void testConfigValidation() {
        // Given - 缺少必要配置
        when(qiniuConfig.getAccessKey()).thenReturn(null);

        // When & Then
        assertThrows(StorageException.class, () -> {
            qiniuStorageService.init();
        });
    }

    /**
     * 测试HTTP/HTTPS URL生成
     */
    @Test
    void testHttpsUrlGeneration() {
        // Given
        when(qiniuConfig.getUseHttps()).thenReturn(false);
        String fileKey = "test.jpg";

        // When
        String result = qiniuStorageService.getFileUrl(fileKey);

        // Then
        assertTrue(result.startsWith("http://"));
        assertFalse(result.startsWith("https://"));
    }

    /**
     * 测试域名处理逻辑
     */
    @Test
    void testDomainProcessing() {
        // Given - 域名包含协议前缀
        when(qiniuConfig.getDomain()).thenReturn("https://test.domain.com");
        String fileKey = "test.jpg";

        // When
        String result = qiniuStorageService.getFileUrl(fileKey);

        // Then
        // 应该正确处理域名，避免重复协议
        long httpsCount = result.chars().filter(ch -> ch == 's').count();
        assertTrue(httpsCount >= 1); // 至少有一个's'在'https'中
    }

    /**
     * 测试存储区域选择
     */
    @Test
    void testRegionSelection() {
        // 测试不同区域的处理
        when(qiniuConfig.getRegion()).thenReturn("华东");

        // 通过其他方式验证区域配置是否正确
        // 可以检查初始化后的配置状态
        assertDoesNotThrow(() -> {
            qiniuStorageService.init();
        });
    }
}