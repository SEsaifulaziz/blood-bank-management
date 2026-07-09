package com.bloodbank.service;

import com.bloodbank.dto.request.RegisterRequestDTO;
import com.bloodbank.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(RegisterRequestDTO  registerRequestDTO);
}
