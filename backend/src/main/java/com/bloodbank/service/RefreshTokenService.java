package com.bloodbank.service;

import com.bloodbank.entity.RefreshToken;
import com.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.exception.TokenRefreshException;
import com.bloodbank.repository.RefreshTokenRepository;
import com.bloodbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // Default 7 days
    private long jwtRefreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository  userRepository;

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        log.info("Generating refresh token for user ID: {}", userId);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpirationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        // Delete any existing refresh token for this user first
        refreshTokenRepository.deleteByUser(refreshToken.getUser());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token expired for user ID: {}. Removing token.", token.getUser().getUserId());
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request.");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId){
        log.info("Deleting refresh token for user ID: {}", userId);
        return userRepository.findById(userId)
                .map(refreshTokenRepository::deleteByUser)
                .orElse(0);
    }
}
