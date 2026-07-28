package com.bloodbank.controller;

import com.bloodbank.config.JwtUtils;
import com.bloodbank.dto.request.LoginRequestDTO;
import com.bloodbank.dto.request.SignupRequestDTO;
import com.bloodbank.dto.request.TokenRefreshRequestDTO;
import com.bloodbank.dto.response.JWTResponseDTO;
import com.bloodbank.dto.response.TokenRefreshResponseDTO;
import com.bloodbank.entity.RefreshToken;
import com.bloodbank.exception.TokenRefreshException;
import com.bloodbank.service.RefreshTokenService;
import com.bloodbank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<JWTResponseDTO> registerUser(@Valid @RequestBody SignupRequestDTO dto) {
        log.info("REST request received to register user account with email: {}", dto.getEmail());
        JWTResponseDTO responseDTO = userService.registerUser(dto);
        log.info("User with email {} saved successfully", dto.getEmail());
        return new  ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("REST request received to login user account with email: {}", dto.getEmail());
        JWTResponseDTO responseDTO = userService.loginUser(dto);


        log.info("Authentication complete. Returning profile data map container for User ID: {}",
                responseDTO.getUserId());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO dto) {
        log.info("REST request received to refresh JWT token");
        String requestRefreshToken = dto.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    List<String> roles = user.getUserRole() != null
                            ? List.of(user.getUserRole().toString())
                            : List.of("ROLE_USER");

                    String accessToken = jwtUtils.generateTokenFromUsername(user.getEmail(), roles);
                    log.info("Refreshing access token for user ID: {}", user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponseDTO(accessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(
                        requestRefreshToken,
                        "Refresh token is not registered in system"
                ));
    }
}
