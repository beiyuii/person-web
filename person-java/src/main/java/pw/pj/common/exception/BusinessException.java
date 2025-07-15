package pw.pj.common.exception;

import lombok.Getter;
import pw.pj.common.result.ResultEnum;

/**
 * 业务异常类
 * 用于处理业务逻辑相关的异常情况
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造方法 - 使用错误码和消息
     * 
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法 - 使用结果枚举
     * 
     * @param resultEnum 结果枚举
     */
    public BusinessException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    /**
     * 构造方法 - 使用结果枚举和自定义消息
     * 
     * @param resultEnum 结果枚举
     * @param message    自定义错误消息
     */
    public BusinessException(ResultEnum resultEnum, String message) {
        super(message);
        this.code = resultEnum.getCode();
        this.message = message;
    }

    /**
     * 构造方法 - 使用消息（默认错误码500）
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultEnum.ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法 - 使用消息和原因（默认错误码500）
     * 
     * @param message 错误消息
     * @param cause   异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultEnum.ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法 - 使用错误码、消息和原因
     * 
     * @param code    错误码
     * @param message 错误消息
     * @param cause   异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法 - 使用结果枚举和原因
     * 
     * @param resultEnum 结果枚举
     * @param cause      异常原因
     */
    public BusinessException(ResultEnum resultEnum, Throwable cause) {
        super(resultEnum.getMessage(), cause);
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    /**
     * 静态方法 - 快速创建业务异常
     * 
     * @param resultEnum 结果枚举
     * @return 业务异常
     */
    public static BusinessException of(ResultEnum resultEnum) {
        return new BusinessException(resultEnum);
    }

    /**
     * 静态方法 - 快速创建业务异常（自定义消息）
     * 
     * @param resultEnum 结果枚举
     * @param message    自定义错误消息
     * @return 业务异常
     */
    public static BusinessException of(ResultEnum resultEnum, String message) {
        return new BusinessException(resultEnum, message);
    }

    /**
     * 静态方法 - 快速创建业务异常（错误码和消息）
     * 
     * @param code    错误码
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException of(Integer code, String message) {
        return new BusinessException(code, message);
    }

    /**
     * 静态方法 - 快速创建业务异常（仅消息）
     * 
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    @Override
    public String toString() {
        return String.format("BusinessException{code=%d, message='%s'}", code, message);
    }
}