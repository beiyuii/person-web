package pw.pj.POJO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类信息VO
 * 用于前端显示分类信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class CategoryVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标URL
     */
    private String iconUrl;

    /**
     * 分类封面图片URL
     */
    private String coverImage;

    /**
     * 分类颜色（用于前端主题）
     */
    private String color;

    /**
     * 父分类ID（支持多级分类）
     */
    private Long parentId;

    /**
     * 分类层级（1-顶级，2-二级，以此类推）
     */
    private Integer level;

    /**
     * 排序值（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 分类状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 文章数量
     */
    private Integer articleCount;

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
     * 父分类名称
     */
    private String parentName;

    /**
     * 分类路径（用于面包屑导航）
     */
    private String path;

    /**
     * 是否为热门分类
     */
    private Boolean isHot;

    /**
     * 获取分类状态文本描述
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
     * 判断是否为顶级分类
     * 
     * @return 是否为顶级分类
     */
    public boolean isTopLevel() {
        return level != null && level == 1;
    }

    /**
     * 判断是否为子分类
     * 
     * @return 是否为子分类
     */
    public boolean isSubCategory() {
        return parentId != null && parentId > 0;
    }

    /**
     * 判断分类是否正常状态
     * 
     * @return 是否正常
     */
    public boolean isNormal() {
        return status != null && status == 0;
    }

    /**
     * 判断是否为热门分类
     * 
     * @return 是否热门
     */
    public boolean isHotCategory() {
        return Boolean.TRUE.equals(isHot);
    }

    /**
     * 获取分类层级文本描述
     * 
     * @return 层级文本
     */
    public String getLevelText() {
        if (level == null) {
            return "未知";
        }
        switch (level) {
            case 1:
                return "顶级分类";
            case 2:
                return "二级分类";
            case 3:
                return "三级分类";
            default:
                return level + "级分类";
        }
    }

    /**
     * 获取分类链接地址
     * 
     * @return 链接地址
     */
    public String getUrl() {
        return "/category/" + id;
    }

    /**
     * 获取分类的完整路径（包含父级）
     * 
     * @return 完整路径
     */
    public String getFullPath() {
        if (path != null && !path.trim().isEmpty()) {
            return path;
        }

        if (parentName != null && !parentName.trim().isEmpty()) {
            return parentName + " > " + name;
        }

        return name;
    }

    /**
     * 获取分类图标（如果没有则返回默认图标）
     * 
     * @return 图标URL
     */
    public String getDisplayIcon() {
        if (iconUrl != null && !iconUrl.trim().isEmpty()) {
            return iconUrl;
        }
        return "/static/icons/category-default.png";
    }

    /**
     * 获取分类颜色（如果没有则返回默认颜色）
     * 
     * @return 颜色值
     */
    public String getDisplayColor() {
        if (color != null && !color.trim().isEmpty()) {
            return color;
        }
        return "#409EFF"; // 默认蓝色
    }
}