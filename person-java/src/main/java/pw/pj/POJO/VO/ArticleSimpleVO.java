package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章简化版VO
 * 用于文章列表、相关文章等场景
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class ArticleSimpleVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章封面图片URL
     */
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
     * 分类名称
     */
    private String categoryName;

    /**
     * 作者昵称
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 文章阅读时长（分钟）
     */
    private Integer readingTime;

    /**
     * 文章字数
     */
    private Integer wordCount;

    /**
     * 获取文章状态文本描述
     * 
     * @return 状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            case 2:
                return "已撤回";
            default:
                return "未知";
        }
    }

    /**
     * 获取文章类型文本描述
     * 
     * @return 类型文本
     */
    public String getTypeText() {
        if (type == null) {
            return "原创";
        }
        switch (type) {
            case 0:
                return "原创";
            case 1:
                return "转载";
            case 2:
                return "翻译";
            default:
                return "原创";
        }
    }

    /**
     * 判断是否为已发布状态
     * 
     * @return 是否已发布
     */
    public boolean isPublished() {
        return status != null && status == 1;
    }

    /**
     * 判断是否为置顶文章
     * 
     * @return 是否置顶
     */
    public boolean isTopArticle() {
        return isTop != null && isTop == 1;
    }

    /**
     * 判断是否为推荐文章
     * 
     * @return 是否推荐
     */
    public boolean isRecommendArticle() {
        return isRecommend != null && isRecommend == 1;
    }

    /**
     * 获取相对时间描述（如：3天前、1小时前等）
     * 
     * @return 相对时间描述
     */
    public String getRelativeTime() {
        if (createTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(createTime, now);

        long days = duration.toDays();
        if (days > 0) {
            return days + "天前";
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return hours + "小时前";
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + "分钟前";
        }

        return "刚刚";
    }
}