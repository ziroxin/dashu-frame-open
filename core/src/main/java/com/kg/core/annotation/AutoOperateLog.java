package com.kg.core.annotation;

import java.lang.annotation.*;

/**
 * 操作日志
 *
 * @author ziro
 * @date 2023-01-07 17:08:14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoOperateLog {
    /**
     * 方法路径
     */
    String logMethod() default "";

    /**
     * 方法描述
     */
    String logMsg() default "";
}
