package org.example.javajwtonboading.application.service;

import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.domain.model.User;
import org.example.javajwtonboading.domain.repository.UserRepository;
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
}
