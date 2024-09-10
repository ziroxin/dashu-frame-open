package com.kg.module.redisCache.controller;

import cn.hutool.json.JSONUtil;
import com.kg.component.redis.RedisUtils;
import com.kg.module.redisCache.dto.RedisCacheManagerDTO;
import org.springframework.web.bind.annotation.*;

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
                    RedisCacheManagerDTO dto = new RedisCacheManagerDTO();
                    dto.setKey(key);
                    dto.setValue(JSONUtil.toJsonStr(redisUtils.get(key)));
                    dto.setExpireTime(redisUtils.getExpire(key));
                    return dto;
                }).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public void delete(@RequestBody String key) {
        redisUtils.delete(key);
        // 更新缓存列表（判断无效的key删除）
        redisUtils.updateSaveKeysList(null);
    }
}
