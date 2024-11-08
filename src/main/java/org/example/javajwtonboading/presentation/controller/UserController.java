package org.example.javajwtonboading.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.application.service.UserService;
import org.example.javajwtonboading.domain.model.User;
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

    @PostMapping("/signup")
    public SignupResponseDTO signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        return userService.signup(
            signupRequestDTO.username(),
            signupRequestDTO.password(),
            signupRequestDTO.nickname()
        );
    }

    @PostMapping("/sign")
    public SigninResponseDTO sign(@RequestBody SigninRequestDTO signinRequestDTO) {
        User loginedUser = userService.sign(
            signinRequestDTO.username(),
            signinRequestDTO.password()
        );
        String token = "temp"; // TODO: 시큐리티 이후 jwt 토큰 생성 반영

        return SigninResponseDTO.builder()
            .token(token)
            .build();
    }

}
