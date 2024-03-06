package com.kg.module.oauth2.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.module.oauth2.user.entity.OauthClientUser;
import com.kg.module.oauth2.user.mapper.OauthClientUserMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Oauth2 - 用户绑定表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2024-03-04
 */
@Service
public class OauthClientUserServiceImpl extends ServiceImpl<OauthClientUserMapper, OauthClientUser> implements OauthClientUserService {

}
