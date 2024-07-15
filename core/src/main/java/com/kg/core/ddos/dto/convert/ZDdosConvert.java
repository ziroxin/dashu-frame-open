package com.kg.core.ddos.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.core.ddos.dto.ZDdosDTO;
import com.kg.core.ddos.entity.ZDdos;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2024-07-15
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZDdosConvert extends BaseConverter<ZDdos, ZDdosDTO> {
}