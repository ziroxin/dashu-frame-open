package com.kg.module.message.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.message.dto.ZMessageDTO;
import com.kg.module.message.entity.ZMessage;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2024-06-18
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZMessageConvert extends BaseConverter<ZMessage, ZMessageDTO> {
}