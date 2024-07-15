package com.kg.core.security.util;

import cn.hutool.json.JSONUtil;
import com.kg.component.jwt.JwtUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.service.ZOrganizationService;
import com.kg.core.zuser.dto.ZUserAllDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserRoleService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 获取当前登录用户
 *
 * @author ziro
 * @date 2022/5/9 22:32
 */
@Component
public class CurrentUserUtils {
    @Resource
    private ZOrganizationService zOrganizationService;
    @Resource
    private IZUserRoleService zUserRoleService;
    @Resource
    private RedisUtils redisUtils;

    private static ZOrganizationService staticZOrganizationService;
    private static IZUserRoleService staticZUserRoleService;
    private static RedisUtils staticRedisUtils;

    @PostConstruct
    public void init() {
        staticZOrganizationService = zOrganizationService;
        staticZUserRoleService = zUserRoleService;
        staticRedisUtils = redisUtils;
    }

    /**
     * 获取用户基本信息
     *
     * @return {@link ZUser }
     */
    public static ZUser getCurrentUser() {
        SecurityUserDetailEntity entity = getSecurityUserDetailEntity();
        if (entity != null) {
            return entity.getZUser();
        }
        return null;
    }

    /**
     * 获取用户完整信息（用户信息 + 用户角色List + 用户所在部门）
     *
     * @return {@link ZUserAllDTO }
     */
    public static ZUserAllDTO getCurrentUserAll() {
        ZUser user = getCurrentUser();
        if (user != null) {
            ZUserAllDTO result = JSONUtil.toBean(JSONUtil.parseObj(user), ZUserAllDTO.class);
            // 查询部门信息
            ZOrganization org = staticZOrganizationService.getById(user.getOrgId());
            if (org != null) {
                result.setOrgName(org.getOrgName());
            }
            // 查询角色列表
            result.setRoleList(staticZUserRoleService.getRoleListByUserId(user.getUserId()));
            return result;
        }
        return null;
    }

    /**
     * 获取当前用户信息（ZUser） - 根据jwtToken获取
     *
     * @param token jwtToken值
     * @return 当前用户信息
     */
    public static ZUser getCurrentUserByToken(String token) throws BaseException {
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
                staticRedisUtils.get(CacheConstant.LOGIN_INFO_REDIS_PRE + userId);
        if (ObjectUtils.isEmpty(userDetailEntity)) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
        } else {
            // 读取成功
            return userDetailEntity.getZUser();
        }
    }

    /**
     * 获取Security用户信息(包含：用户信息 + 用户权限标记）
     *
     * @return {@link SecurityUserDetailEntity }
     */
    public static SecurityUserDetailEntity getSecurityUserDetailEntity() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || "anonymousUser".equals(principal.toString())) {
            // 无用户或匿名用户返回null
            return null;
        }
        SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity) principal;
        return userDetailEntity;
    }
}
