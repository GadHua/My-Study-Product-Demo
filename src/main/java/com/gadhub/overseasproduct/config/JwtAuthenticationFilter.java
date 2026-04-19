package com.gadhub.overseasproduct.config;

import com.gadhub.overseasproduct.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从请求头获取 Token
        String header = request.getHeader(JwtUtil.HEADER_STRING);

        // 2. 验证 Token 格式
        if (header != null && header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            String token = header.replace(JwtUtil.TOKEN_PREFIX, "");

            // 3. 验证 Token 是否有效
            if (JwtUtil.validateToken(token)) {
                // 4. 解析用户ID
                String userId = JwtUtil.getUserIdFromToken(token);

                // 5. 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                // 6. 存入 Security 上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 7. 继续过滤链
        filterChain.doFilter(request, response);
    }
}
