package com.kg.module.atable.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.atable.dto.ATableDTO;
import com.kg.module.atable.entity.ATable;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2025-02-11
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ATableConvert extends BaseConverter<ATable, ATableDTO> {
}