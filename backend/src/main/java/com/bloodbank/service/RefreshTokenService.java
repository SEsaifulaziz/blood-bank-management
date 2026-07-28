package com.bloodbank.service;

import com.bloodbank.entity.RefreshToken;
import com.bloodbank.exception.ResourceNotFoundException;
import com.bloodbank.repository.RefreshTokenRepository;
import com.bloodbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // Default 7 days
    private long jwtRefreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository  userRepository;

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
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
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request.");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long id){
        return userRepository.findById(id)
                .map(refreshTokenRepository::deleteByUser)
                .orElse(0);
    }
}
