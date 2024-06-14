package com.kg.component.rabbitmq.config;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 自定义配置类
 * <p>
 * 不使用application.yml中 spring.rabbitmq 配置
 * 这样可以根据需要配置是否启用 RabbitMQ
 * </p>
 *
 * @author ziro
 * @date 2024/6/13 15:36
 */
@Configuration
@RabbitIsEnabled
public class RabbitConfiguration {

    @Bean
    public CachingConnectionFactory connectionFactory(RabbitConfig config) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(config.getHost());
        connectionFactory.setPort(config.getPort());
        connectionFactory.setUsername(config.getUsername());
        connectionFactory.setPassword(config.getPassword());
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
