package org.example.javajwtonboading.application.service;

import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.domain.model.User;
import org.example.javajwtonboading.domain.repository.UserRepository;
import org.example.javajwtonboading.presentation.response.SigninResponseDTO;
import org.example.javajwtonboading.presentation.response.SignupResponseDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public SignupResponseDTO signup(String username, String password, String nickname) {
        User user = User.builder()
            .username(username)
            .password(password)
            .nickname(nickname)
            .build();

        userRepository.save(user);

        return SignupResponseDTO.builder()
            .username(user.getUsername())
            .nickname(user.getNickname())
            .userRole(user.getRole())
            .build();
    }

    public User sign(String username, String password){
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new IllegalArgumentException("User not found")
        );

        // TODO: 패스워드 검증 - 시큐리티 적용 후 반영

        return user;
    }
}
