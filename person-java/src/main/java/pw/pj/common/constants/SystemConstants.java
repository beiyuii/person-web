package pw.pj.common.constants;

/**
 * 系统常量类
 * 定义项目中使用的各种业务常量
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
public class SystemConstants {

    /**
     * 系统基础常量
     */
    public static final class System {
        /** 系统名称 */
        public static final String SYSTEM_NAME = "PersonWeb博客系统";

        /** 系统版本 */
        public static final String SYSTEM_VERSION = "1.0.0";

        /** 系统编码 */
        public static final String DEFAULT_CHARSET = "UTF-8";

        /** 默认时区 */
        public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";

        /** 系统管理员角色 */
        public static final String ADMIN_ROLE = "ADMIN";

        /** 普通用户角色 */
        public static final String USER_ROLE = "USER";

        /** 访客角色 */
        public static final String GUEST_ROLE = "GUEST";
    }

    /**
     * 用户相关常量
     */
    public static final class User {
        /** 默认头像 */
        public static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";

        /** 用户名最小长度 */
        public static final int USERNAME_MIN_LENGTH = 3;

        /** 用户名最大长度 */
        public static final int USERNAME_MAX_LENGTH = 20;

        /** 密码最小长度 */
        public static final int PASSWORD_MIN_LENGTH = 6;

        /** 密码最大长度 */
        public static final int PASSWORD_MAX_LENGTH = 32;

        /** 用户状态：正常 */
        public static final Integer STATUS_NORMAL = 1;

        /** 用户状态：禁用 */
        public static final Integer STATUS_DISABLED = 0;

        /** 用户状态：删除 */
        public static final Integer STATUS_DELETED = -1;

        /** 默认用户昵称前缀 */
        public static final String DEFAULT_NICKNAME_PREFIX = "用户";
    }

    /**
     * 文章相关常量
     */
    public static final class Article {
        /** 文章状态：草稿 */
        public static final Integer STATUS_DRAFT = 0;

        /** 文章状态：已发布 */
        public static final Integer STATUS_PUBLISHED = 1;

        /** 文章状态：已删除 */
        public static final Integer STATUS_DELETED = -1;

        /** 文章类型：原创 */
        public static final Integer TYPE_ORIGINAL = 0;

        /** 文章类型：转载 */
        public static final Integer TYPE_REPRINT = 1;

        /** 文章类型：翻译 */
        public static final Integer TYPE_TRANSLATION = 2;

        /** 置顶：否 */
        public static final Integer TOP_NO = 0;

        /** 置顶：是 */
        public static final Integer TOP_YES = 1;

        /** 推荐：否 */
        public static final Integer RECOMMEND_NO = 0;

        /** 推荐：是 */
        public static final Integer RECOMMEND_YES = 1;

        /** 允许评论：否 */
        public static final Integer COMMENT_NO = 0;

        /** 允许评论：是 */
        public static final Integer COMMENT_YES = 1;

        /** 默认封面图片 */
        public static final String DEFAULT_COVER = "/static/images/default-cover.jpg";

        /** 文章摘要最大长度 */
        public static final int SUMMARY_MAX_LENGTH = 500;

        /** 标题最大长度 */
        public static final int TITLE_MAX_LENGTH = 100;
    }

    /**
     * 分类相关常量
     */
    public static final class Category {
        /** 分类状态：启用 */
        public static final Integer STATUS_ENABLED = 1;

        /** 分类状态：禁用 */
        public static final Integer STATUS_DISABLED = 0;

        /** 默认分类名称 */
        public static final String DEFAULT_CATEGORY = "默认分类";

        /** 分类名称最大长度 */
        public static final int NAME_MAX_LENGTH = 50;

        /** 分类描述最大长度 */
        public static final int DESCRIPTION_MAX_LENGTH = 200;
    }

    /**
     * 标签相关常量
     */
    public static final class Tag {
        /** 标签状态：启用 */
        public static final Integer STATUS_ENABLED = 1;

        /** 标签状态：禁用 */
        public static final Integer STATUS_DISABLED = 0;

        /** 标签名称最大长度 */
        public static final int NAME_MAX_LENGTH = 30;

        /** 单篇文章最大标签数 */
        public static final int MAX_TAGS_PER_ARTICLE = 10;

        /** 热门标签显示数量 */
        public static final int HOT_TAG_LIMIT = 20;
    }

    /**
     * 评论相关常量
     */
    public static final class Comment {
        /** 评论状态：待审核 */
        public static final Integer STATUS_PENDING = 0;

        /** 评论状态：已通过 */
        public static final Integer STATUS_APPROVED = 1;

        /** 评论状态：已拒绝 */
        public static final Integer STATUS_REJECTED = -1;

        /** 评论状态：垃圾评论 */
        public static final Integer STATUS_SPAM = -2;

        /** 评论类型：文章评论 */
        public static final Integer TYPE_ARTICLE = 1;

        /** 评论类型：页面评论 */
        public static final Integer TYPE_PAGE = 2;

        /** 评论内容最大长度 */
        public static final int CONTENT_MAX_LENGTH = 1000;

        /** 评论最大层级深度 */
        public static final int MAX_REPLY_DEPTH = 5;

        /** 游客评论默认昵称 */
        public static final String GUEST_NICKNAME = "匿名用户";
    }

    /**
     * 文件相关常量
     */
    public static final class File {
        /** 文件状态：正常 */
        public static final Integer STATUS_NORMAL = 1;

        /** 文件状态：删除 */
        public static final Integer STATUS_DELETED = 0;

        /** 文件类型：图片 */
        public static final Integer TYPE_IMAGE = 1;

        /** 文件类型：文档 */
        public static final Integer TYPE_DOCUMENT = 2;

        /** 文件类型：视频 */
        public static final Integer TYPE_VIDEO = 3;

        /** 文件类型：音频 */
        public static final Integer TYPE_AUDIO = 4;

        /** 文件类型：其他 */
        public static final Integer TYPE_OTHER = 99;

        /** 默认文件上传路径 */
        public static final String DEFAULT_UPLOAD_PATH = "/uploads/";

        /** 图片上传路径 */
        public static final String IMAGE_UPLOAD_PATH = "/uploads/images/";

        /** 文档上传路径 */
        public static final String DOCUMENT_UPLOAD_PATH = "/uploads/documents/";

        /** 单个文件最大大小（10MB） */
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

        /** 图片文件最大大小（5MB） */
        public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

        /** 允许上传的图片格式 */
        public static final String[] ALLOWED_IMAGE_FORMATS = { "jpg", "jpeg", "png", "gif", "bmp", "webp" };

        /** 允许上传的文档格式 */
        public static final String[] ALLOWED_DOCUMENT_FORMATS = { "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
                "txt" };
    }

    /**
     * 系统配置相关常量
     */
    public static final class Config {
        /** 配置状态：启用 */
        public static final Integer STATUS_ENABLED = 1;

        /** 配置状态：禁用 */
        public static final Integer STATUS_DISABLED = 0;

        /** 配置类型：系统配置 */
        public static final String TYPE_SYSTEM = "system";

        /** 配置类型：业务配置 */
        public static final String TYPE_BUSINESS = "business";

        /** 配置类型：安全配置 */
        public static final String TYPE_SECURITY = "security";

        /** 网站标题配置键 */
        public static final String SITE_TITLE = "site.title";

        /** 网站描述配置键 */
        public static final String SITE_DESCRIPTION = "site.description";

        /** 网站关键词配置键 */
        public static final String SITE_KEYWORDS = "site.keywords";

        /** 备案号配置键 */
        public static final String SITE_BEIAN = "site.beian";

        /** 评论审核开关 */
        public static final String COMMENT_AUDIT = "comment.audit";

        /** 注册开关 */
        public static final String REGISTER_ENABLED = "register.enabled";
    }

    /**
     * 分页相关常量
     */
    public static final class Page {
        /** 默认页码 */
        public static final int DEFAULT_PAGE_NUM = 1;

        /** 默认每页大小 */
        public static final int DEFAULT_PAGE_SIZE = 10;

        /** 最大每页大小 */
        public static final int MAX_PAGE_SIZE = 100;

        /** 首页文章显示数量 */
        public static final int HOME_ARTICLE_SIZE = 10;

        /** 热门文章显示数量 */
        public static final int HOT_ARTICLE_SIZE = 5;

        /** 最新文章显示数量 */
        public static final int RECENT_ARTICLE_SIZE = 10;

        /** 推荐文章显示数量 */
        public static final int RECOMMEND_ARTICLE_SIZE = 6;
    }

    /**
     * 缓存相关常量
     */
    public static final class Cache {
        /** 缓存过期时间：1小时 */
        public static final int EXPIRE_HOUR = 60 * 60;

        /** 缓存过期时间：1天 */
        public static final int EXPIRE_DAY = 24 * 60 * 60;

        /** 缓存过期时间：1周 */
        public static final int EXPIRE_WEEK = 7 * 24 * 60 * 60;

        /** 缓存过期时间：1个月 */
        public static final int EXPIRE_MONTH = 30 * 24 * 60 * 60;

        /** 验证码缓存过期时间（5分钟） */
        public static final int CAPTCHA_EXPIRE = 5 * 60;

        /** 登录令牌缓存过期时间（1天） */
        public static final int TOKEN_EXPIRE = EXPIRE_DAY;

        /** 热门数据缓存过期时间（1小时） */
        public static final int HOT_DATA_EXPIRE = EXPIRE_HOUR;
    }

    /**
     * 正则表达式常量
     */
    public static final class Regex {
        /** 手机号正则 */
        public static final String MOBILE = "^1[3-9]\\d{9}$";

        /** 邮箱正则 */
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        /** 用户名正则（字母、数字、下划线） */
        public static final String USERNAME = "^[a-zA-Z0-9_]{3,20}$";

        /** 密码正则（至少包含字母和数字） */
        public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,32}$";

        /** IP地址正则 */
        public static final String IP = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        /** URL正则 */
        public static final String URL = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";

        /** 中文正则 */
        public static final String CHINESE = "[\u4e00-\u9fa5]";

        /** 标签名正则（中文、字母、数字、短横线） */
        public static final String TAG_NAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-]{1,30}$";
    }

    /**
     * 消息模板常量
     */
    public static final class Message {
        /** 操作成功 */
        public static final String SUCCESS = "操作成功";

        /** 操作失败 */
        public static final String FAIL = "操作失败";

        /** 数据不存在 */
        public static final String DATA_NOT_FOUND = "数据不存在";

        /** 参数错误 */
        public static final String PARAM_ERROR = "参数错误";

        /** 权限不足 */
        public static final String NO_PERMISSION = "权限不足";

        /** 用户未登录 */
        public static final String NOT_LOGIN = "用户未登录";

        /** 账号或密码错误 */
        public static final String LOGIN_ERROR = "账号或密码错误";

        /** 验证码错误 */
        public static final String CAPTCHA_ERROR = "验证码错误";

        /** 用户名已存在 */
        public static final String USERNAME_EXISTS = "用户名已存在";

        /** 邮箱已存在 */
        public static final String EMAIL_EXISTS = "邮箱已存在";

        /** 密码强度不够 */
        public static final String PASSWORD_WEAK = "密码强度不够，至少包含字母和数字";

        /** 文件上传失败 */
        public static final String FILE_UPLOAD_FAIL = "文件上传失败";

        /** 文件格式不支持 */
        public static final String FILE_FORMAT_NOT_SUPPORT = "文件格式不支持";

        /** 文件大小超限 */
        public static final String FILE_SIZE_EXCEED = "文件大小超出限制";
    }
}