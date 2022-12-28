package com.kg.core.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.zapi.service.IZApiService;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Security 用户服务实现
 *
 * @author ziro
 * @date 2022/4/27 22:27
 */
@Service
public class SecurityUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IZUserService userService;
    @Autowired
    private IZApiService apiService;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        QueryWrapper<ZUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ZUser::getUserName, username);
        ZUser user = userService.getOne(wrapper);
        // 没查询到
        if (null == user) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG);
        }
        // 查询用户权限列表
        List<String> lists = apiService.listApiByUserId(user.getUserId());
        // 查到用户，并返回
        return new SecurityUserDetailEntity(user, lists);
    }
}
