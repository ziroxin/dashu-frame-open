package com.kg.module.aTableFiles.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.aTableFiles.dto.ATableFilesDTO;
import com.kg.module.aTableFiles.entity.ATableFiles;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2025-03-18
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ATableFilesConvert extends BaseConverter<ATableFiles, ATableFilesDTO> {
}