package com.kg.core.zlog.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.core.zlog.dto.ZOperateLogDTO;
import com.kg.core.zlog.entity.ZOperateLog;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-01-07
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZOperateLogConvert extends BaseConverter<ZOperateLog, ZOperateLogDTO> {
}