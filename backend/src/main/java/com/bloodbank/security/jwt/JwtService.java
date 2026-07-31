package com.bloodbank.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {


    private final SecretKey signingKey;
    private final long jwtExpirationMs;

    public JwtUtils(
            @Value("${app.jwt.secret}") final String jwtSecret,
            @Value("${app.jwt.expiration.ms}") final long  jwtExpirationMs
    ) {
        log.info("Initializing JWT 0.12.6 engine with external configuration...");
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(final Authentication authentication) {
        String username;
        Object principal = authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            username = principal.toString();
        }

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return  buildToken(username, roles);
    }

    public String generateTokenFromUserName(final String username, final List<String> roles) {
        return buildToken(username, roles);
    }

    public String generateTokenFromUsername(final String username, List<String> roles) {
        return buildToken(username, List.of("ROLE_USER"));
    }

    private String buildToken(final String username, final List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

    }

    public String getUserNameFromJwtToken(final String token){
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(final String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature layout confirmation failed: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Malformed JWT string payload configuration error: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token life cycle limit breached: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT format payload tracking error: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims payload target sequence is null or empty reference: {}", ex.getMessage());
        }
        return false;
    }
}
