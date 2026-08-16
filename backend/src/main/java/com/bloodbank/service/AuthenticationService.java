package com.bloodbank.service;

import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.AuthResponseDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService {

    AuthResponseDTO register(SignupRequestDTO registerRequestDTO);

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO) throws UsernameNotFoundException;

}
