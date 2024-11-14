package com.kg.component.xml.reload;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * XML文件热加载 - 自动配置
 *
 * @author ziro
 * @date 2024/11/14 10:39
 */
@Configuration
@EnableConfigurationProperties({MybatisXmlReloadProperties.class})
public class MybatisXmlReloadConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MybatisXmlReload mybatisXmlReload(MybatisXmlReloadProperties prop,
                                             List<SqlSessionFactory> sqlSessionFactories) throws IOException {
        MybatisXmlReload mybatisXmlReload = new MybatisXmlReload(prop, sqlSessionFactories);
        if (prop.isEnabled()) {
            mybatisXmlReload.xmlReload();
        }
        return mybatisXmlReload;
    }

    @Bean
    public static LazyInitializationExcludeFilter integrationLazyInitializationExcludeFilter() {
        return LazyInitializationExcludeFilter.forBeanTypes(MybatisXmlReload.class);
    }
}
