package com.kg.module.filesStatic.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.filesStatic.dto.ZFilesStaticDTO;
import com.kg.module.filesStatic.entity.ZFilesStatic;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2025-01-24
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZFilesStaticConvert extends BaseConverter<ZFilesStatic, ZFilesStaticDTO> {
}