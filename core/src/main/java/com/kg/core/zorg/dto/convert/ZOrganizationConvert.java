package com.kg.core.zorg.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.entity.ZOrganization;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-01-11
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZOrganizationConvert extends BaseConverter<ZOrganization, ZOrganizationDTO> {
}