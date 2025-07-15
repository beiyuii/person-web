package pw.pj.service;

import pw.pj.POJO.DO.TbComment;
import pw.pj.POJO.VO.CommentCreateVO;
import pw.pj.POJO.VO.CommentVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 评论管理服务接口
 * 针对表【tb_comment(评论表)】的数据库操作Service
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbCommentService extends IService<TbComment> {

    // ==================== 评论CRUD操作 ====================

    /**
     * 创建评论
     * 
     * @param commentCreateVO 评论创建信息
     * @return 创建后的评论信息
     */
    CommentVO createComment(CommentCreateVO commentCreateVO);

    /**
     * 回复评论
     * 
     * @param parentId        父评论ID
     * @param commentCreateVO 评论创建信息
     * @return 创建后的评论信息
     */
    CommentVO replyComment(Long parentId, CommentCreateVO commentCreateVO);

    /**
     * 更新评论内容
     * 
     * @param commentId 评论ID
     * @param content   新内容
     * @return 更新后的评论信息
     */
    CommentVO updateComment(Long commentId, String content);

    /**
     * 根据ID获取评论详情
     * 
     * @param commentId 评论ID
     * @return 评论详情
     */
    CommentVO getCommentById(Long commentId);

    /**
     * 删除评论
     * 
     * @param commentId 评论ID
     * @return 删除的记录数
     */
    Integer deleteComment(Long commentId);

    /**
     * 批量删除评论
     * 
     * @param commentIds 评论ID列表
     * @return 删除的记录数
     */
    Integer deleteCommentsBatch(List<Long> commentIds);

    // ==================== 评论查询操作 ====================

    /**
     * 分页获取评论列表（管理端）
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<CommentVO> getCommentList(PageQueryVO pageQueryVO);

    /**
     * 获取文章的评论列表（前端展示）
     * 
     * @param articleId 文章ID
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 分页结果
     */
    PageResult<CommentVO> getArticleComments(Long articleId, Integer pageNum, Integer pageSize);

    /**
     * 获取评论树（层级结构）
     * 
     * @param articleId 文章ID
     * @param maxLevel  最大层级（避免过深）
     * @return 评论树列表
     */
    List<CommentVO> getCommentTree(Long articleId, Integer maxLevel);

    /**
     * 获取最新评论
     * 
     * @param limit 数量限制
     * @return 最新评论列表
     */
    List<CommentVO> getLatestComments(Integer limit);

    /**
     * 获取热门评论
     * 
     * @param limit 数量限制
     * @return 热门评论列表
     */
    List<CommentVO> getHotComments(Integer limit);

    /**
     * 根据关键词搜索评论
     * 
     * @param keyword  搜索关键词
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    PageResult<CommentVO> searchComments(String keyword, Integer pageNum, Integer pageSize);

    // ==================== 评论审核管理 ====================

    /**
     * 审核通过评论
     * 
     * @param commentId 评论ID
     * @return 审核成功的记录数
     */
    Integer approveComment(Long commentId);

    /**
     * 拒绝评论
     * 
     * @param commentId 评论ID
     * @param reason    拒绝原因
     * @return 拒绝成功的记录数
     */
    Integer rejectComment(Long commentId, String reason);

    /**
     * 批量审核评论
     * 
     * @param commentIds 评论ID列表
     * @param status     审核状态：1-通过，2-拒绝，3-标记为垃圾评论
     * @return 审核成功的记录数
     */
    Integer batchAuditComments(List<Long> commentIds, Integer status);

    /**
     * 获取待审核评论列表
     * 
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    PageResult<CommentVO> getPendingComments(Integer pageNum, Integer pageSize);

    /**
     * 标记为垃圾评论
     * 
     * @param commentId 评论ID
     * @return 标记成功的记录数
     */
    Integer markAsSpam(Long commentId);

    // ==================== 评论互动操作 ====================

    /**
     * 点赞评论
     * 
     * @param commentId 评论ID
     * @param userId    用户ID（可选，游客点赞传null）
     * @return 点赞成功的记录数
     */
    Integer likeComment(Long commentId, Long userId);

    /**
     * 取消点赞评论
     * 
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 取消点赞成功的记录数
     */
    Integer unlikeComment(Long commentId, Long userId);

    /**
     * 置顶评论
     * 
     * @param commentId 评论ID
     * @return 置顶成功的记录数
     */
    Integer stickComment(Long commentId);

    /**
     * 取消置顶评论
     * 
     * @param commentId 评论ID
     * @return 取消置顶成功的记录数
     */
    Integer unstickComment(Long commentId);

    // ==================== 评论统计和验证 ====================

    /**
     * 获取文章的评论数量
     * 
     * @param articleId 文章ID
     * @return 评论数量
     */
    Integer getCommentCountByArticleId(Long articleId);

    /**
     * 更新文章的评论数量统计
     * 
     * @param articleId 文章ID
     * @return 更新后的评论数量
     */
    Integer updateArticleCommentCount(Long articleId);

    /**
     * 获取评论统计信息
     * 
     * @return 统计数据
     */
    Map<String, Object> getCommentStatistics();

    /**
     * 检查IP是否被限制评论
     * 
     * @param ipAddress IP地址
     * @return 限制状态：0-未限制，1-已限制
     */
    Integer checkIpRestriction(String ipAddress);

    /**
     * 检查评论内容是否为垃圾评论
     * 
     * @param content     评论内容
     * @param authorEmail 评论者邮箱
     * @param authorIp    评论者IP
     * @return 垃圾评论检测：0-正常评论，1-垃圾评论
     */
    Integer isSpamComment(String content, String authorEmail, String authorIp);

    // ==================== 数据清理和维护 ====================

    /**
     * 清理垃圾评论
     * 
     * @param days 清理多少天前的垃圾评论
     * @return 清理的评论数量
     */
    Integer cleanupSpamComments(Integer days);

    /**
     * 重建评论树结构
     * 重新计算评论的层级和路径
     * 
     * @return 重建的评论数量
     */
    Integer rebuildCommentTree();

    /**
     * 同步评论回复数量
     * 重新计算所有评论的回复数量
     * 
     * @return 同步的评论数量
     */
    Integer syncCommentReplyCount();

    // ==================== 数据转换方法 ====================

    /**
     * 将TbComment转换为CommentVO
     * 
     * @param comment 评论实体
     * @return 评论VO
     */
    CommentVO convertToVO(TbComment comment);

    /**
     * 将TbComment列表转换为CommentVO列表
     * 
     * @param comments 评论实体列表
     * @return 评论VO列表
     */
    List<CommentVO> convertToVOList(List<TbComment> comments);

    /**
     * 构建评论树结构
     * 
     * @param comments 扁平的评论列表
     * @return 树形结构的评论列表
     */
    List<CommentVO> buildCommentTree(List<CommentVO> comments);

}
