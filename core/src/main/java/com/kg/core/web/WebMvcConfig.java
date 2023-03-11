package com.kg.core.web;

import com.kg.component.file.FilePathConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置静态资源映射
 *
 * @author ziro
 * @date 2022-07-02 21:54:31
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置 /upload/** 映射到  /SAVE_PATH/**
        String fileUploadPath = FilePathConfig.SAVE_PATH + "/";
        registry.addResourceHandler(FilePathConfig.URL_PRE + "/**")
                .addResourceLocations("file:" + fileUploadPath.replaceAll("//", "/"));
    }
}
