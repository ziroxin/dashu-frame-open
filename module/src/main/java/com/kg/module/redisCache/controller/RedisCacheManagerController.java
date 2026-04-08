package com.kg.module.redisCache.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.redis.RedisUtils;
import com.kg.core.exception.BaseException;
import com.kg.module.redisCache.dto.RedisCacheManagerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Redis 数据缓存管理
 *
 * @author ziro
 * @date 2024/2/2 13:45
 */
@Api(tags = "Redis 缓存管理")
@RestController
@RequestMapping("/redis/cache")
public class RedisCacheManagerController {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 分页查询 Redis Key 列表
     *
     * @param pattern  匹配模式，如: user:*, order:*, *
     * @param page     页码（从 1 开始）
     * @param pageSize 每页大小
     * @param refresh  是否强制刷新缓存
     * @return 分页结果
     */
    @ApiOperation(value = "/redis/cache/keys", notes = "查询Redis Key列表（带缓存和分页）", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pattern", value = "匹配模式，如:user:*,order:*,*", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码，从1开始", paramType = "query", required = false, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "query", required = false, dataType = "int"),
            @ApiImplicitParam(name = "refresh", value = "是否强制刷新缓存", paramType = "query", required = false, dataType = "boolean")
    })
    @GetMapping("/keys")
    public Page<String> listKeys(@RequestParam(defaultValue = "*") String pattern,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "20") int pageSize,
                                 @RequestParam(defaultValue = "false") boolean refresh) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        // 获取所有 Key（可能来自缓存）
        List<String> allKeys = redisUtils.getKeysWithCache(pattern, refresh);
        // 计算总数
        int total = allKeys.size();
        // 计算分页范围
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        // 截取当前页数据
        List<String> pageData = fromIndex < total ? allKeys.subList(fromIndex, toIndex) : Collections.emptyList();
        // 封装返回结果
        Page<String> result = new Page<>(page, pageSize);
        result.setRecords(pageData);
        result.setTotal(total);
        return result;
    }

    @ApiOperation(value = "/redis/cache/key/detail", notes = "获取 Key 详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/key/detail")
    public RedisCacheManagerDTO getKeyDetail(@RequestParam String key) throws BaseException {
        try {
            return JSONUtil.toBean(redisUtils.getKeyDetail(key), RedisCacheManagerDTO.class);
        } catch (Exception e) {
            throw new BaseException("获取 Key 详情失败！");
        }
    }

    @ApiOperation("删除单个 Key")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", paramType = "query", required = false, dataType = "String")
    })
    @DeleteMapping("/key/delete")
    public void deleteKey(@RequestParam String key) throws BaseException {
        try {
            redisUtils.delete(key);
        } catch (Exception e) {
            throw new BaseException("删除 Key 失败！");
        }
    }

    /**
     * 批量删除 Key（支持通配符）
     */
    @ApiOperation("批量删除 Key")
    @DeleteMapping("/keys/batchDelete")
    public void deleteByPattern(@RequestParam String pattern) throws BaseException {
        try {
            redisUtils.deleteByPattern(pattern);
        } catch (Exception e) {
            throw new BaseException("批量删除 Key 失败！");
        }
    }
}
