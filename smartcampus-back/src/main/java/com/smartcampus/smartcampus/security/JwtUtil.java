package com.smartcampus.smartcampus.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:SmartCampus2026SecretKey20260716}")
    private String secret;

    @Value("${jwt.expire:86400000}")
    private Long expire;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成Token */
    public String generateToken(Long userId, String identityNumber, String roleCode) {
        return Jwts.builder()
                .claims()
                    .add("userId", userId)
                    .add("identityNumber", identityNumber)
                    .add("roleCode", roleCode)
                    .and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getKey())
                .compact();
    }

    /** 解析Token */
    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /** 验证Token */
    public boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    /** 从Token获取用户ID */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", Long.class) : null;
    }

    /** 从Token获取角色 */
    public String getRoleCodeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("roleCode", String.class) : null;
    }
}
