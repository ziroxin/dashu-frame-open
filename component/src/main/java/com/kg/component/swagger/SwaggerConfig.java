package com.kg.component.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger生成API配置类
 *
 * @author ziro
 * @date 2019/7/24 17:28
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "com.kg.swagger.enable", havingValue = "true")
public class SwaggerConfig {
    @Value("${com.kg.swagger.title}")
    private String title;
    @Value("${com.kg.swagger.description}")
    private String description;
    // API协议地址
    @Value("${com.kg.swagger.terms-of-service-url}")
    private String termsOfServiceUrl;
    @Value("${com.kg.swagger.version}")
    private String version;
    @Value("${com.kg.swagger.concat.name}")
    private String name;
    @Value("${com.kg.swagger.concat.www}")
    private String www;
    @Value("${com.kg.swagger.concat.email}")
    private String email;

    /**
     * 配置swagger注册信息
     *
     * @return
     */
    @Bean
    public ApiInfo apiInfo() {
        Contact contact = new Contact(name, www, email);
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .termsOfServiceUrl(termsOfServiceUrl)
                .contact(contact) // 联系方式
                .version(version)
                .build();
    }

    /**
     * 全部API
     */
    @Bean
    public Docket allApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("allApi")
                .select()
                // 这里采用包含注解的方式来确定要显示的接口(建议使用这种)
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 所有接口
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 如需把API分组，采用以下例子
     */
    @Bean
    public Docket api1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
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
