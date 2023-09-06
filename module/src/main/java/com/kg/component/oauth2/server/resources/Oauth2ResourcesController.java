package com.kg.component.oauth2.server.resources;

import cn.hutool.json.JSONUtil;
import com.kg.component.oauth2.server.dto.Oauth2User;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zuser.entity.ZUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oauth2 资源服务 API
 *
 * @author ziro
 * @date 2023/9/1 16:13
 */
@RestController
@RequestMapping("/oauth/resources")
public class Oauth2ResourcesController {

    /**
     * 使用 access_token 获取当前登录用户基本信息的接口
     */
    @PostMapping("userInfo")
    public Oauth2User userInfo() {
        ZUser currentUser = CurrentUserUtils.getCurrentUser();
        Oauth2User user = JSONUtil.toBean(JSONUtil.toJsonStr(currentUser), Oauth2User.class);
        user.setOpenId(currentUser.getUserId());
        return user;
    }
}
