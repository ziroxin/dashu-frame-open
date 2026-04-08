package com.kg.component.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 只配置 Key 的序列化方式，防止乱码
 * Value 使用默认的 JdkSerializationRedisSerializer
 *
 * @author ziro
 * @date 2026/4/8 13:49
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // ✅ 只配置 Key 使用 String 序列化器（解决乱码问题）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 和 HashValue 使用默认的 JdkSerializationRedisSerializer（无需配置）

        template.afterPropertiesSet();
        return template;
    }
}

