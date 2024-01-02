package com.kg.core.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 登录业务相关常量
 *
 * @author ziro
 * @date 2022/5/4 23:02
 */
@Component
public class LoginConstant {

    /**
     * 全局jwt_token的key
     */
    public static final String LOGIN_JWT_TOKEN_KEY = "UserJwtToken";

    /**
     * 用户最后一次登录的token前缀
     */
    public static final String LAST_LOGIN_TOKEN_PRE = "last_login_token@";

    /**
     * 配置：登录token的有效时间（单位：分钟）
     */
    public static Integer LOGIN_JWT_TOKEN_EXPIRY;

    @Value("${com.kg.login.jwt-token-expiry}")
    public void setLoginJwtTokenExpiry(Integer expiry) {
        LoginConstant.LOGIN_JWT_TOKEN_EXPIRY = expiry;
    }

    /**
     * 配置：开发管理员userIds
     */
    public static String DEVELOPER_USER_IDS;

    @Value("${com.kg.developer-user-ids}")
    public void setDeveloperUserIds(String userIds) {
        LoginConstant.DEVELOPER_USER_IDS = userIds;
    }

    /**
     * 配置：是否开启单例登录：true开启，false关闭
     */
    public static boolean IS_USER_LOGIN_ONLY_ONE = false;

    @Value("${com.kg.login.is-only-one}")
    public void setDeveloperUserIds(boolean onlyOne) {
        LoginConstant.IS_USER_LOGIN_ONLY_ONE = onlyOne;
    }

    /**
     * 判断 userId 是否开发管理员
     *
     * @param userId 用户id
     * @return boolean 是否开发管理员
     */
    public static boolean isDeveloper(String userId) {
        return (LoginConstant.DEVELOPER_USER_IDS + ",").contains(userId + ",");
    }
}
