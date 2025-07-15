package pw.pj.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 * 提供字符串的各种常用操作方法
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
public class StringUtils {

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 下划线字符
     */
    public static final String UNDERSCORE = "_";

    /**
     * 减号字符
     */
    public static final String DASH = "-";

    /**
     * 点号字符
     */
    public static final String DOT = ".";

    /**
     * 斜杠字符
     */
    public static final String SLASH = "/";

    /**
     * 换行符
     */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 随机字符串字符集
     */
    private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String RANDOM_NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 判断字符串是否为空
     * 
     * @param str 待检查的字符串
     * @return 如果字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否非空
     * 
     * @param str 待检查的字符串
     * @return 如果字符串不为null且不为空字符串，返回true
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     * 
     * @param str 待检查的字符串
     * @return 如果字符串为null、空字符串或只包含空白字符，返回true
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空白
     * 
     * @param str 待检查的字符串
     * @return 如果字符串不为null、不为空字符串且不只包含空白字符，返回true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 获取字符串，如果为null则返回默认值
     * 
     * @param str          字符串
     * @param defaultValue 默认值
     * @return 字符串或默认值
     */
    public static String defaultIfNull(String str, String defaultValue) {
        return str != null ? str : defaultValue;
    }

    /**
     * 获取字符串，如果为空白则返回默认值
     * 
     * @param str          字符串
     * @param defaultValue 默认值
     * @return 字符串或默认值
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isNotBlank(str) ? str : defaultValue;
    }

    /**
     * 安全的trim操作
     * 
     * @param str 字符串
     * @return trim后的字符串，如果原字符串为null则返回null
     */
    public static String trim(String str) {
        return str != null ? str.trim() : null;
    }

