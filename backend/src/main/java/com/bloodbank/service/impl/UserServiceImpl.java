package com.bloodbank.service.impl;

import com.bloodbank.config.JwtUtils;
import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
import com.bloodbank.entity.RefreshToken;
import com.bloodbank.entity.User;
import com.bloodbank.exception.DuplicateResourceException;
import com.bloodbank.exception.InvalidCredentialsException;
import com.bloodbank.mapper.UserMapper;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.service.RefreshTokenService;
import com.bloodbank.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public JWTResponseDTO registerUser(SignupRequestDTO dto) {
      log.info("Received request to register user with email: {}",
              dto != null ? dto.getEmail() : "null");

        // 1. Guard Clause: Defensive null checking on incoming payload
        Objects.requireNonNull(dto, "Signup request payload cannot be null");

        if(userRepo.existsByEmail(dto.getEmail())) {
            log.info("User with email {} already exists", dto.getEmail());
            throw new DuplicateResourceException("User with email already exists");
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank() && userRepo.existsByPhone(dto.getPhone())) {
            log.warn("Registration failed: Phone number {} is already in use", dto.getPhone());
            throw new DuplicateResourceException("Phone number is already in use");
        }

        User user = userMapper.toEntity(dto);
        String securePassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(securePassword);
        User savedUser = userRepo.save(user);
        log.info("User with email {} saved successfully", dto.getEmail());

        // Wrap the loaded principal context into the token generation token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                null
        );
        String token = jwtUtils.generateToken(authentication);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUserId());

        return JWTResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .userRole(savedUser.getUserRole() != null ? savedUser.getUserRole().toString() : "USER")
                .build();
    }

    @Override
    @Transactional
    public JWTResponseDTO loginUser(LoginRequestDTO dto) throws UsernameNotFoundException {
        log.info("Processing authentication request for email: {}",
                dto != null ? dto.getEmail() : "null");

        Objects.requireNonNull(dto, "Login request payload cannot be null");

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
            log.info("Credential validation has been satisfied");

            User user = userRepo.findByEmail(dto.getEmail())
                    .orElseThrow(() ->
                            new InvalidCredentialsException("Invalid email or password")
                    );
            //check if account is active
            if(!user.getIsActive()){
                log.warn("Login attempt on inactive account: {}", dto.getEmail());
                throw new InvalidCredentialsException("Account is disabled or inactive");
            }

            // Generate cryptographically signed token using the validated authentication principal
            String token = jwtUtils.generateToken(authentication);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

            return JWTResponseDTO.builder()
                    .token(token)
                    .refreshToken(refreshToken.getToken())
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .userRole(user.getUserRole() != null ? user.getUserRole().toString() : "USER")
                    .build();

        } catch (UsernameNotFoundException ex) {
            log.warn("User not found: {}", dto.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");

        } catch(AuthenticationException ex){
            log.warn("Authentication failed for email: {} - {}",
                    dto.getEmail(), ex.getClass().getSimpleName());
            throw new InvalidCredentialsException("Invalid email or password");

        } catch(Exception ex){
            log.error("Unexpected error during login for email: {}", dto.getEmail(), ex);
            throw new InvalidCredentialsException("Authentication service temporarily unavailable");
        }
    }
}
