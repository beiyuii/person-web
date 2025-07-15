package pw.pj.POJO.VO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录请求VO
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class LoginVO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码UUID（用于验证码验证）
     */
    private String captchaUuid;

    /**
     * 记住我（7天免登录）
     */
    private Boolean rememberMe = false;

    /**
     * 登录设备信息
     */
    private String deviceInfo;

    /**
     * 客户端IP地址
     */
    private String clientIp;
}