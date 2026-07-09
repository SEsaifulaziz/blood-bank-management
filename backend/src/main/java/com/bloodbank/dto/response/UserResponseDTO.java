package com.bloodbank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long userId;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String userRole;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
