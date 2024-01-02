package com.kg.module.sms.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.sms.dto.DemoSmsDTO;
import com.kg.module.sms.entity.DemoSms;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-12-14
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface DemoSmsConvert extends BaseConverter<DemoSms, DemoSmsDTO> {
}