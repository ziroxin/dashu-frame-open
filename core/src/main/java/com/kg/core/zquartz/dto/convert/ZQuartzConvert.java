package com.kg.core.zquartz.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.core.zquartz.dto.ZQuartzDTO;
import com.kg.core.zquartz.entity.ZQuartz;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2022-12-26
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZQuartzConvert extends BaseConverter<ZQuartz, ZQuartzDTO> {
}