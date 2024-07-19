package com.kg.component.oss.aliyun.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kg.component.redis.RedisUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 已上传的oss文件，缓存读取工具
 *
 * @author ziro
 * @date 2024/7/18 16:26
 */
@Component
public class OssFileCacheUtils {
    @Resource
    private RedisUtils redisUtils;

    private static RedisUtils staticRedisUtils;

    @PostConstruct
    public void init() {
        staticRedisUtils = redisUtils;
    }

    /**
     * 读取缓存中的已上传文件信息
     *
     * @param fileId 文件id（缓存的key）
     * @return {@link JSONObject }
     */
    public static JSONObject get(String fileId) {
        if (staticRedisUtils.hasKey(fileId)) {
            return (JSONObject) staticRedisUtils.get(fileId);
        }
        return null;
    }

    /**
     * 读取缓存中的已上传文件信息
     *
     * @param fileId 文件id（缓存的key）
     * @param clazz  需要转换的类
     * @return {@link T }
     */
    public static <T> T getBean(String fileId, Class<T> clazz) {
        return JSONUtil.toBean(get(fileId), clazz);
    }
}
