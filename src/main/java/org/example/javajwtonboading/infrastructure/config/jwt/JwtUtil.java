package org.example.javajwtonboading.infrastructure.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.example.javajwtonboading.domain.model.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, String username, UserRole role) {
        return createToken(userId, username, role,
            new Date(System.currentTimeMillis() + 60 * 60 * 1000));
    }

    public String createRefreshToken(Long userId, String username, UserRole role) {
        return createToken(userId, username, role,
            new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
    }

    public String createToken(Long userId, String username, UserRole role, Date expiration) {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("username", username)
            .claim("role", role)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace(BEARER_PREFIX, ""))
                .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 Claims를 반환하여 이후 로직에서 만료 상태를 처리할 수 있도록 함
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshAccessToken(Long userId, String refreshToken) {
        Claims claims = extractClaims(refreshToken);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        return createAccessToken(userId, username, UserRole.valueOf(role));
    }
}
