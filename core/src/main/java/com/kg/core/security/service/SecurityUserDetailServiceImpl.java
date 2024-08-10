package com.kg.core.security.service;

import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.zapi.service.IZApiService;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;


/**
 * Security 用户服务实现
 *
 * @author ziro
 * @date 2022/4/27 22:27
 */
@Service
public class SecurityUserDetailServiceImpl implements UserDetailsService {
    @Resource
    private IZUserService userService;
    @Resource
    private IZApiService apiService;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        Optional<ZUser> zUser = userService.lambdaQuery().eq(ZUser::getUserName, username).last("LIMIT 1").oneOpt();
        // 没查询到，用户名错误
        if (!zUser.isPresent()) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG);
        }
        // 用户禁用
        if (!StringUtils.hasText(zUser.get().getStatus()) || !"1".equals(zUser.get().getStatus())) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_USER_DISABLED);
        }
        /**
         * 查询当前用户的权限标记List，权限标记包括：
         * 1. @PreAuthorize("hasAuthority('权限标记')") - Security 根据该标记，自动判断接口权限
         * 2. 前端路由权限标记 - 前端菜单显示隐藏
         * 3. 按钮 v-permission 中的权限标记 - 页面按钮/元素显示隐藏
         */
        List<String> lists = apiService.listApiByUserId(zUser.get().getUserId());
        // 查到用户，组合Security所需的用户信息（包含权限列表）
        return new SecurityUserDetailEntity(zUser.get(), lists);
    }
}
