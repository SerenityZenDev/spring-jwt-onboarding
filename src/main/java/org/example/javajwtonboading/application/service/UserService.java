package org.example.javajwtonboading.application.service;

import lombok.RequiredArgsConstructor;
import org.example.javajwtonboading.domain.model.User;
import org.example.javajwtonboading.domain.repository.UserRepository;
import org.example.javajwtonboading.presentation.response.SigninResponseDTO;
import org.example.javajwtonboading.presentation.response.SignupResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDTO signup(String username, String password, String nickname) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
            .username(username)
            .password(encodedPassword)
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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return user;
    }
}
