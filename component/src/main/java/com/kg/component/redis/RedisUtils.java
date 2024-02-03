package com.kg.component.redis;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 工具类
 *
 * @author ziro
 * @date 2020/5/19 19:30
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 读取缓存
     */
    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 写入缓存（永不过期）
     * 删除须手动删除
     */
    public boolean setNoTimeLimit(final String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
            updateSaveKeysList(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取当前key的有效期
     */
    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 写入缓存（默认10分钟过期）
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES);
            result = true;
            updateSaveKeysList(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存（超时时间，单位s）
     *
     * @param timeout 超时时间，单位s
     */
    public boolean set(final String key, Object value, Long timeout) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            result = true;
            updateSaveKeysList(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存（加到期时间）
     *
     * @param endtime 到期时间
     */
    public boolean set(final String key, Object value, Date endtime) {
        boolean result = false;
        try {
            Long timeout = DateUtil.between(DateUtil.date(), endtime, DateUnit.SECOND);
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            result = true;
            updateSaveKeysList(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新缓存超期时间
     *
     * @param timeout 超时时间，单位s
     */
    public boolean setExpire(final String key, Long timeout) {
        boolean result = false;
        try {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            result = true;
            updateSaveKeysList(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除缓存
     */
    public boolean delete(final String key) {
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
            updateSaveKeysList(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 已存储的keys列表
     */
    public List<String> getSaveKeysList() {
        if (redisTemplate.hasKey("REDIS_ALL_SAVE_KEYS_LIST")) {
            Object keys = redisTemplate.opsForValue().get("REDIS_ALL_SAVE_KEYS_LIST");
            if (keys != null && !"".equals(keys.toString())) {
                return Arrays.asList(keys.toString().split(","));
            }
        }
        return new ArrayList<>();
    }

    /**
     * 保存自主存储的redisKey（主要用于缓存查询，或模糊查询）
     */
    public void updateSaveKeysList(String key) {
        ThreadUtil.execute(() -> {
            // 获取分布式锁
            String lockKey = "updateSaveKeysList_lock";
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked");
            if (locked != null && locked) {
                try {
                    // 获取所有key
                    List<String> allKeys = new ArrayList<>(getSaveKeysList());
                    if (StringUtils.hasText(key) && !allKeys.contains(key)) {
                        allKeys.add(key);// 存入新key
                    }
                    // 检查key是否过期，过期的删除
                    List<String> updateList = allKeys.stream()
                            .filter(k -> redisTemplate.hasKey(k)).collect(Collectors.toList());
                    updateList.stream().forEach(System.out::println);
                    // 更新REDIS_ALL_SAVE_KEYS_LIST
                    redisTemplate.opsForValue().set("REDIS_ALL_SAVE_KEYS_LIST",
                            updateList.stream().collect(Collectors.joining(",")));
                } finally {
                    // 释放分布式锁
                    redisTemplate.delete(lockKey);
                }
            } else {
                // 未获取到锁，处理并重试或者放弃
                try {
                    Thread.sleep(1000L);
                    updateSaveKeysList(key);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
