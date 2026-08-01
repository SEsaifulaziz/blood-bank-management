package com.bloodbank.service;

import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService {

    JWTResponseDTO register(SignupRequestDTO registerRequestDTO);

    JWTResponseDTO login(LoginRequestDTO loginRequestDTO) throws UsernameNotFoundException;

}
