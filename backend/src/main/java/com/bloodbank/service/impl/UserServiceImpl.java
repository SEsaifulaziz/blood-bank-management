package com.bloodbank.service.impl;

import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.UserResponseDTO;
import com.bloodbank.entity.User;
import com.bloodbank.exception.DuplicateResourceException;
import com.bloodbank.exception.InvalidCredentialsException;
import com.bloodbank.mapper.UserMapper;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponseDTO registerUSer(SignupRequestDTO dto) {
      log.info("Received request to register user with email: {}",
              dto != null ? dto.getEmail() : "null");

        // 1. Guard Clause: Defensive null checking on incoming payload
        Objects.requireNonNull(dto, "Signup request payload cannot be null");

        if(userRepo.existsByEmail(dto.getEmail())) {
            log.info("User with email {} already exists", dto.getEmail());

            throw new DuplicateResourceException("User with email already exists");
        }

        log.debug("Mapping SignupRequestDTO to structural User entity for: {}", dto.getEmail());
        User user = userMapper.toEntity(dto);

        log.debug("Encrypting raw text user password: {}", user.getPassword());
        String securePassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(securePassword);

        log.debug("persisting secure user record to database....");
        User savedUser = userRepo.save(user);
        log.info("User with email {} saved successfully", dto.getEmail());

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO loginUser(LoginRequestDTO dto) {
        log.info("Processing authentication request for email: {}",
                dto != null ? dto.getEmail() : "null");

        Objects.requireNonNull(dto, "Login request payload cannot be null");

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
            log.info("Credential validation has been satisfied");

            User user = userRepo.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
            return userMapper.toResponseDTO(user);

        }catch(Exception e){
            log.warn("Authentication fault encountered for email: {} - Reason: {}", dto.getEmail(), e.getMessage());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
}
