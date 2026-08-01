package com.bloodbank.service.impl;

import com.bloodbank.security.jwt.JwtService;
import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
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
    public JWTResponseDTO register(SignupRequestDTO request) {

        validateRegistration(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepo.save(user);

        String token = jwtService.generateToken(savedUser);

        return buildResponse(savedUser, token);
    }

    @Override
    @Transactional(readOnly = true)
    public JWTResponseDTO login(LoginRequestDTO request) throws UsernameNotFoundException {

        try{
            CustomUserDetails customUserDetails =
                    (CustomUserDetails) authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword())
                    ).getPrincipal();

            User user = customUserDetails.getUser();

            if(!user.isActive()){
                throw new InvalidCredentialsException("Account is disabled or inactive");
            }

            String token = jwtService.generateToken(user);

            return buildResponse(user, token);

        } catch (UsernameNotFoundException ex) {
            throw new InvalidCredentialsException("Invalid email or password");

        } catch(AuthenticationException ex){
            throw new InvalidCredentialsException("Invalid email or password");

        } catch(Exception ex){
            throw new InvalidCredentialsException("Authentication service temporarily unavailable");
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

    private JWTResponseDTO buildResponse(User user, String token){
        return JWTResponseDTO.builder()
                .token(token)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userRole(user.getUserRole().name())
                .build();
    }
}
