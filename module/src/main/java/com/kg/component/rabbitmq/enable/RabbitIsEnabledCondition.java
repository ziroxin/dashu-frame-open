package com.kg.component.rabbitmq.enable;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;


/**
 * 配置 rabbitmq 是否启用的条件，注解 @RabbitIsEnabled 自动调用
 *
 * @author ziro
 * @date 2024/6/13 15:49
 */
public class RabbitIsEnabledCondition implements Condition {
    /**
     * 配置项 com.kg.rabbitmq.enable 的值
     */
    private static String enableValue;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (!StringUtils.hasText(enableValue)) {
            enableValue = context.getEnvironment().getProperty("com.kg.rabbitmq.enable");
        }
        return "true".equalsIgnoreCase(enableValue);
    }
}
