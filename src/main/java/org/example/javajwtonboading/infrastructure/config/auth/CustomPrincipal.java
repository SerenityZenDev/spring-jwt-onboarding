package org.example.javajwtonboading.infrastructure.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomPrincipal {

    private Long userId;
    private String username;

    public CustomPrincipal(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}

