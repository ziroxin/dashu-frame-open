package com.kg.module.dbQuery.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * db-query 模块启用条件，读取 com.kg.db-query.enabled。
 * 缺省（未配置）视为启用，仅显式 false 时禁用整个模块。
 *
 * @author ziro
 * @date 2026/3/9
 */
public class DbQueryEnabledCondition implements Condition {

    private static volatile String enableValue;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (!StringUtils.hasText(enableValue)) {
            enableValue = context.getEnvironment().getProperty("com.kg.db-query.enabled");
        }
        // 未配置默认启用；显式 false 才禁用
        return !Boolean.FALSE.toString().equalsIgnoreCase(enableValue);
    }
}
