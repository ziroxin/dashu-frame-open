package com.kg.module.oauth2.client.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.oauth2.client.dto.OauthClientDetailsDTO;
import com.kg.module.oauth2.client.entity.OauthClientDetails;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-09-12
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface OauthClientDetailsConvert extends BaseConverter<OauthClientDetails, OauthClientDetailsDTO> {
}