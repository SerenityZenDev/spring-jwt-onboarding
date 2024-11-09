package org.example.javajwtonboading.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import org.example.javajwtonboading.domain.model.UserRole;
import org.example.javajwtonboading.infrastructure.config.jwt.JwtAuthorizationFilter;
import org.example.javajwtonboading.infrastructure.config.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtAuthorizationFilterTest {

    @Autowired
    private JwtUtil jwtUtil;
    private JwtAuthorizationFilter jwtAuthorizationFilter;
    private Cache<Long, String> refreshTokenCache;

    @Mock
    private jakarta.servlet.FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil.init();

        refreshTokenCache = Caffeine.newBuilder().build();
        jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtil, refreshTokenCache);
    }

    @Test
    void 유효한_액세스_토큰_테스트() throws Exception {
        // 유효한 Access Token 생성
        String accessToken = jwtUtil.createAccessToken(1L, "testUser", UserRole.ROLE_USER);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", JwtUtil.BEARER_PREFIX + accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock된 filterChain 전달
        doNothing().when(filterChain).doFilter(request, response);

        // Reflection을 사용하여 doFilterInternal 메서드 호출
        Method doFilterInternal = JwtAuthorizationFilter.class.getDeclaredMethod(
            "doFilterInternal", jakarta.servlet.http.HttpServletRequest.class,
            jakarta.servlet.http.HttpServletResponse.class,
            jakarta.servlet.FilterChain.class);
        doFilterInternal.setAccessible(true);
        doFilterInternal.invoke(jwtAuthorizationFilter, request, response, filterChain);

        // SecurityContext에 인증 정보가 설정되었는지 검증
        UsernamePasswordAuthenticationToken authentication =
            (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        assertNotNull(authentication);
        assertEquals(1L, authentication.getPrincipal());
        assertTrue(
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void 만료된_액세스_토큰과_리프레시_토큰_갱신_테스트() throws Exception {
        // Reflection을 통해 JwtUtil의 key에 접근하여 만료된 토큰 생성
        Field keyField = JwtUtil.class.getDeclaredField("key");
        keyField.setAccessible(true);
        Key key = (Key) keyField.get(jwtUtil);

        String expiredAccessToken = Jwts.builder()
            .setSubject("1")
            .claim("username", "testUser")
            .claim("role", UserRole.ROLE_USER.name())
            .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 과거 시간으로 만료된 토큰 설정
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        String refreshToken = jwtUtil.createRefreshToken(1L, "testUser", UserRole.ROLE_USER);
        refreshTokenCache.put(1L, refreshToken);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", JwtUtil.BEARER_PREFIX + expiredAccessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(filterChain).doFilter(request, response);

        // Reflection을 사용하여 doFilterInternal 메서드 호출
        Method doFilterInternal = JwtAuthorizationFilter.class.getDeclaredMethod(
            "doFilterInternal", HttpServletRequest.class,
            HttpServletResponse.class, FilterChain.class);
        doFilterInternal.setAccessible(true);
        doFilterInternal.invoke(jwtAuthorizationFilter, request, response, filterChain);

        // 새로 발급된 Access Token이 응답 헤더에 설정되었는지 확인
        String newAccessTokenHeader = response.getHeader("Authorization");
        assertNotNull(newAccessTokenHeader, "새 Access Token이 응답 헤더에 없습니다.");
        String newAccessToken = newAccessTokenHeader.substring(JwtUtil.BEARER_PREFIX.length());
        assertTrue(jwtUtil.validateToken(newAccessToken));

        // Claims를 통해 새 Access Token의 유효성 검증
        Claims claims = jwtUtil.extractClaims(newAccessToken);
        assertEquals("1", claims.getSubject());
        assertEquals("testUser", claims.get("username"));
        assertEquals("ROLE_USER", claims.get("role"));
    }



}
