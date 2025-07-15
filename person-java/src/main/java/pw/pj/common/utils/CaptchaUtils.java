package pw.pj.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 验证码工具类
 * 提供各种验证码生成功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
public class CaptchaUtils {

    /**
     * 默认验证码长度
     */
    private static final int DEFAULT_CODE_LENGTH = 4;

    /**
     * 默认图片宽度
     */
    private static final int DEFAULT_WIDTH = 120;

    /**
     * 默认图片高度
     */
    private static final int DEFAULT_HEIGHT = 40;

    /**
     * 数字字符集
     */
    private static final String NUMBERS = "0123456789";

    /**
     * 字母字符集
     */
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 混合字符集（数字+字母）
     */
    private static final String MIXED = NUMBERS + LETTERS;

    /**
     * 易混淆字符集（去除容易混淆的字符）
     */
    private static final String CLEAR_CHARS = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";

    /**
     * 随机数生成器
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        /** 验证码文本 */
        private String code;
        /** 验证码图片Base64编码 */
        private String imageBase64;

        public CaptchaResult(String code, String imageBase64) {
            this.code = code;
            this.imageBase64 = imageBase64;
        }

        public String getCode() {
            return code;
        }

        public String getImageBase64() {
            return imageBase64;
        }
    }

    /**
     * 生成数字验证码
     * 
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateNumberCode(int length) {
        if (length <= 0) {
            length = DEFAULT_CODE_LENGTH;
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return code.toString();
    }

    /**
     * 生成字母验证码
     * 
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateLetterCode(int length) {
        if (length <= 0) {
            length = DEFAULT_CODE_LENGTH;
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        return code.toString();
    }

    /**
     * 生成混合验证码（数字+字母）
     * 
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateMixedCode(int length) {
        if (length <= 0) {
            length = DEFAULT_CODE_LENGTH;
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(MIXED.charAt(RANDOM.nextInt(MIXED.length())));
        }
        return code.toString();
    }

    /**
     * 生成清晰验证码（去除容易混淆的字符）
     * 
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateClearCode(int length) {
        if (length <= 0) {
            length = DEFAULT_CODE_LENGTH;
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(CLEAR_CHARS.charAt(RANDOM.nextInt(CLEAR_CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 生成数字验证码（默认长度）
     * 
     * @return 验证码字符串
     */
    public static String generateNumberCode() {
        return generateNumberCode(DEFAULT_CODE_LENGTH);
    }

    /**
     * 生成字母验证码（默认长度）
     * 
     * @return 验证码字符串
     */
    public static String generateLetterCode() {
        return generateLetterCode(DEFAULT_CODE_LENGTH);
    }

    /**
     * 生成混合验证码（默认长度）
     * 
     * @return 验证码字符串
     */
    public static String generateMixedCode() {
        return generateMixedCode(DEFAULT_CODE_LENGTH);
    }

    /**
     * 生成清晰验证码（默认长度）
     * 
     * @return 验证码字符串
     */
    public static String generateClearCode() {
        return generateClearCode(DEFAULT_CODE_LENGTH);
    }

    /**
     * 生成图片验证码
     * 
     * @param code   验证码文本
     * @param width  图片宽度
     * @param height 图片高度
     * @return 验证码结果对象
     */
    public static CaptchaResult generateImageCaptcha(String code, int width, int height) {
        if (StringUtils.isBlank(code)) {
            code = generateClearCode();
        }
        if (width <= 0) {
            width = DEFAULT_WIDTH;
        }
        if (height <= 0) {
            height = DEFAULT_HEIGHT;
        }

        try {
            // 创建图片
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 开启抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 填充背景色
            g2d.setColor(getRandomColor(200, 255));
            g2d.fillRect(0, 0, width, height);

            // 绘制干扰线
            drawInterferenceLines(g2d, width, height);

            // 绘制验证码
            drawCode(g2d, code, width, height);

            // 绘制干扰点
            drawInterferencePoints(g2d, width, height);

            g2d.dispose();

            // 转换为Base64
            String imageBase64 = imageToBase64(image);
            return new CaptchaResult(code, imageBase64);

        } catch (Exception e) {
            log.error("生成图片验证码失败", e);
            return new CaptchaResult(code, "");
        }
    }

    /**
     * 生成图片验证码（默认尺寸）
     * 
     * @param code 验证码文本
     * @return 验证码结果对象
     */
    public static CaptchaResult generateImageCaptcha(String code) {
        return generateImageCaptcha(code, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成图片验证码（随机验证码）
     * 
     * @param width  图片宽度
     * @param height 图片高度
     * @return 验证码结果对象
     */
    public static CaptchaResult generateImageCaptcha(int width, int height) {
        return generateImageCaptcha(generateClearCode(), width, height);
    }

    /**
     * 生成图片验证码（默认设置）
     * 
     * @return 验证码结果对象
     */
    public static CaptchaResult generateImageCaptcha() {
        return generateImageCaptcha(generateClearCode(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成数学运算验证码
     * 
     * @return 验证码结果对象，code为运算结果
     */
    public static CaptchaResult generateMathCaptcha() {
        return generateMathCaptcha(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成数学运算验证码
     * 
     * @param width  图片宽度
     * @param height 图片高度
     * @return 验证码结果对象，code为运算结果
     */
    public static CaptchaResult generateMathCaptcha(int width, int height) {
        int num1 = RANDOM.nextInt(10) + 1;
        int num2 = RANDOM.nextInt(10) + 1;
        int operator = RANDOM.nextInt(2); // 0为加法，1为减法

        String expression;
        int result;

        if (operator == 0) {
            expression = num1 + " + " + num2 + " = ?";
            result = num1 + num2;
        } else {
            // 确保减法结果为正数
            if (num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            expression = num1 + " - " + num2 + " = ?";
            result = num1 - num2;
        }

        return generateImageCaptcha(expression, width, height, String.valueOf(result));
    }

    /**
     * 生成自定义表达式的图片验证码
     * 
     * @param expression 表达式
     * @param width      图片宽度
     * @param height     图片高度
     * @param code       验证码答案
     * @return 验证码结果对象
     */
    private static CaptchaResult generateImageCaptcha(String expression, int width, int height, String code) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 填充背景色
            g2d.setColor(getRandomColor(200, 255));
            g2d.fillRect(0, 0, width, height);

            // 绘制干扰线
            drawInterferenceLines(g2d, width, height);

            // 绘制表达式
            drawExpression(g2d, expression, width, height);

            // 绘制干扰点
            drawInterferencePoints(g2d, width, height);

            g2d.dispose();

            String imageBase64 = imageToBase64(image);
            return new CaptchaResult(code, imageBase64);

        } catch (Exception e) {
            log.error("生成数学运算验证码失败", e);
            return new CaptchaResult(code, "");
        }
    }

    /**
     * 生成短信验证码
     * 
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateSmsCode(int length) {
        return generateNumberCode(length);
    }

    /**
     * 生成短信验证码（默认6位）
     * 
     * @return 验证码字符串
     */
    public static String generateSmsCode() {
        return generateSmsCode(6);
    }

    /**
     * 生成邮箱验证码
     * 
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateEmailCode(int length) {
        return generateNumberCode(length);
    }

    /**
     * 生成邮箱验证码（默认6位）
     * 
     * @return 验证码字符串
     */
    public static String generateEmailCode() {
        return generateEmailCode(6);
    }

    /**
     * 验证验证码（忽略大小写）
     * 
     * @param inputCode   用户输入的验证码
     * @param correctCode 正确的验证码
     * @return 是否匹配
     */
    public static boolean validateCode(String inputCode, String correctCode) {
        if (StringUtils.isBlank(inputCode) || StringUtils.isBlank(correctCode)) {
            return false;
        }
        return inputCode.trim().equalsIgnoreCase(correctCode.trim());
    }

    /**
     * 获取随机颜色
     * 
     * @param min 最小值
     * @param max 最大值
     * @return 随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        int r = RANDOM.nextInt(max - min) + min;
        int g = RANDOM.nextInt(max - min) + min;
        int b = RANDOM.nextInt(max - min) + min;
        return new Color(r, g, b);
    }

    /**
     * 绘制干扰线
     * 
     * @param g2d    图形对象
     * @param width  图片宽度
     * @param height 图片高度
     */
    private static void drawInterferenceLines(Graphics2D g2d, int width, int height) {
        g2d.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < 5; i++) {
            g2d.setColor(getRandomColor(100, 200));
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制验证码
     * 
     * @param g2d    图形对象
     * @param code   验证码文本
     * @param width  图片宽度
     * @param height 图片高度
     */
    private static void drawCode(Graphics2D g2d, String code, int width, int height) {
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int codeWidth = fm.stringWidth(code);
        int x = (width - codeWidth) / 2;
        int y = (height + fm.getAscent()) / 2;

        // 绘制每个字符
        for (int i = 0; i < code.length(); i++) {
            g2d.setColor(getRandomColor(30, 150));
            char c = code.charAt(i);
            int charWidth = fm.charWidth(c);

            // 添加随机角度
            g2d.rotate(Math.toRadians(RANDOM.nextInt(21) - 10), x + charWidth / 2, y);
            g2d.drawString(String.valueOf(c), x, y);
            g2d.rotate(Math.toRadians(-(RANDOM.nextInt(21) - 10)), x + charWidth / 2, y);

            x += charWidth + 2;
        }
    }

    /**
     * 绘制表达式
     * 
     * @param g2d        图形对象
     * @param expression 表达式
     * @param width      图片宽度
     * @param height     图片高度
     */
    private static void drawExpression(Graphics2D g2d, String expression, int width, int height) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(getRandomColor(30, 150));
        FontMetrics fm = g2d.getFontMetrics();
        int expressionWidth = fm.stringWidth(expression);
        int x = (width - expressionWidth) / 2;
        int y = (height + fm.getAscent()) / 2;
        g2d.drawString(expression, x, y);
    }

    /**
     * 绘制干扰点
     * 
     * @param g2d    图形对象
     * @param width  图片宽度
     * @param height 图片高度
     */
    private static void drawInterferencePoints(Graphics2D g2d, int width, int height) {
        for (int i = 0; i < 50; i++) {
            g2d.setColor(getRandomColor(150, 200));
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            g2d.fillOval(x, y, 2, 2);
        }
    }

    /**
     * 将图片转换为Base64编码
     * 
     * @param image 图片对象
     * @return Base64编码字符串
     */
    private static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }
}