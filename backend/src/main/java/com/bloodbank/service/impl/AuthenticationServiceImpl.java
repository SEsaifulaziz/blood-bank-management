package com.bloodbank.service.impl;

import com.bloodbank.dto.response.AuthResponseDTO;
import com.bloodbank.security.jwt.JwtService;
import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.entity.User;
import com.bloodbank.exception.DuplicateResourceException;
import com.bloodbank.exception.InvalidCredentialsException;
import com.bloodbank.mapper.UserMapper;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.security.user.CustomUserDetails;
import com.bloodbank.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    @Transactional
    public AuthResponseDTO register(SignupRequestDTO request) {

        validateRegistration(request);

        User user = userMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepo.save(user);

        String token = jwtService.generateToken(savedUser);

        return userMapper.toAuthResponse(savedUser, token);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        try{
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            User user = userDetails.getUser();

            String token = jwtService.generateToken(user);

            return userMapper.toAuthResponse(user, token);

        } catch(AuthenticationException ex){
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

    }

    private void validateRegistration(SignupRequestDTO request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (request.getPhone() !=null
                && !request.getPhone().isEmpty()
                && userRepo.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException("Phone already exists");
        }
    }

}
