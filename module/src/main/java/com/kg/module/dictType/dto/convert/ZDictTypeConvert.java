package com.kg.module.dictType.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.dictType.dto.ZDictTypeDTO;
import com.kg.module.dictType.entity.ZDictType;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-05-26
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZDictTypeConvert extends BaseConverter<ZDictType, ZDictTypeDTO> {
}