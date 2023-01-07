package com.kg.core.annotation;

import java.lang.annotation.*;

/**
 * 防止重复提交
 *
 * @author ziro
 * @date 2023-01-07 11:05:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NoRepeatSubmit {
    /**
     * 防止重复锁定时间 默认2s不能重复提交
     */
    long lockSecond() default 2L;
}
