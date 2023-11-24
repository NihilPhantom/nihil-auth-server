package com.nihil.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String secretString = "12345678901234567890123456789012";
    private static final SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    public static String generateToken(String userId, String userRoles, Map<String, Object> other) {
        // 设置有效时间
        long period = 7200000;
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(other) // 使用setClaims可以将Map数据进行加密，必须放在其他变量之前
                .setId(userId)
                .setSubject(userRoles)
                .setExpiration(new Date(System.currentTimeMillis() + period)) // 设置有效期
                .signWith(key);
        return jwtBuilder.compact();
    }

    public static String generateToken(String userId, String userRoles) {
        // 设置有效时间
        long period = 7200000;
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(userId)
                .setSubject(userRoles)
                .setExpiration(new Date(System.currentTimeMillis() + period)) // 设置有效期
                .signWith(key);
        return jwtBuilder.compact();
    }

    public static Claims parseToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
