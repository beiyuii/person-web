package pw.pj.service;

import pw.pj.POJO.DO.TbCategory;
import pw.pj.POJO.VO.CategoryVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 分类管理服务接口
 * 提供分类的完整业务功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbCategoryService extends IService<TbCategory> {

    // ==================== 分类CRUD操作 ====================

    /**
     * 创建分类
     * 
     * @param categoryVO 分类创建请求
     * @return 分类详情
     */
    CategoryVO createCategory(CategoryVO categoryVO);

    /**
     * 更新分类
     * 
     * @param categoryId 分类ID
     * @param categoryVO 分类更新请求
     * @return 分类详情
     */
    CategoryVO updateCategory(Long categoryId, CategoryVO categoryVO);

    /**
     * 根据ID获取分类详情
     * 
     * @param categoryId 分类ID
     * @return 分类详情
     */
    CategoryVO getCategoryById(Long categoryId);

    /**
     * 删除分类（逻辑删除）
     * 
     * @param categoryId 分类ID
     * @return 是否成功
     */
    Boolean deleteCategory(Long categoryId);

    /**
     * 批量删除分类
     * 
     * @param categoryIds 分类ID列表
     * @return 是否成功
     */
    Boolean deleteCategoriesBatch(List<Long> categoryIds);

    // ==================== 分类查询操作 ====================

    /**
     * 分页查询分类列表
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<CategoryVO> getCategoryList(PageQueryVO pageQueryVO);

    /**
     * 获取所有分类列表（不分页）
     * 
     * @return 分类列表
     */
    List<CategoryVO> getAllCategories();

    /**
     * 根据分类名称搜索分类
     * 
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    List<CategoryVO> searchCategories(String keyword);

    /**
     * 获取热门分类列表
     * 
     * @param limit 数量限制
     * @return 热门分类列表
     */
    List<CategoryVO> getHotCategories(Integer limit);

    // ==================== 分类状态管理 ====================

    /**
     * 启用分类
     * 
     * @param categoryId 分类ID
     * @return 是否成功
     */
    Boolean enableCategory(Long categoryId);

    /**
     * 禁用分类
     * 
     * @param categoryId 分类ID
     * @return 是否成功
     */
    Boolean disableCategory(Long categoryId);

    // ==================== 分类统计和验证 ====================

    /**
     * 获取分类的文章数量
     * 
     * @param categoryId 分类ID
     * @return 文章数量
     */
    Integer getCategoryArticleCount(Long categoryId);

    /**
     * 检查分类名称是否重复
     * 
     * @param name      分类名称
     * @param excludeId 排除的分类ID（用于更新时排除自己）
     * @return 是否重复
     */
    Boolean checkCategoryName(String name, Long excludeId);

    /**
     * 检查分类路径是否重复
     * 
     * @param path      分类路径
     * @param excludeId 排除的分类ID（用于更新时排除自己）
     * @return 是否重复
     */
    Boolean checkCategoryPath(String path, Long excludeId);

    /**
     * 获取分类统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getCategoryStatistics();

    // ==================== 数据转换方法 ====================

    /**
     * DO转VO
     * 
     * @param category 分类实体
     * @return 分类VO
     */
    CategoryVO convertToVO(TbCategory category);

    /**
     * DO列表转VO列表
     * 
     * @param categories 分类实体列表
     * @return 分类VO列表
     */
    List<CategoryVO> convertToVOList(List<TbCategory> categories);
}
