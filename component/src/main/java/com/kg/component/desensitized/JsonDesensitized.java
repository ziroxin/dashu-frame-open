package com.kg.component.desensitized;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziro
 * @date 2023-02-15 14:23:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = JsonDesensitizedUtils.class)
public @interface JsonDesensitized {
    /**
     * 脱敏类型（必填）
     */
    DesensitizedType value();
}
