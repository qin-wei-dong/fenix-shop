package com.fenix.shop.user.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:fenix-shop-jwt-secret-key-2025-very-long-secret-key}")
    private String jwtSecret;

    /**
     * JWT过期时间（毫秒）- 7天
     */
    @Value("${jwt.expiration:604800000}")
    private Long jwtExpiration;

    /**
     * JWT签发者
     */
    @Value("${jwt.issuer:fenix-shop}")
    private String jwtIssuer;

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, userId);
    }

    /**
     * 创建JWT令牌
     * @param claims 声明
     * @param subject 主题（用户ID）
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从JWT令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从JWT令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("username");
    }

    /**
     * 从JWT令牌中获取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从JWT令牌中获取声明
     * @param token JWT令牌
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw new RuntimeException("令牌已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT令牌: {}", e.getMessage());
            throw new RuntimeException("不支持的令牌格式");
        } catch (MalformedJwtException e) {
            log.warn("JWT令牌格式错误: {}", e.getMessage());
            throw new RuntimeException("令牌格式错误");
        } catch (SignatureException e) {
            log.warn("JWT令牌签名验证失败: {}", e.getMessage());
            throw new RuntimeException("令牌签名验证失败");
        } catch (IllegalArgumentException e) {
            log.warn("JWT令牌为空: {}", e.getMessage());
            throw new RuntimeException("令牌不能为空");
        }
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌
     * @param userId 用户ID
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token, String userId) {
        try {
            String tokenUserId = getUserIdFromToken(token);
            return tokenUserId.equals(userId) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查JWT令牌是否过期
     * @param token JWT令牌
     * @return true-已过期，false-未过期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 获取签名密钥
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取JWT过期时间（秒）
     * @return 过期时间（秒）
     */
    public Long getExpirationTime() {
        return jwtExpiration / 1000;
    }

    /**
     * 刷新JWT令牌
     * @param token 原JWT令牌
     * @return 新JWT令牌
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String userId = claims.getSubject();
            String username = (String) claims.get("username");
            return generateToken(userId, username);
        } catch (Exception e) {
            log.warn("JWT令牌刷新失败: {}", e.getMessage());
            throw new RuntimeException("令牌刷新失败");
        }
    }

    /**
     * 从请求头中提取JWT令牌
     * @param authHeader 授权头
     * @return JWT令牌
     */
    public String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 从HttpServletRequest中提取JWT令牌
     * @param request HTTP请求
     * @return JWT令牌，如果不存在则返回null
     */
    public String getTokenFromRequest(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return extractTokenFromHeader(authHeader);
    }

    /**
     * 验证JWT令牌（重载方法，只验证令牌本身）
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
}