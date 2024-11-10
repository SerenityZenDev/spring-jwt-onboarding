package org.example.javajwtonboading.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.application.service.UserService;
import org.example.javajwtonboading.domain.model.User;
import org.example.javajwtonboading.infrastructure.config.auth.CustomPrincipal;
import org.example.javajwtonboading.infrastructure.config.jwt.JwtUtil;
import org.example.javajwtonboading.presentation.request.SigninRequestDTO;
import org.example.javajwtonboading.presentation.request.SignupRequestDTO;
import org.example.javajwtonboading.presentation.response.SigninResponseDTO;
import org.example.javajwtonboading.presentation.response.SignupResponseDTO;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Authentication API", description = "사용자 인증 관련 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CacheManager cacheManager;

    @Operation(summary = "회원가입", description = "사용자 계정을 생성합니다.")
    @PostMapping("/signup")
    public SignupResponseDTO signup(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {
        return userService.signup(
            signupRequestDTO.username(),
            signupRequestDTO.password(),
            signupRequestDTO.nickname()
        );
    }

    @Operation(summary = "로그인", description = "사용자 인증을 수행하고 JWT 토큰을 반환합니다.")
    @PostMapping("/sign")
    public SigninResponseDTO sign(@RequestBody @Valid SigninRequestDTO signinRequestDTO) {
        User loginedUser = userService.sign(
            signinRequestDTO.username(),
            signinRequestDTO.password()
        );
        String accessToken = jwtUtil.createAccessToken(loginedUser.getId(),
            loginedUser.getUsername(), loginedUser.getRole());
        String refreshToken = jwtUtil.createRefreshToken(loginedUser.getId(),
            loginedUser.getUsername(), loginedUser.getRole());

        // 리프레시 토큰을 Caffeine 캐시에 저장
        Cache cache = cacheManager.getCache("refreshTokenCache");
        if (cache != null) {
            cache.put(loginedUser.getId(), refreshToken);
            System.out.println(cache.get(loginedUser.getId()));
        }

        return SigninResponseDTO.builder()
            .token(accessToken)
            .build();
    }

    @Operation(summary = "프로필 조회", security = { @SecurityRequirement(name = "bearerAuth") },description = "인증된 사용자의 프로필 정보를 반환합니다.")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getUserProfile(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        String username = principal.getUsername();

        return ResponseEntity.ok("User profile for username: " + username + " (user ID: " + userId + ")");
    }

}
