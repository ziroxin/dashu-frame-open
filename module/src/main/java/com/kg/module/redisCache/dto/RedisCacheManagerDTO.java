package com.kg.module.redisCache.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Redis 数据缓存管理 DTO
 *
 * @author ziro
 * @date 2024/2/3 14:42
 */
@Getter
@Setter
public class RedisCacheManagerDTO {

    /**
     * key
     */
    private String key;
    /**
     * 值
     */
    private String value;
    /**
     * 过期时间
     */
    private Long expireTime;
}
