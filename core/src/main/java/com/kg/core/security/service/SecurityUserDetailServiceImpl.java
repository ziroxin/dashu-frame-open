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
        Optional<ZUser> zUser = userService.lambdaQuery().eq(ZUser::getUserName, username).oneOpt();
        // 没查询到，用户名错误
        if (!zUser.isPresent()) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG);
        }
        // 查询用户权限列表
        List<String> lists = apiService.listApiByUserId(zUser.get().getUserId());
        // 查到用户，并返回
        return new SecurityUserDetailEntity(zUser.get(), lists);
    }
}
