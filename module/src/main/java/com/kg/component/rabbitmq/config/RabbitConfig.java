package com.kg.component.rabbitmq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq 配置信息引入
 *
 * @author ziro
 * @date 2024/6/13 15:41
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.kg.rabbitmq")
public class RabbitConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    // ============= 更多配置，可根据需要扩展 =============
}
