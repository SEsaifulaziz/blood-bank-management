package com.bloodbank.controller;

import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.UserResponseDTO;
import com.bloodbank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody SignupRequestDTO dto) {
        log.info("REST request received to register user account with email: {}", dto.getEmail());

        UserResponseDTO responseDTO = userService.registerUSer(dto);

        log.info("User with email {} saved successfully", dto.getEmail());

        return new  ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
