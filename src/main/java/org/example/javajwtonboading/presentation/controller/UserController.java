package org.example.javajwtonboading.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.application.service.UserService;
import org.example.javajwtonboading.domain.model.User;
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

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CacheManager cacheManager;

    @PostMapping("/signup")
    public SignupResponseDTO signup(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {
        return userService.signup(
            signupRequestDTO.username(),
            signupRequestDTO.password(),
            signupRequestDTO.nickname()
        );
    }

    @PostMapping("/sign")
    public SigninResponseDTO sign(@RequestBody @Valid SigninRequestDTO signinRequestDTO) {
        User loginedUser = userService.sign(
            signinRequestDTO.username(),
            signinRequestDTO.password()
        );
        String accessToken = jwtUtil.createAccessToken(loginedUser.getId(), loginedUser.getUsername(), loginedUser.getRole());
        String refreshToken = jwtUtil.createRefreshToken(loginedUser.getId(), loginedUser.getUsername(), loginedUser.getRole());

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

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getUserProfile(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        return ResponseEntity.ok("User profile for: " + username);
    }

}
