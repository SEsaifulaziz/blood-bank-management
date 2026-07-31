package com.bloodbank.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequestDTO {

    @NotBlank(message = "Refresh token is required.")
    private String refreshToken;
}
