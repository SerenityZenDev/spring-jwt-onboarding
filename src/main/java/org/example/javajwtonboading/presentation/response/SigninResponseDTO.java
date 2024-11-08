package org.example.javajwtonboading.presentation.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SigninResponseDTO {
    private String token;

    @Builder
    public SigninResponseDTO(String token) {
        this.token = token;
    }
}
