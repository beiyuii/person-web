package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户信息更新请求VO
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class UserUpdateVO {

    /**
     * 昵称
     */
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;

    /**
     * 性别：0-保密，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime birthday;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String introduction;

    /**
     * 个人网站
     */
    @Size(max = 255, message = "个人网站URL长度不能超过255个字符")
    @Pattern(regexp = "^https?://.*", message = "个人网站必须是有效的HTTP/HTTPS链接")
    private String website;

    /**
     * 所在地区
     */
    @Size(max = 100, message = "所在地区长度不能超过100个字符")
    private String location;

    /**
     * 原密码（修改密码时需要）
     */
    private String oldPassword;

    /**
     * 新密码（修改密码时需要）
     */
    @Size(min = 6, max = 32, message = "新密码长度必须在6-32个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$", message = "新密码必须包含至少一个大写字母、一个小写字母和一个数字")
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmNewPassword;

    /**
     * 邮箱验证码（修改邮箱时需要）
     */
    private String emailCode;

    /**
     * 手机验证码（修改手机时需要）
     */
    private String phoneCode;

    /**
     * 验证新密码是否一致
     * 
     * @return 是否一致
     */
    public boolean isNewPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    /**
     * 判断是否要修改密码
     * 
     * @return 是否要修改密码
     */
    public boolean isChangePassword() {
        return oldPassword != null && !oldPassword.trim().isEmpty()
                && newPassword != null && !newPassword.trim().isEmpty();
    }

    /**
     * 判断是否要修改邮箱
     * 
     * @return 是否要修改邮箱
     */
    public boolean isChangeEmail() {
        return email != null && !email.trim().isEmpty()
                && emailCode != null && !emailCode.trim().isEmpty();
    }

    /**
     * 判断是否要修改手机号
     * 
     * @return 是否要修改手机号
     */
    public boolean isChangePhone() {
        return phone != null && !phone.trim().isEmpty()
                && phoneCode != null && !phoneCode.trim().isEmpty();
    }

    /**
     * 验证性别值是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidGender() {
        return gender == null || (gender >= 0 && gender <= 2);
    }

    /**
     * 验证生日是否合法（不能是未来时间）
     * 
     * @return 是否合法
     */
    public boolean isValidBirthday() {
        return birthday == null || birthday.isBefore(LocalDateTime.now());
    }

    /**
     * 清除密码相关字段（用于日志记录等安全场景）
     */
    public void clearPasswordFields() {
        this.oldPassword = null;
        this.newPassword = null;
        this.confirmNewPassword = null;
    }
}