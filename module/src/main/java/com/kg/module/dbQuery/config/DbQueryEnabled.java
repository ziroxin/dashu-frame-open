package com.kg.module.dbQuery.config;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库查询工具是否启用的条件注解。
 * <p>
 * 标注在 db-query 模块的 Bean 上，当 com.kg.db-query.enabled=false 时，
 * 这些类不会被 Spring 容器注册，接口也不会被 API 扫描发现。
 * </p>
 *
 * @author ziro
 * @date 2026/3/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(DbQueryEnabledCondition.class)
public @interface DbQueryEnabled {
}
