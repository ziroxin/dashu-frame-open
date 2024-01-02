package com.kg.module.oauth2.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.oauth2.client.entity.OauthClientDetails;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Oauth2客户端信息表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2023-09-12
 */
@Repository
public interface OauthClientDetailsMapper extends BaseMapper<OauthClientDetails> {

}
