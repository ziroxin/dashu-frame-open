package com.kg.component.xml.reload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XML文件热加载 - 配置类
 *
 * @author ziro
 * @date 2024/11/14 10:38
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "mybatis-xml-reload")
public class MybatisXmlReloadProperties {
    private boolean enabled;
    private String[] mapperLocations;
}
