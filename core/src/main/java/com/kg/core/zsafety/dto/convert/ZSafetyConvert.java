package com.kg.core.zsafety.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.core.zsafety.dto.ZSafetyDTO;
import com.kg.core.zsafety.entity.ZSafety;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2022-12-30
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZSafetyConvert extends BaseConverter<ZSafety, ZSafetyDTO> {
}