package com.kg.core.exception.handler;

import com.google.common.primitives.Ints;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.web.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理
 * <pre>
 *     400：客户端错误（如参数数量、参数格式，等错误）
 *     405：请求类型异常（如：POST请求却使用GET，等错误）
 *     500：服务器端错误
 * </pre>
 *
 * @author ziro
 * @date 2022/5/2 10:22
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 服务端异常：默认全局异常处理
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleBaseException(BaseException ex) {
        String error = ex.getMessage();
        Integer code = Ints.tryParse(error);
        if (code != null) {
            BaseErrorCode errorCode = BaseErrorCode.getByCode(code);
            if (errorCode != null) {
                // 固定错误码异常
                log.error("错误代码:" + errorCode.getCode() + ";错误信息：" + errorCode.getInfo(), ex);
                return ResponseResult.builder()
                        .code(errorCode.getCode().toString())
                        .message(errorCode.getInfo())
                        .build();
            }
        }
        // 未定义异常，统一返回500异常
        log.error(error, ex);
        return ResponseResult.error(error);
    }


    /**
     * 服务端异常：用户名或密码错误 40001
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.builder()
                .code(BaseErrorCode.LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG.getCode().toString())
                .message(BaseErrorCode.LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG.getInfo())
                .data(ex.getMessage())
                .build();
    }

    /**
     * 服务端异常：文件大小超限
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error("上传文件大小超出限制！");
    }

    /**
     * 服务端异常：未做主动处理的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error(ex.getMessage());
    }

    /**
     * 服务端异常：运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleRuntimeException(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error(ex.getMessage());
    }

    /**
     * 服务端异常：IOException 文件读写异常
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleIOException(IOException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error(ex.getMessage());
    }

    /**
     * 服务端异常：IllegalArgumentException.java 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error(ex.getMessage());
    }


    /**
     * 客户端异常：请求类型错误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.builder()
                .code("405")// HttpStatus.METHOD_NOT_ALLOWED
                .message("客户端错误：请求类型错误！")
                .data("Method Not Allowed!" + ex.getMessage())
                .build();
    }

    /**
     * 客户端异常：BindException（例如：端口被占用）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleBindException(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return ResponseResult.builder()
                .code("400")// HttpStatus.BAD_REQUEST
                .message("客户端错误：" + fieldErrors == null ? "参数错误" : fieldErrors.get(0).getDefaultMessage())
                .data(String.format("错误数量: %", ex.getBindingResult().getErrorCount()))
                .build();
    }

    /**
     * 客户端异常：方法参数无效
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return ResponseResult.builder()
                .code("400")// HttpStatus.BAD_REQUEST
                .message("客户端错误：" + fieldErrors == null ? "参数错误" : fieldErrors.get(0).getDefaultMessage())
                .data(String.format("错误数量: %", ex.getBindingResult().getErrorCount()))
                .build();
    }

    /**
     * 客户端异常：参数格式不正确
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder();
        message.append("客户端错误：参数格式不正确！");
        // 参数错误信息
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if (constraintViolations != null && !constraintViolations.isEmpty()) {
            for (ConstraintViolation constraintViolation : constraintViolations) {
                message.append(constraintViolation.getMessage());
            }
        }
        return ResponseResult.builder()
                .code("400")// HttpStatus.BAD_REQUEST
                .message(message.toString())
                .build();
    }

}
