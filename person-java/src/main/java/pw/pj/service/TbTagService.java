package pw.pj.service;

import pw.pj.POJO.DO.TbTag;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.TagVO;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 标签管理服务接口
 * 针对表【tb_tag(标签表)】的数据库操作Service
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbTagService extends IService<TbTag> {

    // ==================== 标签CRUD操作 ====================

    /**
     * 创建标签
     * 
     * @param tagVO 标签信息
     * @return 创建后的标签信息
     */
    TagVO createTag(TagVO tagVO);

    /**
     * 更新标签
     * 
     * @param tagId 标签ID
     * @param tagVO 标签信息
     * @return 更新后的标签信息
     */
    TagVO updateTag(Long tagId, TagVO tagVO);

    /**
     * 根据ID获取标签详情
     * 
     * @param tagId 标签ID
     * @return 标签详情
     */
    TagVO getTagById(Long tagId);

    /**
     * 删除标签
     * 
     * @param tagId 标签ID
     * @return 是否删除成功
     */
    Boolean deleteTag(Long tagId);

    /**
     * 批量删除标签
     * 
     * @param tagIds 标签ID列表
     * @return 是否删除成功
     */
    Boolean deleteTagsBatch(List<Long> tagIds);

    // ==================== 标签查询操作 ====================

    /**
     * 分页获取标签列表
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<TagVO> getTagList(PageQueryVO pageQueryVO);

    /**
     * 获取所有可用标签
     * 
     * @return 标签列表
     */
    List<TagVO> getAllTags();

    /**
     * 根据关键词搜索标签
     * 
     * @param keyword 搜索关键词
     * @return 匹配的标签列表
     */
    List<TagVO> searchTags(String keyword);

    /**
     * 获取热门标签
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    List<TagVO> getHotTags(Integer limit);

    /**
     * 获取推荐标签
     * 
     * @param limit 数量限制
     * @return 推荐标签列表
     */
    List<TagVO> getRecommendTags(Integer limit);

    /**
     * 根据类型获取标签
     * 
     * @param tagType 标签类型
     * @param limit   数量限制
     * @return 标签列表
     */
    List<TagVO> getTagsByType(Integer tagType, Integer limit);

    /**
     * 获取标签云数据
     * 
     * @param maxTags 最大标签数量
     * @return 标签云数据
     */
    List<TagVO> getTagCloud(Integer maxTags);

    // ==================== 标签状态管理 ====================

    /**
     * 启用标签
     * 
     * @param tagId 标签ID
     * @return 是否成功
     */
    Boolean enableTag(Long tagId);

    /**
     * 禁用标签
     * 
     * @param tagId 标签ID
     * @return 是否成功
     */
    Boolean disableTag(Long tagId);

    // ==================== 标签关联操作 ====================

    /**
     * 获取文章的所有标签
     * 
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<TagVO> getTagsByArticleId(Long articleId);

    /**
     * 更新标签的文章数量统计
     * 
     * @param tagId 标签ID
     * @return 更新后的文章数量
     */
    Integer updateTagArticleCount(Long tagId);

    /**
     * 增加标签点击次数
     * 
     * @param tagId 标签ID
     * @return 是否成功
     */
    Boolean incrementTagClickCount(Long tagId);

    // ==================== 标签统计和验证 ====================

    /**
     * 获取标签的文章数量
     * 
     * @param tagId 标签ID
     * @return 文章数量
     */
    Integer getTagArticleCount(Long tagId);

    /**
     * 检查标签名称是否已存在
     * 
     * @param name      标签名称
     * @param excludeId 排除的标签ID（用于更新时排除自己）
     * @return 是否存在
     */
    Boolean checkTagName(String name, Long excludeId);

    /**
     * 检查标签别名是否已存在
     * 
     * @param slug      标签别名
     * @param excludeId 排除的标签ID
     * @return 是否存在
     */
    Boolean checkTagSlug(String slug, Long excludeId);

    /**
     * 获取标签统计信息
     * 
     * @return 统计数据
     */
    Map<String, Object> getTagStatistics();

    /**
     * 清理无关联文章的标签
     * 
     * @return 清理的标签数量
     */
    Integer cleanupUnusedTags();

    // ==================== 数据转换方法 ====================

    /**
     * 将TbTag转换为TagVO
     * 
     * @param tag 标签实体
     * @return 标签VO
     */
    TagVO convertToVO(TbTag tag);

    /**
     * 将TbTag列表转换为TagVO列表
     * 
     * @param tags 标签实体列表
     * @return 标签VO列表
     */
    List<TagVO> convertToVOList(List<TbTag> tags);

}
