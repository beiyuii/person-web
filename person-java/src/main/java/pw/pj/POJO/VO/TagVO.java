package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签信息VO
 * 用于前端显示标签信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class TagVO {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签颜色（用于前端主题）
     */
    private String color;

    /**
     * 标签图标URL
     */
    private String iconUrl;

    /**
     * 排序值（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 标签状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 使用次数（关联文章数量）
     */
    private Integer useCount;

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
     * 是否为热门标签
     */
    private Boolean isHot;

    /**
     * 是否为推荐标签
     */
    private Boolean isRecommend;

    /**
     * 标签类型：0-普通标签，1-技术标签，2-分类标签
     */
    private Integer tagType;

    /**
     * 获取标签状态文本描述
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
     * 获取标签类型文本描述
     * 
     * @return 类型文本
     */
    public String getTagTypeText() {
        if (tagType == null) {
            return "普通标签";
        }
        switch (tagType) {
            case 0:
                return "普通标签";
            case 1:
                return "技术标签";
            case 2:
                return "分类标签";
            default:
                return "普通标签";
        }
    }

    /**
     * 判断标签是否正常状态
     * 
     * @return 是否正常
     */
    public boolean isNormal() {
        return status != null && status == 0;
    }

    /**
     * 判断是否为热门标签
     * 
     * @return 是否热门
     */
    public boolean isHotTag() {
        return Boolean.TRUE.equals(isHot);
    }

    /**
     * 判断是否为推荐标签
     * 
     * @return 是否推荐
     */
    public boolean isRecommendTag() {
        return Boolean.TRUE.equals(isRecommend);
    }

    /**
     * 判断是否为技术标签
     * 
     * @return 是否技术标签
     */
    public boolean isTechTag() {
        return tagType != null && tagType == 1;
    }

    /**
     * 获取标签链接地址
     * 
     * @return 链接地址
     */
    public String getUrl() {
        return "/tag/" + id;
    }

    /**
     * 获取标签颜色（如果没有则返回默认颜色）
     * 
     * @return 颜色值
     */
    public String getDisplayColor() {
        if (color != null && !color.trim().isEmpty()) {
            return color;
        }

        // 根据标签类型返回不同的默认颜色
        if (tagType != null) {
            switch (tagType) {
                case 1:
                    return "#67C23A"; // 技术标签 - 绿色
                case 2:
                    return "#E6A23C"; // 分类标签 - 橙色
                default:
                    return "#409EFF"; // 普通标签 - 蓝色
            }
        }

        return "#409EFF"; // 默认蓝色
    }

    /**
     * 获取标签图标（如果没有则返回默认图标）
     * 
     * @return 图标URL
     */
    public String getDisplayIcon() {
        if (iconUrl != null && !iconUrl.trim().isEmpty()) {
            return iconUrl;
        }
        return "/static/icons/tag-default.png";
    }

    /**
     * 获取标签热度等级
     * 
     * @return 热度等级（1-5级）
     */
    public int getHotLevel() {
        if (useCount == null || useCount == 0) {
            return 1;
        }

        if (useCount >= 100) {
            return 5;
        } else if (useCount >= 50) {
            return 4;
        } else if (useCount >= 20) {
            return 3;
        } else if (useCount >= 10) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 获取标签热度文本描述
     * 
     * @return 热度文本
     */
    public String getHotLevelText() {
        int level = getHotLevel();
        switch (level) {
            case 5:
                return "超热门";
            case 4:
                return "很热门";
            case 3:
                return "热门";
            case 2:
                return "较热门";
            case 1:
            default:
                return "一般";
        }
    }

    /**
     * 判断是否为新标签（创建时间在7天内）
     * 
     * @return 是否新标签
     */
    public boolean isNewTag() {
        if (createTime == null) {
            return false;
        }
        return createTime.isAfter(LocalDateTime.now().minusDays(7));
    }
}