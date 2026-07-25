package com.bloodbank.service;

import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    JWTResponseDTO registerUser(SignupRequestDTO registerRequestDTO);

    JWTResponseDTO loginUser(LoginRequestDTO loginRequestDTO) throws UsernameNotFoundException;

}
