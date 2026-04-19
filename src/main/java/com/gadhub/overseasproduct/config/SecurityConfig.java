package com.gadhub.overseasproduct.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.gadhub.overseasproduct.config.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF（跨站请求伪造）保护，方便前后端分离测试
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)


                // 2. 定义哪些接口需要保护，哪些可以公开
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/backend/user/register", "/api/v1/backend/user/login").permitAll() // 注册接口公开
                        .anyRequest().authenticated()                         // 其他所有接口都必须登录
                );

        return http.build();
    }
}
