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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

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
        String token = jwtUtil.createToken(loginedUser.getId(), loginedUser.getUsername(), loginedUser.getRole());

        return SigninResponseDTO.builder()
            .token(token)
            .build();
    }

}
