package com.kg.module.oauth2.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.oauth2.user.entity.OauthClientUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Oauth2 - 用户绑定表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2024-03-04
 */
@Repository
public interface OauthClientUserMapper extends BaseMapper<OauthClientUser> {

}
