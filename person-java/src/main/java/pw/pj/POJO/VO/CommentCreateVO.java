package pw.pj.POJO.VO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 评论创建请求VO
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class CommentCreateVO {

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /**
     * 父评论ID（0表示顶级评论）
     */
    private Long parentId = 0L;

    /**
     * 回复评论ID（针对具体某条评论的回复）
     */
    private Long replyId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000个字符之间")
    private String content;

    /**
     * 评论者昵称（游客评论时使用）
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 评论者邮箱（游客评论时使用）
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 评论者网站（游客评论时使用）
     */
    @Size(max = 255, message = "网站URL长度不能超过255个字符")
    private String website;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码UUID
     */
    private String captchaUuid;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 评论来源：web、mobile、api
     */
    private String source = "web";

    /**
     * 是否通知作者
     */
    private Boolean notifyAuthor = true;

    /**
     * 是否通知被回复者
     */
    private Boolean notifyReplyUser = true;

    /**
     * 判断是否为顶级评论
     * 
     * @return 是否为顶级评论
     */
    public boolean isTopLevelComment() {
        return parentId == null || parentId == 0L;
    }

    /**
     * 判断是否为子评论
     * 
     * @return 是否为子评论
     */
    public boolean isSubComment() {
        return parentId != null && parentId > 0L;
    }

    /**
     * 判断是否为回复评论
     * 
     * @return 是否为回复评论
     */
    public boolean isReplyComment() {
        return replyId != null && replyId > 0L;
    }

    /**
     * 判断是否为游客评论
     * 
     * @return 是否为游客评论
     */
    public boolean isGuestComment() {
        return nickname != null && !nickname.trim().isEmpty() &&
                email != null && !email.trim().isEmpty();
    }

    /**
     * 验证邮箱格式是否正确
     * 
     * @return 邮箱格式是否正确
     */
    public boolean isValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            return !isGuestComment(); // 如果不是游客评论，邮箱可以为空
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * 验证网站URL格式是否正确
     * 
     * @return URL格式是否正确
     */
    public boolean isValidWebsite() {
        if (website == null || website.trim().isEmpty()) {
            return true; // 网站可以为空
        }

        String urlRegex = "^https?://[^\\s/$.?#].[^\\s]*$";
        return website.matches(urlRegex);
    }

    /**
     * 验证昵称是否合法
     * 
     * @return 昵称是否合法
     */
    public boolean isValidNickname() {
        if (nickname == null || nickname.trim().isEmpty()) {
            return !isGuestComment(); // 如果不是游客评论，昵称可以为空
        }

        // 检查昵称是否包含敏感词或特殊字符
        String cleanNickname = nickname.trim();
        return cleanNickname.length() >= 2 && cleanNickname.length() <= 50 &&
                !cleanNickname.matches(".*[<>\"'&].*"); // 不包含HTML特殊字符
    }

    /**
     * 验证评论内容是否合法
     * 
     * @return 内容是否合法
     */
    public boolean isValidContent() {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        String cleanContent = content.trim();
        return cleanContent.length() >= 1 && cleanContent.length() <= 1000;
    }

    /**
     * 获取安全的评论内容（过滤HTML标签）
     * 
     * @return 安全的评论内容
     */
    public String getSafeContent() {
        if (content == null) {
            return "";
        }

        // 简单的HTML标签过滤，实际项目中应使用专业的HTML过滤库
        return content.replaceAll("<[^>]*>", "")
                .replaceAll("&[^;]*;", "")
                .trim();
    }

    /**
     * 获取安全的昵称（过滤特殊字符）
     * 
     * @return 安全的昵称
     */
    public String getSafeNickname() {
        if (nickname == null) {
            return "";
        }

        return nickname.replaceAll("[<>\"'&]", "").trim();
    }

    /**
     * 获取安全的网站URL
     * 
     * @return 安全的网站URL
     */
    public String getSafeWebsite() {
        if (website == null || website.trim().isEmpty()) {
            return "";
        }

        String cleanWebsite = website.trim();
        if (!cleanWebsite.startsWith("http://") && !cleanWebsite.startsWith("https://")) {
            cleanWebsite = "http://" + cleanWebsite;
        }

        return cleanWebsite;
    }

    /**
     * 计算评论内容字数
     * 
     * @return 内容字数
     */
    public int getContentLength() {
        if (content == null) {
            return 0;
        }
        return content.trim().length();
    }

    /**
     * 判断是否需要验证码
     * 
     * @return 是否需要验证码
     */
    public boolean needCaptcha() {
        return isGuestComment() || source.equals("api");
    }

    /**
     * 验证验证码是否有效
     * 
     * @return 验证码是否有效
     */
    public boolean isValidCaptcha() {
        if (!needCaptcha()) {
            return true;
        }

        return captcha != null && !captcha.trim().isEmpty() &&
                captchaUuid != null && !captchaUuid.trim().isEmpty();
    }

    /**
     * 判断是否应该通知作者
     * 
     * @return 是否通知作者
     */
    public boolean shouldNotifyAuthor() {
        return Boolean.TRUE.equals(notifyAuthor);
    }

    /**
     * 判断是否应该通知被回复者
     * 
     * @return 是否通知被回复者
     */
    public boolean shouldNotifyReplyUser() {
        return Boolean.TRUE.equals(notifyReplyUser) && isReplyComment();
    }

    /**
     * 获取评论层级
     * 
     * @return 评论层级
     */
    public int getCommentLevel() {
        if (isTopLevelComment()) {
            return 1;
        } else if (isSubComment()) {
            return 2;
        } else {
            return 3; // 最多支持三级评论
        }
    }
}