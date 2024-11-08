package org.example.javajwtonboading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.example.javajwtonboading.application.service.UserService;
import org.example.javajwtonboading.domain.model.User;
import org.example.javajwtonboading.domain.repository.UserRepository;
import org.example.javajwtonboading.presentation.response.SignupResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 회원가입_테스트() {
        // given
        String username = "JIN HO";
        String password = "password";
        String nickname = "Mentos";

        // when
        SignupResponseDTO response = userService.signup(username, password, nickname);

        // then
        verify(userRepository).save(any(User.class)); // save가 호출되었는지만 검증
        assertEquals(username, response.getUsername(), "Username should match");
        assertEquals(nickname, response.getNickname(), "Nickname should match");
        assertEquals("ROLE_USER", response.getAuthorities().get(0).getAuthorityName(),
            "User role should match");
    }
}
