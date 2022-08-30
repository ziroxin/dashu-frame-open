package com.kg.core.exception;

/**
 * 全局异常
 *
 * @author ziro
 * @date 2022/5/2 10:01
 */
public class MessageException extends Exception {
    private static final long serialVersionUID = 1L;

    public MessageException() {
        super();
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }
}
