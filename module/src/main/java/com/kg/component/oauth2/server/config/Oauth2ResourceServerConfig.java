package com.kg.component.oauth2.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * Oauth2 资源服务器配置
 *
 * @author ziro
 * @date 2023/9/1 15:57
 */
@Configuration
@EnableResourceServer
public class Oauth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 自定义 Oauth2 的资源地址
     * 说明：
     * 1. 通过 access_token 的验证，就能访问
     * 2. 如果security config中有重复url，以本配置优先，会忽略security中配置
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 过滤资源 API 地址
                .requestMatchers().antMatchers("/oauth/resources/**")
                // 通过 access_token 验证，则放行的资源服务
                .and().authorizeRequests().antMatchers("/oauth/resources/**").authenticated();
    }
}
