package com.kg.module.tradeRefund.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.tradeRefund.dto.BusTradeRefundDTO;
import com.kg.module.tradeRefund.entity.BusTradeRefund;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-06-14
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface BusTradeRefundConvert extends BaseConverter<BusTradeRefund, BusTradeRefundDTO> {
}