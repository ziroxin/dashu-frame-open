package com.kg.core.retgister.service;

import com.kg.component.utils.GuidUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.retgister.dto.RegisterFormDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.entity.ZUserRole;
import com.kg.core.zuser.service.IZUserRoleService;
import com.kg.core.zuser.service.IZUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author ziro
 * @date 2024/5/27 14:11
 */
@Service("RegisterService")
public class RegisterServiceImpl implements RegisterService {
    @Resource
    private IZUserService userService;
    @Resource
    private IZUserRoleService userRoleService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(RegisterFormDTO regFormDTO) throws BaseException {
        // 查重
        if (userService.lambdaQuery().eq(ZUser::getUserName, regFormDTO.getUserName()).exists()) {
            throw new BaseException("用户名已存在!");
        }
        // 保存用户
        ZUser zUser = new ZUser();
        BeanUtils.copyProperties(regFormDTO, zUser);
        zUser.setUserId(GuidUtils.getUuid());
        zUser.setPassword(passwordEncoder.encode(regFormDTO.getPassword()));
        zUser.setCreateTime(LocalDateTime.now());
        userService.save(zUser);
        // 保存用户角色
        ZUserRole zUserRole = new ZUserRole();
        zUserRole.setUserId(zUser.getUserId());
        zUserRole.setRoleId("54eaea4ee678e9fa5c600e9be57626b9");// 注册用户角色ID
        userRoleService.save(zUserRole);
    }
}
