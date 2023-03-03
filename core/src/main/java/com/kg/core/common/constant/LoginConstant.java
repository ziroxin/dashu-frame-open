package com.kg.core.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 登录相关常量
 *
 * @author ziro
 * @date 2022/5/4 23:02
 */
@Component
public class LoginConstant {
    // 登录信息存储到reids的前缀
    public static final String LOGIN_INFO_REDIS_PRE = "zlogin@";
    // 登录token的key
    public static final String LOGIN_JWT_TOKEN_KEY = "z_jwt_token";
    // 用户最后一次登录的token前缀
    public static final String LAST_LOGIN_TOKEN_PRE = "lasg_login_token@";
    // 权限信息存储到redis的前缀
    public static final String PERMISSION_REDIS_PRE = "zpermission@";
    // 角色和api关联关系redis缓存key
    public static final String ROLE_API_REDIS_KEY = "role_4_api_redis";
    // 登录token的有效时间（单位：分钟）
    public static Integer LOGIN_JWT_TOKEN_EXPIRY;
    // 开发管理员userIds
    public static String DEVELOPER_USER_IDS;
    // 密码安全策略配置redis存储名称
    public static final String SAFETY_SET_REDIS_KEY = "z_safety_set_config";
    // 用户登录错误次数
    public static final String LOGIN_ERROR_COUNT_REDIS_PRE = "z_login_error_count@";
    // 用户缓存锁定时，存入redis的前缀
    public static final String USER_LOCK_REDIS_PRE = "z_user_lock@";
    // 缓存中锁定用户的列表记录
    public static final String USER_LOCKED_LIST_REDIS_KEY = "user_locked_list_redis";
    // 是否开启单例登录：true开启，false关闭
    public static boolean IS_USER_LOGIN_ONLY_ONE = false;

    // 读取配置：jwtToken的有效期
    @Value("${com.kg.login.jwt-token-expiry}")
    public void setLoginJwtTokenExpiry(Integer expiry) {
        LoginConstant.LOGIN_JWT_TOKEN_EXPIRY = expiry;
    }

    // 读取配置：开发管理员的ids
    @Value("${com.kg.developer-user-ids}")
    public void setDeveloperUserIds(String userIds) {
        LoginConstant.DEVELOPER_USER_IDS = userIds;
    }

    // 读取配置：是否单例登录
    @Value("${com.kg.login.is-only-one}")
    public void setDeveloperUserIds(boolean onlyOne) {
        LoginConstant.IS_USER_LOGIN_ONLY_ONE = onlyOne;
    }

}
