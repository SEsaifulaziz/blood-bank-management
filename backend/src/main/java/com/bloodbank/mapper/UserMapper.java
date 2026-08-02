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
        user.setActive(true);
        return user;
    }
}
