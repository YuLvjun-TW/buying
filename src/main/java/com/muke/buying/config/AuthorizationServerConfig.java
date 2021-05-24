package com.muke.buying.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * 授权服务器
 */
@EnableAuthorizationServer
@Configuration
@AllArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // 客户端ID
                .withClient("clientId123")
                // 密钥
                .secret(passwordEncoder.encode("112233"))
                // 重定向地址
                .redirectUris("http://www.baidu.com")
                // 授权范围
                .scopes("all")
                // 授权类型：授权码模式
                .authorizedGrantTypes("authorization_code")
                .accessTokenValiditySeconds(60);
    }
}
