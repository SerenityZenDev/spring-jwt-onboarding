package org.example.javajwtonboading.jwt;

import io.jsonwebtoken.Claims;
import org.example.javajwtonboading.infrastructure.config.jwt.JwtUtil;
import org.example.javajwtonboading.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil.init();
    }

    @Test
    void 액세스_토큰_생성_테스트() {
        String accessToken = jwtUtil.createAccessToken(1L, "testUser", UserRole.ROLE_USER);
        Claims claims = jwtUtil.extractClaims(accessToken);

        assertEquals("1", claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals(UserRole.ROLE_USER.name(), claims.get("role"));
    }

    @Test
    void 리프레시_토큰_생성_테스트() {
        String refreshToken = jwtUtil.createRefreshToken(1L, "testUser", UserRole.ROLE_USER);
        Claims claims = jwtUtil.extractClaims(refreshToken);

        assertEquals("1", claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals(UserRole.ROLE_USER.name(), claims.get("role"));
    }

    @Test
    void 만료된_토큰_검증_테스트() {
        String expiredToken = jwtUtil.createToken(1L, "testUser", UserRole.ROLE_USER, new Date(System.currentTimeMillis() - 1000));
        assertFalse(jwtUtil.validateToken(expiredToken));
    }

    @Test
    void 리프레시_토큰을_통한_새_액세스_토큰_발급_테스트() {
        String refreshToken = jwtUtil.createRefreshToken(1L, "testUser", UserRole.ROLE_USER);
        String newAccessToken = jwtUtil.refreshAccessToken(1L, refreshToken);

        Claims claims = jwtUtil.extractClaims(newAccessToken);
        assertEquals("1", claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals(UserRole.ROLE_USER.name(), claims.get("role"));
    }
}
