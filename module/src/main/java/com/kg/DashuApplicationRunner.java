package com.kg;

import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.CacheConstant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目启动后，执行一些初始化操作
 *
 * @author ziro
 * @date 2023-04-17 14:58:44
 */
@Component
public class DashuApplicationRunner implements ApplicationRunner {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 项目启动后：进行初始化操作
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 清空用户授权信息缓存
        redisUtils.delete(CacheConstant.ROLE_API_REDIS_KEY);
        // 清空安全配置缓存
        redisUtils.delete(CacheConstant.SAFETY_SET_REDIS_KEY);
    }
}
