package com.kg.core.common.constant;

/**
 * 缓存常量
 *
 * @author ziro
 * @date 2023/6/3 8:53
 */
public class CacheConstant {
    /**
     * 前缀：存储登录信息 pre + userId
     */
    public static final String LOGIN_INFO_REDIS_PRE = "z_login@";

    /**
     * 前缀：用户锁定 pre + userName
     */
    public static final String USER_LOCK_REDIS_PRE = "z_user_lock@";

    /**
     * 前缀：用户登录错误次数(用于锁定用户判断) pre + userName
     */
    public static final String LOGIN_ERROR_COUNT_REDIS_PRE = "z_login_error_count@";

    /**
     * 前缀：数据字典 pre + dictType
     */
    public static final String DICT_TYPE_REDIS_PRE = "z_dict_type@";


    /**
     * KEY：角色和api关联关系redis缓存key
     */
    public static final String ROLE_API_REDIS_KEY = "role_4_api_redis";

    /**
     * KEY：安全策略（密码规则等）
     */
    public static final String SAFETY_SET_REDIS_KEY = "z_safety_set_config";

    /**
     * KEY：用户锁定列表
     */
    public static final String USER_LOCKED_LIST_REDIS_KEY = "user_locked_list_redis";

}
