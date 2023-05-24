package com.kg.module.trade.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.trade.dto.BusTradeDTO;
import com.kg.module.trade.entity.BusTrade;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-05-16
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface BusTradeConvert extends BaseConverter<BusTrade, BusTradeDTO> {
}