package com.gadhub.overseasproduct.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成、验证和解析 JWT Token
 */
public class JwtUtil {

    // JWT 签名密钥（生产环境应该从配置文件读取，并且要足够复杂）
    private static final String SECRET_KEY = "OverseasProductSecretKey2026ForJWTTokenGenerationAndValidation";

    // Token 有效期：24小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    // Token 前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    // HTTP 请求头中的 Token 字段名
    public static final String HEADER_STRING = "Authorization";

    /**
     * 获取签名密钥
     * 将字符串密钥转换为 HMAC-SHA256 算法所需的 SecretKey 对象
     */
    private static SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId 用户ID（存入 Token 的主体信息）
     * @return 生成的 JWT Token 字符串
     */
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)                    // 设置主题（用户ID）
                .setIssuedAt(new Date())               // 设置签发时间（当前时间）
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 设置过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 使用 HS256 算法签名
                .compact();                            // 压缩生成最终的 Token 字符串
    }

    /**
     * 生成 JWT Token（支持自定义 claims）
     *
     * @param userId 用户ID
     * @param claims 额外的声明信息（如用户名、角色等）
     * @return 生成的 JWT Token 字符串
     */
    public static String generateToken(String userId, Map<String, Object> claims) {
        var jwtBuilder = Jwts.builder()
                .setSubject(userId)                    // 设置主题（用户ID）
                .setIssuedAt(new Date())               // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME));  // 设置过期时间

        // 添加自定义 claims
        if (claims != null && !claims.isEmpty()) {
            claims.forEach(jwtBuilder::claim);
        }

        return jwtBuilder
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 签名
                .compact();                            // 生成 Token
    }

    /**
     * 解析 JWT Token，获取 Claims 信息
     *
     * @param token JWT Token 字符串
     * @return Claims 对象，包含 Token 中的所有信息
     * @throws Exception Token 无效或已过期时抛出异常
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())        // 设置签名密钥用于验证
                .build()                               // 构建解析器
                .parseClaimsJws(token)                 // 解析并验证 Token
                .getBody();                            // 获取 Token 的 payload 部分
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token 字符串
     * @return true-有效，false-无效或已过期
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 检查是否过期
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Token 解析失败（格式错误、签名不匹配、已过期等）
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token 字符串
     * @return 用户ID
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();  // subject 中存储的是用户ID
    }

    /**
     * 检查 Token 是否即将过期（剩余时间少于30分钟）
     *
     * @param token JWT Token 字符串
     * @return true-即将过期，false-未即将过期
     */
    public static boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            // 剩余时间少于30分钟
            return remainingTime < 30 * 60 * 1000;
        } catch (Exception e) {
            return true;  // 解析失败也视为需要刷新
        }
    }

    /**
     * 刷新 Token（生成新的 Token）
     *
     * @param oldToken 旧的 Token
     * @return 新的 Token
     */
    public static String refreshToken(String oldToken) {
        String userId = getUserIdFromToken(oldToken);
        return generateToken(userId);  // 用相同的用户ID生成新 Token
    }
}
