package com.kg.module.userTheme.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.userTheme.dto.ZUserThemeDTO;
import com.kg.module.userTheme.entity.ZUserTheme;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-11-04
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZUserThemeConvert extends BaseConverter<ZUserTheme, ZUserThemeDTO> {
}