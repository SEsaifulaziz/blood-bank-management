package com.bloodbank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTResponseDTO {

    String token;
    @Builder.Default
    private String type = "Bearer";

    private Long userId;
    private String email;
    private String fullName;
    private String userRole;
}
