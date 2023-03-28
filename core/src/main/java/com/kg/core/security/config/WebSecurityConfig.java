package com.kg.core.security.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.kg.core.filter.JwtTokenAuthenticationFilter;
import com.kg.core.security.handler.SimpleAccessDeniedHandler;
import com.kg.core.security.handler.SimpleAuthenticationEntryPoint;
import com.kg.core.security.util.MyPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serurity 配置
 *
 * @author ziro
 * @date 2022/4/27 23:08
 */
@Configuration
// 允许security注解权限标记
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    @Resource
    private SimpleAccessDeniedHandler accessDeniedHandler;
    @Resource
    private SimpleAuthenticationEntryPoint authenticationEntryPoint;
    /**
     * 配置是否允许跨域，默认false
     */
    @Value("${com.kg.cors-enable}")
    private Boolean isCorsEnable;

    /**
     * 配置默认解密方法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MyPasswordEncoder();
    }

    /**
     * 配置AuthenticationManager
     * （这样就可以在自己的service中注入了）
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 读取忽略列表
        List<String> antMatchers = FileUtil.readLines("security.ignore", CharsetUtil.defaultCharset());
        String[] ignoreUrls = antMatchers.stream()
                .filter(url -> StringUtils.hasText(url) && !url.startsWith("#"))
                .collect(Collectors.toList()).toArray(new String[]{});

        // 配置Security
        http
                // 关闭跨站请求防护（前后端分离项目）
                .csrf().disable()
                // 关闭session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 配置忽略认证的请求（如登录页）
                .authorizeRequests()
                .antMatchers(ignoreUrls).permitAll()
                // 其他请求，均需认证
                .anyRequest().authenticated();

        // 配置JwtToken过滤器，早于用户名密码验证过滤器
        http.addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置异常的拦截器
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);

        // 允许iframe调用
        http.headers().frameOptions().sameOrigin();

        // 跨域配置
        if (isCorsEnable) {
            // 允许跨域，覆盖跨域配置
            http.cors().configurationSource((corsConfigurationSource()));
        }
    }

    /**
     * 跨域资源配置
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");// 同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedHeader("*");// header，允许哪些header，本案中使用的是token，此处可将*替换为token；
        corsConfiguration.addAllowedMethod("*");// 允许的请求方法，PSOT、GET等
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**", corsConfiguration);// 配置允许跨域访问的url
        return source;
    }
}
