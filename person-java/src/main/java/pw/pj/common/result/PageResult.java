package pw.pj.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Collections;

/**
 * 分页结果封装类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页显示条数
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否为第一页
     */
    private Boolean isFirst;

    /**
     * 是否为最后一页
     */
    private Boolean isLast;

    /**
     * 私有构造方法
     * 
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页显示条数
     */
    private PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records != null ? records : Collections.emptyList();
        this.total = total != null ? total : 0L;
        this.current = current != null ? current : 1;
        this.size = size != null ? size : 10;

        // 计算总页数
        this.pages = (int) Math.ceil((double) this.total / this.size);

        // 计算分页状态
        this.hasPrevious = this.current > 1;
        this.hasNext = this.current < this.pages;
        this.isFirst = this.current == 1;
        this.isLast = this.current.equals(this.pages) || this.pages == 0;
    }

    /**
     * 创建空的分页结果
     * 
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 1, 10);
    }

    /**
     * 创建空的分页结果（指定分页参数）
     * 
     * @param current 当前页码
     * @param size    每页显示条数
     * @param <T>     数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(Integer current, Integer size) {
        return new PageResult<>(Collections.emptyList(), 0L, current, size);
    }

    /**
     * 根据PageHelper的PageInfo创建分页结果
     * 
     * @param pageInfo PageHelper的分页信息
     * @param <T>      数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(PageInfo<T> pageInfo) {
        if (pageInfo == null) {
            return empty();
        }
        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize());
    }

    /**
     * 根据数据列表和分页信息创建分页结果
     * 
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页显示条数
     * @param <T>     数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer current, Integer size) {
        return new PageResult<>(records, total, current, size);
    }

    /**
     * 根据数据列表和分页信息创建分页结果（total为int类型）
     * 
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页显示条数
     * @param <T>     数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Integer total, Integer current, Integer size) {
        return new PageResult<>(records, total != null ? total.longValue() : 0L, current, size);
    }

    /**
     * 数据转换，将当前分页结果中的数据转换为另一种类型
     * 
     * @param converter 转换器函数
     * @param <R>       目标数据类型
     * @return 转换后的分页结果
     */
    public <R> PageResult<R> convert(java.util.function.Function<T, R> converter) {
        List<R> convertedRecords = this.records.stream()
                .map(converter)
                .collect(java.util.stream.Collectors.toList());

        return PageResult.<R>builder()
                .records(convertedRecords)
                .total(this.total)
                .current(this.current)
                .size(this.size)
                .pages(this.pages)
                .hasPrevious(this.hasPrevious)
                .hasNext(this.hasNext)
                .isFirst(this.isFirst)
                .isLast(this.isLast)
                .build();
    }

    /**
     * 判断是否有数据
     * 
     * @return 是否有数据
     */
    public boolean hasRecords() {
        return this.records != null && !this.records.isEmpty();
    }

    /**
     * 获取当前页的数据条数
     * 
     * @return 当前页数据条数
     */
    public int getCurrentPageSize() {
        return this.records != null ? this.records.size() : 0;
    }

    /**
     * 获取开始记录数（用于显示"显示第x到第y条记录"）
     * 
     * @return 开始记录数
     */
    public long getStartRow() {
        if (this.total == 0) {
            return 0;
        }
        return (long) (this.current - 1) * this.size + 1;
    }

    /**
     * 获取结束记录数（用于显示"显示第x到第y条记录"）
     * 
     * @return 结束记录数
     */
    public long getEndRow() {
        if (this.total == 0) {
            return 0;
        }
        long endRow = (long) this.current * this.size;
        return Math.min(endRow, this.total);
    }

    /**
     * 创建分页结果的简化版本（仅包含必要字段）
     * 
     * @return 简化的分页结果
     */
    public PageResult<T> simplified() {
        return PageResult.<T>builder()
                .records(this.records)
                .total(this.total)
                .current(this.current)
                .size(this.size)
                .pages(this.pages)
                .build();
    }
}