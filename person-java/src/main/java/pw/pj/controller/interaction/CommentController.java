package pw.pj.controller.interaction;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pw.pj.POJO.VO.CommentCreateVO;
import pw.pj.POJO.VO.CommentVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.common.utils.IpUtils;
import pw.pj.service.TbCommentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评论管理控制器
 * 提供评论的增删改查、审核、点赞等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Api(tags = "评论系统")
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    @Autowired
    private TbCommentService commentService;

    /**
     * 发表评论
     * 
     * @param createVO 评论创建参数
     * @param request  HTTP请求对象
     * @return 评论信息
     */
    @ApiOperation("发表评论")
    @PostMapping
    public ApiResponse<CommentVO> createComment(
            @Valid @RequestBody CommentCreateVO createVO,
            HttpServletRequest request) {
        log.info("发表评论: {}", createVO);

        try {
            String clientIp = IpUtils.getClientIp(request);
            String userAgent = IpUtils.getUserAgent(request);

            // 设置IP和UserAgent到VO中
            createVO.setClientIp(clientIp);
            createVO.setUserAgent(userAgent);

            // 调用服务层创建评论
            CommentVO comment = commentService.createComment(createVO);

            log.info("评论发表成功: commentId={}, articleId={}", comment.getId(), createVO.getArticleId());
            return ApiResponse.success("评论发表成功", comment);

        } catch (Exception e) {
            log.error("发表评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("评论发表失败: " + e.getMessage());
        }
    }

    /**
     * 回复评论
     * 
     * @param parentId 父评论ID
     * @param createVO 评论创建参数
     * @param request  HTTP请求对象
     * @return 回复评论信息
     */
    @ApiOperation("回复评论")
    @PostMapping("/{parentId}/reply")
    public ApiResponse<CommentVO> replyComment(
            @ApiParam(value = "父评论ID", required = true) @PathVariable @NotNull Long parentId,
            @Valid @RequestBody CommentCreateVO createVO,
            HttpServletRequest request) {
        log.info("回复评论: parentId={}, reply={}", parentId, createVO);

        try {
            String clientIp = IpUtils.getClientIp(request);
            String userAgent = IpUtils.getUserAgent(request);

            // 设置IP和UserAgent到VO中
            createVO.setClientIp(clientIp);
            createVO.setUserAgent(userAgent);
            // 设置父评论ID
            createVO.setParentId(parentId);

            // 调用服务层回复评论
            CommentVO comment = commentService.replyComment(parentId, createVO);

            log.info("评论回复成功: commentId={}, parentId={}", comment.getId(), parentId);
            return ApiResponse.success("回复评论成功", comment);

        } catch (Exception e) {
            log.error("回复评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("回复评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论详情
     * 
     * @param id 评论ID
     * @return 评论详情
     */
    @ApiOperation("获取评论详情")
    @GetMapping("/{id}")
    public ApiResponse<CommentVO> getComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable @NotNull Long id) {
        log.info("获取评论详情: commentId={}", id);

        try {
            // 调用服务层获取评论详情
            CommentVO comment = commentService.getCommentById(id);

            if (comment != null) {
                log.info("获取评论详情成功: commentId={}", id);
                return ApiResponse.success(comment);
            } else {
                return ApiResponse.error("评论不存在");
            }

        } catch (Exception e) {
            log.error("获取评论详情失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取评论详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取文章评论列表
     * 
     * @param articleId 文章ID
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @return 评论列表
     */
    @ApiOperation("获取文章评论列表")
    @GetMapping("/article/{articleId}")
    public ApiResponse<PageResult<CommentVO>> getArticleComments(
            @ApiParam(value = "文章ID", required = true) @PathVariable @NotNull Long articleId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("获取文章评论列表: articleId={}, pageNum={}, pageSize={}", articleId, pageNum, pageSize);

        try {
            // 调用服务层获取文章评论列表
            PageResult<CommentVO> commentList = commentService.getArticleComments(articleId, pageNum, pageSize);

            log.info("获取文章评论列表成功: articleId={}, total={}", articleId, commentList.getTotal());
            return ApiResponse.success(commentList);

        } catch (Exception e) {
            log.error("获取文章评论列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取文章评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新评论列表
     * 
     * @param limit 返回数量限制
     * @return 最新评论列表
     */
    @ApiOperation("获取最新评论列表")
    @GetMapping("/recent")
    public ApiResponse<List<CommentVO>> getRecentComments(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最新评论列表: limit={}", limit);

        try {
            // 调用服务层获取最新评论列表
            List<CommentVO> recentComments = commentService.getLatestComments(limit);

            log.info("获取最新评论列表成功: count={}", recentComments.size());
            return ApiResponse.success(recentComments);

        } catch (Exception e) {
            log.error("获取最新评论列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取最新评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 点赞评论
     * 
     * @param id      评论ID
     * @param request HTTP请求对象
     * @return 点赞结果
     */
    @ApiOperation("点赞评论")
    @PostMapping("/{id}/like")
    public ApiResponse<Void> likeComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable @NotNull Long id,
            HttpServletRequest request) {
        log.info("点赞评论: commentId={}", id);

        try {
            // 调用服务层点赞评论 (userId设为null，表示游客点赞)
            Integer result = commentService.likeComment(id, null);

            if (result != null && result > 0) {
                log.info("评论点赞成功: commentId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("点赞失败");
            }

        } catch (Exception e) {
            log.error("点赞评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @return 删除结果
     */
    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable @NotNull Long id) {
        log.info("删除评论: commentId={}", id);

        try {
            // 调用服务层删除评论
            Integer result = commentService.deleteComment(id);

            if (result != null && result > 0) {
                log.info("评论删除成功: commentId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("删除失败");
            }

        } catch (Exception e) {
            log.error("删除评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除评论
     * 
     * @param ids 评论ID列表
     * @return 删除结果
     */
    @ApiOperation("批量删除评论")
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteCommentsBatch(@RequestBody List<Long> ids) {
        log.info("批量删除评论: count={}", ids.size());

        try {
            // 调用服务层批量删除评论
            Integer result = commentService.deleteCommentsBatch(ids);

            if (result != null && result > 0) {
                log.info("批量删除评论成功: count={}", result);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("删除失败");
            }

        } catch (Exception e) {
            log.error("批量删除评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 审核评论
     * 
     * @param id     评论ID
     * @param status 审核状态：1-通过，2-拒绝
     * @param reason 拒绝原因（可选）
     * @return 审核结果
     */
    @ApiOperation("审核评论")
    @PutMapping("/{id}/audit")
    public ApiResponse<Void> auditComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable @NotNull Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason) {
        log.info("审核评论: commentId={}, status={}, reason={}", id, status, reason);

        try {
            Integer result;
            if (status == 1) {
                // 审核通过
                result = commentService.approveComment(id);
            } else if (status == 2) {
                // 审核拒绝
                result = commentService.rejectComment(id, reason);
            } else {
                return ApiResponse.error("无效的审核状态");
            }

            if (result != null && result > 0) {
                log.info("评论审核成功: commentId={}, status={}", id, status);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("审核失败");
            }

        } catch (Exception e) {
            log.error("审核评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("审核评论失败: " + e.getMessage());
        }
    }

    /**
     * 置顶评论
     * 
     * @param id 评论ID
     * @return 置顶结果
     */
    @ApiOperation("置顶评论")
    @PutMapping("/{id}/stick")
    public ApiResponse<Void> stickComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable @NotNull Long id) {
        log.info("置顶评论: commentId={}", id);

        try {
            // 调用服务层置顶评论
            Integer result = commentService.stickComment(id);

            if (result != null && result > 0) {
                log.info("评论置顶成功: commentId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("置顶失败");
            }

        } catch (Exception e) {
            log.error("置顶评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("置顶评论失败: " + e.getMessage());
        }
    }

    /**
     * 取消置顶评论
     * 
     * @param id 评论ID
     * @return 取消置顶结果
     */
    @ApiOperation("取消置顶评论")
    @PutMapping("/{id}/unstick")
    public ApiResponse<Void> unstickComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable @NotNull Long id) {
        log.info("取消置顶评论: commentId={}", id);

        try {
            // 调用服务层取消置顶评论
            Integer result = commentService.unstickComment(id);

            if (result != null && result > 0) {
                log.info("评论取消置顶成功: commentId={}", id);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("取消置顶失败");
            }

        } catch (Exception e) {
            log.error("取消置顶评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("取消置顶评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核评论列表
     * 
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 待审核评论列表
     */
    @ApiOperation("获取待审核评论列表")
    @GetMapping("/pending")
    public ApiResponse<PageResult<CommentVO>> getPendingComments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("获取待审核评论列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            // 调用服务层获取待审核评论列表
            PageResult<CommentVO> pendingComments = commentService.getPendingComments(pageNum, pageSize);

            log.info("获取待审核评论列表成功: total={}", pendingComments.getTotal());
            return ApiResponse.success(pendingComments);

        } catch (Exception e) {
            log.error("获取待审核评论列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取待审核评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 搜索评论
     * 
     * @param keyword  搜索关键词
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    @ApiOperation("搜索评论")
    @GetMapping("/search")
    public ApiResponse<PageResult<CommentVO>> searchComments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("搜索评论: keyword={}, pageNum={}, pageSize={}", keyword, pageNum, pageSize);

        try {
            // 调用服务层搜索评论
            PageResult<CommentVO> searchResult = commentService.searchComments(keyword, pageNum, pageSize);

            log.info("搜索评论成功: keyword={}, total={}", keyword, searchResult.getTotal());
            return ApiResponse.success(searchResult);

        } catch (Exception e) {
            log.error("搜索评论失败: {}", e.getMessage(), e);
            return ApiResponse.error("搜索评论失败: " + e.getMessage());
        }
    }

    /**
     * 管理端分页获取评论列表
     * 
     * @param pageQueryVO 分页查询参数
     * @return 评论列表
     */
    @ApiOperation("管理端获取评论列表")
    @PostMapping("/admin/list")
    public ApiResponse<PageResult<CommentVO>> adminPageComments(@RequestBody PageQueryVO pageQueryVO) {
        log.info("管理端获取评论列表: {}", pageQueryVO);

        try {
            // 调用服务层获取评论列表
            PageResult<CommentVO> commentList = commentService.getCommentList(pageQueryVO);

            log.info("管理端获取评论列表成功: total={}", commentList.getTotal());
            return ApiResponse.success(commentList);

        } catch (Exception e) {
            log.error("管理端获取评论列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取评论列表失败: " + e.getMessage());
        }
    }
}