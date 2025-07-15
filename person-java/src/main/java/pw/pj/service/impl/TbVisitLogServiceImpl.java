package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pw.pj.POJO.DO.TbVisitLog;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.VisitLogVO;
import pw.pj.common.result.PageResult;
import pw.pj.mapper.TbVisitLogMapper;
import pw.pj.service.TbVisitLogService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 访问日志管理服务实现类
 * 提供访问日志的记录、统计分析等功能的具体实现
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbVisitLogServiceImpl extends ServiceImpl<TbVisitLogMapper, TbVisitLog>
        implements TbVisitLogService {

    @Autowired
    private TbVisitLogMapper visitLogMapper;

    // ==================== 访问日志记录 ====================

    /**
     * 记录访问日志
     * 
     * @param articleId 文章ID（可为空，表示非文章页面访问）
     * @param request   HTTP请求对象
     * @return 访问日志ID
     */
    @Override
    public Long recordVisit(Long articleId, HttpServletRequest request) {
        try {
            String visitorIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");
            String requestUrl = request.getRequestURL().toString();

            return recordVisit(articleId, visitorIp, userAgent, referer, requestUrl);
        } catch (Exception e) {
            log.error("记录访问日志失败", e);
            return null;
        }
    }

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
    @Override
    public Long recordVisit(Long articleId, String visitorIp, String userAgent, String referer, String requestUrl) {
        try {
            TbVisitLog visitLog = new TbVisitLog();
            visitLog.setArticleId(articleId);
            visitLog.setVisitorIp(visitorIp);
            visitLog.setUserAgent(userAgent);
            visitLog.setReferer(referer);
            visitLog.setRequestUrl(requestUrl);
            visitLog.setVisitTime(new Date());
            visitLog.setCreateTime(new Date());
            visitLog.setUpdateTime(new Date());
            visitLog.setIsDelete(0);

            // 解析用户代理信息
            parseUserAgent(visitLog, userAgent);

            // 识别爬虫
            visitLog.setIsSpider(identifySpider(userAgent) ? 1 : 0);

            // 保存访问日志
            if (save(visitLog)) {
                return visitLog.getId();
            }
            return null;
        } catch (Exception e) {
            log.error("记录访问日志失败: visitorIp={}, userAgent={}", visitorIp, userAgent, e);
            return null;
        }
    }

    /**
     * 更新访问停留时间
     * 
     * @param visitLogId 访问日志ID
     * @param stayTime   停留时间（秒）
     * @return 是否成功
     */
    @Override
    public Boolean updateStayTime(Long visitLogId, Integer stayTime) {
        try {
            TbVisitLog visitLog = new TbVisitLog();
            visitLog.setId(visitLogId);
            visitLog.setStayTime(stayTime);
            visitLog.setUpdateTime(new Date());

            return updateById(visitLog);
        } catch (Exception e) {
            log.error("更新停留时间失败: visitLogId={}, stayTime={}", visitLogId, stayTime, e);
            return false;
        }
    }

    // ==================== 访问日志查询 ====================

    /**
     * 分页查询访问日志
     * 
     * @param pageQueryVO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<VisitLogVO> getVisitLogList(PageQueryVO pageQueryVO) {
        try {
            Page<TbVisitLog> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getIsDelete, 0);

            // 添加搜索条件
            if (StringUtils.hasText(pageQueryVO.getKeyword())) {
                queryWrapper.and(wrapper -> wrapper
                        .like(TbVisitLog::getVisitorIp, pageQueryVO.getKeyword())
                        .or().like(TbVisitLog::getRequestUrl, pageQueryVO.getKeyword())
                        .or().like(TbVisitLog::getReferer, pageQueryVO.getKeyword()));
            }

            queryWrapper.orderByDesc(TbVisitLog::getCreateTime);

            IPage<TbVisitLog> pageResult = page(page, queryWrapper);
            List<VisitLogVO> voList = convertToVOList(pageResult.getRecords());

            return PageResult.of(voList, pageResult.getTotal(), pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        } catch (Exception e) {
            log.error("分页查询访问日志失败", e);
            return PageResult.empty();
        }
    }

    /**
     * 根据文章ID查询访问日志
     * 
     * @param articleId 文章ID
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 分页结果
     */
    @Override
    public PageResult<VisitLogVO> getVisitLogsByArticleId(Long articleId, Integer pageNum, Integer pageSize) {
        try {
            Page<TbVisitLog> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getArticleId, articleId)
                    .eq(TbVisitLog::getIsDelete, 0)
                    .orderByDesc(TbVisitLog::getCreateTime);

            IPage<TbVisitLog> pageResult = page(page, queryWrapper);
            List<VisitLogVO> voList = convertToVOList(pageResult.getRecords());

            return PageResult.of(voList, pageResult.getTotal(), pageNum, pageSize);
        } catch (Exception e) {
            log.error("根据文章ID查询访问日志失败: articleId={}", articleId, e);
            return PageResult.empty();
        }
    }

    // ==================== 私有工具方法 ====================

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }

    /**
     * 解析用户代理信息
     * 
     * @param visitLog  访问日志对象
     * @param userAgent 用户代理字符串
     */
    private void parseUserAgent(TbVisitLog visitLog, String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return;
        }

        // 简单的用户代理解析逻辑
        userAgent = userAgent.toLowerCase();

        // 检测是否为移动端
        visitLog.setIsMobile(isMobileDevice(userAgent) ? 1 : 0);

        // 解析浏览器信息
        String[] browserInfo = parseBrowser(userAgent);
        visitLog.setBrowser(browserInfo[0]);
        visitLog.setBrowserVersion(browserInfo[1]);

        // 解析操作系统
        visitLog.setOs(parseOperatingSystem(userAgent));

        // 解析设备类型
        visitLog.setDevice(parseDeviceType(userAgent));
    }

    /**
     * 检测是否为移动设备
     * 
     * @param userAgent 用户代理字符串
     * @return 是否为移动设备
     */
    private boolean isMobileDevice(String userAgent) {
        return userAgent.contains("mobile") || userAgent.contains("android") ||
                userAgent.contains("iphone") || userAgent.contains("ipad") ||
                userAgent.contains("blackberry") || userAgent.contains("windows phone");
    }

    /**
     * 解析浏览器信息
     * 
     * @param userAgent 用户代理字符串
     * @return [浏览器名称, 版本号]
     */
    private String[] parseBrowser(String userAgent) {
        if (userAgent.contains("chrome")) {
            return new String[] { "Chrome", extractVersion(userAgent, "chrome/") };
        } else if (userAgent.contains("firefox")) {
            return new String[] { "Firefox", extractVersion(userAgent, "firefox/") };
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return new String[] { "Safari", extractVersion(userAgent, "safari/") };
        } else if (userAgent.contains("edge")) {
            return new String[] { "Edge", extractVersion(userAgent, "edge/") };
        } else if (userAgent.contains("opera")) {
            return new String[] { "Opera", extractVersion(userAgent, "opera/") };
        }
        return new String[] { "Unknown", "Unknown" };
    }

    /**
     * 解析操作系统
     * 
     * @param userAgent 用户代理字符串
     * @return 操作系统名称
     */
    private String parseOperatingSystem(String userAgent) {
        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "MacOS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        }
        return "Unknown";
    }

    /**
     * 解析设备类型
     * 
     * @param userAgent 用户代理字符串
     * @return 设备类型
     */
    private String parseDeviceType(String userAgent) {
        if (userAgent.contains("mobile") || userAgent.contains("android")) {
            return "Mobile";
        } else if (userAgent.contains("ipad") || userAgent.contains("tablet")) {
            return "Tablet";
        }
        return "Desktop";
    }

    /**
     * 提取版本号
     * 
     * @param userAgent 用户代理字符串
     * @param prefix    版本前缀
     * @return 版本号
     */
    private String extractVersion(String userAgent, String prefix) {
        int startIndex = userAgent.indexOf(prefix);
        if (startIndex == -1) {
            return "Unknown";
        }

        startIndex += prefix.length();
        int endIndex = userAgent.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = userAgent.indexOf(";", startIndex);
        }
        if (endIndex == -1) {
            endIndex = userAgent.length();
        }

        return userAgent.substring(startIndex, endIndex);
    }

    /**
     * 将TbVisitLog转换为VisitLogVO
     * 
     * @param visitLog 访问日志实体
     * @return 访问日志VO
     */
    @Override
    public VisitLogVO convertToVO(TbVisitLog visitLog) {
        if (visitLog == null) {
            return null;
        }

        VisitLogVO vo = new VisitLogVO();
        BeanUtils.copyProperties(visitLog, vo);

        // 转换时间格式
        if (visitLog.getVisitTime() != null) {
            vo.setVisitTime(visitLog.getVisitTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (visitLog.getCreateTime() != null) {
            vo.setCreateTime(visitLog.getCreateTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (visitLog.getUpdateTime() != null) {
            vo.setUpdateTime(visitLog.getUpdateTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // 设置其他字段
        vo.setVisitorIp(visitLog.getVisitorIp());
        vo.setRequestUrl(visitLog.getRequestUrl());
        vo.setBrowserName(visitLog.getBrowser());
        vo.setBrowserVersion(visitLog.getBrowserVersion());
        vo.setOperatingSystem(visitLog.getOs());
        vo.setDeviceType(visitLog.getDevice());
        vo.setRefererUrl(visitLog.getReferer());
        vo.setStayTime(visitLog.getStayTime());
        vo.setIsMobile(visitLog.getIsMobile() == 1);

        return vo;
    }

    /**
     * 将TbVisitLog列表转换为VisitLogVO列表
     * 
     * @param visitLogs 访问日志实体列表
     * @return 访问日志VO列表
     */
    @Override
    public List<VisitLogVO> convertToVOList(List<TbVisitLog> visitLogs) {
        if (visitLogs == null || visitLogs.isEmpty()) {
            return new ArrayList<>();
        }

        return visitLogs.stream()
                .map(this::convertToVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 识别并标记爬虫访问
     * 
     * @param userAgent 用户代理
     * @return 是否为爬虫
     */
    @Override
    public Boolean identifySpider(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return false;
        }

        String lowerAgent = userAgent.toLowerCase();
        String[] spiderKeywords = {
                "bot", "spider", "crawler", "scraper", "search", "index",
                "googlebot", "bingbot", "baiduspider", "yandexbot", "slurp",
                "facebookexternalhit", "twitterbot", "linkedinbot", "whatsapp",
                "telegrambot", "applebot", "duckduckbot", "sogou", "360spider"
        };

        for (String keyword : spiderKeywords) {
            if (lowerAgent.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    // ==================== 临时实现的其他必需方法 ====================
    // 以下方法将在后续继续完善实现

    @Override
    public PageResult<VisitLogVO> getVisitLogsByIp(String visitorIp, Integer pageNum, Integer pageSize) {
        try {
            Page<TbVisitLog> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getVisitorIp, visitorIp)
                    .eq(TbVisitLog::getIsDelete, 0)
                    .orderByDesc(TbVisitLog::getCreateTime);

            IPage<TbVisitLog> pageResult = page(page, queryWrapper);
            List<VisitLogVO> voList = convertToVOList(pageResult.getRecords());

            return PageResult.of(voList, pageResult.getTotal(), pageNum, pageSize);
        } catch (Exception e) {
            log.error("根据IP查询访问日志失败: visitorIp={}", visitorIp, e);
            return PageResult.empty();
        }
    }

    @Override
    public PageResult<VisitLogVO> getVisitLogsByTimeRange(Date startTime, Date endTime, Integer pageNum,
            Integer pageSize) {
        try {
            Page<TbVisitLog> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getIsDelete, 0);

            if (startTime != null) {
                queryWrapper.ge(TbVisitLog::getVisitTime, startTime);
            }
            if (endTime != null) {
                queryWrapper.le(TbVisitLog::getVisitTime, endTime);
            }

            queryWrapper.orderByDesc(TbVisitLog::getCreateTime);

            IPage<TbVisitLog> pageResult = page(page, queryWrapper);
            List<VisitLogVO> voList = convertToVOList(pageResult.getRecords());

            return PageResult.of(voList, pageResult.getTotal(), pageNum, pageSize);
        } catch (Exception e) {
            log.error("根据时间范围查询访问日志失败: startTime={}, endTime={}", startTime, endTime, e);
            return PageResult.empty();
        }
    }

    @Override
    public Map<String, Object> getVisitStatisticsOverview() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 总访问量
            Long totalVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0));
            statistics.put("totalVisits", totalVisits);

            // 今日访问量
            LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            Date startOfTodayDate = Date.from(startOfToday.atZone(ZoneId.systemDefault()).toInstant());
            Long todayVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0)
                    .ge(TbVisitLog::getVisitTime, startOfTodayDate));
            statistics.put("todayVisits", todayVisits);

            // 独立访客数
            QueryWrapper<TbVisitLog> uniqueVisitorQuery = new QueryWrapper<>();
            uniqueVisitorQuery.select("COUNT(DISTINCT visitor_ip) as uniqueVisitors")
                    .eq("is_delete", 0);
            Map<String, Object> uniqueResult = getMap(uniqueVisitorQuery);
            Long uniqueVisitors = uniqueResult != null ? Long.valueOf(uniqueResult.get("uniqueVisitors").toString())
                    : 0L;
            statistics.put("uniqueVisitors", uniqueVisitors);

            // 爬虫访问量
            Long spiderVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0)
                    .eq(TbVisitLog::getIsSpider, 1));
            statistics.put("spiderVisits", spiderVisits);

            return statistics;
        } catch (Exception e) {
            log.error("获取访问统计概览失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getTodayVisitStatistics() {
        try {
            LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            Date startOfTodayDate = Date.from(startOfToday.atZone(ZoneId.systemDefault()).toInstant());
            Date endOfTodayDate = Date.from(endOfToday.atZone(ZoneId.systemDefault()).toInstant());

            return getVisitStatisticsByTimeRange(startOfTodayDate, endOfTodayDate);
        } catch (Exception e) {
            log.error("获取今日访问统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getVisitStatisticsByDate(Date date) {
        try {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);
            Date startOfDayDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            Date endOfDayDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

            return getVisitStatisticsByTimeRange(startOfDayDate, endOfDayDate);
        } catch (Exception e) {
            log.error("获取指定日期访问统计失败: date={}", date, e);
            return new HashMap<>();
        }
    }

    /**
     * 根据时间范围获取访问统计
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    private Map<String, Object> getVisitStatisticsByTimeRange(Date startTime, Date endTime) {
        Map<String, Object> statistics = new HashMap<>();

        // 总访问量
        LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbVisitLog::getIsDelete, 0)
                .ge(TbVisitLog::getVisitTime, startTime)
                .le(TbVisitLog::getVisitTime, endTime);
        Long totalVisits = count(queryWrapper);
        statistics.put("totalVisits", totalVisits);

        // 独立访客数
        QueryWrapper<TbVisitLog> uniqueQuery = new QueryWrapper<>();
        uniqueQuery.select("COUNT(DISTINCT visitor_ip) as uniqueVisitors")
                .eq("is_delete", 0)
                .ge("visit_time", startTime)
                .le("visit_time", endTime);
        Map<String, Object> uniqueResult = getMap(uniqueQuery);
        Long uniqueVisitors = uniqueResult != null ? Long.valueOf(uniqueResult.get("uniqueVisitors").toString()) : 0L;
        statistics.put("uniqueVisitors", uniqueVisitors);

        // 移动端访问量
        Long mobileVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                .eq(TbVisitLog::getIsDelete, 0)
                .eq(TbVisitLog::getIsMobile, 1)
                .ge(TbVisitLog::getVisitTime, startTime)
                .le(TbVisitLog::getVisitTime, endTime));
        statistics.put("mobileVisits", mobileVisits);

        // 爬虫访问量
        Long spiderVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                .eq(TbVisitLog::getIsDelete, 0)
                .eq(TbVisitLog::getIsSpider, 1)
                .ge(TbVisitLog::getVisitTime, startTime)
                .le(TbVisitLog::getVisitTime, endTime));
        statistics.put("spiderVisits", spiderVisits);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getVisitTrend(Integer days) {
        try {
            List<Map<String, Object>> trendList = new ArrayList<>();

            // 获取最近N天的数据
            for (int i = days - 1; i >= 0; i--) {
                LocalDate targetDate = LocalDate.now().minusDays(i);
                LocalDateTime startOfDay = LocalDateTime.of(targetDate, LocalTime.MIN);
                LocalDateTime endOfDay = LocalDateTime.of(targetDate, LocalTime.MAX);
                Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

                // 统计当天访问量
                Long visits = count(new LambdaQueryWrapper<TbVisitLog>()
                        .eq(TbVisitLog::getIsDelete, 0)
                        .ge(TbVisitLog::getVisitTime, startDate)
                        .le(TbVisitLog::getVisitTime, endDate));

                // 统计独立访客数
                QueryWrapper<TbVisitLog> uniqueQuery = new QueryWrapper<>();
                uniqueQuery.select("COUNT(DISTINCT visitor_ip) as uniqueVisitors")
                        .eq("is_delete", 0)
                        .ge("visit_time", startDate)
                        .le("visit_time", endDate);
                Map<String, Object> uniqueResult = getMap(uniqueQuery);
                Long uniqueVisitors = uniqueResult != null ? Long.valueOf(uniqueResult.get("uniqueVisitors").toString())
                        : 0L;

                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", targetDate.toString());
                dayData.put("visits", visits);
                dayData.put("uniqueVisitors", uniqueVisitors);

                trendList.add(dayData);
            }

            return trendList;
        } catch (Exception e) {
            log.error("获取访问趋势失败: days={}", days, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getHotArticlesStatistics(Integer limit) {
        try {
            QueryWrapper<TbVisitLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("article_id, COUNT(*) as visit_count")
                    .eq("is_delete", 0)
                    .isNotNull("article_id")
                    .groupBy("article_id")
                    .orderByDesc("visit_count")
                    .last("LIMIT " + limit);

            List<Map<String, Object>> resultList = listMaps(queryWrapper);

            // 转换结果格式
            return resultList.stream().map(map -> {
                Map<String, Object> result = new HashMap<>();
                result.put("articleId", map.get("article_id"));
                result.put("visitCount", map.get("visit_count"));
                return result;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取热门文章统计失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getRefererStatistics(Integer limit) {
        try {
            QueryWrapper<TbVisitLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("referer, COUNT(*) as count")
                    .eq("is_delete", 0)
                    .isNotNull("referer")
                    .ne("referer", "")
                    .groupBy("referer")
                    .orderByDesc("count")
                    .last("LIMIT " + limit);

            return listMaps(queryWrapper);
        } catch (Exception e) {
            log.error("获取来源页面统计失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getBrowserStatistics(Integer limit) {
        try {
            QueryWrapper<TbVisitLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("browser, COUNT(*) as count")
                    .eq("is_delete", 0)
                    .isNotNull("browser")
                    .ne("browser", "")
                    .ne("browser", "Unknown")
                    .groupBy("browser")
                    .orderByDesc("count")
                    .last("LIMIT " + limit);

            return listMaps(queryWrapper);
        } catch (Exception e) {
            log.error("获取浏览器统计失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getOsStatistics(Integer limit) {
        try {
            QueryWrapper<TbVisitLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("os, COUNT(*) as count")
                    .eq("is_delete", 0)
                    .isNotNull("os")
                    .ne("os", "")
                    .ne("os", "Unknown")
                    .groupBy("os")
                    .orderByDesc("count")
                    .last("LIMIT " + limit);

            return listMaps(queryWrapper);
        } catch (Exception e) {
            log.error("获取操作系统统计失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getDeviceStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 总访问量
            Long totalVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0));

            // 移动端访问量
            Long mobileVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0)
                    .eq(TbVisitLog::getIsMobile, 1));

            // 桌面端访问量
            Long desktopVisits = totalVisits - mobileVisits;

            // 各设备类型统计
            QueryWrapper<TbVisitLog> deviceQuery = new QueryWrapper<>();
            deviceQuery.select("device, COUNT(*) as count")
                    .eq("is_delete", 0)
                    .isNotNull("device")
                    .ne("device", "")
                    .groupBy("device")
                    .orderByDesc("count");

            List<Map<String, Object>> deviceList = listMaps(deviceQuery);

            statistics.put("totalVisits", totalVisits);
            statistics.put("mobileVisits", mobileVisits);
            statistics.put("desktopVisits", desktopVisits);
            statistics.put("mobileRate",
                    totalVisits > 0 ? String.format("%.2f", (double) mobileVisits / totalVisits * 100) + "%" : "0%");
            statistics.put("deviceDetails", deviceList);

            return statistics;
        } catch (Exception e) {
            log.error("获取设备统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getLocationStatistics(Integer limit) {
        try {
            QueryWrapper<TbVisitLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("visitor_location, COUNT(*) as count")
                    .eq("is_delete", 0)
                    .isNotNull("visitor_location")
                    .ne("visitor_location", "")
                    .groupBy("visitor_location")
                    .orderByDesc("count")
                    .last("LIMIT " + limit);

            return listMaps(queryWrapper);
        } catch (Exception e) {
            log.error("获取地理位置统计失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getHourlyStatistics(Date date) {
        try {
            List<Map<String, Object>> hourlyStats = new ArrayList<>();

            // 如果没有指定日期，默认使用今天
            LocalDate targetDate = date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.now();

            // 统计24小时数据
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime startOfHour = LocalDateTime.of(targetDate, LocalTime.of(hour, 0));
                LocalDateTime endOfHour = LocalDateTime.of(targetDate, LocalTime.of(hour, 59, 59));
                Date startDate = Date.from(startOfHour.atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(endOfHour.atZone(ZoneId.systemDefault()).toInstant());

                Long visits = count(new LambdaQueryWrapper<TbVisitLog>()
                        .eq(TbVisitLog::getIsDelete, 0)
                        .ge(TbVisitLog::getVisitTime, startDate)
                        .le(TbVisitLog::getVisitTime, endDate));

                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", hour);
                hourData.put("visits", visits);

                hourlyStats.add(hourData);
            }

            return hourlyStats;
        } catch (Exception e) {
            log.error("获取每小时统计失败: date={}", date, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getSpiderStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 总爬虫访问量
            Long totalSpiderVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0)
                    .eq(TbVisitLog::getIsSpider, 1));

            // 爬虫名称统计
            QueryWrapper<TbVisitLog> spiderQuery = new QueryWrapper<>();
            spiderQuery.select("spider_name, COUNT(*) as count")
                    .eq("is_delete", 0)
                    .eq("is_spider", 1)
                    .isNotNull("spider_name")
                    .ne("spider_name", "")
                    .groupBy("spider_name")
                    .orderByDesc("count")
                    .last("LIMIT 10");

            List<Map<String, Object>> spiderDetails = listMaps(spiderQuery);

            statistics.put("totalSpiderVisits", totalSpiderVisits);
            statistics.put("spiderDetails", spiderDetails);

            return statistics;
        } catch (Exception e) {
            log.error("获取爬虫统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public List<VisitLogVO> getUserVisitPath(String visitorIp, Integer limit) {
        try {
            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getVisitorIp, visitorIp)
                    .eq(TbVisitLog::getIsDelete, 0)
                    .orderByAsc(TbVisitLog::getVisitTime)
                    .last("LIMIT " + limit);

            List<TbVisitLog> visitLogs = list(queryWrapper);
            return convertToVOList(visitLogs);
        } catch (Exception e) {
            log.error("获取用户访问路径失败: visitorIp={}", visitorIp, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getStayTimeStatistics(Long articleId) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getIsDelete, 0)
                    .isNotNull(TbVisitLog::getStayTime)
                    .gt(TbVisitLog::getStayTime, 0);

            if (articleId != null) {
                queryWrapper.eq(TbVisitLog::getArticleId, articleId);
            }

            List<TbVisitLog> logs = list(queryWrapper);

            if (logs.isEmpty()) {
                statistics.put("averageStayTime", 0);
                statistics.put("totalRecords", 0);
                statistics.put("maxStayTime", 0);
                statistics.put("minStayTime", 0);
                return statistics;
            }

            // 计算平均停留时间
            double averageStayTime = logs.stream()
                    .mapToInt(TbVisitLog::getStayTime)
                    .average()
                    .orElse(0.0);

            // 计算最大和最小停留时间
            int maxStayTime = logs.stream()
                    .mapToInt(TbVisitLog::getStayTime)
                    .max()
                    .orElse(0);

            int minStayTime = logs.stream()
                    .mapToInt(TbVisitLog::getStayTime)
                    .min()
                    .orElse(0);

            statistics.put("averageStayTime", Math.round(averageStayTime));
            statistics.put("totalRecords", logs.size());
            statistics.put("maxStayTime", maxStayTime);
            statistics.put("minStayTime", minStayTime);

            return statistics;
        } catch (Exception e) {
            log.error("获取停留时间统计失败: articleId={}", articleId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getUserLoyaltyAnalysis() {
        try {
            Map<String, Object> analysis = new HashMap<>();

            // 统计回访用户
            QueryWrapper<TbVisitLog> repeatQuery = new QueryWrapper<>();
            repeatQuery.select("visitor_ip, COUNT(*) as visit_count")
                    .eq("is_delete", 0)
                    .groupBy("visitor_ip")
                    .having("COUNT(*) > 1");

            List<Map<String, Object>> repeatVisitors = listMaps(repeatQuery);

            // 统计新用户
            QueryWrapper<TbVisitLog> newQuery = new QueryWrapper<>();
            newQuery.select("visitor_ip, COUNT(*) as visit_count")
                    .eq("is_delete", 0)
                    .groupBy("visitor_ip")
                    .having("COUNT(*) = 1");

            List<Map<String, Object>> newVisitors = listMaps(newQuery);

            analysis.put("repeatVisitorCount", repeatVisitors.size());
            analysis.put("newVisitorCount", newVisitors.size());
            analysis.put("totalUniqueVisitors", repeatVisitors.size() + newVisitors.size());

            if (!repeatVisitors.isEmpty()) {
                double avgVisitsPerRepeatUser = repeatVisitors.stream()
                        .mapToLong(map -> Long.parseLong(map.get("visit_count").toString()))
                        .average()
                        .orElse(0.0);
                analysis.put("averageVisitsPerRepeatUser", Math.round(avgVisitsPerRepeatUser * 100.0) / 100.0);
            }

            return analysis;
        } catch (Exception e) {
            log.error("获取用户忠诚度分析失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getVisitDepthAnalysis() {
        try {
            Map<String, Object> analysis = new HashMap<>();

            // 按IP统计访问深度（每个IP的访问页面数）
            QueryWrapper<TbVisitLog> depthQuery = new QueryWrapper<>();
            depthQuery.select("visitor_ip, COUNT(DISTINCT request_url) as page_count")
                    .eq("is_delete", 0)
                    .groupBy("visitor_ip");

            List<Map<String, Object>> depthData = listMaps(depthQuery);

            if (depthData.isEmpty()) {
                analysis.put("averageDepth", 0);
                analysis.put("maxDepth", 0);
                analysis.put("singlePageVisitors", 0);
                analysis.put("deepVisitors", 0);
                return analysis;
            }

            // 计算平均访问深度
            double averageDepth = depthData.stream()
                    .mapToLong(map -> Long.parseLong(map.get("page_count").toString()))
                    .average()
                    .orElse(0.0);

            // 计算最大访问深度
            long maxDepth = depthData.stream()
                    .mapToLong(map -> Long.parseLong(map.get("page_count").toString()))
                    .max()
                    .orElse(0);

            // 统计单页面访问者数量
            long singlePageVisitors = depthData.stream()
                    .mapToLong(map -> Long.parseLong(map.get("page_count").toString()))
                    .filter(count -> count == 1)
                    .count();

            // 统计深度访问者（访问5页以上）
            long deepVisitors = depthData.stream()
                    .mapToLong(map -> Long.parseLong(map.get("page_count").toString()))
                    .filter(count -> count >= 5)
                    .count();

            analysis.put("averageDepth", Math.round(averageDepth * 100.0) / 100.0);
            analysis.put("maxDepth", maxDepth);
            analysis.put("singlePageVisitors", singlePageVisitors);
            analysis.put("deepVisitors", deepVisitors);
            analysis.put("totalVisitors", depthData.size());

            return analysis;
        } catch (Exception e) {
            log.error("获取访问深度分析失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public Integer cleanupExpiredLogs(Integer days) {
        try {
            LocalDateTime expiredTime = LocalDateTime.now().minusDays(days);
            Date expiredDate = Date.from(expiredTime.atZone(ZoneId.systemDefault()).toInstant());

            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.lt(TbVisitLog::getCreateTime, expiredDate);

            return Math.toIntExact(count(queryWrapper));
        } catch (Exception e) {
            log.error("清理过期访问日志失败: days={}", days, e);
            return 0;
        }
    }

    @Override
    public Integer cleanupSpiderLogs(Integer days) {
        try {
            LocalDateTime expiredTime = LocalDateTime.now().minusDays(days);
            Date expiredDate = Date.from(expiredTime.atZone(ZoneId.systemDefault()).toInstant());

            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getIsSpider, 1)
                    .lt(TbVisitLog::getCreateTime, expiredDate);

            return Math.toIntExact(count(queryWrapper));
        } catch (Exception e) {
            log.error("清理爬虫访问日志失败: days={}", days, e);
            return 0;
        }
    }

    @Override
    public Integer archiveHistoryLogs(Integer months) {
        try {
            LocalDateTime archiveTime = LocalDateTime.now().minusMonths(months);
            Date archiveDate = Date.from(archiveTime.atZone(ZoneId.systemDefault()).toInstant());

            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.lt(TbVisitLog::getCreateTime, archiveDate);

            // 这里只是统计需要归档的记录数，实际归档逻辑需要根据具体需求实现
            return Math.toIntExact(count(queryWrapper));
        } catch (Exception e) {
            log.error("归档历史访问日志失败: months={}", months, e);
            return 0;
        }
    }

    @Override
    public Integer reParseUserAgent() {
        try {
            // 获取所有需要重新解析的记录
            LambdaQueryWrapper<TbVisitLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TbVisitLog::getIsDelete, 0)
                    .isNotNull(TbVisitLog::getUserAgent)
                    .ne(TbVisitLog::getUserAgent, "");

            List<TbVisitLog> logs = list(queryWrapper);
            int processedCount = 0;

            for (TbVisitLog visitLog : logs) {
                try {
                    // 重新解析用户代理信息
                    parseUserAgent(visitLog, visitLog.getUserAgent());
                    visitLog.setUpdateTime(new Date());

                    if (updateById(visitLog)) {
                        processedCount++;
                    }
                } catch (Exception e) {
                    log.error("重新解析用户代理失败: id={}", visitLog.getId(), e);
                }
            }

            return processedCount;
        } catch (Exception e) {
            log.error("重新解析用户代理信息失败", e);
            return 0;
        }
    }

    @Override
    public Integer updateLocationInfo() {
        // TODO: 需要集成IP地址库API来获取地理位置信息
        try {
            log.info("更新地理位置信息功能需要集成第三方IP地址库API");
            return 0;
        } catch (Exception e) {
            log.error("更新地理位置信息失败", e);
            return 0;
        }
    }

    @Override
    public Integer getRealTimeOnlineUsers() {
        try {
            // 统计最近5分钟内有访问记录的独立IP数
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
            Date fiveMinutesAgoDate = Date.from(fiveMinutesAgo.atZone(ZoneId.systemDefault()).toInstant());

            QueryWrapper<TbVisitLog> onlineQuery = new QueryWrapper<>();
            onlineQuery.select("COUNT(DISTINCT visitor_ip) as online_users")
                    .eq("is_delete", 0)
                    .ge("visit_time", fiveMinutesAgoDate);

            Map<String, Object> result = getMap(onlineQuery);
            return result != null ? Integer.valueOf(result.get("online_users").toString()) : 0;
        } catch (Exception e) {
            log.error("获取实时在线用户数失败", e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getRealTimeStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 最近1小时访问量
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            Date oneHourAgoDate = Date.from(oneHourAgo.atZone(ZoneId.systemDefault()).toInstant());

            Long lastHourVisits = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getIsDelete, 0)
                    .ge(TbVisitLog::getVisitTime, oneHourAgoDate));

            // 实时在线用户数
            Integer onlineUsers = getRealTimeOnlineUsers();

            // 今日访问量
            Map<String, Object> todayStats = getTodayVisitStatistics();

            statistics.put("lastHourVisits", lastHourVisits);
            statistics.put("onlineUsers", onlineUsers);
            statistics.put("todayStats", todayStats);
            statistics.put("updateTime", new Date());

            return statistics;
        } catch (Exception e) {
            log.error("获取实时统计数据失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public Boolean refreshRealTimeCache() {
        try {
            // TODO: 如果使用Redis缓存，在这里刷新缓存
            log.info("实时统计缓存刷新功能需要集成Redis缓存");
            return true;
        } catch (Exception e) {
            log.error("刷新实时统计缓存失败", e);
            return false;
        }
    }

    @Override
    public Boolean checkFrequentVisit(String visitorIp, Integer timeRange, Integer maxCount) {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusMinutes(timeRange);
            Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());

            Long visitCount = count(new LambdaQueryWrapper<TbVisitLog>()
                    .eq(TbVisitLog::getVisitorIp, visitorIp)
                    .eq(TbVisitLog::getIsDelete, 0)
                    .ge(TbVisitLog::getVisitTime, startDate));

            return visitCount > maxCount;
        } catch (Exception e) {
            log.error("检查频繁访问失败: visitorIp={}, timeRange={}, maxCount={}",
                    visitorIp, timeRange, maxCount, e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getSuspiciousIpList(Integer limit) {
        try {
            // 统计最近24小时内访问频率异常的IP
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            Date yesterdayDate = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());

            QueryWrapper<TbVisitLog> suspiciousQuery = new QueryWrapper<>();
            suspiciousQuery.select("visitor_ip, COUNT(*) as visit_count")
                    .eq("is_delete", 0)
                    .ge("visit_time", yesterdayDate)
                    .groupBy("visitor_ip")
                    .having("COUNT(*) > 100") // 24小时内访问超过100次认为可疑
                    .orderByDesc("visit_count")
                    .last("LIMIT " + limit);

            return listMaps(suspiciousQuery);
        } catch (Exception e) {
            log.error("获取可疑IP列表失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }
}
