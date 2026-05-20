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

/**
 * JWT工具类 - 用于处理JSON Web Token的生成、验证和解析
 * 提供用户认证相关的令牌管理功能
 */
@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取签名密钥 - 将配置中的字符串密钥转换为HMAC-SHA安全密钥对象
     * @return SecretKey 用于JWT签名的密钥对象
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌 - 基于用户ID创建基础token
     * @param userId 用户唯一标识，作为token的主题(subject)
     * @return String 生成的JWT令牌字符串 (格式: header.payload.signature)
     */
    public String generateToken(String userId) {
        long expirationTime = jwtConfig.getExpiration(); // 获取配置的过期时间-24小时
        return Jwts.builder()
                .setSubject(userId)  // 设置主题为用户ID
                .setIssuedAt(new Date())  // 设置签发时间为当前时间
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // 设置过期时间 当前时间+过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 使用HS256算法和密钥签名
                .compact();  // 构建并压缩为JWT字符串
    }

    /**
     * 生成带自定义声明的JWT令牌 - 可包含额外的用户信息（如角色、权限等）
     * @param userId 用户唯一标识，作为token的主题(subject)
     * @param claims 自定义声明Map，可添加额外信息到payload中
     * @return String 生成的JWT令牌字符串，包含自定义声明
     */
    public String generateToken(String userId, Map<String, Object> claims) {
        long expirationTime = jwtConfig.getExpiration();
        var jwtBuilder = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime));

        // 如果有自定义声明，添加到token的payload中
        if (claims != null && !claims.isEmpty()) {
            claims.forEach(jwtBuilder::claim);
        }

        return jwtBuilder
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌 - 验证签名并提取payload中的所有声明信息
     * @param token JWT令牌字符串
     * @return Claims 包含token中所有声明信息的对象（如subject、expiration等）
     * @throws io.jsonwebtoken.JwtException 如果token无效、过期或签名不匹配时抛出异常
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // 设置验证密钥，用于验证签名
                .build()
                .parseClaimsJws(token)  // 解析并验证JWT（包括签名验证）
                .getBody();  // 获取payload部分
    }

    /**
     * 验证JWT令牌的有效性 - 检查签名是否正确且未过期
     * @param token JWT令牌字符串
     * @return boolean true表示令牌有效，false表示无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);  // 解析token，如果签名无效会抛异常
            return claims.getExpiration().after(new Date());  // 检查是否过期
        } catch (Exception e) {
            log.warn("JWT Token验证失败: {}", e.getMessage());
            return false;  // 任何异常都认为验证失败
        }
    }

    /**
     * 从JWT令牌中提取用户ID - 获取token的subject字段
     * @param token JWT令牌字符串（必须是有效的、未过期的token）
     * @return String 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();  // subject字段存储的是用户ID
    }

    /**
     * 检查JWT令牌是否即将过期 - 用于提前刷新token
     * @param token JWT令牌字符串
     * @return boolean true表示将在30分钟内过期或已无效，false表示剩余时间充足
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();  // 计算剩余时间（毫秒）
            long warningTime = 30 * 60 * 1000;  // 警告阈值：30分钟
            return remainingTime < warningTime;  // 剩余时间小于30分钟则认为即将过期
        } catch (Exception e) {
            return true;  // 解析失败（如已过期）也认为需要刷新
        }
    }

    /**
     * 刷新JWT令牌 - 基于旧token中的用户信息生成新的token
     * @param oldToken 旧的JWT令牌（必须是有效的）
     * @return String 新生成的JWT令牌，有效期重新计算
     */
    public String refreshToken(String oldToken) {
        String userId = getUserIdFromToken(oldToken);  // 从旧token提取用户ID
        return generateToken(userId);  // 用相同的用户ID生成新token（重置过期时间）
    }

    /**
     * 获取JWT令牌前缀 - 用于HTTP请求头中的token格式
     * @return String token前缀，通常为"Bearer "
     */
    public String getTokenPrefix() {
        return jwtConfig.getPrefix();
    }

    /**
     * 获取JWT请求头名称 - 客户端传递token时使用的HTTP头字段名
     * @return String 请求头名称，通常为"Authorization"
     */
    public String getHeaderName() {
        return jwtConfig.getHeader();
    }
}
