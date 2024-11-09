package org.example.javajwtonboading.infrastructure.config.jwt;

import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final Cache<Long, String> refreshTokenCache;  // Caffeine Cache 사용

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(
            JwtUtil.BEARER_PREFIX)) {
            String accessToken = authorizationHeader.substring(JwtUtil.BEARER_PREFIX.length());

            try {
                Claims claims = jwtUtil.extractClaims(accessToken);

                if (jwtUtil.validateToken(accessToken)) {
                    setAuthentication(claims);
                } else {
                    handleExpiredAccessToken(claims, response);
                }
            } catch (Exception e) {
                log.error("JWT 인증 실패", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Claims claims) {
        Long userId = Long.parseLong(claims.getSubject());
        String role = claims.get("role", String.class);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void handleExpiredAccessToken(Claims claims, HttpServletResponse response) {
        Long userId = Long.parseLong(claims.getSubject());
        String cachedRefreshToken = refreshTokenCache.getIfPresent(userId);

        if (cachedRefreshToken != null) {
            try {
                // Refresh Token을 사용하여 새 Access Token 발급
                String newAccessToken = jwtUtil.refreshAccessToken(userId, cachedRefreshToken);
                if (newAccessToken != null) { // 새로운 Access Token이 발급된 경우
                    response.setHeader("Authorization", JwtUtil.BEARER_PREFIX + newAccessToken);
                    setAuthentication(jwtUtil.extractClaims(newAccessToken));
                } else {
                    log.error("새로운 Access Token 발급 실패");
                    SecurityContextHolder.clearContext();
                }
            } catch (ExpiredJwtException e) {
                log.error("Refresh Token이 만료되었습니다.", e);
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.error("새 Access Token 발급 중 오류", e);
                SecurityContextHolder.clearContext();
            }
        } else {
            log.warn("Refresh Token이 캐시에 존재하지 않습니다.");
            SecurityContextHolder.clearContext();
        }
    }

}
