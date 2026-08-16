package com.bloodbank.mapper;

import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.AuthResponseDTO;
import com.bloodbank.dto.response.UserResponseDTO;
import com.bloodbank.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(SignupRequestDTO dto) {

        if(dto == null){
            return null;
        }
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .userRole(dto.getUserRole())
                .build();
    }
    public AuthResponseDTO toAuthResponse(User user, String token) {

        if (user == null) {
            return null;
        }

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userRole(user.getUserRole().name())
                .build();
    }

    public UserResponseDTO toResponseDTO(User user) {

        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .userRole(user.getUserRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
