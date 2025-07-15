package pw.pj.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.ResultEnum;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有异常情况，返回标准格式的错误响应
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param e       业务异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 - URL: {}, 错误码: {}, 消息: {}", request.getRequestURI(), e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常（@RequestBody 验证失败）
     * 
     * @param e       方法参数验证异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        log.warn("参数验证异常 - URL: {}", request.getRequestURI());

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ApiResponse.validateError(errors);
    }

    /**
     * 处理参数绑定异常（@ModelAttribute 验证失败）
     * 
     * @param e       参数绑定异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("参数绑定异常 - URL: {}", request.getRequestURI());

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ApiResponse.validateError(errors);
    }

    /**
     * 处理约束违反异常（@Validated 验证失败）
     * 
     * @param e       约束违反异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException e,
            HttpServletRequest request) {
        log.warn("约束违反异常 - URL: {}", request.getRequestURI());

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return ApiResponse.validateError(errors);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e       缺少请求参数异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
            HttpServletRequest request) {
        log.warn("缺少请求参数异常 - URL: {}, 参数名: {}", request.getRequestURI(), e.getParameterName());
        String message = String.format("缺少必需的请求参数: %s", e.getParameterName());
        return ApiResponse.error(ResultEnum.VALIDATE_ERROR.getCode(), message);
    }

    /**
     * 处理参数类型不匹配异常
     * 
     * @param e       参数类型不匹配异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        log.warn("参数类型不匹配异常 - URL: {}, 参数名: {}", request.getRequestURI(), e.getName());
        String message = String.format("参数 %s 类型不正确", e.getName());
        return ApiResponse.error(ResultEnum.VALIDATE_ERROR.getCode(), message);
    }

    /**
     * 处理HTTP消息不可读异常（JSON格式错误等）
     * 
     * @param e       HTTP消息不可读异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
            HttpServletRequest request) {
        log.warn("HTTP消息不可读异常 - URL: {}", request.getRequestURI());
        return ApiResponse.error(ResultEnum.VALIDATE_ERROR.getCode(), "请求数据格式错误");
    }

    /**
     * 处理请求方法不支持异常
     * 
     * @param e       请求方法不支持异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {
        log.warn("请求方法不支持异常 - URL: {}, 方法: {}", request.getRequestURI(), request.getMethod());
        String message = String.format("不支持的请求方法: %s", request.getMethod());
        return ApiResponse.error(ResultEnum.METHOD_NOT_ALLOWED.getCode(), message);
    }

    /**
     * 处理媒体类型不支持异常
     * 
     * @param e       媒体类型不支持异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e,
            HttpServletRequest request) {
        log.warn("媒体类型不支持异常 - URL: {}, Content-Type: {}", request.getRequestURI(), request.getContentType());
        return ApiResponse.error(415, "不支持的媒体类型");
    }

    /**
     * 处理文件上传大小超出限制异常
     * 
     * @param e       文件上传大小超出限制异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiResponse<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e,
            HttpServletRequest request) {
        log.warn("文件上传大小超出限制异常 - URL: {}", request.getRequestURI());
        return ApiResponse.error(ResultEnum.FILE_SIZE_EXCEEDED);
    }

    /**
     * 处理404异常
     * 
     * @param e       404异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404异常 - URL: {}", request.getRequestURI());
        return ApiResponse.error(ResultEnum.NOT_FOUND);
    }

    /**
     * 处理数据库异常
     * 
     * @param e       数据库异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("数据库异常 - URL: {}, 错误: {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResponse.error(ResultEnum.DATABASE_OPERATION_ERROR);
    }

    /**
     * 处理空指针异常
     * 
     * @param e       空指针异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常 - URL: {}, 错误: {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResponse.error(ResultEnum.ERROR.getCode(), "系统内部错误，请联系管理员");
    }

    /**
     * 处理非法参数异常
     * 
     * @param e       非法参数异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常 - URL: {}, 错误: {}", request.getRequestURI(), e.getMessage());
        return ApiResponse.error(ResultEnum.VALIDATE_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常
     * 
     * @param e       运行时异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常 - URL: {}, 错误: {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResponse.error(ResultEnum.ERROR);
    }

    /**
     * 处理所有其他异常
     * 
     * @param e       异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常 - URL: {}, 错误: {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResponse.error(ResultEnum.ERROR);
    }
}