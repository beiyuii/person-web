package pw.pj.POJO.VO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册请求VO
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class RegisterVO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和横线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$", message = "密码必须包含至少一个大写字母、一个小写字母和一个数字")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 昵称
     */
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    private String nickname;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码不能为空")
    @Size(min = 6, max = 6, message = "邮箱验证码长度为6位")
    private String emailCode;

    /**
     * 图片验证码
     */
    @NotBlank(message = "图片验证码不能为空")
    private String captcha;

    /**
     * 验证码UUID
     */
    @NotBlank(message = "验证码UUID不能为空")
    private String captchaUuid;

    /**
     * 同意用户协议
     */
    private Boolean agreeTerms = false;

    /**
     * 注册来源（web、mobile、api等）
     */
    private String source = "web";

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 推荐人用户名（可选）
     */
    private String referrer;

    /**
     * 验证两次密码是否一致
     * 
     * @return 是否一致
     */
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * 验证是否同意用户协议
     * 
     * @return 是否同意
     */
    public boolean hasAgreedTerms() {
        return Boolean.TRUE.equals(agreeTerms);
    }
}