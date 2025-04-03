package com.kg.module.redisCache.controller;

import cn.hutool.json.JSONUtil;
import com.kg.component.redis.RedisUtils;
import com.kg.module.redisCache.dto.RedisCacheManagerDTO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Redis 数据缓存管理
 *
 * @author ziro
 * @date 2024/2/2 13:45
 */
@RestController
@RequestMapping("/redis/cache")
public class RedisCacheManagerController {
    @Resource
    private RedisUtils redisUtils;

    @GetMapping("/keys/list")
    public List<RedisCacheManagerDTO> keysList() {
        // 更新缓存列表（判断无效的key删除）
        redisUtils.updateSaveKeysList(null);
        // 获取缓存列表
        List<String> keysList = redisUtils.getSaveKeysList();
        // 按照字母排序
        keysList.sort(String::compareTo);
        return keysList
                .stream().map(key -> {
                    if (!redisUtils.hasKey(key)) {
                        return null;
                    }
                    RedisCacheManagerDTO dto = new RedisCacheManagerDTO();
                    dto.setKey(key);
                    String value;
                    try {
                        value = JSONUtil.toJsonStr(redisUtils.get(key));
                    } catch (Exception e) {
                        e.printStackTrace();
                        value = "该值无法正确解析";
                    }
                    dto.setValue(value);
                    dto.setExpireTime(redisUtils.getExpire(key));
                    return dto;
                }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    @GetMapping("/delete")
    public void delete(String key) {
        redisUtils.setExpire(key, 1l);
        redisUtils.delete(key);
        // 更新缓存列表（判断无效的key删除）
        redisUtils.updateSaveKeysList(null);
    }
}