    /**
     * 安全的trim操作，返回空字符串而不是null
     * 
     * @param str 字符串
     * @return trim后的字符串，如果原字符串为null则返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str != null ? str.trim() : EMPTY;
    }

    /**
     * 截取字符串，支持中文
     * 
     * @param str    字符串
     * @param length 截取长度
     * @return 截取后的字符串
     */
    public static String substring(String str, int length) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length);
    }

    /**
     * 截取字符串并添加省略号
     * 
     * @param str    字符串
     * @param length 截取长度
     * @param suffix 后缀（如"..."）
     * @return 截取后的字符串
     */
    public static String abbreviate(String str, int length, String suffix) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length) + defaultIfNull(suffix, "...");
    }

    /**
     * 生成随机字符串
     * 
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_CHARS.charAt(RANDOM.nextInt(RANDOM_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机数字字符串
     * 
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    public static String generateRandomNumbers(int length) {
        if (length <= 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_NUMBERS.charAt(RANDOM.nextInt(RANDOM_NUMBERS.length())));
        }
        return sb.toString();
    }

    /**
     * 驼峰命名转下划线命名
     * 
     * @param camelCase 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String camelToSnake(String camelCase) {
        if (isEmpty(camelCase)) {
            return camelCase;
        }
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 下划线命名转驼峰命名
     * 
     * @param snakeCase 下划线命名字符串
     * @return 驼峰命名字符串
     */
    public static String snakeToCamel(String snakeCase) {
        if (isEmpty(snakeCase)) {
            return snakeCase;
        }
        StringBuilder result = new StringBuilder();
        String[] words = snakeCase.split(UNDERSCORE);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                result.append(word.toLowerCase());
            } else {
                result.append(capitalize(word.toLowerCase()));
            }
        }
        return result.toString();
    }

    /**
     * 首字母大写
     * 
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     * 
     * @param str 字符串
     * @return 首字母小写的字符串
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 反转字符串
     * 
     * @param str 字符串
     * @return 反转后的字符串
     */
    public static String reverse(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 重复字符串
     * 
     * @param str   字符串
     * @param times 重复次数
     * @return 重复后的字符串
     */
    public static String repeat(String str, int times) {
        if (isEmpty(str) || times <= 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(str.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 字符串连接
     * 
     * @param delimiter 分隔符
     * @param elements  元素数组
     * @return 连接后的字符串
     */
    public static String join(String delimiter, String... elements) {
        if (elements == null || elements.length == 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(defaultIfNull(elements[i], EMPTY));
        }
        return sb.toString();
    }

    /**
     * 字符串连接
     * 
     * @param delimiter 分隔符
     * @param elements  元素集合
     * @return 连接后的字符串
     */
    public static String join(String delimiter, Collection<String> elements) {
        if (elements == null || elements.isEmpty()) {
            return EMPTY;
        }
        return join(delimiter, elements.toArray(new String[0]));
    }

    /**
     * 分割字符串
     * 
     * @param str       字符串
     * @param delimiter 分隔符
     * @return 分割后的字符串数组
     */
    public static String[] split(String str, String delimiter) {
        if (isEmpty(str)) {
            return new String[0];
        }
        return str.split(Pattern.quote(delimiter));
    }

    /**
     * 移除字符串前后的指定字符
     * 
     * @param str        字符串
     * @param stripChars 要移除的字符
     * @return 处理后的字符串
     */
    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * 移除字符串开头的指定字符
     * 
     * @param str        字符串
     * @param stripChars 要移除的字符
     * @return 处理后的字符串
     */
    public static String stripStart(String str, String stripChars) {
        if (isEmpty(str) || isEmpty(stripChars)) {
            return str;
        }
        while (str.length() > 0 && stripChars.indexOf(str.charAt(0)) != -1) {
            str = str.substring(1);
        }
        return str;
    }

    /**
     * 移除字符串结尾的指定字符
     * 
     * @param str        字符串
     * @param stripChars 要移除的字符
     * @return 处理后的字符串
     */
    public static String stripEnd(String str, String stripChars) {
        if (isEmpty(str) || isEmpty(stripChars)) {
            return str;
        }
        while (str.length() > 0 && stripChars.indexOf(str.charAt(str.length() - 1)) != -1) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * URL编码
     * 
     * @param str 字符串
     * @return 编码后的字符串
     */
    public static String urlEncode(String str) {
        if (isEmpty(str)) {
            return str;
        }
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("URL编码失败：{}", e.getMessage(), e);
            return str;
        }
    }

    /**
     * URL解码
     * 
     * @param str 字符串
     * @return 解码后的字符串
     */
    public static String urlDecode(String str) {
        if (isEmpty(str)) {
            return str;
        }
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("URL解码失败：{}", e.getMessage(), e);
            return str;
        }
    }

    /**
     * HTML转义
     * 
     * @param str 字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * HTML反转义
     * 
     * @param str 字符串
     * @return 反转义后的字符串
     */
    public static String unescapeHtml(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#x27;", "'");
    }

    /**
     * 移除HTML标签
     * 
     * @param str 字符串
     * @return 移除HTML标签后的字符串
     */
    public static String removeHtmlTags(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("<[^>]+>", EMPTY);
    }

    /**
     * 正则表达式匹配
     * 
     * @param str   字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String str, String regex) {
        if (isEmpty(str) || isEmpty(regex)) {
            return false;
        }
        return Pattern.matches(regex, str);
    }

    /**
     * 正则表达式替换
     * 
     * @param str         字符串
     * @param regex       正则表达式
     * @param replacement 替换字符串
     * @return 替换后的字符串
     */
    public static String replaceAll(String str, String regex, String replacement) {
        if (isEmpty(str) || isEmpty(regex)) {
            return str;
        }
        return str.replaceAll(regex, defaultIfNull(replacement, EMPTY));
    }

    /**
     * 提取数字
     * 
     * @param str 字符串
     * @return 提取的数字字符串
     */
    public static String extractNumbers(String str) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        return str.replaceAll("[^0-9]", EMPTY);
    }

    /**
     * 提取字母
     * 
     * @param str 字符串
     * @return 提取的字母字符串
     */
    public static String extractLetters(String str) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        return str.replaceAll("[^a-zA-Z]", EMPTY);
    }

    /**
     * 计算字符串的字节长度
     * 
     * @param str 字符串
     * @return 字节长度
     */
    public static int getByteLength(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        return str.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * 判断是否包含中文字符
     * 
     * @param str 字符串
     * @return 是否包含中文字符
     */
    public static boolean containsChinese(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 过滤特殊字符，只保留字母、数字、中文
     * 
     * @param str 字符串
     * @return 过滤后的字符串
     */
    public static String filterSpecialChars(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", EMPTY);
    }

    /**
     * 格式化文件大小
     * 
     * @param size 文件大小（字节）
     * @return 格式化后的字符串
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        String[] units = { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 掩码处理（如手机号、邮箱）
     * 
     * @param str      字符串
     * @param start    开始位置
     * @param end      结束位置
     * @param maskChar 掩码字符
     * @return 掩码后的字符串
     */
    public static String mask(String str, int start, int end, char maskChar) {
        if (isEmpty(str) || start < 0 || end >= str.length() || start > end) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = start; i <= end; i++) {
            sb.setCharAt(i, maskChar);
        }
        return sb.toString();
    }

    /**
     * 生成UUID字符串（去掉短横线）
     * 
     * @return UUID字符串
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace(DASH, EMPTY);
    }

    /**
     * 生成短UUID（8位）
     * 
     * @return 短UUID字符串
     */
    public static String generateShortUuid() {
        return generateUuid().substring(0, 8);
    }
}