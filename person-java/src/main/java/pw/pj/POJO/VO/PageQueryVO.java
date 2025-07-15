package pw.pj.POJO.VO;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页查询参数VO
 * 统一的分页查询参数封装
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
public class PageQueryVO {

    /**
     * 页码（从1开始）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为1")
    private Integer pageNum = 1;

    /**
     * 每页显示数量
     */
    @NotNull(message = "每页显示数量不能为空")
    @Min(value = 1, message = "每页显示数量最小值为1")
    @Max(value = 100, message = "每页显示数量最大值为100")
    private Integer pageSize = 10;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 排序字段
     */
    private String sortField = "createTime";

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortOrder = "desc";

    /**
     * 开始时间（用于时间范围查询）
     */
    private String startTime;

    /**
     * 结束时间（用于时间范围查询）
     */
    private String endTime;

    /**
     * 状态筛选（可选）
     */
    private Integer status;

    /**
     * 分类ID（可选，用于文章查询）
     */
    private Long categoryId;

    /**
     * 标签ID（可选，用于文章查询）
     */
    private Long tagId;

    /**
     * 用户ID（可选，用于查询特定用户的内容）
     */
    private Long userId;

    /**
     * 获取计算后的偏移量
     * 
     * @return 偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 验证排序字段是否合法
     * 
     * @param allowedFields 允许的排序字段数组
     * @return 是否合法
     */
    public boolean isValidSortField(String[] allowedFields) {
        if (sortField == null || sortField.trim().isEmpty()) {
            return false;
        }

        for (String field : allowedFields) {
            if (field.equals(sortField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证排序方向是否合法
     * 
     * @return 是否合法
     */
    public boolean isValidSortOrder() {
        return "asc".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder);
    }

    /**
     * 获取安全的排序字段（防止SQL注入）
     * 
     * @param allowedFields 允许的排序字段数组
     * @param defaultField  默认排序字段
     * @return 安全的排序字段
     */
    public String getSafeSortField(String[] allowedFields, String defaultField) {
        if (isValidSortField(allowedFields)) {
            return sortField;
        }
        return defaultField;
    }

    /**
     * 获取安全的排序方向
     * 
     * @return 安全的排序方向
     */
    public String getSafeSortOrder() {
        return isValidSortOrder() ? sortOrder.toLowerCase() : "desc";
    }
}