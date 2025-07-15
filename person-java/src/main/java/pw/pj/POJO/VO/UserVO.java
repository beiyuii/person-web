package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息响应VO
 * 用于前端显示用户信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号（脱敏处理）
     */
    private String phone;

    /**
     * 头像URL
     */
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
    private String introduction;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 所在地区
     */
    private String location;

    /**
     * 用户状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 用户类型：0-普通用户，1-管理员
     */
    private Integer userType;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 用户权限列表
     */
    private List<String> permissions;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 粉丝数量
     */
    private Integer followersCount;

    /**
     * 关注数量
     */
    private Integer followingCount;

    /**
     * 点赞数量
     */
    private Integer likesCount;

    /**
     * 收藏数量
     */
    private Integer collectionsCount;

    /**
     * 访问量
     */
    private Integer visitCount;

    /**
     * 是否已关注（当前用户视角）
     */
    private Boolean isFollowed;

    /**
     * 是否为当前用户本人
     */
    private Boolean isCurrentUser;

    /**
     * 获取脱敏后的手机号
     * 
     * @param phone 原始手机号
     * @return 脱敏后的手机号
     */
    public static String getMaskedPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 获取脱敏后的邮箱
     * 
     * @param email 原始邮箱
     * @return 脱敏后的邮箱
     */
    public static String getMaskedEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return username.charAt(0) + "*@" + domain;
        } else {
            return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
        }
    }

    /**
     * 获取性别文本描述
     * 
     * @return 性别文本
     */
    public String getGenderText() {
        if (gender == null) {
            return "保密";
        }
        switch (gender) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "保密";
        }
    }

    /**
     * 获取用户状态文本描述
     * 
     * @return 状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "正常";
            case 1:
                return "禁用";
            default:
                return "未知";
        }
    }

    /**
     * 获取用户类型文本描述
     * 
     * @return 类型文本
     */
    public String getUserTypeText() {
        if (userType == null) {
            return "普通用户";
        }
        switch (userType) {
            case 0:
                return "普通用户";
            case 1:
                return "管理员";
            default:
                return "普通用户";
        }
    }

    /**
     * 判断是否为管理员
     * 
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        return userType != null && userType == 1;
    }

    /**
     * 判断用户是否正常状态
     * 
     * @return 是否正常
     */
    public boolean isNormal() {
        return status != null && status == 0;
    }
}