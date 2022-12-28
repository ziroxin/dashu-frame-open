package com.kg.core.exception;

import com.kg.core.exception.enums.BaseErrorCode;

/**
 * 全局异常
 *
 * @author ziro
 * @date 2022/5/2 10:01
 */
public class BaseException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * 未定义异常（错误码统一是500）
     *
     * @param message 异常信息
     */
    public BaseException(String message) {
        // 默认错误码：500
        super(message);
    }

    /**
     * 固定错误码异常
     *
     * @param errorCode 错误码
     */
    public BaseException(BaseErrorCode errorCode) {
        super(errorCode.getCode().toString());
    }
}
