package com.kg.module.config.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.config.dto.ZConfigDTO;
import com.kg.module.config.entity.ZConfig;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2024-05-27
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZConfigConvert extends BaseConverter<ZConfig, ZConfigDTO> {
}