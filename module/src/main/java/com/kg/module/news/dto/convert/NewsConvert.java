package com.kg.module.news.dto.convert;

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import com.kg.module.news.dto.NewsDTO;
import com.kg.module.news.entity.News;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ziro
 * @since 2023-02-17
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface NewsConvert extends BaseConverter<News, NewsDTO> {
}