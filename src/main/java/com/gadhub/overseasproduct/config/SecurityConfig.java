package com.gadhub.overseasproduct.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF（跨站请求伪造）保护，方便前后端分离测试
                .csrf(csrf -> csrf.disable())

                // 2. 定义哪些接口需要保护，哪些可以公开
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/user/register").permitAll() // 注册接口公开
                        .anyRequest().authenticated()                         // 其他所有接口都必须登录
                );

        return http.build();
    }
}
