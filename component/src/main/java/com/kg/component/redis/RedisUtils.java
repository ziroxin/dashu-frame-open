package com.kg.component.redis;


import cn.hutool.json.JSONObject;
import com.kg.component.utils.TimeUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private static final String REDIS_KEY_CACHE_PREFIX = "cache:redis_keys:";
    private static final long CACHE_EXPIRE_SECONDS = 3600; // 缓存 1 小时

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
            Long timeout = TimeUtils.now().betweenSecond(TimeUtils.setTime(endtime));
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            result = true;
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
            return redisTemplate.hasKey(key) && redisTemplate.getExpire(key) != -2;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 原子性设置值（仅在 key 不存在时设置）
     * 用于防重复提交、分布式锁等场景
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     * @return true=设置成功，false=key 已存在
     */
    public boolean setIfAbsent(final String key, Object value, Long timeout) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 扫描 Redis Key（使用 SCAN 命令，不会阻塞 Redis）
     * 适用于后台管理界面查看 Redis Key
     *
     * @param pattern 匹配模式，支持通配符 * ? []
     *                例如: "user:*", "order:2024-*", "*"
     * @param count   每次迭代返回的最大数量，建议 100-1000
     * @return 匹配的 Key 列表
     */
    public List<String> scanKeys(String pattern, int count) {
        if (!StringUtils.hasText(pattern)) {
            pattern = "*";
        }
        if (count <= 0 || count > 10000) {
            count = 100;
        }
        try {
            List<String> keys = new ArrayList<>();
            String finalPattern = pattern;
            int finalCount = count;
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                ScanOptions scanOptions = ScanOptions.scanOptions().match(finalPattern).count(finalCount).build();
                try (Cursor<byte[]> cursor = connection.scan(scanOptions)) {
                    while (cursor.hasNext()) {
                        byte[] keyBytes = cursor.next();
                        String key = deserializeKey(keyBytes);
                        if (key != null && !key.isEmpty()) {
                            keys.add(key);
                            if (keys.size() >= 1000) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Cursor 关闭异常忽略
                }
                return null;
            });

            return keys;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 反序列化 Key，去除 Spring Data Redis 的序列化前缀
     */
    private String deserializeKey(byte[] keyBytes) {
        if (keyBytes == null || keyBytes.length == 0) {
            return null;
        }

        try {
            // 尝试直接使用 UTF-8 解码
            String key = new String(keyBytes, StandardCharsets.UTF_8);

            // 如果包含不可见字符，尝试去除序列化前缀
            if (key.contains("\u0000") || key.contains("\u0005")) {
                // 查找第一个可见字符的位置
                int startIndex = 0;
                for (int i = 0; i < key.length(); i++) {
                    char c = key.charAt(i);
                    // 找到第一个可打印字符（ASCII 32-126 或中文）
                    if ((c >= 32 && c <= 126) || c > 127) {
                        startIndex = i;
                        break;
                    }
                }
                if (startIndex > 0) {
                    key = key.substring(startIndex);
                }
            }

            return key;
        } catch (Exception e) {
            // 如果解码失败，返回原始字节数组的 toString
            return new String(keyBytes, StandardCharsets.ISO_8859_1);
        }
    }

    /**
     * 扫描 Redis Key（简化版，默认每次 100 条）
     */
    public List<String> scanKeys(String pattern) {
        return scanKeys(pattern, 100);
    }

    /**
     * 获取 Key 列表（带缓存优化）
     * 优先从缓存读取，缓存失效或强制刷新时重新扫描 Redis
     *
     * @param pattern      匹配模式
     * @param forceRefresh 是否强制刷新（忽略缓存）
     * @return 所有匹配的 Key 列表（未分页）
     */
    public List<String> getKeysWithCache(String pattern, boolean forceRefresh) {
        String cacheKey = REDIS_KEY_CACHE_PREFIX + pattern;
        // 如果不是强制刷新，先尝试从缓存读取
        if (!forceRefresh) {
            @SuppressWarnings("unchecked")
            List<String> cachedKeys = (List<String>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedKeys != null && !cachedKeys.isEmpty()) {
                return cachedKeys;
            }
        }
        // 缓存不存在或强制刷新，重新扫描 Redis
        List<String> keys = scanKeys(pattern, 1000);
        // 将结果存入缓存，过期时间 1 小时
        if (!keys.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, keys, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
        return keys;
    }

    /**
     * 清除 Key 列表缓存
     */
    public void clearKeysCache(String pattern) {
        String cacheKey = REDIS_KEY_CACHE_PREFIX + pattern;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 清除所有 Key 列表缓存
     */
    public void clearAllKeysCache() {
        List<String> cacheKeys = scanKeys(REDIS_KEY_CACHE_PREFIX + "*", 1000);
        if (!cacheKeys.isEmpty()) {
            redisTemplate.delete(cacheKeys);
        }
    }

    /**
     * 获取 Key 的详细信息
     */
    public JSONObject getKeyDetail(String key) {
        try {
            JSONObject detail = new JSONObject();
            detail.put("key", key);
            detail.put("value", redisTemplate.opsForValue().get(key));
            detail.put("expireTime", redisTemplate.getExpire(key));
            detail.put("type", redisTemplate.type(key));
            return detail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量删除 Key（支持通配符）
     * 注意：大量删除可能影响性能，建议在低峰期使用
     */
    public long deleteByPattern(String pattern) {
        try {
            List<String> keys = scanKeys(pattern, 500);
            if (keys.isEmpty()) {
                return 0;
            }
            Long deletedCount = redisTemplate.delete(keys);
            // 删除后清除对应的缓存
            clearKeysCache(pattern);
            return deletedCount != null ? deletedCount : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
