package pw.pj.controller.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.VisitLogVO;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.PageResult;
import pw.pj.common.utils.IpUtils;
import pw.pj.service.TbVisitLogService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器
 * 提供网站访问统计、用户行为分析、内容统计等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Api(tags = "统计分析")
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    @Autowired
    private TbVisitLogService visitLogService;

    /**
     * 记录访问日志
     * 
     * @param request   HTTP请求对象
     * @param articleId 文章ID（可选）
     * @return 操作结果
     */
    @ApiOperation("记录访问日志")
    @PostMapping("/visit")
    public ApiResponse<Long> recordVisit(
            HttpServletRequest request,
            @RequestParam(required = false) Long articleId) {

        try {
            // 调用服务层记录访问日志
            Long visitLogId = visitLogService.recordVisit(articleId, request);

            log.info("访问日志记录成功: visitLogId={}, articleId={}", visitLogId, articleId);
            return ApiResponse.success(visitLogId);

        } catch (Exception e) {
            log.error("记录访问日志失败: {}", e.getMessage(), e);
            return ApiResponse.error("记录访问日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取网站总览统计
     * 
     * @return 总览统计数据
     */
    @ApiOperation("获取网站总览统计")
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverviewStatistics() {
        log.info("获取网站总览统计");

        try {
            // 调用服务层获取总览统计
            Map<String, Object> overviewStats = visitLogService.getVisitStatisticsOverview();

            log.info("获取网站总览统计成功");
            return ApiResponse.success(overviewStats);

        } catch (Exception e) {
            log.error("获取网站总览统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取总览统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日访问统计
     * 
     * @return 今日统计数据
     */
    @ApiOperation("获取今日访问统计")
    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayStatistics() {
        log.info("获取今日访问统计");

        try {
            // 调用服务层获取今日统计
            Map<String, Object> todayStats = visitLogService.getTodayVisitStatistics();

            log.info("获取今日访问统计成功");
            return ApiResponse.success(todayStats);

        } catch (Exception e) {
            log.error("获取今日访问统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取今日统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取访问趋势统计
     * 
     * @param days 统计天数
     * @return 访问趋势数据
     */
    @ApiOperation("获取访问趋势统计")
    @GetMapping("/visits/trend")
    public ApiResponse<List<Map<String, Object>>> getVisitTrend(
            @RequestParam(defaultValue = "30") Integer days) {
        log.info("获取访问趋势统计: days={}", days);

        try {
            // 调用服务层获取访问趋势
            List<Map<String, Object>> visitTrend = visitLogService.getVisitTrend(days);

            log.info("获取访问趋势统计成功: days={}", days);
            return ApiResponse.success(visitTrend);

        } catch (Exception e) {
            log.error("获取访问趋势统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取访问趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门文章统计
     * 
     * @param limit 返回数量限制
     * @return 热门文章统计数据
     */
    @ApiOperation("获取热门文章统计")
    @GetMapping("/articles/hot")
    public ApiResponse<List<Map<String, Object>>> getHotArticles(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门文章统计: limit={}", limit);

        try {
            // 调用服务层获取热门文章统计
            List<Map<String, Object>> hotArticles = visitLogService.getHotArticlesStatistics(limit);

            log.info("获取热门文章统计成功: count={}", hotArticles.size());
            return ApiResponse.success(hotArticles);

        } catch (Exception e) {
            log.error("获取热门文章统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取热门文章统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取浏览器统计
     * 
     * @param limit 返回数量限制
     * @return 浏览器统计数据
     */
    @ApiOperation("获取浏览器统计")
    @GetMapping("/browser")
    public ApiResponse<List<Map<String, Object>>> getBrowserStatistics(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取浏览器统计: limit={}", limit);

        try {
            // 调用服务层获取浏览器统计
            List<Map<String, Object>> browserStats = visitLogService.getBrowserStatistics(limit);

            log.info("获取浏览器统计成功: count={}", browserStats.size());
            return ApiResponse.success(browserStats);

        } catch (Exception e) {
            log.error("获取浏览器统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取浏览器统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取操作系统统计
     * 
     * @param limit 返回数量限制
     * @return 操作系统统计数据
     */
    @ApiOperation("获取操作系统统计")
    @GetMapping("/os")
    public ApiResponse<List<Map<String, Object>>> getOsStatistics(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取操作系统统计: limit={}", limit);

        try {
            // 调用服务层获取操作系统统计
            List<Map<String, Object>> osStats = visitLogService.getOsStatistics(limit);

            log.info("获取操作系统统计成功: count={}", osStats.size());
            return ApiResponse.success(osStats);

        } catch (Exception e) {
            log.error("获取操作系统统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取操作系统统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备类型统计
     * 
     * @return 设备类型统计数据
     */
    @ApiOperation("获取设备类型统计")
    @GetMapping("/device")
    public ApiResponse<Map<String, Object>> getDeviceStatistics() {
        log.info("获取设备类型统计");

        try {
            // 调用服务层获取设备统计
            Map<String, Object> deviceStats = visitLogService.getDeviceStatistics();

            log.info("获取设备类型统计成功");
            return ApiResponse.success(deviceStats);

        } catch (Exception e) {
            log.error("获取设备类型统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取设备类型统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取地理位置统计
     * 
     * @param limit 返回数量限制
     * @return 地理位置统计数据
     */
    @ApiOperation("获取地理位置统计")
    @GetMapping("/location")
    public ApiResponse<List<Map<String, Object>>> getLocationStatistics(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取地理位置统计: limit={}", limit);

        try {
            // 调用服务层获取地理位置统计
            List<Map<String, Object>> locationStats = visitLogService.getLocationStatistics(limit);

            log.info("获取地理位置统计成功: count={}", locationStats.size());
            return ApiResponse.success(locationStats);

        } catch (Exception e) {
            log.error("获取地理位置统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取地理位置统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取每小时访问统计
     * 
     * @return 每小时访问统计数据
     */
    @ApiOperation("获取每小时访问统计")
    @GetMapping("/hourly")
    public ApiResponse<List<Map<String, Object>>> getHourlyStatistics() {
        log.info("获取每小时访问统计");

        try {
            // 调用服务层获取每小时统计（默认今日）
            List<Map<String, Object>> hourlyStats = visitLogService.getHourlyStatistics(null);

            log.info("获取每小时访问统计成功: count={}", hourlyStats.size());
            return ApiResponse.success(hourlyStats);

        } catch (Exception e) {
            log.error("获取每小时访问统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取每小时访问统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取爬虫访问统计
     * 
     * @return 爬虫统计数据
     */
    @ApiOperation("获取爬虫访问统计")
    @GetMapping("/spider")
    public ApiResponse<Map<String, Object>> getSpiderStatistics() {
        log.info("获取爬虫访问统计");

        try {
            // 调用服务层获取爬虫统计
            Map<String, Object> spiderStats = visitLogService.getSpiderStatistics();

            log.info("获取爬虫访问统计成功");
            return ApiResponse.success(spiderStats);

        } catch (Exception e) {
            log.error("获取爬虫访问统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取爬虫访问统计失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询访问日志
     * 
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键词
     * @return 访问日志列表
     */
    @ApiOperation("分页查询访问日志")
    @GetMapping("/logs/page")
    public ApiResponse<PageResult<VisitLogVO>> pageVisitLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        log.info("分页查询访问日志: pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);

        try {
            // 构建查询参数
            PageQueryVO pageQueryVO = new PageQueryVO();
            pageQueryVO.setPageNum(pageNum);
            pageQueryVO.setPageSize(pageSize);
            pageQueryVO.setKeyword(keyword);

            // 调用服务层分页查询访问日志
            PageResult<VisitLogVO> visitLogs = visitLogService.getVisitLogList(pageQueryVO);

            log.info("分页查询访问日志成功: total={}", visitLogs.getTotal());
            return ApiResponse.success(visitLogs);

        } catch (Exception e) {
            log.error("分页查询访问日志失败: {}", e.getMessage(), e);
            return ApiResponse.error("查询访问日志失败: " + e.getMessage());
        }
    }

    /**
     * 根据文章ID查询访问日志
     * 
     * @param articleId 文章ID
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @return 访问日志列表
     */
    @ApiOperation("根据文章ID查询访问日志")
    @GetMapping("/logs/article/{articleId}")
    public ApiResponse<PageResult<VisitLogVO>> getVisitLogsByArticle(
            @ApiParam(value = "文章ID", required = true) @PathVariable @NotNull Long articleId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("根据文章ID查询访问日志: articleId={}, pageNum={}, pageSize={}", articleId, pageNum, pageSize);

        try {
            // 调用服务层根据文章ID查询访问日志
            PageResult<VisitLogVO> visitLogs = visitLogService.getVisitLogsByArticleId(articleId, pageNum, pageSize);

            log.info("根据文章ID查询访问日志成功: articleId={}, total={}", articleId, visitLogs.getTotal());
            return ApiResponse.success(visitLogs);

        } catch (Exception e) {
            log.error("根据文章ID查询访问日志失败: {}", e.getMessage(), e);
            return ApiResponse.error("查询访问日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时在线统计
     * 
     * @return 实时统计数据
     */
    @ApiOperation("获取实时在线统计")
    @GetMapping("/realtime")
    public ApiResponse<Map<String, Object>> getRealtimeStatistics() {
        log.info("获取实时在线统计");

        try {
            // 调用服务层获取实时统计
            Map<String, Object> realtimeStats = visitLogService.getRealTimeStatistics();

            log.info("获取实时在线统计成功");
            return ApiResponse.success(realtimeStats);

        } catch (Exception e) {
            log.error("获取实时在线统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取实时统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时在线用户数
     * 
     * @return 在线用户数
     */
    @ApiOperation("获取实时在线用户数")
    @GetMapping("/online")
    public ApiResponse<Integer> getOnlineUsers() {
        log.info("获取实时在线用户数");

        try {
            // 调用服务层获取在线用户数
            Integer onlineUsers = visitLogService.getRealTimeOnlineUsers();

            log.info("获取实时在线用户数成功: count={}", onlineUsers);
            return ApiResponse.success(onlineUsers);

        } catch (Exception e) {
            log.error("获取实时在线用户数失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取在线用户数失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户访问路径分析
     * 
     * @param visitorIp 访问者IP
     * @param limit     路径数量限制
     * @return 访问路径数据
     */
    @ApiOperation("获取用户访问路径分析")
    @GetMapping("/user/path")
    public ApiResponse<List<VisitLogVO>> getUserVisitPath(
            @RequestParam String visitorIp,
            @RequestParam(defaultValue = "50") Integer limit) {
        log.info("获取用户访问路径分析: visitorIp={}, limit={}", visitorIp, limit);

        try {
            // 调用服务层获取用户访问路径
            List<VisitLogVO> visitPath = visitLogService.getUserVisitPath(visitorIp, limit);

            log.info("获取用户访问路径分析成功: visitorIp={}, count={}", visitorIp, visitPath.size());
            return ApiResponse.success(visitPath);

        } catch (Exception e) {
            log.error("获取用户访问路径分析失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取用户访问路径失败: " + e.getMessage());
        }
    }

    /**
     * 获取页面停留时间统计
     * 
     * @param articleId 文章ID（可选）
     * @return 停留时间统计数据
     */
    @ApiOperation("获取页面停留时间统计")
    @GetMapping("/stay-time")
    public ApiResponse<Map<String, Object>> getStayTimeStatistics(
            @RequestParam(required = false) Long articleId) {
        log.info("获取页面停留时间统计: articleId={}", articleId);

        try {
            // 调用服务层获取停留时间统计
            Map<String, Object> stayTimeStats = visitLogService.getStayTimeStatistics(articleId);

            log.info("获取页面停留时间统计成功: articleId={}", articleId);
            return ApiResponse.success(stayTimeStats);

        } catch (Exception e) {
            log.error("获取页面停留时间统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取停留时间统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户忠诚度分析
     * 
     * @return 忠诚度分析数据
     */
    @ApiOperation("获取用户忠诚度分析")
    @GetMapping("/user/loyalty")
    public ApiResponse<Map<String, Object>> getUserLoyaltyAnalysis() {
        log.info("获取用户忠诚度分析");

        try {
            // 调用服务层获取用户忠诚度分析
            Map<String, Object> loyaltyAnalysis = visitLogService.getUserLoyaltyAnalysis();

            log.info("获取用户忠诚度分析成功");
            return ApiResponse.success(loyaltyAnalysis);

        } catch (Exception e) {
            log.error("获取用户忠诚度分析失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取用户忠诚度分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取访问深度分析
     * 
     * @return 访问深度分析数据
     */
    @ApiOperation("获取访问深度分析")
    @GetMapping("/visit/depth")
    public ApiResponse<Map<String, Object>> getVisitDepthAnalysis() {
        log.info("获取访问深度分析");

        try {
            // 调用服务层获取访问深度分析
            Map<String, Object> depthAnalysis = visitLogService.getVisitDepthAnalysis();

            log.info("获取访问深度分析成功");
            return ApiResponse.success(depthAnalysis);

        } catch (Exception e) {
            log.error("获取访问深度分析失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取访问深度分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取访问来源统计
     * 
     * @param limit 返回数量限制
     * @return 来源统计数据
     */
    @ApiOperation("获取访问来源统计")
    @GetMapping("/referer")
    public ApiResponse<List<Map<String, Object>>> getRefererStatistics(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取访问来源统计: limit={}", limit);

        try {
            // 调用服务层获取来源统计
            List<Map<String, Object>> refererStats = visitLogService.getRefererStatistics(limit);

            log.info("获取访问来源统计成功: count={}", refererStats.size());
            return ApiResponse.success(refererStats);

        } catch (Exception e) {
            log.error("获取访问来源统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取访问来源统计失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期访问日志
     * 
     * @param days 保留天数
     * @return 清理结果
     */
    @ApiOperation("清理过期访问日志")
    @PostMapping("/logs/cleanup")
    public ApiResponse<Integer> cleanupExpiredLogs(
            @RequestParam(defaultValue = "90") Integer days) {
        log.info("清理过期访问日志: days={}", days);

        try {
            // 调用服务层清理过期日志
            Integer cleanedCount = visitLogService.cleanupExpiredLogs(days);

            log.info("清理过期访问日志成功: cleanedCount={}", cleanedCount);
            return ApiResponse.success(cleanedCount);

        } catch (Exception e) {
            log.error("清理过期访问日志失败: {}", e.getMessage(), e);
            return ApiResponse.error("清理日志失败: " + e.getMessage());
        }
    }

    /**
     * 清理爬虫访问日志
     * 
     * @param days 保留天数
     * @return 清理结果
     */
    @ApiOperation("清理爬虫访问日志")
    @PostMapping("/logs/cleanup/spider")
    public ApiResponse<Integer> cleanupSpiderLogs(
            @RequestParam(defaultValue = "30") Integer days) {
        log.info("清理爬虫访问日志: days={}", days);

        try {
            // 调用服务层清理爬虫日志
            Integer cleanedCount = visitLogService.cleanupSpiderLogs(days);

            log.info("清理爬虫访问日志成功: cleanedCount={}", cleanedCount);
            return ApiResponse.success(cleanedCount);

        } catch (Exception e) {
            log.error("清理爬虫访问日志失败: {}", e.getMessage(), e);
            return ApiResponse.error("清理爬虫日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取可疑访问IP列表
     * 
     * @param limit 数量限制
     * @return 可疑IP列表
     */
    @ApiOperation("获取可疑访问IP列表")
    @GetMapping("/suspicious-ip")
    public ApiResponse<List<Map<String, Object>>> getSuspiciousIpList(
            @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取可疑访问IP列表: limit={}", limit);

        try {
            // 调用服务层获取可疑IP列表
            List<Map<String, Object>> suspiciousIps = visitLogService.getSuspiciousIpList(limit);

            log.info("获取可疑访问IP列表成功: count={}", suspiciousIps.size());
            return ApiResponse.success(suspiciousIps);

        } catch (Exception e) {
            log.error("获取可疑访问IP列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取可疑IP列表失败: " + e.getMessage());
        }
    }

    /**
     * 刷新实时统计缓存
     * 
     * @return 刷新结果
     */
    @ApiOperation("刷新实时统计缓存")
    @PostMapping("/cache/refresh")
    public ApiResponse<Void> refreshRealTimeCache() {
        log.info("刷新实时统计缓存");

        try {
            // 调用服务层刷新实时统计缓存
            Boolean result = visitLogService.refreshRealTimeCache();

            if (result) {
                log.info("实时统计缓存刷新成功");
                return ApiResponse.success();
            } else {
                return ApiResponse.error("缓存刷新失败");
            }

        } catch (Exception e) {
            log.error("刷新实时统计缓存失败: {}", e.getMessage(), e);
            return ApiResponse.error("缓存刷新失败: " + e.getMessage());
        }
    }

    /**
     * 更新访问停留时间
     * 
     * @param visitLogId 访问日志ID
     * @param stayTime   停留时间（秒）
     * @return 更新结果
     */
    @ApiOperation("更新访问停留时间")
    @PutMapping("/visit/{visitLogId}/stay-time")
    public ApiResponse<Void> updateStayTime(
            @ApiParam(value = "访问日志ID", required = true) @PathVariable @NotNull Long visitLogId,
            @ApiParam(value = "停留时间（秒）", required = true) @RequestParam @NotNull Integer stayTime) {
        log.info("更新访问停留时间: visitLogId={}, stayTime={}", visitLogId, stayTime);

        try {
            // 调用服务层更新停留时间
            Boolean result = visitLogService.updateStayTime(visitLogId, stayTime);

            if (result) {
                log.info("访问停留时间更新成功: visitLogId={}, stayTime={}", visitLogId, stayTime);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("停留时间更新失败");
            }

        } catch (Exception e) {
            log.error("更新访问停留时间失败: {}", e.getMessage(), e);
            return ApiResponse.error("停留时间更新失败: " + e.getMessage());
        }
    }
}