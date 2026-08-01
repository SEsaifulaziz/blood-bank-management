package com.bloodbank.controller;

import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
import com.bloodbank.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService userService;

    @PostMapping("/register")
    public ResponseEntity<JWTResponseDTO> register(@Valid @RequestBody SignupRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
       return  ResponseEntity.ok(
               userService.login(dto)
       );
    }

}
