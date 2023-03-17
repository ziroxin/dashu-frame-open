package com.kg.component.desensitized;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据脱敏注解
 *
 * 使用方法：在需要脱敏的实体，字段上加注解
 * 例如：
 * <pre>
 *     // 姓名脱敏
 *     @JsonDesensitized(DesensitizedType.CHINESE_NAME)
 *     private String name;
 * </pre>
 * 目前支持的脱敏格式，见枚举类：DesensitizedType
 *
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
