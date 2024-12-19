package com.kg.module.applet.wechat2user.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.applet.wechat2user.dto.ZUserWechatDTO;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2024-12-17
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZUserWechatConvert extends BaseConverter<ZUserWechat, ZUserWechatDTO> {
}