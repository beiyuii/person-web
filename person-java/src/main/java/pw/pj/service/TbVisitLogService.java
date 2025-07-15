package pw.pj.service;

import pw.pj.POJO.DO.TbVisitLog;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.VisitLogVO;
import pw.pj.common.result.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 访问日志管理服务接口
 * 提供访问日志的记录、统计分析等功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbVisitLogService extends IService<TbVisitLog> {

    // ==================== 访问日志记录 ====================

    /**
     * 记录访问日志
     * 
     * @param articleId 文章ID（可为空，表示非文章页面访问）
     * @param request   HTTP请求对象
     * @return 访问日志ID
     */
    Long recordVisit(Long articleId, HttpServletRequest request);

    /**
     * 记录访问日志（详细参数）
     * 
     * @param articleId  文章ID
     * @param visitorIp  访问者IP
     * @param userAgent  用户代理
     * @param referer    来源页面
     * @param requestUrl 请求URL
     * @return 访问日志ID
     */
    Long recordVisit(Long articleId, String visitorIp, String userAgent, String referer, String requestUrl);

    /**
     * 更新访问停留时间
     * 
     * @param visitLogId 访问日志ID
     * @param stayTime   停留时间（秒）
     * @return 是否成功
     */
    Boolean updateStayTime(Long visitLogId, Integer stayTime);

    // ==================== 访问日志查询 ====================

    /**
     * 分页查询访问日志
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    PageResult<VisitLogVO> getVisitLogList(PageQueryVO pageQueryVO);

    /**
     * 根据文章ID查询访问日志
     * 
     * @param articleId 文章ID
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 分页结果
     */
    PageResult<VisitLogVO> getVisitLogsByArticleId(Long articleId, Integer pageNum, Integer pageSize);

    /**
     * 根据IP查询访问日志
     * 
     * @param visitorIp IP地址
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 分页结果
     */
    PageResult<VisitLogVO> getVisitLogsByIp(String visitorIp, Integer pageNum, Integer pageSize);

    /**
     * 根据时间范围查询访问日志
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 分页结果
     */
    PageResult<VisitLogVO> getVisitLogsByTimeRange(Date startTime, Date endTime, Integer pageNum, Integer pageSize);

    // ==================== 访问统计分析 ====================

    /**
     * 获取访问统计概览
     * 
     * @return 统计数据
     */
    Map<String, Object> getVisitStatisticsOverview();

    /**
     * 获取今日访问统计
     * 
     * @return 统计数据
     */
    Map<String, Object> getTodayVisitStatistics();

    /**
     * 获取指定日期的访问统计
     * 
     * @param date 日期
     * @return 统计数据
     */
    Map<String, Object> getVisitStatisticsByDate(Date date);

    /**
     * 获取最近N天的访问趋势
     * 
     * @param days 天数
     * @return 访问趋势数据
     */
    List<Map<String, Object>> getVisitTrend(Integer days);

    /**
     * 获取热门文章访问统计
     * 
     * @param limit 数量限制
     * @return 热门文章统计
     */
    List<Map<String, Object>> getHotArticlesStatistics(Integer limit);

    /**
     * 获取访问来源统计
     * 
     * @param limit 数量限制
     * @return 来源统计
     */
    List<Map<String, Object>> getRefererStatistics(Integer limit);

    /**
     * 获取浏览器统计
     * 
     * @param limit 数量限制
     * @return 浏览器统计
     */
    List<Map<String, Object>> getBrowserStatistics(Integer limit);

    /**
     * 获取操作系统统计
     * 
     * @param limit 数量限制
     * @return 操作系统统计
     */
    List<Map<String, Object>> getOsStatistics(Integer limit);

    /**
     * 获取设备类型统计
     * 
     * @return 设备类型统计
     */
    Map<String, Object> getDeviceStatistics();

    /**
     * 获取地理位置统计
     * 
     * @param limit 数量限制
     * @return 地理位置统计
     */
    List<Map<String, Object>> getLocationStatistics(Integer limit);

    /**
     * 获取每小时访问统计
     * 
     * @param date 日期（可为空，表示今日）
     * @return 每小时访问统计
     */
    List<Map<String, Object>> getHourlyStatistics(Date date);

    /**
     * 获取爬虫访问统计
     * 
     * @return 爬虫统计
     */
    Map<String, Object> getSpiderStatistics();

    // ==================== 用户行为分析 ====================

    /**
     * 获取用户访问路径分析
     * 
     * @param visitorIp 访问者IP
     * @param limit     路径数量限制
     * @return 访问路径
     */
    List<VisitLogVO> getUserVisitPath(String visitorIp, Integer limit);

    /**
     * 获取页面停留时间统计
     * 
     * @param articleId 文章ID（可为空，表示所有页面）
     * @return 停留时间统计
     */
    Map<String, Object> getStayTimeStatistics(Long articleId);

    /**
     * 获取用户忠诚度分析
     * 
     * @return 忠诚度分析
     */
    Map<String, Object> getUserLoyaltyAnalysis();

    /**
     * 获取访问深度分析
     * 
     * @return 访问深度分析
     */
    Map<String, Object> getVisitDepthAnalysis();

    // ==================== 数据清理和维护 ====================

    /**
     * 清理过期访问日志
     * 
     * @param days 保留天数
     * @return 清理的记录数
     */
    Integer cleanupExpiredLogs(Integer days);

    /**
     * 清理爬虫访问日志
     * 
     * @param days 保留天数
     * @return 清理的记录数
     */
    Integer cleanupSpiderLogs(Integer days);

    /**
     * 归档历史访问日志
     * 
     * @param months 归档月份数
     * @return 归档的记录数
     */
    Integer archiveHistoryLogs(Integer months);

    /**
     * 重新解析用户代理信息
     * 
     * @return 处理的记录数
     */
    Integer reParseUserAgent();

    /**
     * 更新访问地理位置信息
     * 
     * @return 处理的记录数
     */
    Integer updateLocationInfo();

    // ==================== 实时统计 ====================

    /**
     * 获取实时在线用户数
     * 
     * @return 在线用户数
     */
    Integer getRealTimeOnlineUsers();

    /**
     * 获取实时访问统计
     * 
     * @return 实时统计数据
     */
    Map<String, Object> getRealTimeStatistics();

    /**
     * 刷新实时统计缓存
     * 
     * @return 是否成功
     */
    Boolean refreshRealTimeCache();

    // ==================== 防刷和安全 ====================

    /**
     * 检查IP是否频繁访问
     * 
     * @param visitorIp IP地址
     * @param timeRange 时间范围（分钟）
     * @param maxCount  最大访问次数
     * @return 是否频繁访问
     */
    Boolean checkFrequentVisit(String visitorIp, Integer timeRange, Integer maxCount);

    /**
     * 识别并标记爬虫访问
     * 
     * @param userAgent 用户代理
     * @return 是否为爬虫
     */
    Boolean identifySpider(String userAgent);

    /**
     * 获取可疑访问IP列表
     * 
     * @param limit 数量限制
     * @return 可疑IP列表
     */
    List<Map<String, Object>> getSuspiciousIpList(Integer limit);

    // ==================== 数据转换方法 ====================

    /**
     * 将TbVisitLog转换为VisitLogVO
     * 
     * @param visitLog 访问日志实体
     * @return 访问日志VO
     */
    VisitLogVO convertToVO(TbVisitLog visitLog);

    /**
     * 将TbVisitLog列表转换为VisitLogVO列表
     * 
     * @param visitLogs 访问日志实体列表
     * @return 访问日志VO列表
     */
    List<VisitLogVO> convertToVOList(List<TbVisitLog> visitLogs);
}
