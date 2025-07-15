package pw.pj.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统状态码枚举
 * 统一管理系统中的所有状态码和错误信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    // ========== 通用状态码 ==========
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 系统内部错误
     */
    ERROR(500, "系统内部错误"),

    /**
     * 参数验证失败
     */
    VALIDATE_ERROR(400, "参数验证失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权访问"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不被允许
     */
    METHOD_NOT_ALLOWED(405, "请求方法不被允许"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 资源冲突
     */
    CONFLICT(409, "资源冲突"),

    /**
     * 请求实体过大
     */
    PAYLOAD_TOO_LARGE(413, "请求实体过大"),

    /**
     * 请求格式正确，但语义错误
     */
    UNPROCESSABLE_ENTITY(422, "请求格式正确，但语义错误"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // ========== 业务状态码 1000+ ==========

    // --- 用户相关 1000-1099 ---
    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(1002, "验证码错误"),

    /**
     * Token已过期
     */
    TOKEN_EXPIRED(1003, "Token已过期"),

    /**
     * Token无效
     */
    TOKEN_INVALID(1004, "Token无效"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1005, "用户名已存在"),

    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS(1006, "邮箱已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1007, "用户不存在"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1008, "用户已被禁用"),

    /**
     * 密码格式不正确
     */
    PASSWORD_FORMAT_ERROR(1009, "密码格式不正确"),

    /**
     * 用户注册失败
     */
    USER_REGISTER_FAIL(1010, "用户注册失败"),

    /**
     * 用户登录失败
     */
    USER_LOGIN_FAIL(1011, "用户登录失败"),

    /**
     * 用户密码错误
     */
    USER_PASSWORD_ERROR(1012, "密码错误"),

    /**
     * 用户登录受限
     */
    USER_LOGIN_RESTRICTED(1013, "登录受限，请稍后再试"),

    /**
     * 用户已存在
     */
    USER_EXISTS(1014, "用户已存在"),

    /**
     * 参数错误
     */
    PARAM_ERROR(1015, "参数错误"),

    /**
     * 保存失败
     */
    SAVE_FAIL(1016, "保存失败"),

    /**
     * 更新失败
     */
    UPDATE_FAIL(1017, "更新失败"),

    /**
     * 操作被禁止
     */
    OPERATION_FORBIDDEN(1018, "操作被禁止"),

    // --- 文章相关 2000-2099 ---
    /**
     * 文章不存在
     */
    ARTICLE_NOT_FOUND(2001, "文章不存在"),

    /**
     * 文章已删除
     */
    ARTICLE_DELETED(2002, "文章已删除"),

    /**
     * 文章密码错误
     */
    ARTICLE_PASSWORD_ERROR(2003, "文章密码错误"),

    /**
     * 文章标题已存在
     */
    ARTICLE_TITLE_EXISTS(2004, "文章标题已存在"),

    /**
     * 文章内容为空
     */
    ARTICLE_CONTENT_EMPTY(2005, "文章内容不能为空"),

    // --- 分类相关 3000-3099 ---
    /**
     * 分类不存在
     */
    CATEGORY_NOT_FOUND(3001, "分类不存在"),

    /**
     * 分类下还有文章，无法删除
     */
    CATEGORY_HAS_ARTICLES(3002, "分类下还有文章，无法删除"),

    /**
     * 分类名称已存在
     */
    CATEGORY_NAME_EXISTS(3003, "分类名称已存在"),

    // --- 标签相关 3100-3199 ---
    /**
     * 标签不存在
     */
    TAG_NOT_FOUND(3101, "标签不存在"),

    /**
     * 标签名称已存在
     */
    TAG_NAME_EXISTS(3102, "标签名称已存在"),

    // --- 评论相关 4000-4099 ---
    /**
     * 评论不存在
     */
    COMMENT_NOT_FOUND(4001, "评论不存在"),

    /**
     * 评论已关闭
     */
    COMMENT_CLOSED(4002, "评论已关闭"),

    /**
     * 评论内容为空
     */
    COMMENT_CONTENT_EMPTY(4003, "评论内容不能为空"),

    /**
     * 评论过于频繁
     */
    COMMENT_TOO_FREQUENT(4004, "评论过于频繁，请稍后再试"),

    // --- 文件相关 5000-5099 ---
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(5001, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(5002, "文件类型不支持"),

    /**
     * 文件大小超出限制
     */
    FILE_SIZE_EXCEEDED(5003, "文件大小超出限制"),

    /**
     * 文件不存在
     */
    FILE_NOT_FOUND(5004, "文件不存在"),

    /**
     * 文件读取失败
     */
    FILE_READ_ERROR(5005, "文件读取失败"),

    // --- 系统配置相关 6000-6099 ---
    /**
     * 配置项不存在
     */
    CONFIG_NOT_FOUND(6001, "配置项不存在"),

    /**
     * 配置值格式错误
     */
    CONFIG_VALUE_FORMAT_ERROR(6002, "配置值格式错误"),

    /**
     * 配置键已存在
     */
    CONFIG_KEY_EXISTS(6003, "配置键已存在"),

    // --- 缓存相关 7000-7099 ---
    /**
     * 缓存操作失败
     */
    CACHE_ERROR(7001, "缓存操作失败"),

    /**
     * 缓存已过期
     */
    CACHE_EXPIRED(7002, "缓存已过期"),

    // --- 数据库相关 8000-8099 ---
    /**
     * 数据库连接失败
     */
    DATABASE_CONNECTION_ERROR(8001, "数据库连接失败"),

    /**
     * 数据库操作失败
     */
    DATABASE_OPERATION_ERROR(8002, "数据库操作失败"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(8003, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_EXISTS(8004, "数据已存在"),

    // --- 第三方服务相关 9000-9099 ---
    /**
     * 第三方服务调用失败
     */
    THIRD_PARTY_SERVICE_ERROR(9001, "第三方服务调用失败"),

    /**
     * 短信发送失败
     */
    SMS_SEND_ERROR(9002, "短信发送失败"),

    /**
     * 邮件发送失败
     */
    EMAIL_SEND_ERROR(9003, "邮件发送失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    /**
     * 根据状态码获取枚举
     * 
     * @param code 状态码
     * @return 对应的枚举，如果不存在则返回null
     */
    public static ResultEnum getByCode(Integer code) {
        for (ResultEnum resultEnum : values()) {
            if (resultEnum.getCode().equals(code)) {
                return resultEnum;
            }
        }
        return null;
    }

    /**
     * 判断状态码是否表示成功
     * 
     * @param code 状态码
     * @return 是否成功
     */
    public static boolean isSuccess(Integer code) {
        return SUCCESS.getCode().equals(code);
    }
}