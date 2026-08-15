package com.bloodbank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private Long userId;
    private String email;
    private String fullName;
    private String userRole;
}
