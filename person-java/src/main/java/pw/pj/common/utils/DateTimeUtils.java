package pw.pj.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具类
 * 提供日期时间的各种常用操作方法
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
public class DateTimeUtils {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 默认时间戳格式
     */
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 紧凑日期时间格式
     */
    public static final String COMPACT_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 紧凑日期格式
     */
    public static final String COMPACT_DATE_FORMAT = "yyyyMMdd";

    /**
     * ISO日期时间格式
     */
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 中文日期时间格式
     */
    public static final String CHINESE_DATE_TIME_FORMAT = "yyyy年MM月dd日 HH:mm:ss";

    /**
     * 中文日期格式
     */
    public static final String CHINESE_DATE_FORMAT = "yyyy年MM月dd日";

    /**
     * 默认时区
     */
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    /**
     * 获取当前时间
     * 
     * @return 当前LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     * 
     * @return 当前LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     * 
     * @return 当前LocalTime
     */
    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    /**
     * 获取当前时间戳
     * 
     * @return 当前时间戳（秒）
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前毫秒时间戳
     * 
     * @return 当前毫秒时间戳
     */
    public static long currentMillisTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 格式化日期时间
     * 
     * @param dateTime 日期时间
     * @param pattern  格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("日期格式化失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化日期
     * 
     * @param date    日期
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        try {
            return date.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("日期格式化失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化时间
     * 
     * @param time    时间
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        try {
            return time.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("时间格式化失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 使用默认格式格式化日期时间
     * 
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 使用默认格式格式化日期
     * 
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 解析日期时间字符串
     * 
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式模式
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (StringUtils.isBlank(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("日期时间解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @param pattern 格式模式
     * @return LocalDate对象
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("日期解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析时间字符串
     * 
     * @param timeStr 时间字符串
     * @param pattern 格式模式
     * @return LocalTime对象
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        try {
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            log.error("时间解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 使用默认格式解析日期时间
     * 
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 使用默认格式解析日期
     * 
     * @param dateStr 日期字符串
     * @return LocalDate对象
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_FORMAT);
    }

    /**
     * Date转LocalDateTime
     * 
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     * 
     * @param dateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDate转Date
     * 
     * @param date LocalDate对象
     * @return Date对象
     */
    public static Date toDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 时间戳转LocalDateTime
     * 
     * @param timestamp 时间戳（秒）
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * 毫秒时间戳转LocalDateTime
     * 
     * @param timestamp 毫秒时间戳
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTimeFromMillis(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime转时间戳
     * 
     * @param dateTime LocalDateTime对象
     * @return 时间戳（秒）
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(DEFAULT_ZONE_ID).toEpochSecond();
    }

    /**
     * LocalDateTime转毫秒时间戳
     * 
     * @param dateTime LocalDateTime对象
     * @return 毫秒时间戳
     */
    public static long toMillisTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 获取日期的开始时间（00:00:00）
     * 
     * @param date 日期
     * @return 开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * 获取日期的结束时间（23:59:59）
     * 
     * @param date 日期
     * @return 结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59, 999999999);
    }

    /**
     * 获取月份的第一天
     * 
     * @param date 日期
     * @return 月份第一天
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取月份的最后一天
     * 
     * @param date 日期
     * @return 月份最后一天
     */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取年份的第一天
     * 
     * @param date 日期
     * @return 年份第一天
     */
    public static LocalDate firstDayOfYear(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取年份的最后一天
     * 
     * @param date 日期
     * @return 年份最后一天
     */
    public static LocalDate lastDayOfYear(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 日期时间加减操作
     * 
     * @param dateTime 日期时间
     * @param amount   数量
     * @param unit     单位
     * @return 计算后的日期时间
     */
    public static LocalDateTime plus(LocalDateTime dateTime, long amount, ChronoUnit unit) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plus(amount, unit);
    }

    /**
     * 日期时间减法操作
     * 
     * @param dateTime 日期时间
     * @param amount   数量
     * @param unit     单位
     * @return 计算后的日期时间
     */
    public static LocalDateTime minus(LocalDateTime dateTime, long amount, ChronoUnit unit) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.minus(amount, unit);
    }

    /**
     * 计算两个日期时间之间的差值
     * 
     * @param start 开始时间
     * @param end   结束时间
     * @param unit  单位
     * @return 差值
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        if (start == null || end == null) {
            return 0;
        }
        return unit.between(start, end);
    }

    /**
     * 判断日期是否为今天
     * 
     * @param date 日期
     * @return 是否为今天
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now());
    }

    /**
     * 判断日期是否为昨天
     * 
     * @param date 日期
     * @return 是否为昨天
     */
    public static boolean isYesterday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now().minusDays(1));
    }

    /**
     * 判断日期是否为明天
     * 
     * @param date 日期
     * @return 是否为明天
     */
    public static boolean isTomorrow(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now().plusDays(1));
    }

    /**
     * 判断是否为同一天
     * 
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }

    /**
     * 判断是否为周末
     * 
     * @param date 日期
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 获取年龄
     * 
     * @param birthDate 出生日期
     * @return 年龄
     */
    public static int getAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 格式化友好的时间显示
     * 
     * @param dateTime 日期时间
     * @return 友好的时间字符串
     */
    public static String formatFriendly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 60 * 24) {
            return (minutes / 60) + "小时前";
        } else if (minutes < 60 * 24 * 30) {
            return (minutes / (60 * 24)) + "天前";
        } else if (minutes < 60 * 24 * 30 * 12) {
            return (minutes / (60 * 24 * 30)) + "个月前";
        } else {
            return (minutes / (60 * 24 * 30 * 12)) + "年前";
        }
    }
}