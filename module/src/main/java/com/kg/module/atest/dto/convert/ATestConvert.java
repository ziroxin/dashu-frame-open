package com.kg.module.atest.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.atest.dto.ATestDTO;
import com.kg.module.atest.entity.ATest;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-03-30
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ATestConvert extends BaseConverter<ATest, ATestDTO> {
}