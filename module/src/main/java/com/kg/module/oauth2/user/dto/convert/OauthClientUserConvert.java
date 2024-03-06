package com.kg.module.oauth2.user.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.oauth2.user.dto.OauthClientUserDTO;
import com.kg.module.oauth2.user.entity.OauthClientUser;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2024-03-04
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface OauthClientUserConvert extends BaseConverter<OauthClientUser, OauthClientUserDTO> {
}