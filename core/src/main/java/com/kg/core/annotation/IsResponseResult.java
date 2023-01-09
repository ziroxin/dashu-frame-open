package com.kg.core.annotation;

import java.lang.annotation.*;

/**
 * 是否使用默认全局返回值 ResponseResult
 *
 * @author ziro
 * @date 2022/5/2 11:01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IsResponseResult {

    /**
     * 是否使用ResponseResult
     * 默认值：true
     * 返回false时，不使用全局返回值
     */
    boolean value() default true;
}
