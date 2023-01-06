package com.kg.core.zuserlock.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.core.zuserlock.dto.ZUserLockDTO;
import com.kg.core.zuserlock.entity.ZUserLock;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-01-05
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZUserLockConvert extends BaseConverter<ZUserLock, ZUserLockDTO> {
}