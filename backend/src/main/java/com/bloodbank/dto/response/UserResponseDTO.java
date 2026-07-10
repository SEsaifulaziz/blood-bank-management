package com.bloodbank.dto.response;

import com.bloodbank.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
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
