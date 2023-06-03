package com.kg.core.zsafety.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.mapper.ZSafetyMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * <p>
 * 密码安全等设置 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
@Service
public class ZSafetyServiceImpl extends ServiceImpl<ZSafetyMapper, ZSafety> implements ZSafetyService {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取密码安全配置信息
     */
    @Override
    public ZSafety getSafety() {
        if (redisUtils.hasKey(CacheConstant.SAFETY_SET_REDIS_KEY)) {
            // 如果缓存中有，从缓存读取
            return (ZSafety) redisUtils.get(CacheConstant.SAFETY_SET_REDIS_KEY);
        }
        Optional<ZSafety> first = list().stream().findFirst();
        if (first.isPresent()) {
            // 写入缓存
            redisUtils.setNoTimeLimit(CacheConstant.SAFETY_SET_REDIS_KEY, first.get());
            return first.get();
        }
        return null;
    }

    /**
     * 清空安全配置缓存
     */
    @Override
    public void clearCache() {
        redisUtils.delete(CacheConstant.SAFETY_SET_REDIS_KEY);
    }
}
