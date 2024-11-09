package org.example.javajwtonboading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.javajwtonboading.application.service.UserService;
import org.example.javajwtonboading.domain.model.User;
import org.example.javajwtonboading.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void 회원가입_테스트() {
        // 회원가입 테스트 코드 (생략, 이전 예제 참조)
    }

    @Test
    void 로그인_존재하지_않는_사용자_테스트() {
        // given
        String username = "unknownUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.sign(username, password),
            "User not found 예외가 발생해야 합니다.");
    }

    @Test
    void 로그인_잘못된_비밀번호_테스트() {
        // given
        String username = "JIN HO";
        String password = "wrongPassword";
        User user = User.builder()
            .username(username)
            .password("encodedCorrectPassword")
            .nickname("Mentos")
            .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.sign(username, password),
            "Wrong password 예외가 발생해야 합니다.");
    }

    @Test
    void 로그인_성공_테스트() {
        // given
        String username = "JIN HO";
        String password = "correctPassword";
        String encodedPassword = "encodedCorrectPassword";
        User user = User.builder()
            .username(username)
            .password(encodedPassword)
            .nickname("Mentos")
            .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // when
        User result = userService.sign(username, password);

        // then
        assertEquals(user, result, "로그인된 사용자 정보가 일치해야 합니다.");
    }
}
