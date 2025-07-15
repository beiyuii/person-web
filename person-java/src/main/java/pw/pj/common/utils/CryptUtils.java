package pw.pj.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具类
 * 提供密码加密、解密和验证功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
public class CryptUtils {

    /**
     * 默认盐值长度
     */
    private static final int DEFAULT_SALT_LENGTH = 16;

    /**
     * 默认密钥长度
     */
    private static final int DEFAULT_KEY_LENGTH = 128;

    /**
     * AES算法
     */
    private static final String AES_ALGORITHM = "AES";

    /**
     * AES/ECB/PKCS5Padding算法
     */
    private static final String AES_ECB_PKCS5_PADDING = "AES/ECB/PKCS5Padding";

    /**
     * 生成随机盐值
     * 
     * @return 随机盐值
     */
    public static String generateSalt() {
        return generateSalt(DEFAULT_SALT_LENGTH);
    }

    /**
     * 生成指定长度的随机盐值
     * 
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * MD5加密
     * 
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String md5(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5加密失败", e);
            return "";
        }
    }

    /**
     * SHA-1加密
     * 
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String sha1(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1加密失败", e);
            return "";
        }
    }

    /**
     * SHA-256加密
     * 
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String sha256(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256加密失败", e);
            return "";
        }
    }

    /**
     * SHA-512加密
     * 
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String sha512(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-512加密失败", e);
            return "";
        }
    }

    /**
     * 使用Spring的DigestUtils进行MD5加密
     * 
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String md5Hex(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        return DigestUtils.md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 密码加密（使用MD5 + 盐值）
     * 
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, String salt) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(salt)) {
            return "";
        }
        return md5(password + salt);
    }

    /**
     * 密码加密（自动生成盐值）
     * 
     * @param password 原始密码
     * @return 加密后的密码和盐值，格式：加密密码$盐值
     */
    public static String encryptPasswordWithSalt(String password) {
        if (StringUtils.isBlank(password)) {
            return "";
        }
        String salt = generateSalt();
        String encryptedPassword = encryptPassword(password, salt);
        return encryptedPassword + "$" + salt;
    }

    /**
     * 验证密码
     * 
     * @param password              原始密码
     * @param encryptedPasswordData 加密后的密码数据（格式：加密密码$盐值）
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String encryptedPasswordData) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(encryptedPasswordData)) {
            return false;
        }

        String[] parts = encryptedPasswordData.split("\\$");
        if (parts.length != 2) {
            return false;
        }

        String encryptedPassword = parts[0];
        String salt = parts[1];

        String testPassword = encryptPassword(password, salt);
        return encryptedPassword.equals(testPassword);
    }

    /**
     * 使用SHA-256加密密码
     * 
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public static String encryptPasswordSha256(String password, String salt) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(salt)) {
            return "";
        }
        return sha256(password + salt);
    }

    /**
     * 使用SHA-256加密密码（自动生成盐值）
     * 
     * @param password 原始密码
     * @return 加密后的密码和盐值，格式：加密密码$盐值
     */
    public static String encryptPasswordSha256WithSalt(String password) {
        if (StringUtils.isBlank(password)) {
            return "";
        }
        String salt = generateSalt();
        String encryptedPassword = encryptPasswordSha256(password, salt);
        return encryptedPassword + "$" + salt;
    }

    /**
     * 验证SHA-256加密的密码
     * 
     * @param password              原始密码
     * @param encryptedPasswordData 加密后的密码数据（格式：加密密码$盐值）
     * @return 是否匹配
     */
    public static boolean verifyPasswordSha256(String password, String encryptedPasswordData) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(encryptedPasswordData)) {
            return false;
        }

        String[] parts = encryptedPasswordData.split("\\$");
        if (parts.length != 2) {
            return false;
        }

        String encryptedPassword = parts[0];
        String salt = parts[1];

        String testPassword = encryptPasswordSha256(password, salt);
        return encryptedPassword.equals(testPassword);
    }

    /**
     * 生成AES密钥
     * 
     * @return AES密钥
     */
    public static String generateAesKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(DEFAULT_KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("生成AES密钥失败", e);
            return "";
        }
    }

    /**
     * AES加密
     * 
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     */
    public static String aesEncrypt(String data, String key) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
            return "";
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            return "";
        }
    }

    /**
     * AES解密
     * 
     * @param encryptedData 加密后的数据
     * @param key           密钥
     * @return 解密后的数据
     */
    public static String aesDecrypt(String encryptedData, String key) {
        if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(key)) {
            return "";
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            return "";
        }
    }

    /**
     * Base64编码
     * 
     * @param data 待编码数据
     * @return 编码后的字符串
     */
    public static String base64Encode(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     * 
     * @param encodedData 编码后的数据
     * @return 解码后的字符串
     */
    public static String base64Decode(String encodedData) {
        if (StringUtils.isBlank(encodedData)) {
            return "";
        }
        try {
            return new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64解码失败", e);
            return "";
        }
    }

    /**
     * URL安全的Base64编码
     * 
     * @param data 待编码数据
     * @return 编码后的字符串
     */
    public static String base64UrlEncode(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * URL安全的Base64解码
     * 
     * @param encodedData 编码后的数据
     * @return 解码后的字符串
     */
    public static String base64UrlDecode(String encodedData) {
        if (StringUtils.isBlank(encodedData)) {
            return "";
        }
        try {
            return new String(Base64.getUrlDecoder().decode(encodedData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64 URL解码失败", e);
            return "";
        }
    }

    /**
     * 生成随机字符串（用于验证码等）
     * 
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        return StringUtils.generateRandomString(length);
    }

    /**
     * 生成随机数字字符串（用于验证码等）
     * 
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    public static String generateRandomNumbers(int length) {
        return StringUtils.generateRandomNumbers(length);
    }

    /**
     * 生成UUID
     * 
     * @return UUID字符串
     */
    public static String generateUuid() {
        return StringUtils.generateUuid();
    }

    /**
     * 生成短UUID
     * 
     * @return 短UUID字符串
     */
    public static String generateShortUuid() {
        return StringUtils.generateShortUuid();
    }

    /**
     * 简单的异或加密
     * 
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     */
    public static String xorEncrypt(String data, String key) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            result.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
        }
        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 简单的异或解密
     * 
     * @param encryptedData 加密后的数据
     * @param key           密钥
     * @return 解密后的数据
     */
    public static String xorDecrypt(String encryptedData, String key) {
        if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(key)) {
            return "";
        }
        try {
            String data = new String(Base64.getDecoder().decode(encryptedData), StandardCharsets.UTF_8);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < data.length(); i++) {
                result.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
            }
            return result.toString();
        } catch (Exception e) {
            log.error("异或解密失败", e);
            return "";
        }
    }

    /**
     * 字节数组转十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 十六进制字符串转字节数组
     * 
     * @param hex 十六进制字符串
     * @return 字节数组
     */
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}