package pw.pj.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一API响应格式类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private String timestamp;

    /**
     * 错误详情（仅在出错时返回）
     */
    private Object errors;

    /**
     * 私有构造方法，用于构建响应对象
     * 
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> successWithMessage(String message) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功响应（自定义消息和数据）
     * 
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应（使用错误枚举）
     * 
     * @param resultEnum 错误枚举
     * @param <T>        数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(ResultEnum resultEnum) {
        return new ApiResponse<>(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    /**
     * 失败响应（自定义错误消息）
     * 
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ResultEnum.ERROR.getCode(), message, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     * 
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 失败响应（带错误详情）
     * 
     * @param resultEnum 错误枚举
     * @param errors     错误详情
     * @param <T>        数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(ResultEnum resultEnum, Object errors) {
        ApiResponse<T> response = new ApiResponse<>(resultEnum.getCode(), resultEnum.getMessage(), null);
        response.setErrors(errors);
        return response;
    }

    /**
     * 参数验证失败响应
     * 
     * @param errors 验证错误详情
     * @param <T>    数据类型
     * @return 参数验证失败响应对象
     */
    public static <T> ApiResponse<T> validateError(Object errors) {
        ApiResponse<T> response = new ApiResponse<>(ResultEnum.VALIDATE_ERROR.getCode(),
                ResultEnum.VALIDATE_ERROR.getMessage(), null);
        response.setErrors(errors);
        return response;
    }

    /**
     * 判断响应是否成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return ResultEnum.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 获取响应数据，如果失败则返回null
     * 
     * @return 响应数据
     */
    public T getData() {
        return isSuccess() ? this.data : null;
    }
}