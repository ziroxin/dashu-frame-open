package com.kg.component.rabbitmq.enable;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否启用 RabbitMQ 的注解
 * <p>
 * 使用方法：
 * 在需要启用 RabbitMQ 的类上添加注解 @RabbitIsEnabled
 * </p>
 *
 * @author ziro
 * @date 2024/6/13 16:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(RabbitIsEnabledCondition.class)
public @interface RabbitIsEnabled {
}
