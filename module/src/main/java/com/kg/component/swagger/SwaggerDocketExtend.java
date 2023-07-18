package com.kg.component.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;

/**
 * Swagger扩展分组配置
 *
 * @author ziro
 * @date 2023/5/19 09:43
 */
@Component
@ConditionalOnProperty(name = "com.kg.swagger.enable", havingValue = "true")
public class SwaggerDocketExtend {
    @Resource
    private SwaggerConfig swaggerConfig;

    /**
     * 自定义扩展Api分组
     */
    @Bean
    public Docket apiDocket1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggerConfig.apiInfo())
                // 分组
                .groupName("news")
                .select()
                // 这里采用包含注解的方式来确定要显示的接口(建议使用这种)
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // =======================paths支持多种匹配方式=================================
                // demo1: 以/news/开头的接口
                .paths(PathSelectors.ant("/news/**"))
                // demo2: /news/list接口
//                .paths(path -> "/news/list".equals(path))
                // demo2: 所有接口
//                .paths(PathSelectors.any())
                .build();
    }

}
