package pw.pj.common.constants;

/**
 * Redis缓存键名管理常量类
 * 统一管理项目中所有Redis缓存的键名前缀和模板
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public class RedisConstants {

    /**
     * 系统基础前缀
     */
    public static final String SYSTEM_PREFIX = "person_blog:";

    /**
     * 用户相关缓存键
     */
    public static final class User {
        /** 用户信息缓存前缀 */
        public static final String USER_INFO = SYSTEM_PREFIX + "user:info:";

        /** 用户权限缓存前缀 */
        public static final String USER_PERMISSION = SYSTEM_PREFIX + "user:permission:";

        /** 用户角色缓存前缀 */
        public static final String USER_ROLE = SYSTEM_PREFIX + "user:role:";

        /** 用户登录状态缓存前缀 */
        public static final String USER_LOGIN_STATUS = SYSTEM_PREFIX + "user:login:";

        /** 用户登录失败次数缓存前缀 */
        public static final String USER_LOGIN_FAIL_COUNT = SYSTEM_PREFIX + "user:login:fail:";

        /** 用户在线状态缓存前缀 */
        public static final String USER_ONLINE = SYSTEM_PREFIX + "user:online:";

        /** 用户会话信息缓存前缀 */
        public static final String USER_SESSION = SYSTEM_PREFIX + "user:session:";

        /** 用户最后活动时间缓存前缀 */
        public static final String USER_LAST_ACTIVE = SYSTEM_PREFIX + "user:last_active:";

        /** 用户关注列表缓存前缀 */
        public static final String USER_FOLLOW_LIST = SYSTEM_PREFIX + "user:follow:";

        /** 用户粉丝列表缓存前缀 */
        public static final String USER_FANS_LIST = SYSTEM_PREFIX + "user:fans:";
    }

    /**
     * 认证相关缓存键
     */
    public static final class Auth {
        /** JWT令牌缓存前缀 */
        public static final String JWT_TOKEN = SYSTEM_PREFIX + "auth:jwt:";

        /** 刷新令牌缓存前缀 */
        public static final String REFRESH_TOKEN = SYSTEM_PREFIX + "auth:refresh:";

        /** 验证码缓存前缀 */
        public static final String CAPTCHA = SYSTEM_PREFIX + "auth:captcha:";

        /** 短信验证码缓存前缀 */
        public static final String SMS_CODE = SYSTEM_PREFIX + "auth:sms:";

        /** 邮箱验证码缓存前缀 */
        public static final String EMAIL_CODE = SYSTEM_PREFIX + "auth:email:";

        /** 密码重置令牌缓存前缀 */
        public static final String PASSWORD_RESET_TOKEN = SYSTEM_PREFIX + "auth:pwd_reset:";

        /** 账号激活令牌缓存前缀 */
        public static final String ACCOUNT_ACTIVATE_TOKEN = SYSTEM_PREFIX + "auth:activate:";

        /** 登录重试限制缓存前缀 */
        public static final String LOGIN_RETRY_LIMIT = SYSTEM_PREFIX + "auth:retry:";

        /** IP地址登录限制缓存前缀 */
        public static final String IP_LOGIN_LIMIT = SYSTEM_PREFIX + "auth:ip_limit:";

        /** 令牌黑名单缓存前缀 */
        public static final String TOKEN_BLACKLIST = SYSTEM_PREFIX + "auth:blacklist:";
    }

    /**
     * 文章相关缓存键
     */
    public static final class Article {
        /** 文章详情缓存前缀 */
        public static final String ARTICLE_DETAIL = SYSTEM_PREFIX + "article:detail:";

        /** 文章列表缓存前缀 */
        public static final String ARTICLE_LIST = SYSTEM_PREFIX + "article:list:";

        /** 文章浏览量缓存前缀 */
        public static final String ARTICLE_VIEW_COUNT = SYSTEM_PREFIX + "article:view:";

        /** 文章点赞数缓存前缀 */
        public static final String ARTICLE_LIKE_COUNT = SYSTEM_PREFIX + "article:like:";

        /** 文章评论数缓存前缀 */
        public static final String ARTICLE_COMMENT_COUNT = SYSTEM_PREFIX + "article:comment:";

        /** 热门文章缓存键 */
        public static final String HOT_ARTICLES = SYSTEM_PREFIX + "article:hot";

        /** 最新文章缓存键 */
        public static final String RECENT_ARTICLES = SYSTEM_PREFIX + "article:recent";

        /** 推荐文章缓存键 */
        public static final String RECOMMEND_ARTICLES = SYSTEM_PREFIX + "article:recommend";

        /** 置顶文章缓存键 */
        public static final String TOP_ARTICLES = SYSTEM_PREFIX + "article:top";

        /** 文章搜索结果缓存前缀 */
        public static final String ARTICLE_SEARCH = SYSTEM_PREFIX + "article:search:";

        /** 相关文章缓存前缀 */
        public static final String RELATED_ARTICLES = SYSTEM_PREFIX + "article:related:";

        /** 文章归档缓存键 */
        public static final String ARTICLE_ARCHIVE = SYSTEM_PREFIX + "article:archive";

        /** 用户文章缓存前缀 */
        public static final String USER_ARTICLES = SYSTEM_PREFIX + "article:user:";
    }

    /**
     * 分类相关缓存键
     */
    public static final class Category {
        /** 分类详情缓存前缀 */
        public static final String CATEGORY_DETAIL = SYSTEM_PREFIX + "category:detail:";

        /** 分类列表缓存键 */
        public static final String CATEGORY_LIST = SYSTEM_PREFIX + "category:list";

        /** 分类文章数量缓存前缀 */
        public static final String CATEGORY_ARTICLE_COUNT = SYSTEM_PREFIX + "category:count:";

        /** 热门分类缓存键 */
        public static final String HOT_CATEGORIES = SYSTEM_PREFIX + "category:hot";

        /** 分类树形结构缓存键 */
        public static final String CATEGORY_TREE = SYSTEM_PREFIX + "category:tree";

        /** 分类路径映射缓存键 */
        public static final String CATEGORY_PATH_MAP = SYSTEM_PREFIX + "category:path_map";
    }

    /**
     * 标签相关缓存键
     */
    public static final class Tag {
        /** 标签详情缓存前缀 */
        public static final String TAG_DETAIL = SYSTEM_PREFIX + "tag:detail:";

        /** 标签列表缓存键 */
        public static final String TAG_LIST = SYSTEM_PREFIX + "tag:list";

        /** 标签文章数量缓存前缀 */
        public static final String TAG_ARTICLE_COUNT = SYSTEM_PREFIX + "tag:count:";

        /** 热门标签缓存键 */
        public static final String HOT_TAGS = SYSTEM_PREFIX + "tag:hot";

        /** 标签云数据缓存键 */
        public static final String TAG_CLOUD = SYSTEM_PREFIX + "tag:cloud";

        /** 文章标签缓存前缀 */
        public static final String ARTICLE_TAGS = SYSTEM_PREFIX + "tag:article:";

        /** 用户常用标签缓存前缀 */
        public static final String USER_FREQUENT_TAGS = SYSTEM_PREFIX + "tag:user_frequent:";
    }

    /**
     * 评论相关缓存键
     */
    public static final class Comment {
        /** 评论详情缓存前缀 */
        public static final String COMMENT_DETAIL = SYSTEM_PREFIX + "comment:detail:";

        /** 文章评论列表缓存前缀 */
        public static final String ARTICLE_COMMENTS = SYSTEM_PREFIX + "comment:article:";

        /** 评论回复列表缓存前缀 */
        public static final String COMMENT_REPLIES = SYSTEM_PREFIX + "comment:replies:";

        /** 评论点赞数缓存前缀 */
        public static final String COMMENT_LIKE_COUNT = SYSTEM_PREFIX + "comment:like:";

        /** 最新评论缓存键 */
        public static final String RECENT_COMMENTS = SYSTEM_PREFIX + "comment:recent";

        /** 用户评论缓存前缀 */
        public static final String USER_COMMENTS = SYSTEM_PREFIX + "comment:user:";

        /** 待审核评论缓存键 */
        public static final String PENDING_COMMENTS = SYSTEM_PREFIX + "comment:pending";

        /** 评论统计缓存键 */
        public static final String COMMENT_STATISTICS = SYSTEM_PREFIX + "comment:statistics";

        /** IP评论次数限制缓存前缀 */
        public static final String IP_COMMENT_COUNT = SYSTEM_PREFIX + "comment:ip_count:";
    }

    /**
     * 文件相关缓存键
     */
    public static final class File {
        /** 文件信息缓存前缀 */
        public static final String FILE_INFO = SYSTEM_PREFIX + "file:info:";

        /** 文件上传记录缓存前缀 */
        public static final String FILE_UPLOAD_RECORD = SYSTEM_PREFIX + "file:upload:";

        /** 文件下载次数缓存前缀 */
        public static final String FILE_DOWNLOAD_COUNT = SYSTEM_PREFIX + "file:download:";

        /** 用户文件列表缓存前缀 */
        public static final String USER_FILES = SYSTEM_PREFIX + "file:user:";

        /** 文件类型统计缓存键 */
        public static final String FILE_TYPE_STATISTICS = SYSTEM_PREFIX + "file:type_stats";

        /** 热门文件缓存键 */
        public static final String HOT_FILES = SYSTEM_PREFIX + "file:hot";

        /** 文件存储统计缓存键 */
        public static final String FILE_STORAGE_STATISTICS = SYSTEM_PREFIX + "file:storage_stats";
    }

    /**
     * 系统配置相关缓存键
     */
    public static final class Config {
        /** 系统配置缓存前缀 */
        public static final String SYSTEM_CONFIG = SYSTEM_PREFIX + "config:system:";

        /** 业务配置缓存前缀 */
        public static final String BUSINESS_CONFIG = SYSTEM_PREFIX + "config:business:";

        /** 安全配置缓存前缀 */
        public static final String SECURITY_CONFIG = SYSTEM_PREFIX + "config:security:";

        /** 所有配置缓存键 */
        public static final String ALL_CONFIGS = SYSTEM_PREFIX + "config:all";

        /** 配置变更通知缓存键 */
        public static final String CONFIG_CHANGE_NOTIFY = SYSTEM_PREFIX + "config:change_notify";

        /** 网站设置缓存键 */
        public static final String SITE_SETTINGS = SYSTEM_PREFIX + "config:site_settings";
    }

    /**
     * 统计分析相关缓存键
     */
    public static final class Statistics {
        /** 网站访问统计缓存前缀 */
        public static final String SITE_VISIT_STATS = SYSTEM_PREFIX + "stats:visit:";

        /** 用户活跃统计缓存前缀 */
        public static final String USER_ACTIVE_STATS = SYSTEM_PREFIX + "stats:user_active:";

        /** 文章统计缓存键 */
        public static final String ARTICLE_STATISTICS = SYSTEM_PREFIX + "stats:article";

        /** 用户统计缓存键 */
        public static final String USER_STATISTICS = SYSTEM_PREFIX + "stats:user";

        /** 评论统计缓存键 */
        public static final String COMMENT_STATISTICS_CACHE = SYSTEM_PREFIX + "stats:comment";

        /** 日访问量统计缓存前缀 */
        public static final String DAILY_VISIT = SYSTEM_PREFIX + "stats:daily_visit:";

        /** 月访问量统计缓存前缀 */
        public static final String MONTHLY_VISIT = SYSTEM_PREFIX + "stats:monthly_visit:";

        /** 年访问量统计缓存前缀 */
        public static final String YEARLY_VISIT = SYSTEM_PREFIX + "stats:yearly_visit:";

        /** 实时在线用户数缓存键 */
        public static final String REAL_TIME_ONLINE = SYSTEM_PREFIX + "stats:real_time_online";

        /** 搜索关键词统计缓存键 */
        public static final String SEARCH_KEYWORDS = SYSTEM_PREFIX + "stats:search_keywords";

        /** 热门搜索词缓存键 */
        public static final String HOT_SEARCH_KEYWORDS = SYSTEM_PREFIX + "stats:hot_search";

        /** 来源统计缓存键 */
        public static final String REFERRER_STATISTICS = SYSTEM_PREFIX + "stats:referrer";

        /** 浏览器统计缓存键 */
        public static final String BROWSER_STATISTICS = SYSTEM_PREFIX + "stats:browser";

        /** 操作系统统计缓存键 */
        public static final String OS_STATISTICS = SYSTEM_PREFIX + "stats:os";

        /** 地域统计缓存键 */
        public static final String LOCATION_STATISTICS = SYSTEM_PREFIX + "stats:location";
    }

    /**
     * 锁相关缓存键
     */
    public static final class Lock {
        /** 分布式锁前缀 */
        public static final String DISTRIBUTED_LOCK = SYSTEM_PREFIX + "lock:";

        /** 用户操作锁前缀 */
        public static final String USER_OPERATION_LOCK = SYSTEM_PREFIX + "lock:user:";

        /** 文章操作锁前缀 */
        public static final String ARTICLE_OPERATION_LOCK = SYSTEM_PREFIX + "lock:article:";

        /** 评论操作锁前缀 */
        public static final String COMMENT_OPERATION_LOCK = SYSTEM_PREFIX + "lock:comment:";

        /** 文件上传锁前缀 */
        public static final String FILE_UPLOAD_LOCK = SYSTEM_PREFIX + "lock:file_upload:";

        /** 邮件发送锁前缀 */
        public static final String EMAIL_SEND_LOCK = SYSTEM_PREFIX + "lock:email:";

        /** 统计更新锁前缀 */
        public static final String STATISTICS_UPDATE_LOCK = SYSTEM_PREFIX + "lock:stats_update:";
    }

    /**
     * 限流相关缓存键
     */
    public static final class RateLimit {
        /** API访问限流前缀 */
        public static final String API_RATE_LIMIT = SYSTEM_PREFIX + "rate_limit:api:";

        /** 用户操作限流前缀 */
        public static final String USER_OPERATION_LIMIT = SYSTEM_PREFIX + "rate_limit:user:";

        /** IP访问限流前缀 */
        public static final String IP_RATE_LIMIT = SYSTEM_PREFIX + "rate_limit:ip:";

        /** 邮件发送限流前缀 */
        public static final String EMAIL_SEND_LIMIT = SYSTEM_PREFIX + "rate_limit:email:";

        /** 短信发送限流前缀 */
        public static final String SMS_SEND_LIMIT = SYSTEM_PREFIX + "rate_limit:sms:";

        /** 文件上传限流前缀 */
        public static final String FILE_UPLOAD_LIMIT = SYSTEM_PREFIX + "rate_limit:upload:";

        /** 评论发布限流前缀 */
        public static final String COMMENT_POST_LIMIT = SYSTEM_PREFIX + "rate_limit:comment:";

        /** 搜索请求限流前缀 */
        public static final String SEARCH_RATE_LIMIT = SYSTEM_PREFIX + "rate_limit:search:";
    }

    /**
     * 缓存工具方法
     */
    public static final class Utils {
        /** 分隔符 */
        public static final String SEPARATOR = ":";

        /** 通配符 */
        public static final String WILDCARD = "*";

        /**
         * 构建缓存键
         * 
         * @param prefix 前缀
         * @param suffix 后缀
         * @return 完整的缓存键
         */
        public static String buildKey(String prefix, String suffix) {
            return prefix + suffix;
        }

        /**
         * 构建缓存键
         * 
         * @param prefix 前缀
         * @param middle 中间部分
         * @param suffix 后缀
         * @return 完整的缓存键
         */
        public static String buildKey(String prefix, String middle, String suffix) {
            return prefix + middle + SEPARATOR + suffix;
        }

        /**
         * 构建带通配符的缓存键模式
         * 
         * @param prefix 前缀
         * @return 带通配符的键模式
         */
        public static String buildPattern(String prefix) {
            return prefix + WILDCARD;
        }
    }
}