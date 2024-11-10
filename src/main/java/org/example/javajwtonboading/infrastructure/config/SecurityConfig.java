package org.example.javajwtonboading.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.infrastructure.config.jwt.JwtAuthorizationFilter;
import org.example.javajwtonboading.infrastructure.config.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public Cache<Long, String> refreshTokenCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(7, TimeUnit.DAYS)
            .maximumSize(1000)
            .build();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, refreshTokenCache());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**", "/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/sign", "/signup")
                .permitAll() // H2 콘솔 및 인증 경로 허용
                .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
            )
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)) // H2 콘솔 iframe 허용
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)) // 세션 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable); // HTTP Basic 인증 비활성화

        // JWT 필터 등록
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
