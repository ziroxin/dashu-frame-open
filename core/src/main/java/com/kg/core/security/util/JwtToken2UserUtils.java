package com.kg.core.security.util;

import com.kg.component.jwt.JwtUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.zuser.entity.ZUser;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * @author ziro
 * @date 2024/3/7 10:35
 */
@Component
public class JwtToken2UserUtils {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取当前用户信息（ZUser） - 根据jwtToken获取
     *
     * @param token jwtToken值
     * @return 当前用户信息
     */
    public ZUser getCurrentUserByToken(String token) throws BaseException {
        // 解析token
        Object userId;
        try {
            userId = JwtUtils.parseToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
        }
        if (ObjectUtils.isEmpty(userId)) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
        }
        // 从redis中读取用户信息
        SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity)
                redisUtils.get(CacheConstant.LOGIN_INFO_REDIS_PRE + userId);
        if (ObjectUtils.isEmpty(userDetailEntity)) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
        } else {
            // 读取成功
            return userDetailEntity.getZUser();
        }
    }
}
