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
import java.lang.reflect.UndeclaredThrowableException;
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
        // 检测是否包含数据库敏感信息，防止泄露
        if (isDatabaseException(error)) {
            log.error("数据库异常被拦截: {}", error, ex);
            return ResponseResult.error("数据操作异常，请检查输入内容或联系管理员！");
        }
        // 未定义异常，统一返回500异常
        log.error(error, ex);
        return ResponseResult.error(error);
    }

    /**
     * 判断是否为数据库相关异常（通过异常消息特征识别）
     *
     * @param errorMessage 异常消息
     * @return true-数据库异常，false-其他异常
     */
    private boolean isDatabaseException(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return false;
        }

        String lowerMessage = errorMessage.toLowerCase();

        // 1. 数据库驱动包名特征
        String[] dbPackageKeywords = {
                "com.mysql",
                "org.postgresql",
                "oracle.jdbc",
                "com.microsoft.sqlserver",
                "com.ibm.db2",
                "org.h2",
                "org.sqlite"
        };

        // 2. 数据库异常类型特征
        String[] dbExceptionKeywords = {
                "sqlsyntaxerrorexception",
                "sqlexception",
                "dataintegrityviolation",
                "constraintviolation",
                "deadlock",
                "timeout"
        };

        // 3. 常见数据库错误信息特征
        String[] dbErrorKeywords = {
                "duplicate entry",           // MySQL 唯一约束冲突
                "data too long",             // 字段长度超限
                "cannot be null",            // 非空约束
                "foreign key",               // 外键约束
                "table doesn't exist",       // 表不存在
                "column doesn't exist",      // 列不存在
                "truncated",                 // 数据截断
                "out of range",              // 数值越界
                "connection refused",        // 连接拒绝
                "connection timed out"       // 连接超时
        };

        // 检查数据库驱动包名
        for (String keyword : dbPackageKeywords) {
            if (lowerMessage.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        // 检查异常类型
        for (String keyword : dbExceptionKeywords) {
            if (lowerMessage.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        // 检查错误信息特征
        for (String keyword : dbErrorKeywords) {
            if (lowerMessage.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
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
        return ResponseResult.error("系统繁忙，请稍后重试！");
    }

    /**
     * 服务端异常：运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleRuntimeException(RuntimeException ex) {
        if (ex instanceof UndeclaredThrowableException && ex.getCause() instanceof BaseException) {
            return handleBaseException((BaseException) ex.getCause());
        }
        log.error(ex.getMessage(), ex);
        return ResponseResult.error("服务器端异常，请联系管理员！");
    }

    /**
     * 服务端异常：IOException 文件读写异常
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleIOException(IOException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error("文件操作失败，请重试！");
    }

    /**
     * 服务端异常：IllegalArgumentException.java 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseResult.error("参数不正确，请检查后重试！");
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
