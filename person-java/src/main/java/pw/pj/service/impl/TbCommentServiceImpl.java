package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import pw.pj.POJO.DO.TbComment;
import pw.pj.POJO.VO.CommentCreateVO;
import pw.pj.POJO.VO.CommentVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.IpUtils;
import pw.pj.common.utils.RedisUtils;
import pw.pj.common.utils.StringUtils;
import pw.pj.mapper.TbCommentMapper;
import pw.pj.service.TbCommentService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论管理服务实现类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbCommentServiceImpl extends ServiceImpl<TbCommentMapper, TbComment> implements TbCommentService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HttpServletRequest request;

    // ==================== 评论CRUD操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(CommentCreateVO commentCreateVO) {
        log.info("创建评论：文章ID={}, 内容长度={}", commentCreateVO.getArticleId(),
                commentCreateVO.getContent() != null ? commentCreateVO.getContent().length() : 0);

        // 1. 内容验证
        if (StringUtils.isBlank(commentCreateVO.getContent())) {
            throw new BusinessException(ResultEnum.COMMENT_CONTENT_EMPTY);
        }

        // 2. IP限制检查
        String clientIp = IpUtils.getClientIp(request);
        if (checkIpRestriction(clientIp) == 1) {
            throw new BusinessException(ResultEnum.COMMENT_TOO_FREQUENT);
        }

        // 3. 垃圾评论检测
        if (isSpamComment(commentCreateVO.getContent(), commentCreateVO.getNickname(), clientIp) == 1) {
            throw new BusinessException(ResultEnum.COMMENT_TOO_FREQUENT, "评论内容涉嫌垃圾信息");
        }

        // 4. 构建评论对象
        TbComment comment = buildCommentFromCreate(commentCreateVO, clientIp);

        // 5. 保存评论
        boolean saved = save(comment);
        if (!saved) {
            throw new BusinessException(ResultEnum.SAVE_FAIL, "评论保存失败");
        }

        // 6. 更新文章评论数
        updateArticleCommentCount(commentCreateVO.getArticleId());

        // 7. 清除缓存
        clearCommentCache(commentCreateVO.getArticleId());

        log.info("评论创建成功，评论ID：{}", comment.getId());
        return convertToVO(comment);
    }

    @Override
    public CommentVO getCommentById(Long commentId) {
        if (commentId == null) {
            return null;
        }

        TbComment comment = lambdaQuery()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .one();

        return convertToVO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO updateComment(Long commentId, String content) {
        log.info("更新评论：评论ID={}", commentId);

        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getContent, content)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated) {
            log.info("评论更新成功，评论ID：{}", commentId);
            // 返回更新后的评论信息
            TbComment comment = getById(commentId);
            return convertToVO(comment);
        }

        throw new BusinessException(ResultEnum.UPDATE_FAIL, "评论更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteComment(Long commentId) {
        log.info("删除评论：评论ID={}", commentId);

        // 检查评论是否存在
        TbComment comment = getById(commentId);
        if (comment == null || comment.getIsDelete() == 1) {
            throw new BusinessException(ResultEnum.UPDATE_FAIL, "评论不存在");
        }

        // 软删除评论
        boolean deleted = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .set(TbComment::getIsDelete, 1)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (deleted) {
            // 删除成功后更新文章评论数
            updateArticleCommentCount(comment.getArticleId());
            clearCommentCache(comment.getArticleId());
            log.info("评论删除成功，评论ID：{}", commentId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteCommentsBatch(List<Long> commentIds) {
        log.info("批量删除评论：数量={}", commentIds.size());

        if (ObjectUtils.isEmpty(commentIds)) {
            return 0;
        }

        // 先查询要删除的评论信息
        List<TbComment> commentsToDelete = lambdaQuery()
                .in(TbComment::getId, commentIds)
                .eq(TbComment::getIsDelete, 0)
                .list();

        if (ObjectUtils.isEmpty(commentsToDelete)) {
            return 0;
        }

        // 批量软删除
        boolean deleted = lambdaUpdate()
                .in(TbComment::getId, commentIds)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getIsDelete, 1)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        int deletedCount = 0;
        if (deleted) {
            deletedCount = commentsToDelete.size();

            // 更新相关文章的评论数
            Set<Long> articleIds = commentsToDelete.stream()
                    .map(TbComment::getArticleId)
                    .collect(Collectors.toSet());

            for (Long articleId : articleIds) {
                updateArticleCommentCount(articleId);
                clearCommentCache(articleId);
            }

            log.info("批量删除评论完成，删除数量：{}", deletedCount);
        }

        return deletedCount;
    }

    // ==================== 评论层级回复 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO replyComment(Long parentId, CommentCreateVO commentCreateVO) {
        log.info("回复评论：父评论ID={}", parentId);

        // 1. 验证父评论是否存在
        TbComment parentComment = getById(parentId);
        if (parentComment == null || parentComment.getIsDelete() == 1) {
            throw new BusinessException(ResultEnum.COMMENT_NOT_FOUND, "被回复的评论不存在");
        }

        // 2. 设置父评论ID
        commentCreateVO.setParentId(parentId);

        // 3. 创建回复评论
        CommentVO reply = createComment(commentCreateVO);

        // 4. 更新父评论回复数
        lambdaUpdate()
                .eq(TbComment::getId, parentId)
                .setSql("reply_count = reply_count + 1")
                .update();

        return reply;
    }

    @Override
    public List<CommentVO> getCommentTree(Long articleId, Integer limit) {
        // 1. 获取顶级评论
        List<TbComment> topComments = lambdaQuery()
                .eq(TbComment::getArticleId, articleId)
                .eq(TbComment::getParentId, 0)
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .orderByDesc(TbComment::getIsSticky)
                .orderByDesc(TbComment::getCreateTime)
                .last(limit != null ? "LIMIT " + limit : "")
                .list();

        if (ObjectUtils.isEmpty(topComments)) {
            return new ArrayList<>();
        }

        List<CommentVO> commentVOList = convertToVOList(topComments);

        // 2. 递归构建评论树
        return buildCommentTree(commentVOList);
    }

    @Override
    public List<CommentVO> buildCommentTree(List<CommentVO> commentVOList) {
        if (ObjectUtils.isEmpty(commentVOList)) {
            return new ArrayList<>();
        }

        Map<Long, List<CommentVO>> commentMap = new HashMap<>();

        // 构建父子关系映射
        for (CommentVO comment : commentVOList) {
            Long parentId = comment.getParentId() != null ? comment.getParentId() : 0L;
            commentMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(comment);
        }

        // 为每个评论设置子评论
        for (CommentVO comment : commentVOList) {
            List<CommentVO> children = commentMap.get(comment.getId());
            if (children != null) {
                comment.setChildren(children);
            }
        }

        // 返回顶级评论
        return commentMap.getOrDefault(0L, new ArrayList<>());
    }

    // ==================== 评论查询操作 ====================

    @Override
    public PageResult<CommentVO> getCommentList(PageQueryVO pageQueryVO) {
        LambdaQueryWrapper<TbComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbComment::getIsDelete, 0)
                .orderByDesc(TbComment::getCreateTime);

        // 关键词搜索
        if (StringUtils.isNotBlank(pageQueryVO.getKeyword())) {
            queryWrapper.like(TbComment::getContent, pageQueryVO.getKeyword())
                    .or().like(TbComment::getAuthorName, pageQueryVO.getKeyword());
        }

        Page<TbComment> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbComment> resultPage = page(page, queryWrapper);

        List<CommentVO> commentVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(commentVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    @Override
    public PageResult<CommentVO> getArticleComments(Long articleId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TbComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbComment::getArticleId, articleId)
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .orderByDesc(TbComment::getIsSticky)
                .orderByDesc(TbComment::getCreateTime);

        Page<TbComment> page = new Page<>(pageNum, pageSize);
        Page<TbComment> resultPage = page(page, queryWrapper);

        List<CommentVO> commentVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(commentVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    @Override
    public PageResult<CommentVO> searchComments(String keyword, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(keyword)) {
            return PageResult.empty(pageNum, pageSize);
        }

        LambdaQueryWrapper<TbComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbComment::getIsDelete, 0)
                .and(wrapper -> wrapper.like(TbComment::getContent, keyword)
                        .or().like(TbComment::getAuthorName, keyword))
                .orderByDesc(TbComment::getCreateTime);

        Page<TbComment> page = new Page<>(pageNum, pageSize);
        Page<TbComment> resultPage = page(page, queryWrapper);

        List<CommentVO> commentVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(commentVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    @Override
    public Integer getCommentCountByArticleId(Long articleId) {
        return Math.toIntExact(lambdaQuery()
                .eq(TbComment::getArticleId, articleId)
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .count());
    }

    // ==================== 评论审核管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer approveComment(Long commentId) {
        log.info("审核通过评论：评论ID={}", commentId);

        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 获取评论信息并更新文章评论数
            TbComment comment = getById(commentId);
            if (comment != null) {
                updateArticleCommentCount(comment.getArticleId());
                clearCommentCache(comment.getArticleId());
            }
            log.info("评论审核通过，评论ID：{}", commentId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer rejectComment(Long commentId, String reason) {
        log.info("审核拒绝评论：评论ID={}, 原因={}", commentId, reason);

        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getStatus, SystemConstants.Comment.STATUS_REJECTED)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated) {
            log.info("评论审核拒绝，评论ID：{}", commentId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchAuditComments(List<Long> commentIds, Integer status) {
        log.info("批量审核评论：数量={}, 审核状态={}", commentIds.size(), status);

        // 验证状态值是否有效
        if (status == null || !isValidStatus(status)) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "无效的审核状态");
        }

        boolean updated = lambdaUpdate()
                .in(TbComment::getId, commentIds)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getStatus, status)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated && status.equals(SystemConstants.Comment.STATUS_APPROVED)) {
            // 如果是批量通过，需要更新相关文章的评论数
            List<TbComment> comments = lambdaQuery()
                    .in(TbComment::getId, commentIds)
                    .list();

            Set<Long> articleIds = comments.stream()
                    .map(TbComment::getArticleId)
                    .collect(Collectors.toSet());

            for (Long articleId : articleIds) {
                updateArticleCommentCount(articleId);
                clearCommentCache(articleId);
            }
        }

        log.info("批量审核评论完成，数量：{}", commentIds.size());
        return updated ? 1 : 0;
    }

    /**
     * 验证评论状态是否有效
     */
    private boolean isValidStatus(Integer status) {
        return status.equals(SystemConstants.Comment.STATUS_PENDING) ||
                status.equals(SystemConstants.Comment.STATUS_APPROVED) ||
                status.equals(SystemConstants.Comment.STATUS_REJECTED) ||
                status.equals(SystemConstants.Comment.STATUS_SPAM);
    }

    @Override
    public PageResult<CommentVO> getPendingComments(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TbComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_PENDING)
                .orderByDesc(TbComment::getCreateTime);

        Page<TbComment> page = new Page<>(pageNum, pageSize);
        Page<TbComment> resultPage = page(page, queryWrapper);

        List<CommentVO> commentVOList = convertToVOList(resultPage.getRecords());
        return PageResult.of(commentVOList, resultPage.getTotal(), pageNum, pageSize);
    }

    // ==================== 评论互动操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer likeComment(Long commentId, Long userId) {
        // 增加点赞数
        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .setSql("like_count = like_count + 1")
                .update();

        if (updated) {
            log.info("评论点赞成功，评论ID：{}, 用户ID：{}", commentId, userId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer unlikeComment(Long commentId, Long userId) {
        // 减少点赞数
        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .setSql("like_count = GREATEST(like_count - 1, 0)")
                .update();

        if (updated) {
            log.info("评论取消点赞成功，评论ID：{}, 用户ID：{}", commentId, userId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer stickComment(Long commentId) {
        log.info("置顶评论：评论ID={}", commentId);

        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getIsSticky, 1)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated) {
            log.info("评论置顶成功，评论ID：{}", commentId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer unstickComment(Long commentId) {
        log.info("取消置顶评论：评论ID={}", commentId);

        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getIsSticky, 0)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated) {
            log.info("评论取消置顶成功，评论ID：{}", commentId);
            return 1;
        }

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer markAsSpam(Long commentId) {
        log.info("标记垃圾评论：评论ID={}", commentId);

        boolean updated = lambdaUpdate()
                .eq(TbComment::getId, commentId)
                .eq(TbComment::getIsDelete, 0)
                .set(TbComment::getStatus, SystemConstants.Comment.STATUS_SPAM)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        if (updated) {
            log.info("标记垃圾评论成功，评论ID：{}", commentId);
            return 1;
        }

        return 0;
    }

    // ==================== 评论统计和数据 ====================

    @Override
    public List<CommentVO> getHotComments(Integer limit) {
        List<TbComment> comments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .orderByDesc(TbComment::getLikeCount)
                .orderByDesc(TbComment::getReplyCount)
                .last("LIMIT " + limit)
                .list();

        return convertToVOList(comments);
    }

    @Override
    public List<CommentVO> getLatestComments(Integer limit) {
        List<TbComment> comments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .orderByDesc(TbComment::getCreateTime)
                .last("LIMIT " + limit)
                .list();

        return convertToVOList(comments);
    }

    @Override
    public Map<String, Object> getCommentStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总评论数
        Long totalComments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .count();

        // 待审核评论数
        Long pendingComments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_PENDING)
                .count();

        // 已通过评论数
        Long approvedComments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .count();

        // 垃圾评论数
        Long spamComments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_SPAM)
                .count();

        statistics.put("totalComments", totalComments);
        statistics.put("pendingComments", pendingComments);
        statistics.put("approvedComments", approvedComments);
        statistics.put("spamComments", spamComments);

        return statistics;
    }

    // ==================== 评论检查和验证 ====================

    @Override
    public Integer isSpamComment(String content, String authorName, String ipAddress) {
        if (content == null || content.trim().isEmpty()) {
            return 1; // 空内容视为垃圾评论
        }

        String lowerContent = content.toLowerCase();

        // 检查垃圾词汇
        String[] spamKeywords = { "广告", "赚钱", "免费", "点击", "优惠", "推广" };
        for (String keyword : spamKeywords) {
            if (lowerContent.contains(keyword)) {
                return 1; // 包含垃圾关键词
            }
        }

        // 检查IP是否在短时间内频繁评论
        String cacheKey = RedisConstants.Comment.IP_COMMENT_COUNT + ipAddress;
        Integer commentCount = redisUtils.get(cacheKey, Integer.class);
        if (commentCount != null && commentCount > 5) {
            return 1; // IP频繁评论视为垃圾
        }

        return 0; // 正常评论
    }

    @Override
    public Integer checkIpRestriction(String ipAddress) {
        String cacheKey = RedisConstants.Comment.IP_COMMENT_COUNT + ipAddress;
        Integer commentCount = redisUtils.get(cacheKey, Integer.class);

        if (commentCount == null) {
            commentCount = 0;
        }

        // 每小时最多评论10次
        if (commentCount >= 10) {
            return 1; // 已限制
        }

        // 增加计数
        redisUtils.set(cacheKey, commentCount + 1, 3600); // 1小时过期

        return 0; // 未限制
    }

    // ==================== 数据维护操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanupSpamComments(Integer days) {
        log.info("清理垃圾评论：天数={}", days);

        // 计算清理时间点
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date cleanupDate = calendar.getTime();

        // 先查询要删除的垃圾评论数量
        Long deleteCount = lambdaQuery()
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_SPAM)
                .lt(TbComment::getCreateTime, cleanupDate)
                .eq(TbComment::getIsDelete, 0)
                .count();

        // 删除指定天数前的垃圾评论
        boolean deleted = lambdaUpdate()
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_SPAM)
                .lt(TbComment::getCreateTime, cleanupDate)
                .set(TbComment::getIsDelete, 1)
                .set(TbComment::getUpdateTime, new Date())
                .update();

        int cleanedCount = deleted ? deleteCount.intValue() : 0;
        log.info("垃圾评论清理完成，清理数量：{}", cleanedCount);
        return cleanedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer rebuildCommentTree() {
        log.info("重建评论树结构");

        // 获取所有评论
        List<TbComment> allComments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .list();

        int rebuiltCount = 0;
        // 重新计算每个评论的回复数
        for (TbComment comment : allComments) {
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                // 顶级评论，计算回复数
                Long replyCount = lambdaQuery()
                        .eq(TbComment::getParentId, comment.getId())
                        .eq(TbComment::getIsDelete, 0)
                        .count();

                boolean updated = lambdaUpdate()
                        .eq(TbComment::getId, comment.getId())
                        .set(TbComment::getReplyCount, replyCount.intValue())
                        .update();

                if (updated) {
                    rebuiltCount++;
                }
            }
        }

        log.info("评论树结构重建完成，重建数量：{}", rebuiltCount);
        return rebuiltCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncCommentReplyCount() {
        log.info("同步评论回复数量");

        // 获取所有父评论
        List<TbComment> parentComments = lambdaQuery()
                .eq(TbComment::getIsDelete, 0)
                .and(wrapper -> wrapper.isNull(TbComment::getParentId)
                        .or().eq(TbComment::getParentId, 0))
                .list();

        int syncedCount = 0;
        // 更新每个父评论的回复数
        for (TbComment parent : parentComments) {
            Long replyCount = lambdaQuery()
                    .eq(TbComment::getParentId, parent.getId())
                    .eq(TbComment::getIsDelete, 0)
                    .count();

            boolean updated = lambdaUpdate()
                    .eq(TbComment::getId, parent.getId())
                    .set(TbComment::getReplyCount, replyCount.intValue())
                    .update();

            if (updated) {
                syncedCount++;
            }
        }

        log.info("评论回复数量同步完成，同步数量：{}", syncedCount);
        return syncedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateArticleCommentCount(Long articleId) {
        // 统计文章的评论数量
        Long commentCount = lambdaQuery()
                .eq(TbComment::getArticleId, articleId)
                .eq(TbComment::getIsDelete, 0)
                .eq(TbComment::getStatus, SystemConstants.Comment.STATUS_APPROVED)
                .count();

        // 这里需要调用文章服务更新文章的评论数，暂时用日志记录
        log.info("文章ID: {} 的评论数量为: {}", articleId, commentCount);

        return commentCount.intValue();
    }

    // ==================== 数据转换方法 ====================

    @Override
    public CommentVO convertToVO(TbComment comment) {
        if (comment == null) {
            return null;
        }

        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);

        return commentVO;
    }

    @Override
    public List<CommentVO> convertToVOList(List<TbComment> comments) {
        if (ObjectUtils.isEmpty(comments)) {
            return new ArrayList<>();
        }

        return comments.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从创建VO构建评论对象
     */
    private TbComment buildCommentFromCreate(CommentCreateVO createVO, String clientIp) {
        TbComment comment = new TbComment();

        // 手动设置字段，因为VO和DO的字段名不完全匹配
        comment.setArticleId(createVO.getArticleId());
        comment.setParentId(createVO.getParentId() != null ? createVO.getParentId() : 0L);
        comment.setReplyToId(createVO.getReplyId());
        comment.setContent(createVO.getSafeContent());
        comment.setAuthorName(createVO.getSafeNickname());
        comment.setAuthorEmail(createVO.getEmail());
        comment.setAuthorWebsite(createVO.getSafeWebsite());
        comment.setAuthorIp(clientIp);
        comment.setAuthorAgent(createVO.getUserAgent());

        // 设置默认值
        Date now = new Date();
        comment.setCreateTime(now);
        comment.setUpdateTime(now);
        comment.setIsDelete(0);
        comment.setStatus(SystemConstants.Comment.STATUS_PENDING); // 默认待审核
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setIsSticky(0);
        comment.setIsAdmin(0); // 默认非管理员评论
        comment.setNotifyEmail(createVO.shouldNotifyAuthor() || createVO.shouldNotifyReplyUser() ? 1 : 0);

        // 设置评论层级（可根据parentId计算）
        comment.setLevel(createVO.getCommentLevel());

        return comment;
    }

    /**
     * 清除评论相关缓存
     */
    private void clearCommentCache(Long articleId) {
        String cacheKey = RedisConstants.Comment.ARTICLE_COMMENTS + articleId;
        redisUtils.delete(cacheKey);
    }
}
