package com.bloodbank.service;

import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
import com.bloodbank.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

public interface UserService {

//    JWTResponseDTO registerUser(SignupRequestDTO registerRequestDTO);
//
//    JWTResponseDTO loginUser(LoginRequestDTO loginRequestDTO);

    UserResponseDTO registerUSer(SignupRequestDTO dto);

    UserResponseDTO loginUser(LoginRequestDTO dto);
}
