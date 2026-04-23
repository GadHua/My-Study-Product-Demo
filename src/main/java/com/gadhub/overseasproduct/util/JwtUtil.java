package com.gadhub.overseasproduct.util;

import com.gadhub.overseasproduct.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userId) {
        long expirationTime = jwtConfig.getExpiration();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String userId, Map<String, Object> claims) {
        long expirationTime = jwtConfig.getExpiration();
        var jwtBuilder = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime));

        if (claims != null && !claims.isEmpty()) {
            claims.forEach(jwtBuilder::claim);
        }

        return jwtBuilder
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            log.warn("JWT Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            long warningTime = 30 * 60 * 1000;
            return remainingTime < warningTime;
        } catch (Exception e) {
            return true;
        }
    }

    public String refreshToken(String oldToken) {
        String userId = getUserIdFromToken(oldToken);
        return generateToken(userId);
    }

    public String getTokenPrefix() {
        return jwtConfig.getPrefix();
    }

    public String getHeaderName() {
        return jwtConfig.getHeader();
    }
}
