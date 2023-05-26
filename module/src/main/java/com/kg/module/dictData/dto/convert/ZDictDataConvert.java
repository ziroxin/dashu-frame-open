package com.kg.module.dictData.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.dictData.dto.ZDictDataDTO;
import com.kg.module.dictData.entity.ZDictData;
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
public interface ZDictDataConvert extends BaseConverter<ZDictData, ZDictDataDTO> {
}