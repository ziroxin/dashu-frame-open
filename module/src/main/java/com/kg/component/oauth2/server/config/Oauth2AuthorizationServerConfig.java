package com.kg.component.oauth2.server.config;

import com.kg.core.security.service.SecurityUserDetailServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Oauth2 授权服务器配置
 *
 * @author ziro
 * @date 2023/8/28 17:04
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private DataSource dataSource;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private SecurityUserDetailServiceImpl userDetailsService;
    @Resource
    private RedisConnectionFactory redisConnectionFactory;


    /**
     * 配置令牌访问权限，及授权模式
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 允许可客户端用户名密码授权（密码模式）
                .allowFormAuthenticationForClients()
                // 只有经过security身份验证（认证过）的客户端和资源服务器才能访问令牌密钥（登录后的？）
                .tokenKeyAccess("isAuthenticated()")
                // 允许所有人，使用令牌验证端点（/oauth/check_token）
                .checkTokenAccess("permitAll()");
    }

    /**
     * 配置客户端（通过数据库表配置）
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    /**
     * 配置令牌端点的行为
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 自定义【用户确认授权】的页面（把默认地址替换成/oauth2/confirm_access.html）
                .pathMapping("/oauth/confirm_access", "/oauth2/approval")
                // 开启密码模式（不配置不支持密码模式）
                .authenticationManager(authenticationManager)
                // 配置令牌存储方式
                .tokenStore(redisTokenStore())
                // 配置支持刷新token功能（不配置不支持refresh_token接口）
                .userDetailsService(userDetailsService);
    }

    /**
     * 配置令牌（access_token）仓储，存入redis
     */
    private RedisTokenStore redisTokenStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("oauth2-token@");// 设置redis前缀方便查询
        return redisTokenStore;
    }
}
