package com.kg.module.generator.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.generator.dto.ZFormGeneratorDTO;
import com.kg.module.generator.entity.ZFormGenerator;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-11-22
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZFormGeneratorConvert extends BaseConverter<ZFormGenerator, ZFormGeneratorDTO> {
}