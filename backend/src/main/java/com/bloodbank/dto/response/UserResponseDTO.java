package com.bloodbank.dto.response;

import com.bloodbank.model.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private UserRole userRole;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
