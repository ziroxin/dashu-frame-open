package com.kg.module.aTableImages.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.aTableImages.dto.ATableImagesDTO;
import com.kg.module.aTableImages.entity.ATableImages;
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
public interface ATableImagesConvert extends BaseConverter<ATableImages, ATableImagesDTO> {
}