package com.bloodbank.mapper;

import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.UserResponseDTO;
import com.bloodbank.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(SignupRequestDTO dto) {

        if(dto == null){
            return null;
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Will be hashed in the service layer
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setUserRole(dto.getUserRole());
        user.setIsActive(true); // Explicitly ensure it's active on creation

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setUserRole(user.getUserRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }
}
