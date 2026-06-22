package ${package.Convert};

import com.kg.core.base.converter.BaseConverter;
import com.kg.core.base.converter.BaseConverterConfig;
import ${package.DTO}.${dtoName};
import ${package.Entity}.${entity};
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * DTO和实体转换类
 *
 * @author ${author}
 * @since ${date}
 */
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ${dtoconvertName} extends BaseConverter<${entity}, ${dtoName}> {
}