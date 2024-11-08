package org.example.javajwtonboading.presentation.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.javajwtonboading.domain.model.UserRole;

@Getter
public class SignupResponseDTO {

    private String username;
    private String nickname;
    private List<Authority> authorities;

    @Builder
    public SignupResponseDTO(String username, String nickname, UserRole userRole) {
        this.username = username;
        this.nickname = nickname;
        this.authorities = List.of(new Authority(userRole.name()));
    }

    @Getter
    @AllArgsConstructor
    public static class Authority {

        private String authorityName;
    }
}
