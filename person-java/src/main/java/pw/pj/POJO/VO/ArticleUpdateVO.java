package pw.pj.POJO.VO;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 文章更新请求VO
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class ArticleUpdateVO {

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long id;

    /**
     * 文章标题
     */
    @Size(min = 1, max = 100, message = "文章标题长度必须在1-100个字符之间")
    private String title;

    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;

    /**
     * 文章内容
     */
    @Size(min = 10, message = "文章内容至少需要10个字符")
    private String content;

    /**
     * 文章封面图片URL
     */
    @Size(max = 255, message = "封面图片URL长度不能超过255个字符")
    private String coverImage;

    /**
     * 文章状态：0-草稿，1-已发布，2-已撤回
     */
    private Integer status;

    /**
     * 文章类型：0-原创，1-转载，2-翻译
     */
    private Integer type;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommend;

    /**
     * 转载文章的原文链接
     */
    @Size(max = 255, message = "原文链接长度不能超过255个字符")
    private String originalUrl;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 标签名称列表（用于新建标签）
     */
    private List<String> tagNames;

    /**
     * SEO关键词
     */
    @Size(max = 255, message = "SEO关键词长度不能超过255个字符")
    private String keywords;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    private String description;

    /**
     * 是否允许评论：0-否，1-是
     */
    private Integer allowComment;

    /**
     * 是否开启打赏：0-否，1-是
     */
    private Integer allowReward;

    /**
     * 发布密码（私密文章）
     */
    private String password;

    /**
     * 更新原因（可选）
     */
    @Size(max = 500, message = "更新原因长度不能超过500个字符")
    private String updateReason;

    /**
     * 是否发送更新通知给关注者
     */
    private Boolean sendNotification = false;

    /**
     * 验证文章状态是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidStatus() {
        return status == null || (status >= 0 && status <= 2);
    }

    /**
     * 验证文章类型是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidType() {
        return type == null || (type >= 0 && type <= 2);
    }

    /**
     * 验证是否置顶值是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidIsTop() {
        return isTop == null || (isTop == 0 || isTop == 1);
    }

    /**
     * 验证是否推荐值是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidIsRecommend() {
        return isRecommend == null || (isRecommend == 0 || isRecommend == 1);
    }

    /**
     * 验证是否允许评论值是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidAllowComment() {
        return allowComment == null || (allowComment == 0 || allowComment == 1);
    }

    /**
     * 验证是否开启打赏值是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidAllowReward() {
        return allowReward == null || (allowReward == 0 || allowReward == 1);
    }

    /**
     * 判断是否要更新为草稿状态
     * 
     * @return 是否为草稿
     */
    public boolean isDraft() {
        return status != null && status == 0;
    }

    /**
     * 判断是否要更新为发布状态
     * 
     * @return 是否为发布
     */
    public boolean isPublish() {
        return status != null && status == 1;
    }

    /**
     * 判断是否要更新为撤回状态
     * 
     * @return 是否为撤回
     */
    public boolean isWithdraw() {
        return status != null && status == 2;
    }

    /**
     * 判断是否要更新为转载文章
     * 
     * @return 是否为转载
     */
    public boolean isReprint() {
        return type != null && type == 1;
    }

    /**
     * 判断是否要更新为翻译文章
     * 
     * @return 是否为翻译
     */
    public boolean isTranslation() {
        return type != null && type == 2;
    }

    /**
     * 判断是否要更新为原创文章
     * 
     * @return 是否为原创
     */
    public boolean isOriginal() {
        return type != null && type == 0;
    }

    /**
     * 判断是否需要原文链接
     * 
     * @return 是否需要原文链接
     */
    public boolean needOriginalUrl() {
        return isReprint() || isTranslation();
    }

    /**
     * 判断是否要设置为私密文章
     * 
     * @return 是否为私密文章
     */
    public boolean isPrivate() {
        return password != null && !password.trim().isEmpty();
    }

    /**
     * 判断是否要清除密码（公开文章）
     * 
     * @return 是否清除密码
     */
    public boolean isClearPassword() {
        return password != null && password.trim().isEmpty();
    }

    /**
     * 获取有效的标签ID列表
     * 
     * @return 有效的标签ID列表
     */
    public List<Long> getValidTagIds() {
        if (tagIds == null) {
            return null;
        }
        return tagIds.stream().filter(id -> id != null && id > 0).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取有效的标签名称列表
     * 
     * @return 有效的标签名称列表
     */
    public List<String> getValidTagNames() {
        if (tagNames == null) {
            return null;
        }
        return tagNames.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 计算文章字数
     * 
     * @return 文章字数
     */
    public int getWordCount() {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        // 简单的字数统计，可以根据需要优化
        return content.replaceAll("\\s+", "").length();
    }

    /**
     * 预估阅读时长（分钟）
     * 
     * @return 阅读时长
     */
    public int getEstimateReadingTime() {
        int wordCount = getWordCount();
        // 假设每分钟阅读200字
        int minutes = wordCount / 200;
        return minutes < 1 ? 1 : minutes;
    }

    /**
     * 判断是否有实际的更新内容
     * 
     * @return 是否有更新内容
     */
    public boolean hasUpdateContent() {
        return title != null || summary != null || content != null ||
                coverImage != null || status != null || type != null ||
                isTop != null || isRecommend != null || originalUrl != null ||
                categoryId != null || tagIds != null || tagNames != null ||
                keywords != null || description != null || allowComment != null ||
                allowReward != null || password != null;
    }

    /**
     * 判断是否需要发送通知
     * 
     * @return 是否发送通知
     */
    public boolean shouldSendNotification() {
        return Boolean.TRUE.equals(sendNotification) && isPublish();
    }
}