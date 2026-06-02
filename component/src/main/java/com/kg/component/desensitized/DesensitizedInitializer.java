package com.kg.component.desensitized;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 脱敏配置初始化类
 * 用于将 DesensitizedConfig 注入到 JsonDesensitizedUtils 的静态变量中
 *
 * @author ziro
 * @date 2026-06-02 15:30:00
 */
@Configuration
@RequiredArgsConstructor
public class DesensitizedInitializer {

    private final DesensitizedConfig desensitizedConfig;

    @PostConstruct
    public void init() {
        // 将配置注入到 JsonDesensitizedUtils 的静态变量中
        JsonDesensitizedUtils.setConfig(desensitizedConfig);
    }
}
