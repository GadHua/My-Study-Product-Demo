package com.gadhub.overseasproduct.config;

import com.gadhub.overseasproduct.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(jwtUtil.getHeaderName());

        if (header != null && header.startsWith(jwtUtil.getTokenPrefix())) {
            String token = header.replace(jwtUtil.getTokenPrefix(), "");

            if (jwtUtil.validateToken(token)) {
                String userId = jwtUtil.getUserIdFromToken(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("用户认证成功, userId: {}", userId);
            } else {
                log.warn("JWT Token验证失败");
            }
        }

        filterChain.doFilter(request, response);
    }
}
