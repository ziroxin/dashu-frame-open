package com.kg.module.files.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.files.dto.ZFilesDTO;
import com.kg.module.files.entity.ZFiles;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-09-15
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZFilesConvert extends BaseConverter<ZFiles, ZFilesDTO> {
}