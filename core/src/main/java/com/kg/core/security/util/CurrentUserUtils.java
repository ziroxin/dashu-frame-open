package com.kg.core.security.util;

import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.zuser.entity.ZUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前登录用户
 *
 * @author ziro
 * @date 2022/5/9 22:32
 */
public class CurrentUserUtils {

    /**
     * @return 获取当前用户信息
     */
    public static ZUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || "anonymousUser".equals(principal.toString())) {
            // 无用户或匿名用户返回null
            return null;
        }
        SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity) principal;
        return userDetailEntity.getZUser();
    }


}
