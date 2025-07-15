package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章信息响应VO
 * 用于前端显示文章信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class ArticleVO {

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
     * 文章内容
     */
    private String content;

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
     * 转载文章的原文链接
     */
    private String originalUrl;

    /**
     * 文章分类信息
     */
    private CategoryVO category;

    /**
     * 文章标签列表
     */
    private List<TagVO> tags;

    /**
     * 作者信息
     */
    private UserVO author;

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
     * 分享数
     */
    private Integer shareCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 是否已点赞（当前用户视角）
     */
    private Boolean isLiked;

    /**
     * 是否已收藏（当前用户视角）
     */
    private Boolean isCollected;

    /**
     * 是否为当前用户的文章
     */
    private Boolean isOwner;

    /**
     * 文章阅读时长（分钟）
     */
    private Integer readingTime;

    /**
     * 文章字数
     */
    private Integer wordCount;

    /**
     * SEO关键词
     */
    private String keywords;

    /**
     * SEO描述
     */
    private String description;

    /**
     * 相关文章列表（简化版）
     */
    private List<ArticleSimpleVO> relatedArticles;

    /**
     * 上一篇文章
     */
    private ArticleSimpleVO previousArticle;

    /**
     * 下一篇文章
     */
    private ArticleSimpleVO nextArticle;

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
     * 判断是否为草稿状态
     * 
     * @return 是否为草稿
     */
    public boolean isDraft() {
        return status != null && status == 0;
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
     * 判断是否为转载文章
     * 
     * @return 是否转载
     */
    public boolean isReprint() {
        return type != null && type == 1;
    }

    /**
     * 判断是否为翻译文章
     * 
     * @return 是否翻译
     */
    public boolean isTranslation() {
        return type != null && type == 2;
    }

    /**
     * 计算文章热度（综合指标）
     * 
     * @return 热度值
     */
    public double getHotScore() {
        int views = viewCount != null ? viewCount : 0;
        int likes = likeCount != null ? likeCount : 0;
        int comments = commentCount != null ? commentCount : 0;
        int collects = collectCount != null ? collectCount : 0;
        int shares = shareCount != null ? shareCount : 0;

        // 权重设置：浏览量(1) + 点赞(3) + 评论(5) + 收藏(7) + 分享(10)
        return views + likes * 3 + comments * 5 + collects * 7 + shares * 10;
    }

    /**
     * 获取标签名称列表
     * 
     * @return 标签名称列表
     */
    public List<String> getTagNames() {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tags.stream().map(TagVO::getName).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取格式化的创建时间
     * 
     * @return 格式化时间
     */
    public String getFormattedCreateTime() {
        if (createTime == null) {
            return "";
        }
        return createTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"));
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