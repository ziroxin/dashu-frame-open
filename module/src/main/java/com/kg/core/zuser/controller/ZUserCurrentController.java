package com.kg.core.zuser.controller;

import cn.hutool.json.JSONUtil;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zrole.entity.ZRole;
import com.kg.core.zuser.dto.MyUserDTO;
import com.kg.core.zuser.dto.ZUserAllDTO;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;
import com.kg.module.applet.wechat2user.service.ZUserWechatService;
import com.kg.module.oauth2.user.entity.OauthClientUser;
import com.kg.module.oauth2.user.service.OauthClientUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 用户个人中心接口
 *
 * @author ziro
 * @date 2024/1/16 14:29
 */
@RestController
public class ZUserCurrentController {
    @Resource
    private IZUserService userService;
    @Resource
    private OauthClientUserService oauthClientUserService;
    @Resource
    private ZUserWechatService userWechatService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/user/getCurrentUser")
    public MyUserDTO getCurrentUser() {
        try {
            ZUserAllDTO currentUser = CurrentUserUtils.getCurrentUserAll();
            // 用户基本资料
            ZUserRoleSaveDTO user = userService.getUserById(currentUser.getUserId());
            MyUserDTO myUserDTO = JSONUtil.toBean(JSONUtil.parseObj(user), MyUserDTO.class);
            // 角色IDs
            if (LoginConstant.isDeveloper(currentUser.getUserId())) {
                myUserDTO.setRoleId(Arrays.asList("isDeveloper"));
            } else {
                myUserDTO.setRoleId(currentUser.getRoleList().stream().map(ZRole::getRoleId).collect(Collectors.toList()));
            }
            // 单位名称
            myUserDTO.setOrgName(currentUser.getOrgName());
            // 是否绑定Oauth2的openId
            Long count = oauthClientUserService.lambdaQuery().eq(OauthClientUser::getUserId, myUserDTO.getUserId()).count();
            myUserDTO.setOauthBind(count > 0 ? true : false);
            // 是否绑定微信
            Long count_wechat = userWechatService.lambdaQuery().eq(ZUserWechat::getUserId, myUserDTO.getUserId()).count();
            myUserDTO.setWechatBind(count_wechat > 0 ? true : false);
            return myUserDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存当前用户个人信息
     */
    @PostMapping("/user/saveCurrentUser")
    public void saveCurrentUser(@RequestBody ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        try {
            if (CurrentUserUtils.getCurrentUser().getUserId().equals(zUserRoleSaveDTO.getUserId())) {
                // 查重
                if (userService.lambdaQuery().eq(ZUser::getUserName, zUserRoleSaveDTO.getUserName())
                        .ne(ZUser::getUserId, zUserRoleSaveDTO.getUserId()).exists()) {
                    throw new BaseException("用户名已存在!");
                }
                ZUser zUser = new ZUser();
                BeanUtils.copyProperties(zUserRoleSaveDTO, zUser);
                zUser.setUpdateTime(LocalDateTime.now());
                userService.updateById(zUser);
                // 刷新当前用户信息
                SecurityUserDetailEntity updateUserDetail = CurrentUserUtils.getSecurityUserDetailEntity();
                updateUserDetail.setZUser(zUser);
                redisUtils.set(CacheConstant.LOGIN_INFO_REDIS_PRE + zUser.getUserId(), updateUserDetail,
                        LoginConstant.LOGIN_JWT_TOKEN_EXPIRY * 60L);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改当前用户信息失败");
        }
    }
}
