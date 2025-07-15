package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息响应VO
 * 用于前端显示评论信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class CommentVO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 父评论ID（0表示顶级评论）
     */
    private Long parentId;

    /**
     * 回复评论ID（针对具体某条评论的回复）
     */
    private Long replyId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者信息
     */
    private UserVO commenter;

    /**
     * 回复对象信息（被回复的用户）
     */
    private UserVO replyUser;

    /**
     * 评论状态：0-正常，1-审核中，2-已删除
     */
    private Integer status;

    /**
     * 评论层级（1-顶级，2-二级，以此类推）
     */
    private Integer level;

    /**
     * 评论路径（用于树形结构）
     */
    private String path;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数（子评论数量）
     */
    private Integer replyCount;

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
     * 评论者IP地址
     */
    private String ipAddress;

    /**
     * 评论者位置信息
     */
    private String location;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 是否已点赞（当前用户视角）
     */
    private Boolean isLiked;

    /**
     * 是否为当前用户的评论
     */
    private Boolean isOwner;

    /**
     * 是否置顶评论
     */
    private Boolean isTop;

    /**
     * 是否为热门评论
     */
    private Boolean isHot;

    /**
     * 子评论列表
     */
    private List<CommentVO> children;

    /**
     * 获取评论状态文本描述
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
                return "审核中";
            case 2:
                return "已删除";
            default:
                return "未知";
        }
    }

    /**
     * 判断是否为顶级评论
     * 
     * @return 是否为顶级评论
     */
    public boolean isTopLevel() {
        return level != null && level == 1;
    }

    /**
     * 判断是否为子评论
     * 
     * @return 是否为子评论
     */
    public boolean isSubComment() {
        return parentId != null && parentId > 0;
    }

    /**
     * 判断是否为回复评论
     * 
     * @return 是否为回复评论
     */
    public boolean isReplyComment() {
        return replyId != null && replyId > 0;
    }

    /**
     * 判断评论是否正常状态
     * 
     * @return 是否正常
     */
    public boolean isNormal() {
        return status != null && status == 0;
    }

    /**
     * 判断评论是否审核中
     * 
     * @return 是否审核中
     */
    public boolean isPending() {
        return status != null && status == 1;
    }

    /**
     * 判断评论是否已删除
     * 
     * @return 是否已删除
     */
    public boolean isDeleted() {
        return status != null && status == 2;
    }

    /**
     * 判断是否为置顶评论
     * 
     * @return 是否置顶
     */
    public boolean isTopComment() {
        return Boolean.TRUE.equals(isTop);
    }

    /**
     * 判断是否为热门评论
     * 
     * @return 是否热门
     */
    public boolean isHotComment() {
        return Boolean.TRUE.equals(isHot);
    }

    /**
     * 判断是否有子评论
     * 
     * @return 是否有子评论
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 判断是否有回复
     * 
     * @return 是否有回复
     */
    public boolean hasReplies() {
        return replyCount != null && replyCount > 0;
    }

    /**
     * 获取评论层级文本描述
     * 
     * @return 层级文本
     */
    public String getLevelText() {
        if (level == null) {
            return "未知";
        }
        switch (level) {
            case 1:
                return "顶级评论";
            case 2:
                return "二级评论";
            case 3:
                return "三级评论";
            default:
                return level + "级评论";
        }
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
     * 获取评论热度（基于点赞数和回复数）
     * 
     * @return 热度值
     */
    public double getHotScore() {
        int likes = likeCount != null ? likeCount : 0;
        int replies = replyCount != null ? replyCount : 0;

        // 权重设置：点赞(1) + 回复(2)
        return likes + replies * 2;
    }

    /**
     * 获取评论者昵称
     * 
     * @return 评论者昵称
     */
    public String getCommenterName() {
        if (commenter != null) {
            return commenter.getNickname() != null ? commenter.getNickname() : commenter.getUsername();
        }
        return "匿名用户";
    }

    /**
     * 获取评论者头像
     * 
     * @return 评论者头像URL
     */
    public String getCommenterAvatar() {
        if (commenter != null && commenter.getAvatar() != null) {
            return commenter.getAvatar();
        }
        return "/static/images/default-avatar.png";
    }

    /**
     * 获取回复对象昵称
     * 
     * @return 回复对象昵称
     */
    public String getReplyUserName() {
        if (replyUser != null) {
            return replyUser.getNickname() != null ? replyUser.getNickname() : replyUser.getUsername();
        }
        return "";
    }

    /**
     * 获取简化的IP地址（脱敏处理）
     * 
     * @return 脱敏后的IP地址
     */
    public String getMaskedIpAddress() {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return "";
        }

        // IPv4地址脱敏：192.168.1.100 -> 192.168.*.*
        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".*.*";
        }

        return ipAddress;
    }

    /**
     * 获取设备类型（从userAgent中提取）
     * 
     * @return 设备类型
     */
    public String getDeviceType() {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return "未知";
        }

        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "移动设备";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "平板设备";
        } else {
            return "桌面设备";
        }
    }
}